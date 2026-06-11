package com.jigar.me.ui.view.home.screens.levels.level2.formula

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.data.local.data.RodMovement
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.base.abacus_base.AbacusCalculations
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.HomePageBackground
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.screens.levels.level2.l2ComputeMovement
import com.jigar.me.ui.view.home.screens.levels.level2.l2FormulaRodMovements
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
        why   = "When your number is 5–9, the heaven bead is already down. Need to subtract but not enough earth beads? Swap the heaven bead back and get earth beads in return! 🔄",
        when_ = "When you want to subtract 1, 2, 3, or 4 — but not enough earth beads to remove!",
        how   = "Push heaven bead UP + add earth beads UP at the same time. Two moves = one answer! 🤝",
        startColor = Color(0xFF004D40), endColor = Color(0xFF26A69A),
        rows = listOf(
            FormulaRow("−1 = −5+4", "Push heaven up → add 4 earth", "5−1=4", 5, 4),
            FormulaRow("−2 = −5+3", "Push heaven up → add 3 earth", "5−2=3", 5, 3),
            FormulaRow("−3 = −5+2", "Push heaven up → add 2 earth", "5−3=2", 5, 2),
            FormulaRow("−4 = −5+1", "Push heaven up → add 1 earth", "5−4=1", 5, 1),
        )
    ),
    FormulaGroup(
        id = 3, emoji = "🔢", name = "Big Friend +",
        why   = "When adding makes your total 10 or bigger, you need to carry over to the TENS rod next door! Think of moving to a bigger neighborhood! 🏘️",
        when_ = "When adding would make your total 10 or more — time to carry!",
        how   = "Add 1 bead to the TENS rod (+10), then remove beads from ONES rod. They balance out to give the right answer! 🔢",
        startColor = Color(0xFF1A237E), endColor = Color(0xFF5C6BC0), columns = 2,
        rows = listOf(
            FormulaRow("+1 = +10−9", "Carry to tens → remove 9 from ones", "9+1=10", 9, 10),
            FormulaRow("+2 = +10−8", "Carry to tens → remove 8 from ones", "8+2=10", 8, 10),
            FormulaRow("+3 = +10−7", "Carry to tens → remove 7 from ones", "7+3=10", 7, 10),
            FormulaRow("+4 = +10−6", "Carry to tens → remove 6 from ones", "6+4=10", 6, 10),
            FormulaRow("+5 = +10−5", "Carry to tens → remove 5 from ones", "5+5=10", 5, 10),
            FormulaRow("+6 = +10−4", "Carry to tens → remove 4 from ones", "5+6=11", 5, 11),
            FormulaRow("+7 = +10−3", "Carry to tens → remove 3 from ones", "4+7=11", 4, 11),
            FormulaRow("+8 = +10−2", "Carry to tens → remove 2 from ones", "3+8=11", 3, 11),
            FormulaRow("+9 = +10−1", "Carry to tens → remove 1 from ones", "5+9=14", 5, 14),
        )
    ),
    FormulaGroup(
        id = 4, emoji = "🔢", name = "Big Friend −",
        why   = "When the ONES rod is empty but you still need to subtract, borrow 10 from the TENS rod next door! Like borrowing a crayon from a friend! 🖍️",
        when_ = "When the ones rod doesn't have enough beads to subtract!",
        how   = "Remove 1 bead from TENS rod (−10), then add beads to ONES rod. Borrow and give back — easy! 🔢",
        startColor = Color(0xFF33691E), endColor = Color(0xFF8BC34A), columns = 2,
        rows = listOf(
            FormulaRow("−1 = −10+9", "Borrow from tens → add 9 to ones", "10−1=9",  10, 9),
            FormulaRow("−2 = −10+8", "Borrow from tens → add 8 to ones", "10−2=8",  10, 8),
            FormulaRow("−3 = −10+7", "Borrow from tens → add 7 to ones", "12−3=9",  12, 9),
            FormulaRow("−4 = −10+6", "Borrow from tens → add 6 to ones", "11−4=7",  11, 7),
            FormulaRow("−5 = −10+5", "Borrow from tens → add 5 to ones", "10−5=5",  10, 5),
            FormulaRow("−6 = −10+4", "Borrow from tens → add 4 to ones", "11−6=5",  11, 5),
            FormulaRow("−7 = −10+3", "Borrow from tens → add 3 to ones", "12−7=5",  12, 5),
            FormulaRow("−8 = −10+2", "Borrow from tens → add 2 to ones", "13−8=5",  13, 5),
            FormulaRow("−9 = −10+1", "Borrow from tens → add 1 to ones", "14−9=5",  14, 5),
        )
    ),
    FormulaGroup(
        id = 5, emoji = "👨‍👩‍👧‍👦", name = "Family + (−5+10)",
        why   = "Sometimes adding needs BOTH a heaven swap AND a carry to tens — all at once! Small Friend and Big Friend team up as a family! 👨‍👩‍👧‍👦",
        when_ = "When adding 6, 7, 8, or 9 — and the heaven bead is already DOWN!",
        how   = "3 quick moves: heaven UP (−5) → carry to TENS (+10) → add a few earth beads. All at once! 🤝",
        startColor = Color(0xFFE65100), endColor = Color(0xFFFF8F00), columns = 2,
        rows = listOf(
            FormulaRow("+6 = −5+10+1", "Heaven up → carry → add 1 earth", "7+6=13", 7,  13),
            FormulaRow("+7 = −5+10+2", "Heaven up → carry → add 2 earth", "6+7=13", 6,  13),
            FormulaRow("+8 = −5+10+3", "Heaven up → carry → add 3 earth", "6+8=14", 6,  14),
            FormulaRow("+9 = −5+10+4", "Heaven up → carry → add 4 earth", "6+9=15", 6,  15),
        )
    ),
    FormulaGroup(
        id = 6, emoji = "👨‍👩‍👧‍👦", name = "Family − (+5−10)",
        why   = "Sometimes subtracting needs both a borrow from tens AND a heaven bead — all at once! The whole family works together! 👨‍👩‍👧‍👦",
        when_ = "When subtracting 6, 7, 8, or 9 — and there aren't enough earth beads!",
        how   = "3 quick moves: borrow from TENS (−10) → heaven bead DOWN (+5) → remove a few earth beads. Team effort! 🤝",
        startColor = Color(0xFFBF360C), endColor = Color(0xFFE64A19), columns = 2,
        rows = listOf(
            FormulaRow("−6 = +5−10−1", "Borrow → heaven down → remove 1 earth", "11−6=5",  11, 5),
            FormulaRow("−7 = +5−10−2", "Borrow → heaven down → remove 2 earth", "12−7=5",  12, 5),
            FormulaRow("−8 = +5−10−3", "Borrow → heaven down → remove 3 earth", "13−8=5",  13, 5),
            FormulaRow("−9 = +5−10−4", "Borrow → heaven down → remove 4 earth", "14−9=5",  14, 5),
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

    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()
        Column(modifier = Modifier.fillMaxSize()) {
            // Header row: back button + inline scrollable tab chips
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButtonWithText(
                    title       = "Formulas 📋",
                    modifier    = Modifier.wrapContentWidth(),
                    onBackClick = onBackClick
                )
                LazyRow(
                    modifier              = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
                    contentPadding        = PaddingValues(end = AppDimens.Dimens12)
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
    val bg = if (isSelected)
        Brush.linearGradient(listOf(group.startColor, group.endColor))
    else
        Brush.linearGradient(listOf(Color(0xFF37474F), Color(0xFF546E7A)))

    Box(
        modifier = Modifier
            .height(AppDimens.Dimens32)
            .shadow(
                if (isSelected) AppDimens.Dimens6 else AppDimens.Dimens2,
                RoundedCornerShape(AppDimens.Dimens24),
                ambientColor = group.endColor.copy(if (isSelected) 0.5f else 0f),
                spotColor    = group.endColor.copy(if (isSelected) 0.5f else 0f)
            )
            .background(bg, RoundedCornerShape(AppDimens.Dimens24))
            .then(
                if (isSelected) Modifier.border(AppDimens.Dimens2, Color.White.copy(0.4f), RoundedCornerShape(AppDimens.Dimens24))
                else Modifier
            )
            .clickable(remember { MutableInteractionSource() }, null) { onClick() }
            .padding(horizontal = AppDimens.Dimens12),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens4)
        ) {
            Text(text = group.emoji, style = MaterialTheme.typography.labelMedium.scaled())
            Text(
                text       = group.name,
                style      = MaterialTheme.typography.labelMedium.scaled(),
                color      = Color.White,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
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
    val shape = RoundedCornerShape(AppDimens.Dimens16)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(AppDimens.Dimens4, shape, ambientColor = endColor.copy(0.2f), spotColor = endColor.copy(0.2f))
            .background(Color.White, shape)
            .border(AppDimens.Dimens1, endColor.copy(0.40f), shape)
            .padding(AppDimens.Dimens12)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens6)) {
            Text(
                text       = label,
                style      = MaterialTheme.typography.labelLarge.scaled(),
                fontWeight = FontWeight.Bold,
                color      = endColor
            )
            Text(
                text  = text,
                style = MaterialTheme.typography.bodySmall.scaled(),
                color = Color(0xFF1A1A1A)
            )
        }
    }
}

// ── Animated demo ─────────────────────────────────────────────────────────────

private enum class FormulaAnimPhase { ZERO, SETUP_ARROW, INITIAL, OP_ARROW, RESULT }

@Composable
private fun FormulaAnimatedDemo(group: FormulaGroup, heightFraction: Float = 0.82f) {
    val prefs         = LocalPreferencesHelper.current
    val selectedTheme = prefs.getCustomParam(AppConstants.Settings.Theam, AppConstants.Settings.theam_Default)

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

    val outerShape = RoundedCornerShape(AppDimens.Dimens20)
    val rightShape = RoundedCornerShape(topEnd = AppDimens.Dimens20, bottomEnd = AppDimens.Dimens20)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(heightFraction)
            .shadow(AppDimens.Dimens6, outerShape,
                ambientColor = group.endColor.copy(0.3f),
                spotColor    = group.endColor.copy(0.3f))
            .background(Color.White, outerShape)
            .border(AppDimens.Dimens2, group.endColor.copy(0.25f), outerShape)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Left: abacus + phase step label below (white background)
            Column(
                modifier            = Modifier.weight(0.55f).fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Abacus
                Box(
                    modifier         = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    AbacusWithDecimalCanvas(
                        selectedTheme               = selectedTheme,
                        screenType                  = AppConstants.AbacusScreen.screenTypeLevel2LearnMeetFormula,
                        abacusData                  = abCalc,
                        numberOfColumns             = group.columns,
                        rodMovement                 = rodMovement,
                        showDirectionHint           = showArrows,
                        isBeadSoundOn               = false,
                        isDisplayCurrentAbacusInput = false,
                        onRodMovementChange         = {},
                        onShowDirectionHintsChange  = {},
                        onShowHighlighterChange     = {},
                        onReset                     = {},
                        onNext                      = {},
                    )
                }
                // Phase step label below abacus
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = AppDimens.Dimens16, end = AppDimens.Dimens16, bottom = AppDimens.Dimens8)
                        .background(group.endColor.copy(0.12f), RoundedCornerShape(AppDimens.Dimens8)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = phaseLabel,
                        style      = MaterialTheme.typography.bodySmall.scaled(),
                        fontWeight = FontWeight.Bold,
                        color      = group.endColor,
                        textAlign  = TextAlign.Center,
                        modifier   = Modifier.padding(vertical = AppDimens.Dimens4)
                    )
                }
            }

            // Right panel: colored gradient, op label, current formula, nav, example
            Column(
                modifier = Modifier
                    .weight(0.45f)
                    .fillMaxHeight()
                    .background(
                        Brush.linearGradient(listOf(group.startColor.copy(0.85f), group.endColor.copy(0.75f))),
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
                        text       = opLabel,
                        style      = MaterialTheme.typography.headlineMedium.scaled(),
                        fontWeight = FontWeight.Black,
                        color      = Color.White,
                        textAlign  = TextAlign.Center
                    )
                    Text(
                        text       = row.formula,
                        style      = MaterialTheme.typography.labelSmall.scaled(),
                        fontWeight = FontWeight.Bold,
                        color      = Color.White.copy(0.85f),
                        textAlign  = TextAlign.Center
                    )
                    Text(
                        text       = group.name,
                        style      = MaterialTheme.typography.labelSmall.scaled(),
                        fontWeight = FontWeight.Medium,
                        color      = Color.White.copy(0.65f),
                        textAlign  = TextAlign.Center
                    )
                }

                // Prev / Next navigation buttons
                Row(
                    modifier              = Modifier.padding(vertical = AppDimens.Dimens10),
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens10),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    val hasPrev = rowIndex > 0
                    val hasNext = rowIndex < group.rows.size - 1
                    Box(
                        modifier = Modifier
                            .background(
                                if (hasPrev) Color.White.copy(0.3f) else Color.White.copy(0.1f),
                                RoundedCornerShape(AppDimens.Dimens8)
                            )
                            .clickable(remember { MutableInteractionSource() }, null) {
                                if (hasPrev) { rowIndex--; animTrigger++ }
                            }
                            .padding(horizontal = AppDimens.Dimens8, vertical = AppDimens.Dimens6),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text  = "◀",
                            style = MaterialTheme.typography.bodySmall.scaled(),
                            color = if (hasPrev) Color.White else Color.White.copy(0.3f)
                        )
                    }
                    Text(
                        text       = "${rowIndex + 1}/${group.rows.size}",
                        style      = MaterialTheme.typography.labelMedium.scaled(),
                        color      = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .background(
                                if (hasNext) Color.White.copy(0.3f) else Color.White.copy(0.1f),
                                RoundedCornerShape(AppDimens.Dimens8)
                            )
                            .clickable(remember { MutableInteractionSource() }, null) {
                                if (hasNext) { rowIndex++; animTrigger++ }
                            }
                            .padding(horizontal = AppDimens.Dimens8, vertical = AppDimens.Dimens6),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text  = "▶",
                            style = MaterialTheme.typography.bodySmall.scaled(),
                            color = if (hasNext) Color.White else Color.White.copy(0.3f)
                        )
                    }
                }

                // Example + replay
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text       = row.example,
                        style      = MaterialTheme.typography.titleLarge.scaled(),
                        color      = Color.White,
                        fontWeight = FontWeight.Black,
                        textAlign  = TextAlign.Center
                    )
                    Spacer(Modifier.height(AppDimens.Dimens6))
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(0.20f), RoundedCornerShape(AppDimens.Dimens20))
                            .clickable(remember { MutableInteractionSource() }, null) { animTrigger++ }
                            .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens6),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = "▶ Replay",
                            style      = MaterialTheme.typography.labelMedium.scaled(),
                            color      = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
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
            Text("Formula",    Modifier.weight(0.28f), group.endColor, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall.scaled())
            Text("Bead Moves", Modifier.weight(0.45f), group.endColor, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall.scaled())
            Text("Example",    Modifier.weight(0.27f), group.endColor, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall.scaled(), textAlign = TextAlign.End)
        }

        HorizontalDivider(color = group.endColor.copy(0.3f), thickness = AppDimens.Dimens1)
        Spacer(Modifier.height(AppDimens.Dimens4))

        group.rows.forEach { row ->
            Row(
                modifier              = Modifier.fillMaxWidth().padding(vertical = AppDimens.Dimens3),
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(row.formula,   Modifier.weight(0.28f), Color.White,               fontWeight = FontWeight.Bold,  style = MaterialTheme.typography.labelMedium.scaled())
                Text(row.beadMoves, Modifier.weight(0.45f), Color.White.copy(0.75f),                                   style = MaterialTheme.typography.labelSmall.scaled())
                Text(row.example,   Modifier.weight(0.27f), group.endColor.copy(0.9f), fontWeight = FontWeight.Bold,  style = MaterialTheme.typography.labelSmall.scaled(), textAlign = TextAlign.End)
            }
        }
    }
}
