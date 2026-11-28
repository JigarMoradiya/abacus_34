#!/usr/bin/env python3
"""Add NEW languages to the existing master files (docs/l10n/translations/*.json), with a translator + reviewer step (docs/l10n/NEWLANGS.md).

  newlang.py make [--size 220]         write docs/l10n/translations/pending_nl_NN_en.json: every master entry grouped into chunks of about
                                       <size> entries (masters are never split unless one is bigger than the size)
  newlang.py merge [--chunks 01,02] <lang> [<lang> ...]   read pending_nl_NN_<lang>.json (translator) and pending_nl_NN_<lang>_rev.json (reviewer, only the
                                       changed keys, wins over the translator) for every chunk, check them, add the language to the masters;
                                       the reviewer notes go to docs/l10n/review_log/nl_<lang>.json. Safe to run again.
  newlang.py status [<lang> ...]       how many entries each master has per language
  newlang.py cleanup                   delete the pending_nl_* files (after every language is merged)
"""
import glob, json, os, re, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from make_pending import english, BASE

D = os.path.join(BASE, 'docs', 'l10n', 'translations')
LOG = os.path.join(BASE, 'docs', 'l10n', 'review_log')
NEED = {'de': ['one', 'other'], 'id': ['other'], 'vi': ['other'], 'zh': ['other'], 'ko': ['other'], 'hi': ['one', 'other']}
PH = re.compile(r'%(?:\d+\$)?[-#+ 0,(]*\d*(?:\.\d+)?[sdfxXeEgGcb%n]')
TAGS = re.compile(r'</?(?:b|i|u|font|br|a|small|big|strong|em)\b[^>]*>', re.I)
EMOJI = re.compile('[\U0001F000-\U0001FAFF☀-➿⬀-⯿←-⇿⌀-⏿️]')

def natural(p):
    return [int(x) if x.isdigit() else x for x in re.split(r'(\d+)', os.path.basename(p))]

def masters():
    return sorted((f for f in glob.glob(os.path.join(D, '*.json')) if not os.path.basename(f).startswith('pending_')), key=natural)

def load(path):
    return json.load(open(path, encoding='utf-8'))

def save(path, data, newline='\n'):
    open(path, 'w', encoding='utf-8').write(json.dumps(data, ensure_ascii=False, indent=1) + newline)

def make(size):
    eng_s, eng_p = english()
    chunks, cur_s, cur_p, cur_c, cur_n = [], {}, {}, {}, 0
    def close():
        nonlocal cur_s, cur_p, cur_c, cur_n
        if cur_n: chunks.append({'strings': cur_s, 'plurals': cur_p, 'context': cur_c})
        cur_s, cur_p, cur_c, cur_n = {}, {}, {}, 0
    for path in masters():
        module = os.path.basename(path)[:-5]
        keys = [k for k in load(path) if k in eng_s or k in eng_p]
        if cur_n and cur_n + len(keys) > size: close()
        for k in keys:
            if cur_n >= size: close()
            if k in eng_p: cur_p[k] = eng_p[k]
            else: cur_s[k] = eng_s[k]
            cur_c[k] = module; cur_n += 1
    close()
    for old in glob.glob(os.path.join(D, 'pending_nl_*_en.json')): os.remove(old)
    for i, c in enumerate(chunks, 1):
        save(os.path.join(D, f'pending_nl_{i:02d}_en.json'), c)
        print(f'pending_nl_{i:02d}_en.json: {len(c["strings"])} strings, {len(c["plurals"])} plurals, modules {sorted(set(c["context"].values()), key=natural)[0]} .. {sorted(set(c["context"].values()), key=natural)[-1]}')

def check_one(lang, key, en, tr, kind, problems, warns):
    """en / tr are strings (kind 'string') or one plural item (kind 'plural:<quantity>')."""
    if not isinstance(tr, str) or not tr.strip(): problems.append(f'{key}: empty'); return
    pe, pt = sorted(PH.findall(en)), sorted(PH.findall(tr))
    if kind in ('string', 'plural:other'):
        if pe != pt: problems.append(f'{key}: placeholders {pe} -> {pt}')
    elif not set(pt) <= set(pe): problems.append(f'{key}: placeholders {pe} -> {pt}')
    if sorted(TAGS.findall(en)) != sorted(TAGS.findall(tr)): problems.append(f'{key}: tags differ')
    if en.count('\n') != tr.count('\n'): warns.append(f'{key}: line breaks {en.count(chr(10))} -> {tr.count(chr(10))}')
    if sorted(EMOJI.findall(en)) != sorted(EMOJI.findall(tr)): warns.append(f'{key}: emoji / arrows differ')
    ratio = len(tr) / max(1, len(en))
    if (len(en) <= 18 and ratio > 1.8 and len(tr) > 14) or (len(en) > 18 and ratio > 1.7): warns.append(f'{key}: {len(tr)} characters for {len(en)} in English')
    if lang == 'vi':
        import unicodedata
        if unicodedata.normalize('NFC', tr) != tr: problems.append(f'{key}: not Unicode NFC')

