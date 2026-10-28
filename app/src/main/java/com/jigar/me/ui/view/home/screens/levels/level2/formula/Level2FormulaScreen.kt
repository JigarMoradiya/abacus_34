package com.jigar.me.ui.view.home.screens.levels.level2.formula

import com.jigar.me.ui.view.ab.abReadable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.view.base.abacus_base.AbacusTheme
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.data.local.data.RodMovement
import com.jigar.me.ui.view.ab.AbCard
import com.jigar.me.ui.view.ab.AbGlassPanel
import com.jigar.me.ui.view.ab.AbPanelButton
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbTopBar
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.ab.AbacusFrameStyle
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.ab.abSquash
import com.jigar.me.ui.view.base.abacus_base.AbacusCalculations
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.screens.levels.LevelFitToBox
import com.jigar.me.ui.view.home.screens.levels.LevelScreenRoot
import com.jigar.me.ui.view.home.screens.levels.level2.l2FormulaRodMovements
import com.jigar.me.ui.view.home.screens.levels.levelForeground
import com.jigar.me.ui.view.home.screens.levels.levelParagraph
import com.jigar.me.ui.view.home.screens.levels.levelText
import com.jigar.me.ui.view.home.screens.levels.levelTopBarPadding
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.AppConstants
import kotlinx.coroutines.delay

// ── Data ─────────────────────────────────────────────────────────────────────

private data class FormulaRow(
    val formula: String,
    val beadMoves: String,
    val example: String,
    val fromVal: Int,
    val toVal: Int,
)

private data class FormulaGroup(
    val id: Int,
    val emoji: String,
    val name: String,
    val why: String,
    val when_: String,
    val how: String,
    val startColor: Color,
    val endColor: Color,
    val columns: Int = 1,
    val rows: List<FormulaRow>,
)

