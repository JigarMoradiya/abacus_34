package com.jigar.me.ui.view.home.screens.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
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
import com.jigar.me.ui.view.ab.AbMenuPicker
import com.jigar.me.ui.view.ab.AbSettingRow
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

/** One entry of the picker. */
private data class LanguageChoice(val tag: String, val title: String)

/** The language picker. Choosing a language applies it at once (the screen is redrawn in that language). */
@Composable
fun LanguageDialog(languages: List<LanguageManager.Language>, onDismiss: () -> Unit) {
    val s = LocalAbScale.current.factor
    val choices = languages.map { LanguageChoice(it.tag, stringResource(it.nameRes)) }
    val selected = choices.firstOrNull { it.tag == LanguageManager.currentTag() } ?: choices.first()
    AbDialog(
        title = stringResource(R.string.settings_language_title),
        icon = Icons.Rounded.Language,
        onDismiss = onDismiss,
    ) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp * s)) {
            AbSettingRow(label = stringResource(R.string.settings_language_title), icon = Icons.Rounded.Language) {
                AbMenuPicker(
                    items = choices,
                    selectedTitle = selected.title,
                    title = { it.title },
                    onSelect = {
                        if (it.tag != selected.tag) LanguageManager.set(it.tag)
                        onDismiss()
                    },
                    minWidth = 150.dp * s,
                )
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                AbButton(
                    text = stringResource(R.string.txtCancel), tone = AbTone.Ghost, size = AbSize.Small,
                    icon = Icons.Rounded.Close, onClick = onDismiss,
                )
            }
        }
    }
}
