// Preset projectile shapes (custom geometry) + user model upload (GLB/STL).
import { sphere, lathe, cylinder, cone, box, mergeGeos, transformGeo, computeNormals } from './geo.js';
import { parseModelFile } from './loaders.js';
import { meshStats, autoReferenceArea } from './meshstats.js';
import { mat4FromBasis } from './glmath.js';

const T = (geo, x, y, z, s = 1) => transformGeo(geo, mat4FromBasis([x, y, z], [1, 0, 0], [0, 1, 0], [0, 0, 1], s));
const SY = (geo, sy) => transformGeo(geo, mat4FromBasis([0, 0, 0], [1, 0, 0], [0, sy, 0], [0, 0, 1], 1));

// Unit-length shapes (long axis = +Y, centered at origin). Returns {geo, color}.
export function buildPresetShape(shape, color = 0x888888) {
  const c = [(color >> 16 & 255) / 255, (color >> 8 & 255) / 255, (color & 255) / 255];
  let geo;
  if (shape === 'sphere') {
    geo = sphere(0.5, 32, 24);
  } else if (shape === 'shell') {
    const profile = [[0, -0.5], [0.5, -0.5], [0.5, -0.2]];
    for (let i = 1; i <= 10; i++) {
      const t = i / 10;
      profile.push([0.5 * Math.sqrt(Math.max(1e-4, 1 - t * t)), -0.2 + t * 0.6]);
    }
    profile.push([0, 0.5]);
    geo = lathe(profile, 32);
  } else if (shape === 'mortar') {
    const body = SY(sphere(0.42, 24, 18), 1.1);
    const tail = T(cylinder(0.12, 0.16, 0.45, 12), 0, -0.6, 0);
    const fins = T(box(0.9, 0.06, 0.3), 0, -0.75, 0);
    geo = computeNormals(mergeGeos([body, tail, fins]));
  } else if (shape === 'rocket') {
    const body = T(cylinder(0.32, 0.32, 0.72, 24), 0, 0.1, 0);
    const nose = T(cone(0.32, 0.28, 24), 0, 0.6, 0);
    const skirt = T(cylinder(0.36, 0.3, 0.1, 24), 0, -0.3, 0);
    const fins = [];
    for (let i = 0; i < 4; i++) {
      const a = (i / 4) * Math.PI * 2;
      fins.push(T(box(0.04, 0.22, 0.24), Math.cos(a) * 0.36, -0.32, Math.sin(a) * 0.36));
    }
    geo = computeNormals(mergeGeos([body, nose, skirt, ...fins]));
  } else if (shape === 'arrow') {
    const shaft = T(cylinder(0.02, 0.02, 0.8, 10), 0, 0, 0);
    const tip = T(cone(0.045, 0.12, 10), 0, 0.46, 0);
    const fletch = T(box(0.12, 0.01, 0.14), 0, -0.38, 0);
    geo = computeNormals(mergeGeos([shaft, tip, fletch]));
  } else {
    geo = box(0.8, 0.8, 0.8);
  }
  return { geo, color: c };
}

function bboxOf(geo) {
  const p = geo.positions;
  const min = [Infinity, Infinity, Infinity], max = [-Infinity, -Infinity, -Infinity];
  for (let i = 0; i < p.length / 3; i++) {
    for (let k = 0; k < 3; k++) {
      const v = p[i * 3 + k];
      if (v < min[k]) min[k] = v;
      if (v > max[k]) max[k] = v;
    }
  }
  return { min, max, size: [max[0] - min[0], max[1] - min[1], max[2] - min[2]] };
}

// Load an uploaded model file, normalized so its longest dimension = targetLengthM.
export async function loadModelFile(file, targetLengthM) {
  let geo = await parseModelFile(file);
  const bb = bboxOf(geo);
  const longest = Math.max(...bb.size) || 1;
  const s = targetLengthM / longest;
  geo = transformGeo(geo, mat4FromBasis([0, 0, 0], [1, 0, 0], [0, 1, 0], [0, 0, 1], s));
  const center = [(bb.min[0] + bb.max[0]) / 2 * s, (bb.min[1] + bb.max[1]) / 2 * s, (bb.min[2] + bb.max[2]) / 2 * s];
  geo = transformGeo(geo, mat4FromBasis([-center[0], -center[1], -center[2]], [1, 0, 0], [0, 1, 0], [0, 0, 1], 1));
  const st = meshStats(geo.positions, geo.indices);
  const ref = autoReferenceArea(geo.positions, geo.indices);
  return {
    object: { geo, color: [0.22, 0.6, 0.92] },
    stats: st,
    refArea: ref.area,
    scaledSize: bb.size.map((v) => v * s),
  };
}

// normalize any {geo} to fit a 1.4-unit preview box, centered
export function normalizeForPreview(geo, target = 1.4) {
  const bb = bboxOf(geo);
  const longest = Math.max(...bb.size) || 1;
  const s = target / longest;
  const center = [(bb.min[0] + bb.max[0]) / 2, (bb.min[1] + bb.max[1]) / 2, (bb.min[2] + bb.max[2]) / 2];
  return transformGeo(geo, mat4FromBasis([-center[0] * s, -center[1] * s, -center[2] * s], [1, 0, 0], [0, 1, 0], [0, 0, 1], s));
}
