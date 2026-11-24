package com.jigar.me.ui.view.home.screens.levels

import androidx.annotation.StringRes
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.screens.math_game_zone.common.drawConfetti
import kotlinx.coroutines.delay
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowCircleRight
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbChip
import com.jigar.me.ui.view.ab.AbParentalGate
import com.jigar.me.ui.view.ab.AbPopupCard
import com.jigar.me.ui.view.ab.AbPopupLayer
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.common_ui.dialogs.FreemiumPaywallBottomSheet
import com.jigar.me.ui.view.home.common_ui.dialogs.PaywallContext
import com.jigar.me.ui.view.home.common_ui.rememberHasFullAccess
import com.jigar.me.ui.view.home.screens.levels.level1.level1Lessons
import com.jigar.me.ui.view.home.screens.levels.level2.Level2ChapterType
import com.jigar.me.ui.view.home.screens.levels.level2.level2Chapters
import com.jigar.me.utils.AppAnalytics

/** Level 1 progress. The quiz of each of the five lessons saves `l1_quiz_stars_<id>` (0..3, a finished quiz is at least one star). */
object Level1Progress {
    private const val SHOWN_KEY = "l1_complete_card_shown"

    fun stars(prefs: AppPreferencesHelper, lessonId: Int): Int = prefs.getCustomParamInt("l1_quiz_stars_$lessonId", 0)
    fun lessonsDone(prefs: AppPreferencesHelper): Int = level1Lessons.count { stars(prefs, it.id) >= 1 }
    fun isComplete(prefs: AppPreferencesHelper): Boolean = lessonsDone(prefs) == level1Lessons.size

    /** The card opens by itself only once; the chip on the Level 1 screen brings it back whenever the level is complete. */
    fun cardShown(prefs: AppPreferencesHelper): Boolean = prefs.getCustomParamBoolean(SHOWN_KEY, false)
    fun markCardShown(prefs: AppPreferencesHelper) = prefs.setCustomParamBoolean(SHOWN_KEY, true)
}

/**
 * Level 2 progress. The quiz of each chapter saves `l2_quiz_stars_<id>` (0..3, a finished quiz is at least one star). Chapter 5 ("Meet
 * Formulas") is reading only: it has no quiz, so it never counts. Chapters 1 to 3 are the free ones (the same rule as the chapter grid:
 * a free user can open the first three).
 */
object Level2Progress {
    private const val FREE_SHOWN_KEY = "l2_free_card_shown"
    private const val DONE_SHOWN_KEY = "l2_done_card_shown"
    const val FREE_CHAPTERS = 3

    /** Chapters that have a quiz (all but the reading chapter 5). */
    val quizChapterIds: List<Int> get() = level2Chapters.filter { it.type != Level2ChapterType.FORMULA_REF }.map { it.id }
    val freeChapterIds: List<Int> get() = level2Chapters.take(FREE_CHAPTERS).map { it.id }

    fun stars(prefs: AppPreferencesHelper, chapterId: Int): Int = prefs.getCustomParamInt("l2_quiz_stars_$chapterId", 0)
    fun freeDone(prefs: AppPreferencesHelper): Boolean = freeChapterIds.all { stars(prefs, it) >= 1 }
    fun allDone(prefs: AppPreferencesHelper): Boolean = quizChapterIds.all { stars(prefs, it) >= 1 }

    /** Each card opens by itself only once. Only the "all chapters" one has a chip on the Level 2 screen to bring it back. */
    fun shown(prefs: AppPreferencesHelper, kind: LevelCompleteKind): Boolean =
        prefs.getCustomParamBoolean(if (kind == LevelCompleteKind.LEVEL2_FREE) FREE_SHOWN_KEY else DONE_SHOWN_KEY, false)
    fun markShown(prefs: AppPreferencesHelper, kind: LevelCompleteKind) =
        prefs.setCustomParamBoolean(if (kind == LevelCompleteKind.LEVEL2_FREE) FREE_SHOWN_KEY else DONE_SHOWN_KEY, true)

    /**
     * The card a Level 2 quiz's "Continue" should open, or null. A free user who has all three free chapters done gets the free-chapters card
     * (once); a subscriber with every quiz chapter done gets the celebration (once).
     */
    fun cardToShow(prefs: AppPreferencesHelper, subscribed: Boolean): LevelCompleteKind? = when {
        !subscribed && freeDone(prefs) && !shown(prefs, LevelCompleteKind.LEVEL2_FREE) -> LevelCompleteKind.LEVEL2_FREE
        subscribed && allDone(prefs) && !shown(prefs, LevelCompleteKind.LEVEL2_DONE) -> LevelCompleteKind.LEVEL2_DONE
        else -> null
    }
}

