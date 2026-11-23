#!/usr/bin/env python3
"""walk_lang.py <outdir> <wait_s> <lang> [<lang> ...]

For each language, open every route of the existing route walk (plus the debug preview screens) on the Redmi and save, per route:
status (OK / CRASH / ANR / BLANK / OTHERAPP), the foreground activity, and every text node with its bounds (uiautomator).
Two adb calls per route. No images. One JSON per language: <outdir>/<lang>.json, progress lines in <outdir>/progress.log.
"""
import json, os, re, subprocess, sys, time
import xml.etree.ElementTree as ET

D = os.environ.get('PHONE', '1d04811b')
PKG = os.environ.get('PKG', 'com.abacus.puzzle.l10n')
ACT = f'{PKG}/com.jigar.me.ui.view.home.HomeActivity'
HELPER = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'walk_routes.py')

out, WAIT, langs = sys.argv[1], float(sys.argv[2]), sys.argv[3:]
os.makedirs(out, exist_ok=True)

# the route list of the existing walk (everything before it starts using the phone)
_saved = sys.argv
sys.argv = ['x', 'all']
ns = {}
exec(open(HELPER).read().split("only = sys.argv")[0], ns)
sys.argv = _saved
routes = [('deep_link_route', r) for r in ns['routes']]
routes += [('ab_screen', f'onboarding{i}') for i in range(1, 9)]
routes += [('ab_screen', s) for s in ('paywall', 'purchase', 'paywallstore2', 'purchasestore2', 'purchasebought')]
DUMMY = re.compile(r'^(LevelCategory/\d+|AbacusSet/1/Test|AbacusList/1|AbacusDoPractice/(1|99999)/false)$')
if os.environ.get('REAL_IDS'):   # the Practice screens with real ids from the synced data (ids are UUIDs, "1" gives empty lists)
    routes = [r for r in routes if not DUMMY.match(r[1])]
    routes += [('deep_link_route', r) for r in json.load(open(os.environ['REAL_IDS']))]
if os.environ.get('ROUTES_FILE'):   # only these route keys (names, or ab:<screen> for the debug previews)
    keep = set(json.load(open(os.environ['ROUTES_FILE'])))
    routes = [r for r in routes if (r[1] if r[0] == 'deep_link_route' else 'ab:' + r[1]) in keep]
if os.environ.get('ONLY'):   # a quick trial on a few routes
    routes = [r for r in routes if re.search(os.environ['ONLY'], r[1])]


def adb(*a, timeout=40):
    return subprocess.run(['adb', '-s', D, *a], capture_output=True, timeout=timeout).stdout.decode('utf-8', 'ignore')


def log(msg):
    with open(f'{out}/progress.log', 'a') as f:
        f.write(msg + '\n')
    print(msg, flush=True)


def open_route(kind, name):
    adb('shell', f'input keyevent KEYCODE_WAKEUP; wm dismiss-keyguard; am force-stop {PKG}; logcat -c; '
                 f'am start -n {ACT} --es {kind} "{name}" >/dev/null 2>&1')


def read_screen():
    raw = adb('shell',
              f'echo "@@PID:$(pidof {PKG})"; echo "@@TOP:$(dumpsys activity activities | grep topResumedActivity | head -1)"; '
              f'echo "@@ERR:"; logcat -d -t 3000 *:E | grep -E "FATAL EXCEPTION|ANR in {PKG}" -A3 | head -6; '
              f'uiautomator dump /sdcard/w.xml >/dev/null 2>&1; echo "@@XML"; cat /sdcard/w.xml')
    pid = re.search(r'@@PID:(\d*)', raw)
    top = re.search(r'@@TOP:(.*)', raw)
    err = raw.split('@@ERR:')[1].split('@@XML')[0].strip() if '@@ERR:' in raw else ''
    xml = raw.split('@@XML', 1)[1].strip() if '@@XML' in raw else ''
    texts = []
    try:
        root = ET.fromstring(xml)
        for n in root.iter('node'):
            t = (n.get('text') or n.get('content-desc') or '').strip()
            if t:
                texts.append([t, n.get('bounds')])
    except Exception:
        pass
    return (pid.group(1) if pid else ''), (top.group(1).strip() if top else ''), err, texts


for lang in langs:
    adb('shell', 'cmd', 'locale', 'set-app-locales', PKG, '--locales', lang)
    path = f'{out}/{lang}.json'
    results = json.load(open(path)) if os.path.exists(path) else {}   # resume: routes already done are kept
    t0 = time.time()
    log(f'== {lang}: {len(routes)} routes, wait {WAIT}s')
    for kind, name in routes:
        key = name if kind == 'deep_link_route' else 'ab:' + name
        if key in results:
            continue
        for attempt in (1, 2):
            open_route(kind, name)
            time.sleep(WAIT)
            pid, top, err, texts = read_screen()
            status = 'OK'
            if not pid or 'FATAL EXCEPTION' in err:
                status = 'CRASH'
            elif f'ANR in {PKG}' in err:
                status = 'ANR'
            elif PKG not in top:
                status = 'OTHERAPP'
            elif not texts:
                status = 'BLANK'
            if status == 'BLANK' and attempt == 1:   # a slow first frame: give it 4 more seconds, read again
                time.sleep(4)
                pid, top, err, texts = read_screen()
                if texts:
                    status = 'OK'
            if status != 'OTHERAPP':
                break
            time.sleep(2)   # another app took the foreground: try the route once more
        results[key] = {'status': status, 'top': top[-80:], 'err': err[:300], 'texts': texts}
        json.dump(results, open(path, 'w'), ensure_ascii=False)
        if status != 'OK':
            log(f'{lang} {status:8} {key} {err[:100]!r}')
    bad = [k for k, v in results.items() if v['status'] != 'OK']
    log(f'== {lang} done in {int(time.time() - t0)}s, {len(bad)} not OK: {bad}')

adb('shell', 'cmd', 'locale', 'set-app-locales', PKG, '--locales', 'en')
log('ALL DONE')
