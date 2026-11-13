import { mat4Perspective, mat4LookAt, mat4Multiply, mat4Invert, mat4FromBasis, transformPoint, projectPoint, rayEllipsoid, screenRay, v3 } from '../js/glmath.js';
import { ellipsoid, sphere, cone, ring, mergeGeos, computeNormals, transformGeo } from '../js/geo.js';
import { parseGLB, parseSTL } from '../js/loaders.js';
import { meshStats } from '../js/meshstats.js';

let passed = 0, failed = 0;
const ok = (c, name, extra = '') => { if (c) { passed++; console.log('PASS  ' + name); } else { failed++; console.log('FAIL  ' + name + ' ' + extra); } };
const approx = (a, b, rel, name) => ok(Math.abs(a - b) <= rel * Math.max(Math.abs(b), 1e-12), name, `(got ${a}, want ${b})`);

// --- mat4 ---
{
  const p = mat4Perspective(60, 1, 0.1, 100);
  const v = mat4LookAt([0, 0, 5], [0, 0, 0], [0, 1, 0]);
  const vp = mat4Multiply(p, v);
  const clip = projectPoint(vp, [0, 0, 0]);
  ok(clip[2] / clip[3] > -1 && clip[2] / clip[3] < 1, 'origin in front maps inside NDC z', `ndcZ=${(clip[2] / clip[3]).toFixed(3)}`);
  const clipBehind = projectPoint(vp, [0, 0, 10]);
  ok(clipBehind[3] < 0, 'point behind camera has negative w');
  const inv = mat4Invert(vp);
  const id = mat4Multiply(vp, inv);
  let maxErr = 0;
  for (let i = 0; i < 16; i++) maxErr = Math.max(maxErr, Math.abs(id[i] - (i % 5 === 0 ? 1 : 0)));
  ok(maxErr < 1e-9, 'mat4Invert roundtrip', `err=${maxErr}`);
  const m = mat4FromBasis([1, 2, 3], [1, 0, 0], [0, 1, 0], [0, 0, 1], 2);
  const tp = transformPoint(m, [1, 0, 0]);
  approx(tp[0], 3, 1e-12, 'basis+scale transform x');
  approx(tp[1], 2, 1e-12, 'basis+scale transform y');
}

// --- ray ellipsoid ---
{
  const t = rayEllipsoid([10, 0, 0], [-1, 0, 0], 5, 5);
  approx(t, 5, 1e-12, 'ray hits sphere surface at t=radius from outside');
  ok(rayEllipsoid([10, 0, 0], [1, 0, 0], 5, 5) === null, 'ray away from sphere misses');
  const t2 = rayEllipsoid([0, 0, 10], [0, 0, -1], 5, 3);
  approx(t2, 7, 1e-12, 'ray hits ellipsoid pole at c distance');
}

// --- screenRay ---
{
  const cam = { pos: [0, 0, 10] };
  const view = mat4LookAt(cam.pos, [0, 0, 0], [0, 1, 0]);
  const proj = mat4Perspective(60, 1, 0.1, 100);
  const ray = screenRay(cam, view, proj, 100, 100, 50, 50);
  approx(ray.dir[2], -1, 1e-9, 'center screen ray points at target (-z)');
  approx(ray.dir[0], 0, 1e-9, 'center ray x ~ 0');
}

