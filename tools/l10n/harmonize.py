#!/usr/bin/env python3
"""Make identical English texts read identically in every language.

  harmonize.py <master.json> [<master.json> ...] [--dry]

Translators of different batches phrase the same heading ("👉 How it works:") a little differently. For every group of keys of
the given masters whose English text is identical, the most frequent translation per language is written to all keys of the group
(ties: the one of the first key). Only plain strings are touched, and only for keys with 2+ members in the group.
"""
import collections, glob, json, os, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from make_pending import english, BASE

def main():
    files = [a for a in sys.argv[1:] if not a.startswith('--')]
    files = [f if os.path.isabs(f) else os.path.join(BASE, 'docs', 'l10n', 'translations', f) for f in files]
    eng, _ = english()
    masters = {f: json.load(open(f, encoding='utf-8')) for f in files}
    groups = collections.defaultdict(list)
    for f, d in masters.items():
        for k, v in d.items():
            if k in eng and isinstance(v, dict) and 'plural' not in v and len(eng[k]) >= 4: groups[eng[k]].append((f, k))
    changed = 0
    for text, members in groups.items():
        if len(members) < 2: continue
        for lang in ('fr', 'es', 'pt', 'tr', 'ru', 'ja', 'th'):
            vals = [masters[f][k].get(lang) for f, k in members if masters[f][k].get(lang)]
            if len(set(vals)) < 2: continue
            best = collections.Counter(vals).most_common(1)[0][0]
            if collections.Counter(vals).most_common(2)[0][1] == collections.Counter(vals).most_common(2)[1][1]: best = vals[0]
            for f, k in members:
                if masters[f][k].get(lang) != best:
                    masters[f][k][lang] = best; changed += 1
    print(changed, 'values unified')
    if '--dry' not in sys.argv:
        for f, d in masters.items(): json.dump(d, open(f, 'w', encoding='utf-8'), ensure_ascii=False, indent=1)

if __name__ == '__main__': main()
