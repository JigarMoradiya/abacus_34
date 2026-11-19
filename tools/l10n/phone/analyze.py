#!/usr/bin/env python3
"""analyze.py cluster <dir>            -> writes <dir>/reps.json: one route per group of routes with the same English text
   analyze.py leaks <dir> <lang> ...   -> English text left on the screen, per language (against en.json and the other languages)
   analyze.py layout <dir> <lang> ...  -> text outside the screen, new overlaps against English
"""
import itertools, json, re, sys

W, H = 2392, 1080
LETTER = re.compile(r'[^\W\d_]', re.U)


def norm(t):
    return re.sub(r'\d+([.,]\d+)?', '#', t).strip()


def words(texts):
    return {norm(t) for t, _ in texts if LETTER.search(t)}


def load(d, lang):
    return json.load(open(f'{d}/{lang}.json'))


def box(b):
    m = re.findall(r'\d+', b or '')
    return tuple(map(int, m)) if len(m) == 4 else None


def cluster(d):
    en = load(d, 'en')
    groups = {}
    for k, v in en.items():
        groups.setdefault(frozenset(words(v['texts'])), []).append(k)
    reps = [g[0] for g in groups.values()]
    json.dump(reps, open(f'{d}/reps.json', 'w'), indent=0)
    print(f'{len(en)} routes -> {len(reps)} different screens ({len(en) - len(reps)} repeat an earlier screen)')
    st = {}
    for k, v in en.items():
        st.setdefault(v['status'], []).append(k)
    for s, ks in st.items():
        print(' ', s, len(ks), ks if s != 'OK' else '')


def leaks(d, langs):
    en = load(d, 'en')
    data = {l: load(d, l) for l in langs}
    # a text that is the same in English and in most of the checked languages is a name or a symbol, not a leak
    same_count = {}
    for l, res in data.items():
        for k, v in res.items():
            if k not in en:
                continue
            e = words(en[k]['texts'])
            for w in words(v['texts']) & e:
                same_count[w] = same_count.get(w, 0) + 1
    neutral_at = max(3, len(langs) // 2 + 1)
    for l, res in data.items():
        seen = {}
        for k, v in res.items():
            if k not in en:
                continue
            for w in sorted(words(v['texts']) & words(en[k]['texts'])):
                if same_count.get(w, 0) >= neutral_at or len(w) < 3:
                    continue
                seen.setdefault(w, []).append(k)
        print(f'\n[{l}] {len(seen)} texts still like English')
        for w, ks in sorted(seen.items(), key=lambda x: -len(x[1])):
            print(f'   {w[:70]!r:74} {ks[0]}{" +%d" % (len(ks) - 1) if len(ks) > 1 else ""}')


def overlaps(texts):
    boxes = [(t, box(b)) for t, b in texts if box(b)]
    n = 0
    for (t1, a), (t2, b) in itertools.combinations(boxes, 2):
        if t1 == t2:
            continue
        ix = min(a[2], b[2]) - max(a[0], b[0])
        iy = min(a[3], b[3]) - max(a[1], b[1])
        if ix > 6 and iy > 6:
            # one box inside the other is a label on its own plate, not a clash
            inside = (a[0] <= b[0] and a[1] <= b[1] and a[2] >= b[2] and a[3] >= b[3]) or \
                     (b[0] <= a[0] and b[1] <= a[1] and b[2] >= a[2] and b[3] >= a[3])
            if not inside:
                n += 1
    return n


def layout(d, langs):
    en = load(d, 'en')
    for l in langs:
        res = load(d, l)
        print(f'\n[{l}]')
        for k, v in res.items():
            if v['status'] != 'OK':
                continue
            out = [t for t, b in v['texts'] if box(b) and (box(b)[0] < -2 or box(b)[1] < -2 or box(b)[2] > W + 2 or box(b)[3] > H + 2)]
            out_en = [t for t, b in en.get(k, {'texts': []})['texts'] if box(b) and (box(b)[0] < -2 or box(b)[1] < -2 or box(b)[2] > W + 2 or box(b)[3] > H + 2)]
            new_out = [t for t in out if not any(norm(t) == norm(e) for e in out_en)]
            ov, ov_en = overlaps(v['texts']), overlaps(en[k]['texts']) if k in en else 0
            if (new_out and len(out) > len(out_en)) or ov > ov_en + 1:
                print(f'   {k:36} out-of-screen+{len(out) - len(out_en)} overlaps {ov_en}->{ov} {[o[:24] for o in new_out[:3]]}')


if __name__ == '__main__':
    cmd, d = sys.argv[1], sys.argv[2]
    {'cluster': lambda: cluster(d), 'leaks': lambda: leaks(d, sys.argv[3:]), 'layout': lambda: layout(d, sys.argv[3:])}[cmd]()
