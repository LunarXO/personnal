import { defaultParams, simulate, summarize, monteCarlo } from './physics.js';
import { EarthScene } from './earth3d.js';
import { View } from './scene3d.js';
import { Chart } from './charts.js';
import { initUI, renderResults } from './ui.js';
import { buildPresetShape, normalizeForPreview } from './models.js';
import { PROJECTILE_PRESETS } from './presets.js';
import { mat4FromPosRotY, v3 } from './glmath.js';

const state = {
  params: defaultParams(),
  result: null,
  summary: null,
  mc: null,
  playing: false,
  playT: 0,
  speed: 16,
  model: null,
};

// ------------------------------------------------------------ 3D scene ----
const scene = new EarthScene(document.getElementById('scene'));

// ------------------------------------------------------------- charts -----
const mapSamples = (s) => s.map((p) => ({ ...p, altKm: p.h / 1000, qKPa: p.q / 1000 }));
const chartAlt = new Chart(document.getElementById('chartAlt'), [
  { key: 'altKm', label: 'altitude (km)', color: '#2ee66b', axis: 'l' },
  { key: 'rangeKm', label: 'range (km)', color: '#ffd24a', axis: 'r' },
], { title: 'Altitude & range vs time' });
const chartSpeed = new Chart(document.getElementById('chartSpeed'), [
  { key: 'speed', label: 'speed (m/s)', color: '#5aa2ff', axis: 'l' },
  { key: 'mach', label: 'Mach', color: '#ff8855', axis: 'r' },
], { title: 'Speed & Mach vs time' });

// ------------------------------------------------------------ preview -----
const previewCanvas = document.getElementById('preview');
const previewView = new View(previewCanvas);
previewView.camera = { pos: [1.7, 1.2, 2.1], target: [0, 0, 0], up: [0, 1, 0], fov: 40, near: 0.01, far: 100 };
previewView.sunDir = v3.norm([2, 3, 4]);
let previewDrawable = null;
let previewAngle = 0;
const previewWrap = document.getElementById('previewWrap');
const previewLabel = document.getElementById('previewLabel');

function refreshPreview() {
  if (previewDrawable) previewView.remove(previewDrawable);
  let src;
  if (state.params.projectile.preset === 'custom' && state.model) src = state.model.object;
  else {
    const pp = PROJECTILE_PRESETS[state.params.projectile.preset] || PROJECTILE_PRESETS.custom;
    src = buildPresetShape(pp.shape, pp.color);
  }
  const geo = normalizeForPreview(src.geo, 1.4);
  previewDrawable = previewView.add({ geo, shader: 'lit', color: src.color });
  const p = state.params.projectile;
  previewLabel.textContent = `${state.params._preset || p.preset} · ${p.mass} kg · A=${p.area.toFixed(4)} m² · Ø${p.diameter} m × L${p.length} m`;
}

function renderPreview(dt) {
  if (previewWrap.classList.contains('hidden')) return;
  previewAngle += dt * 0.9;
  if (previewDrawable) previewDrawable.model = mat4FromPosRotY([0, 0, 0], previewAngle);
  previewView.render();
}

// ---------------------------------------------------------------- UI -------
let runTimer = null;
function scheduleRun() {
  clearTimeout(runTimer);
  runTimer = setTimeout(run, 350);
}

