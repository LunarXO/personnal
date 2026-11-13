import { PROJECTILE_PRESETS, SCENARIO_PRESETS } from './presets.js';
import { loadModelFile } from './models.js';

export function getPath(obj, path) {
  return path.split('.').reduce((o, k) => (o == null ? o : o[k]), obj);
}
export function setPath(obj, path, v) {
  const ks = path.split('.');
  let o = obj;
  for (let i = 0; i < ks.length - 1; i++) o = o[ks[i]];
  o[ks[ks.length - 1]] = v;
}

const FIELDS = {
  'fields-launch': [
    { path: 'launch.lat', label: 'Latitude', type: 'num', min: -90, max: 90, step: 0.0001, unit: '°' },
    { path: 'launch.lon', label: 'Longitude', type: 'num', min: -180, max: 180, step: 0.0001, unit: '°' },
    { path: 'launch.alt', label: 'Site altitude', type: 'num', min: -500, max: 9000, step: 10, unit: 'm' },
    { path: 'launcher.azimuth', label: 'Azimuth (cw from N)', type: 'range', min: 0, max: 359.9, step: 0.1, unit: '°' },
    { path: 'launcher.elevation', label: 'Elevation', type: 'range', min: -5, max: 90, step: 0.1, unit: '°' },
  ],
  'fields-launcher': [
    { path: 'launcher.type', label: 'Launcher type', type: 'select', options: [['cannon', 'Cannon / gun'], ['mortar', 'Mortar'], ['rocket', 'Rocket / missile']] },
    { path: 'launcher.mode', label: 'Input mode', type: 'select', options: [['velocity', 'Muzzle velocity'], ['force', 'Force × barrel length']], showIf: (p) => p.launcher.type !== 'rocket' },
    { path: 'launcher.muzzleVelocity', label: 'Muzzle velocity', type: 'num', min: 0, max: 12000, step: 1, unit: 'm/s', showIf: (p) => p.launcher.type === 'rocket' || p.launcher.mode === 'velocity' },
    { path: 'launcher.force', label: 'Launch force', type: 'num', min: 0, max: 1e9, step: 1000, unit: 'N', showIf: (p) => p.launcher.type !== 'rocket' && p.launcher.mode === 'force' },
    { path: 'launcher.barrelLength', label: 'Barrel length', type: 'num', min: 0.1, max: 100, step: 0.1, unit: 'm', showIf: (p) => p.launcher.type !== 'rocket' && p.launcher.mode === 'force' },
  ],
  'fields-projectile': [
    { path: '_preset', label: 'Shape preset', type: 'select', options: Object.entries(PROJECTILE_PRESETS).map(([k, v]) => [k, v.name]) },
    { path: 'projectile.mass', label: 'Mass', type: 'num', min: 0.0001, max: 1e7, step: 'any', unit: 'kg' },
    { path: 'projectile.diameter', label: 'Diameter', type: 'num', min: 0.001, max: 50, step: 'any', unit: 'm' },
    { path: 'projectile.length', label: 'Length', type: 'num', min: 0.001, max: 200, step: 'any', unit: 'm' },
    { path: 'projectile.area', label: 'Drag ref. area', type: 'num', min: 0, max: 100, step: 'any', unit: 'm²' },
    { path: 'projectile.cdScale', label: 'Cd multiplier', type: 'num', min: 0, max: 5, step: 0.01, unit: '×' },
  ],
  'fields-env': [
    { path: 'env.tempOffsetK', label: 'ISA temp offset', type: 'num', min: -50, max: 50, step: 0.5, unit: 'K' },
    { path: 'env.pressureRatio', label: 'Pressure ratio', type: 'num', min: 0.5, max: 1.2, step: 0.005, unit: '×' },
    { path: 'env.windSpeed', label: 'Wind speed (10 m)', type: 'num', min: 0, max: 80, step: 0.5, unit: 'm/s' },
    { path: 'env.windDirFrom', label: 'Wind FROM dir', type: 'range', min: 0, max: 359.9, step: 1, unit: '°' },
    { path: 'env.windShearExponent', label: 'Wind shear exponent', type: 'num', min: 0, max: 0.6, step: 0.01, unit: '' },
    { path: 'sim.terrain', label: 'Terrain', type: 'select', options: [['ellipsoid', 'WGS84 ellipsoid (smooth)'], ['hills', 'Procedural hills (demo)']] },
    { path: 'sim.noAtmosphere', label: 'Vacuum (no drag)', type: 'checkbox' },
  ],
  'fields-sim': [
    { path: 'sim.dt', label: 'Integrator step', type: 'num', min: 0.001, max: 0.1, step: 0.001, unit: 's' },
    { path: 'sim.maxTime', label: 'Max flight time', type: 'num', min: 10, max: 7200, step: 10, unit: 's' },
    { path: 'sim.recordEvery', label: 'Sample every', type: 'num', min: 0.05, max: 10, step: 0.05, unit: 's' },
    { path: 'sim.stopDistanceM', label: 'Impact stop dist.', type: 'num', min: 0.01, max: 100, step: 0.1, unit: 'm' },
    { path: '_mc', label: 'Monte Carlo dispersion', type: 'checkbox' },
    { path: 'sim.mcSamples', label: 'MC samples', type: 'num', min: 5, max: 500, step: 5, unit: '', showIf: (p) => p._mc },
    { path: 'sim.mcSeed', label: 'MC seed', type: 'num', min: 0, max: 1e9, step: 1, unit: '', showIf: (p) => p._mc },
  ],
};

