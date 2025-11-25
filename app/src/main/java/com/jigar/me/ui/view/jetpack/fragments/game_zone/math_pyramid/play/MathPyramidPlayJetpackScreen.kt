package com.jigar.me.ui.view.jetpack.fragments.game_zone.math_pyramid.play

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.fragments.common.BackButtonWithText
import com.jigar.me.ui.view.jetpack.fragments.common.dialogs.CustomPopupView
import com.jigar.me.ui.view.jetpack.fragments.common.enums.CommonDifficulty4
import com.jigar.me.ui.view.jetpack.fragments.game_zone.math_pyramid.play.components.NumberPyramidGenerator
import com.jigar.me.utils.PlaySound

@Composable
fun MathPyramidPlayJetpackScreen(
    levels: Int = 4,
    difficulty: CommonDifficulty4 = CommonDifficulty4.easy,
    onBackClick: () -> Unit
) {
    var pyramid by remember { mutableStateOf<List<List<Int?>>>(emptyList()) }
    var editableMask by remember { mutableStateOf<List<List<Boolean>>>(emptyList()) }
    var selectedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var isSolved by remember { mutableStateOf(false) }
    val context = LocalContext.current

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

    Box(modifier = Modifier.fillMaxSize()) {
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
                            PlaySound.playTap(context)
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
                                        PlaySound.playClear(context)
                                        newPyramid[r][c] = null
                                    }
                                    "Erase" -> {
                                        PlaySound.playClear(context)
                                        val next = current / 10
                                        newPyramid[r][c] = if (next == 0) null else next
                                    }
                                    else -> {
                                        PlaySound.playClick(context)
                                        val digit = label.toIntOrNull()
                                        if (digit != null) {
                                            val next = current * 10 + digit
                                            if (next <= 9999) newPyramid[r][c] = next
                                        }
                                    }
                                }
                                pyramid = newPyramid
                                if (checkIfSolved(pyramid)) {
                                    PlaySound.playWin(context)
                                    isSolved = true
                                    selectedCell = null
                                }
                            }
                        })
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val shape = RoundedCornerShape(50)
                    Box(modifier = Modifier.shadow(elevation = 8.dp,shape = shape, clip = false)) {
                        Button(
                            onClick = {
                                PlaySound.playHint(context)
                                generateNewPuzzle() },
                            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.colorPrimary),
                                contentColor = colorResource(R.color.white)),
                            contentPadding = PaddingValues(horizontal = dimensionResource(R.dimen.activity_padding16))
                        ) {
                            // --- 1. Shuffle Icon (Left side) ---
                            Icon(imageVector = Icons.Filled.Shuffle, contentDescription = null,)

                            // Add a small spacer between the icon and the text
                            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.activity_padding6))) // Adjust spacing as needed

                            // --- 2. Text (Right side) ---
                            Text(text = stringResource(R.string.start_new), fontSize = dimensionResource(R.dimen.textSizeSuperExtraLarge).value.sp,
                                fontFamily = FontFamily(Font(R.font.font_bold)))
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))
        }

        AnimatedVisibility(
            visible = isSolved,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            CustomPopupView(
                title = stringResource(R.string.you_did_it),
                description = "You built the pyramid",
                positiveButtonText = stringResource(R.string.continue_to_play),
                negativeButtonText = stringResource(R.string.no_i_want_to_close),
                icon = R.drawable.ic_complete,
                widthMultiplier = 0.5f,
                onPositiveTapped = {
                    isSolved = false
                    generateNewPuzzle()
                },
                onNegativeTapped = {
                    isSolved = false
                    onBackClick()
                }
            )
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
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        val rows = pyramid.size
        for (r in 0 until rows) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // add spacer left to center the row like pyramid
//                val leftPadding = (rows - r - 1) * 8
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
private fun PyramidCell(levels : Int,value: Int?, isEditable: Boolean, isSelected: Boolean, onTap: () -> Unit) {
    val originalWidth = 72
    var width : Double = originalWidth.toDouble()
    var height : Double = originalWidth.toDouble()
    var color = Color.Cyan
    if (levels == 2){
        width = (originalWidth * 1.9)
        height = (originalWidth * 1.2)
        color = Color.Cyan
    }else if (levels == 3){
        width = (originalWidth * 1.3)
        height = (originalWidth * 0.9)
        color = Color.Magenta
    }else if (levels == 4){
        width = (originalWidth * 1.1)
        height = (originalWidth * 0.7)
        color = Color.Yellow
    }else if (levels == 5){
        height = (originalWidth * 0.6)
        color = Color.Green
    }else if (levels == 6){
        width = (originalWidth * 0.95)
        height = (originalWidth * 0.56)
        color = Color.Red
    }
    val fonts = height * 0.6
    Card(
        modifier = Modifier
            .size(width = width.dp, height = height.dp)
            .clickable { if (isEditable) onTap() },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(  // Material3 syntax
            defaultElevation = if (isEditable) 6.dp else 2.dp
        ))
    {
        Box(modifier = Modifier.fillMaxSize()
            .background(
                when {
                    isEditable -> color.copy(alpha = if (isSelected) 0.3f else 0.1F)                 // empty editable
                    value != null -> Color.White                     // fixed number
                    else -> color.copy(alpha = if (isSelected) 0.3f else 0.1F)      // optional style
                }
            ),contentAlignment = Alignment.Center) {
            when {
                value != null -> Text(text = value.toString(), fontSize = fonts.sp,
                    fontFamily = FontFamily(Font(R.font.font_bold)))
                isEditable -> Text(text = "?", fontSize = fonts.sp, color = Color.Black.copy(alpha = 0.5f),fontFamily = FontFamily(Font(R.font.font_bold)))
                else -> Text("", fontSize = fonts.sp,fontFamily = FontFamily(Font(R.font.font_bold)))
            }
            if (isSelected) {
                // simple selected overlay
                Box(modifier = Modifier.matchParentSize().background(color.copy(alpha = 0.3f)))
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
        listOf("Erase", "0", "Clear")
    )

    Column(modifier = Modifier.wrapContentWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        buttons.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { label ->
                    val bgColor = when (label) {
                        "Erase", "Clear" -> colorResource(R.color.red_400)
                        else -> colorResource(R.color.colorPrimary)
                    }
                    Box(modifier = Modifier.size(width = 48.dp, height = 36.dp).background(bgColor
                        , shape = RoundedCornerShape(8.dp)
                    ).clickable{
                        onKey(label)
                    },contentAlignment = Alignment.Center){
                        when (label){
                            "Erase" ->{
                                Icon(
                                    painter = painterResource(R.drawable.ic_backspace), // Replace with your Backspace/Erase Icon
                                    contentDescription = "Erase",
                                    tint = colorResource(R.color.white),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            "Clear" ->{
                                Text(text = "C",
                                    color = colorResource(R.color.white),
                                    fontSize = dimensionResource(id = R.dimen.textSize24).value.sp,
                                    fontFamily = FontFamily(Font(R.font.font_bold)))
                            }
                            else ->{
                                Text(text = label,
                                    color = colorResource(R.color.white),
                                    fontSize = dimensionResource(id = R.dimen.textSize24).value.sp,
                                    fontFamily = FontFamily(Font(R.font.font_bold)))
                            }
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

@Preview(
    name = "Math Pyramid Home - Light",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 800,
    heightDp = 400
)
@Composable
fun NumberPuzzleHomeScreenPreview() {
    MaterialTheme {
        MathPyramidPlayJetpackScreen(
            levels = 6,
            difficulty= CommonDifficulty4.easy,
            onBackClick = {}
        )
    }
}