package com.jigar.me.ui.view.home.screens.onboarding

import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.ab.abReadable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.animation.AnimatedContent
import androidx.compose.ui.unit.sp
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.width
import com.jigar.me.ui.view.ab.AbTopBar
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.data.local.data.ExamProvider.AbacusFormulaType
import com.jigar.me.data.local.data.RodMovement
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbHintCard
import com.jigar.me.ui.view.ab.AbHintKind
import com.jigar.me.ui.view.ab.AbHintTag
import com.jigar.me.ui.view.ab.AbSize
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.ab.AbacusFrameStyle
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abFixedStyle
import com.jigar.me.ui.view.ab.hintInk
import com.jigar.me.ui.view.base.abacus_base.AbacusCalculations
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.home.screens.levels.level2.l2ComputeMovement
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.AppAnalytics
import com.jigar.me.utils.AppConstants
import kotlin.math.min

/**
 * First launch: a 3-rod abacus, the mascot's speech bubble, step bubbles and two guided sums, then a page for the
 * parent (what the child will master) with the plans (free trial) on the same page. Shown once per install.
 * Mirrors the iOS `OnboardingTutorialView`.
 */
object OnboardingGate {
    const val KEY = "ab_onboarding_seen"

    /** Once to EVERY user, existing users included: only a device that already finished or skipped it is left out. */
    fun shouldShow(prefs: AppPreferencesHelper): Boolean = !prefs.getCustomParamBoolean(KEY, false)

    fun markSeen(prefs: AppPreferencesHelper) = prefs.setCustomParamBoolean(KEY, true)
}

private data class Move(val to: Int, val afterTitle: String, val afterBody: String)

private data class Page(
    val title: String,
    val body: String,
    val valueBefore: Int,
    val move: Move? = null,
    val showFriendCard: Boolean = false,   // the Small Friend hint card, as in Level 2
)

private val pages = listOf(
    Page("Meet the abacus", "Each rod is one digit.", 0),
    Page("Bottom beads = 1", "Slide them up to count.", 3),
    Page("Top bead = 5", "5 and 2 more make 7.", 7),
    Page("Let's add: 2 + 1", "Slide one bead up.", 2, Move(3, "2 + 1 = 3", "Great job!")),
    Page("Now: 4 + 3", "No room for 3 more beads!", 4, Move(7, "4 + 3 = 7", "That was a Small Friend!"), showFriendCard = true),
)

/** The iPad multiplier of `responsiveDimenstion`, so type grows the same way on tablets. */
private val responsive: Float get() = if (AppDimens.isLargeTablet) 1.7f else if (AppDimens.isTablet) 1.5f else 1f

/**
 * @param parentPage draws the last page: the page's own left column (`left`) plus the plans; `finish(subscribed)` ends the tutorial.
 */