const ui = initUI(state, {
  changed(path, structural) {
    if (path === 'launcher.azimuth' || path === 'launcher.elevation' || path === 'cdTable') refreshAimOverlay();
    scheduleRun();
  },
  structural(path) {
    if (path === 'launcher.type') {
      const L = state.params.launcher;
      if (L.type === 'rocket' && (L.muzzleVelocity == null || L.muzzleVelocity > 50)) L.muzzleVelocity = 5;
      if (L.type !== 'rocket' && L.mode === 'velocity' && (L.muzzleVelocity == null || L.muzzleVelocity < 1)) L.muzzleVelocity = 800;
      if (!L.stages || !L.stages.length) L.stages = [{ thrust: 20000, propellantMass: 10, isp: 250, dropMass: 0 }];
    }
    ui.sync();
    refreshAimOverlay();
    scheduleRun();
  },
  scenarioApplied() {
    ui.sync();
    refreshPreview();
    const L = state.params.launch;
    scene.setLaunch(L.lat, L.lon);
    refreshAimOverlay();
    run();
  },
  modelChanged() { refreshPreview(); },
  run() { run(); },
  toast(msg) { ui.setRunStatus(msg); },
  exportJson() {
    if (!state.result) return;
    const data = {
      app: 'ballista-3d', version: 1, exportedAt: new Date().toISOString(),
      params: state.params, summary: state.summary, impact: state.result.impact,
      mc: state.mc ? { n: state.mc.n, cepKm: state.mc.cepKm, r95Km: state.mc.r95Km, meanDownrangeKm: state.mc.meanDownrangeKm, stdDownrangeKm: state.mc.stdDownrangeKm, stdCrossrangeKm: state.mc.stdCrossrangeKm } : null,
    };
    downloadBlob('ballista3d-result.json', JSON.stringify(data, null, 2), 'application/json');
  },
  exportCsv() {
    if (!state.result) return;
    const rows = ['t_s,lat_deg,lon_deg,alt_m,speed_mps,mach,dyn_pressure_pa,range_km,mass_kg'];
    for (const s of state.result.samples) {
      rows.push([s.t.toFixed(3), s.lat.toFixed(6), s.lon.toFixed(6), s.h.toFixed(2), s.speed.toFixed(2), s.mach.toFixed(4), s.q.toFixed(1), s.rangeKm.toFixed(4), s.mass.toFixed(3)].join(','));
    }
    downloadBlob('ballista3d-trajectory.csv', rows.join('\n'), 'text/csv');
  },
});

function downloadBlob(name, text, mime) {
  const a = document.createElement('a');
  a.href = URL.createObjectURL(new Blob([text], { type: mime }));
  a.download = name;
  a.click();
  setTimeout(() => URL.revokeObjectURL(a.href), 5000);
}

function refreshAimOverlay() {
  const p = state.params;
  scene.setAzimuthRay(p.launcher.azimuth, Math.max(5, Math.min(4000, state.summary ? state.summary.rangeKm : 200)));
}

// ---------------------------------------------------------------- run ------
function run() {
  const t0 = performance.now();
  const params = JSON.parse(JSON.stringify(state.params));
  delete params._preset;
  delete params._mc;
  const mcSigmas = params.sim.mcSigmas;
  const mcSamples = params.sim.mcSamples || 60;
  const mcSeed = params.sim.mcSeed || 1337;
  delete params.sim.mcSigmas; delete params.sim.mcSamples; delete params.sim.mcSeed;

  state.result = simulate(params);
  state.summary = summarize(state.result);
  let mc = null;
  if (state.params._mc) {
    const t1 = performance.now();
    mc = monteCarlo(params, { samples: mcSamples, seed: mcSeed, sigmas: mcSigmas });
    state.mc = mc;
    state.mcMs = performance.now() - t1;
  } else state.mc = null;

  const ms = performance.now() - t0;
  const L = params.launch;
  scene.setLaunch(L.lat, L.lon);
  scene.setTrajectory(state.result.samples);
  scene.setImpact(state.result.impact.lat, state.result.impact.lon);
  scene.setSurfacePath(L, state.result.impact);
  scene.setDispersion(state.mc, params.launcher.azimuth);
  refreshAimOverlay();
  renderResults(state, state.summary, state.mc);

  const mapped = mapSamples(state.result.samples);
  chartAlt.setData(mapped);
  chartSpeed.setData(mapped);

  state.playT = 0;
  state.playing = true;
  playBtn.textContent = '⏸';
  const T = state.result.impact.t;
  timeSlider.max = Math.max(1, Math.ceil(T * 10));
  timeSlider.value = 0;
  hud.classList.remove('hidden');
  updateHud();
  ui.setRunStatus(`ran in ${ms.toFixed(0)} ms · ${state.result.samples.length} samples` + (state.mc ? ` · MC ${state.mcMs.toFixed(0)} ms` : ''));
}

