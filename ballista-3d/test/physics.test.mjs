import {
  simulate, summarize, monteCarlo, defaultParams, normalizeParams,
  atmosphere, cdAt, geodeticToEcef, ecefToGeodetic, enuToEcef, gravityEcef,
  WGS84, greatCircleKm,
} from '../js/physics.js';

let passed = 0, failed = 0;
function ok(cond, name, extra = '') {
  if (cond) { passed++; console.log(`PASS  ${name}`); }
  else { failed++; console.log(`FAIL  ${name} ${extra}`); }
}
function approx(a, b, rel, name) {
  const err = Math.abs(a - b) / Math.max(Math.abs(b), 1e-12);
  ok(err <= rel, name, `(got ${a}, want ${b}, rel err ${(err * 100).toFixed(3)}%)`);
}

// --- 1. frame roundtrip ---
{
  const r = geodeticToEcef(33.5, -117.2, 250);
  const g = ecefToGeodetic(r[0], r[1], r[2]);
  approx(g.lat, 33.5, 1e-10, 'ecef->geodetic lat roundtrip');
  approx(g.lon, -117.2, 1e-10, 'ecef->geodetic lon roundtrip');
  approx(g.h, 250, 1e-8, 'ecef->geodetic h roundtrip');
  const e = enuToEcef(0, 0, 1, 0, 0); // east at equator/lon0 -> +y
  approx(e[1], 1, 1e-12, 'ENU east at (0,0) is +y');
  const n = enuToEcef(0, 0, 0, 1, 0); // north at equator -> +z
  approx(n[2], 1, 1e-12, 'ENU north at (0,0) is +z');
}

// --- 2. atmosphere sanity ---
{
  const s0 = atmosphere(0);
  approx(s0.rho, 1.225, 0.005, 'sea-level density ~1.225 kg/m^3');
  approx(s0.a, 340.3, 0.005, 'sea-level speed of sound ~340.3 m/s');
  const s11 = atmosphere(11000);
  approx(s11.T, 216.65, 1e-9, 'tropopause temperature 216.65 K');
  ok(atmosphere(50000).rho < s11.rho, 'density decreases with altitude');
  ok(atmosphere(100000).rho > 0 && atmosphere(100000).rho < 1e-5, 'exponential continuation above 86 km');
}

// --- 3. Cd interpolation ---
{
  const t = [[0, 0.4], [1, 0.8], [2, 0.6]];
  approx(cdAt(t, 0.5), 0.6, 1e-12, 'cdAt midpoint interpolation');
  approx(cdAt(t, -1), 0.4, 1e-12, 'cdAt clamp low');
  approx(cdAt(t, 5), 0.6, 1e-12, 'cdAt clamp high');
}

// --- 4. vacuum flat-earth comparison ---
{
  const p = defaultParams();
  p.sim.noAtmosphere = true;
  p.sim.dt = 0.005;
  p.launcher.type = 'cannon';
  p.launcher.muzzleVelocity = 300;
  p.launcher.elevation = 45;
  p.launcher.azimuth = 0;
  p.launch.alt = 0;
  p.env.windSpeed = 0;
  p.sim.maxTime = 120;
  const res = simulate(p);
  const s = summarize(res);
  const g0 = 9.80665;
  const v = 300, el = 45 * Math.PI / 180;
  const rangeFlat = (v * v * Math.sin(2 * el)) / g0 / 1000;
  const apogeeFlat = (v * v * Math.sin(el) * Math.sin(el)) / (2 * g0) / 1000;
  approx(s.rangeKm, rangeFlat, 0.02, 'vacuum range matches flat-earth analytic (2%)');
  approx(s.apogee.hKm, apogeeFlat, 0.02, 'vacuum apogee matches analytic (2%)');
  ok(!s.missed && res.impact.h < 5, 'impact detected near ground', `h=${res.impact.h}`);
  ok(s.impactSpeed > 250 && s.impactSpeed < 310, 'vacuum impact speed ~ launch speed (energy)', `v=${s.impactSpeed.toFixed(1)}`);
  // symmetry: impact angle ~ -45 deg
  approx(Math.abs(s.impactAngleDeg), 45, 0.03, 'vacuum 45deg impact angle ~ -45');
}

// --- 5. energy conservation in vacuum (validates integrator + gravity) ---
{
  const p = defaultParams();
  p.sim.noAtmosphere = true;
  p.sim.dt = 0.01;
  p.launcher.muzzleVelocity = 500;
  p.launcher.elevation = 60;
  p.env.windSpeed = 0;
  const res = simulate(p);
  const E = (smp) => {
    const r = Math.hypot(...smp.ecef);
    return 0.5 * smp.inertialSpeed ** 2 - WGS84.mu / r;
  };
  const E0 = E(res.samples[0]);
  let maxDrift = 0;
  for (const smp of res.samples) maxDrift = Math.max(maxDrift, Math.abs(E(smp) - E0) / Math.abs(E0));
  ok(maxDrift < 1e-5, 'specific orbital energy conserved in vacuum (RK4)', `drift=${maxDrift.toExponential(2)}`);
}

