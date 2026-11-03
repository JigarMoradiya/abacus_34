#!/usr/bin/env python3
"""Build the English master (pending_<name>_en.json) for the translators of one module batch.

  make_pending.py <name> <path|module> [<path|module> ...]   e.g.  make_pending.py m17a ui/view/home/screens/math_game_zone/common

Collects every R.string.<key> / R.plurals.<key> used by the given folders or .kt files that has no translation yet
(no entry in docs/l10n/translations/*.json) and writes docs/l10n/translations/pending_<name>_en.json
({"strings": {key: english}, "plurals": {key: {"one": .., "other": ..}}}). Keys that are translatable="false" are skipped.
Other scripts can import `untranslated(paths)` and add the new keys they create themselves.
"""
import glob, json, os, re, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from scan_static import MODULES, ROOT

BASE = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..')
SRC = os.path.join(BASE, 'app', 'src', 'main', 'res', 'values', 'strings.xml')
USED = re.compile(r'R\.(?:string|plurals)\.([A-Za-z0-9_]+)')

def unescape(inner):
    """The text of a strings.xml value as a translator should see it (markup kept, entities and backslashes resolved)."""
    t = inner.strip()
    if t.startswith('<![CDATA[') and t.endswith(']]>'): return t[9:-3]   # raw text: no entities, no backslash escapes
    if len(t) >= 2 and t[0] == '"' and t[-1] == '"': t = t[1:-1]
    if '<' not in t: t = t.replace('&lt;', '<').replace('&gt;', '>')
    t = t.replace('&amp;', '&')
    t = t.replace("\\'", "'").replace('\\"', '"').replace('\\n', '\n').replace('\\@', '@').replace('\\?', '?')
    return t

def english():
    x = open(SRC, encoding='utf-8').read()
    strings, plurals = {}, {}
    for m in re.finditer(r'<string name="([^"]+)"([^>]*)>(.*?)</string>', x, re.S):
        if 'translatable="false"' not in m.group(2): strings[m.group(1)] = unescape(m.group(3))
    for m in re.finditer(r'<plurals name="([^"]+)"([^>]*)>(.*?)</plurals>', x, re.S):
        if 'translatable="false"' in m.group(2): continue
        plurals[m.group(1)] = {q: unescape(t) for q, t in re.findall(r'<item quantity="(\w+)">(.*?)</item>', m.group(3), re.S)}
    return strings, plurals

def translated():
    done = set()
    for f in glob.glob(os.path.join(BASE, 'docs', 'l10n', 'translations', '*.json')):
        if os.path.basename(f).startswith('pending_'): continue
        done |= set(json.load(open(f, encoding='utf-8')))
    return done

def files_of(paths):
    out = []
    for a in paths:
        p = MODULES.get(a, a)
        full = p if os.path.isabs(p) else os.path.join(ROOT, p)
        if os.path.isdir(full): out += glob.glob(os.path.join(full, '**', '*.kt'), recursive=True)
        elif os.path.isfile(full): out.append(full)
    return sorted(set(out))

def untranslated(paths):
    eng_s, eng_p = english(); done = translated(); used = set()
    for f in files_of(paths): used |= set(USED.findall(open(f, encoding='utf-8', errors='ignore').read()))
    return ({k: eng_s[k] for k in sorted(used) if k in eng_s and k not in done},
            {k: eng_p[k] for k in sorted(used) if k in eng_p and k not in done})

if __name__ == '__main__':
    name, paths = sys.argv[1], sys.argv[2:]
    s, p = untranslated(paths)
    out = os.path.join(BASE, 'docs', 'l10n', 'translations', f'pending_{name}_en.json')
    json.dump({'strings': s, 'plurals': p}, open(out, 'w', encoding='utf-8'), ensure_ascii=False, indent=1)
    print(len(s), 'strings,', len(p), 'plurals ->', out)