// Rod movements for both 1-column and 2-column abacus demos
// 2-col layout: index 0 = tens (left), index 1 = ones (right)
private val formulaGroups = listOf(
    FormulaGroup(
        id = 1, emoji = "🤝", name = "Small Friend +",
        why   = "The ones rod can only hold 4 earth beads! When there's no room for more earth beads but the answer is still below 10, we do a magic swap with the heaven bead! 🪄",
        when_ = "When you want to add 1, 2, 3, or 4 — but there's no more space for earth beads!",
        how   = "Push earth beads DOWN + push heaven bead DOWN at the same time. Two moves = one answer! 🤝",
        startColor = Color(0xFF1B5E20), endColor = Color(0xFF4CAF50),
        rows = listOf(
            FormulaRow("+1 = −4+5", "Remove 4 earth → push heaven down", "4+1=5", 4, 5),
            FormulaRow("+2 = −3+5", "Remove 3 earth → push heaven down", "3+2=5", 3, 5),
            FormulaRow("+3 = −2+5", "Remove 2 earth → push heaven down", "2+3=5", 2, 5),
            FormulaRow("+4 = −1+5", "Remove 1 earth → push heaven down", "1+4=5", 1, 5),
        )
    ),
    FormulaGroup(
        id = 2, emoji = "🤝", name = "Small Friend −",
        why   = "When your number is 5–8, the heaven bead is already down. Need to subtract but not enough earth beads? Swap the heaven bead back and get earth beads in return! 🔄",
        when_ = "When you want to subtract 1, 2, 3, or 4 — but not enough earth beads to remove!",
        how   = "Push heaven bead UP + add earth beads UP at the same time. Two moves = one answer! 🤝",
        startColor = Color(0xFF004D40), endColor = Color(0xFF26A69A),
        rows = listOf(
            FormulaRow("−1 = +4−5", "Push heaven up → add 4 earth", "5−1=4", 5, 4),
            FormulaRow("−2 = +3−5", "Push heaven up → add 3 earth", "5−2=3", 5, 3),
            FormulaRow("−3 = +2−5", "Push heaven up → add 2 earth", "5−3=2", 5, 2),
            FormulaRow("−4 = +1−5", "Push heaven up → add 1 earth", "5−4=1", 5, 1),
        )
    ),
    FormulaGroup(
        id = 3, emoji = "🔢", name = "Big Friend +",
        why   = "When adding makes your total 10 or bigger, you need to carry over to the TENS rod next door! Think of moving to a bigger neighborhood! 🏘️",
        when_ = "When adding would make your total 10 or more — time to carry!",
        how   = "Add 1 bead to the TENS rod (+10), then remove beads from ONES rod. They balance out to give the right answer! 🔢",
        startColor = Color(0xFF1A237E), endColor = Color(0xFF5C6BC0), columns = 2,
        rows = listOf(
            FormulaRow("+1 = −9+10", "Carry to tens → remove 9 from ones", "9+1=10", 9, 10),
            FormulaRow("+2 = −8+10", "Carry to tens → remove 8 from ones", "8+2=10", 8, 10),
            FormulaRow("+3 = −7+10", "Carry to tens → remove 7 from ones", "7+3=10", 7, 10),
            FormulaRow("+4 = −6+10", "Carry to tens → remove 6 from ones", "6+4=10", 6, 10),
            FormulaRow("+5 = −5+10", "Carry to tens → remove 5 from ones", "5+5=10", 5, 10),
            FormulaRow("+6 = −4+10", "Carry to tens → remove 4 from ones", "5+6=11", 5, 11),
            FormulaRow("+7 = −3+10", "Carry to tens → remove 3 from ones", "4+7=11", 4, 11),
            FormulaRow("+8 = −2+10", "Carry to tens → remove 2 from ones", "3+8=11", 3, 11),
            FormulaRow("+9 = −1+10", "Carry to tens → remove 1 from ones", "5+9=14", 5, 14),
        )
    ),
    FormulaGroup(
        id = 4, emoji = "🔢", name = "Big Friend −",
        why   = "When the ONES rod is empty but you still need to subtract, borrow 10 from the TENS rod next door! Like borrowing a crayon from a friend! 🖍️",
        when_ = "When the ones rod doesn't have enough beads to subtract!",
        how   = "Remove 1 bead from TENS rod (−10), then add beads to ONES rod. Borrow and give back — easy! 🔢",
        startColor = Color(0xFF33691E), endColor = Color(0xFF8BC34A), columns = 2,
        rows = listOf(
            FormulaRow("−1 = +9−10", "Borrow from tens → add 9 to ones", "10−1=9",  10, 9),
            FormulaRow("−2 = +8−10", "Borrow from tens → add 8 to ones", "10−2=8",  10, 8),
            FormulaRow("−3 = +7−10", "Borrow from tens → add 7 to ones", "12−3=9",  12, 9),
            FormulaRow("−4 = +6−10", "Borrow from tens → add 6 to ones", "11−4=7",  11, 7),
            FormulaRow("−5 = +5−10", "Borrow from tens → add 5 to ones", "10−5=5",  10, 5),
            FormulaRow("−6 = +4−10", "Borrow from tens → add 4 to ones", "11−6=5",  11, 5),
            FormulaRow("−7 = +3−10", "Borrow from tens → add 3 to ones", "12−7=5",  12, 5),
            FormulaRow("−8 = +2−10", "Borrow from tens → add 2 to ones", "13−8=5",  13, 5),
            FormulaRow("−9 = +1−10", "Borrow from tens → add 1 to ones", "14−9=5",  14, 5),
        )
    ),
    FormulaGroup(
        id = 5, emoji = "👨‍👩‍👧‍👦", name = "Family + (−5+10)",
        why   = "Sometimes adding needs BOTH a heaven swap AND a carry to tens — all at once! Small Friend and Big Friend team up as a family! 👨‍👩‍👧‍👦",
        when_ = "When adding 6, 7, 8, or 9 — and the heaven bead is already DOWN!",
        how   = "3 quick moves: heaven UP (−5) → carry to TENS (+10) → add a few earth beads. All at once! 🤝",
        startColor = Color(0xFFE65100), endColor = Color(0xFFFF8F00), columns = 2,
        rows = listOf(
            FormulaRow("+6 = +1−5+10", "Heaven up → carry → add 1 earth", "7+6=13", 7,  13),
            FormulaRow("+7 = +2−5+10", "Heaven up → carry → add 2 earth", "6+7=13", 6,  13),
            FormulaRow("+8 = +3−5+10", "Heaven up → carry → add 3 earth", "6+8=14", 6,  14),
            FormulaRow("+9 = +4−5+10", "Heaven up → carry → add 4 earth", "6+9=15", 6,  15),
        )
    ),
    FormulaGroup(
        id = 6, emoji = "👨‍👩‍👧‍👦", name = "Family − (+5−10)",
        why   = "Sometimes subtracting needs both a borrow from tens AND a heaven bead — all at once! The whole family works together! 👨‍👩‍👧‍👦",
        when_ = "When subtracting 6, 7, 8, or 9 — and there aren't enough earth beads!",
        how   = "3 quick moves: borrow from TENS (−10) → heaven bead DOWN (+5) → remove a few earth beads. Team effort! 🤝",
        startColor = Color(0xFFBF360C), endColor = Color(0xFFE64A19), columns = 2,
        rows = listOf(
            FormulaRow("−6 = −1+5−10", "Borrow → heaven down → remove 1 earth", "11−6=5",  11, 5),
            FormulaRow("−7 = −2+5−10", "Borrow → heaven down → remove 2 earth", "12−7=5",  12, 5),
            FormulaRow("−8 = −3+5−10", "Borrow → heaven down → remove 3 earth", "13−8=5",  13, 5),
            FormulaRow("−9 = −4+5−10", "Borrow → heaven down → remove 4 earth", "14−9=5",  14, 5),
        )
    ),
)

