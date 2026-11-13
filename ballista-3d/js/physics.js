// Ballista 3D - physics core (pure ESM, no rendering deps, unit-testable in Node)
// Model: point-mass 3-DOF integrated in ECI (inertial) frame with RK4.
//  - WGS84 ellipsoid, gravity = point mass + J2 zonal harmonic
//  - ISA atmosphere to 84.852 km + exponential continuation (density, pressure, temperature, speed of sound)
//  - Quadratic drag with Mach-dependent Cd table, reference area
//  - Wind (meteorological "from" direction) with power-law shear, advected with lat/lon
//  - Earth rotation enters via initial velocity (omega x r) and drag relative wind -> Coriolis included
//  - Rockets: piecewise thrust schedule (staging), mass from propellant burn + stage drops

export const WGS84 = {
  a: 6378137.0,
  f: 1 / 298.257223563,
  mu: 3.986004418e14,
  J2: 1.08262668e-3,
  omega: 7.2921150e-5,
};
WGS84.e2 = WGS84.f * (2 - WGS84.f);

export const ATM = { R: 287.05287, gamma: 1.4, g0: 9.80665 };

const DEG = Math.PI / 180;

// ---------------------------------------------------------------- frames ---
export function geodeticToEcef(latDeg, lonDeg, h) {
  const lat = latDeg * DEG, lon = lonDeg * DEG;
  const s = Math.sin(lat), c = Math.cos(lat);
  const N = WGS84.a / Math.sqrt(1 - WGS84.e2 * s * s);
  return [
    (N + h) * c * Math.cos(lon),
    (N + h) * c * Math.sin(lon),
    (N * (1 - WGS84.e2) + h) * s,
  ];
}

export function ecefToGeodetic(x, y, z) {
  const p = Math.hypot(x, y);
  const lon = Math.atan2(y, x);
  let lat = Math.atan2(z, p * (1 - WGS84.e2));
  let h = 0;
  for (let i = 0; i < 8; i++) {
    const s = Math.sin(lat);
    const N = WGS84.a / Math.sqrt(1 - WGS84.e2 * s * s);
    h = p / Math.cos(lat) - N;
    lat = Math.atan2(z, p * (1 - WGS84.e2 * (N / (N + h))));
  }
  return { lat: lat / DEG, lon: lon / DEG, h };
}

export function enuToEcef(latDeg, lonDeg, ve, vn, vu) {
  const lat = latDeg * DEG, lon = lonDeg * DEG;
  const sl = Math.sin(lat), cl = Math.cos(lat);
  const so = Math.sin(lon), co = Math.cos(lon);
  return [
    -so * ve - sl * co * vn + cl * co * vu,
    co * ve - sl * so * vn + cl * so * vu,
    cl * vn + sl * vu,
  ];
}

export function ecefToEnu(latDeg, lonDeg, v) {
  const lat = latDeg * DEG, lon = lonDeg * DEG;
  const sl = Math.sin(lat), cl = Math.cos(lat);
  const so = Math.sin(lon), co = Math.cos(lon);
  return [
    -so * v[0] + co * v[1],
    -sl * co * v[0] - sl * so * v[1] + cl * v[2],
    cl * co * v[0] + cl * so * v[1] + sl * v[2],
  ];
}

export function omegaCrossR(r) {
  return [-WGS84.omega * r[1], WGS84.omega * r[0], 0];
}

// rotate a vector about the z axis by ang (rad). Used to bring ECI state into the Earth-fixed frame.
export function rotZ(v, ang) {
  const c = Math.cos(ang), s = Math.sin(ang);
  return [c * v[0] - s * v[1], s * v[0] + c * v[1], v[2]];
}

// ECI position -> Earth-fixed (ECEF) position at time t
export function eciToEcef(t, r) {
  return rotZ(r, -WGS84.omega * t);
}

export function greatCircleKm(lat1, lon1, lat2, lon2) {
  const p1 = lat1 * DEG, p2 = lat2 * DEG;
  const dl = (lon2 - lon1) * DEG;
  const c = Math.sin(p1) * Math.sin(p2) + Math.cos(p1) * Math.cos(p2) * Math.cos(dl);
  return WGS84.a / 1000 * Math.acos(Math.min(1, Math.max(-1, c)));
}

