# Remote Config: what to set in the Firebase console for the new languages

Everything below is optional: with nothing set the app shows English for these texts and no extra language in the picker (release builds).

## 1. Which languages the picker offers: `enabled_languages`
A comma list of language tags, for example `fr,es,pt,tr,ru,ja,th`. English is always there. Release builds only show the listed languages;
debug builds show all of them. Remove a tag to switch a language off without a release (a person who chose it falls back to the phone's language, or English).
Arabic (`ar`) is ignored until the right-to-left pass is finished (`LanguageManager.RTL_READY`).

## 2. Server text in other languages
Four texts come from Remote Config and are shown to the parent. Each keeps its current key for the English text. For another language, add the
same key with an underscore and the language tag at the end. A language without its own key shows the English text.

| English key (unchanged) | What it is | Per-language keys |
|---|---|---|
| `video` | JSON list of the tutorial videos (`[{"id":"...","txt":"title","so":1}, ...]`) | `video_fr`, `video_es`, `video_pt`, `video_tr`, `video_ru`, `video_ja`, `video_th` |
| `paywall_social_proof` | one line under the paywall benefits | `paywall_social_proof_fr` ... |
| `newVersionNotes` | HTML note on the login screen | `newVersionNotes_fr` ... |
| `bulkLogin` | HTML note on the login screen | `bulkLogin_fr` ... |

For `video_<tag>` use the same JSON as `video`: keep each `id` and `so`, translate only `txt` (a different `id` is a different video, so
a per-language video is possible too). Texts are translated by you or by the translation helpers; the app reads the value of the language it is in
at the moment the screen opens, so a language change shows the new text after the screen is opened again.

## 3. Not translated here
Practice page titles and descriptions come from the server database, not Remote Config. They are translated inside the app (module M22, pattern map, English fallback), so there is nothing to set in the console. When the server database gets a new kind of title, run `tools/l10n/practice_patterns.py <synced db> --check` (see DECISIONS 22): a title with no pattern shows in English until it is translated.
