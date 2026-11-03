#!/usr/bin/env python3
"""Merge the per-language files written by translation helpers into one master file.

  merge_pending.py <pending-prefix> <out-name>       e.g.  merge_pending.py pending_m13 m13_level1

Reads docs/l10n/translations/<prefix>_en.json (strings + plurals) and <prefix>_<lang>.json for fr es pt tr ru ja th, checks that every key exists
in every language, then writes docs/l10n/translations/<out-name>.json (the format apply.py reads) and deletes the pending files.
"""
import json, os, sys
D = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..', 'docs', 'l10n', 'translations')
LANGS = ['fr', 'es', 'pt', 'tr', 'ru', 'ja', 'th']
NEED = {'fr': ['one', 'many', 'other'], 'es': ['one', 'many', 'other'], 'pt': ['one', 'many', 'other'], 'tr': ['one', 'other'], 'ru': ['one', 'few', 'many', 'other'], 'ja': ['other'], 'th': ['other']}
prefix, out_name = sys.argv[1], sys.argv[2]
en = json.load(open(os.path.join(D, f'{prefix}_en.json'), encoding='utf-8'))
out, problems = {}, 0
for lang in LANGS:
    path = os.path.join(D, f'{prefix}_{lang}.json')
    if not os.path.exists(path): print('missing file', path); problems += 1; continue
    tr = json.load(open(path, encoding='utf-8'))
    for k in en['strings']:
        v = tr.get('strings', {}).get(k)
        if not isinstance(v, str) or not v.strip(): print(f'{lang}: missing string {k}'); problems += 1; continue
        out.setdefault(k, {})[lang] = v
    for k in en.get('plurals', {}):
        v = tr.get('plurals', {}).get(k)
        if not isinstance(v, dict) or not set(NEED[lang]) <= set(v): print(f'{lang}: plural {k} needs {NEED[lang]}'); problems += 1; continue
        out.setdefault(k, {'plural': {}})['plural'][lang] = {q: v[q] for q in NEED[lang]}
if problems: print('NOT merged:', problems, 'problems'); sys.exit(1)
json.dump(out, open(os.path.join(D, f'{out_name}.json'), 'w', encoding='utf-8'), ensure_ascii=False, indent=1)
for lang in ['en'] + LANGS:
    p = os.path.join(D, f'{prefix}_{lang}.json')
    if os.path.exists(p): os.remove(p)
print('merged', len(out), 'keys ->', out_name + '.json')
