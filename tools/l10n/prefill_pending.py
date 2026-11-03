#!/usr/bin/env python3
"""Reuse existing translations for pending strings whose English text is identical to an already translated string.

  prefill_pending.py <name>      reads  docs/l10n/translations/pending_<name>_en.json
                                 writes docs/l10n/translations/prefill_<name>.json (a master file for apply.py)
                                 and removes those keys from pending_<name>_en.json (the translators do not see them again)
Only plain strings are matched (not plurals), the English must be 4+ characters and identical, and the translated source key
must be in a master file. Keys of the same module that appear in several languages are copied exactly.
"""
import glob, json, os, re, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from make_pending import english, BASE

def main(name):
    tdir = os.path.join(BASE, 'docs', 'l10n', 'translations')
    pend_path = os.path.join(tdir, f'pending_{name}_en.json')
    pend = json.load(open(pend_path, encoding='utf-8'))
    eng, _ = english()
    index = {}
    for f in sorted(glob.glob(os.path.join(tdir, '*.json'))):
        b = os.path.basename(f)
        if b.startswith('pending_') or b == f'prefill_{name}.json': continue
        for k, v in json.load(open(f, encoding='utf-8')).items():
            if k == 'plural' or not isinstance(v, dict) or 'plural' in v or k not in eng: continue
            if len(v) >= 7: index.setdefault(eng[k], v)
    got = {}
    for k, t in list(pend['strings'].items()):
        if len(t) >= 4 and t in index and not re.search(r'%\d?\$?[sd]', t):
            got[k] = index[t]; del pend['strings'][k]
    json.dump(got, open(os.path.join(tdir, f'prefill_{name}.json'), 'w', encoding='utf-8'), ensure_ascii=False, indent=1)
    json.dump(pend, open(pend_path, 'w', encoding='utf-8'), ensure_ascii=False, indent=1)
    print(f'{len(got)} reused, {len(pend["strings"])} strings and {len(pend["plurals"])} plurals left for the translators')

if __name__ == '__main__': main(sys.argv[1])
