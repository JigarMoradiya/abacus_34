#!/usr/bin/env python3
"""game_walk.py <out.json> <langs comma list> <route> [<route> ...] [--howto]

Opens every route on the phone in English and in each language, dumps the screen text (and, with --howto, taps the "How to play" pill at the
top right and dumps the dialog text too), then prints the lines that are still English:
  - in ru / ja / th / fr / es / pt / tr: a line that is identical to the English line of the same screen (digits and symbols aside)
  - in ru / ja / th: a line with a word of 4+ Latin letters that is not a known name (Sudoku, Kakuro, Anzan, BINGO ...).
Needs the l10n test copy installed (com.abacus.puzzle.l10n) and the phone 1d04811b. Saves every dump to <out.json>.
"""
import json, os, re, subprocess, sys, time, xml.etree.ElementTree as ET
D = os.environ.get('PHONE', '1d04811b'); PKG = os.environ.get('PKG', 'com.abacus.puzzle.l10n'); ACT = f'{PKG}/com.jigar.me.ui.view.home.HomeActivity'
WAIT = float(os.environ.get('WAIT', '7'))
KNOWN = re.compile(r'\b(Sudoku|Kakuro|Calcudoku|Anzan|BINGO|Bingo|Abacus|Flash|Pop|Mix|Merge|Cross|Math|FREE|Vedaavi|OK|Anzan)\b')

def adb(*a, raw=False, t=60):
    r = subprocess.run(['adb', '-s', D, *a], capture_output=True, timeout=t); return r.stdout if raw else r.stdout.decode('utf-8', 'ignore')

def nodes():
    adb('shell', 'uiautomator', 'dump', '/sdcard/r.xml')
    try: root = ET.fromstring(adb('exec-out', 'cat', '/sdcard/r.xml'))
    except Exception: return []
    out = []
    for n in root.iter('node'):
        t = (n.get('text') or n.get('content-desc') or '').strip()
        b = re.findall(r'\d+', n.get('bounds') or '')
        out.append((t, n.get('clickable') == 'true', [int(x) for x in b] if len(b) == 4 else None))
    return out

def texts(ns): return [t for t, _, _ in ns if t]

def launch(route, lang):
    adb('shell', 'cmd', 'locale', 'set-app-locales', PKG, '--locales', lang)
    adb('shell', 'input', 'keyevent', 'KEYCODE_WAKEUP'); adb('shell', 'wm', 'dismiss-keyguard')
    adb('shell', 'am', 'force-stop', PKG); adb('shell', 'am', 'start', '-n', ACT, '--es', 'deep_link_route', route); time.sleep(WAIT)

def tap_howto():
    for t, click, b in nodes():
        if click and b and b[0] > 1800 and b[3] < 260:      # the pill at the top right of a game home
            adb('shell', 'input', 'tap', str((b[0] + b[2]) // 2), str((b[1] + b[3]) // 2)); time.sleep(2.5); return True
    return False

def main():
    out, langs, rest = sys.argv[1], sys.argv[2].split(','), sys.argv[3:]
    howto = '--howto' in rest; routes = [r for r in rest if not r.startswith('--')]
    data = json.load(open(out)) if os.path.exists(out) else {}
    for route in routes:
        data.setdefault(route, {})
        for lang in ['en'] + langs:
            launch(route, lang)
            main_t = texts(nodes())
            how_t = []
            if howto and tap_howto(): how_t = texts(nodes())
            data[route][lang] = {'screen': main_t, 'howto': how_t}
            print(f'{route} {lang}: {len(main_t)} lines, how-to {len(how_t)} lines', flush=True)
        json.dump(data, open(out, 'w'), ensure_ascii=False, indent=1)
    adb('shell', 'cmd', 'locale', 'set-app-locales', PKG, '--locales', '')
    report(data, langs)

def report(data, langs):
    for route, per in data.items():
        en = per.get('en', {})
        for lang in langs:
            if lang not in per: continue
            for part in ('screen', 'howto'):
                same, latin = [], []
                for line in per[lang][part]:
                    letters = re.sub(r'[^A-Za-z]', '', line)
                    if len(letters) < 4: continue
                    if line in en.get(part, []): same.append(line)
                    elif lang in ('ru', 'ja', 'th') and re.search(r'[A-Za-z]{4,}', KNOWN.sub('', line)): latin.append(line)
                for l in same: print(f'ENGLISH  {route} {lang} {part}: {l[:100]}')
                for l in latin: print(f'LATIN    {route} {lang} {part}: {l[:100]}')

if __name__ == '__main__':
    if sys.argv[1] == '--report': report(json.load(open(sys.argv[2])), sys.argv[3].split(','))
    else: main()
