package com.jigar.me.ui.view.home.screens.levels.level4

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.ab.AB_SF_TO_VECTOR
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbGlassPanel
import com.jigar.me.ui.view.ab.AbOptionState
import com.jigar.me.ui.view.ab.AbSize
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abChunkyFace
import com.jigar.me.ui.view.ab.abFixedStyle
import com.jigar.me.ui.view.home.common_ui.LocalTextToSpeechManager
import com.jigar.me.ui.view.home.screens.levels.level3.components.abFont
import com.jigar.me.ui.view.home.screens.levels.level3.components.abResponsive
import com.jigar.me.ui.view.home.screens.levels.level3.components.systemPrimary
import com.jigar.me.ui.view.home.theme.AppDimens
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens20
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens28
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens6
import com.jigar.me.ui.view.home.theme.AppDimens.ToolbarIconSize
import com.jigar.me.ui.view.home.theme.PrimaryBlue
import kotlinx.coroutines.isActive

enum class FillBlankType { ANSWER, MULTIPLICAND }

data class FillBlankQuestion(
    val multiplier: Int,
    val multiplicand: Int,
    val blankType: FillBlankType,
    val choices: List<Int>,
) {
    val correctAnswer: Int get() = when (blankType) {
        FillBlankType.ANSWER -> multiplier * multiplicand
        FillBlankType.MULTIPLICAND -> multiplicand
    }
}

fun generateFillBlankQuestions(tableNumber: Int, count: Int = 10): List<FillBlankQuestion> {
    val multiplicands = (1..10).shuffled().take(count)
    return multiplicands.mapIndexed { idx, m ->
        val type = if (idx % 2 == 0) FillBlankType.ANSWER else FillBlankType.MULTIPLICAND
        when (type) {
            FillBlankType.ANSWER -> {
                val correct = tableNumber * m
                val wrong = mutableSetOf<Int>()
                while (wrong.size < 3) {
                    val c = tableNumber * (1..10).random()
                    if (c != correct) wrong.add(c)
                }
                FillBlankQuestion(tableNumber, m, type, (listOf(correct) + wrong.toList()).shuffled())
            }
            FillBlankType.MULTIPLICAND -> {
                val correct = m
                val wrong = mutableSetOf<Int>()
                while (wrong.size < 3) {
                    val c = (1..10).random()
                    if (c != correct) wrong.add(c)
                }
                FillBlankQuestion(tableNumber, m, type, (listOf(correct) + wrong.toList()).shuffled())
            }
        }
    }
}

fun optionState(choice: Int, answer: Int, selected: Int?): AbOptionState {
    if (selected == null) return AbOptionState.Idle
    if (choice == answer) return AbOptionState.Correct
    if (choice == selected) return AbOptionState.Wrong
    return AbOptionState.Idle
}

data class TableQuestionData(
    val multiplier: Int,
    val multiplicand: Int,
    val choices: List<Int>
) {
    val answer: Int get() = multiplier * multiplicand
}

fun generateTableQuestions(tableNumber: Int, count: Int = 10): List<TableQuestionData> {
    val multiplicands = (1..10).toMutableList().shuffled().take(count)
    return multiplicands.map { m ->
        val correct = tableNumber * m
        val wrong = mutableSetOf<Int>()
        while (wrong.size < 3) {
            val candidate = tableNumber * (1..10).random()
            if (candidate != correct) wrong.add(candidate)
        }
        TableQuestionData(
            multiplier = tableNumber,
            multiplicand = m,
            choices = (listOf(correct) + wrong.toList()).shuffled()
        )
    }
}

fun generateTableQuestions(tables: List<Int>, count: Int = 10): List<TableQuestionData> {
    return (1..count).map {
        val tableNum = tables.random()
        val m = (1..10).random()
        val correct = tableNum * m
        val wrong = mutableSetOf<Int>()
        while (wrong.size < 3) {
            val candidate = tables.random() * (1..10).random()
            if (candidate != correct) wrong.add(candidate)
        }
        TableQuestionData(
            multiplier = tableNum,
            multiplicand = m,
            choices = (listOf(correct) + wrong.toList()).shuffled()
        )
    }
}