// ------------------------------------------------------------- gravity -----
export function gravityEcef(r) {
  const [x, y, z] = r;
  const r2 = x * x + y * y + z * z;
  const rm = Math.sqrt(r2);
  const r3 = r2 * rm;
  const zr = (z * z) / r2;
  const k = 1.5 * WGS84.J2 * (WGS84.a * WGS84.a / r2);
  const mu = WGS84.mu;
  return [
    (-mu * x / r3) * (1 - k * (5 * zr - 1)),
    (-mu * y / r3) * (1 - k * (5 * zr - 1)),
    (-mu * z / r3) * (1 - k * (5 * zr - 3)),
  ];
}

// ---------------------------------------------------------- atmosphere -----
// ISA layers (base altitude m, base temperature K, lapse rate K/m)
const ISA_LAYERS = [
  [0, 288.15, -0.0065],
  [11000, 216.65, 0.0],
  [20000, 216.65, 0.001],
  [32000, 228.65, 0.0028],
  [47000, 270.65, 0.0],
  [51000, 270.65, -0.0028],
  [71000, 214.65, -0.002],
  [84852, 186.946, 0.0],
];
const ISA_TOP = 84852;

function isaBoundaryTable() {
  const rows = [{ h: 0, T: 288.15, p: 101325.0 }];
  for (let i = 1; i < ISA_LAYERS.length; i++) {
    const h0 = ISA_LAYERS[i - 1][0], T0 = ISA_LAYERS[i - 1][1], L = ISA_LAYERS[i - 1][2];
    const h1 = ISA_LAYERS[i][0];
    const T1 = T0 + L * (h1 - h0);
    let p1;
    if (Math.abs(L) < 1e-12) p1 = rows[i - 1].p * Math.exp(-ATM.g0 * (h1 - h0) / (ATM.R * T0));
    else p1 = rows[i - 1].p * Math.pow(T1 / T0, -ATM.g0 / (L * ATM.R));
    rows.push({ h: h1, T: T1, p: p1 });
  }
  return rows;
}
const ISA_TABLE = isaBoundaryTable();

// h: geodetic altitude (m). Returns {T, p, rho, a} with env offsets applied.
export function atmosphere(h, env = {}) {
  const dT = env.tempOffsetK || 0;
  const pRatio = env.pressureRatio != null ? env.pressureRatio : 1;
  let T, p;
  if (h <= ISA_TOP) {
    let i = 0;
    while (i < ISA_TABLE.length - 2 && h > ISA_TABLE[i + 1].h) i++;
    const b0 = ISA_TABLE[i], b1 = ISA_TABLE[i + 1];
    const L = (b1.T - b0.T) / (b1.h - b0.h);
    T = b0.T + L * (h - b0.h);
    if (Math.abs(L) < 1e-12) p = b0.p * Math.exp(-ATM.g0 * (h - b0.h) / (ATM.R * b0.T));
    else p = b0.p * Math.pow(T / b0.T, -ATM.g0 / (L * ATM.R));
  } else {
    const top = ISA_TABLE[ISA_TABLE.length - 1];
    T = top.T;
    p = top.p * Math.exp(-ATM.g0 * (h - top.h) / (ATM.R * top.T));
  }
  T += dT;
  p *= pRatio;
  const rho = p / (ATM.R * T);
  const a = Math.sqrt(ATM.gamma * ATM.R * T);
  return { T, p, rho, a };
}

// ------------------------------------------------------------ Cd table -----
export function cdAt(cdTable, mach) {
  if (!cdTable || cdTable.length === 0) return 0.4;
  if (mach <= cdTable[0][0]) return cdTable[0][1];
  const n = cdTable.length;
  if (mach >= cdTable[n - 1][0]) return cdTable[n - 1][1];
  for (let i = 0; i < n - 1; i++) {
    const [m0, c0] = cdTable[i], [m1, c1] = cdTable[i + 1];
    if (mach >= m0 && mach <= m1) {
      const f = (mach - m0) / (m1 - m0);
      return c0 + f * (c1 - c0);
    }
  }
  return cdTable[n - 1][1];
}

