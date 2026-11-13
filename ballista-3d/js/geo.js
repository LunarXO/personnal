// Procedural geometry generators. Pure ESM, node-testable.
// Geometry = { positions: Float32Array, normals: Float32Array, uvs: Float32Array, indices: Uint32Array }
// Triangle indices, CCW front faces.

import { v3, transformPoint } from './glmath.js';

export function computeNormals(geo) {
  const { positions, indices } = geo;
  const n = positions.length / 3;
  const normals = new Float32Array(positions.length);
  const idx = indices || defaultIndices(n);
  for (let t = 0; t < idx.length; t += 3) {
    const i0 = idx[t], i1 = idx[t + 1], i2 = idx[t + 2];
    const a = [positions[i0 * 3], positions[i0 * 3 + 1], positions[i0 * 3 + 2]];
    const b = [positions[i1 * 3], positions[i1 * 3 + 1], positions[i1 * 3 + 2]];
    const c = [positions[i2 * 3], positions[i2 * 3 + 1], positions[i2 * 3 + 2]];
    const cr = v3.cross(v3.sub(b, a), v3.sub(c, a));
    for (const i of [i0, i1, i2]) {
      normals[i * 3] += cr[0]; normals[i * 3 + 1] += cr[1]; normals[i * 3 + 2] += cr[2];
    }
  }
  for (let i = 0; i < n; i++) {
    const l = Math.hypot(normals[i * 3], normals[i * 3 + 1], normals[i * 3 + 2]) || 1;
    normals[i * 3] /= l; normals[i * 3 + 1] /= l; normals[i * 3 + 2] /= l;
  }
  return { ...geo, normals };
}

function defaultIndices(n) {
  const idx = new Uint32Array(n);
  for (let i = 0; i < n; i++) idx[i] = i;
  return idx;
}

export function transformGeo(geo, m) {
  const positions = new Float32Array(geo.positions.length);
  for (let i = 0; i < geo.positions.length / 3; i++) {
    const p = transformPoint(m, [geo.positions[i * 3], geo.positions[i * 3 + 1], geo.positions[i * 3 + 2]]);
    positions.set(p, i * 3);
  }
  let normals = geo.normals;
  if (normals) {
    // approximate: rotate by mat3 (valid for uniform scale / rotation)
    normals = new Float32Array(normals.length);
    for (let i = 0; i < normals.length / 3; i++) {
      const x = geo.normals[i * 3], y = geo.normals[i * 3 + 1], z = geo.normals[i * 3 + 2];
      const nx = m[0] * x + m[4] * y + m[8] * z;
      const ny = m[1] * x + m[5] * y + m[9] * z;
      const nz = m[2] * x + m[6] * y + m[10] * z;
      const l = Math.hypot(nx, ny, nz) || 1;
      normals.set([nx / l, ny / l, nz / l], i * 3);
    }
  }
  return { ...geo, positions, normals };
}

export function mergeGeos(list) {
  let np = 0, ni = 0;
  for (const g of list) { np += g.positions.length; ni += (g.indices ? g.indices.length : g.positions.length / 3); }
  const positions = new Float32Array(np);
  const normals = list.every((g) => g.normals) ? new Float32Array(np) : null;
  const uvs = list.every((g) => g.uvs && g.uvs.length === g.positions.length / 3 * 2) ? new Float32Array(np / 3 * 2) : null;
  const indices = new Uint32Array(ni);
  let po = 0, io = 0, vo = 0;
  for (const g of list) {
    positions.set(g.positions, po);
    if (normals) normals.set(g.normals, po);
    if (uvs) uvs.set(g.uvs, vo);
    const idx = g.indices || defaultIndices(g.positions.length / 3);
    for (let i = 0; i < idx.length; i++) indices[io + i] = idx[i] + vo / 2;
    po += g.positions.length; io += idx.length; vo += (g.positions.length / 3) * 2;
  }
  return { positions, normals, uvs, indices };
}