/** Analytics names of one card kind. [primary]/[gatePassed]/[go] are null where the card has no such step. */
class LevelCompleteEvents(val shown: String, val dismiss: String, val primary: String?, val gatePassed: String?, val go: String?, val chip: String?)

/**
 * Which "you finished" card: Level 1 done (free users are shown Level 2, subscribers go on to it), the free chapters of Level 2 done (free users
 * are shown the rest of Level 2), or every Level 2 chapter done (subscribers: a pure celebration).
 */
enum class LevelCompleteKind(
    @StringRes val titleRes: Int,
    @StringRes val chipRes: Int,
    val events: LevelCompleteEvents,
) {
    LEVEL1(
        R.string.l1_complete_title, R.string.l1_complete_chip,
        LevelCompleteEvents(
            AppAnalytics.LEVEL1_COMPLETE_SHOWN, AppAnalytics.LEVEL1_COMPLETE_NOT_NOW, AppAnalytics.LEVEL1_COMPLETE_SEE_LEVEL2,
            AppAnalytics.LEVEL1_COMPLETE_GATE_PASSED, AppAnalytics.LEVEL1_COMPLETE_GO_LEVEL2, AppAnalytics.LEVEL1_COMPLETE_CHIP_TAPPED,
        ),
    ),
    LEVEL2_FREE(
        R.string.l2_free_title, R.string.l2_free_title,
        LevelCompleteEvents(
            AppAnalytics.LEVEL2_FREE_CARD_SHOWN, AppAnalytics.LEVEL2_FREE_CARD_NOT_NOW, AppAnalytics.LEVEL2_FREE_CARD_SEE_FORMULAS,
            AppAnalytics.LEVEL2_FREE_CARD_GATE_PASSED, AppAnalytics.LEVEL2_FREE_CARD_SUBSCRIBED, null,
        ),
    ),
    LEVEL2_DONE(
        R.string.l2_done_title, R.string.l2_done_chip,
        LevelCompleteEvents(AppAnalytics.LEVEL2_COMPLETE_SHOWN, AppAnalytics.LEVEL2_COMPLETE_DONE, null, null, null, AppAnalytics.LEVEL2_COMPLETE_CHIP_TAPPED),
    );

    /** The medals, in order: the lesson / chapter numbers. */
    val medalIds: List<Int> get() = when (this) {
        LEVEL1 -> level1Lessons.map { it.id }
        LEVEL2_FREE -> Level2Progress.freeChapterIds
        LEVEL2_DONE -> level2Chapters.map { it.id }
    }

    /** Stars a medal earned, or null for a chapter without a quiz (it shows no stars). */
    fun starsOf(prefs: AppPreferencesHelper, id: Int): Int? = when (this) {
        LEVEL1 -> Level1Progress.stars(prefs, id)
        else -> if (id in Level2Progress.quizChapterIds) Level2Progress.stars(prefs, id) else null
    }

    fun totalStars(prefs: AppPreferencesHelper): Int = medalIds.sumOf { starsOf(prefs, it) ?: 0 }
    fun maxStars(prefs: AppPreferencesHelper): Int = medalIds.count { starsOf(prefs, it) != null } * 3
}

/**
 * The "you finished" card (see [LevelCompleteKind]): shown once, right after the quiz that finishes the level (or the free chapters), and
 * again from the green chip where the kind has one. A trophy sits on the card's top edge with sparkles and falling confetti, the lessons show
 * as medals with the stars they earned. A free user sees a line, a button that asks the grown-up question first and then shows the plans, and
 * "Not now"; a subscriber sees "Go to Level 2", or (Level 2 complete) one "Great!" button. Nothing on it counts down or pushes.
 * Compose it as the last child of a full-screen box (inside the kit theme). Back does what "Not now" does.
 *
 * @param source "quiz" (it opened by itself), "chip" (the level screen) or "preview": it only goes to analytics
 * @param onClose "Not now" / "Great!"
 * @param onContinue the subscriber's "Go to Level 2" (Level 1 card), and what happens after a free user subscribes in the plans sheet
 * @param previewSubscribed debug previews only: show the subscriber's or the free user's card whatever the phone owns
 */
