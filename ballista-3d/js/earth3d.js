// 3D globe scene on the custom WebGL engine. Same public API as the three.js version.
import { View, makeTexture, loadB64Image, canvasTexture } from './scene3d.js';
import { ellipsoid, cone, ring, mergeGeos, transformGeo, computeNormals } from './geo.js';
import { geodeticToEcef, ecefToGeodetic, WGS84 } from './physics.js';
import { mat4FromBasis, mat4FromPosRotY, rayEllipsoid, v3 } from './glmath.js';

export const A_KM = WGS84.a / 1000;
export const C_KM = (WGS84.a * (1 - WGS84.f)) / 1000;
export const R_KM = 6371.0;
const DEG = Math.PI / 180;

export function latLonToVec3(lat, lon, hKm = 0) {
  const r = geodeticToEcef(lat, lon, hKm * 1000);
  return [r[0] / 1000, r[1] / 1000, r[2] / 1000];
}

export function vec3ToLatLon(v) {
  const g = ecefToGeodetic(v[0] * 1000, v[1] * 1000, v[2] * 1000);
  return { lat: g.lat, lon: g.lon };
}

function offsetLatLon(lat, lon, eastKm, northKm) {
  return {
    lat: lat + northKm / 111.195,
    lon: lon + eastKm / (111.195 * Math.max(Math.cos(lat * DEG), 1e-6)),
  };
}

function surfaceBasis(lat, lon) {
  const up = v3.norm(latLonToVec3(lat, lon, 0));
  const lo = lon * DEG;
  const east = [-Math.sin(lo), Math.cos(lo), 0];
  const north = v3.cross(up, east);
  return { up, east, north };
}

function makeLabelSprite(view, text, color = '#ffe08a') {
  const canvas = document.createElement('canvas');
  canvas.width = 512; canvas.height = 128;
  const g = canvas.getContext('2d');
  const sprite = { center: [0, 0, 0], size: [60, 15], texture: null, opacity: 1, canvas };
  const set = (t, col = color) => {
    g.clearRect(0, 0, 512, 128);
    g.font = 'bold 44px system-ui, sans-serif';
    g.textAlign = 'center'; g.textBaseline = 'middle';
    g.shadowColor = 'rgba(0,0,0,0.9)'; g.shadowBlur = 10;
    g.fillStyle = col;
    g.fillText(t, 256, 64);
    if (sprite.texture) { view.gl.bindTexture(view.gl.TEXTURE_2D, sprite.texture); view.gl.texImage2D(view.gl.TEXTURE_2D, 0, view.gl.RGBA, view.gl.RGBA, view.gl.UNSIGNED_BYTE, canvas); }
    else sprite.texture = canvasTexture(view.gl, canvas);
  };
  set(text);
  sprite.setText = set;
  return sprite;
}

function markerGeo(color) {
  const c = cone(5, 16, 16);
  const cT = transformGeo(c, mat4FromBasis([0, 8, 0], [1, 0, 0], [0, 1, 0], [0, 0, 1], 1));
  const r = ring(7, 9, 40);
  const rT = transformGeo(r, mat4FromBasis([0, 0.5, 0], [1, 0, 0], [0, 1, 0], [0, 0, 1], 1));
  const m = computeNormals(mergeGeos([cT, rT]));
  return { geo: m, color };
}

function speedColor(s, sMin, sMax) {
  const f = sMax > sMin ? (s - sMin) / (sMax - sMin) : 0;
  // HSL blue->red
  const h = 0.62 - 0.62 * f;
  return hslToRgb(h, 0.9, 0.55);
}
function hslToRgb(h, s, l) {
  const q = l < 0.5 ? l * (1 + s) : l + s - l * s;
  const p = 2 * l - q;
  const f = (t) => {
    if (t < 0) t += 1; if (t > 1) t -= 1;
    if (t < 1 / 6) return p + (q - p) * 6 * t;
    if (t < 1 / 2) return q;
    if (t < 2 / 3) return p + (q - p) * (2 / 3 - t) * 6;
    return p;
  };
  return [f(h + 1 / 3), f(h), f(h - 1 / 3)];
}

function lineGeo(points, colors, closed = false) {
  const positions = new Float32Array(points.length * 3);
  const cols = new Float32Array(points.length * 3);
  points.forEach((p, i) => {
    positions.set(p, i * 3);
    cols.set(colors[i] || [1, 1, 1], i * 3);
  });
  return { positions, colors: cols, indices: null, closed };
}

