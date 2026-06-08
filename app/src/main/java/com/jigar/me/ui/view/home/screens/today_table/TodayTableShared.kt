package com.jigar.me.ui.view.home.screens.today_table

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.VolumeOff
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.LocalTextToSpeechManager
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
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
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.theme.AppDimens.ToolbarIconSize
import com.jigar.me.ui.view.home.theme.ButtonType
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

fun optionType(choice: Int, answer: Int, selected: Int?): ButtonType {
    if (selected == null) return ButtonType.OPTIONS
    if (choice == answer) return ButtonType.POSITIVE
    if (choice == selected) return ButtonType.RED
    return ButtonType.OPTIONS
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

@Composable
fun TableDisplayCard(tableNumber: Int) {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier.padding(Dimens12),
            contentAlignment = Alignment.TopCenter
        ) {
            // White card
            Card(
                shape = RoundedCornerShape(Dimens16),
                elevation = CardDefaults.cardElevation(Dimens4),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Spacer(Modifier.height(ToolbarIconSize * 0.55f))
                    (1..10).forEach { i ->
                        val highlighted = speakingRow == i
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    when {
                                        highlighted -> PrimaryBlue.copy(alpha = 0.18f)
                                        i % 2 != 0 -> PrimaryBlue.copy(alpha = 0.06f)
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable { speakSingle(i) }
                                .padding(vertical = Dimens6, horizontal = Dimens16),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "$tableNumber × $i",
                                style = MaterialTheme.typography.bodySmall.scaled(),
                                fontWeight = FontWeight.SemiBold,
                                color = if (highlighted) PrimaryBlue else Color.Black.copy(alpha = 0.75f)
                            )
                            Text(
                                text = " = ",
                                style = MaterialTheme.typography.bodySmall.scaled(),
                                color = Color.Gray
                            )
                            Text(
                                text = "${tableNumber * i}",
                                style = MaterialTheme.typography.bodySmall.scaled(),
                                fontWeight = FontWeight.Black,
                                color = PrimaryBlue
                            )
                            if (highlighted) {
                                Spacer(Modifier.width(Dimens6))
                                Icon(
                                    imageVector = Icons.Rounded.VolumeUp,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(Dimens12)
                                )
                            }
                        }
                    }
                }
            }
            // Banner — tap to speak entire table (toggle)
            Row(
                modifier = Modifier
                    .offset(y = -(ToolbarIconSize * 0.22f))
                    .shadow(Dimens4, RoundedCornerShape(Dimens8))
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(PrimaryBlue, PrimaryBlue.copy(alpha = 0.85f))
                        ),
                        shape = RoundedCornerShape(Dimens8)
                    )
                    .clickable { speakAll() }
                    .padding(horizontal = Dimens12, vertical = Dimens4),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens6)
            ) {
                Text(
                    text = "×$tableNumber",
                    style = MaterialTheme.typography.bodyMedium.scaled(),
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Icon(
                    imageVector = if (isSpeaking) Icons.Rounded.VolumeOff else Icons.Rounded.VolumeUp,
                    contentDescription = "Speak table",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(Dimens16)
                )
            }
        }
        Spacer(Modifier.height(Dimens12))
    }
}

@Composable
fun TableResultContent(
    score: Int,
    total: Int,
    onRetry: () -> Unit,
    onBack: () -> Unit,
) {
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
                    style = MaterialTheme.typography.bodySmall.scaled(),
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens20))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge.scaled(),
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = AppDimens.Dimens24)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Action buttons — KidsActionButton centered horizontally (wrapContentWidth)
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens16),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = Dimens28)
        ) {
            KidsActionButton(
                text = "Back",
                icon = Icons.AutoMirrored.Rounded.ArrowBack,
                type = ButtonType.NEGATIVE,
                isIconStart = true,
                isSmall = true,
                onClick = onBack
            )
            KidsActionButton(
                text = "Try Again",
                icon = Icons.Default.Refresh,
                type = ButtonType.BLUE,
                isIconStart = true,
                isSmall = true,
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
