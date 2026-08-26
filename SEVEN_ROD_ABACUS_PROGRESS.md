# 7-Rod / 13-Rod Abacus Setting — Progress Summary

Branch: `feature/configurable-abacus-rods` (off `revanuecut_migration`, pushed to origin)
Status: Implemented, reviewed, all found bugs fixed, Android build verified green. **Not yet merged.**

## What this feature does

New Settings toggle — **"7 Rods Abacus"** (default OFF = today's 13-rod behavior, unchanged for existing users):

- **OFF (default):** no change at all. Free Mode and Abacus Practice keep the current 13-rod abacus everywhere.
- **ON:** Free Mode and Abacus Practice show a simpler 7-rod abacus (no decimal/remainder side). Division questions dynamically get `7 + dividend_digit_count + 1` rods — enough to hold the dividend on the right side while working through the problem, plus one buffer rod. Example: 48 ÷ 8 → dividend "48" is 2 digits → 3 right rods → 10 total, abacus shows "048" on the right side at the start.
- Setting takes effect on next visit to the screen, not live mid-session.
- Out of scope (explicitly untouched): Exercise, CCM, Exam, Levels 1–3, Settings theme preview.

## Commits on this branch

1. `fe01b81` — Core feature: new setting, `AbacusCalculations`/`MathUtils` generalized to any column count, Free Mode + Practice wiring, resize-in-place mechanism.
2. `5d06d10` — Fixed: Free Mode "how to use abacus" tour pointed at the wrong rod in 7-rod mode (hardcoded pixel offsets assumed 13 columns); division rod-count formula was originally based on remainder digits (too small) — corrected to dividend digits + 1.
3. `308fb99` — Full code review pass (8-agent multi-angle review) found and fixed: two more tour steps ("Unit place", "Addition top bead") missed by the tour fix, and a pre-existing bug where `isDivisionQuestion` was never reset, causing division notation ("X < Y") to leak into non-Division questions.

## Key files touched

- `AbacusCalculations.kt` — column count now resizable in place (`resizeColumns()`), `recalcTotal()` generalized.
- `MathUtils.kt` — `calculateRodMovements()`'s rod-index formula generalized.
- `BaseAbacusViewModel.kt` — shared `is7RodsModeEnabled` / `currentNumberOfColumns` getters.
- `AbacusDoPracticeViewModel.kt` — `computeNumberOfColumns()`, resize-per-question, division formula fixes.
- `AbacusFreeModeViewModel.kt` / `AbacusFreeModeScreen.kt` — column count read once per visit.
- `AbacusWithDecimalCanvas.kt` — tour highlighter positions generalized to any column count.
- `SettingViewModel.kt` / `SettingsScreen.kt` / `AppConstants.kt` — new toggle.

## Verified

- `./gradlew.bat :app:assembleDebug` — clean build.
- Full 8-angle code review (correctness, removed-behavior, cross-file, reuse, simplification, efficiency, altitude, conventions) + independent verification pass on the strongest candidates.

## Not yet done — pick up here on Mac

1. **Manual QA in the emulator/device** — this session never ran the app UI (Windows box, Compose preview only goes so far). Check:
   - 13-rod mode (default) looks and behaves identical to before, across Free Mode, Practice (all 4 question types), and the tour.
   - 7-rod mode: Free Mode shows 7 rods; Practice shows 7 rods for Number/AddSub/Multiplication and `7 + N` for Division; the tour highlights the correct rod at every step.
2. Review the two skipped cleanup items (not bugs, just duplication — see code review findings): the `numberOfColumns - 7` formula and the "7-or-13 from setting" ternary are each repeated at several call sites instead of one shared helper. Fine to leave, but worth a look if you're already in this code.
3. Once QA passes on both platforms, merge to `revanuecut_migration` (create the PR from the link GitHub prints on push, or via `gh pr create`).

## Known caveat carried over from the iOS side

The iOS branch (`iOS_Abacus`, same branch name) has never been compiled — this session ran on Windows with no Swift toolchain, so iOS changes were only reviewed line-by-line + brace-balance checked. **Build and smoke-test iOS first** before trusting it as much as the Android side.
