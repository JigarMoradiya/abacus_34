package com.jigar.me.ui.view.home.screens.math_game_zone.math_pyramid.play

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorLightYellow
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.screens.math_game_zone.math_pyramid.play.components.NumberPyramidGenerator
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.getButtonColors
import kotlinx.coroutines.delay
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.common_ui.animations.ConfettiRainEffect
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.common_ui.buttons.KidsKeyPad
import com.jigar.me.ui.view.home.theme.AppDimens.keyPadHeight
import com.jigar.me.ui.view.home.theme.ButtonType

@Composable
fun MathPyramidPlayJetpackScreen(
    levels: Int = 4,
    difficulty: CommonDifficulty4 = CommonDifficulty4.easy,
    onBackClick: () -> Unit
) {
    val solveMarker: com.jigar.me.ui.view.home.screens.math_game_zone.ZoneDailyMarkerViewModel =
        androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()
    var pyramid by remember { mutableStateOf<List<List<Int?>>>(emptyList()) }
    var editableMask by remember { mutableStateOf<List<List<Boolean>>>(emptyList()) }
    var selectedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var isSolved by remember { mutableStateOf(false) }

    fun generateNewPuzzle() {
        val range = when (difficulty) {
            CommonDifficulty4.easy -> 1..20
            CommonDifficulty4.medium -> 10..50
            CommonDifficulty4.hard -> 30..70
            CommonDifficulty4.veryHard -> 50..99
        }
        val res = NumberPyramidGenerator.generate(levels = levels, difficulty = difficulty, numberRange = range)
        pyramid = res.puzzle
        editableMask = pyramid.map { row -> row.map { it == null } }
        selectedCell = null
        isSolved = false
    }

    LaunchedEffect(Unit) { generateNewPuzzle() }

    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 🔹 Header Bar
            BackButtonWithText(title = "Level $levels • ${difficulty.displayName}", onBackClick = onBackClick)

            Spacer(Modifier.weight(1f))

            Row {
                // Pyramid grid
                Column(modifier = Modifier.weight(0.7f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    PyramidGrid(levels,pyramid, editableMask, selectedCell) { r, c ->
                        if (editableMask.getOrNull(r)?.getOrNull(c) == true) {
                            selectedCell = Pair(r, c)
                            AudioPlayerManager.playSoundBtnClick()
                        }
                    }
                }

                // Right column: keypad + controls
                Column(modifier = Modifier.weight(0.3f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    AnimatedVisibility(visible = selectedCell != null, enter = fadeIn(), exit = fadeOut()) {
                        KeypadCompose(onKey = { label ->
                            selectedCell?.let { (r, c) ->
                                val current = pyramid[r][c] ?: 0
                                val newPyramid = pyramid.map { it.toMutableList() }.toMutableList()
                                when (label) {
                                    "Clear" -> {
                                        AudioPlayerManager.playSoundBtnBack()
                                        newPyramid[r][c] = null
                                    }
                                    "Erase" -> {
                                        AudioPlayerManager.playSoundBtnBack()
                                        val next = current / 10
                                        newPyramid[r][c] = if (next == 0) null else next
                                    }
                                    else -> {
                                        AudioPlayerManager.playSoundTilePlace()
                                        val digit = label.toIntOrNull()
                                        if (digit != null) {
                                            val next = current * 10 + digit
                                            if (next <= 9999) newPyramid[r][c] = next
                                        }
                                    }
                                }
                                pyramid = newPyramid
                                if (checkIfSolved(pyramid)) {
                                    AudioPlayerManager.playSoundWin()
                                    solveMarker.markSolved(
                                        com.jigar.me.ui.view.home.screens.math_game_zone.common.ZoneSolveCounter.MATH_PYRAMID,
                                        difficulty.name
                                    )
                                    solveMarker.recordWeekly(1)
                                    isSolved = true
                                    selectedCell = null
                                }
                            }
                        })
                    }

                    Spacer(modifier = Modifier.height(if (DeviceInfo.isTablet) AppDimens.Dimens24 else AppDimens.Dimens16))

                    KidsActionButton(
                        text = stringResource(R.string.start_new),
                        icon = Icons.Filled.Shuffle,
                        type = ButtonType.ORANGE,
                        isSmall = true,
                        onClick = {
                            generateNewPuzzle()
                        }
                    )
                }
            }

            Spacer(Modifier.weight(1f))
        }
    }

    if (isSolved) {
        PyramidResultOverlay(
            levels = levels,
            difficultyName = difficulty.displayName,
            onPlayAgain = {
                isSolved = false
                generateNewPuzzle()
            },
            onBack = {
                isSolved = false
                onBackClick()
            }
        )
    }
}