// --- geo ---
{
  const s = sphere(2, 48, 32);
  const st = meshStats(s.positions, s.indices);
  approx(st.volume, (4 / 3) * Math.PI * 8, 0.01, 'sphere mesh volume ~ 4/3 pi r^3 (1%)', `got ${st.volume.toFixed(3)}`);
  ok(s.normals && s.normals.length === s.positions.length, 'ellipsoid/sphere has normals');
  const c = cone(1, 2, 12);
  ok(c.positions.length / 3 > 12, 'cone has vertices');
  const r = ring(1, 2, 8);
  ok(r.indices.length === 8 * 6, 'ring indices count');
  const merged = mergeGeos([transformGeo(sphere(1, 24, 16), mat4FromBasis([5, 0, 0], [1, 0, 0], [0, 1, 0], [0, 0, 1], 1)), sphere(1, 24, 16)]);
  const st2 = meshStats(merged.positions, merged.indices);
  approx(st2.volume, 2 * (4 / 3) * Math.PI, 0.03, 'merged spheres volume doubles');
  const e = ellipsoid(6378.137, 6356.752, 32, 24);
  const n0 = [e.normals[0], e.normals[1], e.normals[2]];
  approx(v3.len(n0), 1, 1e-9, 'ellipsoid normals normalized');
}

// --- loaders: synthetic GLB with one triangle ---
{
  const positions = new Float32Array([0, 0, 0, 1, 0, 0, 0, 1, 0]);
  const indices = new Uint16Array([0, 1, 2]);
  const bin = new Uint8Array(positions.byteLength + indices.byteLength);
  bin.set(new Uint8Array(positions.buffer), 0);
  bin.set(new Uint8Array(indices.buffer), positions.byteLength);
  const json = {
    asset: { version: '2.0' },
    buffers: [{ byteLength: positions.byteLength + indices.byteLength }],
    bufferViews: [
      { buffer: 0, byteOffset: 0, byteLength: positions.byteLength },
      { buffer: 0, byteOffset: positions.byteLength, byteLength: indices.byteLength },
    ],
    accessors: [
      { bufferView: 0, componentType: 5126, count: 3, type: 'VEC3' },
      { bufferView: 1, componentType: 5123, count: 3, type: 'SCALAR' },
    ],
    meshes: [{ primitives: [{ attributes: { POSITION: 0 }, indices: 1, mode: 4 }] }],
  };
  const jsonStr = JSON.stringify(json);
  const jsonBytes = new TextEncoder().encode(jsonStr);
  const jsonPad = (4 - (jsonBytes.length % 4)) % 4;
  const binLen = bin.byteLength;
  const total = 12 + 8 + jsonBytes.length + jsonPad + 8 + binLen;
  const glb = new ArrayBuffer(total);
  const dv = new DataView(glb);
  dv.setUint32(0, 0x46546c67, true);
  dv.setUint32(4, 2, true);
  dv.setUint32(8, total, true);
  dv.setUint32(12, jsonBytes.length + jsonPad, true);
  dv.setUint32(16, 0x4e4f534a, true);
  new Uint8Array(glb, 20, jsonBytes.length).set(jsonBytes);
  const binOff = 20 + jsonBytes.length + jsonPad;
  dv.setUint32(binOff, binLen, true);
  dv.setUint32(binOff + 4, 0x004e4942, true);
  new Uint8Array(glb, binOff + 8, binLen).set(bin);
  const geo = parseGLB(glb);
  ok(geo.positions.length === 9, 'GLB parse: 3 vertices', `got ${geo.positions.length / 3}`);
  ok(geo.indices.length === 3, 'GLB parse: 3 indices');
  approx(geo.positions[3], 1, 1e-6, 'GLB vertex data correct');
  ok(geo.normals && geo.normals.length === 9, 'GLB normals computed');

  // binary STL with one triangle
  const stl = new ArrayBuffer(84 + 50);
  const sdv = new DataView(stl);
  sdv.setUint32(80, 1, true);
  const tri = [0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0];
  for (let i = 0; i < 12; i++) sdv.setFloat32(84 + i * 4, tri[i], true);
  const sgeo = parseSTL(stl);
  ok(sgeo.positions.length === 9, 'STL binary parse: 3 vertices');
  approx(sgeo.positions[3], 1, 1e-6, 'STL vertex data correct');
}

console.log(`\n${passed} passed, ${failed} failed`);
process.exit(failed ? 1 : 0);