@Composable
fun LevelCompleteCard(
    kind: LevelCompleteKind,
    source: String,
    onClose: () -> Unit,
    onContinue: () -> Unit,
    previewSubscribed: Boolean? = null,
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val prefs = LocalPreferencesHelper.current
    val hasFullAccess = rememberHasFullAccess()
    var boughtHere by remember { mutableStateOf(false) }   // a purchase in the plans sheet: the card becomes a subscriber's
    val subscribed = previewSubscribed ?: (hasFullAccess || boughtHere)
    var showGate by remember { mutableStateOf(false) }
    var showPlans by remember { mutableStateOf(false) }
    val totalStars = kind.totalStars(prefs)
    val maxStars = kind.maxStars(prefs)
    val events = kind.events
    val dismiss = { AppAnalytics.log(events.dismiss, "source" to source); onClose() }
    val compact = kind.medalIds.size > 5   // ten medals: smaller ones, and the stars pill gets its own row

    // the trophy pops in a moment after the card, the sparkles twinkle, the confetti falls once (it is gone after about 4 seconds)
    var trophyPop by remember { mutableStateOf(false) }
    // the same spring as on iOS (response 0.5 s, damping 0.5): stiffness = (2 pi / 0.5)^2
    val pop by animateFloatAsState(if (trophyPop) 1f else 0.15f, spring(dampingRatio = 0.5f, stiffness = 158f), label = "trophy")
    var confettiT by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        AudioPlayerManager.playSoundClap()
        AppAnalytics.log(events.shown, "source" to source, "stars" to totalStars, "subscribed" to if (subscribed) 1 else 0)
        delay(250)
        trophyPop = true
        val start = androidx.compose.runtime.withFrameNanos { it }
        while (confettiT < 5f) { confettiT = (androidx.compose.runtime.withFrameNanos { it } - start) / 1_000_000_000f }
    }

    Box(Modifier.fillMaxSize()) {
        AbPopupLayer(onScrimTap = dismiss, dismissOnScrimTap = false) { layerWidth, layerHeight, shown ->
            val cardW = minOf(layerWidth * 0.7f, 620.dp * s)
            // a short screen (a phone on its side): the whole card (trophy included) shrinks to fit the screen height
            val fit = if (layerHeight < 280.dp * s) (layerHeight / (280.dp * s)).coerceAtLeast(0.7f) else 1f
            Box(Modifier.width(cardW).graphicsLayer { scaleX = fit; scaleY = fit }, contentAlignment = Alignment.TopCenter) {
                AbPopupCard(
                    width = cardW,
                    shown = shown,
                    tone = colors.accentGreen,
                    lightBorderAlpha = 0.55f,
                    topTint = 0.2f,   // the soft green glow from the top edge, as on iOS
                    padding = 14.dp * s,
                    modifier = Modifier.padding(top = (TROPHY_SCALE * 42).dp * s),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp * s),
                ) {
                    Spacer(Modifier.height(24.dp * s))   // under the lower half of the trophy
                    Text(
                        stringResource(kind.titleRes), style = AbTypography.title(s), color = colors.textPrimary,
                        textAlign = TextAlign.Center, maxLines = 2,
                    )
                    // the stars total and the medals share one row: it keeps the card short (ten medals: the pill gets its own row)
                    val medals: @Composable () -> Unit = {
                        Row(horizontalArrangement = Arrangement.spacedBy((if (compact) 6 else 8).dp * s)) {
                            kind.medalIds.forEachIndexed { i, id -> LevelMedal(i, id, kind.starsOf(prefs, id), compact) }
                        }
                    }
                    if (compact) {
                        LevelStarsPill(totalStars, maxStars)
                        medals()
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp * s), verticalAlignment = Alignment.CenterVertically) {
                            LevelStarsPill(totalStars, maxStars)
                            medals()
                        }
                    }
                    val line = when {
                        kind == LevelCompleteKind.LEVEL1 && !subscribed -> R.string.l1_complete_parent_line
                        kind == LevelCompleteKind.LEVEL2_FREE -> R.string.l2_free_line
                        else -> null
                    }
                    if (line != null) {
                        Text(stringResource(line), style = AbTypography.body(s), color = colors.textSecondary, textAlign = TextAlign.Center)
                    }
                    if (kind == LevelCompleteKind.LEVEL2_DONE) {
                        // a pure celebration: one button
                        AbButton(stringResource(R.string.l2_done_great), Modifier.fillMaxWidth(), tone = AbTone.Success, fillWidth = true) { dismiss() }
                    } else {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp * s)) {
                            AbButton(stringResource(R.string.l1_complete_not_now), Modifier.weight(1f), tone = AbTone.Ghost, fillWidth = true) { dismiss() }
                            AbButton(
                                text = stringResource(
                                    when {
                                        kind == LevelCompleteKind.LEVEL2_FREE -> R.string.l2_free_see
                                        subscribed -> R.string.l1_complete_go_level2
                                        else -> R.string.l1_complete_see_level2
                                    },
                                ),
                                modifier = Modifier.weight(1f), tone = AbTone.Success, fillWidth = true,
                                icon = Icons.Rounded.ArrowCircleRight, iconAtEnd = true,   // iOS: arrow.right.circle.fill
                            ) {
                                if (subscribed) {
                                    AppAnalytics.log(events.go!!, "source" to source)
                                    onContinue()
                                } else {
                                    AppAnalytics.log(events.primary!!, "source" to source)
                                    showGate = true
                                }
                            }
                        }
                    }
                }
                LevelTrophy(Modifier.graphicsLayer { scaleX = pop; scaleY = pop }, TROPHY_SCALE)
            }
            // paper confetti over the whole screen (it never takes a tap)
            Canvas(Modifier.fillMaxSize()) { drawConfetti(confettiT, size.width, size.height) }
        }

        // the grown-up question comes first; only then the plans (the same order as the day 3 / day 7 reminder)
        if (showGate) {
            AbParentalGate(
                onSuccess = {
                    showGate = false
                    AppAnalytics.log(events.gatePassed!!, "source" to source)
                    showPlans = true
                },
                onClose = { showGate = false },
            )
        }
        if (showPlans) {
            FreemiumPaywallBottomSheet(
                paywallContext = PaywallContext.LEVEL2,
                gateAlreadyPassed = true,
                onSubscriptionActivated = {
                    showPlans = false
                    boughtHere = true
                    AppAnalytics.log(events.go!!, "source" to source, "after" to "subscribed")
                    onContinue()
                },
                onDismiss = { showPlans = false },
            )
        }
    }
}