// "Candy Pop" celebration card — matches Number Snake's result popup style
// (gradient card, white badge overlapping the top edge, two symmetric filled
// buttons) instead of the generic CustomPopupView celebration theme.
@Composable
private fun PyramidResultOverlay(
    levels: Int,
    difficultyName: String,
    onPlayAgain: () -> Unit,
    onBack: () -> Unit
) {
    val accentColors = getButtonColors(ButtonType.ORANGE)
    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(1000); enabled = true }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
        LaunchedEffect(Unit) { AudioPlayerManager.playSoundClap() }
        ConfettiRainEffect()
        Box(modifier = Modifier.fillMaxWidth(0.6f), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(20.dp, RoundedCornerShape(AppDimens.Dimens20 * 1.2f), ambientColor = accentColors.base, spotColor = accentColors.base)
                    .background(accentColors.gradient, RoundedCornerShape(AppDimens.Dimens20 * 1.2f))
                    .padding(horizontal = AppDimens.Dimens30, vertical = AppDimens.Dimens20),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)
            ) {
                Spacer(Modifier.height(AppDimens.Dimens20))
                Text(
                    stringResource(R.string.you_did_it),
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                    fontSize = 26.sp.scaled(),
                    style = TextStyle(shadow = Shadow(color = accentColors.base.copy(alpha = 0.9f), offset = Offset(1.5f, 1.5f), blurRadius = 0f)),
                    textAlign = TextAlign.Center
                )
                Text(
                    "You built the pyramid",
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.font_semibold)),
                    fontSize = 18.sp.scaled(),
                    textAlign = TextAlign.Center
                )
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens4), verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clip(RoundedCornerShape(100f)).background(Color.White)
                        .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens6)) {
                    Text("🔺", fontSize = (16f * gameScale()).sp)
                    Text(
                        "Level $levels • $difficultyName",
                        color = accentColors.base,
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                        fontSize = 16.sp.scaled(),
                        maxLines = 1
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens20)) {
                    KidsActionButton(text = stringResource(R.string.continue_to_play), type = ButtonType.POSITIVE,
                        onClick = { if (enabled) onPlayAgain() })
                    KidsActionButton(text = stringResource(R.string.no_i_want_to_close), type = ButtonType.NEGATIVE,
                        onClick = { if (enabled) onBack() })
                }
            }

            Box(
                modifier = Modifier
                    .offset(y = (-28).dp)
                    .size(64.dp)
                    .shadow(8.dp, CircleShape)
                    .background(Color.White, CircleShape)
                    .border(4.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🔺", fontSize = (32f * gameScale()).sp)
            }
        }
    }
}