@Composable
fun OnboardingTutorialScreen(
    selectedTheme: String,
    onFinish: (subscribed: Boolean) -> Unit,
    startPage: Int = 0,
    /** The plans page (the free trial) goes to everyone who is not subscribed; a subscriber or a user with a plan assigned by the
     *  admin ends the tutorial after the guided steps. Read when the last step is reached, so the store info has loaded. */
    showsPlans: () -> Boolean = { true },
    parentPage: @Composable (left: @Composable () -> Unit, footer: @Composable () -> Unit, finish: (Boolean) -> Unit) -> Unit,
) {
    var index by remember { mutableIntStateOf(startPage.coerceIn(0, pages.size + featureGroups.size)) }
    var moved by remember { mutableStateOf(false) }
    val parentIndex = pages.size + featureGroups.size
    val onFeaturesPage = index >= pages.size && index < parentIndex     // after the guided steps: what is inside the app
    val onParentPage = index >= parentIndex                              // last: the plans

    LaunchedEffect(index) {
        moved = false
        AppAnalytics.log(
            AppAnalytics.TUTORIAL_STEP,
            "step" to (index + 1).coerceAtMost(parentIndex + 1), "total" to parentIndex + 1,
            "page" to if (onParentPage) "parent" else if (onFeaturesPage) "features" else "tutorial",
        )
    }
    BackHandler(enabled = index > 0) { index -= 1 }

    fun finish(subscribed: Boolean) {
        AppAnalytics.log(AppAnalytics.TUTORIAL_FINISHED, "subscribed" to subscribed)
        onFinish(subscribed)
    }

    Box(Modifier.fillMaxSize()) {
        AbBackground(AbWorld.Meadow, Modifier.fillMaxSize())
        if (onParentPage) {
            parentPage({ ParentLeft() }, { ParentFooter(onContinueFree = { finish(false) }) }, ::finish)
        } else if (onFeaturesPage) {
            FeaturesPage(
                index = index,
                group = featureGroups[(index - pages.size).coerceIn(0, featureGroups.size - 1)],
                nextText = if (index == parentIndex - 1) { if (showsPlans()) "Continue" else "Start learning" } else "Next",
                onPrevious = { index -= 1 },
                onNext = { if (index == parentIndex - 1 && !showsPlans()) finish(true) else index += 1 },
                onSkip = {
                    AppAnalytics.log(AppAnalytics.TUTORIAL_SKIPPED, "step" to index + 1)
                    if (showsPlans()) index = parentIndex else finish(true)
                },
            )
        } else {
            TutorialPage(
                selectedTheme = selectedTheme, index = index, moved = moved,
                nextText = "Next",
                onShowMe = { moved = true },
                onPrevious = { if (moved) moved = false else if (index > 0) index -= 1 },
                onNext = { index += 1 },
                onSkip = {
                    AppAnalytics.log(AppAnalytics.TUTORIAL_SKIPPED, "step" to index + 1)
                    if (showsPlans()) index = parentIndex else finish(true)
                },
            )
        }
    }
}

