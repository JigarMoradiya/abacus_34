package com.jigar.me.ui.view.home.screens.levels.level3.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.theme.AppDimens

@Composable
fun L3Numpad(
    typedValue:     String,
    onDigit:        (Int) -> Unit,
    onDelete:       () -> Unit,
    onConfirm:      () -> Unit,
    modifier:       Modifier = Modifier,
    confirmEnabled: Boolean  = typedValue.isNotEmpty(),
) {
    val answerShape = RoundedCornerShape(AppDimens.Dimens12)
    Column(
        modifier             = modifier,
        verticalArrangement  = Arrangement.spacedBy(AppDimens.Dimens4)
    ) {
        // Answer display — white card so it pops against the translucent keys
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.4f)
                .shadow(5.dp, answerShape)
                .background(Color.White.copy(0.92f), answerShape)
                .padding(horizontal = AppDimens.Dimens12),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = typedValue.ifEmpty { "?" },
                fontSize   = 38.sp.scaled(),
                color      = if (typedValue.isEmpty()) Color(0xFF1A237E).copy(0.30f) else Color(0xFF1A237E),
                fontWeight = FontWeight.Black,
                textAlign  = TextAlign.Center,
            )
        }

        // Digit rows
        listOf(listOf(7, 8, 9), listOf(4, 5, 6), listOf(1, 2, 3)).forEach { row ->
            Row(
                modifier              = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens4)
            ) {
                row.forEach { digit ->
                    L3Key(
                        label      = "$digit",
                        startColor = Color.White.copy(0.28f),
                        endColor   = Color.White.copy(0.18f),
                        modifier   = Modifier.weight(1f).fillMaxHeight()
                    ) { onDigit(digit) }
                }
            }
        }

        // Bottom row: delete, 0, confirm
        Row(
            modifier              = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens4)
        ) {
            L3Key("⌫", Color(0xFFC62828), Color(0xFF8B0000),
                Modifier.weight(1f).fillMaxHeight()) { onDelete() }
            L3Key("0", Color(0xFF546E7A), Color(0xFF263238),
                Modifier.weight(1f).fillMaxHeight()) { onDigit(0) }
            L3Key("✓", Color(0xFF2E7D32), Color(0xFF1B5E20),
                Modifier.weight(1f).fillMaxHeight(), enabled = confirmEnabled) { onConfirm() }
        }
    }
}

@Composable
private fun L3Key(
    label:      String,
    startColor: Color,
    endColor:   Color,
    modifier:   Modifier = Modifier,
    enabled:    Boolean  = true,
    onClick:    () -> Unit,
) {
    val shape = RoundedCornerShape(AppDimens.Dimens12)
    Box(
        modifier = modifier
            .background(
                if (enabled) Brush.linearGradient(listOf(startColor, endColor))
                else Brush.linearGradient(listOf(Color.Gray.copy(0.5f), Color.Gray.copy(0.3f))),
                shape
            )
            .clickable(remember { MutableInteractionSource() }, null, enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = label,
            style      = MaterialTheme.typography.titleLarge.scaled(),
            color      = Color.White.copy(if (enabled) 1f else 0.5f),
            fontWeight = FontWeight.Black,
            textAlign  = TextAlign.Center,
        )
    }
}

@Composable
fun L3ResultBtn(label: String, filled: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(AppDimens.Dimens100)
    Box(
        modifier = Modifier
            .padding(AppDimens.Dimens3)
            .shadow(AppDimens.Dimens4, shape)
            .background(if (filled) Color.White else Color.White.copy(0.22f), shape)
            .clickable(remember { MutableInteractionSource() }, null) { onClick() }
            .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens12),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            style      = MaterialTheme.typography.labelLarge.scaled(),
            color      = if (filled) Color(0xFF1A237E) else Color.White,
            fontWeight = FontWeight.Black
        )
    }
}
