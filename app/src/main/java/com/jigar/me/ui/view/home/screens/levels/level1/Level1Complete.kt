package com.jigar.me.ui.view.home.screens.levels.level1

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
import com.jigar.me.utils.AppAnalytics

/** Level 1 progress. The quiz of each of the five lessons saves `l1_quiz_stars_<id>` (0..3, a finished quiz is at least one star). */
object Level1Progress {
    private const val SHOWN_KEY = "l1_complete_card_shown"
    private const val MAX_STARS_PER_LESSON = 3

    fun stars(prefs: AppPreferencesHelper, lessonId: Int): Int = prefs.getCustomParamInt("l1_quiz_stars_$lessonId", 0)
    fun lessonsDone(prefs: AppPreferencesHelper): Int = level1Lessons.count { stars(prefs, it.id) >= 1 }
    fun totalStars(prefs: AppPreferencesHelper): Int = level1Lessons.sumOf { stars(prefs, it.id) }
    fun maxStars(): Int = level1Lessons.size * MAX_STARS_PER_LESSON
    fun isComplete(prefs: AppPreferencesHelper): Boolean = lessonsDone(prefs) == level1Lessons.size

    /** The card opens by itself only once; the chip on the Level 1 screen brings it back whenever the level is complete. */
    fun cardShown(prefs: AppPreferencesHelper): Boolean = prefs.getCustomParamBoolean(SHOWN_KEY, false)
    fun markCardShown(prefs: AppPreferencesHelper) = prefs.setCustomParamBoolean(SHOWN_KEY, true)
}

/**
 * The "You finished Level 1!" card: shown once, right after the quiz that finishes the last lesson, and again from the green chip on the
 * Level 1 screen. A trophy sits on the card's top edge with sparkles and falling confetti, the five lessons show as medals with the stars they
 * earned. A free user sees a grown-up line, "See Level 2" (grown-up question first, then the plans) and "Not now"; a subscriber sees
 * "Go to Level 2". Nothing on it counts down or pushes: it is a celebration with one calm door to Level 2.
 * Compose it as the last child of a full-screen box (inside the kit theme). Back does what "Not now" does.
 *
 * @param source "quiz" (it opened by itself), "chip" (the Level 1 screen) or "preview": it only goes to analytics
 * @param onClose "Not now"
 * @param onGoLevel2 the subscriber's button, and what happens after a free user subscribes in the plans sheet
 * @param previewSubscribed debug previews only: show the subscriber's or the free user's card whatever the phone owns
 */
@Composable
fun Level1CompleteCard(
    source: String,
    onClose: () -> Unit,
    onGoLevel2: () -> Unit,
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
    val totalStars = Level1Progress.totalStars(prefs)
    val notNow = { AppAnalytics.log(AppAnalytics.LEVEL1_COMPLETE_NOT_NOW, "source" to source); onClose() }

    // the trophy pops in a moment after the card, the sparkles twinkle, the confetti falls once (it is gone after about 4 seconds)
    var trophyPop by remember { mutableStateOf(false) }
    // the same spring as on iOS (response 0.5 s, damping 0.5): stiffness = (2 pi / 0.5)^2
    val pop by animateFloatAsState(if (trophyPop) 1f else 0.15f, spring(dampingRatio = 0.5f, stiffness = 158f), label = "trophy")
    var confettiT by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        AudioPlayerManager.playSoundClap()
        AppAnalytics.log(AppAnalytics.LEVEL1_COMPLETE_SHOWN, "source" to source, "stars" to totalStars, "subscribed" to if (subscribed) 1 else 0)
        delay(250)
        trophyPop = true
        val start = androidx.compose.runtime.withFrameNanos { it }
        while (confettiT < 5f) { confettiT = (androidx.compose.runtime.withFrameNanos { it } - start) / 1_000_000_000f }
    }

    Box(Modifier.fillMaxSize()) {
        AbPopupLayer(onScrimTap = notNow, dismissOnScrimTap = false) { layerWidth, layerHeight, shown ->
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
                        stringResource(R.string.l1_complete_title), style = AbTypography.title(s), color = colors.textPrimary,
                        textAlign = TextAlign.Center, maxLines = 2,
                    )
                    // the stars total and the five medals share one row: it keeps the card short
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp * s), verticalAlignment = Alignment.CenterVertically) {
                        Level1StarsPill(totalStars)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp * s)) {
                            level1Lessons.forEach { Level1Medal(it.id, Level1Progress.stars(prefs, it.id)) }
                        }
                    }
                    if (!subscribed) {
                        Text(
                            stringResource(R.string.l1_complete_parent_line), style = AbTypography.body(s), color = colors.textSecondary,
                            textAlign = TextAlign.Center,
                        )
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp * s)) {
                        AbButton(stringResource(R.string.l1_complete_not_now), Modifier.weight(1f), tone = AbTone.Ghost, fillWidth = true) { notNow() }
                        AbButton(
                            text = stringResource(if (subscribed) R.string.l1_complete_go_level2 else R.string.l1_complete_see_level2),
                            modifier = Modifier.weight(1f), tone = AbTone.Success, fillWidth = true,
                            icon = Icons.Rounded.ArrowCircleRight, iconAtEnd = true,   // iOS: arrow.right.circle.fill
                        ) {
                            if (subscribed) {
                                AppAnalytics.log(AppAnalytics.LEVEL1_COMPLETE_GO_LEVEL2, "source" to source)
                                onGoLevel2()
                            } else {
                                AppAnalytics.log(AppAnalytics.LEVEL1_COMPLETE_SEE_LEVEL2, "source" to source)
                                showGate = true
                            }
                        }
                    }
                }
                Level1Trophy(Modifier.graphicsLayer { scaleX = pop; scaleY = pop }, TROPHY_SCALE)
            }
            // paper confetti over the whole screen (it never takes a tap)
            Canvas(Modifier.fillMaxSize()) { drawConfetti(confettiT, size.width, size.height) }
        }

        // the grown-up question comes first; only then the plans (the same order as the day 3 / day 7 reminder)
        if (showGate) {
            AbParentalGate(
                onSuccess = {
                    showGate = false
                    AppAnalytics.log(AppAnalytics.LEVEL1_COMPLETE_GATE_PASSED, "source" to source)
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
                    AppAnalytics.log(AppAnalytics.LEVEL1_COMPLETE_GO_LEVEL2, "source" to source, "after" to "subscribed")
                    onGoLevel2()
                },
                onDismiss = { showPlans = false },
            )
        }
    }
}