/** Settings: every card of both "What's inside" pages in one horizontal list, to look at any time. */
@Composable
fun FeatureListScreen(onClose: () -> Unit) {
    val gap = AppDimens.Dimens8
    val unit = 165.dp * responsive   // one column; the Game Zone tile is three wide
    BackHandler(onBack = onClose)
    Box(Modifier.fillMaxSize()) {
        AbBackground(AbWorld.Meadow, Modifier.fillMaxSize())
        Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
            AbTopBar(
                title = "What's free and Premium", onBack = onClose,
                modifier = Modifier.padding(start = DeviceInfo.screenHorizontalPadding(), end = AppDimens.Dimens16, top = DeviceInfo.screenTopPadding()),
            )
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Row(
                    Modifier
                        .horizontalScroll(rememberScrollState())
                        .height(IntrinsicSize.Min)      // every card as tall as the tallest
                        .padding(start = DeviceInfo.screenHorizontalPadding(), end = DeviceInfo.screenHorizontalPadding() + AppDimens.Dimens24),   // room after the last card
                    horizontalArrangement = Arrangement.spacedBy(gap),
                ) {
                    featureGroups.flatMap { it.items }.forEach { f ->
                        val tile = Modifier.width(unit * f.span + gap * (f.span - 1)).fillMaxHeight()
                        if (f.games != null) GameZoneTile(f, tile) else FeatureTile(f, tile)
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------------------------------
// Tutorial pages: step bubbles on top, mascot + speech bubble on the left, the 3-rod abacus (and hint card) on the right
// ---------------------------------------------------------------------------------------------------------------------

@Composable
private fun TutorialPage(
    selectedTheme: String,
    index: Int,
    moved: Boolean,
    nextText: String,
    onShowMe: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSkip: () -> Unit,
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val k = responsive
    val page = pages[index]
    val title = (if (moved) page.move?.afterTitle else null) ?: page.title
    val body = (if (moved) page.move?.afterBody else null) ?: page.body
    val hasMoveToShow = page.move != null && !moved

    // the abacus shows the page's value; a page with a move also shows the arrows for it until "Show me" is tapped
    val abCalc = remember { AbacusCalculations(3) }
    var rodMovement by remember { mutableStateOf<List<RodMovement>>(emptyList()) }
    var showArrows by remember { mutableStateOf(false) }
    LaunchedEffect(index, moved) {
        val value = if (moved && page.move != null) page.move.to else page.valueBefore
        abCalc.resetAbacusData()
        if (value > 0) abCalc.setAbacusValueFromString(value.toString())
        val move = page.move
        if (move != null && !moved) {
            // 3 rods: the ones rod is the last one (index 2)
            rodMovement = listOf(RodMovement(2, l2ComputeMovement(page.valueBefore, move.to)))
            showArrows = true
        } else {
            rodMovement = emptyList()
            showArrows = false
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = AppDimens.Dimens16),
    ) {
        Box(Modifier.fillMaxWidth().padding(top = AppDimens.Dimens8), contentAlignment = Alignment.Center) {
            StepBubbles(index, title)
            Text(
                "Skip",
                Modifier
                    .align(Alignment.CenterEnd)
                    .clickable(remember { MutableInteractionSource() }, indication = null) { onSkip() }
                    .padding(AppDimens.Dimens8),
                style = AbTypography.fixed(14f * k, FontWeight.Bold), color = colors.textSecondary,
            )
        }
        Row(
            Modifier.weight(1f).fillMaxWidth().padding(vertical = AppDimens.Dimens12),
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(0.45f)) {
                SpeechBubble(body)
                Spacer(Modifier.height(AppDimens.Dimens12))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                    Mascot(116.dp * k)
                    Spacer(Modifier.weight(1f))
                    Row(Modifier.padding(bottom = AppDimens.Dimens8), horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)) {
                        if (index > 0 || moved) {
                            AbButton(
                                text = "Previous", tone = AbTone.Purple, size = AbSize.Small, icon = Icons.Rounded.KeyboardArrowLeft,
                                minHeightOverride = 32f, horizontalPaddingOverride = 11f, onClick = onPrevious,
                            )
                        }
                        AbButton(
                            text = if (hasMoveToShow) "Show me" else nextText,
                            tone = if (hasMoveToShow) AbTone.Warning else AbTone.Primary,
                            size = AbSize.Small,
                            icon = if (hasMoveToShow) Icons.Rounded.TouchApp else Icons.Rounded.KeyboardArrowRight,
                            iconAtEnd = !hasMoveToShow,
                            minHeightOverride = 32f, horizontalPaddingOverride = 11f,
                            onClick = { if (hasMoveToShow) onShowMe() else onNext() },
                        )
                    }
                }
            }
            Row(
                Modifier.weight(0.55f).fillMaxHeight(),
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // the abacus is for watching: a transparent layer on top takes the touches; the number it shows sits under it
                Column(Modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.weight(1f).aspectRatio(0.8f)) {
                    AbacusWithDecimalCanvas(
                        selectedTheme = selectedTheme,
                        screenType = AppConstants.AbacusScreen.screenTypeLevel2Learn,   // no Reset / Next answer bar
                        isBeadSoundOn = false,
                        isDisplayCurrentAbacusInput = false,
                        abacusData = abCalc,
                        numberOfColumns = 3,
                        rodMovement = rodMovement,
                        showDirectionHint = showArrows,
                        frameStyle = AbacusFrameStyle.Toy,
                        onRodMovementChange = {},
                        onShowDirectionHintsChange = {},
                        onShowHighlighterChange = {},
                        onReset = {},
                        onNext = {},
                    )
                    Box(Modifier.matchParentSize().clickable(remember { MutableInteractionSource() }, indication = null) {})
                }
                NumberPlate(if (moved && page.move != null) page.move.to else page.valueBefore)
                }
                AnimatedVisibility(
                    visible = page.showFriendCard,
                    enter = scaleIn(initialScale = 0.6f) + fadeIn(),
                    exit = scaleOut(targetScale = 0.6f) + fadeOut(),
                ) {
                    val hintSize = (if (DeviceInfo.isLargeTablet) 24f else if (DeviceInfo.isTablet) 20f else 12f) * 1.3f
                    Box(Modifier.padding(horizontal = 22.dp * k).scale(1.3f)) {
                        AbHintCard {
                            AbHintTag(AbacusFormulaType.SMALL_FRIEND.description, AbHintKind.SmallFriend)
                            Text("+3=-2+5", style = abFixedStyle(hintSize, FontWeight.ExtraBold), color = colors.hintInk)
                        }
                    }
                }
            }
        }
    }
}

