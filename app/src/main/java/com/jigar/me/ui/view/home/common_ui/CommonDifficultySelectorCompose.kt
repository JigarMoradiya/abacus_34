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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.FontWeight
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abChunkyFace
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abGameTray

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
            .abGameTray()
            .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens12),
        content = content
    )
}

@Composable
fun DifficultyPillItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    // the selected choice is a chunky brand pill; the others sit flat on the tray at the same height (its 3D edge)
    val look = if (isSelected) {
        Modifier.abChunkyFace(colors.brandPrimary, colors.brandPrimaryDark, null, AbShapes.pill, 3.dp * s)
    } else {
        Modifier.padding(bottom = 3.dp * s).background(colors.bgSecondary, CircleShape).border(2.dp, colors.outline, CircleShape)
    }
    Text(
        text = text,
        style = AbTypography.fixed(14f * s, FontWeight.ExtraBold),
        color = if (isSelected) Color.White else colors.textPrimary,
        modifier = Modifier
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() }
            .then(look)
            .padding(horizontal = 14.dp * s, vertical = 6.dp * s)
    )
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