export class EarthScene {
  constructor(container) {
    this.container = container;
    this.canvas = document.createElement('canvas');
    this.canvas.style.cssText = 'position:absolute;inset:0;width:100%;height:100%;display:block';
    container.appendChild(this.canvas);
    this.view = new View(this.canvas);
    this.view.camera.up = [0, 0, 1];
    this.samples = null;
    this.playT = 0;
    this.follow = false;
    this.pulse = 0;
    this.launch = null;
    this.onTapLaunch = null;
    this.view.onTap = (px, py) => {
      const ll = this.pickPx(px, py);
      if (ll && this.onTapLaunch) this.onTapLaunch(ll.lat, ll.lon);
    };
    this._build();
  }

  _build() {
    const view = this.view;
    // earth
    this.earthGeo = ellipsoid(A_KM, C_KM, 128, 86);
    this.earth = view.add({ geo: this.earthGeo, shader: 'earth', dayMap: null, nightMap: null });
    loadB64Image('assets/earth-day.jpg.b64', 'image/jpeg').then((img) => { this.earth.dayMap = makeTexture(view.gl, img); });
    loadB64Image('assets/earth-night.jpg.b64', 'image/jpeg').then((img) => { this.earth.nightMap = makeTexture(view.gl, img); });
    // clouds
    this.cloudGeo = ellipsoid(A_KM * 1.006, C_KM * 1.006, 72, 48);
    this.clouds = view.add({ geo: this.cloudGeo, shader: 'lit', color: [1, 1, 1], transparent: true, opacity: 0.55, map: null, mapAlpha: true, model: mat4FromPosRotY([0, 0, 0], 0) });
    // clouds texture is grayscale (luminance = alpha); expand to RGBA at load time
    loadB64Image('assets/clouds-alpha.png.b64', 'image/png').then((img) => {
      const cv = document.createElement('canvas');
      cv.width = img.width; cv.height = img.height;
      const g = cv.getContext('2d');
      g.drawImage(img, 0, 0);
      const id = g.getImageData(0, 0, cv.width, cv.height);
      for (let i = 0; i < id.data.length; i += 4) {
        const l = id.data[i];
        id.data[i] = 255; id.data[i + 1] = 255; id.data[i + 2] = 255; id.data[i + 3] = l;
      }
      g.putImageData(id, 0, 0);
      this.clouds.map = makeTexture(view.gl, cv);
    });
    // atmosphere glow
    view.add({ geo: ellipsoid(A_KM * 1.035, C_KM * 1.035, 64, 48), shader: 'glow' });
    // stars
    {
      const n = 2500, pos = new Float32Array(n * 3), col = new Float32Array(n * 3);
      for (let i = 0; i < n; i++) {
        const v = [Math.random() * 2 - 1, Math.random() * 2 - 1, Math.random() * 2 - 1];
        const l = v3.len(v) || 1;
        const r = 90000 + Math.random() * 40000;
        pos.set(v3.scale(v, r / l), i * 3);
        const b = 0.4 + Math.random() * 0.6, w = Math.random();
        col.set([b, b * (w > 0.8 ? 0.85 : 1), b * (w < 0.2 ? 0.8 : 1)], i * 3);
      }
      view.add({ geo: { positions: pos, colors: col }, shader: 'points', pointSize: 1.7, opacity: 0.95 });
    }
    // graticule
    {
      const pts = [];
      const push = (a, b) => { pts.push(a, b); };
      for (let lon = -180; lon < 180; lon += 10) for (let lat = -80; lat < 80; lat += 2) push(latLonToVec3(lat, lon, 1.5), latLonToVec3(lat + 2, lon, 1.5));
      for (let lat = -80; lat <= 80; lat += 10) for (let lon = -180; lon < 180; lon += 2) push(latLonToVec3(lat, lon, 1.5), latLonToVec3(lat, lon + 2, 1.5));
      const g = lineGeo(pts, pts.map(() => [0.29, 0.42, 0.54]));
      view.add({ geo: g, shader: 'line', mode: 'lines', opacity: 0.16 });
    }
    // markers
    this.launchMarkerGeo = markerGeo();
    this.impactMarkerGeo = markerGeo();
    this.launchDrawable = view.add({ geo: this.launchMarkerGeo.geo, shader: 'flat', color: [0.18, 0.9, 0.42, 1], model: mat4FromBasis([0, 0, 0], [1, 0, 0], [0, 0, 1], [0, 1, 0], 1) });
    this.impactDrawable = view.add({ geo: this.impactMarkerGeo.geo, shader: 'flat', color: [1, 0.25, 0.25, 1], visible: false, model: mat4FromBasis([0, 0, 0], [1, 0, 0], [0, 0, 1], [0, 1, 0], 1) });
    this.launchLabel = makeLabelSprite(this.view, 'LAUNCH');
    this.impactLabel = makeLabelSprite(this.view, 'IMPACT');
    this.impactLabel.visible = false;
    view.sprites.push(this.launchLabel, this.impactLabel);
    // projectile marker: cone + glow sphere
    const pc = transformGeo(cone(2.4, 9, 12), mat4FromBasis([0, 4.5, 0], [1, 0, 0], [0, 1, 0], [0, 0, 1], 1));
    const sp = ellipsoid(3.2, 3.2, 12, 8);
    this.projDrawable = view.add({ geo: mergeGeos([pc, sp]), shader: 'flat', color: [1, 0.63, 0.19, 1], visible: false, model: mat4FromBasis([0, 0, 0], [1, 0, 0], [0, 0, 1], [0, 1, 0], 1) });
    // trajectory + overlays
    this.trajFull = view.add({ geo: { positions: new Float32Array(0), colors: new Float32Array(0) }, shader: 'line', mode: 'strip', opacity: 0.35, visible: false });
    this.trajFlown = view.add({ geo: { positions: new Float32Array(0), colors: new Float32Array(0) }, shader: 'line', mode: 'strip', opacity: 1, visible: false, count: 0 });
    this.pathLine = view.add({ geo: { positions: new Float32Array(0), colors: new Float32Array(0) }, shader: 'line', mode: 'strip', color: [1, 1, 1], opacity: 0.8, visible: false });
    this.azLine = view.add({ geo: { positions: new Float32Array(0), colors: new Float32Array(0) }, shader: 'line', mode: 'strip', opacity: 0.9, visible: false });
    this.dispDrawables = [];
  }

