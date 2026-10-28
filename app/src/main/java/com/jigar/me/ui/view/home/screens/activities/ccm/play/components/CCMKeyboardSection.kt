package com.jigar.me.ui.view.home.screens.activities.ccm.play.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.ab.AB_SF_TO_VECTOR
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbGlassPanel
import com.jigar.me.ui.view.ab.AbShrinkText
import com.jigar.me.ui.view.ab.AbSize
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.ab.abSquash
import com.jigar.me.ui.view.home.screens.activities.ActivityDimens
import com.jigar.me.ui.view.home.screens.activities.ccm.play.viewmodels.CCMPlayUiState
import com.jigar.me.ui.view.home.screens.activities.ccm.play.viewmodels.CCMPlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens

@Composable
fun CCMKeyboardSection(
    uiState: CCMPlayUiState,
    viewModel: CCMPlayViewModel
) {
    val s = LocalAbScale.current.factor
    val colors = LocalAbColors.current
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens10)) {

        // what has been typed (or 0), on a calm panel as wide as the three keys
        AbGlassPanel(Modifier.width(ActivityDimens.keypadItem * 3 + AppDimens.Dimens8 * 2)) {
            Box(Modifier.fillMaxWidth().height(40.dp * s), contentAlignment = Alignment.Center) {
                AbShrinkText(
                    text = uiState.answerText.ifEmpty { "0" },
                    style = AbTypography.fixed(28f * s, FontWeight.ExtraBold),
                    color = colors.textPrimary,
                    minScale = 0.5f,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)) {
            val onKey: (String) -> Unit = {
                when (it) {
                    "C" -> viewModel.clearKeyboard()
                    "⌫" -> viewModel.eraseKeyboardValue()
                    else -> viewModel.addKeyboardValue(it)
                }
            }
            KeypadRow(listOf("1", "2", "3"), onKey)
            KeypadRow(listOf("4", "5", "6"), onKey)
            KeypadRow(listOf("7", "8", "9"), onKey)
            KeypadRow(listOf("C", "0", "⌫"), onKey)
        }

        AbButton(
            text = stringResource(R.string.check_answer),
            modifier = Modifier.padding(top = AppDimens.Dimens4),
            tone = AbTone.Warning,
            size = AbSize.Small,
            minHeightOverride = 34f, // same text, a lower face
            icon = Icons.Default.Check,
            onClick = { viewModel.submitAnswer() },
        )
    }
}

@Composable
fun KeypadRow(
    keys: List<String>,
    onClick: (String) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)) {
        keys.forEach { key -> AbKeypadKey(key) { onClick(key) } }
    }
}

/**
 * One key of the answer keypad: a chunky white key with indigo ink, or a red one for "C" (clear) and "⌫" (erase).
 * It is as big as the keypad item in every direction, and the 3D edge is part of that size. The view models play the
 * keys' own sounds, so the key itself is silent.
 */
@Composable
fun AbKeypadKey(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val isDelete = label == "C" || label == "⌫"
    val face = if (isDelete) colors.danger else Color.White
    val edge = if (isDelete) colors.danger.abDarkened(0.3f) else Color(0xFFC9CEE6)
    val ink = if (isDelete) Color.White else Color(0xFF1E2470)
    val edgeHeight = 4.dp * s
    val faceHeight = ActivityDimens.keypadItem - edgeHeight
    Box(
        modifier
            .size(ActivityDimens.keypadItem)
            .abSquash(face, edge, null, 14.dp * s, edgeHeight, true, sound = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (label == "⌫") {
            // the kit font has no erase glyph, so it is a symbol that scales with the key
            Icon(Icons.AutoMirrored.Filled.Backspace, null, Modifier.size(faceHeight * 0.5f * AB_SF_TO_VECTOR), tint = ink)
        } else {
            Text(label, style = AbTypography.fixed(faceHeight.value * 0.55f, FontWeight.ExtraBold), color = ink, maxLines = 1)
        }
    }
}
