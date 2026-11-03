# Localization progress (Android)

Branch `feature/localization`, worktree `/Users/jigarmoradiya/Documents/newProject/abacus_l10n/android`. Plan: `~/.claude/plans/generic-greeting-pixel.md`.
Rule from the owner (2026-11-03, away): continue module by module, each verified on the phone before the next; the owner tests everything together later. **Stop before M22 (Practice page titles): discuss with the owner first.** Local commits per verified module, never pushed.

How to check on the phone: build the side-by-side copy `./gradlew assembleDev1Debug -Pl10nCopy` (app id `com.abacus.puzzle.l10n`, label "Abacus L10n", installs next to the Play app and never touches it), install with `adb -s 1d04811b install -r`, set a language with `adb shell cmd locale set-app-locales com.abacus.puzzle.l10n --locales fr`, walk routes with the scratch scripts (`walk_routes.py`, `shots.py`).

| Module | Status | Notes |
|---|---|---|
| M0 Foundation | done, committed `adbd1fdf`; 36 routes walked on the phone, 0 crashes, English identical | |
| M1 Settings + language picker + voice | done on the phone (picker switches the language and recreates the screen, theme names, toggles, voice card checked in fr es pt tr ru ja th) | 46 keys x 7 languages; long theme names shortened so the labels do not shrink to tiny text; hours: tracked from M2 on |
| M2 Numbers + speech engine | done on the phone: number words, voice words and spoken phrases checked in en fr es pt tr ru ja th; speech screens open without crash in en and fr; 60 unit tests pass | own `NumberWords.kt` for fr es pt tr ru ja th, checked against 621 numbers per language from a real phone; English keeps its lakh/crore words; zero is now spoken (was silent); about 1 hour |
