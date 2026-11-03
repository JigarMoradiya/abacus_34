# Localization progress (Android)

Branch `feature/localization`, worktree `/Users/jigarmoradiya/Documents/newProject/abacus_l10n/android`. Plan: `~/.claude/plans/generic-greeting-pixel.md`.
Rule from the owner (2026-11-03, away): continue module by module, each verified on the phone before the next; the owner tests everything together later. **Stop before M22 (Practice page titles): discuss with the owner first.** Local commits per verified module, never pushed.

How to check on the phone: build the side-by-side copy `./gradlew assembleDev1Debug -Pl10nCopy` (app id `com.abacus.puzzle.l10n`, label "Abacus L10n", installs next to the Play app and never touches it), install with `adb -s 1d04811b install -r`, set a language with `adb shell cmd locale set-app-locales com.abacus.puzzle.l10n --locales fr`, walk routes with the scratch scripts (`walk_routes.py`, `shots.py`).

| Module | Status | Notes |
|---|---|---|
| M0 Foundation | code done, compiles, 48 unit tests pass; phone check running | |
