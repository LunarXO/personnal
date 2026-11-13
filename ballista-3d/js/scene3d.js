// Compact WebGL scene renderer: camera + orbit controls + drawables + picking.
// Browser-only module (DOM/WebGL used inside the class, not at import time).
import { createProgram, makeBuffer, makeTexture, loadB64Image, canvasTexture } from './gl.js';
import { mat4Perspective, mat4LookAt, mat4Multiply, mat4Invert, mat3NormalFromMat4, screenRay, v3 } from './glmath.js';

const GAMMA = `vec3 srgbEncode(vec3 c){ return pow(max(c, vec3(0.0)), vec3(1.0/2.2)); }
vec3 srgbDecode(vec3 c){ return pow(max(c, vec3(0.0)), vec3(2.2)); }`;

const SHADERS = {
  flat: {
    vs: `attribute vec3 aPos; attribute vec3 aColor;
      uniform mat4 uVP; uniform vec4 uColor; uniform float uUseVC;
      varying vec4 vC;
      void main(){ vC = mix(uColor, vec4(aColor,1.0), uUseVC); gl_Position = uVP * vec4(aPos,1.0); }`,
    fs: `precision mediump float; varying vec4 vC; uniform float uOpacity;
      ${GAMMA}
      void main(){ gl_FragColor = vec4(srgbEncode(vC.rgb), vC.a * uOpacity); }`,
  },
  lit: {
    vs: `attribute vec3 aPos; attribute vec3 aNrm; attribute vec2 aUV;
      uniform mat4 uVP; uniform mat4 uModel; uniform mat3 uNMat;
      varying vec3 vN; varying vec2 vUV; varying vec3 vWp;
      void main(){ vec4 wp = uModel * vec4(aPos,1.0); vWp = wp.xyz;
        vN = normalize(uNMat * aNrm); vUV = aUV; gl_Position = uVP * vec4(aPos,1.0); }`,
    fs: `precision mediump float; varying vec3 vN; varying vec2 vUV; varying vec3 vWp;
      uniform vec3 uColor; uniform sampler2D uMap; uniform float uHasMap; uniform float uMapAlpha; uniform vec3 uSunDir; uniform float uOpacity;
      ${GAMMA}
      void main(){
        vec3 base = uColor;
        float alpha = uOpacity;
        if (uHasMap > 0.5) {
          vec4 tex = texture2D(uMap, vUV);
          base *= srgbDecode(tex.rgb);
          alpha *= mix(1.0, tex.a, uMapAlpha);
        }
        vec3 n = normalize(vN);
        float l = max(dot(n, normalize(uSunDir)), 0.0);
        vec3 col = base * (0.25 + 1.05 * l);
        gl_FragColor = vec4(srgbEncode(col), alpha);
      }`,
  },
  earth: {
    vs: `attribute vec3 aPos; attribute vec3 aNrm; attribute vec2 aUV;
      uniform mat4 uVP; uniform mat4 uModel; uniform mat3 uNMat;
      varying vec3 vN; varying vec2 vUV;
      void main(){ vN = normalize(uNMat * aNrm); vUV = aUV; gl_Position = uVP * vec4(aPos,1.0); }`,
    fs: `precision mediump float; varying vec3 vN; varying vec2 vUV;
      uniform sampler2D uDay; uniform sampler2D uNight; uniform float uHasDay; uniform float uHasNight;
      uniform vec3 uSunDir;
      ${GAMMA}
      void main(){
        vec3 n = normalize(vN);
        float light = dot(n, normalize(uSunDir));
        float day = clamp(light, 0.0, 1.0);
        float nightMask = 1.0 - smoothstep(-0.06, 0.22, day);
        vec3 dayCol = uHasDay > 0.5 ? srgbDecode(texture2D(uDay, vUV).rgb) : vec3(0.25,0.4,0.65);
        vec3 nightCol = uHasNight > 0.5 ? srgbDecode(texture2D(uNight, vUV).rgb) : vec3(0.0);
        vec3 col = dayCol * (0.06 + 1.15 * day) + nightCol * nightMask * 1.1;
        gl_FragColor = vec4(srgbEncode(col), 1.0);
      }`,
  },
  glow: {
    vs: `attribute vec3 aPos; attribute vec3 aNrm;
      uniform mat4 uVP; uniform mat4 uModel; uniform mat3 uNMat;
      varying vec3 vN; varying vec3 vP;
      void main(){ vN = normalize(uNMat * aNrm); vec4 p = uModel * vec4(aPos,1.0); vP = p.xyz; gl_Position = uVP * vec4(aPos,1.0); }`,
    fs: `precision mediump float; varying vec3 vN; varying vec3 vP;
      void main(){
        vec3 V = normalize(-vP);
        float intensity = pow(clamp(0.68 - dot(normalize(vN), V), 0.0, 1.0), 3.2);
        gl_FragColor = vec4(vec3(0.35,0.62,1.0) * intensity, 1.0);
      }`,
  },
  line: {
    vs: `attribute vec3 aPos; attribute vec3 aColor;
      uniform mat4 uVP;
      varying vec4 vC;
      void main(){ vC = vec4(aColor, 1.0); gl_Position = uVP * vec4(aPos,1.0); }`,
    fs: `precision mediump float; varying vec4 vC; uniform float uOpacity;
      ${GAMMA}
      void main(){ gl_FragColor = vec4(srgbEncode(vC.rgb), vC.a * uOpacity); }`,
  },
  points: {
    vs: `attribute vec3 aPos; attribute vec3 aColor;
      uniform mat4 uVP; uniform float uSize;
      varying vec4 vC;
      void main(){ vC = vec4(aColor,1.0); gl_Position = uVP * vec4(aPos,1.0); gl_PointSize = uSize; }`,
    fs: `precision mediump float; varying vec4 vC; uniform float uOpacity;
      ${GAMMA}
      void main(){ vec2 d = gl_PointCoord - 0.5; if (dot(d,d) > 0.25) discard;
        gl_FragColor = vec4(srgbEncode(vC.rgb), vC.a * uOpacity); }`,
  },
  sprite: {
    vs: `attribute vec2 aCorner;
      uniform mat4 uVP; uniform vec3 uCenter; uniform vec2 uSize; uniform vec3 uRight; uniform vec3 uUp;
      varying vec2 vUV;
      void main(){ vUV = aCorner * 0.5 + 0.5;
        vec3 wp = uCenter + uRight * (aCorner.x * uSize.x) + uUp * (aCorner.y * uSize.y);
        gl_Position = uVP * vec4(wp, 1.0); }`,
    fs: `precision mediump float; varying vec2 vUV; uniform sampler2D uMap; uniform float uOpacity;
      void main(){ vec4 t = texture2D(uMap, vUV); gl_FragColor = vec4(t.rgb, t.a * uOpacity); }`,
  },
};

