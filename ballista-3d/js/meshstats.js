// Pure-math mesh statistics from raw position/index arrays (no three.js needed).

export function meshStats(positions, index) {
  let min = [Infinity, Infinity, Infinity], max = [-Infinity, -Infinity, -Infinity];
  let vol = 0, area = 0;
  const n = positions.length / 3;
  for (let i = 0; i < n; i++) {
    for (let k = 0; k < 3; k++) {
      const v = positions[i * 3 + k];
      if (v < min[k]) min[k] = v;
      if (v > max[k]) max[k] = v;
    }
  }
  const tris = index ? index.length / 3 : n / 3;
  for (let t = 0; t < tris; t++) {
    const i0 = index ? index[t * 3] : t * 3;
    const i1 = index ? index[t * 3 + 1] : t * 3 + 1;
    const i2 = index ? index[t * 3 + 2] : t * 3 + 2;
    const ax = positions[i0 * 3], ay = positions[i0 * 3 + 1], az = positions[i0 * 3 + 2];
    const bx = positions[i1 * 3], by = positions[i1 * 3 + 1], bz = positions[i1 * 3 + 2];
    const cx = positions[i2 * 3], cy = positions[i2 * 3 + 1], cz = positions[i2 * 3 + 2];
    const ux = bx - ax, uy = by - ay, uz = bz - az;
    const vx = cx - ax, vy = cy - ay, vz = cz - az;
    const nx = uy * vz - uz * vy, ny = uz * vx - ux * vz, nz = ux * vy - uy * vx;
    area += 0.5 * Math.hypot(nx, ny, nz);
    vol += (ax * nx + ay * ny + az * nz) / 6;
  }
  const size = [max[0] - min[0], max[1] - min[1], max[2] - min[2]];
  return { volume: Math.abs(vol), surfaceArea: area, min, max, size };
}

// Silhouette area seen along dir: sum of front-facing triangle projected areas
// (exact for convex bodies, tight upper bound for concave ones).
export function projectedArea(positions, index, dir) {
  const dl = Math.hypot(dir[0], dir[1], dir[2]) || 1;
  const d = [dir[0] / dl, dir[1] / dl, dir[2] / dl];
  let a = 0;
  const tris = index ? index.length / 3 : positions.length / 9;
  for (let t = 0; t < tris; t++) {
    const i0 = index ? index[t * 3] : t * 3;
    const i1 = index ? index[t * 3 + 1] : t * 3 + 1;
    const i2 = index ? index[t * 3 + 2] : t * 3 + 2;
    const ax = positions[i0 * 3], ay = positions[i0 * 3 + 1], az = positions[i0 * 3 + 2];
    const bx = positions[i1 * 3], by = positions[i1 * 3 + 1], bz = positions[i1 * 3 + 2];
    const cx = positions[i2 * 3], cy = positions[i2 * 3 + 1], cz = positions[i2 * 3 + 2];
    const ux = bx - ax, uy = by - ay, uz = bz - az;
    const vx = cx - ax, vy = cy - ay, vz = cz - az;
    const nx = uy * vz - uz * vy, ny = uz * vx - ux * vz, nz = ux * vy - uy * vx;
    const proj = nx * d[0] + ny * d[1] + nz * d[2];
    if (proj > 0) a += 0.5 * proj;
  }
  return a;
}

// Long axis of the bounding box (dominant dimension) and projected area along it.
export function autoReferenceArea(positions, index) {
  const st = meshStats(positions, index);
  const axes = [
    [1, 0, 0], [0, 1, 0], [0, 0, 1],
    [1 / Math.SQRT2, 1 / Math.SQRT2, 0], [1 / Math.SQRT2, 0, 1 / Math.SQRT2], [0, 1 / Math.SQRT2, 1 / Math.SQRT2],
  ];
  let best = 0, bestAxis = [1, 0, 0];
  for (const ax of axes) {
    const a = projectedArea(positions, index, ax);
    if (a > best) { best = a; bestAxis = ax; }
  }
  return { area: best, axis: bestAxis, stats: st };
}