/** The trophy badge is [TROPHY_SCALE] of its drawn size (84 kit units): a smaller one keeps the whole card short. */
private const val TROPHY_SCALE = 0.8f

/**
 * The glow behind a celebration badge and six sparkles around it, drawn past the box's own size. Same as on iOS: the glow is a 124-unit yellow
 * circle at 0.4 blurred by 12 units (a blur is not there before Android 12, so this draws the same falloff as a gradient: about 0.4 up to the
 * badge's edge, half of it at 62 units, gone at about 88). Each sparkle starts a little after the one before and pulses between 0.65x and 1.25x,
 * and its distance from the badge pulses with it (they drift in and out). Put it first in a centred Box, with the badge on top of it.
 * [k] scales it (1 = for an 84-unit badge).
 */
@Composable
internal fun BoxScope.CelebrationAura(k: Float = 1f) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor * k
    val transition = rememberInfiniteTransition(label = "twinkle")
    Box(
        Modifier.size(124.dp * s).drawBehind {
            val r = 88.dp.toPx() * s
            val glow = colors.accentYellow
            drawCircle(
                brush = Brush.radialGradient(
                    0f to glow.copy(alpha = 0.40f), 0.43f to glow.copy(alpha = 0.39f), 0.57f to glow.copy(alpha = 0.336f),
                    0.705f to glow.copy(alpha = 0.20f), 0.84f to glow.copy(alpha = 0.064f), 0.98f to glow.copy(alpha = 0.009f), 1f to Color.Transparent,
                    center = center, radius = r,
                ),
                radius = r, center = center,
            )
        },
    )
    // dx, dy, size of each sparkle
    listOf(Triple(-66, -14, 15), Triple(64, -20, 12), Triple(-52, 34, 11), Triple(56, 32, 14), Triple(-18, -50, 11), Triple(22, -52, 13))
        .forEachIndexed { i, (dx, dy, size) ->
            val twinkle by transition.animateFloat(
                0.65f, 1.25f,
                infiniteRepeatable(tween(900, easing = EaseInOut), RepeatMode.Reverse, initialStartOffset = StartOffset(250 + i * 150)),
                label = "sparkle$i",
            )
            Text(
                "✦", fontSize = (size * s).sp, color = if (i % 2 == 0) colors.accentYellow else Color.White,
                modifier = Modifier.graphicsLayer {
                    scaleX = twinkle; scaleY = twinkle
                    translationX = dx * s * density * twinkle   // the offset scales too, like iOS (scaleEffect after offset)
                    translationY = dy * s * density * twinkle
                    alpha = (twinkle - 0.25f).coerceIn(0.4f, 1f)   // 0.4 at 0.65x, 1 at 1.25x
                },
            )
        }
}

