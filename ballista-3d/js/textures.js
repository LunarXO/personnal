import { LAND_MASK_W, LAND_MASK_H, LAND_MASK_B64 } from './landmask.js';

export async function decodeLandMask() {
  const bin = atob(LAND_MASK_B64);
  const buf = Uint8Array.from(bin, (c) => c.charCodeAt(0));
  const ds = new DecompressionStream('deflate');
  const out = await new Response(new Blob([buf]).stream().pipeThrough(ds)).arrayBuffer();
  return new Uint8Array(out);
}

function mulberry32(seed) {
  let a = seed >>> 0;
  return () => {
    a |= 0; a = (a + 0x6D2B79F5) | 0;
    let t = Math.imul(a ^ (a >>> 15), 1 | a);
    t = (t + Math.imul(t ^ (t >>> 7), 61 | t)) ^ t;
    return ((t ^ (t >>> 14)) >>> 0) / 4294967296;
  };
}

function hash3(ix, iy, iz, seed) {
  let h = seed ^ Math.imul(ix, 374761393) ^ Math.imul(iy, 668265263) ^ Math.imul(iz, 2246822519);
  h = Math.imul(h ^ (h >>> 13), 1274126177);
  return ((h ^ (h >>> 16)) >>> 0) / 4294967296;
}

function smooth(t) { return t * t * (3 - 2 * t); }

function vnoise3(x, y, z, seed) {
  const ix = Math.floor(x), iy = Math.floor(y), iz = Math.floor(z);
  const fx = x - ix, fy = y - iy, fz = z - iz;
  const u = smooth(fx), v = smooth(fy), w = smooth(fz);
  const c000 = hash3(ix, iy, iz, seed), c100 = hash3(ix + 1, iy, iz, seed);
  const c010 = hash3(ix, iy + 1, iz, seed), c110 = hash3(ix + 1, iy + 1, iz, seed);
  const c001 = hash3(ix, iy, iz + 1, seed), c101 = hash3(ix + 1, iy, iz + 1, seed);
  const c011 = hash3(ix, iy + 1, iz + 1, seed), c111 = hash3(ix + 1, iy + 1, iz + 1, seed);
  const x00 = c000 + (c100 - c000) * u, x10 = c010 + (c110 - c010) * u;
  const x01 = c001 + (c101 - c001) * u, x11 = c011 + (c111 - c011) * u;
  const y0 = x00 + (x10 - x00) * v, y1 = x01 + (x11 - x01) * v;
  return y0 + (y1 - y0) * w;
}

function fbm3(x, y, z, oct, seed) {
  let amp = 0.5, freq = 1, sum = 0, norm = 0;
  for (let i = 0; i < oct; i++) {
    sum += amp * vnoise3(x * freq, y * freq, z * freq, seed + i * 101);
    norm += amp; amp *= 0.5; freq *= 2.03;
  }
  return sum / norm;
}

function maskBit(mask, lon, lat) {
  const x = Math.min(LAND_MASK_W - 1, Math.max(0, Math.floor(((lon + 180) / 360) * LAND_MASK_W)));
  const y = Math.min(LAND_MASK_H - 1, Math.max(0, Math.floor(((90 - lat) / 180) * LAND_MASK_H)));
  const byte = mask[y * ((LAND_MASK_W + 7) >> 3) + (x >> 3)];
  return (byte >> (7 - (x & 7))) & 1;
}

function lerp(a, b, t) { return a + (b - a) * t; }
function mix3(c1, c2, t) { return [lerp(c1[0], c2[0], t), lerp(c1[1], c2[1], t), lerp(c1[2], c2[2], t)]; }

export async function landMaskAt(lon, lat) {
  if (!landMaskAt.cache) landMaskAt.cache = await decodeLandMask();
  return maskBit(landMaskAt.cache, lon, lat);
}