export class View {
  constructor(canvas) {
    this.canvas = canvas;
    const gl = canvas.getContext('webgl', { antialias: true, alpha: false }) || canvas.getContext('experimental-webgl');
    if (!gl) throw new Error('WebGL not available');
    this.gl = gl;
    this.programs = {};
    for (const [name, s] of Object.entries(SHADERS)) this.programs[name] = createProgram(gl, s.vs, s.fs);
    this.drawables = [];
    this.sprites = [];
    this.camera = { pos: [9000, 6000, 12000], target: [0, 0, 0], up: [0, 0, 1], fov: 55, near: 0.05, far: 500000 };
    this.sunDir = v3.norm([30000, 9000, 12000]);
    this.onTap = null;
    this._geoCache = new WeakMap();
    this._bindControls();
    this.resize();
  }

  add(d) { this.drawables.push(d); return d; }
  remove(d) { const i = this.drawables.indexOf(d); if (i >= 0) this.drawables.splice(i, 1); }
  clear() { this.drawables.length = 0; this.sprites.length = 0; }

  resize() {
    const dpr = Math.min(window.devicePixelRatio || 1, 2);
    const w = this.canvas.clientWidth, h = this.canvas.clientHeight;
    if (!w || !h) return;
    if (this.canvas.width !== Math.round(w * dpr) || this.canvas.height !== Math.round(h * dpr)) {
      this.canvas.width = Math.round(w * dpr);
      this.canvas.height = Math.round(h * dpr);
    }
  }

