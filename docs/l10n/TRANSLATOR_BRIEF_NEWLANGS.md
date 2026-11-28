# Translator + reviewer brief for the new languages (one agent per language per chunk)

You translate the UI texts of "Abacus" (Vedaavi Learning), an app that teaches children aged 4-10 the abacus, mental math and math games. Parents buy it, children use it. The texts are shown on phones and tablets (mostly landscape), on small buttons, chips, cards and tiles. Quality matters: a native parent will read this in a store-ready app.

Work in `/Users/jigarmoradiya/Documents/newProject/abacus/android`. Read first, completely: `docs/l10n/glossary_newlangs.md` (words and tone for your language) and `docs/l10n/glossary.md` (what each term means in the app). Then look at 2-3 already translated master files in `docs/l10n/translations/` (`m7_onboarding.json`, `m13_level1.json`, `m8_paywall.json`: shape `{key: {lang: text}}`) to see the app's voice and how game names are written: a game name inside a text must be written exactly as the `game_name_<game>` entry of that game.

## Translator
Input: `docs/l10n/translations/pending_nl_<NN>_en.json`
`{"strings": {"<key>": "<English>"}, "plurals": {"<key>": {"one": "..", "other": ".."}}, "context": {"<key>": "<module name>"}}`
The key name and the module tell you where the text is. If a key is unclear, search the code for `R.string.<key>` (read-only, `app/src/main/java`) to see the screen.
Output (you create it): `docs/l10n/translations/pending_nl_<NN>_<lang>.json` with `"strings"` and `"plurals"` only (same keys, translated values). Plurals: give every quantity your language needs (table below).
Validate when done (replace NN and lang), must print True True:
`python3 -c "import json;d=json.load(open('docs/l10n/translations/pending_nl_<NN>_<lang>.json',encoding='utf-8'));e=json.load(open('docs/l10n/translations/pending_nl_<NN>_en.json',encoding='utf-8'));print(set(e['strings'])==set(d['strings']), set(e['plurals'])==set(d['plurals']))"`

### Rules
1. Keep every placeholder exactly (`%1$d`, `%1$s`, `%2$d`, `%d`, `%s` ...): do not add, drop or renumber; you may move them inside the sentence.
2. Keep every emoji exactly once, in the same place. Keep arrows `→ ← ▶ ✓`, `·`, `×`, `÷`, `+`, `−`, `=`.
3. Keep `\n` line breaks and `<b>..</b>` style tags where the English has them.
4. WORDS IN CAPITALS are emphasis: keep the emphasis (capitals where the language has them, but follow the language's own rules: German nouns are always capitalised anyway, so use bold-free emphasis like "genau", or keep CAPS only where the English clearly shouts).
5. Digits are always 0-9. Numbers and math expressions inside texts stay exactly as in the English.
6. Do not change, add or drop keys.
7. Natural wording of a native speaker, not word for word. A child text is short, warm, informal. Keep each text about as long as the English or shorter; a button, chip or tile label must stay one or two words. When the language runs longer (German +30-40%), choose the shorter word.
8. Use the glossary words. The same English word gets the same translation in every string. Do not invent a second word.
9. Brand: the app is called "Abacus" in English. In your language the app name is: de "Abakus", id "Sempoa", vi "Bàn tính" (as in the glossary). Product / store names stay as written: Google Play, App Store, Premium, Anzan.
10. `—` may become a comma, colon or full stop.
11. Do not leave English words in the translation unless it is a brand, a product name or a loanword the glossary lists.
12. Honest doubts: if you cannot tell what a text means, translate the most likely meaning and list the key in your reply.

### Per language
| lang | plural keys | notes |
|---|---|---|
| de | one, other | du for children, Sie for parents (glossary). Nouns capitalised. „Anführungszeichen“. No gender stars or colons. Sentence case for buttons. |
| id | other | kamu for children, Anda for parents. Standard Bahasa Indonesia. No plural after a number ("3 manik", not "3 manik-manik"). |
| vi | other | bạn. All diacritics exact, Unicode NFC. Classifiers where natural ("3 hạt"). Keep the tone friendly, not baby talk. |
| zh | other | Simplified Chinese (mainland). 你 for children, 您 for parents. Full-width punctuation. Measure words where natural ("3颗算珠"). App name 算盘. |
| ko | other | Standard Korean. Friendly 해요체 for children, polite 해요체/합니다체 for parents. No particle right after a placeholder (rephrase). App name 주판. |

## Reply
One line: `done` + the number of strings. Then only real doubts (key + why). Do not paste the translations.

## Reviewer (a different agent from the translator)
Input: the English chunk and the translator's file for ONE language. You are a native-level reviewer and also a careful QA person. For EVERY string:
- is the meaning right and complete (nothing added or dropped), is it natural for a native speaker of that country, is the register right (child informal / parent polite, see glossary_newlangs.md),
- are glossary words used and consistent with the other strings of the chunk,
- is it short enough for a small button / chip / tile when the English is short,
- are placeholders, emoji, `\n`, tags, numbers, game names intact; spelling, diacritics, grammar, capitalisation correct,
- no English left over, no wrong-script characters.
Output: `docs/l10n/translations/pending_nl_<NN>_<lang>_rev.json` with ONLY the strings you changed: `{"strings": {key: corrected}, "plurals": {key: {...}}, "notes": {key: "one-line reason"}}`. If a string is fine, leave it out. Do not "improve" taste: change only real errors, unnatural phrasing, glossary breaks, wrong register or length problems. Reply: `reviewed`, number checked, number changed, and any term that should be added to the glossary.
