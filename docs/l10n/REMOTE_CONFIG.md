# Remote Config: what to set in the Firebase console for the new languages

Everything below is optional: with nothing set the app shows English for these texts and no extra language in the picker (release builds).
In the console: Run > Remote Config > Add parameter, then Publish changes. Android and iOS read the same keys.

## 1. Which languages the picker offers: `enabled_languages`
A comma list of language tags: `fr,es,pt,tr,ru,ja,th,ar`. English is always there. Release builds only show the listed languages;
debug builds show all of them. Remove a tag to switch that language off without a release: it leaves the picker AND a person who already chose it
goes back to English (right after the app fetches the new value, and again at every launch from the saved value). Clear the value completely
and every person is back in English and the language row disappears from Settings. Debug builds never reset a language.
Arabic (`ar`) is ready on both platforms (the right-to-left pass is finished, `RTL_READY` / `rtlReady` are on).

## 2. Server text in other languages: ONE value, a JSON object, in a NEW parameter
The apps already live read `paywall_social_proof` as plain text, so DO NOT change that value to JSON (the live apps would show the JSON on the
paywall). Leave it as it is (plain English line) and add a NEW parameter for the JSON: the new apps read the new one first and use the old one
only when the new one gives nothing.

A text that is shown to the parent in the app's language is one Remote Config value that is a JSON object keyed by language tag. A language without
its own entry (or with an empty one) shows the `en` entry. A damaged JSON, or an object without `en` and without the language, gives nothing and the
app goes on with the old plain-text key. Use the console's JSON data type (it checks the JSON before you publish). The app reads the value of the
language it is in at the moment the screen opens, so a language change shows the new text after the screen is opened again.

Only one text works this way: the paywall line (section 3). `video` is not translated (section 4). `newVersionNotes` and `bulkLogin` are not
used for languages: no screen shows them any more (the Android code still reads them, nothing displays them).

## 3. The paywall line: new parameter `paywall_social_proof_languages`
Add a NEW parameter `paywall_social_proof_languages`, data type JSON, with this value (polite form, because a parent reads it). Keep the `en` line the same
as the text of the old `paywall_social_proof` (that old one keeps serving the apps already live):

```json
{
  "en": "Join thousands of students learning with Vedaavi Abacus",
  "fr": "Rejoignez des milliers d'élèves qui apprennent avec Vedaavi Abacus",
  "es": "Únete a miles de estudiantes que aprenden con Vedaavi Abacus",
  "pt": "Junte-se a milhares de alunos que aprendem com o Vedaavi Abacus",
  "tr": "Vedaavi Abacus ile öğrenen binlerce öğrenciye katılın",
  "ru": "Присоединяйтесь к тысячам учеников, которые учатся с Vedaavi Abacus",
  "ja": "Vedaavi Abacusで学ぶ何千人もの生徒の仲間になりましょう",
  "th": "ร่วมเรียนกับนักเรียนนับพันคนที่ใช้ Vedaavi Abacus",
  "ar": "انضموا إلى آلاف الطلاب الذين يتعلمون مع Vedaavi Abacus"
}
```

To change one language, edit its entry. To hide the line everywhere, empty BOTH `paywall_social_proof_languages` and `paywall_social_proof`.

## 4. The video list (the `video` key): titles only in English
Every tutorial video is narrated in English, so the app shows the title under a video only when the app is in English and hides it in every other
language (the thumbnail with its play button stays). The `video` value stays the JSON list it is today (`[{"id":"...","txt":"title","so":1}, ...]`)
and there is nothing to add per language.

## 5. Not translated here
Practice page titles and descriptions come from the server database, not Remote Config. They are translated inside the app (module M22, pattern map, English fallback), so there is nothing to set in the console. When the server database gets a new kind of title, run `tools/l10n/practice_patterns.py <synced db> --check` (see DECISIONS 22): a title with no pattern shows in English until it is translated.