  _geoBuffers(geo) {
    let b = this._geoCache.get(geo);
    if (b) return b;
    const gl = this.gl;
    b = {
      pos: makeBuffer(gl, geo.positions),
      nrm: geo.normals ? makeBuffer(gl, geo.normals) : null,
      uv: geo.uvs ? makeBuffer(gl, geo.uvs) : null,
      col: geo.colors ? makeBuffer(gl, geo.colors) : null,
      idx: geo.indices ? makeBuffer(gl, geo.indices, gl.ELEMENT_ARRAY_BUFFER) : null,
      count: geo.indices ? geo.indices.length : geo.positions.length / 3,
    };
    this._geoCache.set(geo, b);
    return b;
  }

  _bindControls() {
    const cv = this.canvas;
    let drag = null;
    cv.addEventListener('pointerdown', (e) => {
      cv.setPointerCapture(e.pointerId);
      drag = { x: e.clientX, y: e.clientY, t: performance.now(), btn: e.button, moved: 0, pan: e.button === 2 || e.shiftKey };
    });
    cv.addEventListener('pointermove', (e) => {
      if (!drag) return;
      const dx = e.clientX - drag.x, dy = e.clientY - drag.y;
      drag.moved += Math.abs(dx) + Math.abs(dy);
      drag.x = e.clientX; drag.y = e.clientY;
      const cam = this.camera;
      const toCam = v3.sub(cam.pos, cam.target);
      const r = v3.len(toCam);
      if (drag.pan) {
        const right = v3.norm(v3.cross(v3.norm(toCam), cam.up));
        const upv = v3.cross(right, v3.norm(toCam));
        const k = r * 0.0016;
        cam.target = v3.add(cam.target, v3.add(v3.scale(right, -dx * k), v3.scale(upv, dy * k)));
      } else {
        const theta = Math.atan2(toCam[1], toCam[0]) - dx * 0.005;
        let phi = Math.acos(v3.norm(toCam)[2]) - dy * 0.005;
        phi = Math.max(0.02, Math.min(Math.PI - 0.02, phi));
        const sp = Math.sin(phi);
        cam.pos = v3.add(cam.target, [r * sp * Math.cos(theta), r * sp * Math.sin(theta), r * Math.cos(phi)]);
      }
    });
    cv.addEventListener('pointerup', (e) => {
      if (drag && drag.moved < 6 && performance.now() - drag.t < 500 && this.onTap) {
        const rect = cv.getBoundingClientRect();
        this.onTap((e.clientX - rect.left) * (cv.width / rect.width), (e.clientY - rect.top) * (cv.height / rect.height));
      }
      drag = null;
    });
    cv.addEventListener('wheel', (e) => {
      e.preventDefault();
      const cam = this.camera;
      const toCam = v3.sub(cam.pos, cam.target);
      let r = v3.len(toCam) * Math.exp(e.deltaY * 0.0011);
      r = Math.max(6400, Math.min(150000, r));
      cam.pos = v3.add(cam.target, v3.scale(v3.norm(toCam), r));
    }, { passive: false });
    cv.addEventListener('contextmenu', (e) => e.preventDefault());
  }

  setCamera(pos, target) {
    this.camera.pos = pos.slice();
    this.camera.target = target.slice();
  }

  viewProj() {
    const cam = this.camera;
    const aspect = this.canvas.width / Math.max(1, this.canvas.height);
    const proj = mat4Perspective(cam.fov, aspect, cam.near, cam.far);
    const view = mat4LookAt(cam.pos, cam.target, cam.up);
    return { proj, view, vp: mat4Multiply(proj, view) };
  }

  // world ray from canvas pixel coords (device px, y down)
  screenRayAt(px, py) {
    const { proj, view } = this.viewProj();
    return screenRay(this.camera, view, proj, this.canvas.width, this.canvas.height, px, py);
  }

  render() {
    const gl = this.gl;
    const { vp } = this.viewProj();
    gl.viewport(0, 0, this.canvas.width, this.canvas.height);
    gl.clearColor(0.012, 0.02, 0.035, 1);
    gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);
    gl.enable(gl.DEPTH_TEST);
    gl.disable(gl.BLEND);