// ------------------------------------------------------------- HUD etc -----
const hud = document.getElementById('hud');
const playBtn = document.getElementById('playBtn');
const timeSlider = document.getElementById('timeSlider');
const timeLabel = document.getElementById('timeLabel');

function fmtAlt(h) { return h >= 1000 ? (h / 1000).toFixed(2) + ' km' : h.toFixed(1) + ' m'; }
function updateHud() {
  const st = scene.sampleAt(state.playT);
  if (!st) return;
  const s = st.sample;
  document.getElementById('hudT').textContent = s.t.toFixed(1) + ' s';
  document.getElementById('hudAlt').textContent = fmtAlt(s.h);
  document.getElementById('hudSpeed').textContent = s.speed.toFixed(1) + ' m/s';
  document.getElementById('hudMach').textContent = s.mach.toFixed(2);
  document.getElementById('hudQ').textContent = (s.q / 1000).toFixed(1) + ' kPa';
  document.getElementById('hudRange').textContent = s.rangeKm.toFixed(2) + ' km';
  document.getElementById('hudMass').textContent = s.mass >= 1 ? s.mass.toFixed(2) + ' kg' : (s.mass * 1000).toFixed(1) + ' g';
  document.getElementById('hudPos').textContent = s.lat.toFixed(3) + '°, ' + s.lon.toFixed(3) + '°';
  timeLabel.textContent = `${state.playT.toFixed(1)} / ${(state.result ? state.result.impact.t : 0).toFixed(1)} s`;
}

playBtn.addEventListener('click', () => {
  if (!state.result) return;
  if (!state.playing && state.playT >= state.result.impact.t - 1e-6) state.playT = 0;
  state.playing = !state.playing;
  playBtn.textContent = state.playing ? '⏸' : '▶';
});
timeSlider.addEventListener('input', () => {
  state.playT = parseFloat(timeSlider.value) / 10;
  state.playing = false;
  playBtn.textContent = '▶';
  scene.setPlayback(state.playT);
  chartAlt.setPlayhead(state.playT);
  chartSpeed.setPlayhead(state.playT);
  updateHud();
});
document.getElementById('speedSelect').addEventListener('change', (e) => { state.speed = parseFloat(e.target.value); });
document.getElementById('followChk').addEventListener('change', (e) => scene.setFollow(e.target.checked));
document.getElementById('camGlobalBtn').addEventListener('click', () => {
  scene.view.setCamera([9000, 6000, 12000], [0, 0, 0]);
});
document.getElementById('camLaunchBtn').addEventListener('click', () => {
  const L = state.params.launch;
  scene.focusOn(L.lat, L.lon, 5000);
});
document.getElementById('camImpactBtn').addEventListener('click', () => {
  if (state.result) scene.focusOn(state.result.impact.lat, state.result.impact.lon, 900);
});

// globe click -> launch site (View detects taps, EarthScene raycasts the ellipsoid)
scene.onTapLaunch = (lat, lon) => {
  ui.setLaunchLatLon(lat, lon);
  scheduleRun();
};

window.addEventListener('resize', () => scene.resize());

// ------------------------------------------------------------- main loop ---
let last = performance.now();
function frame(now) {
  const dt = Math.min((now - last) / 1000, 0.1);
  last = now;
  if (state.playing && state.result) {
    state.playT += dt * state.speed;
    const T = state.result.impact.t;
    if (state.playT >= T) {
      state.playT = T;
      state.playing = false;
      playBtn.textContent = '▶';
    }
    scene.setPlayback(state.playT);
    timeSlider.value = Math.round(state.playT * 10);
    chartAlt.setPlayhead(state.playT);
    chartSpeed.setPlayhead(state.playT);
    updateHud();
  }
  scene.update(dt);
  scene.render();
  renderPreview(dt);
  requestAnimationFrame(frame);
}

// ---------------------------------------------------------------- boot -----
ui.sync();
refreshPreview();
previewWrap.classList.remove('hidden');
scene.setLaunch(state.params.launch.lat, state.params.launch.lon);
scene.focusOn(state.params.launch.lat, state.params.launch.lon, 7000);
run();
requestAnimationFrame(frame);
