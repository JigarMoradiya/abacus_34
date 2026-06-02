package com.jigar.me.ui.view.home.common_ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorPrimary
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.Loader
import com.jigar.me.ui.view.home.common_ui.sheets.KidsBottomSheet
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.login.screens.login.LoginScreen
import com.jigar.me.ui.view.login.screens.login_home.viewmodels.LoginHomeViewModel
import com.jigar.me.utils.extensions.toastL

@Composable
fun FreemiumLoginBottomSheet(
    showContinueWithoutSaving: Boolean = true,
    subtitle: String? = null,
    onLoginSuccess: () -> Unit,
    onContinueWithoutSaving: (() -> Unit)? = null,
    onNavigateToCredentials: (() -> Unit)? = null,
    onDismiss: () -> Unit,
) {
    val viewModel: LoginHomeViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    uiState.navigateToHome?.consume {
        onLoginSuccess()
    }

    uiState.errorMessage?.let { msg ->
        LaunchedEffect(msg) {
            context.toastL(msg)
            viewModel.consumeError()
        }
    }

    KidsBottomSheet(
        visible = true,
        onDismiss = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = AppDimens.Dimens16),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppDimens.Dimens20),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)
            ) {
                Box(
                    modifier = Modifier
                        .size(AppDimens.Dimens40)
                        .background(ColorPrimary.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = ColorPrimary,
                        modifier = Modifier.size(AppDimens.Dimens20)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.login_required),
                        style = MaterialTheme.typography.titleSmall.scaled().copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.font_bold)),
                            color = ColorPrimary
                        )
                    )
                    Text(
                        text = subtitle ?: if (showContinueWithoutSaving) stringResource(R.string.login_to_save_results)
                               else stringResource(R.string.login_is_required_to_access_this_feature),
                        style = MaterialTheme.typography.labelMedium.scaled().copy(
                            fontFamily = FontFamily(Font(R.font.font_regular)),
                            color = Color.Black.copy(alpha = 0.6f)
                        ),
                        maxLines = 2
                    )
                }
            }

            Spacer(Modifier.size(AppDimens.Dimens12))

            LoginPillButtonImage(
                text = stringResource(R.string.sign_in_with_google),
                iconRes = R.drawable.ic_google,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppDimens.Dimens20),
                onClick = { viewModel.signInWithGoogle(context) }
            )

            if (showContinueWithoutSaving && onContinueWithoutSaving != null) {
                Spacer(Modifier.size(AppDimens.Dimens8))
                Text(
                    text = stringResource(R.string.continue_without_saving),
                    style = MaterialTheme.typography.labelMedium.scaled().copy(
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily(Font(R.font.font_medium)),
                        color = Color.Gray,
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier
                        .clickable { onContinueWithoutSaving() }
                        .padding(AppDimens.Dimens8)
                )
            }

            if (onNavigateToCredentials != null) {
                Spacer(Modifier.size(AppDimens.Dimens8))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppDimens.Dimens20)
                        .clip(RoundedCornerShape(100))
                        .background(Color(0xFF1C1C1E))
                        .clickable { onNavigateToCredentials() }
                        .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens10),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Login,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(AppDimens.Dimens16)
                    )
                    Spacer(Modifier.size(AppDimens.Dimens6))
                    Text(
                        text = stringResource(R.string.login_with_credentials),
                        style = MaterialTheme.typography.labelSmall.scaled().copy(
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily(Font(R.font.font_semibold)),
                            color = Color.White
                        )
                    )
                }
                Spacer(Modifier.size(AppDimens.Dimens4))
                Text(
                    text = stringResource(R.string.login_credentials_old_user_note),
                    style = MaterialTheme.typography.labelSmall.scaled().copy(
                        fontFamily = FontFamily(Font(R.font.font_regular)),
                        color = Color.Gray
                    ),
                    modifier = Modifier.padding(horizontal = AppDimens.Dimens20)
                )
            }
        }
    }

    if (uiState.isLoading) {
        Loader()
    }
}

@Composable
private fun LoginPillButtonImage(
    text: String,
    iconRes: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(100))
            .background(Color(0xFF1C1C1E))
            .clickable { onClick() }
            .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens10),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(AppDimens.Dimens16)
        )
        Spacer(Modifier.size(AppDimens.Dimens6))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.scaled().copy(
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily(Font(R.font.font_semibold)),
                color = Color.White
            )
        )
    }
}