export async function dayTexture(w = 1024, h = 512, seed = 1337) {
  const mask = await decodeLandMask();
  const data = new Uint8Array(w * h * 4);
  for (let y = 0; y < h; y++) {
    const lat = 90 - ((y + 0.5) / h) * 180;
    const sLat = Math.sin((lat * Math.PI) / 180);
    for (let x = 0; x < w; x++) {
      const lon = ((x + 0.5) / w) * 360 - 180;
      const th = (lon * Math.PI) / 180;
      const nx = Math.cos(th), ny = Math.sin(th);
      const n1 = fbm3(nx * 3 + 10, ny * 3, sLat * 3, 5, seed);
      const n2 = fbm3(nx * 7 - 4, ny * 7, sLat * 7 + 31, 4, seed + 7);
      const i = (y * w + x) * 4;
      let c;
      const land = maskBit(mask, lon, lat);
      const aLat = Math.abs(lat);
      if (!land) {
        const depth = fbm3(nx * 2 + 50, ny * 2, sLat * 2 + 77, 4, seed + 13);
        c = mix3([16, 48, 105], [38, 96, 150], depth);
        if (aLat > 68 + n1 * 6) c = mix3(c, [225, 235, 245], Math.min(1, (aLat - 66) / 10));
      } else {
        const dry = fbm3(nx * 5 + 90, ny * 5, sLat * 5 + 11, 4, seed + 29);
        const moist = fbm3(nx * 4 - 60, ny * 4, sLat * 4 + 53, 4, seed + 41);
        if (aLat > 62 + dry * 8) c = [235, 240, 245];
        else if (aLat > 50) c = mix3([110, 105, 80], [90, 110, 70], moist);
        else if (aLat > 30 && dry > 0.58) c = mix3([168, 148, 100], [140, 120, 80], dry - 0.58);
        else c = mix3([52, 96, 44], [86, 128, 58], moist);
        c = mix3(c, [70, 70, 62], n2 * 0.25);
      }
      const shade = 0.92 + n1 * 0.16;
      data[i] = Math.max(0, Math.min(255, c[0] * shade));
      data[i + 1] = Math.max(0, Math.min(255, c[1] * shade));
      data[i + 2] = Math.max(0, Math.min(255, c[2] * shade));
      data[i + 3] = 255;
    }
  }
  return { w, h, data };
}

export async function nightTexture(w = 1024, h = 512, seed = 9241) {
  const mask = await decodeLandMask();
  const data = new Uint8Array(w * h * 4);
  for (let y = 0; y < h; y++) {
    const lat = 90 - ((y + 0.5) / h) * 180;
    const sLat = Math.sin((lat * Math.PI) / 180);
    for (let x = 0; x < w; x++) {
      const lon = ((x + 0.5) / w) * 360 - 180;
      const th = (lon * Math.PI) / 180;
      const nx = Math.cos(th), ny = Math.sin(th);
      const i = (y * w + x) * 4;
      const land = maskBit(mask, lon, lat);
      let c = [4, 9, 22];
      if (land) {
        c = [10, 12, 20];
        const lights = fbm3(nx * 6 + 7, ny * 6, sLat * 6 + 91, 5, seed);
        const urban = fbm3(nx * 13 - 21, ny * 13, sLat * 13 + 47, 4, seed + 3);
        const t = Math.min(1, Math.max(0, (lights - 0.62) * 6)) * Math.min(1, Math.max(0, (urban - 0.45) * 4));
        if (t > 0) {
          const warm = 0.75 + 0.25 * fbm3(nx * 21, ny * 21, sLat * 21, 3, seed + 11);
          c = [lerp(c[0], 255, t), lerp(c[1], 186 * warm, t), lerp(c[2], 105 * warm, t)];
        }
      }
      data[i] = c[0]; data[i + 1] = c[1]; data[i + 2] = c[2]; data[i + 3] = 255;
    }
  }
  return { w, h, data };
}

export function cloudsTexture(w = 512, h = 256, seed = 555) {
  const data = new Uint8Array(w * h * 4);
  for (let y = 0; y < h; y++) {
    const lat = 90 - ((y + 0.5) / h) * 180;
    const sLat = Math.sin((lat * Math.PI) / 180);
    for (let x = 0; x < w; x++) {
      const lon = ((x + 0.5) / w) * 360 - 180;
      const th = (lon * Math.PI) / 180;
      const nx = Math.cos(th), ny = Math.sin(th);
      let n = fbm3(nx * 4 + 3, ny * 4, sLat * 4 + 17, 5, seed);
      n += 0.35 * fbm3(nx * 9 - 8, ny * 9, sLat * 9 + 63, 4, seed + 5);
      n /= 1.35;
      const band = Math.sin((lat * Math.PI) / 180 * 3.0) * 0.06;
      const a = Math.max(0, Math.min(1, (n + band - 0.52) * 4.5));
      const i = (y * w + x) * 4;
      data[i] = 255; data[i + 1] = 255; data[i + 2] = 255;
      data[i + 3] = Math.round(a * 235);
    }
  }
  return { w, h, data };
}