  _setLine(d, geo) {
    d.geo = geo;
    d.visible = true;
  }

  resize() { this.view.resize(); }

  pickPx(px, py) {
    const ray = this.view.screenRayAt(px, py);
    const t = rayEllipsoid(ray.origin, ray.dir, A_KM, C_KM);
    if (t == null) return null;
    return vec3ToLatLon(v3.add(ray.origin, v3.scale(ray.dir, t)));
  }

  pick(clientX, clientY) {
    const rect = this.canvas.getBoundingClientRect();
    return this.pickPx((clientX - rect.left) * (this.canvas.width / rect.width), (clientY - rect.top) * (this.canvas.height / rect.height));
  }

  setLaunch(lat, lon) {
    this.launch = { lat, lon };
    const p = latLonToVec3(lat, lon, 1);
    const { up, east, north } = surfaceBasis(lat, lon);
    this.launchDrawable.model = mat4FromBasis(p, east, up, north, 1);
    this.launchLabel.center = v3.add(p, v3.scale(up, 26));
    this.launchLabel.setText(`LAUNCH  ${lat.toFixed(3)}°, ${lon.toFixed(3)}°`);
    this.launchLabel.visible = true;
    // ENU axes
    if (this.axes) for (const a of this.axes) this.view.remove(a);
    this.axes = [];
    const mk = (dir, color) => {
      const g = lineGeo([[0, 0, 0], v3.scale(dir, 90)], [color, color]);
      const d = this.view.add({ geo: g, shader: 'line', mode: 'strip', opacity: 1 });
      d.model = mat4FromBasis(p, east, up, north, 1);
      this.axes.push(d);
    };
    mk(east, [1, 0.33, 0.33]); mk(north, [0.33, 1, 0.33]); mk(up, [0.33, 0.53, 1]);
  }

  setAzimuthRay(azimuthDeg, lengthKm) {
    if (!this.launch) return;
    const { lat, lon } = this.launch;
    const az = azimuthDeg * DEG;
    const pts = [];
    for (let i = 0; i <= 60; i++) {
      const f = i / 60;
      const p = offsetLatLon(lat, lon, Math.sin(az) * lengthKm * f, Math.cos(az) * lengthKm * f);
      pts.push(latLonToVec3(p.lat, p.lon, 1.5));
    }
    this._setLine(this.azLine, lineGeo(pts, pts.map(() => [1, 0.82, 0.29])));
  }

