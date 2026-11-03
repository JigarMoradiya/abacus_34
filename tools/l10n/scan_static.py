#!/usr/bin/env python3
"""Find user-visible text that is stored as a static / constant in Kotlin, or hard-coded in code.

Rule (owner, 2026-11-03): visible text must live in strings.xml. A class keeps only a @StringRes id; the text is resolved
when it is drawn. Static values are fixed when the class loads and would not follow a language change.

  scan_static.py                      -> static texts (const val, object/companion/top-level val, enum args, defaults)
  scan_static.py --kind literal       -> hard-coded visible literals inside functions / composables
  scan_static.py --module home        -> only one module (see MODULES)
  scan_static.py --summary            -> counts per module only
Exit code 1 when something is found for the chosen filter (so a module can be "done" = scanner clean).
Texts that are identifiers or sent to the server are skipped by the heuristics; review the output, it is a finder, not a judge.
"""
import argparse, os, re, sys

ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..', 'app', 'src', 'main', 'java', 'com', 'jigar', 'me')
MODULES = {
    'home': 'ui/view/home/screens/home', 'settings': 'ui/view/home/screens/settings', 'my_account': 'ui/view/home/screens/my_account',
    'reports': 'ui/view/home/screens/reports', 'faqs': 'ui/view/home/screens/faqs', 'whats_learning': 'ui/view/home/screens/whats_learning',
    'youtube': 'ui/view/home/screens/youtube', 'onboarding': 'ui/view/home/screens/onboarding', 'purchase': 'ui/view/home/screens/purchase',
    'free_mode': 'ui/view/home/screens/abacus_free_mode', 'practice': 'ui/view/home/screens/abacus_practice',
    'exam': 'ui/view/home/screens/activities/exam', 'exercise': 'ui/view/home/screens/activities/exercise', 'ccm': 'ui/view/home/screens/activities/ccm',
    'level1': 'ui/view/home/screens/levels/level1', 'level2': 'ui/view/home/screens/levels/level2', 'level3': 'ui/view/home/screens/levels/level3',
    'level4': 'ui/view/home/screens/levels/level4', 'games': 'ui/view/home/screens/math_game_zone', 'how_to_play': 'ui/view/home/common_ui/how_to_play',
    'common_ui': 'ui/view/home/common_ui', 'ab_kit': 'ui/view/ab', 'base': 'ui/view/base', 'login': 'ui/view/login', 'other': 'ui/view/other',
    'utils': 'utils', 'data': 'data/local/data',
}
STR = re.compile(r'"((?:[^"\\\n]|\\.)*)"')   # every literal first, short ones are dropped by visible()
SKIP = re.compile(r'^(https?:|[a-z0-9_.\-/]+$|[A-Z0-9_]{3,}$|[a-z]+[A-Z]\w*$|\W+$|SELECT |INSERT |UPDATE |DELETE |CREATE |yyyy|HH:|#?[0-9A-Fa-f]{6,8}$|%[0-9.$]*[sdf]$|[A-Za-z]+\.[A-Za-z.]+$)')
def visible(v):
    if SKIP.match(v): return False
    return bool(re.search(r'[A-Za-z]{3,}', v)) and (' ' in v or v[0].isupper()) and not re.fullmatch(r'[a-z_]+', v)

def classify(text):
    """Return a list of (line_no, kind, snippet) found in one Kotlin file."""
    out, stack = [], []          # stack of scope kinds: class/object/enum/fun/other
    in_block_comment = False
    for i, raw in enumerate(text.splitlines(), 1):
        line = raw.strip()
        if in_block_comment:
            if '*/' in line: in_block_comment = False
            continue
        if line.startswith('/*'):
            if '*/' not in line: in_block_comment = True
            continue
        if not line or line.startswith('//') or line.startswith('*') or line.startswith('import ') or line.startswith('package ') or line.startswith('@'):
            continue
        code = re.sub(r'//.*$', '', line)
        strs = [m.group(1) for m in STR.finditer(code) if visible(m.group(1))]
        # decide the scope this line belongs to
        top = stack[-1] if stack else 'file'
        is_decl_val = re.match(r'^(private |internal |public |protected )?(const )?(val|var) \w+(\s*:\s*[\w<>?,\s]+)?\s*(=|by)', code) is not None
        is_enum_entry = top == 'enum' and re.match(r'^[A-Z][A-Z0-9_]*\s*(\(|,|;|$)', code) is not None
        is_default_param = re.search(r'\b(val|var) \w+\s*:\s*String\??\s*=\s*"', code) is not None
        if strs and top != 'fun' and 'log' not in code.lower() and 'route' not in code.lower():
            if 'const val' in code and ('"' in code): kind = 'static: const val'
            elif is_enum_entry: kind = 'static: enum text'
            elif is_default_param and top in ('class', 'file'): kind = 'static: default value'
            elif is_decl_val and top in ('object', 'file'): kind = 'static: object/top-level val'
            elif is_decl_val and top == 'class' and re.search(r'listOf|mapOf|arrayOf|arrayListOf', code): kind = 'static: list/map field'
            elif top in ('object', 'enum', 'file') and re.search(r'listOf|mapOf|arrayOf|\bto\b', code): kind = 'static: list/map field'
            else: kind = None
            if kind: out.append((i, kind, code[:140]))
        elif strs and top == 'fun':
            out.append((i, 'literal in code', code[:140]))
        # update scope stack from braces on this line (simple but good enough for a finder)
        opens, closes = code.count('{'), code.count('}')
        if opens:
            if re.search(r'\bcompanion object\b|\bobject\b', code): kind_new = 'object'
            elif re.search(r'\benum class\b', code): kind_new = 'enum'
            elif re.search(r'\b(class|interface)\b', code): kind_new = 'class'
            elif re.search(r'\bfun\b', code) or top in ('fun', 'other'): kind_new = 'fun'
            else: kind_new = 'other' if top in ('fun', 'other') else top if top in ('object', 'enum', 'class') else 'other'
            for _ in range(opens): stack.append(kind_new)
        for _ in range(closes):
            if stack: stack.pop()
    return out

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--kind', choices=['static', 'literal'], default='static')
    ap.add_argument('--module'); ap.add_argument('--summary', action='store_true')
    a = ap.parse_args()
    mods = {a.module: MODULES[a.module]} if a.module else MODULES
    found_total, rows = 0, []
    for name, rel in mods.items():
        base = os.path.normpath(os.path.join(ROOT, rel))
        n = 0
        for r, _, fs in os.walk(base):
            for f in sorted(fs):
                if not f.endswith('.kt'): continue
                p = os.path.join(r, f)
                for ln, kind, snip in classify(open(p, encoding='utf-8', errors='ignore').read()):
                    if (a.kind == 'static') == kind.startswith('static'):
                        n += 1
                        if not a.summary: print(f'{name:12} {os.path.relpath(p, ROOT)}:{ln} [{kind}] {snip}')
        rows.append((name, n)); found_total += n
    print('\nsummary (%s texts per module):' % a.kind)
    for name, n in rows: print(f'  {name:14} {n}')
    print(f'  TOTAL          {found_total}')
    sys.exit(1 if found_total else 0)

if __name__ == '__main__':
    main()