fun generateFillBlankQuestions(tables: List<Int>, count: Int = 10): List<FillBlankQuestion> {
    return (0 until count).map { idx ->
        val tableNum = tables.random()
        val m = (1..10).random()
        val type = if (idx % 2 == 0) FillBlankType.ANSWER else FillBlankType.MULTIPLICAND
        when (type) {
            FillBlankType.ANSWER -> {
                val correct = tableNum * m
                val wrong = mutableSetOf<Int>()
                while (wrong.size < 3) {
                    val c = tables.random() * (1..10).random()
                    if (c != correct) wrong.add(c)
                }
                FillBlankQuestion(tableNum, m, type, (listOf(correct) + wrong.toList()).shuffled())
            }
            FillBlankType.MULTIPLICAND -> {
                val correct = m
                val wrong = mutableSetOf<Int>()
                while (wrong.size < 3) {
                    val c = (1..10).random()
                    if (c != correct) wrong.add(c)
                }
                FillBlankQuestion(tableNum, m, type, (listOf(correct) + wrong.toList()).shuffled())
            }
        }
    }
}

data class MixGroup(
    val label: String,
    val tables: List<Int>,
    val isFree: Boolean,
    val emoji: String,
    val startColor: Color,
    val endColor: Color,
)

val MIX_GROUPS = listOf(
    MixGroup("×1 to ×3",   (1..3).toList(),   true,  "🌱", Color(0xFF2E7D32), Color(0xFF43A047)),
    MixGroup("×1 to ×5",   (1..5).toList(),   false, "⭐", Color(0xFF1565C0), Color(0xFF1976D2)),
    MixGroup("×6 to ×10",  (6..10).toList(),  false, "🔥", Color(0xFFE65100), Color(0xFFF57C00)),
    MixGroup("×1 to ×10",  (1..10).toList(),  false, "🏆", Color(0xFF00695C), Color(0xFF00897B)),
    MixGroup("×11 to ×15", (11..15).toList(), false, "💫", Color(0xFF6A1B9A), Color(0xFF7B1FA2)),
    MixGroup("×15 to ×20", (15..20).toList(), false, "💪", Color(0xFFB71C1C), Color(0xFFE53935)),
    MixGroup("×1 to ×20",  (1..20).toList(),  false, "👑", Color(0xFFF57F17), Color(0xFFF9A825)),
)

/** iOS `min(UIScreen.main.bounds.width * 0.80, responsiveDimenstion(360))`: the width of a question or flash card. */
@Composable
internal fun tableCardWidth(): Dp =
    minOf(LocalConfiguration.current.screenWidthDp.dp * 0.80f, abResponsive(360f).dp)

/** The green "✓ 3" pill of the quiz headers. */
@Composable
internal fun TableScoreBadge(score: Int) {
    val style = abFixedStyle(abResponsive(14f), FontWeight.ExtraBold)
    Row(
        modifier = Modifier
            .background(Color(0xFF2FC48D), CircleShape)
            .padding(horizontal = Dimens12, vertical = AppDimens.Dimens6),
        horizontalArrangement = Arrangement.spacedBy(Dimens4),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("✓", style = style, color = Color.White)
        Text("$score", style = style, color = Color.White)
    }
}

/**
 * Stacks its children on top of each other, every one at the size of the biggest (a SwiftUI `ZStack` of flexible panels): the
 * two faces of a flip card stay the same size whichever one is showing.
 */
@Composable
internal fun EqualStack(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        // A Measurable may be measured only once per pass, so the biggest size comes from the intrinsic sizes.
        val width = constraints.constrainWidth(measurables.maxOfOrNull { it.maxIntrinsicWidth(constraints.maxHeight) } ?: 0)
        val height = constraints.constrainHeight(measurables.maxOfOrNull { it.maxIntrinsicHeight(width) } ?: 0)
        val fixed = Constraints.fixed(width, height)
        val placeables = measurables.map { it.measure(fixed) }
        layout(width, height) { placeables.forEach { it.place(0, 0) } }
    }
}

