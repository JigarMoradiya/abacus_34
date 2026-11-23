#!/usr/bin/env python3
"""List the strings of values/strings.xml that a module's Kotlin files use and that have no translation yet.

  module_keys.py <module> [<module> ...]        keys used by those folders (names as in scan_static.py MODULES, or a path under app/src/main/java/com/jigar/me)
  module_keys.py <module> --all                 also the ones already translated
  module_keys.py <module> --json                print a ready-to-fill master file skeleton (English as a comment-less value list)
A key counts as used when R.string.<key> or R.plurals.<key> appears in a .kt file of the folder (how_to_play is skipped for common_ui).
"""
import glob, json, os, re, sys
import xml.etree.ElementTree as ET
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from scan_static import MODULES, ROOT

BASE = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..')
SRC = os.path.join(BASE, 'app', 'src', 'main', 'res', 'values', 'strings.xml')
USED = re.compile(r'R\.(?:string|plurals)\.([A-Za-z0-9_]+)')

def english():
    out = {}
    for el in ET.parse(SRC).getroot():
        n = el.get('name')
        if n and el.get('translatable') != 'false' and el.tag in ('string', 'plurals'):
            out[n] = (el.tag, ''.join(el.itertext()) if el.tag == 'string' else ' / '.join(i.text or '' for i in el))
    return out

def translated():
    done = set()
    for f in glob.glob(os.path.join(BASE, 'docs', 'l10n', 'translations', '*.json')):
        done |= set(json.load(open(f, encoding='utf-8')))
    return done

def main():
    args = [a for a in sys.argv[1:] if not a.startswith('--')]
    folders = [MODULES.get(a, a) for a in args]
    eng, done = english(), translated()
    used = {}
    for folder in folders:
        for path in glob.glob(os.path.join(ROOT, folder, '**', '*.kt'), recursive=True):
            if folder == MODULES['common_ui'] and 'how_to_play' in path: continue
            for k in USED.findall(open(path, encoding='utf-8', errors='ignore').read()):
                used.setdefault(k, set()).add(os.path.basename(path))
    show = [k for k in sorted(used) if k in eng and (k not in done or '--all' in sys.argv)]
    missing = [k for k in sorted(used) if k not in eng]
    if '--json' in sys.argv:
        print(json.dumps({k: {} for k in show}, ensure_ascii=False, indent=1)); return
    for k in show: print(f'{k:55s} {eng[k][1][:90]!r}')
    print(f'\n{len(show)} keys to translate ({len([k for k in used if k in done])} already done, {len(missing)} not in strings.xml: {missing[:6]})')
if __name__ == '__main__': main()