  setImpact(lat, lon, label = 'IMPACT') {
    if (lat == null) { this.impactDrawable.visible = false; this.impactLabel.visible = false; return; }
    this.impact = { lat, lon };
    const p = latLonToVec3(lat, lon, 1);
    const { up, east, north } = surfaceBasis(lat, lon);
    this.impactDrawable.model = mat4FromBasis(p, east, up, north, 1);
    this.impactDrawable.visible = true;
    this.impactLabel.center = v3.add(p, v3.scale(up, 26));
    this.impactLabel.setText(`${label}  ${lat.toFixed(3)}°, ${lon.toFixed(3)}°`);
    this.impactLabel.visible = true;
  }

  setTrajectory(samples) {
    this.samples = samples;
    if (!samples || samples.length < 2) {
      this.trajFull.visible = false; this.trajFlown.visible = false; this.projDrawable.visible = false;
      return;
    }
    const n = samples.length;
    let sMin = Infinity, sMax = -Infinity;
    for (const s of samples) { sMin = Math.min(sMin, s.speed); sMax = Math.max(sMax, s.speed); }
    const pts = [], cols = [];
    for (const s of samples) {
      pts.push([s.ecef[0] / 1000, s.ecef[1] / 1000, s.ecef[2] / 1000]);
      cols.push(speedColor(s.speed, sMin, sMax));
    }
    const g = lineGeo(pts, cols);
    this._setLine(this.trajFull, g);
    this._setLine(this.trajFlown, g);
    this.trajFlown.count = 2;
    this.playT = 0;
  }

  setSurfacePath(launch, impact) {
    if (!launch || !impact) { this.pathLine.visible = false; return; }
    const pts = [];
    for (let i = 0; i <= 160; i++) {
      const f = i / 160;
      pts.push(latLonToVec3(launch.lat + (impact.lat - launch.lat) * f, launch.lon + (impact.lon - launch.lon) * f, 1.2));
    }
    this._setLine(this.pathLine, lineGeo(pts, pts.map(() => [1, 0.82, 0.29])));
  }

  setDispersion(mc, launchAzimuthDeg) {
    for (const d of this.dispDrawables) this.view.remove(d);
    this.dispDrawables = [];
    if (!mc) return;
    const mean = {
      lat: mc.impacts.reduce((s, i) => s + i.lat, 0) / mc.n,
      lon: mc.impacts.reduce((s, i) => s + i.lon, 0) / mc.n,
    };
    const az = launchAzimuthDeg * DEG;
    // downrange/crossrange unit vectors as (east km, north km) offsets
    const down = { e: Math.sin(az), n: Math.cos(az) };
    const cross = { e: Math.cos(az), n: -Math.sin(az) };
    const ringPts = (semiDown, semiCross) => {
      const pts = [];
      for (let i = 0; i <= 128; i++) {
        const th = (i / 128) * Math.PI * 2;
        const e = down.e * Math.cos(th) * semiDown + cross.e * Math.sin(th) * semiCross;
        const n = down.n * Math.cos(th) * semiDown + cross.n * Math.sin(th) * semiCross;
        const p = offsetLatLon(mean.lat, mean.lon, e, n);
        pts.push(latLonToVec3(p.lat, p.lon, 2.0));
      }
      return pts;
    };
    const addRing = (pts, color, opacity) => {
      const g = lineGeo(pts, pts.map(() => color));
      g.closed = true;
      const d = this.view.add({ geo: g, shader: 'line', mode: 'loop', opacity });
      this.dispDrawables.push(d);
    };
    addRing(ringPts(mc.cepKm, mc.cepKm), [1, 0.67, 0], 0.95);
    addRing(ringPts(2.4477 * mc.stdDownrangeKm, 2.4477 * mc.stdCrossrangeKm), [1, 0.4, 0.27], 0.55);
    const pts = mc.impacts.map((i) => latLonToVec3(i.lat, i.lon, 1.5));
    const g = lineGeo(pts, pts.map(() => [1, 0.53, 0.33]));
    this.dispDrawables.push(this.view.add({ geo: g, shader: 'points', pointSize: 3.2, opacity: 0.85 }));
  }