// --- 6. drag reduces range; wind matters ---
{
  const base = defaultParams();
  base.launcher.muzzleVelocity = 800;
  base.launcher.elevation = 45;
  base.env.windSpeed = 0;
  const withAtmo = summarize(simulate(base));
  const vac = JSON.parse(JSON.stringify(base));
  vac.sim.noAtmosphere = true;
  const noAtmo = summarize(simulate(vac));
  ok(withAtmo.rangeKm < noAtmo.rangeKm * 0.9, 'drag significantly reduces range', `atmo=${withAtmo.rangeKm.toFixed(1)}km vac=${noAtmo.rangeKm.toFixed(1)}km`);
  ok(withAtmo.qMaxKPa > 0 && withAtmo.machMax > 1, 'reports max-Q and max Mach', `Q=${withAtmo.qMaxKPa.toFixed(0)}kPa M=${withAtmo.machMax.toFixed(2)}`);

  const tail = JSON.parse(JSON.stringify(base));
  tail.env.windSpeed = 20; tail.env.windDirFrom = 180; tail.launcher.azimuth = 0; // blow toward north = downrange
  const tailRes = summarize(simulate(tail));
  const head = JSON.parse(JSON.stringify(base));
  head.env.windSpeed = 20; head.env.windDirFrom = 0; head.launcher.azimuth = 0; // from north = headwind
  const headRes = summarize(simulate(head));
  ok(tailRes.rangeKm > withAtmo.rangeKm && headRes.rangeKm < withAtmo.rangeKm, 'tailwind extends, headwind shortens range', `tail=${tailRes.rangeKm.toFixed(1)} base=${withAtmo.rangeKm.toFixed(1)} head=${headRes.rangeKm.toFixed(1)}`);
}

// --- 7. force/energy relation from muzzle energy ---
{
  const p = defaultParams();
  p.sim.noAtmosphere = true;
  p.launcher.muzzleVelocity = 100;
  p.launcher.elevation = 30;
  p.projectile.mass = 10;
  p.launch.alt = 0;
  const s = summarize(simulate(p));
  approx(s.keMJ, 0.5 * 10 * 100 * 100 / 1e6, 0.03, 'impact KE ~ muzzle KE in vacuum');
}

// --- 8. rocket with staging ---
{
  const p = defaultParams();
  p.launcher.type = 'rocket';
  p.launcher.muzzleVelocity = 2;
  p.launcher.elevation = 85;
  p.launcher.azimuth = 0;
  p.projectile.mass = 30;
  p.projectile.area = 0.01;
  p.projectile.cdTable = [[0, 0.3], [1, 0.5], [3, 0.4], [5, 0.3]];
  p.launcher.stages = [
    { thrust: 20000, propellantMass: 10, isp: 250, dropMass: 2 },
  ];
  p.env.windSpeed = 0;
  p.sim.maxTime = 600;
  const res = simulate(p);
  const s = summarize(res);
  ok(s.apogee.hKm > 5, 'rocket ascends to >5 km', `apogee=${s.apogee.hKm.toFixed(1)}km`);
  ok(!s.missed, 'rocket returns and lands');
  approx(res.impact.mass, 18, 1e-9, 'impact mass = initial - propellant - dropMass', `m=${res.impact.mass}`);
  const tBurn = (10 * 250 * 9.80665) / 20000;
  ok(Math.abs(s.flightTimeS) > tBurn, 'flight time exceeds burn time', `t=${s.flightTimeS.toFixed(1)}s burn=${tBurn.toFixed(1)}s`);
}

// --- 9. Monte Carlo dispersion ---
{
  const p = defaultParams();
  p.launcher.muzzleVelocity = 800;
  p.launcher.elevation = 45;
  p.env.windSpeed = 5;
  const mc = monteCarlo(p, { samples: 60, seed: 42 });
  ok(mc.impacts.length === 60, 'MC produced 60 impacts');
  ok(Number.isFinite(mc.cepKm) && mc.cepKm > 0, 'CEP positive and finite', `cep=${mc.cepKm.toFixed(3)}km`);
  const nominal = summarize(simulate(p));
  const tol = 5 * mc.stdDownrangeKm / Math.sqrt(mc.n) + 0.05;
  ok(Math.abs(mc.meanDownrangeKm - nominal.downrangeKm) < tol, 'MC mean near nominal impact', `mean=${mc.meanDownrangeKm.toFixed(2)} nominal=${nominal.downrangeKm.toFixed(2)} tol=${tol.toFixed(2)}`);
  ok(mc.stdDownrangeKm > 0 && mc.stdCrossrangeKm > 0, 'MC dispersion nonzero');
  const mc2 = monteCarlo(p, { samples: 60, seed: 42 });
  ok(mc2.cepKm === mc.cepKm, 'MC reproducible with same seed');
}

// --- 10. gravity model magnitude ---
{
  const r = geodeticToEcef(45, 0, 0);
  const g = gravityEcef(r);
  const gm = Math.hypot(...g);
  ok(gm > 9.79 && gm < 9.83, 'surface gravity magnitude ~9.8 m/s^2', `g=${gm.toFixed(4)}`);
  ok(greatCircleKm(0, 0, 0, 90) > 9900 && greatCircleKm(0, 0, 0, 90) < 10100, 'quarter circumference ~10000 km');
}

// --- 11. normalizeParams fills defaults ---
{
  const p = normalizeParams({ launcher: { type: 'cannon', mode: 'force', force: 500000, barrelLength: 6 }, projectile: { mass: 20 } });
  ok(p.projectile.area > 0 && p.sim.dt > 0 && Array.isArray(p.projectile.cdTable), 'normalizeParams fills defaults');
  const v = Math.sqrt(2 * 500000 * 6 / 20);
  approx(p.launcher.muzzleVelocity, v, 1e-12, 'force+barrel -> muzzle velocity sqrt(2FL/m)');
  const res = simulate(normalizeParams({ launch: { alt: 0 }, launcher: { type: 'cannon', mode: 'force', force: 500000, barrelLength: 6, elevation: 45 }, projectile: { mass: 20 }, env: { windSpeed: 0 }, sim: { noAtmosphere: true } }));
  approx(summarize(res).impactSpeed, v, 0.03, 'force-mode sim impact speed ~ sqrt(2FL/m)');
}

console.log(`\n${passed} passed, ${failed} failed`);
process.exit(failed ? 1 : 0);
