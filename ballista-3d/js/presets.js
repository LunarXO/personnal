// Preset projectiles, launchers and Cd(Mach) curves.
// Cd values are drag coefficients referenced to the projectile reference area.

export const CD_TABLES = {
  sphere: [
    [0, 0.47], [0.5, 0.47], [0.7, 0.5], [0.85, 0.65], [0.95, 0.85], [1.05, 0.98],
    [1.2, 1.0], [1.5, 0.95], [2, 0.9], [3, 0.82], [4, 0.76], [5, 0.72],
  ],
  shell: [
    [0, 0.28], [0.7, 0.29], [0.85, 0.34], [0.95, 0.45], [1.05, 0.6],
    [1.2, 0.63], [1.5, 0.58], [2, 0.48], [3, 0.38], [4, 0.32], [5, 0.28],
  ],
  blunt: [
    [0, 0.4], [0.7, 0.42], [0.9, 0.55], [1.0, 0.75], [1.2, 0.85], [1.5, 0.8], [2, 0.7], [3, 0.6], [5, 0.5],
  ],
  rocket: [
    [0, 0.22], [0.8, 0.24], [1.0, 0.4], [1.2, 0.5], [1.5, 0.46], [2, 0.4], [3, 0.32], [5, 0.26],
  ],
  golf: [
    [0, 0.24], [0.7, 0.25], [0.9, 0.35], [1.0, 0.55], [1.2, 0.6], [1.5, 0.5], [2, 0.42], [3, 0.35], [5, 0.3],
  ],
};

export const PROJECTILE_PRESETS = {
  shell155: {
    name: '155 mm artillery shell (M107-like)', mass: 43.2, diameter: 0.155, length: 0.62,
    area: (Math.PI * 0.155 ** 2) / 4, cdTable: CD_TABLES.shell, color: 0x6b7a3f, shape: 'shell',
  },
  shell406: {
    name: '16 in naval shell (AP, ~1225 kg)', mass: 1225, diameter: 0.406, length: 1.83,
    area: (Math.PI * 0.406 ** 2) / 4, cdTable: CD_TABLES.shell, color: 0x4a4f55, shape: 'shell',
  },
  cannonball: {
    name: 'Iron cannonball 250 mm', mass: 63.8, diameter: 0.25, length: 0.25,
    area: (Math.PI * 0.25 ** 2) / 4, cdTable: CD_TABLES.sphere, color: 0x2f3338, shape: 'sphere',
  },
  mortar81: {
    name: '81 mm mortar bomb', mass: 3.4, diameter: 0.081, length: 0.34,
    area: (Math.PI * 0.081 ** 2) / 4, cdTable: CD_TABLES.blunt, color: 0x5a6b4a, shape: 'mortar',
  },
  golfball: {
    name: 'Golf ball', mass: 0.04593, diameter: 0.04267, length: 0.04267,
    area: (Math.PI * 0.04267 ** 2) / 4, cdTable: CD_TABLES.golf, color: 0xf2f2f2, shape: 'sphere',
  },
  arrow: {
    name: 'Archery arrow', mass: 0.045, diameter: 0.008, length: 0.76,
    area: (Math.PI * 0.008 ** 2) / 4, cdTable: CD_TABLES.blunt, color: 0x8a6d3b, shape: 'arrow',
  },
  modelrocket: {
    name: 'Model rocket (Estes-class)', mass: 0.25, diameter: 0.051, length: 0.45,
    area: (Math.PI * 0.051 ** 2) / 4, cdTable: CD_TABLES.rocket, color: 0xc0392b, shape: 'rocket',
  },
  v2: {
    name: 'V-2 ballistic missile (A-4)', mass: 12900, diameter: 1.65, length: 14.0,
    area: (Math.PI * 1.65 ** 2) / 4, cdTable: CD_TABLES.rocket, color: 0x7f8c8d, shape: 'rocket',
  },
  custom: { name: 'Custom / uploaded model', mass: 10, diameter: 0.2, length: 1, area: 0.0314, cdTable: CD_TABLES.shell, color: 0x3498db, shape: 'custom' },
};

// Launcher + projectile + typical firing solution combos
export const SCENARIO_PRESETS = {
  howitzer155: {
    label: '155 mm howitzer (max charge)',
    projectile: 'shell155',
    launcher: { type: 'cannon', mode: 'velocity', muzzleVelocity: 827, elevation: 45, azimuth: 90 },
  },
  naval16: {
    label: '16" naval gun (force mode)',
    projectile: 'shell406',
    launcher: { type: 'cannon', mode: 'force', force: 2.2e7, barrelLength: 20, elevation: 15, azimuth: 60 },
  },
  mortar: {
    label: '81 mm mortar',
    projectile: 'mortar81',
    launcher: { type: 'cannon', mode: 'velocity', muzzleVelocity: 310, elevation: 45, azimuth: 210 },
  },
  cannon: {
    label: 'Cannon (cannonball)',
    projectile: 'cannonball',
    launcher: { type: 'cannon', mode: 'velocity', muzzleVelocity: 150, elevation: 45, azimuth: 45 },
  },
  golf: {
    label: 'Golf drive',
    projectile: 'golfball',
    launcher: { type: 'cannon', mode: 'velocity', muzzleVelocity: 72, elevation: 12, azimuth: 0 },
  },
  v2: {
    label: 'V-2 ballistic missile',
    projectile: 'v2',
    launcher: {
      type: 'rocket', mode: 'velocity', muzzleVelocity: 2, elevation: 82, azimuth: 90,
      stages: [{ thrust: 245000, propellantMass: 8800, isp: 210, dropMass: 0 }],
    },
  },
  sounding: {
    label: '2-stage sounding rocket',
    projectile: 'modelrocket',
    launcher: {
      type: 'rocket', mode: 'velocity', muzzleVelocity: 5, elevation: 88, azimuth: 0,
      stages: [
        { thrust: 30000, propellantMass: 40, isp: 260, dropMass: 10 },
        { thrust: 8000, propellantMass: 10, isp: 280, dropMass: 0 },
      ],
    },
    projectileMassOverride: 80, projectileAreaOverride: 0.02, projectileDiameter: 0.16,
  },
};