const SIGMA_FIELDS = [
  ['muzzleVelocity', 'σ muzzle vel (rel)'],
  ['elevationDeg', 'σ elevation (°)'],
  ['azimuthDeg', 'σ azimuth (°)'],
  ['cdScale', 'σ Cd (rel)'],
  ['mass', 'σ mass (rel)'],
  ['windSpeed', 'σ wind (m/s)'],
  ['windDirDeg', 'σ wind dir (°)'],
];

export function initUI(state, actions) {
  state.ui = state.ui || {};
  state.params._preset = 'shell155';
  state.params._mc = true;
  state.params.sim.mcSamples = 60;
  state.params.sim.mcSeed = 1337;
  state.params.sim.mcSigmas = {
    muzzleVelocity: 0.01, elevationDeg: 0.05, azimuthDeg: 0.05, cdScale: 0.05,
    mass: 0.01, windSpeed: 1.0, windDirDeg: 3.0,
  };

  const inputs = {}; // path -> [elements]

  function makeField(container, spec) {
    const row = document.createElement('div');
    row.className = 'field';
    row.dataset.path = spec.path;
    const lab = document.createElement('label');
    lab.textContent = spec.label;
    const val = document.createElement('div');
    val.className = 'val';
    let el;
    if (spec.type === 'range') {
      el = document.createElement('input');
      el.type = 'range'; el.min = spec.min; el.max = spec.max; el.step = spec.step;
      const num = document.createElement('input');
      num.type = 'number'; num.min = spec.min; num.max = spec.max; num.step = spec.step || 'any';
      num.addEventListener('input', () => { setPath(state.params, spec.path, parseFloat(num.value)); actions.changed(spec.path, true); });
      el.addEventListener('input', () => { num.value = el.value; setPath(state.params, spec.path, parseFloat(el.value)); actions.changed(spec.path, true); });
      val.appendChild(el); val.appendChild(num);
      (inputs[spec.path] = inputs[spec.path] || []).push(el, num);
    } else if (spec.type === 'num') {
      el = document.createElement('input');
      el.type = 'number'; el.min = spec.min; el.max = spec.max; el.step = spec.step;
      el.addEventListener('input', () => { const v = parseFloat(el.value); if (Number.isFinite(v)) { setPath(state.params, spec.path, v); actions.changed(spec.path, spec.path === 'projectile.cdTable'); } });
      val.appendChild(el);
      (inputs[spec.path] = inputs[spec.path] || []).push(el);
    } else if (spec.type === 'select') {
      el = document.createElement('select');
      for (const [v, label] of spec.options) {
        const o = document.createElement('option');
        o.value = v; o.textContent = label;
        el.appendChild(o);
      }
      el.addEventListener('change', () => {
        const raw = getPath(state.params, spec.path);
        const isNum = spec.options.every(([v]) => !isNaN(parseFloat(v)) && isFinite(v));
        setPath(state.params, spec.path, isNum ? parseFloat(el.value) : el.value);
        if (raw !== getPath(state.params, spec.path)) actions.structural(spec.path);
        actions.changed(spec.path, true);
      });
      val.appendChild(el);
      (inputs[spec.path] = inputs[spec.path] || []).push(el);
    } else if (spec.type === 'checkbox') {
      el = document.createElement('input');
      el.type = 'checkbox';
      el.addEventListener('change', () => { setPath(state.params, spec.path, el.checked); actions.structural(spec.path); actions.changed(spec.path, true); });
      val.appendChild(el);
      (inputs[spec.path] = inputs[spec.path] || []).push(el);
    }
    if (spec.unit) {
      const u = document.createElement('span');
      u.className = 'unit'; u.textContent = spec.unit;
      val.appendChild(u);
    }
    row.appendChild(lab); row.appendChild(val);
    container.appendChild(row);
    row._spec = spec;
    return row;
  }

  const rows = [];
  for (const [containerId, specs] of Object.entries(FIELDS)) {
    const c = document.getElementById(containerId);
    for (const spec of specs) rows.push(makeField(c, spec));
  }

  // sigma grid
  const sigGrid = document.getElementById('sigGrid');
  const sigInputs = {};
  for (const [key, label] of SIGMA_FIELDS) {
    const lab = document.createElement('label');
    lab.textContent = label;
    const inp = document.createElement('input');
    inp.type = 'number'; inp.step = 'any'; inp.min = 0;
    inp.addEventListener('input', () => {
      const v = parseFloat(inp.value);
      if (Number.isFinite(v)) { state.params.sim.mcSigmas[key] = v; actions.changed('mcSigmas', false); }
    });
    lab.appendChild(inp);
    sigGrid.appendChild(lab);
    sigInputs[key] = inp;
  }

  // Cd table textarea
  const cdInput = document.getElementById('cdTableInput');
  cdInput.addEventListener('input', () => {
    const table = [];
    for (const line of cdInput.value.split('\n')) {
      const parts = line.split(/[,\s]+/).filter(Boolean);
      if (parts.length >= 2) {
        const m = parseFloat(parts[0]), c = parseFloat(parts[1]);
        if (Number.isFinite(m) && Number.isFinite(c)) table.push([m, c]);
      }
    }
    if (table.length >= 2) { table.sort((a, b) => a[0] - b[0]); state.params.projectile.cdTable = table; actions.changed('cdTable', false); }
  });

  // scenario select
  const scenarioSelect = document.getElementById('scenarioSelect');
  {
    const blank = document.createElement('option');
    blank.value = ''; blank.textContent = '— scenario presets —';
    scenarioSelect.appendChild(blank);
    for (const [k, v] of Object.entries(SCENARIO_PRESETS)) {
      const o = document.createElement('option');
      o.value = k; o.textContent = v.label;
      scenarioSelect.appendChild(o);
    }
  }
  scenarioSelect.addEventListener('change', () => {
    if (!scenarioSelect.value) return;
    applyScenario(state.params, scenarioSelect.value);
    actions.scenarioApplied(scenarioSelect.value);
    scenarioSelect.value = '';
  });

  // model upload
  const modelFile = document.getElementById('modelFile');
  const modelInfo = document.getElementById('modelInfo');
  modelFile.addEventListener('change', async () => {
    const f = modelFile.files[0];
    if (!f) return;
    modelInfo.textContent = 'Loading ' + f.name + ' …';
    try {
      const res = await loadModelFile(f, state.params.projectile.length || 1);
      state.model = res;
      state.params.projectile.preset = 'custom';
      modelInfo.textContent =
        `${f.name}: volume ${res.stats.volume.toFixed(4)} m³, surface ${res.stats.surfaceArea.toFixed(3)} m², ` +
        `silhouette area ${res.refArea.toFixed(4)} m², scaled size ${res.scaledSize.map((v) => v.toFixed(3)).join(' × ')} m`;
      actions.modelChanged(res);
      actions.changed('model', true);
    } catch (e) {
      modelInfo.textContent = 'Failed to load: ' + e.message;
    }
  });

  document.getElementById('autoMassBtn').addEventListener('click', () => {
    if (!state.model) { modelInfo.textContent = 'Load a model first.'; return; }
    const rho = parseFloat(document.getElementById('densityInput').value) || 7800;
    state.params.projectile.mass = state.model.stats.volume * rho;
    actions.changed('projectile.mass', true);
  });
  document.getElementById('autoAreaBtn').addEventListener('click', () => {
    if (!state.model) { modelInfo.textContent = 'Load a model first.'; return; }
    state.params.projectile.area = state.model.refArea;
    actions.changed('projectile.area', true);
  });

  // stages UI
  const stagesWrap = document.getElementById('stagesWrap');
  const addStageBtn = document.getElementById('addStageBtn');
  addStageBtn.addEventListener('click', () => {
    const st = state.params.launcher.stages;
    if (st.length >= 3) return;
    st.push({ thrust: 5000, propellantMass: 5, isp: 250, dropMass: 0 });
    buildStages();
    actions.changed('stages', true);
  });

  function buildStages() {
    stagesWrap.innerHTML = '';
    const isRocket = state.params.launcher.type === 'rocket';
    addStageBtn.classList.toggle('hidden', !isRocket);
    if (!isRocket) return;
    const stages = state.params.launcher.stages;
    stages.forEach((s, i) => {
      const card = document.createElement('div');
      card.className = 'stage-card';
      const h = document.createElement('h3');
      h.textContent = `Stage ${i + 1}`;
      const del = document.createElement('button');
      del.textContent = '✕';
      del.addEventListener('click', () => { stages.splice(i, 1); buildStages(); actions.changed('stages', true); });
      h.appendChild(del);
      card.appendChild(h);
      const fields = document.createElement('div');
      fields.className = 'fields';
      card.appendChild(fields);
      const defs = [
        ['thrust', 'Thrust', 1, 1e8, 'any', 'N'],
        ['propellantMass', 'Propellant', 0, 1e7, 'any', 'kg'],
        ['isp', 'Isp', 1, 500, 'any', 's'],
        ['dropMass', 'Dry mass dropped', 0, 1e7, 'any', 'kg'],
      ];
      for (const [key, label, min, max, step, unit] of defs) {
        const row = document.createElement('div');
        row.className = 'field';
        const lab = document.createElement('label'); lab.textContent = label;
        const val = document.createElement('div'); val.className = 'val';
        const inp = document.createElement('input');
        inp.type = 'number'; inp.min = min; inp.max = max; inp.step = step; inp.value = s[key];
        inp.addEventListener('input', () => { const v = parseFloat(inp.value); if (Number.isFinite(v)) { s[key] = v; burn.textContent = burnText(); actions.changed('stages', false); } });
        const u = document.createElement('span'); u.className = 'unit'; u.textContent = unit;
        val.appendChild(inp); val.appendChild(u);
        row.appendChild(lab); row.appendChild(val);
        fields.appendChild(row);
      }
      const burnText = () => {
        const burn = s.propellantMass * (s.isp || 1) * 9.80665 / Math.max(s.thrust || 1, 1);
        return `burn ≈ ${burn.toFixed(1)} s · mdot ≈ ${s.propellantMass / Math.max(burn, 1e-9)} kg/s`;
      };
      const burn = document.createElement('div');
      burn.className = 'burn'; burn.textContent = burnText();
      card.appendChild(burn);
      stagesWrap.appendChild(card);
    });
  }

  // save / load
  document.getElementById('saveBtn').addEventListener('click', () => {
    const name = prompt('Save scenario as:', 'my-scenario');
    if (!name) return;
    const all = JSON.parse(localStorage.getItem('ballista3d.scenarios') || '{}');
    all[name] = state.params;
    localStorage.setItem('ballista3d.scenarios', JSON.stringify(all));
    actions.toast(`Saved "${name}"`);
  });
  document.getElementById('loadBtn').addEventListener('click', () => {
    const all = JSON.parse(localStorage.getItem('ballista3d.scenarios') || '{}');
    const names = Object.keys(all);
    if (!names.length) { actions.toast('No saved scenarios'); return; }
    const name = prompt('Load scenario:\n' + names.join('\n') + '\n\nName:', names[names.length - 1]);
    if (!name || !all[name]) return;
    Object.assign(state.params, JSON.parse(JSON.stringify(all[name])));
    actions.scenarioApplied(name);
  });

  // exports
  document.getElementById('exportJsonBtn').addEventListener('click', () => actions.exportJson());
  document.getElementById('exportCsvBtn').addEventListener('click', () => actions.exportCsv());
  document.getElementById('runBtn').addEventListener('click', () => actions.run());
  document.getElementById('runBtn2').addEventListener('click', () => actions.run());

  function applyScenario(params, key) {
    const sc = SCENARIO_PRESETS[key];
    if (!sc) return;
    const pp = PROJECTILE_PRESETS[sc.projectile] || PROJECTILE_PRESETS.custom;
    params.projectile = JSON.parse(JSON.stringify(pp));
    params.projectile.preset = sc.projectile;
    params.projectile.cdScale = 1;
    if (sc.projectileMassOverride) params.projectile.mass = sc.projectileMassOverride;
    if (sc.projectileAreaOverride) params.projectile.area = sc.projectileAreaOverride;
    if (sc.projectileDiameter) params.projectile.diameter = sc.projectileDiameter;
    const L = sc.launcher;
    params.launcher.type = L.type;
    params.launcher.mode = L.mode || 'velocity';
    params.launcher.muzzleVelocity = L.muzzleVelocity;
    if (L.force != null) params.launcher.force = L.force;
    if (L.barrelLength != null) params.launcher.barrelLength = L.barrelLength;
    params.launcher.elevation = L.elevation;
    params.launcher.azimuth = L.azimuth;
    params.launcher.stages = JSON.parse(JSON.stringify(L.stages || []));
    params._preset = sc.projectile;
  }

  function applyProjectilePreset(params, key) {
    const pp = PROJECTILE_PRESETS[key];
    if (!pp) return;
    params.projectile = JSON.parse(JSON.stringify(pp));
    params.projectile.preset = key;
    params.projectile.cdScale = 1;
    params._preset = key;
  }

  // sync all inputs from state
  function sync() {
    for (const row of rows) {
      const spec = row._spec;
      if (spec.showIf && !spec.showIf(state.params)) { row.style.display = 'none'; continue; }
      row.style.display = '';
      const v = spec.path === '_preset' ? state.params._preset : spec.path === '_mc' ? state.params._mc : getPath(state.params, spec.path);
      for (const el of inputs[spec.path] || []) {
        if (el.type === 'checkbox') el.checked = !!v;
        else el.value = v;
      }
    }
    for (const [key, inp] of Object.entries(sigInputs)) inp.value = state.params.sim.mcSigmas[key];
    cdInput.value = state.params.projectile.cdTable.map(([m, c]) => `${m}, ${c}`).join('\n');
    buildStages();
  }

  // wire preset select change
  const presetInput = inputs['_preset'][0];
  presetInput.addEventListener('change', () => {
    applyProjectilePreset(state.params, presetInput.value);
    actions.scenarioApplied(presetInput.value);
  });

  return {
    sync,
    buildStages,
    setLaunchLatLon(lat, lon) {
      state.params.launch.lat = lat;
      state.params.launch.lon = lon;
      sync();
    },
    setRunStatus(txt) { document.getElementById('runStatus').textContent = txt; },
    getModelInfoEl: () => modelInfo,
  };
}

