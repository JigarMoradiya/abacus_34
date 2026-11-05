package com.jigar.me.ui.view.other.contactus

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbConfirmDialog
import com.jigar.me.ui.view.ab.AbFormField
import com.jigar.me.ui.view.ab.AbFormTextArea
import com.jigar.me.ui.view.ab.AbSectionLabel
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbSize
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbTopBar
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.home.common_ui.Loader
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.other.contactus.components.CountryCodePickerView
import com.jigar.me.ui.view.other.contactus.viewmodels.ContactUsViewModel
import com.jigar.me.utils.extensions.toastL

@Composable
fun ContactUsScreen(
    onFinish: () -> Unit,
) {
    val viewModel: ContactUsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    AbTheme {
        val colors = LocalAbColors.current
        val s = LocalAbScale.current.factor
        val density = LocalDensity.current
        val moveDown = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })

        Box(Modifier.fillMaxSize()) {
            AbBackground(AbWorld.Lagoon, Modifier.fillMaxSize())

            Column(modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                AbTopBar(
                    title = stringResource(R.string.contact_us),
                    onBack = { viewModel.onBack() },
                    modifier = Modifier.padding(
                        start = DeviceInfo.screenHorizontalPadding(),
                        end = AppDimens.Dimens16,
                        top = DeviceInfo.screenTopPadding()
                    )
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(
                            start = DeviceInfo.screenHorizontalPadding(),
                            end = AppDimens.Dimens16,
                            top = AppDimens.Dimens10,
                            bottom = AppDimens.Dimens16
                        )
                ) {
                    Column(
                        modifier = Modifier.widthIn(max = 720.dp).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)
                    ) {
                        AbSectionLabel(stringResource(R.string.contact_us_fill_detail))

                        // Name
                        AbFormField(
                            icon = Icons.Rounded.Person,
                            placeholder = stringResource(R.string.your_name),
                            text = uiState.name,
                            onTextChange = viewModel::onNameChange,
                            capitalization = KeyboardCapitalization.Words,
                            error = uiState.nameError?.let { stringResource(it) },
                            imeAction = ImeAction.Next,
                            keyboardActions = moveDown
                        )

                        // Country code + Mobile
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
                            verticalAlignment = Alignment.Top
                        ) {
                            // the country code sits on a plate like the fields beside it
                            val plateShape = RoundedCornerShape(AbShapes.md * s)
                            Box(
                                Modifier
                                    .heightIn(min = 48.dp * s)
                                    .background(colors.surfaceTint.copy(alpha = if (colors.isDark) 0.5f else 0.6f), plateShape)
                                    .border(2.dp, colors.outline, plateShape)
                                    .padding(horizontal = 6.dp * s),
                                contentAlignment = Alignment.Center
                            ) {
                                CountryCodePickerView(
                                    initialNameCode = uiState.countryNameCode,
                                    contentColor = colors.textPrimary,
                                    listBackgroundColor = colors.bgSecondary,
                                    textSizePx = with(density) { (15f * s).sp.toPx() }.toInt(),
                                    onCountryChanged = viewModel::onCountrySelected
                                )
                            }
                            AbFormField(
                                icon = Icons.Rounded.Phone,
                                placeholder = stringResource(R.string.mobile_number),
                                text = uiState.mobile,
                                onTextChange = viewModel::onMobileChange,
                                modifier = Modifier.weight(1f),
                                keyboardType = KeyboardType.Phone,
                                error = uiState.mobileError?.let { stringResource(it) },
                                imeAction = ImeAction.Next,
                                keyboardActions = moveDown
                            )
                        }

                        // Email
                        AbFormField(
                            icon = Icons.Rounded.Email,
                            placeholder = stringResource(R.string.email_id),
                            text = uiState.email,
                            onTextChange = viewModel::onEmailChange,
                            keyboardType = KeyboardType.Email,
                            capitalization = KeyboardCapitalization.None,
                            error = uiState.emailError?.let { stringResource(it) },
                            imeAction = ImeAction.Next,
                            keyboardActions = moveDown
                        )

                        // Description (multi-line)
                        AbFormTextArea(
                            icon = Icons.Rounded.Description,
                            placeholder = stringResource(R.string.description),
                            text = uiState.description,
                            onTextChange = viewModel::onDescriptionChange,
                            error = uiState.descriptionError?.let { stringResource(it) }
                        )

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            AbButton(
                                text = stringResource(R.string.submit),
                                tone = AbTone.Primary,
                                size = AbSize.Medium,
                                icon = Icons.Rounded.Send,
                                onClick = {
                                    keyboardController?.hide()
                                    viewModel.onSubmit()
                                }
                            )
                        }
                    }
                }
            }

            if (uiState.isLoading) {
                Loader()
            }

            if (uiState.showSuccessDialog) {
                AbConfirmDialog(
                    title = stringResource(R.string.thank_you_for_reaching_out),
                    message = stringResource(R.string.thank_you_for_reaching_out1),
                    positiveText = stringResource(R.string.ok_thanks),
                    icon = Icons.Rounded.Send,
                    destructive = false,
                    accent = colors.accentGreen,
                    onPositive = { viewModel.onSuccessDialogDismissed() }
                )
            }
        }
    }

    uiState.errorMessage?.let { msg ->
        LaunchedEffect(msg) {
            context.toastL(msg)
            viewModel.consumeError()
        }
    }

    uiState.finishActivity?.consume { onFinish() }
}
