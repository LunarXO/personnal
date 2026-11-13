// GLB / STL parsers -> geometry {positions, indices}. Pure ESM, node-testable.
import { computeNormals } from './geo.js';

const COMP = { 5120: Int8Array, 5121: Uint8Array, 5122: Int16Array, 5123: Uint16Array, 5125: Uint32Array, 5126: Float32Array };
const COMP_SIZE = { 5120: 1, 5121: 1, 5122: 2, 5123: 2, 5125: 4, 5126: 4 };
const TYPE_N = { SCALAR: 1, VEC2: 2, VEC3: 3, VEC4: 4, MAT4: 16 };

function readAccessor(json, bin, idx) {
  const acc = json.accessors[idx];
  const bv = json.bufferViews[acc.bufferView];
  const Ctor = COMP[acc.componentType];
  if (!Ctor) throw new Error('unsupported componentType ' + acc.componentType);
  const nComp = TYPE_N[acc.type];
  const count = acc.count;
  const base = (bv.byteOffset || 0) + (acc.byteOffset || 0);
  const stride = bv.byteStride || nComp * COMP_SIZE[acc.componentType];
  const out = new Ctor(count * nComp);
  const dv = new DataView(bin);
  const elemBytes = nComp * COMP_SIZE[acc.componentType];
  for (let i = 0; i < count; i++) {
    const off = base + i * stride;
    for (let c = 0; c < nComp; c++) {
      const o = off + c * COMP_SIZE[acc.componentType];
      switch (acc.componentType) {
        case 5126: out[i * nComp + c] = dv.getFloat32(o, true); break;
        case 5125: out[i * nComp + c] = dv.getUint32(o, true); break;
        case 5123: out[i * nComp + c] = dv.getUint16(o, true); break;
        case 5122: out[i * nComp + c] = dv.getInt16(o, true); break;
        case 5121: out[i * nComp + c] = dv.getUint8(o); break;
        case 5120: out[i * nComp + c] = dv.getInt8(o); break;
      }
    }
    void elemBytes;
  }
  return out;
}

export function parseGLB(arrayBuffer) {
  const dv = new DataView(arrayBuffer);
  if (dv.getUint32(0, true) !== 0x46546c67) throw new Error('not a GLB file (bad magic)');
  const jsonLen = dv.getUint32(12, true);
  if (dv.getUint32(16, true) !== 0x4e4f534a) throw new Error('GLB missing JSON chunk');
  const json = JSON.parse(new TextDecoder().decode(new Uint8Array(arrayBuffer, 20, jsonLen)));
  let bin = null;
  let off = 20 + jsonLen;
  while (off + 8 <= arrayBuffer.byteLength) {
    const clen = dv.getUint32(off, true);
    const ctype = dv.getUint32(off + 4, true);
    if (ctype === 0x004e4942) bin = arrayBuffer.slice(off + 8, off + 8 + clen);
    off += 8 + clen + ((clen % 4) ? (4 - (clen % 4)) : 0);
  }
  if (!bin) throw new Error('GLB missing BIN chunk');
  const geos = [];
  for (const mesh of json.meshes || []) {
    for (const prim of mesh.primitives || []) {
      if (prim.mode != null && prim.mode !== 4) continue; // triangles only
      const pos = readAccessor(json, bin, prim.attributes.POSITION);
      let idx = prim.indices != null ? readAccessor(json, bin, prim.indices) : null;
      geos.push({ positions: new Float32Array(pos), indices: idx ? new Uint32Array(idx) : null });
    }
  }
  if (!geos.length) throw new Error('no triangle meshes in GLB');
  let np = 0, ni = 0;
  for (const g of geos) { np += g.positions.length; ni += g.indices ? g.indices.length : g.positions.length / 3; }
  const positions = new Float32Array(np);
  const indices = new Uint32Array(ni);
  let po = 0, io = 0, vo = 0;
  for (const g of geos) {
    positions.set(g.positions, po);
    const idx = g.indices || (() => { const a = new Uint32Array(g.positions.length / 3); for (let i = 0; i < a.length; i++) a[i] = i; return a; })();
    for (let i = 0; i < idx.length; i++) indices[io + i] = idx[i] + vo;
    po += g.positions.length; io += idx.length; vo += g.positions.length / 3;
  }
  return computeNormals({ positions, indices });
}

export function parseSTL(arrayBuffer) {
  const dv = new DataView(arrayBuffer);
  const isAscii = arrayBuffer.byteLength > 15 && new TextDecoder().decode(new Uint8Array(arrayBuffer, 0, 6)).trim().startsWith('solid');
  let positions;
  if (!isAscii && arrayBuffer.byteLength >= 84) {
    const count = dv.getUint32(80, true);
    positions = new Float32Array(count * 9);
    for (let i = 0; i < count; i++) {
      const base = 84 + i * 50;
      for (let v = 0; v < 9; v++) positions[i * 9 + v] = dv.getFloat32(base + 12 + v * 4, true);
    }
  } else {
    const text = new TextDecoder().decode(arrayBuffer);
    const nums = text.match(/-?\d+\.?\d*(?:[eE][-+]?\d+)?/g) || [];
    // ascii stl: 7 numbers per vertex (normal xyz + 3 verts... actually 3 normals + 9 coords = 12 per facet, minus 'vertex' keywords)
    positions = new Float32Array((nums.length / 12) * 9);
    let p = 0;
    for (let i = 3; i + 8 < nums.length; i += 12) {
      for (let v = 0; v < 9; v++) positions[p++] = parseFloat(nums[i + v]);
    }
  }
  const n = positions.length / 3;
  const indices = new Uint32Array(n);
  for (let i = 0; i < n; i++) indices[i] = i;
  return computeNormals({ positions, indices });
}

// browser File -> geometry
export function parseModelFile(file) {
  const ext = file.name.split('.').pop().toLowerCase();
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => {
      try {
        if (ext === 'glb' || ext === 'gltf') {
          if (ext === 'gltf') return reject(new Error('Use .glb (binary) — external .bin/.gltf resources are not supported'));
          resolve(parseGLB(reader.result));
        } else if (ext === 'stl') resolve(parseSTL(reader.result));
        else reject(new Error('Unsupported format: .' + ext + ' (use .glb or .stl)'));
      } catch (e) { reject(e); }
    };
    reader.onerror = () => reject(reader.error);
    reader.readAsArrayBuffer(file);
  });
}