// ---------------------------------------------------------------- results --
export function renderResults(state, summary, mc) {
  const grid = document.getElementById('statsGrid');
  const fmt = (v, d = 2) => (typeof v === 'number' ? v.toFixed(d) : String(v));
  const stat = (label, value, cls = '') => `<div class="stat ${cls}"><span>${label}</span><b>${value}</b></div>`;
  let html = '';
  if (!summary) {
    html = '<div class="hint">Run a simulation to see the prediction.</div>';
  } else {
    html =
      stat('Flight time', fmt(summary.flightTimeS, 1) + ' s') +
      stat('Range', fmt(summary.rangeKm, 2) + ' km', 'wide') +
      stat('Downrange', fmt(summary.downrangeKm, 3) + ' km') +
      stat('Crossrange', fmt(summary.crossrangeKm, 3) + ' km') +
      stat('Apogee', fmt(summary.apogee.hKm, 2) + ' km @ ' + fmt(summary.apogee.tS, 0) + ' s') +
      stat('Max-Q', fmt(summary.qMaxKPa, 0) + ' kPa @ ' + fmt(summary.qMaxT, 0) + ' s') +
      stat('Impact point', `${fmt(summary.impactLat, 4)}°, ${fmt(summary.impactLon, 4)}°`, 'wide') +
      stat('Impact speed', fmt(summary.impactSpeed, 0) + ' m/s') +
      stat('Impact Mach', fmt(summary.impactMach, 2)) +
      stat('Impact angle', fmt(summary.impactAngleDeg, 1) + '°') +
      stat('Max Mach', fmt(summary.machMax, 2) + ' @ ' + fmt(summary.machMaxT, 0) + ' s') +
      stat('Impact KE', fmt(summary.keMJ, 2) + ' MJ', 'wide hl') +
      stat('Momentum', fmt(summary.momentumKNs, 1) + ' kN·s') +
      stat('Avg impact force', fmt(summary.forceAvgMN, 3) + ' MN') +
      stat('Impact altitude', fmt(summary.impactH, 0) + ' m');
    if (summary.missed) html += '<div class="hint" style="color:#ff8855">⚠ did not impact within max flight time (or escaped)</div>';
  }
  grid.innerHTML = html;

  const mcCard = document.getElementById('mcCard');
  const mcStats = document.getElementById('mcStats');
  if (mc) {
    mcCard.classList.remove('hidden');
    mcStats.innerHTML =
      stat('Samples', mc.n) +
      stat('CEP (50%)', fmt(mc.cepKm, 3) + ' km', 'hl') +
      stat('95% ellipse', fmt(2.4477 * mc.stdDownrangeKm, 3) + ' × ' + fmt(2.4477 * mc.stdCrossrangeKm, 3) + ' km') +
      stat('Mean downrange', fmt(mc.meanDownrangeKm, 3) + ' km') +
      stat('σ downrange', fmt(mc.stdDownrangeKm, 3) + ' km') +
      stat('σ crossrange', fmt(mc.stdCrossrangeKm, 3) + ' km');
  } else {
    mcCard.classList.add('hidden');
  }
}