    const ordered = [...this.drawables].sort((a, b) => (a.transparent ? 1 : 0) - (b.transparent ? 1 : 0));
    for (const d of ordered) {
      if (d.visible === false) continue;
      this._draw(d, vp);
    }
    for (const s of this.sprites) {
      if (s.visible === false) continue;
      this._drawSprite(s, vp);
    }
  }

  _bindAttrib(p, name, buf, size) {
    const gl = this.gl;
    const loc = p.attribs[name];
    if (loc == null || loc < 0 || !buf) return;
    gl.bindBuffer(gl.ARRAY_BUFFER, buf);
    gl.enableVertexAttribArray(loc);
    gl.vertexAttribPointer(loc, size, gl.FLOAT, false, 0, 0);
  }

  _draw(d, vp) {
    const gl = this.gl;
    const p = this.programs[d.shader];
    gl.useProgram(p.prog);
    const b = this._geoBuffers(d.geo);
    const model = d.model || [1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1];
    gl.uniformMatrix4fv(p.uniforms.uVP, false, vp);
    if (p.uniforms.uModel) gl.uniformMatrix4fv(p.uniforms.uModel, false, model);
    if (p.uniforms.uNMat) gl.uniformMatrix3fv(p.uniforms.uNMat, false, mat3NormalFromMat4(model));

    if (d.shader === 'earth') {
      gl.activeTexture(gl.TEXTURE0);
      gl.bindTexture(gl.TEXTURE_2D, d.dayMap || null);
      gl.uniform1i(p.uniforms.uDay, 0);
      gl.activeTexture(gl.TEXTURE1);
      gl.bindTexture(gl.TEXTURE_2D, d.nightMap || null);
      gl.uniform1i(p.uniforms.uNight, 1);
      gl.uniform1f(p.uniforms.uHasDay, d.dayMap ? 1 : 0);
      gl.uniform1f(p.uniforms.uHasNight, d.nightMap ? 1 : 0);
      gl.uniform3fv(p.uniforms.uSunDir, this.sunDir);
      this._bindAttrib(p, 'aPos', b.pos, 3);
      this._bindAttrib(p, 'aNrm', b.nrm, 3);
      this._bindAttrib(p, 'aUV', b.uv, 2);
      gl.disable(gl.BLEND);
      gl.depthMask(true);
      gl.disable(gl.CULL_FACE);
    } else if (d.shader === 'lit') {
      gl.uniform3fv(p.uniforms.uColor, d.color || [1, 1, 1]);
      gl.uniform1f(p.uniforms.uOpacity, d.opacity != null ? d.opacity : 1);
      gl.uniform3fv(p.uniforms.uSunDir, d.sunDir || this.sunDir);
      if (p.uniforms.uMapAlpha) gl.uniform1f(p.uniforms.uMapAlpha, d.mapAlpha ? 1 : 0);
      if (d.map) {
        gl.activeTexture(gl.TEXTURE0);
        gl.bindTexture(gl.TEXTURE_2D, d.map);
        gl.uniform1i(p.uniforms.uMap, 0);
        gl.uniform1f(p.uniforms.uHasMap, 1);
      } else gl.uniform1f(p.uniforms.uHasMap, 0);
      this._bindAttrib(p, 'aPos', b.pos, 3);
      this._bindAttrib(p, 'aNrm', b.nrm, 3);
      this._bindAttrib(p, 'aUV', b.uv, 2);
      gl.disable(gl.CULL_FACE);
      if (d.transparent) { gl.enable(gl.BLEND); gl.blendFunc(gl.SRC_ALPHA, gl.ONE_MINUS_SRC_ALPHA); gl.depthMask(false); }
      else { gl.disable(gl.BLEND); gl.depthMask(true); }
    } else if (d.shader === 'glow') {
      gl.enable(gl.BLEND);
      gl.blendFunc(gl.ONE, gl.ONE);
      gl.depthMask(false);
      gl.enable(gl.CULL_FACE);
      gl.cullFace(gl.FRONT);
      this._bindAttrib(p, 'aPos', b.pos, 3);
      this._bindAttrib(p, 'aNrm', b.nrm, 3);
    } else if (d.shader === 'flat') {
      gl.disable(gl.CULL_FACE);
      if (d.transparent) { gl.enable(gl.BLEND); gl.blendFunc(gl.SRC_ALPHA, gl.ONE_MINUS_SRC_ALPHA); gl.depthMask(false); }
      else { gl.disable(gl.BLEND); gl.depthMask(true); }
      gl.uniform4fv(p.uniforms.uColor, d.color || [1, 1, 1, 1]);
      gl.uniform1f(p.uniforms.uOpacity, d.opacity != null ? d.opacity : 1);
      gl.uniform1f(p.uniforms.uUseVC, d.vertexColors && b.col ? 1 : 0);
      this._bindAttrib(p, 'aPos', b.pos, 3);
      this._bindAttrib(p, 'aColor', b.col || this._whiteBuf(), 3);
    } else if (d.shader === 'line') {
      gl.disable(gl.BLEND);
      if (d.transparent) { gl.enable(gl.BLEND); gl.blendFunc(gl.SRC_ALPHA, gl.ONE_MINUS_SRC_ALPHA); }
      gl.depthMask(!d.transparent);
      gl.uniform1f(p.uniforms.uOpacity, d.opacity != null ? d.opacity : 1);
      this._bindAttrib(p, 'aPos', b.pos, 3);
      this._bindAttrib(p, 'aColor', b.col || this._whiteBuf(), 3);
      const mode = d.mode === 'strip' ? gl.LINE_STRIP : d.mode === 'loop' ? gl.LINE_LOOP : gl.LINES;
      if (b.idx) {
        gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, b.idx);
        gl.drawElements(mode, d.count != null ? d.count : b.count, gl.UNSIGNED_INT, 0);
      } else {
        gl.drawArrays(mode, 0, d.count != null ? d.count : b.count);
      }
      return;
    } else if (d.shader === 'points') {
      gl.enable(gl.BLEND);
      gl.blendFunc(gl.SRC_ALPHA, gl.ONE_MINUS_SRC_ALPHA);
      gl.depthMask(false);
      gl.uniform1f(p.uniforms.uOpacity, d.opacity != null ? d.opacity : 1);
      gl.uniform1f(p.uniforms.uSize, d.pointSize || 2);
      this._bindAttrib(p, 'aPos', b.pos, 3);
      this._bindAttrib(p, 'aColor', b.col || this._whiteBuf(), 3);
      gl.drawArrays(gl.POINTS, 0, b.count);
      return;
    }
    if (b.idx) {
      gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, b.idx);
      gl.drawElements(gl.TRIANGLES, d.count != null ? d.count : b.count, gl.UNSIGNED_INT, 0);
    } else {
      gl.drawArrays(gl.TRIANGLES, 0, b.count);
    }
    if (d.shader === 'glow') { gl.disable(gl.CULL_FACE); }
  }

  _whiteBuf() {
    if (!this._white) this._white = makeBuffer(this.gl, new Float32Array([1, 1, 1]));
    return this._white;
  }

  _drawSprite(s, vp) {
    const gl = this.gl;
    const p = this.programs.sprite;
    gl.useProgram(p.prog);
    gl.uniformMatrix4fv(p.uniforms.uVP, false, vp);
    gl.uniform3fv(p.uniforms.uCenter, s.center);
    gl.uniform2f(p.uniforms.uSize, s.size[0], s.size[1]);
    const cam = this.camera;
    const fwd = v3.norm(v3.sub(cam.target, cam.pos));
    const right = v3.norm(v3.cross(fwd, cam.up));
    const up = v3.cross(right, fwd);
    gl.uniform3fv(p.uniforms.uRight, right);
    gl.uniform3fv(p.uniforms.uUp, up);
    gl.uniform1f(p.uniforms.uOpacity, s.opacity != null ? s.opacity : 1);
    gl.activeTexture(gl.TEXTURE0);
    gl.bindTexture(gl.TEXTURE_2D, s.texture);
    gl.uniform1i(p.uniforms.uMap, 0);
    if (!s._buf) s._buf = makeBuffer(gl, new Float32Array([-1, -1, 1, -1, -1, 1, 1, 1]));
    this._bindAttrib(p, 'aCorner', s._buf, 2);
    gl.enable(gl.BLEND);
    gl.blendFunc(gl.SRC_ALPHA, gl.ONE_MINUS_SRC_ALPHA);
    gl.depthMask(false);
    gl.disable(gl.CULL_FACE);
    gl.drawArrays(gl.TRIANGLE_STRIP, 0, 4);
  }
}

export { makeTexture, loadB64Image, canvasTexture };