  sampleAt(t) {
    const s = this.samples;
    if (!s || s.length < 2) return null;
    if (t <= s[0].t) return this._mkState(0);
    if (t >= s[s.length - 1].t) return this._mkState(s.length - 1);
    let lo = 0, hi = s.length - 1;
    while (hi - lo > 1) { const mid = (lo + hi) >> 1; if (s[mid].t <= t) lo = mid; else hi = mid; }
    const a = s[lo], b = s[hi];
    const f = (t - a.t) / Math.max(b.t - a.t, 1e-9);
    const pos = [
      (a.ecef[0] + (b.ecef[0] - a.ecef[0]) * f) / 1000,
      (a.ecef[1] + (b.ecef[1] - a.ecef[1]) * f) / 1000,
      (a.ecef[2] + (b.ecef[2] - a.ecef[2]) * f) / 1000,
    ];
    const dir = v3.norm([b.ecef[0] - a.ecef[0], b.ecef[1] - a.ecef[1], b.ecef[2] - a.ecef[2]]);
    const sample = {
      t, h: a.h + (b.h - a.h) * f, speed: a.speed + (b.speed - a.speed) * f,
      mach: a.mach + (b.mach - a.mach) * f, q: a.q + (b.q - a.q) * f,
      rangeKm: a.rangeKm + (b.rangeKm - a.rangeKm) * f, mass: a.mass + (b.mass - a.mass) * f,
      lat: a.lat + (b.lat - a.lat) * f, lon: a.lon + (b.lon - a.lon) * f,
    };
    return { pos, dir, sample, index: lo + f };
  }

  _mkState(i) {
    const s = this.samples[i];
    const pos = [s.ecef[0] / 1000, s.ecef[1] / 1000, s.ecef[2] / 1000];
    const nxt = this.samples[Math.min(i + 1, this.samples.length - 1)];
    const dir = v3.norm([nxt.ecef[0] - s.ecef[0], nxt.ecef[1] - s.ecef[1], nxt.ecef[2] - s.ecef[2]]);
    return { pos, dir, sample: s, index: i };
  }

  setPlayback(t) {
    this.playT = t;
    if (!this.samples || this.samples.length < 2) return;
    const st = this.sampleAt(t);
    if (!st) return;
    this.projDrawable.visible = true;
    const s = this._projScale();
    // orient local +Y along velocity dir
    const dir = st.dir;
    const up = dir;
    let east = v3.cross([0, 0, 1], up);
    if (v3.len(east) < 1e-6) east = [1, 0, 0];
    east = v3.norm(east);
    const north = v3.cross(up, east);
    this.projDrawable.model = mat4FromBasis(st.pos, east, up, north, s);
    this.trajFlown.count = Math.max(2, Math.floor(st.index) + 1);
    if (this.follow) {
      const cam = this.view.camera;
      cam.target = v3.lerp(cam.target, st.pos, 0.12);
    }
  }

  _projScale() {
    const cam = this.view.camera;
    const d = v3.len(v3.sub(cam.pos, this.projDrawable.model ? [this.projDrawable.model[12], this.projDrawable.model[13], this.projDrawable.model[14]] : [0, 0, 0]));
    return Math.max(0.6, Math.min(120, d * 0.006));
  }

  setFollow(on) { this.follow = on; }

  focusOn(lat, lon, distKm = 4000) {
    const p = latLonToVec3(lat, lon, 0);
    const cam = this.view.camera;
    let dir = v3.sub(cam.pos, cam.target);
    if (v3.len(dir) < 1e-6) dir = [1, 0.5, 0.8];
    dir = v3.norm(dir);
    cam.target = p.slice();
    cam.pos = v3.add(p, v3.scale(dir, Math.max(distKm, A_KM + 50)));
  }

  update(dt) {
    this.pulse += dt;
    const s = 1 + 0.08 * Math.sin(this.pulse * 4);
    if (this.launch) {
      const { lat, lon } = this.launch;
      const p = latLonToVec3(lat, lon, 1);
      const { up, east, north } = surfaceBasis(lat, lon);
      this.launchDrawable.model = mat4FromBasis(p, east, up, north, s);
    }
    if (this.impactDrawable.visible && this.impact) {
      const p = latLonToVec3(this.impact.lat, this.impact.lon, 1);
      const { up, east, north } = surfaceBasis(this.impact.lat, this.impact.lon);
      this.impactDrawable.model = mat4FromBasis(p, east, up, north, 1 + 0.12 * Math.sin(this.pulse * 5));
    }
    this.clouds.model = mat4FromPosRotY([0, 0, 0], this.pulse * 0.0016);
  }

  render() { this.view.render(); }
}
