#!/usr/bin/env python3
"""howto_shots.py <outdir> <lang> <Route> [<Route> ...]  -> raw frames outdir/<Route>_<lang>_home.raw and outdir/<Route>_<lang>_howto.raw
Opens each game home, saves a frame, taps the "How to play" pill (top right) and saves a second frame."""
import os, sys, time
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import game_walk as g
out, lang, routes = sys.argv[1], sys.argv[2], sys.argv[3:]; os.makedirs(out, exist_ok=True)
for r in routes:
    g.launch(r, lang)
    open(f'{out}/{r}_{lang}_home.raw', 'wb').write(g.adb('exec-out', 'screencap', raw=True))
    if g.tap_howto(): open(f'{out}/{r}_{lang}_howto.raw', 'wb').write(g.adb('exec-out', 'screencap', raw=True))
    print(r, flush=True)
g.adb('shell', 'cmd', 'locale', 'set-app-locales', g.PKG, '--locales', '')
