import { decodeLandMask, dayTexture, nightTexture, cloudsTexture } from '../js/textures.js';
import { LAND_MASK_W, LAND_MASK_H } from '../js/landmask.js';

let passed = 0, failed = 0;
const ok = (c, name, extra = '') => { if (c) { passed++; console.log('PASS  ' + name); } else { failed++; console.log('FAIL  ' + name + ' ' + extra); } };

const mask = await decodeLandMask();
ok(mask.length === (LAND_MASK_W * LAND_MASK_H + 7) >> 3, 'mask decodes to expected byte count', `${mask.length}`);
let land = 0;
for (const b of mask) for (let i = 0; i < 8; i++) land += (b >> (7 - i)) & 1;
const frac = land / (LAND_MASK_W * LAND_MASK_H);
ok(frac > 0.24 && frac < 0.36, 'land fraction ~29%', `${(frac * 100).toFixed(1)}%`);

const day = await dayTexture(256, 128);
ok(day.w === 256 && day.h === 128 && day.data.length === 256 * 128 * 4, 'day texture dims');
let oceanBlue = 0, oceanN = 0, landGreen = 0, landN = 0;
for (let y = 0; y < 128; y += 2) {
  for (let x = 0; x < 256; x += 2) {
    const lat = 90 - ((y + 0.5) / 128) * 180;
    const lon = ((x + 0.5) / 256) * 360 - 180;
    const mx = Math.min(LAND_MASK_W - 1, Math.floor(((lon + 180) / 360) * LAND_MASK_W));
    const my = Math.min(LAND_MASK_H - 1, Math.floor(((90 - lat) / 180) * LAND_MASK_H));
    const isLand = (mask[my * ((LAND_MASK_W + 7) >> 3) + (mx >> 3)] >> (7 - (mx & 7))) & 1;
    const i = (y * 256 + x) * 4;
    if (isLand) { landN++; if (day.data[i + 1] > day.data[i + 2] || day.data[i] > 200) landGreen++; }
    else { oceanN++; if (day.data[i + 2] > day.data[i]) oceanBlue++; }
  }
}
ok(oceanN > 1000 && oceanBlue / oceanN > 0.9, 'ocean pixels are blue-dominant', `${(100 * oceanBlue / oceanN).toFixed(0)}% of ${oceanN}`);
ok(landN > 500 && landGreen / landN > 0.5, 'land pixels are green/tan/bright', `${(100 * landGreen / landN).toFixed(0)}% of ${landN}`);

const night = await nightTexture(256, 128);
let lights = 0, lightsOnOcean = 0, oceanN2 = 0;
for (let y = 0; y < 128; y += 2) {
  for (let x = 0; x < 256; x += 2) {
    const lat = 90 - ((y + 0.5) / 128) * 180;
    const lon = ((x + 0.5) / 256) * 360 - 180;
    const mx = Math.min(LAND_MASK_W - 1, Math.floor(((lon + 180) / 360) * LAND_MASK_W));
    const my = Math.min(LAND_MASK_H - 1, Math.floor(((90 - lat) / 180) * LAND_MASK_H));
    const isLand = (mask[my * ((LAND_MASK_W + 7) >> 3) + (mx >> 3)] >> (7 - (mx & 7))) & 1;
    const i = (y * 256 + x) * 4;
    if (night.data[i] > 120) { lights++; if (!isLand) lightsOnOcean++; }
    if (!isLand) oceanN2++;
  }
}
ok(lights > 20, 'night texture has city lights', `${lights}`);
ok(lightsOnOcean === 0, 'no city lights over ocean', `${lightsOnOcean}`);

const cl = cloudsTexture(128, 64);
let alphaSum = 0, maxA = 0, minA = 255;
for (let i = 3; i < cl.data.length; i += 4) { alphaSum += cl.data[i]; maxA = Math.max(maxA, cl.data[i]); minA = Math.min(minA, cl.data[i]); }
const meanA = alphaSum / (cl.w * cl.h);
ok(cl.data.length === 128 * 64 * 4 && cl.data[0] === 255, 'clouds RGBA white');
ok(meanA > 10 && meanA < 200 && maxA > 200 && minA < 60, 'cloud alpha has dynamic range', `mean=${meanA.toFixed(1)} min=${minA} max=${maxA}`);

if (process.env.PREVIEW) {
  const { writeFileSync } = await import('node:fs');
  const ppm = (t) => {
    let hdr = `P6\n${t.w} ${t.h}\n255\n`;
    const buf = Buffer.alloc(t.w * t.h * 3);
    for (let i = 0, j = 0; i < t.data.length; i += 4, j += 3) { buf[j] = t.data[i]; buf[j + 1] = t.data[i + 1]; buf[j + 2] = t.data[i + 2]; }
    return Buffer.concat([Buffer.from(hdr), buf]);
  };
  const big = await dayTexture(1024, 512);
  writeFileSync('/tmp/day.ppm', ppm(big));
  const nbig = await nightTexture(1024, 512);
  writeFileSync('/tmp/night.ppm', ppm(nbig));
  writeFileSync('/tmp/clouds.ppm', ppm(cloudsTexture(512, 256)));
  console.log('previews written to /tmp/*.ppm');
}

console.log(`\n${passed} passed, ${failed} failed`);
process.exit(failed ? 1 : 0);
