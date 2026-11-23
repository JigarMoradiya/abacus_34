#!/usr/bin/env python3
"""Keep math inside Arabic sentences left to right.

In a right-to-left paragraph "(1–9)" or "5 + 1 = 6" is shown reversed, because the signs between digits have no direction of their
own. This wraps every such fragment of the Arabic texts in LRE ... PDF (U+202A ... U+202C), which old and new Android versions
both understand. A fragment is two or more numbers (digits, %1$d placeholders, "...") joined by + − × ÷ = < > – - / : , and spaces.
Run it after merging Arabic texts: ar_bidi.py [--dry]. It is idempotent (fragments that already have the mark are skipped).
"""
import glob, json, os, re, sys
BASE = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..')
T = os.path.join(BASE, 'docs', 'l10n', 'translations')
ATOM = r'(?:[+−\-]?\(?(?:\d+(?:\.\d+)?|%\d?\$?[ds]|N|\.\.\.)\)?)'
OP = r'(?:\s*[+−×÷=<>≤≥–\-/:,|]\s*)'
RUN = re.compile(ATOM + r'(?:' + OP + ATOM + r')+')
LRE, PDF = '‪', '‬'

def fix(text):
    out, last = [], 0
    for m in RUN.finditer(text):
        s, e = m.start(), m.end()
        frag = m.group(0)
        if not re.search(r'[+−×÷=<>≤≥–/:|]|\d\s*-\s*\d|,', frag): continue
        if text[max(0, s - 1):s] == LRE: continue
        out.append(text[last:s]); out.append(LRE + frag.strip() + PDF + (' ' if frag.endswith(' ') else '')); last = e
    out.append(text[last:])
    return ''.join(out)

def main():
    n = 0
    for f in sorted(glob.glob(os.path.join(T, '*.json'))):
        if os.path.basename(f).startswith('pending_'): continue
        d = json.load(open(f, encoding='utf-8')); ch = False
        for k, v in d.items():
            if not isinstance(v, dict): continue
            if 'plural' in v:
                for q, t in list(v['plural'].get('ar', {}).items()):
                    t2 = fix(t)
                    if t2 != t: v['plural']['ar'][q] = t2; ch = True; n += 1
            elif 'ar' in v:
                t2 = fix(v['ar'])
                if t2 != v['ar']:
                    if '--dry' in sys.argv and n < 12: print(k, repr(v['ar'][:80]), '->', repr(t2[:90]))
                    v['ar'] = t2; ch = True; n += 1
        if ch and '--dry' not in sys.argv: json.dump(d, open(f, 'w', encoding='utf-8'), ensure_ascii=False, indent=1)
    print(n, 'Arabic texts changed')
if __name__ == '__main__': main()
