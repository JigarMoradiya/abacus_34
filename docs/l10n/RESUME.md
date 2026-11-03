# How to continue the localization (read this first)

Worktree: `/Users/jigarmoradiya/Documents/newProject/abacus_l10n/android`, branch `feature/localization`. Never edit or build in the owner's main
`abacus/android` folder (Android Studio is open there). Commit locally after every verified module, **never push** unless the owner asks.
Status log: `docs/l10n/PROGRESS.md` (last row = last finished module). Decisions: `docs/l10n/DECISIONS.md`. Plan: `~/.claude/plans/generic-greeting-pixel.md`.

## What is left (in this order)
Done and committed: M0-M17 (see PROGRESS.md; M18 notifications and M19 server text are done when PROGRESS.md has their rows).
Next: M20 leftovers (find them with `scan_static.py --rest`, `--kind literal`, `module_keys.py <module>` for every module, and a full route walk with
`walk_routes.py`) -> M21 Arabic / right-to-left pass (`LanguageManager.RTL_READY`, `apply.py --with-ar`, LtrIsland for abacus/math, mirrored arrows,
Arabic texts for the strings that skipped it).
**STOP before M22 (Practice page titles from the server): the owner wants to discuss it first.** M23 = release prep (owner).

## Fast pipeline for a module (used for M13-M19)
1. An edit script (see `tools/l10n/lit_convert.py`, `games_batch.py`, or a one-off script) moves the literals into `strings.xml` and writes `docs/l10n/translations/pending_<batch>_en.json`.
   `tools/l10n/make_pending.py` adds the existing keys a folder uses that have no translation; `tools/l10n/prefill_pending.py <batch>` reuses translations of identical English.
2. One helper agent per language (model sonnet) reads `docs/l10n/TRANSLATOR_BRIEF.md` + the pending file and writes `pending_<batch>_<lang>.json` (validation line in the brief).
3. `python3 tools/l10n/merge_pending.py pending_<batch> <master_name>` -> `apply.py` -> `check_strings.py` (0 errors) -> `harmonize.py <masters>` for identical headings.
4. Build + install, check on the phone (`route_shots.py`, `howto_shots.py`, `game_walk.py`, one small `sheet.py` image), PROGRESS.md row, local commit, touch the heartbeat.
Watch for: a literal that sits in a view model (use a `@StringRes` id + args, e.g. `GameText`), strings with `CDATA` (the tools strip it), English plural nouns after numbers (use plurals).

## Owner rules (do not break)
- Android only for now; one module at a time; verify each on the real phone before the next; local commits; no push.
- Languages: fr es pt tr ru ja th together per module; Arabic last. Digits are always 0-9 (also in Arabic/Thai). Follow the phone language + Settings picker.
- No visible text in static places (const val, companion, enum text, object/top-level lists, data-class defaults): keep a `@StringRes` id and resolve it when drawn.
  Values sent to the server or compared in code stay English constants with a separate translated label (see `Exercise.title/titleRes`, `AbacusFormulaType`).
- Take care of left/right direction and spacing: start/end not left/right; check the longest languages (fr ru pt tr, +30-40%) on a contact sheet; shorten text or
  let it shrink/scroll instead of clipping. Tone: children informal, parents polite (see glossary.md).
- Do not touch the Play build on the phone; the test copy is `com.abacus.puzzle.l10n` (debug, installs next to it, built with `-Pl10nCopy`).
- Never use an emulator (owner: real device only). Phone id `1d04811b`. Keep it awake: `adb -s 1d04811b shell svc power stayon true` (set it back to false at the very end).
- Few images: one small contact sheet per module, delete temp media.

## One module, step by step
1. `python3 tools/l10n/scan_static.py --module <name>` and `--kind literal`; `python3 tools/l10n/scan_layout.py <folder>`; `python3 tools/l10n/module_keys.py <name>` (existing strings the module uses that still have no translation). `--rest` lists code outside every module.
2. Move every visible text into `app/src/main/res/values/strings.xml` (English identical to before; plurals for counts; whole sentences with `%1$s` placeholders, never fragments). Apostrophes `\'`, `&amp;`, line breaks `\n`. A string that Kotlin runs through `htmlToAnnotatedString` is written as TEXT (`&lt;b>`); a string read with `getText` has real tags.
3. Write the translations as a master file `docs/l10n/translations/mNN_<module>.json` (see `tools/l10n/phone/example_translation_script.py` for the shape: key -> {fr,es,pt,tr,ru,ja,th}; plurals as {"plural": {lang: {quantity: text}}}; Arabic may be added later). Run the script with `python3 -c "exec(compile(open(f,encoding='utf-8').read(),'x','exec'))"` (Python 3.9 rejects long UTF-8 source lines). Then `python3 tools/l10n/apply.py` and `python3 tools/l10n/check_strings.py` (0 errors). `%` in a format string is `%%`; French gets a no-break space before ! ? : ;.
4. Build + tests: `./gradlew :app:testDev1DebugUnitTest :app:assembleDev1Debug -Pl10nCopy -q` (all unit tests must pass). Install: `adb -s 1d04811b install -r app/build/outputs/apk/dev1/debug/app-dev1-debug.apk`.
5. Check on the phone: `python3 tools/l10n/phone/route_shots.py <outdir> <Route> fr ru ja ...` (deep-link route list in `walk_routes.py`), `preview_shots.py <outdir> <screen>:<lang> ...` (debug previews in `AbKitGallery.kt`, add more there when a dialog is hard to reach), `sheet.py out.png <scale> frames.raw ...` (PORTRAIT=1 for portrait screens), `walk_routes.py '<regex>' 6` for a crash check. Look at ONE contact sheet; fix clipped text.
6. Update `docs/l10n/PROGRESS.md` (new row), commit: `git add -A app docs tools && git commit -m "Localization MNN: ..."` with the Co-Authored-By trailer. Touch the heartbeat (below).

## Gotchas learned
- The application context (view models) does not follow the app language on Android 12-: use `context.inAppLanguage()` / `withLanguage(tag)`; spoken words follow the VOICE language (`TextToSpeechManager.words/phrase`).
- Dates/numbers for text that is parsed or sent: `Locale.US`; for display `DateTimeUtils.displayLocale()` (Gregorian, digits 0-9).
- `AbShrinkText` shrinks one line; for tiles use short titles of their own; when a screen can overflow use `verticalScroll` + `heightIn(min = room)` (see onboarding feature cards).
- Debug previews only: names containing Preview/Gallery/AbDebug are skipped by the scanner.

## Heartbeat (so two agents never work at once)
The working agent touches `/Users/jigarmoradiya/Documents/newProject/abacus_l10n/.l10n-heartbeat` at least every 20 minutes (`touch` it after each build/commit).
A scheduled task checks it every hour: if it was touched in the last 100 minutes someone is working, so it does nothing.