// -------------------------------------------------------------- terrain ----
export function terrainHeightM(latDeg, lonDeg, mode) {
  if (mode !== 'hills') return 0;
  const la = latDeg * DEG, lo = lonDeg * DEG;
  return (
    900 * Math.sin(2.3 * la + 0.7) * Math.sin(1.9 * lo + 2.1) +
    450 * Math.sin(4.1 * la - 1.3) * Math.cos(3.3 * lo) +
    200 * Math.sin(7.7 * lo + 0.4)
  );
}

// ------------------------------------------------------------------ RNG ----
export function mulberry32(seed) {
  let s = seed >>> 0;
  return function () {
    s = (s + 0x6d2b79f5) >>> 0;
    let t = s;
    t = Math.imul(t ^ (t >>> 15), t | 1);
    t ^= t + Math.imul(t ^ (t >>> 7), t | 61);
    return ((t ^ (t >>> 14)) >>> 0) / 4294967296;
  };
}
export function gaussian(rand) {
  let u = 0, v = 0;
  while (u === 0) u = rand();
  while (v === 0) v = rand();
  return Math.sqrt(-2 * Math.log(u)) * Math.cos(2 * Math.PI * v);
}

// ------------------------------------------------------------ simulation ---
// params: see README / ui defaults. Returns { samples, impact, stats }
export function simulate(params) {
  const p = normalizeParams(params);
  const { launch, launcher, projectile, env, sim } = p;

  const th0 = terrainHeightM(launch.lat, launch.lon, sim.terrain);
  const r0 = geodeticToEcef(launch.lat, launch.lon, launch.alt + th0);

  let v0;
  if (launcher.type === 'rocket') {
    v0 = launcher.muzzleVelocity || 0.1;
  } else if (launcher.muzzleVelocity != null && launcher.muzzleVelocity > 0) {
    v0 = launcher.muzzleVelocity;
  } else {
    v0 = Math.sqrt(2 * launcher.force * launcher.barrelLength / projectile.mass);
  }
  const el = launcher.elevation * DEG, az = launcher.azimuth * DEG;
  const enu0 = [v0 * Math.cos(el) * Math.sin(az), v0 * Math.cos(el) * Math.cos(az), v0 * Math.sin(el)];
  const ecefEnu0 = enuToEcef(launch.lat, launch.lon, enu0[0], enu0[1], enu0[2]);
  const w0 = omegaCrossR(r0);
  let v = [ecefEnu0[0] + w0[0], ecefEnu0[1] + w0[1], ecefEnu0[2] + w0[2]];
  let r = r0.slice();

  const stages = buildStages(launcher);
  const launchDir = ecefEnu0; // used for thrust direction at low speed

  function thrustAt(t) {
    let acc = 0;
    for (const s of stages) {
      if (t >= acc && t < acc + s.burn) return { thrust: s.thrust, active: true, stage: s };
      acc += s.burn;
    }
    return { thrust: 0, active: false, stage: null };
  }
  function massAt(t) {
    let m = projectile.mass, acc = 0;
    for (const s of stages) {
      if (t <= acc) break;
      const dt = Math.min(t, acc + s.burn) - acc;
      m -= s.mdot * dt;
      if (t >= acc + s.burn) m -= s.dropMass;
      acc += s.burn;
    }
    return Math.max(m, 1e-3);
  }

  function windEcef(lat, lon, h) {
    const w = env.windSpeed * Math.pow(Math.max(h, 10) / 10, env.windShearExponent || 0);
    const dir = (env.windDirFrom || 0) * DEG;
    const ve = -w * Math.sin(dir), vn = -w * Math.cos(dir);
    return enuToEcef(lat, lon, ve, vn, 0);
  }

  function deriv(t, y) {
    const r = [y[0], y[1], y[2]];
    const v = [y[3], y[4], y[5]];
    const acc = gravityEcef(r);
    const rf = eciToEcef(t, r);
    const geo = ecefToGeodetic(rf[0], rf[1], rf[2]);
    const th = terrainHeightM(geo.lat, geo.lon, sim.terrain);
    const atm = atmosphere(Math.max(geo.h - th, 0), env);
    const w = omegaCrossR(r);
    const wind = env.windSpeed > 0 ? windEcef(geo.lat, geo.lon, geo.h) : [0, 0, 0];
    const vrel = [v[0] - w[0] - wind[0], v[1] - w[1] - wind[1], v[2] - w[2] - wind[2]];
    const vmag = Math.hypot(vrel[0], vrel[1], vrel[2]);
    if (!sim.noAtmosphere && vmag > 1e-6 && atm.rho > 1e-12) {
      const mach = vmag / atm.a;
      const cd = cdAt(projectile.cdTable, mach) * (projectile.cdScale || 1);
      const f = -(0.5 * atm.rho * cd * projectile.area * vmag) / massAt(t);
      acc[0] += f * vrel[0];
      acc[1] += f * vrel[1];
      acc[2] += f * vrel[2];
    }
    const th2 = thrustAt(t);
    if (th2.active && th2.thrust > 0) {
      let dir;
      if (vmag > 5) dir = [vrel[0] / vmag, vrel[1] / vmag, vrel[2] / vmag];
      else {
        const m = Math.hypot(launchDir[0], launchDir[1], launchDir[2]) || 1;
        dir = [launchDir[0] / m, launchDir[1] / m, launchDir[2] / m];
      }
      const m = massAt(t);
      acc[0] += (th2.thrust / m) * dir[0];
      acc[1] += (th2.thrust / m) * dir[1];
      acc[2] += (th2.thrust / m) * dir[2];
    }
    return [v[0], v[1], v[2], acc[0], acc[1], acc[2]];
  }

  function rk4(t, y, dt) {
    const k1 = deriv(t, y);
    const y2 = y.map((v, i) => v + 0.5 * dt * k1[i]);
    const k2 = deriv(t + 0.5 * dt, y2);
    const y3 = y.map((v, i) => v + 0.5 * dt * k2[i]);
    const k3 = deriv(t + 0.5 * dt, y3);
    const y4 = y.map((v, i) => v + dt * k3[i]);
    const k4 = deriv(t + dt, y4);
    return y.map((v, i) => v + (dt / 6) * (k1[i] + 2 * k2[i] + 2 * k3[i] + k4[i]));
  }

  const dt = sim.dt;
  const samples = [];
  const pushSample = (t, y) => {
    const r = eciToEcef(t, [y[0], y[1], y[2]]);
    const v = [y[3], y[4], y[5]];
    const geo = ecefToGeodetic(r[0], r[1], r[2]);
    const th = terrainHeightM(geo.lat, geo.lon, sim.terrain);
    const w = omegaCrossR([y[0], y[1], y[2]]);
    const vg = [v[0] - w[0], v[1] - w[1], v[2] - w[2]];
    const atm = atmosphere(Math.max(geo.h - th, 0), env);
    const vmag = Math.hypot(vg[0], vg[1], vg[2]);
    samples.push({
      t, ecef: r, lat: geo.lat, lon: geo.lon, h: geo.h - th,
      speed: vmag, inertialSpeed: Math.hypot(v[0], v[1], v[2]),
      mach: vmag / atm.a, q: 0.5 * atm.rho * vmag * vmag,
      mass: massAt(t), rangeKm: greatCircleKm(launch.lat, launch.lon, geo.lat, geo.lon),
    });
  };

  let t = 0, y = [...r, ...v];
  let lastRecord = -1;
  pushSample(0, y);
  lastRecord = 0;

  let impact = null;
  let prevY = y.slice(), prevT = 0;
  const maxR = 4 * WGS84.a;
  while (t < sim.maxTime) {
    prevY = y.slice(); prevT = t;
    y = rk4(t, y, dt);
    t += dt;
    const geo = ecefToGeodetic(...eciToEcef(t, y));
    const th = terrainHeightM(geo.lat, geo.lon, sim.terrain);
    if (geo.h - th <= 0) {
      // bisect within the last step for the impact point
      let lo = 0, hi = dt;
      for (let i = 0; i < 24; i++) {
        const mid = 0.5 * (lo + hi);
        const ym = rk4(prevT, prevY, mid);
        const gm = ecefToGeodetic(...eciToEcef(prevT + mid, ym));
        if (gm.h - terrainHeightM(gm.lat, gm.lon, sim.terrain) <= 0) hi = mid; else lo = mid;
      }
      y = rk4(prevT, prevY, hi);
      t = prevT + hi;
      impact = buildImpact(t, y, p);
      pushSample(t, y);
      break;
    }
    if (Math.hypot(y[0], y[1], y[2]) > maxR) break; // escaped
    if (t - lastRecord >= sim.recordEvery) { pushSample(t, y); lastRecord = t; }
  }
  if (!impact) impact = buildImpact(t, y, p, true);

  return { samples, impact, params: p };
}

