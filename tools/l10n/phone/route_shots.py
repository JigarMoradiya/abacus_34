#!/usr/bin/env python3
"""route_shots.py <outdir> <route> lang [lang ...]  -> raw frames outdir/<route>_<lang>.raw plus the screen text per language."""
import os, subprocess, sys, time, xml.etree.ElementTree as ET
D = '1d04811b'; PKG = os.environ.get('PKG', 'com.abacus.puzzle.l10n'); ACT = f'{PKG}/com.jigar.me.ui.view.home.HomeActivity'
WAIT = float(os.environ.get('WAIT', '7'))
def adb(*a, raw=False, t=60):
    r = subprocess.run(['adb', '-s', D, *a], capture_output=True, timeout=t); return r.stdout if raw else r.stdout.decode('utf-8', 'ignore')
def text():
    adb('shell', 'uiautomator', 'dump', '/sdcard/r.xml')
    try: root = ET.fromstring(adb('exec-out', 'cat', '/sdcard/r.xml'))
    except Exception: return []
    return [t for n in root.iter('node') for t in [(n.get('text') or n.get('content-desc') or '').strip()] if t]
out, route, langs = sys.argv[1], sys.argv[2], sys.argv[3:]; os.makedirs(out, exist_ok=True)
for lang in langs:
    adb('shell', 'cmd', 'locale', 'set-app-locales', PKG, '--locales', lang)
    adb('shell', 'input', 'keyevent', 'KEYCODE_WAKEUP'); adb('shell', 'wm', 'dismiss-keyguard')
    adb('shell', 'am', 'force-stop', PKG); adb('shell', 'am', 'start', '-n', ACT, '--es', 'deep_link_route', route); time.sleep(WAIT)
    t = text(); frame = adb('exec-out', 'screencap', raw=True)
    open(f'{out}/{route.replace("/", "_")}_{lang}.raw', 'wb').write(frame)
    print(f'{lang} alive={bool(adb("shell", "pidof", PKG).strip())} |', ' | '.join(t)[:600])
adb('shell', 'cmd', 'locale', 'set-app-locales', PKG, '--locales', '')
