# Decisions taken while the owner was away (please review when back)

Each line: what was decided, why, and how to change it.

1. **Test copy next to the Play app.** The phone now holds the Play build (177, installed 2026-11-03 18:27), which must not be touched. Test builds use `-Pl10nCopy` (id `com.abacus.puzzle.l10n`, label "Abacus L10n"). Needs a gated line in `app/build.gradle.kts`, a manifest placeholder for the first FileProvider authority, and a local-only `app/src/dev1Debug/google-services.json` (not committed). Purchases cannot be tested in the copy.
2. **Local commits per verified module, no push.** Keeps every module recoverable. Squash or drop whenever you like.
3. **Portuguese** uses one folder `values-pt` with Brazilian wording (also serves Portugal).
4. **Language names** are shown in their own language and are not translated (`language_names.xml`, `translatable="false"`).