@Composable
fun TableDisplayCard(tableNumber: Int) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val tts = LocalTextToSpeechManager.current
    val scope = rememberCoroutineScope()
    var speakingRow by remember { mutableStateOf<Int?>(null) }
    var isSpeaking by remember { mutableStateOf(false) }
    var speakJob by remember { mutableStateOf<Job?>(null) }

    // Stop when screen goes to background or leaves composition
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                speakJob?.cancel()
                tts.stop()
                speakingRow = null
                isSpeaking = false
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            speakJob?.cancel()
            tts.stop()
            speakingRow = null
        }
    }

    fun speakSingle(i: Int) {
        speakJob?.cancel()
        isSpeaking = false
        tts.stop()
        speakingRow = i
        speakJob = scope.launch {
            suspendCancellableCoroutine { cont ->
                tts.speak("$tableNumber times $i equals ${tableNumber * i}", "row_tap_${tableNumber}_$i") {
                    if (cont.isActive) cont.resume(Unit)
                }
                cont.invokeOnCancellation { tts.stop() }
            }
            speakingRow = null
        }
    }

    fun speakAll() {
        if (isSpeaking) {
            speakJob?.cancel()
            tts.stop()
            speakingRow = null
            isSpeaking = false
            return
        }
        speakJob?.cancel()
        isSpeaking = true
        speakJob = scope.launch {
            for (i in 1..10) {
                if (!isActive) break
                speakingRow = i
                suspendCancellableCoroutine { cont ->
                    tts.speak("$tableNumber times $i equals ${tableNumber * i}", "row_all_${tableNumber}_$i") {
                        if (cont.isActive) cont.resume(Unit)
                    }
                    cont.invokeOnCancellation { tts.stop() }
                }
                if (i < 10) delay(400)
            }
            speakingRow = null
            isSpeaking = false
        }
    }

    // iOS DimensionsUtils.backIconCircleSize is the kit's back button
    val backIconSize = 48.dp * s

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier.padding(Dimens12),
            contentAlignment = Alignment.TopCenter
        ) {
            // Glass card with the ten rows
            AbGlassPanel(Modifier.fillMaxWidth()) {
                Column {
                    Spacer(Modifier.height(backIconSize * 0.55f))
                    (1..10).forEach { i ->
                        val highlighted = speakingRow == i
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    when {
                                        highlighted -> colors.brandPrimary.copy(alpha = 0.18f)
                                        i % 2 != 0 -> colors.brandPrimary.copy(alpha = 0.06f)
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { speakSingle(i) }
                                .padding(vertical = AppDimens.Dimens6),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Dimens4, Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = "$tableNumber × $i",
                                style = abFont(12f),
                                color = if (highlighted) colors.brandPrimary else colors.textPrimary
                            )
                            Text(
                                text = " = ",
                                style = abFont(12f),
                                color = colors.textSecondary
                            )
                            Text(
                                text = "${tableNumber * i}",
                                style = abFont(12f, FontWeight.Black),
                                color = colors.brandPrimary
                            )
                            if (highlighted) {
                                Icon(
                                    imageVector = Icons.Rounded.VolumeUp,
                                    contentDescription = null,
                                    tint = colors.brandPrimary,
                                    modifier = Modifier.size((10f * AB_SF_TO_VECTOR).dp)
                                )
                            }
                        }
                    }
                }
            }
            // Banner — tap to speak entire table (toggle)
            Row(
                modifier = Modifier
                    .offset(y = -(backIconSize * 0.22f))
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { speakAll() }
                    .abChunkyFace(colors.brandPrimary, colors.brandPrimaryDark, null, Dimens12, 4.dp)
                    .padding(horizontal = Dimens16, vertical = Dimens4),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens6)
            ) {
                Text(
                    text = "×$tableNumber",
                    style = abFixedStyle(15f, FontWeight.Black),
                    color = Color.White
                )
                Icon(
                    imageVector = if (isSpeaking) Icons.AutoMirrored.Rounded.VolumeOff else Icons.AutoMirrored.Rounded.VolumeUp,
                    contentDescription = "Speak table",
                    tint = Color.White.copy(alpha = 0.88f),
                    modifier = Modifier.size((12f * AB_SF_TO_VECTOR).dp)
                )
            }
        }
    }
}

