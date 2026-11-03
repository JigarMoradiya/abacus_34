# Translator brief (for the helper agents, one agent per language)

You translate the UI texts of "Abacus" (Vedaavi Learning), an app that teaches children aged 6-10 the abacus, mental math and
math games. You get ONE input file (English master) and write ONE output file. Work only with those two files plus the read-only
reference files named below.

## Files
- Input (English master): `docs/l10n/translations/pending_<batch>_en.json`
  `{"strings": {"<key>": "<English text>", ...}, "plurals": {"<key>": {"one": "<English singular>", "other": "<English plural>"}, ...}}`
- Output (you create it): `docs/l10n/translations/pending_<batch>_<lang>.json` with the same shape, the same keys, translated values.
  For "plurals" give every plural quantity the language needs (see the table below).
- Validate when done (replace <batch> and <lang>), both must print True:
  `python3 -c "import json;d=json.load(open('docs/l10n/translations/pending_<batch>_<lang>.json',encoding='utf-8'));e=json.load(open('docs/l10n/translations/pending_<batch>_en.json',encoding='utf-8'));print(set(e['strings'])==set(d['strings']), set(e['plurals'])==set(d['plurals']))"`
  (run it from /Users/jigarmoradiya/Documents/newProject/abacus_l10n/android)
- Read first: `docs/l10n/glossary.md` (abacus terms, tone) and look at the translated masters in `docs/l10n/translations/*.json`
  (shape `{key: {lang: text}}`, e.g. `m7_onboarding.json` has the game names `game_name_*`, `m13_level1.json`, `m14_level2.json`,
  `m15_level3.json`, `m16_level4.json`) and reuse the same words for the same ideas (Try Again, Correct, Next, Start, game names ...).
  A game name in a text must be written exactly as the `game_name_<game>` translation of that game.

## Rules (all languages)
1. Keep every placeholder exactly as written (`%1$d`, `%1$s`, `%2$d` ...). Do not add, drop or renumber; you may move them inside the sentence.
2. Keep emoji exactly: every emoji of the English text appears once in your text, in the same place. Keep arrows `→ ← ▶ ✓`, `·`, `×`, `÷`, `+`, `−`, `=`.
3. A JSON string contains `\n` where the English has a line break: keep those.
4. Words in CAPITALS in the English are emphasis: keep the emphasis with capitals where the language has capitals (not ja, th).
5. Digits stay 0-9 always (never Arabic-Indic, Thai or kanji numerals, not full-width). Math expressions and numbers inside texts stay exactly as in English.
6. Do not translate or change keys. Do not add or drop keys.
7. Text a CHILD reads is short, warm and informal (fr tu, es tú, pt você, tr sen, ru ты, ja friendly です/ます with easy words, th friendly).
   Natural wording, not word for word. Keep each text about as long as the English or shorter; buttons, chips, cards and tiles are small.
   Parent-facing texts (settings, reports, rules for adults) are polite but still simple.
8. `—` (long dash) may become a comma, colon or full stop.
9. Strings that are only a game feature name or a very short label must stay short (one or two words).

## Per language
| lang | plural keys to write | typography / notes |
|---|---|---|
| fr | one, many, other (many = other) | put a no-break space U+00A0 (real character) before `! ? : ;`, French quotes « » if needed |
| es | one, many, other (many = other) | opening ¿ and ¡; neutral Latin-American wording |
| pt | one, many, other (many = other) | Brazilian Portuguese; it runs long: choose short words |
| tr | one, other (may be equal) | dotted/dotless I when you capitalise: i -> İ, ı -> I; no plural after a number |
| ru | one, few, many, other (other = few) | noun in the right case after the number (1 бусина, 2 бусины, 5 бусин); Russian runs long |
| ja | other | easy kanji or hiragana for 6-10 year olds, no spaces needed, counter words like 個, 問 are fine |
| ar | zero, one, two, few, many, other | Modern Standard Arabic, simple; right to left: write the text normally, punctuation Arabic (، ؟ ؛); digits stay 0-9 (Western) also inside sentences; brand/game names and emoji as in the English; math expressions stay exactly as written (the screen keeps them left to right); no tashkeel (diacritics) unless needed to avoid a wrong reading; children: friendly masculine/neutral forms (يمكنك), avoid gendered asides |
| th | other | no spaces between words inside a phrase, spaces between phrases; no zero-width characters; digits 0-9 |

## Reply
One line: `done` and the number of strings translated. Mention only real doubts (a term you could not match to the glossary, a text
whose meaning you could not tell). Do not paste the translations in your reply.
