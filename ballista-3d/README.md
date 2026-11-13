# Ballista 3D — Professional Projectile & Rocket Trajectory Simulator

A browser-based, professional-grade 3D ballistics simulator. Pick a launch point on a
3D reconstruction of the Earth, choose a launcher (cannon, mortar, rocket) and a
projectile (preset shape or your own `.glb` / `.gltf` / `.stl` model), set the
environmental factors, and get an accurate prediction with a full 3D visualization:
trajectory, landing zone (with Monte-Carlo dispersion) and landing force.

![stack](https://img.shields.io/badge/stack-custom%20WebGL%20%C3%B7%20vanilla%20ESM%20%C3%B7%20zero%20dependencies-blue)

## Run it

Any static file server works (ES modules require http, not `file://`):

```bash
cd ballista-3d
python3 -m http.server 8080        # or: npm run serve
# open http://localhost:8080
```

No build step, no runtime CDN, zero dependencies — the renderer is a compact custom
WebGL engine (`js/gl.js`, `js/scene3d.js`, `js/glmath.js`). Earth day/night/cloud textures
are generated procedurally at startup (`js/textures.js`) from an embedded compressed
land/sea mask (`js/landmask.js`, derived from a public-domain Blue Marble image); dropping
base64 part files (`assets/<name>.b64.N`) overrides them with custom imagery.

## Physics model (what makes the prediction "accurate")

Point-mass 3-DOF trajectory integrated in the **inertial (ECI) frame with RK4**
(`dt` adjustable, default 0.01 s), so Earth rotation (Coriolis/centrifugal) is
included exactly:

- **Geodesy:** WGS84 ellipsoid, geodetic↔ECEF conversions.
- **Gravity:** point-mass `μ` plus the **J2 zonal harmonic** (oblateness).
- **Atmosphere:** International Standard Atmosphere to 84.852 km (layered T/p/ρ)
  with exponential continuation above; sea-level temperature/pressure offsets;
  speed of sound → Mach.
- **Drag:** quadratic drag `½ρ·Cd(M)·A·|v_rel|·v_rel` with a **Mach-dependent Cd
  table** (editable), reference area from diameter or measured from your 3D model.
- **Wind:** meteorological "from" direction, power-law shear with altitude, advected
  with the projectile's lat/lon.
- **Launchers:** cannon/mortar via muzzle velocity **or** launch force × barrel
  length (`v = √(2FL/m)`); rockets with **multi-stage thrust schedules**
  (thrust, propellant mass, Isp, dropped dry mass per stage; burn time derived),
  thrust aligned with the relative-velocity vector.
- **Impact:** sub-step bisection to locate ground contact on the ellipsoid
  (or procedural terrain), giving impact point, speed, angle, kinetic energy,
  momentum and an estimated average impact force (`F ≈ KE / stopping distance`).
- **Uncertainty:** seeded **Monte Carlo** over input sigmas (muzzle velocity,
  elevation, azimuth, Cd, mass, wind) → landing CEP and 95% dispersion ellipse
  drawn on the globe.

Validation: `npm test` runs 60+ checks, including vacuum-range/apogee against the
analytic flat-Earth solution, specific-energy conservation of the integrator,
drag/wind/Coriolis sign checks, staging mass bookkeeping and MC reproducibility.
Sanity anchor: the V-2 preset predicts ≈ 297 km range (historical ≈ 300 km).

## Using the app

1. **Launch site:** click anywhere on the globe (or type lat/lon), set altitude.
2. **Firing solution:** azimuth + elevation sliders.
3. **Launcher:** type (cannon/mortar/rocket), velocity or force×barrel mode;
   rockets get up to 3 stages.
4. **Projectile:** pick a preset (155 mm shell, 16″ naval shell, cannonball, 81 mm
   mortar, golf ball, arrow, model rocket, V-2) **or upload your own 3D model** —
   volume, surface and silhouette (reference) area are computed from the mesh;
   "Mass from model" (× density) and "Area from model" buttons fill the inputs.
   Edit the Cd(Mach) table freely.
5. **Environment:** ISA offsets, wind, shear, terrain, vacuum toggle.
6. **Run** (auto-runs on any change). Watch the 3D trajectory playback (play/pause,
   scrub, speed, follow-cam), the HUD (alt, speed, Mach, dynamic pressure, range,
   mass), and the flight-profile charts.
7. **Prediction panel:** flight time, range/downrange/crossrange, apogee, Max-Q,
   Max Mach, impact point/speed/angle, impact KE, momentum, average impact force,
   and Monte-Carlo CEP / 95% landing ellipse.
8. **Export:** full results as JSON, trajectory samples as CSV; scenarios save/load
   in `localStorage`.

## Project layout

```
index.html            app shell
css/style.css         dark professional UI
js/physics.js         pure ESM physics core (no DOM) — unit tested in Node
js/presets.js         projectile/launcher/scenario presets + Cd tables
js/meshstats.js       mesh volume / silhouette area (pure math)
js/glmath.js          vec/mat math, ray-ellipsoid picking (pure, tested)
js/geo.js             procedural geometry generators (pure, tested)
js/loaders.js         GLB / STL parsers (pure, tested)
js/gl.js              WebGL shader/buffer/texture helpers
js/scene3d.js         WebGL renderer: camera, orbit controls, drawables, sprites
js/earth3d.js         globe scene: WGS84 ellipsoid, day/night, clouds, atmosphere,
                      graticule, markers, trajectory, dispersion, picking, playback
js/models.js          preset shapes + model upload (auto mass/area)
js/charts.js          canvas flight-profile charts
js/ui.js              panels, fields, stages, presets, save/load, results
js/main.js            state, run pipeline, playback loop, HUD, exports
js/textures.js        procedural day/night/cloud textures from the embedded land mask
js/landmask.js        embedded deflated 360x180 land/sea bitmask (base64)
test/                 Node test suites (physics, meshstats, gl/geo/loaders, textures)
```

## Accuracy notes & limitations

- Point-mass (3-DOF) model: no aerodynamic lift, Magnus/spin, or attitude dynamics —
  for rockets, thrust follows the velocity vector (no pitch program). Cd(Mach) is the
  main fidelity knob; supply a measured curve for best results.
- Terrain is the WGS84 ellipsoid by default; a procedural "hills" mode exists for
  demo. A DEM heightmap hook is `terrainHeightM()` in `js/physics.js`.
- "Average impact force" is an estimate: KE divided by the configured stopping
  distance; peak force depends on target material.

## License

MIT
