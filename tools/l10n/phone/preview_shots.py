#!/usr/bin/env python3
"""preview_shots.py <outdir> screen:lang [screen:lang ...]  -> raw frames outdir/<screen>_<lang>.raw (debug --es ab_screen preview) and the screen text."""
import os, subprocess, sys, time, xml.etree.ElementTree as ET
D = os.environ.get('PHONE', '1d04811b'); PKG = os.environ.get('PKG', 'com.abacus.puzzle.l10n'); ACT = f'{PKG}/com.jigar.me.ui.view.home.HomeActivity'
def adb(*a, raw=False, t=60):
    r = subprocess.run(['adb', '-s', D, *a], capture_output=True, timeout=t); return r.stdout if raw else r.stdout.decode('utf-8', 'ignore')
def text():
    adb('shell', 'uiautomator', 'dump', '/sdcard/p.xml')
    try: root = ET.fromstring(adb('exec-out', 'cat', '/sdcard/p.xml'))
    except Exception: return []
    return [t for n in root.iter('node') for t in [(n.get('text') or n.get('content-desc') or '').strip()] if t]
out = sys.argv[1]; os.makedirs(out, exist_ok=True)
for item in sys.argv[2:]:
    screen, lang = item.split(':')
    adb('shell', 'cmd', 'locale', 'set-app-locales', PKG, '--locales', lang)
    adb('shell', 'input', 'keyevent', 'KEYCODE_WAKEUP'); adb('shell', 'wm', 'dismiss-keyguard')
    adb('shell', 'am', 'force-stop', PKG); adb('shell', 'am', 'start', '-n', ACT, '--es', 'ab_screen', screen); time.sleep(6)
    t1 = text(); frame = adb('exec-out', 'screencap', raw=True)
    open(f'{out}/{screen}_{lang}.raw', 'wb').write(frame)
    print(f'{screen}:{lang} alive={bool(adb("shell", "pidof", PKG).strip())} |', ' | '.join(t1)[:230])
adb('shell', 'cmd', 'locale', 'set-app-locales', PKG, '--locales', '')
