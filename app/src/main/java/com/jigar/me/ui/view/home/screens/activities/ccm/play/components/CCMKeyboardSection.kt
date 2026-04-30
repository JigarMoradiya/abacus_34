package com.jigar.me.ui.view.home.screens.activities.ccm.play.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.base.abacus_base.AbacusTheme
import com.jigar.me.ui.view.home.common_ui.buttons.KidsKeyPad
import com.jigar.me.ui.view.home.screens.activities.ccm.play.viewmodels.CCMPlayUiState
import com.jigar.me.ui.view.home.screens.activities.ccm.play.viewmodels.CCMPlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens6
import com.jigar.me.ui.view.home.theme.AppDimens.keyPadHeight
import com.jigar.me.ui.view.home.theme.ButtonType

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

        Spacer(Modifier.height(AppDimens.Dimens4))

        Text(
            text = uiState.answerText,
            style = MaterialTheme.typography.displaySmall.copy(color = theme.buttonColor, fontWeight = FontWeight.Bold,fontFamily = FontFamily(Font(R.font.font_bold))),
        )

        Spacer(Modifier.height(AppDimens.Dimens8))

        KeypadRow(listOf("1", "2", "3")) { viewModel.addKeyboardValue(it) }
        Spacer(Modifier.height(Dimens6))

        KeypadRow(listOf("4", "5", "6")) { viewModel.addKeyboardValue(it) }
        Spacer(Modifier.height(Dimens6))

        KeypadRow(listOf("7", "8", "9")) { viewModel.addKeyboardValue(it) }
        Spacer(Modifier.height(Dimens6))

        Row(horizontalArrangement = Arrangement.spacedBy(Dimens4)) {
            KidsKeyPad(
                text = "C",
                type = ButtonType.RED,
                width = keyPadHeight,
                height = keyPadHeight,
                onClick = { viewModel.clearKeyboard() }
            )
            KidsKeyPad(
                text = "0",
                type = ButtonType.GREEN,
                width = keyPadHeight,
                height = keyPadHeight,
                onClick = { viewModel.addKeyboardValue("0") }
            )
            KidsKeyPad(
                icon = painterResource(R.drawable.ic_backspace),
                type = ButtonType.RED,
                width = keyPadHeight,
                height = keyPadHeight,
                onClick = { viewModel.eraseKeyboardValue() }
            )
        }
        Spacer(Modifier.height(AppDimens.Dimens16))

        Surface(
            onClick = { viewModel.submitAnswer() },
            shape = RoundedCornerShape(AppDimens.Dimens50),
            color = colorResource(R.color.colorPrimary),
            tonalElevation = 0.dp,
            shadowElevation = AppDimens.Dimens8
        ) {
            Text(
                text = stringResource(R.string.check_answer).uppercase(), style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.font_bold))), modifier = Modifier.padding(
                    horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens10
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
        horizontalArrangement = Arrangement.spacedBy(Dimens4)
    ) {
        keys.forEach {
            KidsKeyPad(
                text = it,
                type = ButtonType.GREEN,
                width = keyPadHeight,
                height = keyPadHeight,
                onClick = { onClick(it) }
            )
        }
    }
}
