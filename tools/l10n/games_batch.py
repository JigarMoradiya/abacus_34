#!/usr/bin/env python3
"""One batch of the Math Game Zone (M17b..e): move the literals of some games and their how-to-play screens into strings.xml.

  games_batch.py <batch> <game>[:<HowToFile>] ...      e.g.  games_batch.py m17b balloon_pop:HowToPlayBalloonPopView calcudoku:HowToPlayCalcudokuView

For every game folder under screens/math_game_zone and its how-to view under common_ui/how_to_play:
  1. the cross-cutting score lines ("🏆 Best Score: N", "Final Score: N", "Best Time") are rewritten in all games (idempotent),
  2. lit_convert moves the simple literals (keys g_<game>_N) and the rule lines of the how-to screen (keys htp_<game>_N),
  3. the English text of the new keys is appended to values/strings.xml,
  4. docs/l10n/translations/pending_<batch>_en.json is written: the new keys + every existing key the folders use that has
     no translation yet, with the translations of identical English reused (prefill_<batch>.json).
Literals it cannot convert are printed as LEFT: fix them by hand, then rerun make_pending / prefill if keys changed.
"""
import glob, json, os, re, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import lit_convert
from make_pending import untranslated, BASE

Z = os.path.join(BASE, 'app', 'src', 'main', 'java', 'com', 'jigar', 'me', 'ui', 'view', 'home', 'screens', 'math_game_zone')
H = os.path.join(BASE, 'app', 'src', 'main', 'java', 'com', 'jigar', 'me', 'ui', 'view', 'home', 'common_ui', 'how_to_play')
SR = 'androidx.compose.ui.res.stringResource'

def esc(t): return t.replace('&', '&amp;').replace('<', '&lt;').replace("'", "\\'").replace('"', '\\"').replace('\n', '\\n')

def shared_lines(en):
    """Best / final score lines of every game: `"🏆 " + stringResource(R.string.balloon_best_score) + ": $bestScore"` -> one string."""
    A = re.compile(r'"🏆 " \+ (?:androidx\.compose\.ui\.res\.)?stringResource\(R\.string\.balloon_best_score\) \+ ":\s*\$(\{[^}]*\}|\w+)"')
    B = re.compile(r'(?:androidx\.compose\.ui\.res\.)?(?:stringResource|str|stringRes)\(R\.string\.balloon_final_score\) \+ ":\s*\$(\{[^}]*\}|\w+)"')
    C = re.compile(r'"🏆 " \+ (?:androidx\.compose\.ui\.res\.)?stringResource\(R\.string\.best_time\) \+ ": " \+ ("%d:%02d"\.format\([^()]*(?:\([^()]*\)[^()]*)*\))')
    n = 0
    for f in glob.glob(os.path.join(Z, '**', '*.kt'), recursive=True):
        s = open(f, encoding='utf-8').read(); o = s
        s = A.sub(lambda m: f'{SR}(R.string.g_best_score_line, {m.group(1).strip("{}")})', s)
        s = B.sub(lambda m: f'{SR}(R.string.g_final_score_line, {m.group(1).strip("{}")})', s)
        s = C.sub(lambda m: f'{SR}(R.string.g_best_time_line, {m.group(1)})', s)
        if s != o:
            n += 1; open(f, 'w', encoding='utf-8').write(lit_convert.add_imports(s))
    if n:
        en.update({'g_best_score_line': '🏆 Best Score: %1$s', 'g_final_score_line': 'Final Score: %1$s', 'g_best_time_line': '🏆 Best Time: %1$s'})
    return n

def main():
    batch, games = sys.argv[1], sys.argv[2:]
    en = {}
    print('score lines rewritten in', shared_lines(en), 'files')
    folders, left_all = [], []
    for g in games:
        game, _, howto = g.partition(':')
        folder = os.path.join(Z, game); folders.append(folder)
        e, left = lit_convert.convert(sorted(glob.glob(os.path.join(folder, '**', '*.kt'), recursive=True)), f'g_{game}')
        en.update(e); left_all += left
        if howto:
            e, left = lit_convert.convert([os.path.join(H, howto + '.kt')], f'htp_{game}', howto=True)
            en.update(e); left_all += left
        print(f'{game}: {sum(1 for k in en if k.startswith("g_" + game + "_"))} literals, {sum(1 for k in en if k.startswith("htp_" + game + "_"))} rule lines')
    for f, l, c in left_all: print(f'LEFT {os.path.basename(f)}:{l}: {c}')
    # English strings.xml
    p = os.path.join(BASE, 'app', 'src', 'main', 'res', 'values', 'strings.xml'); t = open(p, encoding='utf-8').read().rstrip()
    new = {k: v for k, v in en.items() if f'name="{k}"' not in t}
    lines = ['', f'    <!-- Math Game Zone ({batch}) -->'] + [f'    <string name="{k}">{esc(v)}</string>' for k, v in new.items()]
    open(p, 'w', encoding='utf-8').write(t[:-len('</resources>')].rstrip() + '\n' + '\n'.join(lines) + '\n</resources>\n')
    s_old, p_old = untranslated(folders + [os.path.join(H, g.partition(':')[2] + '.kt') for g in games if ':' in g])
    s_all = dict(s_old); s_all.update(en)
    out = os.path.join(BASE, 'docs', 'l10n', 'translations', f'pending_{batch}_en.json')
    json.dump({'strings': s_all, 'plurals': p_old}, open(out, 'w', encoding='utf-8'), ensure_ascii=False, indent=1)
    print(f'pending_{batch}_en.json: {len(s_all)} strings, {len(p_old)} plurals ({len(en)} new keys)')

if __name__ == '__main__': main()
