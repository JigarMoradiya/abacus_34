# New languages: German, Indonesian, Vietnamese (wave 1) and Chinese, Korean, Hindi (wave 2)

Owner decision 2026-11-25: add `de id vi` first, then `zh ko hi`, Android and iOS, every string. All work on branch `feature/common`, commit only when the owner says so.
The old recipe (fr es pt tr ru ja th ar) is in README.md / RESUME.md. This file is what is NEW for the second round and the single place to see where the work stands.

## Decisions (mine, owner can change)
- Chinese = Simplified (`zh`) only. Traditional later.
- Digits always 0-9. No Devanagari / Hangul-number / Chinese numerals for digits.
- Android resource folder for Indonesian is `values-in` (the legacy code Android uses); the app tag stays `id`. `LanguageManager` maps `in` -> `id`.
- Chinese, Korean, Hindi use the phone's own fonts (as Japanese does); Vietnamese and German are Latin script, Baloo 2 covers them (check the look on a phone).
- Address forms: children informal, parents polite (same idea as fr tu/vous): de du / Sie, id kamu / Anda, vi bạn / bạn (parent role noun "phụ huynh").
- Plural quantities needed: de one+other, id other, vi other (zh ko other, hi one+other).

## How translation quality is protected (owner: "make sure AI translation is proper")
1. **Style + glossary first** (`glossary_newlangs.md`, `TRANSLATOR_BRIEF_NEWLANGS.md`): one word per idea, address form, typography, length rules. Written before any translating.
2. **Translator = the strongest model, one agent per language per chunk.** It sees the English, the key name and the module (context), the glossary and the brief. Chunks are about 200 entries grouped by module so one agent sees related texts together.
3. **Independent reviewer = a different agent** reads English + translation side by side for every string and fixes: wrong meaning, unnatural wording, glossary breaks, wrong register (du/Sie, kamu/Anda), inconsistent terms, text too long for a small button, missing or changed placeholders / emoji / numbers. It writes only the strings it changed, with a one-line reason (kept in `review_log/`).
4. **Mechanical checks** (never skipped): `newlang.py merge` (keys, plural quantities, placeholders), `check_strings.py` (placeholders, tags, apostrophes, plurals), length ratio against English (flag > 1.7x and anything that is a button or chip), identical-to-English leftovers, Unicode NFC for Vietnamese.
5. **High-risk texts get a second, separate pass**: paywall, parent area, parental gate, notifications, review prompts, error messages, store listing. Back-translation by a third agent (target -> English) is compared with the original English meaning.
6. **Looked at on a phone** in the longest wording (German) and tall script (Vietnamese) before a language is called done. iOS reuses the Android masters where the English is identical, so a fix is made once.
7. **Owner-side**: a native speaker skims paywall + store text per language before the language goes into `enabled_languages` (the kill switch). Until then the language stays off in Remote Config.

## Pipeline (Android masters -> iOS)
```
python3 tools/l10n/newlang.py make                 # writes docs/l10n/translations/pending_nl_NN_en.json (chunks)
# one translator agent per chunk per language writes pending_nl_NN_<lang>.json
# one reviewer agent per chunk per language writes pending_nl_NN_<lang>_rev.json (only changed keys)
python3 tools/l10n/newlang.py merge de id vi       # adds the languages to the master files, deletes the pending files
python3 tools/l10n/apply.py && python3 tools/l10n/check_strings.py
```
iOS: `docs/localization/tools/ios_apply_catalog.py` reuses the Android translation when the English matches; the iOS-only strings get their own chunks afterwards.

## Code part (per language, both platforms)
Android: `LanguageManager.all` + `language_names.xml`, `TtsLocale.defaultFor`, `NumberWords` (+ golden rows in `number_words_golden.tsv`), tool language lists (`apply.py`, `check_strings.py`, `merge_pending.py`), `values-xx/app_name.xml`, Practice page-title patterns (`practice_patterns.py`), `video_<tag>` / `paywall_social_proof_<tag>` Remote Config text.
iOS: `AbLanguage.all` + `pluralCategory`, `TextToSpeechManager` locale, number words, `InfoPlist` app name, `logo_<tag>` asset, catalog tool language lists.
Not code: logo with the word in the language, store listing + screenshots, console values (owner).

## Status
| step | de | id | vi | zh | ko | hi |
|---|---|---|---|---|---|---|
| decisions + glossary + brief | doing | doing | doing | - | - | - |
| Android code part | - | - | - | - | - | - |
| Android strings (translate + review) | - | - | - | - | - | - |
| iOS code part | - | - | - | - | - | - |
| iOS strings | - | - | - | - | - | - |
| high-risk second pass | - | - | - | - | - | - |
| looked at on a phone | - | - | - | - | - | - |

## English source issues spotted while translating (not fixed here, owner may decide)
- `l1_rod_zero` says "all beads down", but at 0 the upper bead sits up, away from the beam. "all beads away from the beam" (as `l1_instr_zero` says) would be exact; fr / es / pt translated the English literally.
- `l2_lesson5_step2_body` says "swap to heaven" for 3 + 5, but the animation only lowers the heaven bead and keeps the 3 earth beads (no swap); the Vietnamese translator wrote the accurate move.
- `htp_balloon_pop_6` says "Pop 5 in a row", but the code (BalloonPopPlayViewModel, popsTowardRotation) counts 5 correct pops and does not reset on a wrong tap: the English is slightly inaccurate.
- `htp_balloon_pop_16` says "No punishment for mistakes", but the code takes 5 points off for each wrong tap on every level except Easy.

## Found while reviewing (must fix before Vietnamese is released)
- **Vietnamese font gap (Android):** `app/src/main/res/font/baloo2_extrabold.ttf` and `baloo2_bold.ttf` (used by `AbTypography.fixed` on all game screens) are a Latin-only subset and lack 14 Vietnamese precomposed glyphs (ụ ơ ị ạ ư ả ẻ ẽ ỉ ỏ ủ ỳ ỷ ỹ). Android falls back to the system font for those letters inside a word, so Vietnamese game labels show mixed fonts. Fix: replace them with the full Baloo 2 build that includes the Vietnamese subset (`font_bold.ttf` already has all glyphs). Check the iOS font files for the same gap. German and Indonesian are not affected.
- Place Value game, 5th slot label in Vietnamese is two lines ("Chục\nnghìn"): one line would be 71dp in a 52dp slot.