def merge(langs, only=None):
    data = {p: load(p) for p in masters()}
    before = json.loads(json.dumps(data))
    tail = {p: ('\n' if open(p, encoding='utf-8').read().endswith('\n') else '') for p in data}   # keep each file's own ending: no noise in git
    where = {k: p for p, d in data.items() for k in d}
    chunks = sorted(glob.glob(os.path.join(D, 'pending_nl_??_en.json')))
    if only: chunks = [c for c in chunks if os.path.basename(c)[11:13] in only]
    if not chunks: print('no pending_nl_NN_en.json files (run make)'); return 1
    bad = 0
    for lang in langs:
        notes = {}
        for cpath in chunks:
            nn = os.path.basename(cpath)[11:13]
            en = load(cpath)
            tpath = os.path.join(D, f'pending_nl_{nn}_{lang}.json')
            if not os.path.exists(tpath): print(f'{lang} chunk {nn}: missing {os.path.basename(tpath)}'); bad += 1; continue
            tr = load(tpath)
            rpath = os.path.join(D, f'pending_nl_{nn}_{lang}_rev.json')
            if os.path.exists(rpath):
                rev = load(rpath)
                tr.setdefault('strings', {}).update(rev.get('strings', {})); tr.setdefault('plurals', {}).update(rev.get('plurals', {}))
                notes.update(rev.get('notes', {}))
            problems, warns = [], []
            if set(en['strings']) != set(tr.get('strings', {})): problems.append(f'string keys differ: missing {sorted(set(en["strings"]) - set(tr.get("strings", {})))[:5]} extra {sorted(set(tr.get("strings", {})) - set(en["strings"]))[:5]}')
            if set(en['plurals']) != set(tr.get('plurals', {})): problems.append('plural keys differ')
            for k, e in en['strings'].items():
                if k in tr.get('strings', {}): check_one(lang, k, e, tr['strings'][k], 'string', problems, warns)
            for k, e in en['plurals'].items():
                t = tr.get('plurals', {}).get(k)
                if not isinstance(t, dict) or not set(NEED[lang]) <= set(t): problems.append(f'{k}: plural needs {NEED[lang]}'); continue
                for q in NEED[lang]:
                    src = e.get(q) or e.get('other') or next(iter(e.values()))
                    check_one(lang, f'{k}[{q}]', src, t[q], 'plural:' + q, problems, warns)
            for w in warns: print(f'  warn {lang} {nn} {w}')
            if problems:
                for p in problems: print(f'  ERROR {lang} {nn} {p}')
                bad += 1; continue
            for k, v in tr['strings'].items():
                data[where[k]][k][lang] = v
            for k, v in tr['plurals'].items():
                data[where[k]][k].setdefault('plural', {})[lang] = {q: v[q] for q in NEED[lang]}
            print(f'{lang} chunk {nn}: merged {len(tr["strings"])} strings, {len(tr["plurals"])} plurals' + (f', reviewer changed {len(load(rpath).get("strings", {})) + len(load(rpath).get("plurals", {}))}' if os.path.exists(rpath) else ''))
        if notes:
            os.makedirs(LOG, exist_ok=True)
            old = load(os.path.join(LOG, f'nl_{lang}.json')) if os.path.exists(os.path.join(LOG, f'nl_{lang}.json')) else {}
            old.update(notes); save(os.path.join(LOG, f'nl_{lang}.json'), old)
    if bad: print(f'{bad} problem(s): nothing written'); return 1
    for p, d in data.items():
        if d != before[p]: save(p, d, tail[p])
    print('masters written')
    return 0

def status(langs):
    for p in masters():
        d = load(p); row = []
        for lang in langs:
            n = sum(1 for k, v in d.items() if (lang in v.get('plural', {}) if 'plural' in v else lang in v))
            row.append(f'{lang} {n}/{len(d)}')
        print(f'{os.path.basename(p)[:-5]:30s}', '  '.join(row))

if __name__ == '__main__':
    cmd = sys.argv[1] if len(sys.argv) > 1 else ''
    if cmd == 'make':
        make(int(sys.argv[sys.argv.index('--size') + 1]) if '--size' in sys.argv else 220)
    elif cmd == 'merge':
        args = sys.argv[2:]
        only = None
        if '--chunks' in args:
            i = args.index('--chunks'); only = args[i + 1].split(','); del args[i:i + 2]
        sys.exit(merge(args, only))
    elif cmd == 'status': status(sys.argv[2:] or ['de', 'id', 'vi'])
    elif cmd == 'cleanup':
        for f in glob.glob(os.path.join(D, 'pending_nl_*.json')): os.remove(f)
        print('removed')
    else: print(__doc__)