function buildImpact(t, y, p, missed = false) {
  const { launch, projectile, sim } = p;
  const r = eciToEcef(t, [y[0], y[1], y[2]]);
  const v = [y[3], y[4], y[5]];
  const geo = ecefToGeodetic(r[0], r[1], r[2]);
  const th = terrainHeightM(geo.lat, geo.lon, sim.terrain);
  const w = omegaCrossR([y[0], y[1], y[2]]);
  const vg = [v[0] - w[0], v[1] - w[1], v[2] - w[2]];
  const speed = Math.hypot(vg[0], vg[1], vg[2]);
  const enu = ecefToEnu(geo.lat, geo.lon, vg);
  const impactAngle = Math.asin(Math.min(1, Math.max(-1, -enu[2] / (speed || 1)))) / DEG;
  const m = massAtImpact(t, p);
  const ke = 0.5 * m * speed * speed;
  const az = launch ? p.launcher.azimuth * DEG : 0;
  const dEcef = [
    r[0] - geodeticToEcef(launch.lat, launch.lon, launch.alt + terrainHeightM(launch.lat, launch.lon, sim.terrain))[0],
    r[1] - geodeticToEcef(launch.lat, launch.lon, launch.alt + terrainHeightM(launch.lat, launch.lon, sim.terrain))[1],
    r[2] - geodeticToEcef(launch.lat, launch.lon, launch.alt + terrainHeightM(launch.lat, launch.lon, sim.terrain))[2],
  ];
  const enuL = ecefToEnu(launch.lat, launch.lon, dEcef);
  const downrangeKm = (enuL[0] * Math.sin(az) + enuL[1] * Math.cos(az)) / 1000;
  const crossrangeKm = (enuL[0] * Math.cos(az) - enuL[1] * Math.sin(az)) / 1000;
  return {
    t, lat: geo.lat, lon: geo.lon, h: geo.h - th, ecef: r,
    speedGround: speed, speedInertial: Math.hypot(v[0], v[1], v[2]),
    impactAngleDeg: impactAngle, mass: m, keJ: ke, keMJ: ke / 1e6,
    momentum: m * speed,
    forceAvgN: ke / (sim.stopDistanceM || 1),
    rangeKm: greatCircleKm(launch.lat, launch.lon, geo.lat, geo.lon),
    downrangeKm, crossrangeKm, missed,
  };
}

