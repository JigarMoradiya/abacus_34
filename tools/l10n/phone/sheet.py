#!/usr/bin/env python3
"""sheet.py out.png scale file1.raw file2.raw ...  -> one small picture with the raw phone frames side by side (columns of `cols`)."""
import os, struct, sys, zlib
W, H = (1080, 2392) if os.environ.get('PORTRAIT') else (2392, 1080)   # PORTRAIT=1 for screens that are not landscape
out, S = sys.argv[1], int(sys.argv[2]); files = sys.argv[3:]
cols = 3 if len(files) > 3 else len(files)
def small(p):
    d = open(p, 'rb').read()[16:]; w, h = W // S, H // S
    return [bytes(b for x in range(w) for b in d[((y * S) * W + x * S) * 4:((y * S) * W + x * S) * 4 + 3]) for y in range(h)], w, h
tiles = [small(f) for f in files]; w, h = tiles[0][1], tiles[0][2]; gap = 4
rows_n = (len(tiles) + cols - 1) // cols; sheet = []
for r in range(rows_n):
    for y in range(h):
        line = b''
        for c in range(cols):
            i = r * cols + c
            line += (tiles[i][0][y] if i < len(tiles) else bytes(3 * w)) + (bytes([255, 0, 0] * gap) if c < cols - 1 else b'')
        sheet.append(line)
    if r < rows_n - 1: sheet.append(bytes([255, 0, 0] * ((w + gap) * cols - gap)))
def chunk(t, d): c = struct.pack('>I', len(d)) + t + d; return c + struct.pack('>I', zlib.crc32(t + d) & 0xffffffff)
tw = (w + gap) * cols - gap
open(out, 'wb').write(b'\x89PNG\r\n\x1a\n' + chunk(b'IHDR', struct.pack('>IIBBBBB', tw, len(sheet), 8, 2, 0, 0, 0)) + chunk(b'IDAT', zlib.compress(b''.join(b'\x00' + r for r in sheet), 9)) + chunk(b'IEND', b''))
print('sheet', tw, 'x', len(sheet))
