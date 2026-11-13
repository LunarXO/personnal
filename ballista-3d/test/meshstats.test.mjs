import { meshStats, projectedArea, autoReferenceArea } from '../js/meshstats.js';

let failed = 0;
const ok = (c, name, extra = '') => { console.log((c ? 'PASS  ' : 'FAIL  ') + name + (c ? '' : ' ' + extra)); if (!c) failed++; };
const approx = (a, b, rel, name) => ok(Math.abs(a - b) / Math.max(Math.abs(b), 1e-12) <= rel, name, `(got ${a}, want ${b})`);

// unit cube 2x2x2 centered at origin, indexed
const s = 1;
const pos = new Float32Array([
  -s, -s, -s, s, -s, -s, s, s, -s, -s, s, -s,
  -s, -s, s, s, -s, s, s, s, s, -s, s, s,
]);
const idx = [
  0, 2, 1, 0, 3, 2, // bottom
  4, 5, 6, 4, 6, 7, // top
  0, 1, 5, 0, 5, 4, // -z? side
  2, 3, 7, 2, 7, 6,
  1, 2, 6, 1, 6, 5,
  0, 4, 7, 0, 7, 3,
];
const st = meshStats(pos, idx);
approx(st.volume, 8, 1e-6, 'cube volume = 8');
approx(st.surfaceArea, 24, 1e-6, 'cube surface area = 24');
approx(st.size[0], 2, 1e-12, 'cube size x = 2');

// projected area of the cube along z = 4, along diagonal = 4*sqrt(3) upper bound
approx(projectedArea(pos, idx, [0, 0, 1]), 4, 1e-6, 'projected area along z = 4');
const diag = projectedArea(pos, idx, [1, 1, 1]);
ok(diag > 4 && diag <= 4 * Math.sqrt(3) + 1e-6, 'projected area along diagonal in (4, 4√3]', `got ${diag.toFixed(4)}`);

const ref = autoReferenceArea(pos, idx);
ok(ref.area >= 4, 'autoReferenceArea >= face area', `got ${ref.area.toFixed(3)}`);

// sphere-ish: icosahedron scaled, volume < cube circumscribing
console.log(failed ? `\n${failed} FAILED` : '\nall meshstats tests passed');
process.exit(failed ? 1 : 0);
