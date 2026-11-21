package com.jigar.me.ui.view.home.common_ui.sheets

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbCard
import com.jigar.me.ui.view.ab.AbFormTextArea
import com.jigar.me.ui.view.ab.AbMenuPicker
import com.jigar.me.ui.view.ab.AbSize
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbTopBar
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.home.theme.AppDimens

/**
 * Why a parent tapped the thumb down. The list the parent picks from is shown in the app language ([label]); the e-mail to support
 * carries [mailText], always in English (support reads English, and it can be counted).
 */
enum class ReviewFeedbackReason(@StringRes val label: Int, val mailText: String) {
    TooHard(R.string.feedback_reason_hard, "It is too difficult"),
    TooEasy(R.string.feedback_reason_easy, "It is too easy"),
    NotEnoughContent(R.string.feedback_reason_content, "Not enough lessons or games"),
    Bugs(R.string.feedback_reason_bugs, "Bugs or crashes"),
    TooManyLocked(R.string.feedback_reason_locked, "Too many locked features"),
    Price(R.string.feedback_reason_price, "The price is too high"),
    Other(R.string.feedback_reason_other, "Something else"),
}

/** The paragraph a parent may write is cut at this length, so the mail link stays short enough for every mail app. */
const val FEEDBACK_MAX_CHARS = 1000

/**
 * The one-page form behind the thumb down: pick what went wrong, say more in a paragraph, send. [onSubmit] gets the reason and the
 * paragraph (may be empty) and opens the mail app; [onClose] (the back button or the system back) leaves without sending.
 * A full-screen page (it covers the Home screen), like the other pages of the app.
 */
@Composable
fun ReviewFeedbackPage(
    visible: Boolean,
    onClose: () -> Unit,
    onSubmit: (ReviewFeedbackReason, String) -> Unit,
) {
    AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
        BackHandler { onClose() }
        val colors = LocalAbColors.current
        val s = LocalAbScale.current.factor
        var reason by remember { mutableStateOf<ReviewFeedbackReason?>(null) }
        var details by remember { mutableStateOf("") }
        val labels = ReviewFeedbackReason.entries.associateWith { stringResource(it.label) }

        Box(Modifier.fillMaxSize()) {
            AbBackground(AbWorld.Meadow, Modifier.fillMaxSize())
            // safeDrawing includes the keyboard, so the form moves up and scrolls when the keyboard is open
            Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
                AbTopBar(
                    title = stringResource(R.string.feedback_title),
                    onBack = onClose,
                    modifier = Modifier.padding(
                        start = DeviceInfo.screenHorizontalPadding(),
                        end = AppDimens.Dimens16,
                        top = DeviceInfo.screenTopPadding()
                    )
                )
                Column(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AbCard(Modifier.widthIn(max = 640.dp * s)) {
                        // the question and its answer on one line, so the whole form fits a landscape phone without scrolling
                        Row(
                            Modifier.fillMaxWidth().padding(bottom = 10.dp * s),
                            horizontalArrangement = Arrangement.spacedBy(12.dp * s),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                stringResource(R.string.feedback_reason_label),
                                modifier = Modifier.weight(1f),
                                style = AbTypography.body(s).copy(fontWeight = FontWeight.ExtraBold),
                                color = colors.textPrimary,
                            )
                            AbMenuPicker(
                                items = ReviewFeedbackReason.entries,
                                selectedTitle = reason?.let { labels[it] }.orEmpty(),
                                title = { labels.getValue(it) },
                                onSelect = { reason = it },
                                placeholder = stringResource(R.string.feedback_reason_placeholder),
                                minWidth = 300.dp * s,
                            )
                        }
                        Text(
                            stringResource(R.string.feedback_details_label),
                            style = AbTypography.body(s).copy(fontWeight = FontWeight.ExtraBold),
                            color = colors.textPrimary,
                        )
                        AbFormTextArea(
                            icon = Icons.Rounded.Edit,
                            placeholder = stringResource(R.string.feedback_details_hint),
                            text = details,
                            onTextChange = { details = it.take(FEEDBACK_MAX_CHARS) },
                            modifier = Modifier.padding(top = 6.dp * s, bottom = 10.dp * s),
                            height = 84.dp,
                        )
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp * s),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                stringResource(R.string.feedback_mail_note),
                                modifier = Modifier.weight(1f),
                                style = AbTypography.label(s),
                                color = colors.textSecondary,
                            )
                            AbButton(
                                text = stringResource(R.string.feedback_send),
                                tone = AbTone.Primary,
                                size = AbSize.Medium,
                                enabled = reason != null,
                                icon = Icons.Rounded.Send,
                                iconAtEnd = true,
                                onClick = { reason?.let { onSubmit(it, details.trim()) } },
                            )
                        }
                    }
                }
            }
        }
    }
}
