package com.jigar.me.ui.view.home.common_ui.sheets

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens20
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens24
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.home.theme.PrimaryBlue

// Reusable gate sheet — same UI for both the auto-trigger milestone moments
// and the manual "Rate Us" entry in the parent menu.
@Composable
fun ReviewGateBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    onPositive: () -> Unit,
    onNegative: () -> Unit,
) {
    KidsBottomSheet(visible = visible, onDismiss = onDismiss, widthFraction = 0.62f) {
        val infiniteTransition = rememberInfiniteTransition(label = "smiley")
        val smileyOffsetY by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = -10f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "smileyBounce"
        )

        Spacer(modifier = Modifier.height(Dimens12))

        Text(
            text = "😄",
            style = MaterialTheme.typography.displayLarge.scaled(),
            modifier = Modifier.graphicsLayer { translationY = smileyOffsetY }
        )

        Spacer(modifier = Modifier.height(Dimens12))

        Text(
            text = "Enjoying the App?",
            style = MaterialTheme.typography.titleMedium.scaled(),
            fontWeight = FontWeight.Black,
            color = PrimaryBlue,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimens8))

        Text(
            text = "We'd love to know how it's going so far!",
            style = MaterialTheme.typography.bodyMedium.scaled(),
            color = Color.Black.copy(alpha = 0.65f),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens20)
        )

        Spacer(modifier = Modifier.height(Dimens20))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens16, vertical = Dimens8),
            horizontalArrangement = Arrangement.spacedBy(Dimens12, alignment = Alignment.CenterHorizontally)
        ) {
            KidsActionButton(
                text = "Not too much 😐",
                type = ButtonType.NEGATIVE,
                onClick = onNegative,
            )
            KidsActionButton(
                text = "Yes, Enjoying! 😄",
                type = ButtonType.POSITIVE,
                onClick = onPositive,
            )
        }

        Spacer(modifier = Modifier.height(Dimens24))
    }
}
