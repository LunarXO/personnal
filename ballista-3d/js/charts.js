// Minimal canvas line-chart with dual y-axes and a playback playhead.
export class Chart {
  constructor(canvas, series, opts = {}) {
    this.canvas = canvas;
    this.ctx = canvas.getContext('2d');
    this.series = series; // [{key, label, color, axis:'l'|'r', fmt}]
    this.opts = Object.assign({ xLabel: 'time (s)', title: '' }, opts);
    this.samples = [];
    this.playT = null;
    this.dpr = Math.min(window.devicePixelRatio || 1, 2);
  }

  setData(samples) { this.samples = samples || []; this.draw(); }
  setPlayhead(t) { this.playT = t; this.draw(); }

  draw() {
    const cv = this.canvas;
    const w = cv.clientWidth, h = cv.clientHeight;
    if (!w || !h) return;
    if (cv.width !== w * this.dpr || cv.height !== h * this.dpr) {
      cv.width = w * this.dpr; cv.height = h * this.dpr;
    }
    const g = this.ctx;
    g.setTransform(this.dpr, 0, 0, this.dpr, 0, 0);
    g.clearRect(0, 0, w, h);
    g.fillStyle = 'rgba(10,14,22,0.0)';
    g.fillRect(0, 0, w, h);
    const s = this.samples;
    if (!s.length) return;
    const padL = 46, padR = 46, padT = 22, padB = 20;
    const iw = w - padL - padR, ih = h - padT - padB;
    const tMax = Math.max(s[s.length - 1].t, 1e-6);
    const x = (t) => padL + (t / tMax) * iw;
    const vals = { l: [], r: [] };
    for (const se of this.series) {
      const arr = vals[se.axis];
      for (const p of s) arr.push(p[se.key]);
    }
    const range = (arr) => {
      let mn = Infinity, mx = -Infinity;
      for (const v of arr) { if (v < mn) mn = v; if (v > mx) mx = v; }
      if (mn === mx) { mn -= 1; mx += 1; }
      const pad = (mx - mn) * 0.08;
      return [mn - pad, mx + pad];
    };
    const [lMin, lMax] = range(vals.l.length ? vals.l : [0, 1]);
    const [rMin, rMax] = range(vals.r.length ? vals.r : [0, 1]);
    const yL = (v) => padT + ih - ((v - lMin) / (lMax - lMin)) * ih;
    const yR = (v) => padT + ih - ((v - rMin) / (rMax - rMin)) * ih;

    // grid + axis labels
    g.font = '10px system-ui, sans-serif';
    g.strokeStyle = 'rgba(120,140,170,0.18)';
    g.fillStyle = '#8fa3bf';
    g.lineWidth = 1;
    for (let i = 0; i <= 4; i++) {
      const yy = padT + (i / 4) * ih;
      g.beginPath(); g.moveTo(padL, yy); g.lineTo(padL + iw, yy); g.stroke();
      const lv = lMax - (i / 4) * (lMax - lMin);
      const rv = rMax - (i / 4) * (rMax - rMin);
      g.textAlign = 'right'; g.fillText(this._fmt(lv), padL - 5, yy + 3);
      g.textAlign = 'left'; g.fillText(this._fmt(rv), padL + iw + 5, yy + 3);
    }
    for (let i = 0; i <= 6; i++) {
      const xx = padL + (i / 6) * iw;
      g.textAlign = 'center';
      g.fillText(`${((i / 6) * tMax).toFixed(0)}s`, xx, h - 6);
    }
    g.textAlign = 'left'; g.fillStyle = '#c8d4e6';
    g.fillText(this.opts.title, padL, 14);
    // legend
    let lx = padL + 8;
    for (const se of this.series) {
      g.fillStyle = se.color;
      g.fillRect(lx, 18, 10, 3);
      g.fillStyle = '#aebdd2';
      g.fillText(se.label, lx + 14, 22);
      lx += 14 + g.measureText(se.label).width + 14;
    }
    // series
    for (const se of this.series) {
      const y = se.axis === 'l' ? yL : yR;
      g.strokeStyle = se.color;
      g.lineWidth = 1.6;
      g.beginPath();
      for (let i = 0; i < s.length; i++) {
        const xx = x(s[i].t), yy = y(s[i][se.key]);
        if (i === 0) g.moveTo(xx, yy); else g.lineTo(xx, yy);
      }
      g.stroke();
    }
    // playhead
    if (this.playT != null && this.playT <= tMax) {
      const xx = x(this.playT);
      g.strokeStyle = 'rgba(255,255,255,0.75)';
      g.lineWidth = 1;
      g.beginPath(); g.moveTo(xx, padT); g.lineTo(xx, padT + ih); g.stroke();
    }
  }

  _fmt(v) {
    const a = Math.abs(v);
    if (a >= 1000) return v.toFixed(0);
    if (a >= 100) return v.toFixed(0);
    if (a >= 1) return v.toFixed(1);
    return v.toFixed(2);
  }
}
