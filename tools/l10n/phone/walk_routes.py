#!/usr/bin/env python3
"""Open every Android route on the real phone through the debug deep-link extra and report crashes.

For each route: force-stop, clear logcat, start HomeActivity with deep_link_route, wait, then check
  - the process is still alive
  - logcat has no "FATAL EXCEPTION" / "ANR in" for the app
  - the screen has some text (a blank screen is flagged, not failed)
No taps and no images: numbers and one text snippet per route only.
"""
import os, re, subprocess, sys, time, xml.etree.ElementTree as ET

D = '1d04811b'
PKG = os.environ.get('PKG', 'com.abacus.puzzle.l10n')   # the test copy that sits next to the Play app
ACT = f'{PKG}/com.jigar.me.ui.view.home.HomeActivity'
WAIT = float(sys.argv[2]) if len(sys.argv) > 2 else 5.0


def adb(*a, timeout=30):
    return subprocess.run(['adb', '-s', D, *a], capture_output=True, text=True, timeout=timeout).stdout


def screen_text():
    adb('shell', 'uiautomator', 'dump', '/sdcard/w.xml')
    xml = adb('exec-out', 'cat', '/sdcard/w.xml')
    try:
        root = ET.fromstring(xml)
    except Exception:
        return None
    parts = []
    for n in root.iter('node'):
        t = (n.get('text') or n.get('content-desc') or '').strip()
        if t:
            parts.append(t)
    return parts


D4 = ['easy', 'veryHard']
routes = ['Home', 'Onboarding', 'AbacusFreeMode', 'MathGameZone', 'TrophyRoom', 'Settings', 'Purchase', 'YoutubeVideo',
          'MyAccount', 'ReportHistory', 'FAQs', 'Exercise', 'ExamHome', 'ExamPlay/false', 'CCMHome', 'CCMPlay',
          'CredentialsLoginLandscape']
routes += [f'LevelCategory/{i}' for i in (1, 2, 99)]
routes += ['AbacusSet/1/Test', 'AbacusList/1', 'AbacusDoPractice/1/false', 'AbacusDoPractice/99999/false']
for g in ['NumberSequencePuzzle', 'Sudoku', 'MathPyramid', 'TargetNumber', 'BalloonPop', 'SpeedCompare', 'MissingOperator',
          'MagicSquare', 'Calcudoku', 'Merge2048', 'EquationMatch', 'CrossMath', 'NumberPath', 'TrueFalse', 'PlaceValue',
          'ClockMaster', 'MathBingo', 'Kakuro', 'NumberDetective', 'NumberSnake']:
    routes.append(f'{g}Home')
routes += [f'NumberSequencePuzzlePlay/{t}' for t in (0, 1, 2)]
routes += ['SudokuPlay/FOUR/EASY/true', 'SudokuPlay/FOUR/EASY/false', 'SudokuPlay/SIX/MEDIUM/true', 'SudokuPlay/SIX/HARD/false',
           'SudokuPlay/NINE/HARD/true', 'SudokuPlay/NINE/VERY_HARD/false', 'SudokuPlay/NINE/bogus/true']
routes += ['MathPyramidPlay/3/easy', 'MathPyramidPlay/5/veryHard', 'TargetNumberPlay/1/easy', 'TargetNumberPlay/3/veryHard',
           'SpeedCompareDuel/easy', 'SpeedCompareDuel/veryHard']
for g in ['BalloonPopPlay', 'SpeedComparePlay', 'MissingOperatorPlay', 'MagicSquarePlay', 'CalcudokuPlay', 'Merge2048Play',
          'EquationMatchPlay', 'TrueFalsePlay', 'PlaceValuePlay', 'ClockMasterPlay', 'MathBingoPlay', 'NumberDetectivePlay',
          'NumberSnakePlay']:
    routes += [f'{g}/{d}' for d in D4]
for g in ['CrossMathPlay', 'NumberPathPlay', 'KakuroPlay']:
    routes += [f'{g}/easy/1', f'{g}/hard/3']
routes += ['Level1Home'] + [f'Level1{k}/{i}' for k in ('Lesson', 'Learn', 'Practice', 'Quiz') for i in (1, 4, 12)]
routes += ['Level2Home', 'Level2FormulaRef/1', 'Level2FormulaRef/3'] + \
          [f'Level2{k}/{i}' for k in ('Lesson', 'Learn', 'Practice', 'Quiz') for i in (1, 5, 10, 99)]
routes += ['Level3Home', 'Level3Config/0', 'Level3Config/2', 'Level3Guided/3/1/1000/true', 'Level3Guided/5/2/500/false',
           'Level3Anzan/0/3/1/1000', 'Level3Anzan/2/5/2/500', 'Level3SpeedDrill/1/60', 'Level3FlashPicker', 'Level3FlashPlay/0',
           'Level3FlashPlay/3']
routes += ['Level4TablePicker', 'TableMixPicker', 'TodayTableHome/2', 'TodayTableDrill/2', 'TodayTableFlashcard/2',
           'TodayTableFillBlank/2', 'TodayTableFlashcard/12', 'TodayTableFillBlank/12', 'TableMixHome/2,3', 'TableMixDrill/2,3',
           'TableMixFlashcard/2,3,4', 'TableMixFillBlank/2,3,4,5', 'NotARealRoute/1']

only = sys.argv[1] if len(sys.argv) > 1 and sys.argv[1] != 'all' else None
if only:
    routes = [r for r in routes if re.search(only, r)]

adb('shell', 'input', 'keyevent', 'KEYCODE_WAKEUP')
adb('shell', 'wm', 'dismiss-keyguard')
bad, blank = [], []
print(f'{len(routes)} routes, wait {WAIT}s each')
for r in routes:
    adb('shell', 'input', 'keyevent', 'KEYCODE_WAKEUP')   # the phone's screen times out between routes
    adb('shell', 'wm', 'dismiss-keyguard')
    adb('shell', 'am', 'force-stop', PKG)
    adb('logcat', '-c')
    adb('shell', 'am', 'start', '-n', ACT, '--es', 'deep_link_route', r)
    time.sleep(WAIT)
    alive = bool(adb('shell', 'pidof', PKG).strip())
    log = adb('logcat', '-d', '-t', '4000', '*:E')
    fatal = re.search(r'FATAL EXCEPTION.*?(?=\n[A-Z] [^\n]*AndroidRuntime: \s*$|\Z)', log, re.S)
    anr = 'ANR in ' + PKG in log
    parts = screen_text() if alive else []
    snippet = ' | '.join((parts or [])[:5])[:70]
    status = 'OK'
    if not alive or fatal or anr:
        status = 'CRASH' if (fatal or not alive) else 'ANR'
        bad.append(r)
        m = re.search(r'FATAL EXCEPTION[^\n]*\n(?:[^\n]*AndroidRuntime: )?([^\n]*)\n(?:[^\n]*AndroidRuntime: )?([^\n]*)', log)
        snippet = (m.group(1) + ' ' + m.group(2))[:160] if m else 'process gone'
    elif not parts:
        status = 'BLANK?'
        blank.append(r)
    print(f'{status:7} {r:42} {snippet}', flush=True)

print(f'\nDONE: {len(routes)} routes, {len(bad)} crashed/ANR, {len(blank)} blank')
for r in bad:
    print('  CRASH', r)
for r in blank:
    print('  BLANK', r)
