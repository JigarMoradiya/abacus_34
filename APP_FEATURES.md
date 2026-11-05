# Abacus App — Complete Feature List (Post-Freemium + RevenueCat)

Last updated: 2026-06-17

---

## Access Model

| Feature | Free | Premium |
|---|---|---|
| Abacus Free Mode | Full access | Full access |
| Abacus Practice | Level 1 full + Level 2 first 20 pages | All 8 levels, all pages |
| Learning Path (Levels 1–4) | Level 1 full + Level 2 Ch.1–2 only | All 4 levels |
| Exam | Addition only, Beginner only | All types + all difficulties |
| Exercise | 1st + 2nd variant of addition & subtraction only | All variants |
| Custom Challenge Mode (CCM) | Locked | Full access |
| Math Game Zone | Number Sequence 3×3 only | All games + all board sizes |
| Report History | Last 3 results only | Full history |
| Settings | Theme, Voice, Background Music only | All settings |

**Subscription handled via RevenueCat** (both Android + iOS). Plans: Weekly, Monthly, Yearly, Lifetime (discount/non-discount variants). Which plans appear is controlled from Firebase Remote Config. Admin panel can also assign plans directly to users.

---

## Authentication

- **Google Login** (Android)
- **Apple Login** (iOS)
- **Email / Password** — admin-created accounts only, for specific users
- Post-login: syncs subscription state and progress from server

---

## Home — 4-Level Learning Path

4 landscape cards on the home screen. Each card shows level icon, level tag, title, subtitle, and progress dots.

- Levels 1–3 unlock in sequence (free users unlock Level 1 fully; Level 2 partially)
- Level 4 always unlocked (it is the existing Times Table)

---

## Level 1 — Bead Basics (Age 4–6)

**5 lessons.** Each lesson has 3 phases: **Learn → Practice → Quiz**

| Lesson | Topic |
|---|---|
| 1 | Meet the Abacus (parts, tap to move beads) |
| 2 | Numbers 1–4 (earth beads only) |
| 3 | Number 5 & Beyond (heaven bead, values 6–9) |
| 4 | Place Value (tens and ones columns) |
| 5 | Numbers to 99 (2-digit recognition) |

- **Learn phase**: Animated abacus (left panel) + step-by-step instruction text (right panel). Student taps beads to follow along.
- **Practice phase**: Interactive abacus on left, target number on right. 5 problems per lesson.
- **Quiz phase**: Abacus configuration shown → student picks correct value from 4-choice MCQ. 10 questions. Star rating on result.

**Free**: Full Level 1 access.

---

## Level 2 — Addition & Subtraction (Age 6–8)

**10 chapters.** Each chapter: **Learn → Practice → Quiz** (except Chapter 5 which is Learn only).

| # | Chapter | Scope | Columns |
|---|---|---|---|
| 1 | Earth Bead Add | Direct addition, result ≤ 4 (earth beads only) | 1 |
| 2 | Heaven Bead Add | Direct addition using heaven bead, no formula | 1 |
| 3 | Earth Bead Subtract | Direct subtraction, earth range only | 1 |
| 4 | Heaven Bead Subtract | Direct subtraction, heaven bead involved | 1 |
| 5 | Meet the Formulas | Theory only — WHY/WHEN/HOW for all formula types | 1 |
| 6 | Small Friend + | +1=−4+5, +2=−3+5, +3=−2+5, +4=−1+5 | 1 |
| 7 | Small Friend − | −1=−5+4, −2=−5+3, −3=−5+2, −4=−5+1 | 1 |
| 8 | Big Friend + | +1=+10−9 … +9=+10−1 (carry to tens) | 2 |
| 9 | Big Friend − | −1=−10+9 … −9=−10+1 (borrow from tens) | 2 |
| 10 | Family | +5−10 and −5+10 combined operations | 2 |

**Chapter 5 detail**: Animated slides only. Explains earth bead limits, blocked-case demos, when each formula triggers, formula tables with animation. Dynamic examples generated on every visit (not hardcoded).

**Dynamic example rule**: All practice and quiz problems are runtime-generated using formula-specific generators per chapter — never hardcoded lists.

**Free**: Chapters 1–2 only (first 20 pages of Level 2).

---

## Level 3 — Speed & Mental Math (Age 8–10)

5 modes, all unlocked once Level 3 opens (non-sequential). Reuses the CCM engine with preset configs.

| Mode | Description |
|---|---|
| Guided Series | Abacus visible, 2-digit problems, student moves beads |
| Semi-Anzan | Numbers flash on screen, no abacus, type answer |
| Full Anzan | TTS only, no visual, type answer |
| Speed Drill | 60-second timer, single additions, numpad input |
| Flash Challenge | Difficulty presets: Easy / Medium / Hard / Expert |

---

## Level 4 — Times Tables (Age 10+)

Entry card → table picker grid (×1 to ×20) → existing Today's Table screen (unchanged).

---

## Abacus Free Mode

Learn the abacus structure interactively. Always fully free.