@Composable
private fun PyramidGrid(
    levels: Int,
    pyramid: List<List<Int?>>,
    editableMask: List<List<Boolean>>,
    selectedCell: Pair<Int, Int>?,
    onCellTap: (Int, Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens8), horizontalAlignment = Alignment.CenterHorizontally) {
        val rows = pyramid.size
        for (r in 0 until rows) {
            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)) {
                // add spacer left to center the row like pyramid
                val leftPadding = 0
                Spacer(modifier = Modifier.width(leftPadding.dp))
                for (c in 0 until pyramid[r].size) {
                    val value = pyramid[r][c]
                    val isEditable = editableMask.getOrNull(r)?.getOrNull(c) ?: false
                    val isSelected = selectedCell?.first == r && selectedCell.second == c
                    PyramidCell(levels, value = value, isEditable = isEditable, isSelected = isSelected) {
                        onCellTap(r, c)
                    }
                }
            }
        }
    }
}
@Composable
private fun PyramidCell(
    levels: Int,
    value: Int?,
    isEditable: Boolean,
    isSelected: Boolean,
    onTap: () -> Unit
) {

    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        label = ""
    )

    val originalWidth = AppDimens.PyramidWidth.value
    var width: Double = originalWidth.toDouble()
    var height: Double = originalWidth.toDouble()
    var baseColor = Color(0xFFE3F2FD)

    when (levels) {
        2 -> {
            width = (originalWidth * 1.9)
            height = (originalWidth * 1.2)
            baseColor = Color(0xFFE1F5FE)
        }
        3 -> {
            width = (originalWidth * 1.3)
            height = (originalWidth * 0.9)
            baseColor = Color(0xFFF3E5F5)
        }
        4 -> {
            width = (originalWidth * 1.1)
            height = (originalWidth * 0.7)
            baseColor = ColorLightYellow
        }
        5 -> {
            height = (originalWidth * 0.6)
            baseColor = Color(0xFFE8F5E9)
        }
        6 -> {
            width = (originalWidth * 0.95)
            height = (originalWidth * 0.56)
            baseColor = Color(0xFFFFEBEE)
        }
    }

    val fontSize = height * 0.6

    Card(
        modifier = Modifier
            .size(width.dp, height.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interaction,
                indication = null
            ) {
                if (isEditable) onTap()
            },
        shape = RoundedCornerShape(AppDimens.Dimens10),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> baseColor.copy(alpha = 0.9f)
                isEditable -> baseColor
                else -> Color.White
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) AppDimens.Dimens8 else if (isEditable) AppDimens.Dimens4 else AppDimens.Dimens2
        )
    ) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = when {
                    value != null -> value.toString()
                    isEditable -> "?"
                    else -> ""
                },
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.font_bold)),
                color = when {
                    value != null -> Color(0xFF3E2723)
                    isEditable -> Color.Gray
                    else -> Color.Transparent
                }
            )

            // ✅ Simple selected border (clean!)
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .border(
                            AppDimens.Dimens2,
                            Color(0xFF000000),
                            RoundedCornerShape(AppDimens.Dimens10)
                        )
                )
            }
        }
    }
}
@Composable
fun KeypadCompose(onKey: (String) -> Unit) {
    val buttons = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("Clear", "0", "Erase")
    )

    Column(modifier = Modifier.wrapContentWidth(), verticalArrangement = Arrangement.spacedBy(if (DeviceInfo.isTablet)AppDimens.Dimens10 else AppDimens.Dimens6)) {
        buttons.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(if (DeviceInfo.isTablet)AppDimens.Dimens10 else AppDimens.Dimens6)) {
                row.forEach { label ->
                    when (label){
                        "Erase" ->{
                            KidsKeyPad(
                                icon = painterResource(R.drawable.ic_backspace),
                                type = ButtonType.RED,
                                width = keyPadHeight,
                                height = keyPadHeight,
                                onClick = { onKey("Erase") }
                            )
                        }
                        "Clear" ->{
                            KidsKeyPad(
                                text = "C",
                                type = ButtonType.RED,
                                width = keyPadHeight,
                                height = keyPadHeight,
                                onClick = { onKey("Clear") }
                            )
                        }
                        else ->{
                            KidsKeyPad(
                                text = label,
                                type = ButtonType.GREEN,
                                width = keyPadHeight,
                                height = keyPadHeight,
                                onClick = { onKey(label) }
                            )
                        }
                    }
                }
            }
        }
    }
}
private fun checkIfSolved(pyramid: List<List<Int?>>): Boolean {
    // ensure all filled
    for (r in pyramid.indices) {
        for (c in pyramid[r].indices) {
            if (pyramid[r][c] == null) return false
        }
    }
    val n = pyramid.size
    if (n < 2) return true
    for (r in 0 until n - 1) {
        for (c in 0 until pyramid[r].size) {
            val top = pyramid[r][c]
            val left = pyramid[r + 1][c]
            val right = pyramid[r + 1][c + 1]
            if (top == null || left == null || right == null) return false
            if (top != left + right) return false
        }
    }
    return true
}