/** One tile of a "What's inside" page: what it is, what is free and what Premium adds. null free = nothing free, null premium = everything free. */
private data class Feature(
    val emoji: String, val name: String, val note: String, val free: String?, val premium: String?,
    val span: Int = 1,                  // columns it covers (the Game Zone tile is three wide)
    val games: List<String>? = null,    // the Game Zone tile lists its games
)
private data class FeatureGroup(val title: String, val items: List<Feature>)

/** The 20 games of the Math Game Zone, in the order of its home screen. */
private val gameNames = listOf(
    "Speed Compare", "Balloon Pop", "True or False", "Math Bingo", "Missing Sign", "Equation Match", "Cross Math",
    "Target the Number", "Number Path", "Math Pyramid", "Magic Square", "Number Merge", "Number Sequence", "Place Value",
    "Clock Master", "Number Detective", "Sudoku", "Calcudoku", "Kakuro", "Number Snake",
)

/** The app on two pages (kept in line with APP_FEATURES.md and the Game Zone roster). */
private val featureGroups = listOf(
    FeatureGroup("Learn and practice", listOf(
        Feature("🧮", "Free Mode", "Play with the real abacus and see how beads count", "Everything, always", null),
        Feature("🌱", "Learning path", "4 levels, from first beads to speed and tables", "Level 1: Bead Basics", "Formulas, speed training, tables"),
        Feature("📚", "Abacus Practice", "8 levels of sums with step-by-step help", "Level 1 and the first Level 2 pages", "All 8 levels"),
        Feature("✏️", "Exercise", "Timed drills that build speed", "Addition and subtraction basics", "All drills, with multiply and divide"),
        Feature("📝", "Exam", "Timed tests with saved results", "Addition, beginner level", "All operations and levels"),
    )),
    FeatureGroup("Challenge, games and progress", listOf(
        Feature("🎯", "Custom Challenge (CCM)", "Hear or see numbers, then work out the answer in your head.\n5 to 20 questions\n2 to 6 digits", null, "Full access"),
        Feature("🎮", "Math Game Zone", "20 brain games for speed, logic and number sense", "The easiest level of every game", "All levels and board sizes", span = 3, games = gameNames),
        Feature("📊", "Progress and rewards", "Weekly parent report, streak and themes", "Streak, themes, last 3 results", "Full report history"),
    )),
)

@Composable
private fun FeaturesPage(index: Int, group: FeatureGroup, nextText: String, onPrevious: () -> Unit, onNext: () -> Unit, onSkip: () -> Unit) {
    val colors = LocalAbColors.current
    val k = responsive
    Column(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = AppDimens.Dimens16),
    ) {
        Box(Modifier.fillMaxWidth().padding(top = AppDimens.Dimens8), contentAlignment = Alignment.Center) {
            StepBubbles(index, group.title)
            Text(
                "Skip",
                Modifier
                    .align(Alignment.CenterEnd)
                    .clickable(remember { MutableInteractionSource() }, indication = null) { onSkip() }
                    .padding(AppDimens.Dimens8),
                style = AbTypography.fixed(14f * k, FontWeight.Bold), color = colors.textSecondary,
            )
        }
        // phone: tiles fill the room. tablet: tiles wrap their content (all as tall as the tallest) and sit centred in the room
        val wrap = AppDimens.isTablet || AppDimens.isLargeTablet
        Box(Modifier.weight(1f).fillMaxWidth().padding(vertical = AppDimens.Dimens8), contentAlignment = Alignment.Center) {
            Row(
                Modifier.fillMaxWidth().then(if (wrap) Modifier.height(IntrinsicSize.Min) else Modifier.fillMaxHeight()),
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
            ) {
                group.items.forEach {
                    if (it.games != null) GameZoneTile(it, Modifier.weight(it.span.toFloat()).fillMaxHeight())
                    else FeatureTile(it, Modifier.weight(it.span.toFloat()).fillMaxHeight())
                }
            }
        }
        Row(
            Modifier.fillMaxWidth().padding(bottom = AppDimens.Dimens12),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8, Alignment.End),
        ) {
            AbButton(text = "Previous", tone = AbTone.Purple, size = AbSize.Small, icon = Icons.Rounded.KeyboardArrowLeft,
                minHeightOverride = 32f, horizontalPaddingOverride = 11f, onClick = onPrevious)
            AbButton(text = nextText, tone = AbTone.Primary, size = AbSize.Small, icon = Icons.Rounded.KeyboardArrowRight, iconAtEnd = true,
                minHeightOverride = 32f, horizontalPaddingOverride = 11f, onClick = onNext)
        }
    }
}

