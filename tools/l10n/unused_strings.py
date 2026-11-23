#!/usr/bin/env python3
"""List strings in values/strings.xml that no code or XML refers to (candidates to remove before sending to translation).

  unused_strings.py            -> print the unused keys and a summary
  unused_strings.py --names    -> only the keys, one per line
A key counts as used when R.string.<key>, R.plurals.<key> or @string/<key> appears in any .kt / .java / .xml file.
"""
import os, re, sys
import xml.etree.ElementTree as ET

HERE = os.path.dirname(os.path.abspath(__file__))
APP = os.path.join(HERE, '..', '..', 'app', 'src')
RES = os.path.join(APP, 'main', 'res', 'values', 'strings.xml')

def main():
    keys = {}
    for el in ET.parse(RES).getroot():
        n = el.get('name')
        if n and el.tag in ('string', 'plurals') and el.get('translatable') != 'false': keys[n] = el.tag
    blob = []
    for r, _, fs in os.walk(APP):
        for f in fs:
            if f.endswith(('.kt', '.java', '.xml')) and os.path.join(r, f) != os.path.normpath(RES):
                blob.append(open(os.path.join(r, f), encoding='utf-8', errors='ignore').read())
    text = '\n'.join(blob)
    used = set(re.findall(r'R\.(?:string|plurals)\.(\w+)', text)) | set(re.findall(r'@(?:string|plurals)/(\w+)', text))
    unused = sorted(k for k in keys if k not in used)
    if '--names' in sys.argv:
        print('\n'.join(unused)); return
    words = 0
    src = {el.get('name'): ''.join(el.itertext()) for el in ET.parse(RES).getroot() if el.tag == 'string'}
    for k in unused: words += len(src.get(k, '').split())
    print('\n'.join(unused[:400]))
    print(f'\n{len(unused)} unused of {len(keys)} ({words} words); {len(keys) - len(unused)} in use')

if __name__ == '__main__':
    main()
