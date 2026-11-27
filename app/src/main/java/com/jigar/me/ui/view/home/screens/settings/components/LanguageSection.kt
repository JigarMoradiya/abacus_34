package com.jigar.me.ui.view.home.screens.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Language
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbDialog
import com.jigar.me.ui.view.ab.AbGlassPanel
import com.jigar.me.ui.view.ab.AbSelectCard
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbShrinkText
import com.jigar.me.ui.view.ab.AbSize
import com.jigar.me.ui.view.ab.AbTonePalette
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abSquash
import com.jigar.me.utils.LanguageManager
import kotlin.math.max

/**
 * The language of the app: a card like the voice card whose button shows the language in use and opens [LanguageDialog].
 * Shown only when there is more than one language to choose from.
 */
@Composable
fun LanguageSection(modifier: Modifier = Modifier, onLanguageClick: () -> Unit) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val palette = AbTonePalette.of(AbTone.Secondary, colors)
    AbGlassPanel(modifier, padding = 12.dp) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp * s)) {
            SettingsCardTitle(icon = Icons.Rounded.Language, title = stringResource(R.string.settings_language_title))
            Box(Modifier.fillMaxWidth().height(38.dp * s), contentAlignment = Alignment.Center) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .abSquash(
                            face = palette.face, edge = palette.edge, cornerRadius = AbShapes.pill,
                            edgeHeight = 3.5.dp * s, onClick = onLanguageClick,
                        )
                        .heightIn(min = 32.dp * s)
                        .padding(horizontal = 12.dp * s),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AbShrinkText(
                        text = currentLanguageLabel(),
                        style = AbTypography.label(s),
                        color = Color.White,
                        minScale = 0.6f,
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

/** The chosen language written in itself. */
@Composable
private fun currentLanguageLabel(): String {
    val tag = LanguageManager.currentTag()
    return stringResource((LanguageManager.all.firstOrNull { it.tag == tag } ?: LanguageManager.all.first()).nameRes)
}

/** The language picker, the same as iOS: every language written in itself, as a grid of cards; the one in use is filled.
 *  Choosing one closes the picker and applies it at once (the screen is redrawn in that language). */
@Composable
fun LanguageDialog(languages: List<LanguageManager.Language>, onDismiss: () -> Unit) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val current = LanguageManager.currentTag()
    AbDialog(
        title = stringResource(R.string.settings_language_title),
        icon = Icons.Rounded.Language,
        onDismiss = onDismiss,
        widthFraction = 0.72f,
        footer = { AbButton(text = stringResource(R.string.close), tone = AbTone.Purple, size = AbSize.Small, onClick = onDismiss) },
    ) {
        // like the iOS adaptive grid: as many 130 wide columns as fit, each card centred in its column
        BoxWithConstraints(Modifier.fillMaxWidth().padding(vertical = 4.dp * s)) {
            val gap = 10.dp * s
            val card = 130.dp * s
            val cols = max(1, ((maxWidth + gap) / (card + gap)).toInt())
            Column(verticalArrangement = Arrangement.spacedBy(gap)) {
                languages.chunked(cols).forEach { row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gap)) {
                        row.forEach { language ->
                            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                AbSelectCard(
                                    title = stringResource(language.nameRes), accent = colors.brandPrimary,
                                    isSelected = language.tag == current, width = card, compact = true,
                                    symbol = language.tag.uppercase(),
                                    onClick = {
                                        onDismiss()
                                        if (language.tag != current) LanguageManager.set(language.tag)
                                    },
                                )
                            }
                        }
                        repeat(cols - row.size) { Box(Modifier.weight(1f)) }
                    }
                }
            }
        }
    }
}
