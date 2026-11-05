package com.jigar.me.ui.view.login.screens.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WifiOff
import com.jigar.me.R
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbBeadsLoader
import com.jigar.me.ui.view.ab.AbColors
import com.jigar.me.ui.view.ab.AbConfirmDialog
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.AbThemeMode
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.common_ui.dialogs.CustomPopupView
import com.jigar.me.ui.view.home.common_ui.dialogs.PopupTheme
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.login.screens.splash.viewmodels.SplashViewModel
import com.jigar.me.utils.extensions.openURL

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onFinishActivity: () -> Unit,
) {
    val viewModel: SplashViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchRemoteConfigAndContinue()
    }

    AbTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AbBackground(AbWorld.Meadow, Modifier.fillMaxSize())
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens16)) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier.size(150.dp)
                )
                AbBeadsLoader()
            }
        }
    }

    // App update popup
    AnimatedVisibility(visible = uiState.showAppUpdatePopup, enter = fadeIn(), exit = fadeOut()) {
        CustomPopupView(
            title = stringResource(R.string.app_update),
            description = stringResource(R.string.new_version_msg),
            positiveButtonText = stringResource(R.string.yes_i_want_to_update),
            negativeButtonText = stringResource(R.string.no_thanks),
            icon = R.drawable.ic_alert,
            theme = PopupTheme.CONFIRM,
            accent = ButtonType.BLUE,
            widthMultiplier = 0.7f,
            onPositiveTapped = {
                viewModel.onUpdateConfirmed()
                context.openURL("https://play.google.com/store/apps/details?id=${context.packageName}")
            },
            onNegativeTapped = { viewModel.onUpdateDeclined() }
        )
    }

    // No internet popup / API error popup (the latter only when no local data exists). They appear before the user has
    // chosen anything, so they use the Light kit palette.
    if (uiState.showNoInternetPopup || uiState.showErrorPopup) {
        AbTheme(forcing = AbThemeMode.Light) {
            AbConfirmDialog(
                title = stringResource(if (uiState.showNoInternetPopup) R.string.no_internet_working else R.string.alert),
                message = stringResource(if (uiState.showNoInternetPopup) R.string.no_internet else R.string.something_went_wrong),
                positiveText = stringResource(R.string.retry),
                negativeText = stringResource(R.string.cancel),
                icon = Icons.Rounded.WifiOff,
                destructive = false,
                accent = AbColors.Light.accentOrange,
                onPositive = { viewModel.retryAfterError() },
                onNegative = { onFinishActivity() },
            )
        }
    }

    uiState.finishActivity?.consume { onFinishActivity() }
    uiState.navigateToLoginHome?.consume { onNavigateToHome() }
    uiState.navigateToHome?.consume { onNavigateToHome() }
}
