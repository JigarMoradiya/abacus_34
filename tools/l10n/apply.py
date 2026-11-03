#!/usr/bin/env python3
"""Write the translation files from the master files in docs/l10n/translations/*.json.

Each master file is one module: {"key": {"fr": "...", "es": "...", ...}} for a normal string, or
{"key": {"plural": {"fr": {"one": "...", "many": "...", "other": "..."}, ...}}} for plurals.
The English text stays in res/values/strings.xml (the source). This script writes res/values-XX/strings.xml for every language that has
at least one text, adding Android's escaping (apostrophes, ampersands, angle brackets, leading @ or ?). Re-running is safe: the files
are rebuilt from the masters, so change the master, never the generated file.

  apply.py            write all left-to-right languages (fr es pt tr ru ja th); Arabic is added only with --with-ar (RTL pass, M21)
  apply.py --check    only report keys that are missing in res/values/strings.xml (no files written)
"""
import glob, json, os, re, sys
import xml.etree.ElementTree as ET

BASE = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..')
RES = os.path.join(BASE, 'app', 'src', 'main', 'res')
MASTERS = sorted(glob.glob(os.path.join(BASE, 'docs', 'l10n', 'translations', '*.json')))
LANGS = ['fr', 'es', 'pt', 'tr', 'ru', 'ja', 'th'] + (['ar'] if '--with-ar' in sys.argv else [])   # Arabic (right to left) waits for the RTL pass, M21

def esc(t):
    t = t.replace('\\', '\\\\').replace('&', '&amp;').replace('<', '&lt;').replace("'", "\\'").replace('"', '\\"').replace('\n', '\\n')
    if t.startswith('@') or t.startswith('?'): t = '\\' + t
    return t

def raw(t):
    """Texts that already contain markup (<b>..</b>) are written as given, only apostrophes and quotes are escaped."""
    return t.replace("'", "\\'").replace('"', '\\"').replace('\n', '\\n')

def main():
    root = ET.parse(os.path.join(RES, 'values', 'strings.xml')).getroot()
    source = {el.get('name'): el.tag for el in root if el.get('name')}
    styled = {el.get('name') for el in root if el.get('name') and el.tag == 'string' and len(list(el)) > 0}   # English has real <b> tags: write real tags
    data = {lang: [] for lang in LANGS}; problems = 0; seen = {}
    for path in MASTERS:
        module = os.path.basename(path)[:-5]
        for key, entry in json.load(open(path, encoding='utf-8')).items():
            if key not in source: print(f'{module}: key "{key}" is not in res/values/strings.xml'); problems += 1; continue
            if key in seen: print(f'{module}: key "{key}" is already translated in {seen[key]}'); problems += 1; continue
            seen[key] = module
            for lang in LANGS:
                if 'plural' in entry:
                    if lang in entry['plural']: data[lang].append((module, key, entry['plural'][lang]))
                elif lang in entry: data[lang].append((module, key, entry[lang]))
    if '--check' in sys.argv or problems:
        print('problems:', problems); sys.exit(1 if problems else 0)
    for lang, rows in data.items():
        if not rows: continue
        out = ['<?xml version="1.0" encoding="utf-8"?>', '<resources>']
        last = None
        for module, key, val in rows:
            if module != last: out.append(f'    <!-- {module} -->'); last = module
            if isinstance(val, dict):
                out.append(f'    <plurals name="{key}">')
                for q, t in val.items(): out.append(f'        <item quantity="{q}">{raw(t) if "<" in t and key in styled else esc(t)}</item>')
                out.append('    </plurals>')
            else:
                out.append(f'    <string name="{key}">{raw(val) if key in styled else esc(val)}</string>')
        out.append('</resources>')
        d = os.path.join(RES, f'values-{lang}'); os.makedirs(d, exist_ok=True)
        open(os.path.join(d, 'strings.xml'), 'w', encoding='utf-8').write('\n'.join(out) + '\n')
        print(f'values-{lang}/strings.xml: {len(rows)} entries')

if __name__ == '__main__':
    main()
