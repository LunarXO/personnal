// Tiny WebGL helpers. Browser-only (no top-level DOM access, import-safe).

export function compileShader(gl, type, src) {
  const sh = gl.createShader(type);
  gl.shaderSource(sh, src);
  gl.compileShader(sh);
  if (!gl.getShaderParameter(sh, gl.COMPILE_STATUS)) {
    throw new Error('shader compile: ' + gl.getShaderInfoLog(sh) + '\n' + src);
  }
  return sh;
}

export function createProgram(gl, vsSrc, fsSrc) {
  const prog = gl.createProgram();
  gl.attachShader(prog, compileShader(gl, gl.VERTEX_SHADER, vsSrc));
  gl.attachShader(prog, compileShader(gl, gl.FRAGMENT_SHADER, fsSrc));
  gl.linkProgram(prog);
  if (!gl.getProgramParameter(prog, gl.LINK_STATUS)) {
    throw new Error('program link: ' + gl.getProgramInfoLog(prog));
  }
  const uniforms = {};
  const nU = gl.getProgramParameter(prog, gl.ACTIVE_UNIFORMS);
  for (let i = 0; i < nU; i++) {
    const info = gl.getActiveUniform(prog, i);
    uniforms[info.name.replace('[0]', '')] = gl.getUniformLocation(prog, info.name);
  }
  const attribs = {};
  const nA = gl.getProgramParameter(prog, gl.ACTIVE_ATTRIBUTES);
  for (let i = 0; i < nA; i++) {
    const info = gl.getActiveAttrib(prog, i);
    attribs[info.name] = gl.getAttribLocation(prog, info.name);
  }
  return { prog, uniforms, attribs };
}

export function makeBuffer(gl, data, target = gl.ARRAY_BUFFER) {
  const buf = gl.createBuffer();
  gl.bindBuffer(target, buf);
  gl.bufferData(target, data, gl.STATIC_DRAW);
  return buf;
}

export function makeTexture(gl, source, { srgb = false, clamp = false } = {}) {
  const tex = gl.createTexture();
  gl.bindTexture(gl.TEXTURE_2D, tex);
  gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, gl.RGBA, gl.UNSIGNED_BYTE, source);
  const pot = (n) => (n & (n - 1)) === 0;
  const w = source.width, h = source.height;
  if (pot(w) && pot(h)) {
    gl.generateMipmap(gl.TEXTURE_2D);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.LINEAR_MIPMAP_LINEAR);
  } else {
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.LINEAR);
  }
  gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.LINEAR);
  gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, clamp ? gl.CLAMP_TO_EDGE : gl.REPEAT);
  gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, clamp ? gl.CLAMP_TO_EDGE : gl.REPEAT);
  return tex;
}

// assets are stored as base64 text split into <url>.0, <url>.1, ... parts
export async function loadB64Text(baseUrl) {
  let text = '';
  for (let i = 0; ; i++) {
    const r = await fetch(`${baseUrl}.${i}`);
    if (!r.ok) break;
    text += await r.text();
  }
  if (!text) throw new Error('no asset parts found for ' + baseUrl);
  return text;
}

export function loadB64Image(url, mime) {
  return loadB64Text(url).then((t) => {
    const bin = atob(t);
    const arr = new Uint8Array(bin.length);
    for (let i = 0; i < bin.length; i++) arr[i] = bin.charCodeAt(i);
    const blob = new Blob([arr], { type: mime });
    const objUrl = URL.createObjectURL(blob);
    const img = new Image();
    return new Promise((res, rej) => {
      img.onload = () => res(img);
      img.onerror = () => rej(new Error('failed to decode image ' + url));
      img.src = objUrl;
    });
  });
}

export function canvasTexture(gl, canvas) {
  const tex = gl.createTexture();
  gl.bindTexture(gl.TEXTURE_2D, tex);
  gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, gl.RGBA, gl.UNSIGNED_BYTE, canvas);
  gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.LINEAR);
  gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.LINEAR);
  gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, gl.CLAMP_TO_EDGE);
  gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, gl.CLAMP_TO_EDGE);
  return tex;
}