@Composable
private fun FeatureTile(f: Feature, modifier: Modifier) {
    val colors = LocalAbColors.current
    val k = responsive
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier
            .background(colors.bgSecondary, shape)
            .border(2.dp, colors.outline, shape)
            .padding(AppDimens.Dimens8),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens4),
    ) {
        Text(f.emoji, fontSize = (30f * k).sp)
        Text(f.name, style = AbTypography.fixed(15f * k, FontWeight.Black), color = colors.textPrimary, maxLines = 2, textAlign = TextAlign.Center)
        Text(
            f.note, Modifier.heightIn(min = 46.dp * k),
            style = AbTypography.fixed(12f * k, FontWeight.SemiBold), color = colors.textSecondary, maxLines = 6, textAlign = TextAlign.Center,
        )
        Spacer(Modifier.weight(1f))
        // the FREE and PREMIUM boxes, with a clear gap between them; a free-only tile keeps its FREE box on the row of its neighbours
        Column(verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)) {
            f.free?.let { InfoBox("FREE", it, colors.accentGreen.abReadable(4.5f)) }
            if (f.premium != null) InfoBox("PREMIUM", f.premium, colors.accentOrange.abReadable(4.5f))
            else if (f.free != null) Spacer(Modifier.height(INFO_BOX_MIN * k))
        }
    }
}

/** The Math Game Zone tile: three columns wide, with every game named and the free / premium boxes side by side. */
@Composable
private fun GameZoneTile(f: Feature, modifier: Modifier) {
    val colors = LocalAbColors.current
    val k = responsive
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier
            .background(colors.bgSecondary, shape)
            .border(2.dp, colors.outline, shape)
            .padding(AppDimens.Dimens8),
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens4),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)) {
            Text(f.emoji, fontSize = (28f * k).sp)
            Column {
                Text(f.name, style = AbTypography.fixed(15f * k, FontWeight.Black), color = colors.textPrimary, maxLines = 1)
                Text(f.note, style = AbTypography.fixed(11f * k, FontWeight.SemiBold), color = colors.textSecondary, maxLines = 2)
            }
        }
        // every game, three to a row
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            (f.games ?: emptyList()).chunked(4).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    row.forEach { name ->
                        Text(
                            name,
                            Modifier.weight(1f).background(colors.brandPrimary.copy(alpha = 0.12f), CircleShape).padding(horizontal = 4.dp, vertical = 3.dp),
                            style = AbTypography.fixed(11f * k, FontWeight.Bold), color = colors.textPrimary, maxLines = 1, textAlign = TextAlign.Center,
                        )
                    }
                    repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)) {
            f.free?.let { Box(Modifier.weight(1f)) { InfoBox("FREE", it, colors.accentGreen.abReadable(4.5f)) } }
            f.premium?.let { Box(Modifier.weight(1f)) { InfoBox("PREMIUM", it, colors.accentOrange.abReadable(4.5f)) } }
        }
    }
}

private val INFO_BOX_MIN = 58.dp