function massAtImpact(t, p) {
  if (p.launcher.type !== 'rocket') return p.projectile.mass;
  const stages = buildStages(p.launcher);
  let m = p.projectile.mass, acc = 0;
  for (const s of stages) {
    if (t <= acc) break;
    const dt = Math.min(t, acc + s.burn) - acc;
    m -= s.mdot * dt;
    if (t >= acc + s.burn) m -= s.dropMass;
    acc += s.burn;
  }
  return Math.max(m, 1e-3);
}

function buildStages(launcher) {
  if (launcher.type !== 'rocket' || !launcher.stages || !launcher.stages.length) return [];
  return launcher.stages.map((s) => {
    const prop = s.propellantMass || 0;
    const burn = s.burnTime != null ? s.burnTime : (prop * (s.isp || 200) * ATM.g0) / Math.max(s.thrust, 1);
    return { thrust: s.thrust || 0, burn, mdot: burn > 0 ? prop / burn : 0, dropMass: s.dropMass || 0, isp: s.isp || 0 };
  });
}

export function summarize(result) {
  const { samples, impact } = result;
  let apogee = { h: -Infinity, t: 0, lat: 0, lon: 0 };
  let qMax = 0, qMaxT = 0, machMax = 0, machMaxT = 0;
  for (const s of samples) {
    if (s.h > apogee.h) apogee = { h: s.h, t: s.t, lat: s.lat, lon: s.lon };
    if (s.q > qMax) { qMax = s.q; qMaxT = s.t; }
    if (s.mach > machMax) { machMax = s.mach; machMaxT = s.t; }
  }
  return {
    flightTimeS: impact.t,
    apogee: { hKm: apogee.h / 1000, tS: apogee.t, lat: apogee.lat, lon: apogee.lon },
    rangeKm: impact.rangeKm,
    downrangeKm: impact.downrangeKm,
    crossrangeKm: impact.crossrangeKm,
    impactLat: impact.lat, impactLon: impact.lon,
    impactSpeed: impact.speedGround, impactMach: impact.speedGround / atmosphere(Math.max(impact.h, 0)).a,
    impactAngleDeg: impact.impactAngleDeg,
    keMJ: impact.keMJ, momentumKNs: impact.momentum / 1000,
    forceAvgMN: impact.forceAvgN / 1e6,
    qMaxKPa: qMax / 1000, qMaxT, machMax, machMaxT,
    impactH: impact.h, missed: impact.missed,
  };
}