// ellipsoid with equatorial radius a and polar radius c, uv mapped (equirectangular)
export function ellipsoid(a, c, wSeg = 96, hSeg = 64) {
  const positions = [], uvs = [], indices = [];
  for (let iy = 0; iy <= hSeg; iy++) {
    const v = iy / hSeg;
    const phi = v * Math.PI; // 0..pi from north pole
    const sp = Math.sin(phi), cp = Math.cos(phi);
    for (let ix = 0; ix <= wSeg; ix++) {
      const u = ix / wSeg;
      const theta = u * 2 * Math.PI;
      const st = Math.sin(theta), ct = Math.cos(theta);
      positions.push(a * sp * ct, c * cp, a * sp * st);
      uvs.push(u, v);
    }
  }
  for (let iy = 0; iy < hSeg; iy++) {
    for (let ix = 0; ix < wSeg; ix++) {
      const i0 = iy * (wSeg + 1) + ix;
      const i1 = i0 + wSeg + 1;
      indices.push(i0, i1, i0 + 1, i0 + 1, i1, i1 + 1);
    }
  }
  const geo = { positions: new Float32Array(positions), uvs: new Float32Array(uvs), indices: new Uint32Array(indices) };
  // analytic normals for ellipsoid: n ∝ (x/a², y/c², z/a²)
  const normals = new Float32Array(positions.length);
  for (let i = 0; i < positions.length / 3; i++) {
    const x = positions[i * 3], y = positions[i * 3 + 1], z = positions[i * 3 + 2];
    const n = v3.norm([x / (a * a), y / (c * c), z / (a * a)]);
    normals.set(n, i * 3);
  }
  return { ...geo, normals };
}

export function sphere(r = 1, wSeg = 24, hSeg = 16) {
  return ellipsoid(r, r, wSeg, hSeg);
}

// lathe: profile = [[radius, y], ...] revolved around Y
export function lathe(profile, seg = 24) {
  const positions = [], uvs = [], indices = [];
  const n = profile.length;
  for (let i = 0; i < n; i++) {
    const [r, y] = profile[i];
    for (let j = 0; j <= seg; j++) {
      const t = (j / seg) * 2 * Math.PI;
      positions.push(r * Math.cos(t), y, r * Math.sin(t));
      uvs.push(j / seg, i / (n - 1));
    }
  }
  for (let i = 0; i < n - 1; i++) {
    for (let j = 0; j < seg; j++) {
      const i0 = i * (seg + 1) + j;
      const i1 = i0 + seg + 1;
      indices.push(i0, i0 + 1, i1, i0 + 1, i1 + 1, i1);
    }
  }
  return computeNormals({ positions: new Float32Array(positions), uvs: new Float32Array(uvs), indices: new Uint32Array(indices) });
}

export function cylinder(rTop, rBottom, h, seg = 20, cap = true) {
  const profile = [];
  if (cap) profile.push([0, -h / 2], [rBottom, -h / 2]);
  profile.push([rBottom, -h / 2], [rTop, h / 2]);
  if (cap) profile.push([rTop, h / 2], [0, h / 2]);
  return lathe(profile, seg);
}

export function cone(r, h, seg = 20) {
  return lathe([[0, h / 2], [r, -h / 2], [0, -h / 2]], seg);
}

// flat ring in XZ plane, normal +Y
export function ring(rInner, rOuter, seg = 40) {
  const positions = [], uvs = [], indices = [];
  for (let j = 0; j <= seg; j++) {
    const t = (j / seg) * 2 * Math.PI;
    const c = Math.cos(t), s = Math.sin(t);
    positions.push(rInner * c, 0, rInner * s, rOuter * c, 0, rOuter * s);
    uvs.push(0, 0, 1, 1);
  }
  for (let j = 0; j < seg; j++) {
    const i0 = j * 2;
    indices.push(i0, i0 + 2, i0 + 1, i0 + 1, i0 + 2, i0 + 3);
  }
  const normals = new Float32Array(positions.length);
  for (let i = 0; i < positions.length / 3; i++) normals.set([0, 1, 0], i * 3);
  return { positions: new Float32Array(positions), uvs: new Float32Array(uvs), indices: new Uint32Array(indices), normals };
}

export function box(w, h, d) {
  const x = w / 2, y = h / 2, z = d / 2;
  const positions = [
    -x, -y, -z, x, -y, -z, x, y, -z, -x, y, -z,
    -x, -y, z, x, -y, z, x, y, z, -x, y, z,
  ];
  const indices = [
    0, 2, 1, 0, 3, 2, 4, 5, 6, 4, 6, 7,
    1, 2, 6, 1, 6, 5, 0, 1, 5, 0, 5, 4, 3, 7, 6, 3, 6, 2,
  ];
  return computeNormals({ positions: new Float32Array(positions), indices: new Uint32Array(indices) });
}
