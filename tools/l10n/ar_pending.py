#!/usr/bin/env python3
"""Arabic pass (M21): build the English chunks for the Arabic translators, then merge their answers back into the master files.

  ar_pending.py split [size]     writes docs/l10n/translations/pending_ar01_en.json, pending_ar02_en.json ... (default 180 strings each)
                                 for every key of every master file that has no Arabic yet (plurals travel with the first chunks)
  ar_pending.py merge            reads pending_ar<NN>_ar.json (same shape: {"strings": {key: text}, "plurals": {key: {quantity: text}}}),
                                 writes each text into the master file that owns the key (as "ar"), then deletes the pending files
Then run `python3 tools/l10n/apply.py --with-ar` and `check_strings.py`.
"""
import glob, json, os, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from make_pending import english, BASE
T = os.path.join(BASE, 'docs', 'l10n', 'translations')

def masters():
    for f in sorted(glob.glob(os.path.join(T, '*.json'))):
        if not os.path.basename(f).startswith('pending_'): yield f, json.load(open(f, encoding='utf-8'))

def split(size):
    es, ep = english(); strings, plurals = {}, {}
    for f, d in masters():
        for k, v in d.items():
            if not isinstance(v, dict): continue
            if 'plural' in v:
                if 'ar' not in v['plural'] and k in ep: plurals[k] = ep[k]
            elif 'ar' not in v and k in es: strings[k] = es[k]
    items = list(strings.items()); n = 0
    plural_chunks = [dict(list(plurals.items())[i::6]) for i in range(6)]   # spread the plurals over the first chunks
    for i in range(0, len(items), size):
        n += 1
        out = {'strings': dict(items[i:i + size]), 'plurals': plural_chunks[n - 1] if n <= 6 else {}}
        json.dump(out, open(os.path.join(T, f'pending_ar{n:02d}_en.json'), 'w', encoding='utf-8'), ensure_ascii=False, indent=1)
    print(len(strings), 'strings and', len(plurals), 'plurals in', n, 'chunks')

def merge():
    answers_s, answers_p = {}, {}
    files = sorted(glob.glob(os.path.join(T, 'pending_ar??_ar.json')))
    for f in files:
        d = json.load(open(f, encoding='utf-8')); answers_s.update(d.get('strings', {})); answers_p.update(d.get('plurals', {}))
    done = 0
    for f, d in masters():
        changed = False
        for k, v in d.items():
            if not isinstance(v, dict): continue
            if 'plural' in v and k in answers_p: v['plural']['ar'] = answers_p[k]; changed = True; done += 1
            elif 'plural' not in v and k in answers_s: v['ar'] = answers_s[k]; changed = True; done += 1
        if changed: json.dump(d, open(f, 'w', encoding='utf-8'), ensure_ascii=False, indent=1)
    for f in files + glob.glob(os.path.join(T, 'pending_ar??_en.json')): os.remove(f)
    print(done, 'Arabic texts merged from', len(files), 'files')

if __name__ == '__main__':
    if sys.argv[1] == 'split': split(int(sys.argv[2]) if len(sys.argv) > 2 else 180)
    else: merge()
