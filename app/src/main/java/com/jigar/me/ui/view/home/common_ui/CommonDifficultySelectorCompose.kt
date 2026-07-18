package com.jigar.me.ui.view.home.common_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4
import com.jigar.me.ui.view.home.theme.AppDimens

// ── Shared iOS-matching difficulty selector ────────────────────────────────
// Matches iOS: pills grouped inside a white translucent rounded box; each pill
// is black-filled + white bold text + shadow when selected, white + black
// regular text otherwise, with a light gray border. Used by ALL Math Game
// Zone games so the difficulty UI is identical everywhere (and to iOS).

@Composable
fun DifficultyPillBox(content: @Composable RowScope.() -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(AppDimens.Dimens12))
            .background(Color.White.copy(alpha = 0.75f))
            .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens12),
        content = content
    )
}

@Composable
fun DifficultyPillItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(100f)
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .then(if (isSelected) Modifier.shadow(AppDimens.Dimens6, shape) else Modifier)
            .clip(shape)
            .background(if (isSelected) Color.Black else Color.White.copy(alpha = 0.9f))
            .border(1.dp, Color.Gray.copy(alpha = 0.2f), shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color.Black,
            fontFamily = FontFamily(Font(if (isSelected) R.font.font_extra_bold else R.font.font_regular)),
            fontSize = 15.sp.scaled()
        )
    }
}

@Composable
fun CommonDifficultySelectorCompose(
    selected: CommonDifficulty4,
    onSelect: (CommonDifficulty4) -> Unit,
    label: (CommonDifficulty4) -> String = { it.displayName }
) {
    DifficultyPillBox {
        CommonDifficulty4.entries.forEach { d ->
            DifficultyPillItem(text = label(d), isSelected = d == selected) { onSelect(d) }
        }
    }
}
