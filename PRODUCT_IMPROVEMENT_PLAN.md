# Product Improvement Plan — Abacus App (Android + iOS)

Branch: `feature/product-improvement-plan` (off `revanuecut_migration`, both repos)
Produced by a broad multi-agent audit across 4 lenses (code quality, product/features, performance, testing/reliability) on both platforms. This is a **plan**, not a diff — nothing in this document has been implemented yet. Work happens **one tier at a time**, in the order below, each as its own reviewed commit/PR before moving to the next.

---

## How to read this

Findings are grouped into **6 tiers**, ordered by (impact × how much it unblocks everything after it) ÷ risk. Tier 0 is safe, fast, and should happen first regardless of what else gets prioritized later. Tiers 4+ are where you have real discretion — read the rationale and tell me which ones matter most to you; the ordering there is a reasonable default, not a mandate.

---

## Tier 0 — Do first: safe cleanup + immediate reliability gaps

Low risk, no architecture change, closes the two biggest "we'd find out about this from a bad app-store review" gaps.

1. **Add Firebase Crashlytics to iOS.** Android already has it (`app/build.gradle.kts`: `firebase.crashlytics` plugin + NDK). iOS has `FirebaseCore`/`FirebaseRemoteConfig` but no `FirebaseCrashlytics` — zero production crash visibility on a platform with 227 force-unwrap sites. This is the single highest-leverage reliability fix available.
2. **Stop silently swallowing purchase/restore errors on iOS.** 5 locations (`AuthCore.swift:133`, `FreemiumLoginBottomSheet.swift:231`, `LoginWithCredentialsLandscapeView.swift:231`, `MyAccountView.swift:257,274`, `HomeView.swift:402`) do `_ = try? await Purchases.shared.restorePurchases()` — a failed restore leaves a paying customer silently locked out of content they paid for, with no error shown and nothing logged. Surface the error to the UI and log it (feeds into #1 once Crashlytics exists).
3. **Delete genuinely dead files.** iOS: `AbacusWithDecimalViewTemp.swift` + `ColumnViewTemp.swift` (1,218 lines, confirmed not in `project.pbxproj`), `SubscriptionViewOld.swift` (confirmed unreferenced), stray `Untitled.swift` at repo root.
4. **Resolve the two *live* duplicate Free Mode screens on iOS.** `AbacusFreeModeViewTemp.swift` and `AbacusFreeModeViewTemp2.swift` (600+ lines each) are *still compiled into the app* alongside the real `AbacusFreeModeView.swift` — three implementations of the same screen ship in the binary. Needs verification first (confirm neither is reachable from any nav route), then delete both.
5. **Update `APP_FEATURES.md` on both repos.** It documents 4 Math Game Zone games; the codebase actually ships ~19 (Kakuro, Calcudoku, Merge2048, Sudoku, Number Snake, and 14 more). Stale docs actively mislead planning — including this audit almost scoped around the wrong game count.
6. **Rename or clarify misleading file names.** Android's `OldPurchasesBottomSheet.kt` is still live (used from `PurchaseScreen.kt`) despite its name suggesting deprecation — rename it before someone deletes working code by mistake.

---

## Tier 1 — Centralize the place-value math (highest bug-risk area)

This is the exact class of bug this session spent an entire round fixing (division formula off-by-one, stale division-notation flag, tour rod misalignment). The root cause on both platforms is the same: **the "split total value into integer part / decimal-or-remainder part" logic is reimplemented at every call site instead of living in one place.**

7. **iOS: extract one shared helper for the decimal split.** The `totalValue % 1000000` / `.prefix(7)` / `.suffix(6)` / `leadingZeroesRegex` block appears near-verbatim in 6 files (`AbacusFreeModeView.swift`, both Temp variants, `AbacusPracticeView.swift` in 5 separate spots, `AbacusWithDecimalView.swift`). `MathUtils.swift` already exists as the natural home — move the logic there once, have every call site use it.
8. **Both platforms: name the `7`/`13`/rod-count constants.** Currently bare literals scattered across `AbacusCalculations`, `MathUtils`, ViewModels, and Composables/Views on both platforms (this is exactly what caused the 7-rod feature's bugs to be easy to miss). One named constant (`INTEGER_RODS = 7`, or similar) per platform, referenced everywhere instead of re-typing `7`.
9. **Both platforms: verify VM/View column-count pairs stay in sync.** Several ViewModel+Composable/View pairs each hardcode the same number independently (e.g. Android: `ExerciseViewModel.kt` `=7` / `ExerciseAbacusRow.kt` `=7`; iOS: constructor defaults duplicated at call sites) — same fragility class as #8, fix together.

---

## Tier 2 — Fix the two real, user-noticeable performance issues

Everything else the performance survey found is minor or platform-appropriate already (Android's Canvas-based abacus rendering and preloaded `SoundPool` are both already well-architected). These two on iOS are real:

10. **iOS: stop rebuilding the entire column tree on every bead move.** `AbacusWithDecimalView`/`AbacusWithoutDecimalView` bump `viewId = UUID()` on every `abacusState` change and apply it via `.id(viewId)` on the whole column `HStack` — this destroys and recreates ~180 views (13 columns × beads × gestures) on every single bead drag frame, not just re-renders them. This is the standout jank source during rapid dragging. Fix: stop resetting `.id()` on bead-level changes; only reset it for genuine structural changes (column count changes, which is the resize feature we just built — that's the one case where `.id()` reset is actually correct).
11. **iOS: preload/reuse `AVAudioPlayer` instances instead of allocating one per bead move.** `AudioPlayerManager.swift` currently does a fresh `Bundle.main.url` lookup + disk read + decode + `AVAudioPlayer(contentsOf:)` on every single sound trigger, including every bead move — contrast with Android's `SoundPool`, which preloads once. Switch to `AVAudioPlayer` pooling or `AVAudioEngine`/`AVAudioPlayerNode` with pre-buffered assets.

---

## Tier 3 — Testing foundation

Both platforms currently have effectively **zero** real test coverage (Android: 2 unimplemented template stubs; iOS: no test target exists at all) and **no CI** on either repo.

12. **Add an iOS test target** (`AbacusTests`) and write the first real unit tests for `AbacusCalculations.swift` and `MathUtils.swift` — these are the most fragile, most-frequently-broken files in the whole app (per this session's own history).
13. **Add real Android unit tests** for `AbacusCalculations.kt` and `MathUtils.kt`, replacing the do-nothing `ExampleUnitTest.kt`.
14. **Basic CI on both repos** (GitHub Actions): build + run unit tests on every push/PR. Doesn't need to be elaborate — the goal is "broken code can't merge silently," which is true today on both repos.

---

## Tier 4 — Architecture consistency (larger, do incrementally)

Not urgent, but this is where "adding a new feature takes longer every time" comes from. Recommend tackling one feature area at a time rather than a big-bang rewrite.

15. **iOS: introduce a ViewModel layer for the core paid flows**, starting with `AbacusPracticeView.swift` (1,376 lines — logic, state, and UI fused in one View struct) since it's the most revenue-critical and most complex. `HomeView.swift` (1,251 lines), `ExamView`, `ExerciseView`, `CCMView` follow the same pattern and should get the same treatment over time. The ~20 newer Math Game Zone ViewModels already prove this pattern works well in this codebase — extend it backward to the older, bigger screens.
16. **Android: align the Math Game Zone's ~35 ViewModels to the shared `StatefulViewModel` convention** used by Exam/Exercise/CCM/Practice/Settings, instead of plain `ViewModel()`. Two parallel state-management conventions for the same kind of screen means fixes to one don't propagate to the other.
17. **Android: bring the 4-Level Learning Path (Levels 1-3, ~20 files) into the MVVM+Hilt convention.** Currently the entire feature drives problem generation and animation state directly from Composable `remember`/`mutableStateOf`, an architectural island versus the rest of the app.

---

## Tier 5 — Product / feature ideas

Sized against the *existing* architecture, not a rewritten one. Ordered by effort (small first) so early wins are visible quickly; reorder freely based on what matters most to you.

**Quick wins (S):**
- Shareable milestone/achievement certificates (native share sheet — no share/export feature exists today).
- Accessibility-friendly theme variants (color-blind-safe, dyslexia-friendly font) — the theme system is already pluggable, just needs the variants.
- Offline-state handling + a simple "you're offline, results will sync later" indicator (no connectivity monitoring exists on either platform today).

**Medium (M):**
- iOS VoiceOver/accessibility pass — Android already has 122 `contentDescription` tags across 68 files; iOS has zero `accessibilityLabel` usage. Real parity gap for a kids' app that may be used by mixed-ability households.
- First-run onboarding flow — nothing like this exists; users land straight on Home today.
- Badges/achievement system beyond the existing per-lesson star ratings.
- Adaptive review of previously-missed problem types (spaced repetition) — reuses result data that's already stored.
- Streak-save nudge ("about to lose your streak") — Android has the WorkManager infra already (`StreakNotificationWorker`); iOS would need its notification base built first (ties into the push-notification item below).
- Referral program (parent-invites-parent) — no referral system exists in the current RevenueCat/account flow.

**Large / strategic (L):**
- Real push notifications (FCM + APNs) — today only Android has *local* WorkManager reminders; iOS has no notification channel at all. This is also a prerequisite for several of the Medium items above.
- Multi-language localization — iOS only has `en.lproj`; Android has no source-level `values-xx` folders. Hindi/Spanish/Arabic would fit this app's likely demographic best.
- Parent dashboard / progress digest beyond the current local weekly summary.
- Multi-child family profiles under one subscription (no evidence this exists — RevenueCat entitlement looks single-user).
- Opt-in leaderboard across the Math Game Zone's ~19 games (needs a backend service).

---

## Explicitly not recommended right now

- **Adding more mini-games.** The product survey initially assumed a content gap here; it doesn't exist — 19 games already ship. Effort is better spent on Tier 0-4 and the retention/parent-trust items in Tier 5.
- **A full SwiftUI or Compose rewrite.** The architecture inconsistency is real but incremental (Tier 4) is the right approach given this is a live, monetizing app — a rewrite risks a long feature freeze for a two-repo, one-person-plus-AI team.

---

## Suggested execution order

Tier 0 → Tier 1 → Tier 2 → Tier 3 → Tier 4 (incrementally, alongside normal feature work) → Tier 5 (pick items based on what you want the product to prioritize: engagement, trust, or monetization).

Tell me when you're ready to start on Tier 0, and we'll do it item by item, each verified and committed before moving to the next — same process as the 7-rod feature work.