// ------------------------------------------------------- Monte Carlo -------
export function monteCarlo(baseParams, opts = {}) {
  const n = opts.samples || 50;
  const seed = opts.seed != null ? opts.seed : 1337;
  const rand = mulberry32(seed);
  const sig = Object.assign(
    { muzzleVelocity: 0.01, elevationDeg: 0.05, azimuthDeg: 0.05, cdScale: 0.05, mass: 0.01, windSpeed: 1.0, windDirDeg: 3.0 },
    opts.sigmas || {}
  );
  const impacts = [];
  for (let i = 0; i < n; i++) {
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
    const res = simulate(p);
    impacts.push({ down: res.impact.downrangeKm, cross: res.impact.crossrangeKm, lat: res.impact.lat, lon: res.impact.lon, rangeKm: res.impact.rangeKm });
  }
  const downs = impacts.map((d) => d.down), crosses = impacts.map((d) => d.cross);
  const mean = (a) => a.reduce((s, x) => s + x, 0) / a.length;
  const std = (a, m) => Math.sqrt(a.reduce((s, x) => s + (x - m) * (x - m), 0) / Math.max(1, a.length - 1));
  const md = mean(downs), mc = mean(crosses);
  const sd = std(downs, md), sc = std(crosses, mc);
  // CEP approximation for bivariate normal: 0.5887*(sigma_major+sigma_minor) -> use per-axis
  const cepKm = 0.5887 * (sd + sc);
  const r95Km = 2.4477 * Math.max(sd, sc);
  return {
    n, seed, impacts,
    meanDownrangeKm: md, meanCrossrangeKm: mc,
    stdDownrangeKm: sd, stdCrossrangeKm: sc,
    cepKm, r95Km,
  };
}

// -------------------------------------------------------- param defaults ---
export function defaultParams() {
  return {
    launch: { lat: 32.7, lon: -117.1, alt: 120 },
    launcher: {
      type: 'cannon',
      mode: 'velocity',
      muzzleVelocity: 800, force: null, barrelLength: 8,
      elevation: 45, azimuth: 45,
      stages: [],
    },
    projectile: {
      preset: 'shell155',
      mass: 43.2, diameter: 0.155, length: 0.62,
      area: (Math.PI * 0.155 * 0.155) / 4,
      cdTable: [
        [0, 0.28], [0.7, 0.29], [0.85, 0.34], [0.95, 0.45], [1.05, 0.6],
        [1.2, 0.63], [1.5, 0.58], [2, 0.48], [3, 0.38], [4, 0.32], [5, 0.28],
      ],
      cdScale: 1,
    },
    env: {
      tempOffsetK: 0, pressureRatio: 1,
      windSpeed: 5, windDirFrom: 270, windShearExponent: 0.14,
    },
    sim: {
      dt: 0.01, maxTime: 1500, recordEvery: 0.25,
      terrain: 'ellipsoid', stopDistanceM: 1.0, noAtmosphere: false,
    },
  };
}

export function normalizeParams(p) {
  const d = defaultParams();
  const out = JSON.parse(JSON.stringify(d));
  const merge = (dst, src) => {
    for (const k of Object.keys(src || {})) {
      if (src[k] && typeof src[k] === 'object' && !Array.isArray(src[k]) && typeof dst[k] === 'object') merge(dst[k], src[k]);
      else dst[k] = src[k];
    }
  };
  merge(out, p);
  if (out.launcher.type === 'cannon' && out.launcher.mode === 'force') {
    if (out.launcher.force == null) out.launcher.force = 1e6;
    out.launcher.muzzleVelocity = Math.sqrt((2 * out.launcher.force * out.launcher.barrelLength) / out.projectile.mass);
  }
  return out;
}