@Composable
fun TableResultContent(
    score: Int,
    total: Int,
    onRetry: () -> Unit,
    onBack: () -> Unit,
) {
    val colors = LocalAbColors.current
    val pct = if (total > 0) score.toDouble() / total.toDouble() else 0.0
    val starValue = pct * 3.0

    val message = when {
        pct >= 0.9 -> "Perfect! You're a table master! 🏆"
        pct >= 0.6 -> "Great job! Almost there! 🎉"
        pct >= 0.3 -> "Good effort! Keep practicing! 💪"
        else -> "Don't give up! Try again! 🎯"
    }

    val starSize = ToolbarIconSize * 1.4f

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Progressive stars — each animates from 0 with staggered delay (mirrors iOS spring delay)
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens16),
            verticalAlignment = Alignment.CenterVertically
        ) {
            (0..2).forEach { idx ->
                val fill = (starValue - idx).coerceIn(0.0, 1.0).toFloat()
                ProgressiveStar(fill = fill, starSize = starSize, delayMs = idx * 120L)
            }
        }

        Spacer(modifier = Modifier.height(Dimens28))

        // Score circle with gradient
        Box(
            modifier = Modifier
                .size(ToolbarIconSize * 2.0f)
                .background(
                    brush = Brush.linearGradient(
                        listOf(PrimaryBlue, PrimaryBlue.copy(alpha = 0.8f))
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$score",
                    style = MaterialTheme.typography.displayLarge.scaled(),
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "out of $total",
                    style = abFont(11f),
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens20))

        Text(
            text = message,
            style = abFont(16f, FontWeight.Bold),
            color = colors.systemPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = AppDimens.Dimens24)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Action buttons, centered horizontally
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens16),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = Dimens28)
        ) {
            AbButton(
                text = "Back",
                tone = AbTone.Ghost,
                size = AbSize.Medium,
                icon = Icons.AutoMirrored.Rounded.ArrowBack,
                onClick = onBack
            )
            AbButton(
                text = "Try Again",
                tone = AbTone.Primary,
                size = AbSize.Medium,
                icon = Icons.Rounded.Replay,
                onClick = onRetry
            )
        }
    }
}

@Composable
internal fun ProgressiveStar(fill: Float, starSize: Dp, delayMs: Long = 0L) {
    var animTarget by remember { mutableFloatStateOf(0f) }

    val animatedFill by animateFloatAsState(
        targetValue = animTarget,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow),
        label = "starFill"
    )
    val scale by animateFloatAsState(
        targetValue = when {
            animTarget >= 1.0f -> 1.18f
            animTarget > 0f -> 1.08f
            else -> 1.0f
        },
        animationSpec = spring(dampingRatio = 0.3f, stiffness = Spring.StiffnessMedium),
        label = "starScale"
    )

    LaunchedEffect(fill) {
        if (delayMs > 0L) delay(delayMs)
        animTarget = fill
    }

    Box(
        modifier = Modifier
            .size(starSize)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        // Gray empty star — always visible as background
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = Color.Gray.copy(alpha = 0.28f),
            modifier = Modifier.size(starSize)
        )
        // Amber filled star — draw-level clip reveals exactly the left fill fraction
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = Color(0xFFFFC107),
            modifier = Modifier
                .size(starSize)
                .drawWithContent {
                    if (animatedFill > 0f) {
                        clipRect(right = size.width * animatedFill) {
                            this@drawWithContent.drawContent()
                        }
                    }
                }
        )
    }
}
