package com.jigar.me.ui.view.other.contactus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.view.home.common_ui.Loader
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.common_ui.dialogs.CustomPopupView
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.ButtonType
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

    Column(modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        // Top bar: back card + logo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimens.Dimens8),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton(onClick = { viewModel.onBack() })
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(AppDimens.Dimens45)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(vertical = AppDimens.Dimens20),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.contact_us),
                fontSize = 20.sp,
                color = Color.Black,
            )

            Spacer(modifier = Modifier.height(AppDimens.Dimens6))
            Text(
                text = stringResource(R.string.contact_us_fill_detail),
                fontSize = 13.sp,
                color = Color.Black,
            )

            Spacer(modifier = Modifier.height(AppDimens.Dimens24))

            // Name
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                placeholder = { Text(stringResource(R.string.your_name)) },
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                singleLine = true,
                isError = uiState.nameError != null,
                supportingText = {
                    uiState.nameError?.let {
                        Text(stringResource(it), color = MaterialTheme.colorScheme.error)
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppDimens.Dimens10)
            )

            // Country code + Mobile
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppDimens.Dimens10, vertical = AppDimens.Dimens8),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CountryCodePickerView(
                    initialNameCode = uiState.countryNameCode,
                    onCountryChanged = viewModel::onCountrySelected,
                    modifier = Modifier.height(AppDimens.Dimens56)
                )
                Spacer(modifier = Modifier.width(AppDimens.Dimens8))
                OutlinedTextField(
                    value = uiState.mobile,
                    onValueChange = viewModel::onMobileChange,
                    placeholder = { Text(stringResource(R.string.mobile_number)) },
                    leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null) },
                    singleLine = true,
                    isError = uiState.mobileError != null,
                    supportingText = {
                        uiState.mobileError?.let {
                            Text(stringResource(it), color = MaterialTheme.colorScheme.error)
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            // Email
            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                placeholder = { Text(stringResource(R.string.email_id)) },
                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                singleLine = true,
                isError = uiState.emailError != null,
                supportingText = {
                    uiState.emailError?.let {
                        Text(stringResource(it), color = MaterialTheme.colorScheme.error)
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppDimens.Dimens10)
            )

            // Description (multi-line)
            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChange,
                placeholder = { Text(stringResource(R.string.description)) },
                leadingIcon = { Icon(Icons.Filled.Description, contentDescription = null) },
                isError = uiState.descriptionError != null,
                supportingText = {
                    uiState.descriptionError?.let {
                        Text(stringResource(it), color = MaterialTheme.colorScheme.error)
                    }
                },
                maxLines = 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppDimens.Dimens10, vertical = AppDimens.Dimens8)
                    .height(AppDimens.Dimens150)
            )

            Spacer(modifier = Modifier.height(AppDimens.Dimens20))

            KidsActionButton(
                modifier = Modifier.padding(horizontal = AppDimens.Dimens10),
                text = stringResource(R.string.submit),
                icon = Icons.Default.Update,
                type = ButtonType.ORANGE,
                onClick = {
                    keyboardController?.hide()
                    viewModel.onSubmit()
                }
            )

            Spacer(modifier = Modifier.height(Dimens12))
        }
    }

    if (uiState.isLoading) {
        Loader()
    }

    AnimatedVisibility(
        visible = uiState.showSuccessDialog,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        CustomPopupView(
            title = stringResource(R.string.thank_you_for_reaching_out),
            positiveButtonText = stringResource(R.string.ok_thanks),
            widthMultiplier = 0.7f,
            onPositiveTapped = { viewModel.onSuccessDialogDismissed() },
        )
    }

    uiState.errorMessage?.let { msg ->
        LaunchedEffect(msg) {
            context.toastL(msg)
            viewModel.consumeError()
        }
    }

    uiState.finishActivity?.consume { onFinish() }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(AppDimens.Dimens45)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = null,
                tint = Color.Black
            )
        }
    }
}