// ── Main Screen ───────────────────────────────────────────────────────────────

@Composable
fun Level2FormulaScreen(
    initialGroupId: Int = 1,
    onBackClick: () -> Unit,
) {
    var selectedGroupId by remember { mutableIntStateOf(initialGroupId.coerceIn(1, 6)) }

    LevelScreenRoot(AbWorld.Sunset) {
        Column(modifier = Modifier.levelForeground()) {
            // Header row: back button + title + inline scrollable tab chips
            AbTopBar(title = "Formulas 📋", onBack = onBackClick, modifier = Modifier.levelTopBarPadding()) {
                LazyRow(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
                    contentPadding        = PaddingValues(horizontal = AppDimens.Dimens4, vertical = AppDimens.Dimens6)
                ) {
                    itemsIndexed(formulaGroups) { _, g ->
                        FormulaGroupTab(
                            group      = g,
                            isSelected = g.id == selectedGroupId,
                            onClick    = { selectedGroupId = g.id }
                        )
                    }
                }
            }

            // Detail content
            AnimatedContent(
                targetState    = selectedGroupId,
                transitionSpec = {
                    (fadeIn(tween(220)) + slideInHorizontally(tween(220)) { it / 4 })
                        .togetherWith(fadeOut(tween(150)))
                },
                label          = "formulaDetail"
            ) { gid ->
                FormulaGroupDetail(
                    group    = formulaGroups[gid - 1],
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

// ── Tab chip ─────────────────────────────────────────────────────────────────

@Composable
private fun FormulaGroupTab(group: FormulaGroup, isSelected: Boolean, onClick: () -> Unit) {
    val colors = LocalAbColors.current
    val s      = LocalAbScale.current.factor
    Row(
        modifier = Modifier
            .abSquash(
                face         = if (isSelected) group.endColor else colors.bgSecondary,
                edge         = if (isSelected) group.endColor.abDarkened(0.32f) else colors.outline,
                stroke       = if (isSelected) null else colors.outline,
                cornerRadius = AbShapes.pill,
                edgeHeight   = 4.dp * s,
                onClick      = onClick
            )
            .height(38.dp * s)
            .padding(horizontal = 14.dp * s),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp * s)
    ) {
        Text(text = group.emoji, style = AbTypography.fixed(15f * s, FontWeight.SemiBold))
        Text(
            text     = group.name,
            style    = AbTypography.fixed(14f * s, if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold),
            color    = if (isSelected) Color.White else colors.textPrimary,
            maxLines = 1
        )
    }
}

// ── Group detail ─────────────────────────────────────────────────────────────

@Composable
private fun FormulaGroupDetail(group: FormulaGroup, modifier: Modifier = Modifier) {
    Row(
        modifier              = modifier.padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens4),
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
    ) {
        // Left: explanation cards + formula table
        Column(
            modifier            = Modifier.weight(0.45f).fillMaxHeight().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens10)
        ) {
            ExplanationSection(label = "💡 Why?",    text = group.why,   endColor = group.endColor)
            ExplanationSection(label = "⏰ When?",   text = group.when_, endColor = group.endColor)
            ExplanationSection(label = "🛠️ How?",   text = group.how,   endColor = group.endColor)
            FormulaTable(group = group)
            Spacer(Modifier.height(AppDimens.Dimens8))
        }

        // Right: demo card centered vertically
        Box(
            modifier         = Modifier.weight(0.55f).fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            val fraction = if (DeviceInfo.isTablet) 0.68f else 0.82f
            FormulaAnimatedDemo(group = group, heightFraction = fraction)
        }
    }
}

// ── Explanation card ──────────────────────────────────────────────────────────

@Composable
private fun ExplanationSection(label: String, text: String, endColor: Color) {
    AbCard {
        Column(verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens6)) {
            Text(
                text  = label,
                style = levelText(12f, FontWeight.Bold),
                color = endColor.abReadable(4.0f)
            )
            Text(
                text  = text,
                style = levelParagraph(12f, FontWeight.SemiBold),
                color = LocalAbColors.current.textPrimary
            )
        }
    }
}

// ── Animated demo ─────────────────────────────────────────────────────────────

private enum class FormulaAnimPhase { ZERO, SETUP_ARROW, INITIAL, OP_ARROW, RESULT }

@Composable
private fun FormulaAnimatedDemo(group: FormulaGroup, heightFraction: Float = 0.82f) {
    val prefs         = LocalPreferencesHelper.current
    val selectedTheme = AbacusTheme.active(prefs)

    var rowIndex    by remember(group.id) { mutableIntStateOf(0) }
    var animPhase   by remember(group.id) { mutableStateOf(FormulaAnimPhase.RESULT) }
    var rodMovement by remember(group.id) { mutableStateOf<List<RodMovement>>(emptyList()) }
    var showArrows  by remember(group.id) { mutableStateOf(false) }
    var animTrigger by remember(group.id) { mutableIntStateOf(1) }

    val abCalc = remember(group.id) { AbacusCalculations(group.columns) }
    val row    = group.rows.getOrElse(rowIndex) { group.rows.first() }

    LaunchedEffect(group.id, rowIndex, animTrigger) {
        val fromVal = row.fromVal
        val toVal   = row.toVal
        showArrows  = false
        rodMovement = emptyList()
        abCalc.resetAbacusData()

        animPhase = FormulaAnimPhase.ZERO
        delay(1200)
        animPhase   = FormulaAnimPhase.SETUP_ARROW
        rodMovement = l2FormulaRodMovements(0, fromVal, group.columns)
        showArrows  = true
        delay(1800)
        animPhase   = FormulaAnimPhase.INITIAL
        showArrows  = false
        rodMovement = emptyList()
        abCalc.setAbacusValueFromString(fromVal.toString())
        delay(1200)
        animPhase   = FormulaAnimPhase.OP_ARROW
        rodMovement = l2FormulaRodMovements(fromVal, toVal, group.columns)
        showArrows  = true
        delay(1800)
        animPhase   = FormulaAnimPhase.RESULT
        showArrows  = false
        rodMovement = emptyList()
        abCalc.setAbacusValueFromString(toVal.toString())
    }

    val phaseLabel = when (animPhase) {
        FormulaAnimPhase.ZERO        -> "Start at 0"
        FormulaAnimPhase.SETUP_ARROW -> "Setting up ${row.fromVal}"
        FormulaAnimPhase.INITIAL     -> "Now: ${row.fromVal}"
        FormulaAnimPhase.OP_ARROW    -> "Applying ${row.formula.substringBefore(" =")}"
        FormulaAnimPhase.RESULT      -> "Result: ${row.toVal} ✅"
    }

    val opLabel = row.formula.substringBefore(" =")

    val s          = LocalAbScale.current.factor
    val outerShape = RoundedCornerShape(AbShapes.lg * s)
    val rightShape = RoundedCornerShape(topEnd = AppDimens.Dimens20, bottomEnd = AppDimens.Dimens20)

    // Calm background card
    AbGlassPanel(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(heightFraction)
            .shadow(AppDimens.Dimens6, outerShape, clip = false,
                ambientColor = group.endColor.copy(0.3f),
                spotColor    = group.endColor.copy(0.3f))
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Left: abacus + phase step label below
            Column(
                modifier            = Modifier.weight(0.55f).fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Abacus
                Box(
                    modifier         = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    LevelFitToBox {
                        AbacusWithDecimalCanvas(
                            selectedTheme               = selectedTheme,
                            screenType                  = AppConstants.AbacusScreen.screenTypeLevel2LearnMeetFormula,
                            abacusData                  = abCalc,
                            numberOfColumns             = group.columns,
                            rodMovement                 = rodMovement,
                            showDirectionHint           = showArrows,
                            isBeadSoundOn               = false,
                            isDisplayCurrentAbacusInput = false,
                            frameStyle                  = AbacusFrameStyle.Toy,
                            onRodMovementChange         = {},
                            onShowDirectionHintsChange  = {},
                            onShowHighlighterChange     = {},
                            onReset                     = {},
                            onNext                      = {},
                        )
                    }
                }
                // Phase step label below abacus
                Text(
                    text      = phaseLabel,
                    style     = levelText(12f, FontWeight.Black),
                    color     = group.endColor.abReadable(4.0f),
                    textAlign = TextAlign.Center,
                    modifier  = Modifier
                        .padding(horizontal = AppDimens.Dimens16)
                        .padding(bottom = AppDimens.Dimens8)
                        .fillMaxWidth()
                        .background(group.endColor.copy(0.1f), RoundedCornerShape(AppDimens.Dimens8))
                        .padding(horizontal = AppDimens.Dimens8, vertical = AppDimens.Dimens6)
                )
            }

            // Right panel: colored gradient, op label, current formula, nav, example
            Column(
                modifier = Modifier
                    .weight(0.45f)
                    .fillMaxHeight()
                    .background(
                        Brush.verticalGradient(listOf(group.startColor.copy(0.85f), group.endColor.copy(0.75f))),
                        rightShape
                    )
                    .padding(AppDimens.Dimens10),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Operation number (big) + full formula text
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens4)
                ) {
                    Text(
                        text      = opLabel,
                        style     = levelText(28f, FontWeight.Black),
                        color     = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text      = row.formula,
                        style     = levelText(11f, FontWeight.Bold),
                        color     = Color.White.copy(0.95f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text      = group.name,
                        style     = levelText(11f, FontWeight.Medium),
                        color     = Color.White.copy(0.92f),
                        textAlign = TextAlign.Center
                    )
                }

                // Prev / Next navigation buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens10),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    val hasPrev = rowIndex > 0
                    val hasNext = rowIndex < group.rows.size - 1
                    FormulaNavButton("◀", hasPrev) { rowIndex--; animTrigger++ }
                    Text(
                        text  = "${rowIndex + 1}/${group.rows.size}",
                        style = levelText(12f, FontWeight.Bold),
                        color = Color.White
                    )
                    FormulaNavButton("▶", hasNext) { rowIndex++; animTrigger++ }
                }

                // Example + replay
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens4)
                ) {
                    Text(
                        text      = row.example,
                        style     = levelText(20f, FontWeight.Black),
                        color     = Color.White,
                        textAlign = TextAlign.Center
                    )
                    AbPanelButton(label = "▶ Replay", accent = group.endColor, filled = false) { animTrigger++ }
                }
            }
        }
    }
}

