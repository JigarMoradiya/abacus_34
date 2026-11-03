#!/usr/bin/env python3
"""Check translated strings against the English source (values/strings.xml).

  check_strings.py                 -> check every values-XX/strings.xml that exists
  check_strings.py ru fr           -> only these languages
Errors (exit code 1): missing placeholder / different placeholders, different markup tags (<b>, <font>, <br/>), unescaped apostrophe,
plural quantities that the language needs but are missing, plural/string type mismatch.
Warnings: key missing in the translation (falls back to English), text identical to English for a long string.
"""
import os, re, sys
import xml.etree.ElementTree as ET

RES = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..', 'app', 'src', 'main', 'res')
# CLDR plural categories Android needs per language (Android lint MissingQuantity)
PLURALS = {'fr': {'one', 'many', 'other'}, 'es': {'one', 'many', 'other'}, 'pt': {'one', 'many', 'other'}, 'tr': {'one', 'other'},
           'ru': {'one', 'few', 'many', 'other'}, 'ar': {'zero', 'one', 'two', 'few', 'many', 'other'}, 'ja': {'other'}, 'th': {'other'}}
PH = re.compile(r'%(?:\d+\$)?[-#+ 0,(]*\d*(?:\.\d+)?[sdfxXeEgGcb%n]')
TAGS = re.compile(r'</?(?:b|i|u|font|br|a|small|big|strong|em)\b[^>]*>', re.I)

def load(path):
    """key -> ('string', text) or ('plurals', {quantity: text}), plus the raw text (for apostrophe check)."""
    out = {}
    for el in ET.parse(path).getroot():
        name = el.get('name')
        if not name or el.get('translatable') == 'false': continue
        if el.tag == 'string': out[name] = ('string', ''.join(el.itertext()))
        elif el.tag == 'plurals': out[name] = ('plurals', {i.get('quantity'): ''.join(i.itertext()) for i in el.findall('item')})
    return out

def placeholders(s): return sorted(m.group(0) for m in PH.finditer(s.replace('%%', '')))
def tags(s): return sorted(t.lower().replace(' ', '') for t in TAGS.findall(s))
def raw_apostrophes(path):
    bad = []
    for el in ET.parse(path).getroot():
        if el.tag != 'string': continue
        txt = ''.join(el.itertext())
        if "'" in txt and not (txt.startswith('"') and txt.endswith('"')) and re.search(r"(?<!\\)'", txt):
            bad.append(el.get('name'))
    return bad

def main():
    langs = sys.argv[1:] or sorted(d.split('-', 1)[1] for d in os.listdir(RES) if re.fullmatch(r'values-[a-z]{2,3}', d))
    src = load(os.path.join(RES, 'values', 'strings.xml'))
    errors = warnings = 0
    for lang in langs:
        path = os.path.join(RES, f'values-{lang}', 'strings.xml')
        if not os.path.exists(path): print(f'[{lang}] no strings.xml'); continue
        tr = load(path); miss = [k for k in src if k not in tr]; extra = [k for k in tr if k not in src]
        e = []
        for k, (kind, val) in tr.items():
            if k not in src: continue
            skind, sval = src[k]
            if kind != skind: e.append(f'{k}: type differs ({skind} vs {kind})'); continue
            if kind == 'string':
                if placeholders(val) != placeholders(sval): e.append(f'{k}: placeholders {placeholders(sval)} -> {placeholders(val)}')
                if tags(val) != tags(sval): e.append(f'{k}: markup tags {tags(sval)} -> {tags(val)}')
            else:
                need = PLURALS.get(lang, {'one', 'other'})
                if not need <= set(val): e.append(f'{k}: plural quantities missing {sorted(need - set(val))}')
                for q, t in val.items():
                    base = sval.get(q) or sval.get('other') or ''
                    if placeholders(t) != placeholders(base) and q in sval: e.append(f'{k}[{q}]: placeholders differ')
        for k in raw_apostrophes(path): e.append(f'{k}: unescaped apostrophe (use \\\')')
        same = [k for k, (kind, v) in tr.items() if k in src and kind == 'string' and v == src[k][1] and len(v) > 25]
        print(f'[{lang}] {len(tr)} strings: {len(miss)} missing (fall back to English), {len(extra)} extra, {len(e)} errors, {len(same)} identical-to-English')
        for x in e[:25]: print('   ERROR', x)
        errors += len(e); warnings += len(miss) + len(same)
    print(f'\nerrors: {errors}   warnings: {warnings}')
    sys.exit(1 if errors else 0)

if __name__ == '__main__':
    main()