/** The pop-in of a celebration badge a moment after its popup: 0.15 to 1 with the same spring as on iOS (response 0.5 s, damping 0.5). */
@Composable
internal fun rememberBadgePop(): Float {
    var on by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(250); on = true }
    return animateFloatAsState(if (on) 1f else 0.15f, spring(dampingRatio = 0.5f, stiffness = 158f), label = "badge").value
}

/** Seconds since the popup opened while paper confetti falls (it is gone after 5 s): give it to `drawConfetti`. */
@Composable
internal fun rememberConfettiTime(): Float {
    var t by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        val start = androidx.compose.runtime.withFrameNanos { it }
        while (t < 5f) { t = (androidx.compose.runtime.withFrameNanos { it } - start) / 1_000_000_000f }
    }
    return t
}

/** The gold trophy badge that sits on the card's top edge: [CelebrationAura] behind it. */
@Composable
private fun LevelTrophy(modifier: Modifier = Modifier, k: Float = 1f) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor * k
    Box(modifier.size(width = 124.dp * s, height = 84.dp * s), contentAlignment = Alignment.Center) {
        CelebrationAura(k)
        Box(
            Modifier.size(84.dp * s).shadow(8.dp * s, CircleShape)
                .background(Brush.verticalGradient(listOf(colors.accentYellow, colors.accentOrange)), CircleShape)
                .border(BorderStroke(4.dp * s, Color.White.copy(alpha = 0.95f)), CircleShape),
            contentAlignment = Alignment.Center,
        ) { Icon(Icons.Rounded.EmojiEvents, null, Modifier.size(60.dp * s), tint = Color.White) }   // iOS: trophy.fill at 42 pt (a Material vector fills about 0.75 of its box)
    }
}

/** "13 of 15 stars" in a gold pill. */
@Composable
private fun LevelStarsPill(stars: Int, maxStars: Int) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val shape = RoundedCornerShape(50)
    Row(
        Modifier.background(colors.accentYellow.copy(alpha = if (colors.isDark) 0.24f else 0.2f), shape)
            .border(BorderStroke(2.dp, colors.accentYellow.copy(alpha = 0.6f)), shape)
            .padding(horizontal = 12.dp * s, vertical = 5.dp * s),
        horizontalArrangement = Arrangement.spacedBy(6.dp * s), verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Rounded.Star, null, Modifier.size(16.dp * s), tint = colors.accentYellow)
        Text(stringResource(R.string.l1_complete_stars, stars, maxStars), style = AbTypography.fixed(14f * s, FontWeight.ExtraBold), color = colors.textPrimary, maxLines = 1)
    }
}

/**
 * A lesson / chapter as a numbered medal with the stars it earned ([earned] null: no quiz, so no stars, but the same height). [position] picks
 * its colour; [compact] is the smaller medal used when there are ten.
 */
@Composable
private fun LevelMedal(position: Int, number: Int, earned: Int?, compact: Boolean) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val tint = listOf(colors.brandSecondary, colors.accentOrange, colors.accentGreen, colors.accentPurple, colors.accentPink)[position % 5]
    val size = if (compact) 24 else 30
    val starSize = if (compact) 8 else 11
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(3.dp * s)) {
        Box(
            Modifier.size(size.dp * s).shadow(3.dp * s, CircleShape, ambientColor = tint, spotColor = tint)
                .background(tint, CircleShape).border(BorderStroke((if (compact) 1.5f else 2f).dp * s, Color.White.copy(alpha = 0.9f)), CircleShape),
            contentAlignment = Alignment.Center,
        ) { Text("$number", style = AbTypography.fixed((if (compact) 11f else 14f) * s, FontWeight.Black), color = Color.White) }
        Row(Modifier.alpha(if (earned == null) 0f else 1f), horizontalArrangement = Arrangement.spacedBy(1.dp * s)) {
            repeat(3) { i -> Icon(Icons.Rounded.Star, null, Modifier.size(starSize.dp * s), tint = if (i < (earned ?: 0)) colors.accentYellow else colors.outline.copy(alpha = 0.45f)) }
        }
    }
}

/** The small green chip on a level screen (the top bar's trailing slot) that brings the card back once the level is complete. */
@Composable
fun LevelCompleteChip(kind: LevelCompleteKind, onClick: () -> Unit) {
    AbChip(
        stringResource(kind.chipRes), AbTone.Success,
        Modifier.clickable(remember { MutableInteractionSource() }, indication = null, onClick = onClick),
        icon = Icons.Rounded.Check,
    )
}
