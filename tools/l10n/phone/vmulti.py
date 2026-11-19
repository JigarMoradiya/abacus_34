#!/usr/bin/env python3
"""vsheet.py <out.png> <spec> [lang ...]   spec = route name (deep link) or ab:<screen> (debug preview)

Opens the screen in each language on the Redmi and puts the frames into ONE labelled sheet (3 columns, 1/3 size).
Default languages: en fr es pt tr ru ja th ar.
"""
import os, subprocess, sys, time
from PIL import Image, ImageDraw

D = os.environ.get('PHONE', '1d04811b')
PKG = os.environ.get('PKG', 'com.abacus.puzzle.l10n')
ACT = f'{PKG}/com.jigar.me.ui.view.home.HomeActivity'
WAIT = float(os.environ.get('WAIT', '6'))
PORTRAIT = bool(os.environ.get('PORTRAIT'))
out, lang1 = sys.argv[1], sys.argv[2]
specs = sys.argv[3:]
W, H = (1080, 2392) if PORTRAIT else (2392, 1080)
SCALE = int(os.environ.get('SCALE', '3'))


def adb(*a, raw=False):
    r = subprocess.run(['adb', '-s', D, *a], capture_output=True, timeout=60)
    return r.stdout if raw else r.stdout.decode('utf-8', 'ignore')


tiles = []
for spec in specs:
    lang = lang1
    kind, name = ('ab_screen', spec[3:]) if spec.startswith('ab:') else ('deep_link_route', spec)
    adb('shell', 'cmd', 'locale', 'set-app-locales', PKG, '--locales', lang)
    adb('shell', f'input keyevent KEYCODE_WAKEUP; wm dismiss-keyguard; am force-stop {PKG}; am start -n {ACT} --es {kind} "{name}" >/dev/null 2>&1')
    time.sleep(WAIT)
    # the splash screen has no text: wait (up to ~25 s) until the screen has some, then let animations settle
    for _ in range(8):
        adb('shell', 'uiautomator dump /sdcard/v.xml >/dev/null 2>&1')
        if 'text="' in adb('exec-out', 'cat', '/sdcard/v.xml').replace('text=""', ''):
            break
        time.sleep(2)
    time.sleep(float(os.environ.get('SETTLE', '1.5')))
    d = adb('exec-out', 'screencap', raw=True)[16:]
    im = Image.frombytes('RGBA', (W, H), d[:W * H * 4]).convert('RGB').resize((W // SCALE, H // SCALE), Image.LANCZOS)
    ImageDraw.Draw(im).rectangle((0, 0, 150, 14), fill=(255, 255, 255))
    ImageDraw.Draw(im).text((3, 1), spec[:26], fill=(200, 0, 0))
    tiles.append(im)
w, h = tiles[0].size
cols = 3
rows = (len(tiles) + cols - 1) // cols
sheet = Image.new('RGB', (w * cols + 4 * (cols - 1), h * rows + 4 * (rows - 1)), (255, 0, 0))
for i, t in enumerate(tiles):
    sheet.paste(t, ((i % cols) * (w + 4), (i // cols) * (h + 4)))
sheet.save(out)
print(out, sheet.size)
