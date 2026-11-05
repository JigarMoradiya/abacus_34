package com.jigar.me.ui.view.login.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbFormField
import com.jigar.me.ui.view.ab.AbScale
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbSize
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbTopBar
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abSquash
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.view.home.common_ui.Loader
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.login.screens.login.viewmodels.LoginViewModel
import com.jigar.me.utils.extensions.toastL

@Composable
fun LoginWithCredentialsLandscapeScreen(
    onGoBack: () -> Unit,
    onLoginSuccess: () -> Unit,
) {
    val viewModel: LoginViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var passwordVisible by remember { mutableStateOf(false) }

    uiState.navigateToHome?.consume { onLoginSuccess() }

    uiState.errorMessage?.let { msg ->
        LaunchedEffect(msg) {
            context.toastL(msg)
            viewModel.consumeError()
        }
    }

    AbTheme {
        val colors = LocalAbColors.current
        val base = LocalAbScale.current
        // The form is the whole point of this screen, so on a tablet it is drawn larger than the kit's usual tablet size.
        val scale = AbScale(base.factor * (if (base.isTablet) 1.35f else 1f), base.isTablet)
        val s = scale.factor
        Box(Modifier.fillMaxSize()) {
            AbBackground(AbWorld.Lagoon, Modifier.fillMaxSize())
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                AbTopBar(
                    title = stringResource(R.string.login_with_credentials),
                    onBack = onGoBack,
                    modifier = Modifier.padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8),
                )

                Row(modifier = Modifier.fillMaxSize()) {

                    // LEFT: branding
                    Column(
                        modifier = Modifier
                            .weight(0.38f)
                            .fillMaxHeight()
                            .padding(horizontal = AppDimens.Dimens20),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12, Alignment.CenterVertically)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = null,
                            modifier = Modifier.size(AppDimens.LoginLogo * (if (base.isTablet) 1.7f else 1f))
                        )
                        // the title and its line below sit close: Baloo's line box leaves a wide empty band between two lines
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy((-5).dp * s)) {
                            Text(
                                text = stringResource(R.string.login_with_credentials),
                                style = AbTypography.headline(s),
                                color = colors.brandPrimary,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = stringResource(R.string.login_credentials_old_user_note),
                                style = AbTypography.fixed(13f * s, FontWeight.SemiBold),
                                color = colors.textSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Box(Modifier.fillMaxHeight().padding(vertical = AppDimens.Dimens20).width(2.dp).background(colors.outline))

                    // RIGHT: form, centred
                    Column(
                        modifier = Modifier
                            .weight(0.62f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CompositionLocalProvider(LocalAbScale provides scale) {
                            Column(
                                Modifier.padding(horizontal = AppDimens.Dimens20).widthIn(max = 480.dp * s).fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
                            ) {
                                AbFormField(
                                    icon = Icons.Rounded.Email,
                                    placeholder = stringResource(R.string.email_id),
                                    text = uiState.email,
                                    onTextChange = viewModel::onEmailChange,
                                    keyboardType = KeyboardType.Email,
                                    capitalization = KeyboardCapitalization.None,
                                    error = uiState.emailError?.let { stringResource(id = it) },
                                    imeAction = ImeAction.Next,
                                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                                )
                                Box {
                                    AbFormField(
                                        icon = Icons.Rounded.Key,
                                        placeholder = stringResource(R.string.password),
                                        text = uiState.password,
                                        onTextChange = viewModel::onPasswordChange,
                                        capitalization = KeyboardCapitalization.None,
                                        isSecure = !passwordVisible,
                                        error = uiState.passwordError?.let { stringResource(id = it) },
                                        imeAction = ImeAction.Done,
                                        keyboardActions = KeyboardActions(onDone = {
                                            keyboardController?.hide()
                                            viewModel.onSubmit()
                                        }),
                                    )
                                    // sits over the field, above its error line
                                    Box(
                                        Modifier
                                            .align(Alignment.TopEnd)
                                            .size(44.dp * s, 48.dp * s)
                                            .abSquash(cornerRadius = AbShapes.sm * s, onClick = { passwordVisible = !passwordVisible }),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(
                                            if (passwordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility, null,
                                            Modifier.size(20.dp * s), tint = colors.textSecondary,
                                        )
                                    }
                                }
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                    AbButton(
                                        stringResource(R.string.submit), tone = AbTone.Primary, size = AbSize.Medium,
                                        icon = Icons.AutoMirrored.Rounded.ArrowForward, iconAtEnd = true,
                                        onClick = {
                                            keyboardController?.hide()
                                            viewModel.onSubmit()
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (uiState.isLoading) {
        Loader()
    }
}