/** The trophy badge is [TROPHY_SCALE] of its drawn size (84 kit units): a smaller one keeps the whole card short. */
private const val TROPHY_SCALE = 0.8f

/**
 * The gold trophy badge that sits on the card's top edge: a glow behind it and six sparkles around it. Same as on iOS: each sparkle starts a
 * little after the one before and pulses between 0.65x and 1.25x, and its distance from the trophy pulses with it (they drift in and out).
 */
@Composable
private fun Level1Trophy(modifier: Modifier = Modifier, k: Float = 1f) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor * k
    val transition = rememberInfiniteTransition(label = "twinkle")
    Box(modifier.size(width = 124.dp * s, height = 84.dp * s), contentAlignment = Alignment.Center) {
        // The glow behind the badge, as on iOS: a 124-unit yellow circle at 0.4 blurred by 12 units. A blur is not there before Android 12, so this
        // draws the same falloff as a gradient (about 0.4 up to the badge's edge, half of it at 62 units, gone at about 88), past the box's own size.
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
private fun Level1StarsPill(stars: Int) {
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
        Text(stringResource(R.string.l1_complete_stars, stars, Level1Progress.maxStars()), style = AbTypography.fixed(14f * s, FontWeight.ExtraBold), color = colors.textPrimary, maxLines = 1)
    }
}

/** A lesson as a numbered medal with the stars it earned. */
@Composable
private fun Level1Medal(id: Int, earned: Int) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val tint = listOf(colors.brandSecondary, colors.accentOrange, colors.accentGreen, colors.accentPurple, colors.accentPink)[(id - 1) % 5]
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(3.dp * s)) {
        Box(
            Modifier.size(30.dp * s).shadow(3.dp * s, CircleShape, ambientColor = tint, spotColor = tint)
                .background(tint, CircleShape).border(BorderStroke(2.dp * s, Color.White.copy(alpha = 0.9f)), CircleShape),
            contentAlignment = Alignment.Center,
        ) { Text("$id", style = AbTypography.fixed(14f * s, FontWeight.Black), color = Color.White) }
        Row(horizontalArrangement = Arrangement.spacedBy(1.dp * s)) {
            repeat(3) { i -> Icon(Icons.Rounded.Star, null, Modifier.size(11.dp * s), tint = if (i < earned) colors.accentYellow else colors.outline.copy(alpha = 0.45f)) }
        }
    }
}

/** The small green chip on the Level 1 screen (the top bar's trailing slot) that brings the card back once the level is complete. */
@Composable
fun Level1CompleteChip(onClick: () -> Unit) {
    AbChip(
        stringResource(R.string.l1_complete_chip), AbTone.Success,
        Modifier.clickable(remember { MutableInteractionSource() }, indication = null, onClick = onClick),
        icon = Icons.Rounded.Check,
    )
}
