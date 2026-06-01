package com.jigar.me.ui.view.login.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorCoffee
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorPrimary
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.Loader
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.ButtonType
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        BackButtonWithText(
            title = stringResource(R.string.login_with_credentials),
            onBackClick = onGoBack
        )

        Row(modifier = Modifier.fillMaxSize()) {

            // LEFT — branding panel
            Column(
                modifier = Modifier
                    .weight(0.38f)
                    .fillMaxHeight()
                    .padding(horizontal = AppDimens.Dimens24, vertical = Dimens16),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier.size(AppDimens.Dimens56)
                )
                Spacer(modifier = Modifier.height(Dimens16))
                Text(
                    text = stringResource(R.string.login_with_credentials),
                    style = MaterialTheme.typography.titleMedium.scaled().copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily(Font(R.font.font_bold)),
                        color = ColorPrimary
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(AppDimens.Dimens8))
                Text(
                    text = stringResource(R.string.login_credentials_old_user_note),
                    style = MaterialTheme.typography.labelSmall.scaled().copy(
                        fontFamily = FontFamily(Font(R.font.font_regular)),
                        color = ColorCoffee
                    ),
                    textAlign = TextAlign.Center
                )
            }

            VerticalDivider(modifier = Modifier.fillMaxHeight().padding(vertical = Dimens16))

            // RIGHT — form
            Column(
                modifier = Modifier
                    .weight(0.62f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = AppDimens.Dimens24, vertical = Dimens16),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChange,
                    label = { Text(stringResource(R.string.email_id)) },
                    leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                    singleLine = true,
                    isError = uiState.emailError != null,
                    supportingText = {
                        uiState.emailError?.let {
                            Text(stringResource(id = it), color = MaterialTheme.colorScheme.error)
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(AppDimens.Dimens4))

                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = viewModel::onPasswordChange,
                    label = { Text(stringResource(R.string.password)) },
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    singleLine = true,
                    isError = uiState.passwordError != null,
                    supportingText = {
                        uiState.passwordError?.let {
                            Text(stringResource(id = it), color = MaterialTheme.colorScheme.error)
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            viewModel.onSubmit()
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(AppDimens.Dimens16))

                KidsActionButton(
                    text = stringResource(R.string.submit),
                    type = ButtonType.BLUE,
                    onClick = {
                        keyboardController?.hide()
                        viewModel.onSubmit()
                    }
                )
            }
        }
    }

    if (uiState.isLoading) {
        Loader()
    }
}