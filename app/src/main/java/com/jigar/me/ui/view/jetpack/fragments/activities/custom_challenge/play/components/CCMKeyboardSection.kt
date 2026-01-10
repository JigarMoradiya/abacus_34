package com.jigar.me.ui.view.jetpack.fragments.activities.custom_challenge.play.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.abacus_base.AbacusTheme
import com.jigar.me.ui.view.jetpack.fragments.activities.custom_challenge.play.viewmodels.CCMPlayUiState
import com.jigar.me.ui.view.jetpack.fragments.activities.custom_challenge.play.viewmodels.CCMPlayViewModel
import java.util.Locale

@Composable
fun CCMKeyboardSection(
    uiState: CCMPlayUiState,
    viewModel: CCMPlayViewModel
) {
    val theme = AbacusTheme.colorPreset(viewModel.selectedTheme)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            text = stringResource(R.string.set_your_answer),
            style = MaterialTheme.typography.titleLarge.copy(color = Color.DarkGray, fontWeight = FontWeight.Medium,fontFamily = FontFamily(Font(R.font.font_medium))),
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.activity_padding4)))

        Text(
            text = uiState.answerText,
            style = MaterialTheme.typography.displaySmall.copy(color = theme.buttonColor, fontWeight = FontWeight.Bold,fontFamily = FontFamily(Font(R.font.font_bold))),
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.activity_padding8)))

        KeypadRow(listOf("1","2","3","4","5")) { viewModel.addKeyboardValue(it) }
        Spacer(Modifier.height(dimensionResource(R.dimen.activity_padding6)))
        KeypadRow(listOf("6","7","8","9","0")) { viewModel.addKeyboardValue(it) }
        Spacer(Modifier.height(dimensionResource(R.dimen.activity_padding6)))
        Row(horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.activity_padding4))) {
            KeyButton("C") { viewModel.clearKeyboard() }
            IconKeyButton(Icons.AutoMirrored.Filled.Backspace) { viewModel.eraseKeyboardValue() }
        }

        Spacer(Modifier.height(dimensionResource(R.dimen.activity_padding16)))

        Surface(
            onClick = { viewModel.submitAnswer() },
            shape = RoundedCornerShape(50.dp),
            color = colorResource(R.color.colorPrimary),
            tonalElevation = 0.dp,
            shadowElevation = dimensionResource(R.dimen.activity_padding8)
        ) {
            Text(
                text = stringResource(R.string.check_answer).uppercase(Locale.getDefault()), style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.font_bold))), modifier = Modifier.padding(
                    horizontal = dimensionResource(R.dimen.activity_padding12), vertical = dimensionResource(R.dimen.activity_padding10)
                )
            )
        }
    }
}

@Composable
fun KeypadRow(
    keys: List<String>,
    onClick: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        keys.forEach {
            KeyButton(text = it) { onClick(it) }
        }
    }
}

@Composable
fun KeyButton(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.Transparent,
        border = BorderStroke(1.dp, Color.DarkGray),
        modifier = Modifier.size(40.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge.copy(color = Color.DarkGray, fontWeight = FontWeight.Medium,fontFamily = FontFamily(Font(R.font.font_semibold))),
            )
        }
    }
}

@Composable
fun IconKeyButton(
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        border = BorderStroke(1.dp, Color.DarkGray),
        modifier = Modifier.size(40.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null,tint = Color.DarkGray)
        }
    }
}



