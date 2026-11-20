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

## 6. The timed Home offer and the "Biggest offer" campaign: `home_offer` (JSON, Remote Config)
One JSON value. Old app versions ignore the fields they do not know, so the new fields are safe to publish, but publish them only after the new builds
(Android 16.0.6 / iOS 1.0.35) are live: before that, old versions run the per-device window from `duration_min` and never lock any plan.

```json
{"enabled": true, "name": "Biggest Offer", "product": "yearly",
 "end_at": "2026-12-15T18:29:59Z", "campaign": "biggest"}
```

| Field | Meaning |
|---|---|
| `enabled` | Master switch. `{"enabled": false}` turns everything off at the next fetch. |
| `product` | `yearly` (the `.offer` yearly product replaces the regular yearly while it runs) or `lifetime`. |
| `duration_min` | Optional when `end_at` is set (new builds ignore it then). Per-device window in minutes from the first Home visit, used only by OLD builds that do not know `end_at`: with it they show the plain `.offer` swap (no lock, no badge, not tied to `end_at`, a late phone runs past the end date); without it they show no offer at all. Leave it out to keep old builds out of the campaign. |
| `end_at` | NEW, optional. ISO-8601 with a zone (`Z` = UTC). Every phone counts down to the same moment and the offer is over after it. When present it replaces the per-device window. India midnight 16 Dec = `2026-12-15T18:29:59Z` for 15 Dec end of day. |
| `no_end` | NEW, optional. `true` = an offer with NO end: it runs for every phone until you set `enabled` to `false`, and no countdown shows anywhere (plan cards, Home card). It beats `end_at`, `duration_min` and `trigger`, which are ignored. Note `duration_min: 0` on its own does NOT mean "no end": it means OFF. Old builds do not know `no_end`: they read `duration_min` 0 / missing as OFF and show no offer. |
| `campaign` | NEW, optional. `"biggest"` = "Biggest offer" badge, and every other plan stays visible with its price but is **greyed and cannot be bought** while the `.offer` plan of `product` is on. With `"product":"yearly"` the yearly `.offer` plan is the one to buy (monthly and lifetime greyed). With `"product":"lifetime"` the lifetime `.offer` plan is (monthly and yearly greyed) and the first-launch page shows and leads with Lifetime, which it normally hides. |
| `name` | English only. Other languages show the translated static title. |

The campaign lock is fail-open: the other plans are disabled only when the `.offer` package of `product` exists in the RevenueCat offering, is buyable in
the user's store, and its price is lower than the regular price of the same plan (for example a country where the `.offer` product has no price shows normal plans).
The two `.offer` products: yearly `com.abacus.puzzle.1year.offer` (Play base plan `1year-offer`) / `com.vedaavi.learning.1year.offer` (App Store), and
lifetime `com.abacus.all.offer` (both stores, a one-time product).
A plan the user already owns, or one assigned by the admin, is never disabled.

An offer with no end, with or without the campaign:

```json
{"enabled": true, "name": "Special Offer", "product": "lifetime", "no_end": true}
{"enabled": true, "name": "Biggest Offer", "product": "yearly", "campaign": "biggest", "no_end": true}
```

The prices are NOT in Remote Config: the offer price is whatever price the `.offer` product has in each store (any amount, per country), and the struck-through
price and the "% OFF" come from the two store prices. Renewals stay at the `.offer` product price.

**Ending it.** Neither store can end a separate product on a date (Google Play has start/end dates only on *offers* of a base plan; App Store Connect only on
introductory offers). Never use "Remove from sale" on the `.offer` product: Apple then stops the renewals of everyone who already paid. The end comes from:
1. `end_at` in this JSON (the app stops the offer on its own and the regular plans come back);
2. optionally, a Remote Config **Date/Time condition** that serves `{"enabled": false}` after the date (evaluated when the app fetches, at the next app start);
3. optionally, a RevenueCat Targeting rule with an end time that removes the `.offer` packages from the `default` offering (times are UTC).