/** A small coloured box: its label (FREE / PREMIUM) over what it covers. */
@Composable
private fun InfoBox(label: String, text: String, tint: Color) {
    val colors = LocalAbColors.current
    val k = responsive
    Column(
        Modifier.fillMaxWidth().heightIn(min = INFO_BOX_MIN * k).background(tint.copy(alpha = 0.14f), RoundedCornerShape(10.dp)).padding(AppDimens.Dimens4),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(label, style = AbTypography.fixed(10f * k, FontWeight.Black), color = tint)
        Text(text, style = AbTypography.fixed(12f * k, FontWeight.Bold), color = colors.textPrimary, maxLines = 3, textAlign = TextAlign.Center)
    }
}

/** The number the abacus shows, on a chunky plate under it, so a child links the beads to the digit. */
@Composable
private fun NumberPlate(value: Int) {
    val colors = LocalAbColors.current
    val k = responsive
    Box(Modifier.padding(top = AppDimens.Dimens4, bottom = 3.dp)) {
        Box(Modifier.matchParentSize().offset(y = 3.dp).background(colors.outline, CircleShape))   // the 3D edge
        Row(
            Modifier
                .background(colors.bgSecondary, CircleShape)
                .border(2.dp, colors.outline, CircleShape)
                .padding(horizontal = 20.dp * k, vertical = 2.dp * k),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
        ) {
            Text("Number", style = AbTypography.fixed(12f * k, FontWeight.Bold), color = colors.textSecondary)
            AnimatedContent(
                targetState = value,
                transitionSpec = { (scaleIn(initialScale = 0.6f) + fadeIn()) togetherWith (scaleOut(targetScale = 1.4f) + fadeOut()) },
                label = "abacusNumber",
            ) { v ->
                Text(v.toString(), style = AbTypography.fixed(30f * k, FontWeight.Black), color = colors.brandPrimary)
            }
        }
    }
}

/** 1 ... N bubbles: done = green tick, current = big and gold, next = plain. */
@Composable
private fun StepBubbles(index: Int, title: String) {
    val colors = LocalAbColors.current
    val k = responsive
    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8), verticalAlignment = Alignment.CenterVertically) {
        (0 until pages.size + featureGroups.size).forEach { i ->      // the guided steps + the "What's inside" pages
            val current = i == index
            val done = i < index
            if (current) {
                // the CURRENT step opens into a gold pill that carries the step's title
                val dot = 28.dp * k
                Row(
                    Modifier
                        .drawBehind {
                            drawRoundRect(colors.accentOrange.copy(alpha = 0.35f), Offset(0f, 3.dp.toPx()), size, CornerRadius(size.height / 2f))
                        }
                        .background(Brush.verticalGradient(listOf(colors.accentYellow, colors.accentYellow.abDarkened(0.08f))), CircleShape)
                        .border(3.dp, colors.accentOrange, CircleShape)
                        .padding(start = 5.dp * k, end = 16.dp * k, top = 5.dp * k, bottom = 5.dp * k),
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.size(dot).background(Color.White, CircleShape), contentAlignment = Alignment.Center) {
                        Text("${i + 1}", style = AbTypography.fixed(dot.value * 0.55f, FontWeight.ExtraBold), color = Color(0xFFB35A00))
                    }
                    AnimatedContent(targetState = title, label = "stepTitle") { t ->
                        Text(t, style = AbTypography.fixed(18f * k, FontWeight.Black), color = Color(0xFF5A3A00), maxLines = 1)
                    }
                }
            } else {
                val size = 26.dp * k
                Box(
                    Modifier
                        .size(size)
                        .background(if (done) colors.accentGreen else colors.bgSecondary, CircleShape)
                        .border(2.dp, colors.outline, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    if (done) {
                        Icon(Icons.Rounded.Check, null, Modifier.size(size * 0.55f), tint = Color.White)
                    } else {
                        Text("${i + 1}", style = AbTypography.fixed(size.value * 0.5f, FontWeight.ExtraBold), color = colors.textSecondary)
                    }
                }
            }
        }
    }
}