- **Understanding the Abacus Structure** — labelled parts with tooltips
- **Bead Types and Values** — earth beads (1 each), heaven bead (5)
- **Finger Movement for Bead Manipulation** — guided finger technique
- **Learning to Read and Set Numbers** — interactive number-setting exercise

Number-setting options:
- Sequential or random number generation
- Configurable number range
- Abacus auto-resets after each number is set correctly

---

## Abacus Practice

Structure: **Level → Pages → Sets → Abacus problems**

- 8 levels total
- 20 abacus problems per set
- 3 set types:

| Set Type | Behaviour |
|---|---|
| Step by Step | Must solve each step correctly before advancing. Shows bead direction hints and/or abacus formula on each step (individually toggleable). Works for addition, subtraction, multiplication, division. |
| Final Answer | Must set the correct final answer before advancing. No step hints. |
| Formal Exam | Free navigation through all 20 problems. Shows result (correct/wrong count) at the end. Timed. Result stored on server for history. |

**Free**: Level 1 fully open. Level 2 first 20 pages open, rest locked.

---

## Exam

- **Operation types**: Addition, Subtraction, Multiplication, Division
- **Difficulty levels**: Beginner, Intermediate, Expert
- **Question types**: Simple math equation, Missing number from equation, Abacus visual question
- 4-choice MCQ format
- Timer runs throughout (no time limit, measures elapsed time)
- Result shown at end and stored on server for history

**Free**: Addition only, Beginner difficulty only.

---

## Exercise

- **Operation groups**: Addition & Subtraction / Multiplication & Division
- **Variant selection**: digit count, number of lines, questions-per-minute targets (e.g. 1-digit, 5 lines, 5 questions in 3 minutes)
- **Answer input**: Abacus or number keypad (user's choice)
- Result shown at end and stored on server for history

**Free**: First 2 variants of addition & subtraction only.

---

## Custom Challenge Mode (CCM)

User-configured dynamic challenge session.

**Configuration options:**
- Number of questions: 5, 10, 15, or 20
- Gap between questions (display duration)
- Question digit range: 2, 3, 4, 5, or 6 digits
- Presentation mode (at least one mandatory): speak question / show in numbers / show in words

**Flow**: Questions appear per config → user calculates mentally (no abacus visible during questions) → sets final answer via abacus or number keypad → result shown and stored on server.

**Free**: Locked entirely. Premium only.

---

## Math Game Zone

4 math-skill games. All are mind-development focused, not just entertainment.

| Game | Variants | Free |
|---|---|---|
| Number Sequence Puzzle | 3×3, 4×4, 5×5 sliding board | 3×3 only |
| Sudoku | 4×4, 6×6, 9×9 with difficulty selection | Locked |
| Math Pyramid | Pyramid steps 2–6 with difficulty selection | Locked |
| Target the Number | 3 difficulty levels | Locked |

**Game logic:**
- **Number Sequence**: Slide tiles to arrange numbers in sequence (1 empty cell)
- **Sudoku**: Each row, column, and grid must contain unique numbers
- **Math Pyramid**: Sum of bottom 2 numbers equals the number above them
- **Target the Number**: Use math operations to reach a target value

---

## Settings

| Setting | Free | Premium |
|---|---|---|
| Abacus Theme | Yes | Yes |
| Voice Pronunciation | Yes | Yes |
| Background Music Volume | Yes | Yes |
| Display Current Number Value | Locked | Yes |
| Display Help Hint (formula, step-by-step mode only) | Locked | Yes |
| Display Bead Direction (step-by-step mode only) | Locked | Yes |
| Abacus for Left-Handed | Locked | Yes |
| Abacus Sums Speak (step-by-step mode only) | Locked | Yes |
| Bead Move Sound | Locked | Yes |

---

## My Account

- Last activity timestamps for Exam, Exercise, CCM
- Subscription status + plan management (via RevenueCat)
- Report History cards (free: last 3; premium: full)
- Settings shortcut
- FAQs
- Need Help / Support
- Privacy Policy
- Logout

---

## Report / History

All Formal Exam, Exam, Exercise, and CCM results are stored on server.

- Viewable from My Account → Report History
- **Free**: Last 3 results only
- **Premium**: Full history

---

## Subscription & Billing

- **Platform**: RevenueCat (both Android + iOS)
- **Plans**: Weekly, Monthly, Yearly (with/without discount), Lifetime (with/without discount)
- **Plan visibility**: Controlled via Firebase Remote Config per plan key
- **Admin override**: Plans can be manually assigned to specific users from admin panel
- **Pending purchases**: Handled at launch and post-login on both platforms
- No more 7-day free trial. Freemium model is permanent — free tier always available, paywalls hit naturally as users progress.

---

## Backend & Data

- Progress, results, and history synced to server
- Local database stores home page level/category/page/set/abacus data
- Firebase Remote Config controls: which home menu items show, which subscription plans appear
- Admin panel: user management, manual plan assignment, content management
