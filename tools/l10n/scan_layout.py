#!/usr/bin/env python3
"""List layout code that can break when text gets longer or the reading direction flips (Arabic, module M21).

  scan_layout.py <path-or-folder> [...]      one line per finding: file:line  KIND  code

Kinds:
  ABS-LR     absolute left/right (absolutePadding, Arrangement.Absolute, TextAlign.Left/Right, Alignment.*Left/Right, padding(left/right)
             in view code): does not mirror, use start/end. (Abacus drawing and math are meant to stay left to right: those are fine
             when wrapped in LtrIsland, the scan cannot tell, a person decides.)
  ICON-DIR   a directional icon that is not the AutoMirrored version (arrows, chevrons, back, forward).
  OFFSET-X   .offset(x = ..) / Modifier.absoluteOffset: moves the same way in both directions.
  FIXED-W    a fixed .width(..) / .size(..) on something that holds Text with maxLines = 1 on the same line: a longer translation clips.
  LAYOUT-DIR code that already reads LocalLayoutDirection / layoutDirection (check it is right).
"""
import os, re, sys
RULES = [
    ('ABS-LR', re.compile(r'absolutePadding|Arrangement\.Absolute|TextAlign\.(Left|Right)\b|Alignment\.(CenterLeft|CenterRight|TopLeft|TopRight|BottomLeft|BottomRight)\b|\bpadding\(\s*(left|right)\s*=')),
    ('ICON-DIR', re.compile(r'Icons\.(Default|Filled|Outlined|Rounded)\.(ArrowBack|ArrowForward|KeyboardArrowLeft|KeyboardArrowRight|ChevronLeft|ChevronRight|ArrowLeft|ArrowRight|NavigateNext|NavigateBefore|ArrowBackIos|ArrowForwardIos)\b')),
    ('OFFSET-X', re.compile(r'\.(absoluteOffset|offset)\(\s*(x\s*=|\{)|Modifier\.offset\(\s*[-\w.]+\s*,')),
    ('FIXED-W', re.compile(r'\.(width|requiredWidth)\([^)]*\).*maxLines\s*=\s*1|maxLines\s*=\s*1.*\.(width|requiredWidth)\(')),
    ('LAYOUT-DIR', re.compile(r'LocalLayoutDirection|layoutDirection')),
]
def files(paths):
    for p in paths:
        if os.path.isfile(p): yield p
        for root, _, names in os.walk(p):
            for n in names:
                if n.endswith('.kt'): yield os.path.join(root, n)
def main():
    if len(sys.argv) < 2: print(__doc__); return
    counts = {}
    for f in files(sys.argv[1:]):
        for i, line in enumerate(open(f, encoding='utf-8', errors='ignore'), 1):
            if line.lstrip().startswith(('//', '*', '/*', 'import ')): continue
            for kind, rx in RULES:
                if rx.search(line):
                    counts[kind] = counts.get(kind, 0) + 1
                    print(f'{f}:{i}  {kind:10s}  {line.strip()[:110]}')
    print('total:', ', '.join(f'{k}={v}' for k, v in sorted(counts.items())) or 'nothing found')
if __name__ == '__main__': main()