/** The mascot floats gently; the speech bubble above it points at its head. */
@Composable
private fun Mascot(height: androidx.compose.ui.unit.Dp) {
    val transition = rememberInfiniteTransition(label = "mascot")
    val bob by transition.animateFloat(
        initialValue = -3f, targetValue = 3f,
        animationSpec = infiniteRepeatable(tween(1300, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "mascotBob",
    )
    Image(
        painter = painterResource(R.drawable._mascot_), contentDescription = null,
        modifier = Modifier.height(height).offset(y = bob.dp),
    )
}

/** A speech bubble with a 3D edge; its tail comes out of the bottom and points down at the mascot's head. */
@Composable
private fun SpeechBubble(body: String) {
    val colors = LocalAbColors.current
    val k = responsive
    val tailHeight = 16.dp * k
    val shape = remember(k) { SpeechBubbleShape(tailXDp = 58f * k, tailHeightDp = 16f * k) }
    Column(
        Modifier
            .fillMaxWidth()
            .drawBehind {   // the 3D edge: the same outline, a little lower and darker
                val outline = shape.createOutline(size, layoutDirection, this) as Outline.Generic
                translate(top = 4.dp.toPx()) { drawPath(outline.path, colors.outline) }
            }
            .background(colors.bgSecondary, shape)
            .border(3.dp, colors.outline, shape)
            .padding(start = AppDimens.Dimens16, end = AppDimens.Dimens16, top = AppDimens.Dimens12, bottom = AppDimens.Dimens12 + tailHeight),
    ) {
        Text(body, style = AbTypography.fixed(21f * k, FontWeight.ExtraBold), color = colors.textPrimary)
    }
}

/** Rounded rectangle whose bottom edge carries a tail (centred [tailXDp] from the left) that leans slightly toward the left. */
private class SpeechBubbleShape(private val tailXDp: Float, private val tailHeightDp: Float) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val d = density.density
        val tailH = tailHeightDp * d
        val tailW = 26f * d
        val bodyH = size.height - tailH
        val r = min(20f * d, bodyH / 2f)
        val left = tailXDp * d - tailW / 2f
        val right = left + tailW
        val path = Path().apply {
            moveTo(r, 0f)
            lineTo(size.width - r, 0f)
            arcTo(Rect(size.width - 2 * r, 0f, size.width, 2 * r), -90f, 90f, false)
            lineTo(size.width, bodyH - r)
            arcTo(Rect(size.width - 2 * r, bodyH - 2 * r, size.width, bodyH), 0f, 90f, false)
            lineTo(right, bodyH)
            lineTo(left + tailW * 0.25f, size.height)   // the tip
            lineTo(left, bodyH)
            lineTo(r, bodyH)
            arcTo(Rect(0f, bodyH - 2 * r, 2 * r, bodyH), 90f, 90f, false)
            lineTo(0f, r)
            arcTo(Rect(0f, 0f, 2 * r, 2 * r), 180f, 90f, false)
            close()
        }
        return Outline.Generic(path)
    }
}

// ---------------------------------------------------------------------------------------------------------------------
// Parent page, left column: what the child masters (the plans sit right beside it)
// ---------------------------------------------------------------------------------------------------------------------

/** The "why choose us" column: the mascot and title stay in place, everything under them scrolls. (What is free and what is
 *  premium is on the "What's inside" pages before this one.) */
