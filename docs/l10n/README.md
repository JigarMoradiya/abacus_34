# Translating the Abacus app (Android first)

Languages: Japanese `ja`, French `fr`, Spanish `es`, Turkish `tr`, Thai `th`, Arabic `ar`, Portuguese `pt` (Brazilian wording, one folder for all Portuguese), Russian `ru`. English `en` is the source.
Wave 1 (checked first): fr, es, pt, tr, ru. Wave 2 (needs fonts and right-to-left work): ja, th, ar.

## Rules (owner decisions)
1. **No visible text in static or constant places.** A `const val`, `companion object`, `object`, top-level `val`, enum text, data-class default or a list held in a field is fixed when the class loads and does not follow a language change. Put the text in `strings.xml` and keep only the `@StringRes` id in the class; resolve it when it is drawn (`stringResource`, `context.getString`). Exception: identifiers and values that are sent to or compared with the server or the code (routes, API values, `Level.name` keys, the English exam / exercise labels sent to the server) stay as constants, with a separate `@StringRes` label for display.
2. **Digits are always 0-9** (sums, abacus, answers), also in Arabic and Thai. Use `LocalizedNumberFormat` to show numbers: only the decimal mark follows the language.
3. **Sums, equations, the abacus, the answer bar and number pads stay left to right** in Arabic: wrap them in `LtrIsland`.
4. **Whole sentences with placeholders.** No text glued together in code (`"Best: " + score`), no fragments, no English plural tricks (`"bead" + if (n == 1) "" else "s"`): use `plurals`.
5. **One module at a time.** Each module is moved to resources, translated, checked on the phone and approved before the next starts.

## Workflow for one module
1. `python3 tools/l10n/scan_static.py --module <name>` (static texts) and `--kind literal` (hard-coded texts in code). A module is done when both are clean.
2. Move the texts into `res/values/strings.xml` (names like `home_menu_free_mode_title`), keep English identical, build, check the screens on the phone.
3. Translate into `res/values-xx/strings.xml` using `glossary.md`.
4. `python3 tools/l10n/check_strings.py` must show no errors (placeholders, `<b>` / `<font>` tags, apostrophes, plural quantities).
5. Check the screens in a few languages (and `en-XA` / `ar-XB` pseudo-languages in debug builds): clipped or overflowing text, wrong fonts, mirrored layout.
6. Owner approves, commit.

## Tools (`tools/l10n/`)
- `scan_static.py`: finds static texts and hard-coded literals per module.
- `check_strings.py`: checks every `values-xx` against `values`.
- `unused_strings.py`: strings in `values/strings.xml` that nothing refers to (about half of the file): remove them before translating.

## Which languages the user sees
`LanguageManager` lists the languages. The Remote Config key `enabled_languages` (for example `fr,es,pt,tr,ru`) decides which ones the picker offers besides English; missing means English only. Debug builds offer all of them.
