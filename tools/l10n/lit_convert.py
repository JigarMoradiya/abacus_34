#!/usr/bin/env python3
"""Move simple hard-coded visible literals of Kotlin files into strings.xml keys (a helper for the big modules).

  lit_convert.py <prefix> <file|folder> [...] [--dry]      e.g. lit_convert.py g_balloon app/.../math_game_zone/balloon_pop

Converts, in place, only the safe shapes:
  Text("..."                          text = "..."   title = "..."   label = "..."   subtitle = "..."   message = "..."
  contentDescription = "..."   placeholder = "..."   description = "..."   cta = "..."   retryLabel = "..."   hint = "..."
and, for the how-to-play screens (`--howto`), every string literal argument of the private composable helpers of the file
(HowToBullet("..."), Check("a", "b") ...), which are the screen's rule lines.
`${expr}` / `$name` inside a literal become positional %N$s arguments. Literals without 2 letters, animation `label = "camelCase"`,
log / error messages and identifiers are left alone. Needs `import stringResource` and `R`: both are added. Anything that does not
compile afterwards (a literal used outside a composable) is for the caller to fix by hand.
Importable: convert(files, prefix) -> (english dict {key: text}, list of (file, line, snippet) left)
"""
import glob, os, re, sys

SLOT = re.compile(r'((?:\bText\(\s*|\b(?:text|title|label|subtitle|message|contentDescription|placeholder|description|cta|retryLabel|hint|header|caption|correctText)\s*=\s*))"((?:[^"\\\n]|\\.)*)"')
ARROW = re.compile(r'((?:->|\?:)\s*)"((?:[^"\\\n]|\\.)*)"')   # when-branches and elvis fallbacks
SKIP_LINE = re.compile(r'(\bLog\.|Exception\(|\berror\(|\brequire\(|\bcheck\(|\bprintln|\broute\s*[=(]|Route\.|navigate)')
IMPORT_RES = 'import androidx.compose.ui.res.stringResource'
IMPORT_R = 'import com.jigar.me.R'

def letters(t):
    return len(re.findall(r'[^\W\d_]', re.sub(r'\$\{[^{}]*\}|\$\w+', '', t)))

def xml_text(kotlin_lit):
    """Kotlin literal body -> (plain text with %N$s placeholders, [expressions])."""
    exprs = []
    def sub(m):
        e = m.group(1) or m.group(2)
        if e not in exprs: exprs.append(e)
        return f'%{exprs.index(e) + 1}$s'
    t = re.sub(r'\$\{([^{}]*)\}|\$(\w+)', sub, kotlin_lit)
    t = t.replace('\\"', '"').replace("\\'", "'").replace('\\n', '\n').replace('\\$', '$').replace('\\\\', '\\')
    if '%' in re.sub(r'%\d\$s', '', t): t = None
    return t, exprs

def add_imports(s):
    ms = list(re.finditer(r'^import .*$', s, re.M))
    for imp in (IMPORT_RES, IMPORT_R):
        if imp not in s:
            at = ms[-1].end() if ms else s.index('\n') + 1
            s = s[:at] + '\n' + imp + s[at:]
            ms = list(re.finditer(r'^import .*$', s, re.M))
    return s

def helper_names(s):
    """Names of the composable helper functions of a how-to screen that take a String first."""
    return set(re.findall(r'@Composable\s+(?:private |internal )?fun (\w+)\(\s*(?:\w+:\s*String|\w+:\s*String\?)', s))

def convert(files, prefix, howto=False, start=1, extra_names=()):
    en, left, n = {}, [], start
    for f in files:
        s = open(f, encoding='utf-8').read(); out = []; changed = False
        names = (helper_names(s) | set(extra_names)) if howto else set()
        for lineno, line in enumerate(s.split('\n'), 1):
            code = line
            if line.strip().startswith(('//', '*', '/*', 'import ')) or SKIP_LINE.search(line):
                out.append(line); continue
            def repl_slot(m):
                nonlocal n, changed
                body = m.group(2)
                if letters(body) < 2: return m.group(0)
                if m.group(1).strip().startswith('label') and re.fullmatch(r'[a-z][A-Za-z0-9_\-]*', body): return m.group(0)
                if re.fullmatch(r'[a-z]+', body) and not m.group(1).lstrip().startswith('Text'): return m.group(0)
                t, ex = xml_text(body)
                if t is None: left.append((f, lineno, line.strip()[:140])); return m.group(0)
                key = f'{prefix}_{n}'; n += 1; en[key] = t; changed = True
                args = ''.join(', ' + e for e in ex)
                return f'{m.group(1)}stringResource(R.string.{key}{args})'
            new = SLOT.sub(repl_slot, line)
            def repl_arrow(m):
                nonlocal n, changed
                body = m.group(2)
                if letters(body) < 2 or not re.search(r'[\s!?.,:;]|[^\x00-\x7f]', body): return m.group(0)   # identifiers have no space or punctuation
                t, ex = xml_text(body)
                if t is None: left.append((f, lineno, line.strip()[:140])); return m.group(0)
                key = f'{prefix}_{n}'; n += 1; en[key] = t; changed = True
                return f'{m.group(1)}stringResource(R.string.{key}' + ''.join(', ' + e for e in ex) + ')'
            new = ARROW.sub(repl_arrow, new)
            if howto and names:
                pat = re.compile(r'\b(' + '|'.join(sorted(names)) + r')\(((?:[^()]|\([^()]*\))*)\)')
                def repl_call(m):
                    nonlocal n, changed
                    def one(lm):
                        nonlocal n, changed
                        body = lm.group(1)
                        if not re.search(r'[A-Za-z]', body): return lm.group(0)
                        t, ex = xml_text(body)
                        if t is None: left.append((f, lineno, line.strip()[:140])); return lm.group(0)
                        key = f'{prefix}_{n}'; n += 1; en[key] = t; changed = True
                        return f'stringResource(R.string.{key}' + ''.join(', ' + e for e in ex) + ')'
                    return m.group(1) + '(' + re.sub(r'"((?:[^"\\\n]|\\.)*)"', one, m.group(2)) + ')'
                new = pat.sub(repl_call, new)
            out.append(new)
        if changed:
            res = add_imports('\n'.join(out))
            open(f, 'w', encoding='utf-8').write(res)
    return en, left

def main():
    args = [a for a in sys.argv[1:] if not a.startswith('--')]
    prefix, paths = args[0], args[1:]
    files = []
    for p in paths:
        files += glob.glob(os.path.join(p, '**', '*.kt'), recursive=True) if os.path.isdir(p) else [p]
    en, left = convert(sorted(set(files)), prefix, howto='--howto' in sys.argv)
    for k, v in en.items(): print(f'{k}: {v!r}')
    for f, l, c in left: print(f'LEFT {os.path.basename(f)}:{l}: {c}')
    print(len(en), 'converted,', len(left), 'left')

if __name__ == '__main__': main()