@Composable
private fun ParentLeft() {
    val colors = LocalAbColors.current
    val k = responsive
    val prefs = com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper.current
    val days = com.jigar.me.utils.FreemiumWindow.windowDays(prefs)
    val windowActive = days > 0 && com.jigar.me.utils.FreemiumWindow.phase(prefs) == com.jigar.me.utils.FreemiumWindowLogic.Phase.ACTIVE   // an install with a free week
    // in the vertical middle; if it is ever too tall for the screen, the header stays and the rest scrolls
    Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens8, Alignment.CenterVertically)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)) {
            Image(painterResource(R.drawable._mascot_), null, Modifier.height(56.dp * k))
            Column(verticalArrangement = Arrangement.spacedBy((-5).dp * k)) {   // Baloo's line boxes are tall: the subtitle sits close under the title
                Text("Why parents choose us", style = AbTypography.fixed(22f * k, FontWeight.Black), color = colors.textPrimary)
                Text("All you need to learn the abacus", style = AbTypography.fixed(14f * k, FontWeight.SemiBold), color = colors.textSecondary)
            }
        }
        Column(
            Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)) {
                StatCard("🌱", "4", "learning levels", Modifier.weight(1f))
                StatCard("🧮", "8", "practice levels", Modifier.weight(1f))
                StatCard("🧠", "20", "brain games", Modifier.weight(1f))
            }
            // the other parts of the app, so nothing is missing from the picture
            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)) {
                AlsoChip("✏️", "Exercise")
                AlsoChip("📝", "Exam")
                AlsoChip("🎯", "Custom Challenge (CCM)")
            }
            Column(verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)) {
                ReasonRow("Step-by-step help on every sum")
                ReasonRow("Exam-style practice for maths and abacus")
                ReasonRow("Brain games that build focus and logic")
                ReasonRow("A weekly progress report for parents")
                ReasonRow("Learn at their own pace, with rewards")
            }
            // the free week, when the limit is on: no surprise later
            if (windowActive) {
                Text(
                    if (com.jigar.me.utils.FreemiumWindow.coreAfterWindow(prefs) >= 2) "Free for $days days, then Free Mode and Level 1 stay free."
                    else "Free for $days days, then pick a plan to keep going.",
                    style = AbTypography.fixed(12f * k, FontWeight.SemiBold), color = colors.textSecondary,
                )
            }
        }
    }
}

/** At the bottom of the right side: the trust line and the way to carry on for free. */
@Composable
private fun ParentFooter(onContinueFree: () -> Unit) {
    val colors = LocalAbColors.current
    val k = responsive
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)) {
        Text("🛡️ No ads, safe for kids", style = AbTypography.fixed(12f * k, FontWeight.SemiBold), color = colors.textSecondary)
        Spacer(Modifier.weight(1f))
        AbButton("Continue free for now", tone = AbTone.Ghost, size = AbSize.Small, minHeightOverride = 32f, horizontalPaddingOverride = 11f, onClick = onContinueFree)
    }
}

/** A big number over its label. */
@Composable
private fun StatCard(emoji: String, number: String, label: String, modifier: Modifier) {
    val colors = LocalAbColors.current
    val k = responsive
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier.background(colors.bgSecondary, shape).border(2.dp, colors.outline, shape).padding(horizontal = AppDimens.Dimens8, vertical = AppDimens.Dimens4),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens4)) {
            Text(emoji, fontSize = (22f * k).sp)
            Text(number, style = AbTypography.fixed(28f * k, FontWeight.Black), color = colors.brandPrimary)
        }
        Text(label, style = AbTypography.fixed(12f * k, FontWeight.Bold), color = colors.textSecondary, maxLines = 1)
    }
}

/** A small pill: an emoji and a name. */
@Composable
private fun AlsoChip(emoji: String, name: String) {
    val colors = LocalAbColors.current
    val k = responsive
    Row(
        Modifier.background(colors.brandPrimary.copy(alpha = 0.12f), CircleShape).padding(horizontal = AppDimens.Dimens8, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens4),
    ) {
        Text(emoji, fontSize = (13f * k).sp)
        Text(name, style = AbTypography.fixed(12f * k, FontWeight.Bold), color = colors.textPrimary, maxLines = 1)
    }
}

@Composable
private fun ReasonRow(text: String) {
    val colors = LocalAbColors.current
    val k = responsive
    val lineHeight = 20f * k
    val tick = 18.dp * k
    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)) {
        // the tick is centred on the FIRST line (a fixed line height), however many lines the text takes
        Icon(
            Icons.Rounded.CheckCircle, null,
            Modifier.padding(top = ((lineHeight.dp - tick) / 2)).size(tick), tint = colors.accentGreen.abReadable(4.5f),
        )
        Text(
            text,
            style = AbTypography.fixed(14f * k, FontWeight.SemiBold).copy(
                lineHeight = lineHeight.sp,
                lineHeightStyle = androidx.compose.ui.text.style.LineHeightStyle(
                    androidx.compose.ui.text.style.LineHeightStyle.Alignment.Center, androidx.compose.ui.text.style.LineHeightStyle.Trim.None,
                ),
            ),
            color = colors.textPrimary,
        )
    }
}