/** The small chunky ◀ / ▶ that steps through a group's rows; dim and inert at either end. */
@Composable
private fun FormulaNavButton(symbol: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .abSquash(
                face         = Color.White.copy(alpha = if (enabled) 0.3f else 0.12f),
                edge         = Color.Black.copy(alpha = 0.2f),
                cornerRadius = 12.dp,
                edgeHeight   = 3.dp,
                enabled      = enabled,
                onClick      = onClick
            )
            .padding(horizontal = AppDimens.Dimens10, vertical = AppDimens.Dimens6),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text  = symbol,
            style = levelText(14f, FontWeight.Bold),
            color = if (enabled) Color.White else Color.White.copy(0.3f)
        )
    }
}

// ── Formula table ─────────────────────────────────────────────────────────────

@Composable
private fun FormulaTable(group: FormulaGroup) {
    val shape = RoundedCornerShape(AppDimens.Dimens16)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(AppDimens.Dimens4, shape, ambientColor = group.endColor.copy(0.15f), spotColor = group.endColor.copy(0.15f))
            .background(Color(0xFF1C2026), shape)
            .border(AppDimens.Dimens1, group.endColor.copy(0.25f), shape)
            .padding(AppDimens.Dimens12),
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens1)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)
        ) {
            Text("Formula",    Modifier.weight(0.28f), group.endColor.abReadable(4.0f), style = levelText(10f, FontWeight.Bold))
            Text("Bead Moves", Modifier.weight(0.45f), group.endColor.abReadable(4.0f), style = levelText(10f, FontWeight.Bold))
            Text("Example",    Modifier.weight(0.27f), group.endColor.abReadable(4.0f), style = levelText(10f, FontWeight.Bold), textAlign = TextAlign.End)
        }

        HorizontalDivider(color = group.endColor.copy(0.3f), thickness = AppDimens.Dimens1)
        Spacer(Modifier.height(AppDimens.Dimens4))

        group.rows.forEach { row ->
            Row(
                modifier              = Modifier.fillMaxWidth().padding(vertical = AppDimens.Dimens3),
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(row.formula,   Modifier.weight(0.28f), Color.White,               style = levelText(10f, FontWeight.Bold))
                Text(row.beadMoves, Modifier.weight(0.45f), Color.White.copy(0.92f),   style = levelText(10f, FontWeight.SemiBold))
                Text(row.example,   Modifier.weight(0.27f), group.endColor.copy(0.9f), style = levelText(10f, FontWeight.Bold), textAlign = TextAlign.End)
            }
        }
    }
}
