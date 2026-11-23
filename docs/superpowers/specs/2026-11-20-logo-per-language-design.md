# Logo per language (short design)

Status: waiting for the owner's approval (2026-11-20). Not committed (owner rule: commit only when asked).

## Intent
The logo picture has the English word ABACUS drawn into it, so it is the one thing on the first screens that stays English in the other eight languages. Show the logo with the word in the app's own language.

## Goals
- Nine finished logos: the same bead characters and star, with the word redrawn in the same bubble style (white letters, blue outline, soft shadow).
- The word is the one the app already shows under its icon (owner can correct any):
  en Abacus, fr Boulier, es Ábaco, pt Ábaco, tr Abaküs, ru Абакус, ja そろばん, th ลูกคิด, ar الأباكوس.
- Android: Home header, splash, landscape login, free-trial dialog. iOS: splash. All follow the language picked inside the app (not the phone's language).
- A language without its own logo shows the English one.

## Non-goals
App launcher icon, store graphics, the notification small icon, the website, any other text.

## Anti-goals (a failure even if it works)
Letters cut off or off-centre; a visible seam between beads and letters; different bead art; Arabic letters not joined; Thai marks misplaced; a logo that is bigger or smaller than today on screen.

## Approaches (owner picked 2)
1. Beads-only picture + word drawn by the app in code: always right, sharp, but flatter than the 3D bubble letters.
2. **Nine finished logos (chosen):** same look as today, nine images per platform, English fallback. Costs app size (kept small with lossless WebP on Android).

## How (builder's calls, marked as mine)
- Everything for the logo lives in one folder, `abacus/store_screenshots/logo/`, next to `banners/`, `final/` and `sources/` (owner's request): `source/` the original logo, `fonts/` the downloaded fonts, `out/` the nine finished logos, and the script. The two apps get copies of the finished images.
- One repeatable script in that folder cuts the beads from the current logo, draws the word, writes the nine images. The word is drawn with a real text engine (headless Chrome) because Pillow here has no shaping for Arabic and Thai. The fonts are used only to draw the pictures, they are not put in the apps.
- Fonts (all free, OFL): Baloo 2 ExtraBold (Latin, already in the app), Nunito Black (Russian), Mitr Bold (Thai), Baloo Bhaijaan 2 (Arabic), M PLUS Rounded 1c ExtraBold (Japanese).
- Android: `drawable-<lang>-nodpi/logo.webp` next to the English `logo`: the same qualifier system the strings already use, so no code change; check on the phone that the in-app language picks it.
- iOS: image sets `logo_<tag>` plus one small helper that picks by `AbLanguage` and falls back to `logo`, because the asset catalog follows the phone's language, not the in-app one.
- Same word width rule on every language: the word is centred under the beads and scaled to the width of today's ABACUS, so on-screen size does not change.

## Verification
- One contact sheet of the nine logos to approve the look before anything is wired.
- Redmi (real device): Home header and splash in every language, plus the free-trial dialog and landscape login in two.
- iOS: asset catalog compile and Release build only (no simulators).
