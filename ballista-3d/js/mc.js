// Non-blocking Monte Carlo for the UI: same perturbation model as physics.monteCarlo,
// but yields to the event loop every few samples and uses a coarser integrator step
// (dispersion statistics do not need dt = 0.01 s).
import { simulate, mulberry32, gaussian } from './physics.js';

function mcSigmas(opts) {
  return Object.assign(
    { muzzleVelocity: 0.01, elevationDeg: 0.05, azimuthDeg: 0.05, cdScale: 0.05, mass: 0.01, windSpeed: 1.0, windDirDeg: 3.0 },
    opts.sigmas || {}
  );
}

function mcPerturb(baseParams, rand, sig) {
  const p = JSON.parse(JSON.stringify(baseParams));
  const L = p.launcher, pr = p.projectile, env = p.env;
  if (L.muzzleVelocity != null) L.muzzleVelocity *= 1 + sig.muzzleVelocity * gaussian(rand);
  else if (L.force != null) L.force *= 1 + sig.muzzleVelocity * gaussian(rand);
  L.elevation += sig.elevationDeg * gaussian(rand);
  L.azimuth += sig.azimuthDeg * gaussian(rand);
  pr.cdScale = 1 + sig.cdScale * gaussian(rand);
  pr.mass *= 1 + sig.mass * gaussian(rand);
  env.windSpeed = Math.max(0, env.windSpeed + sig.windSpeed * gaussian(rand));
  env.windDirFrom = (env.windDirFrom + sig.windDirDeg * gaussian(rand)) % 360;
  p.sim = Object.assign({}, p.sim, { recordEvery: 1e9 });
  return p;
}

function mcStats(impacts, n, seed) {
  const downs = impacts.map((d) => d.down), crosses = impacts.map((d) => d.cross);
  const mean = (a) => a.reduce((s, x) => s + x, 0) / a.length;
  const std = (a, m) => Math.sqrt(a.reduce((s, x) => s + (x - m) * (x - m), 0) / Math.max(1, a.length - 1));
  const md = mean(downs), mc = mean(crosses);
  const sd = std(downs, md), sc = std(crosses, mc);
  const cepKm = 0.5887 * (sd + sc);
  const r95Km = 2.4477 * Math.max(sd, sc);
  return {
    n, seed, impacts,
    meanDownrangeKm: md, meanCrossrangeKm: mc,
    stdDownrangeKm: sd, stdCrossrangeKm: sc,
    cepKm, r95Km,
  };
}

export async function monteCarloAsync(baseParams, opts = {}) {
  const n = opts.samples || 50;
  const seed = opts.seed != null ? opts.seed : 1337;
  const rand = mulberry32(seed);
  const sig = mcSigmas(opts);
  const impacts = [];
  for (let i = 0; i < n; i++) {
    const p = mcPerturb(baseParams, rand, sig);
    p.sim.dt = Math.max(p.sim.dt || 0.01, 0.04);
    const res = simulate(p);
    impacts.push({ down: res.impact.downrangeKm, cross: res.impact.crossrangeKm, lat: res.impact.lat, lon: res.impact.lon, rangeKm: res.impact.rangeKm });
    if (opts.onProgress) opts.onProgress(i + 1, n);
    if ((i % 4) === 3) await new Promise((r) => setTimeout(r, 0));
  }
  return mcStats(impacts, n, seed);
}
