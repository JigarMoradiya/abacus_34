package com.jigar.me.ui.view.login.screens.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
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
import com.jigar.me.R
import com.jigar.me.ui.view.home.common_ui.dialogs.CustomPopupView
import com.jigar.me.ui.view.login.screens.splash.viewmodels.SplashViewModel
import com.jigar.me.utils.extensions.openURL
import com.jigar.me.utils.extensions.toastL

@Composable
fun SplashScreen(
    onNavigateToLoginHome: () -> Unit,
    onNavigateToHome: () -> Unit,
    onFinishActivity: () -> Unit,
) {
    val viewModel: SplashViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchRemoteConfigAndContinue()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier.size(150.dp)
        )
    }

    AnimatedVisibility(
        visible = uiState.showAppUpdatePopup,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        CustomPopupView(
            title = stringResource(R.string.app_update),
            description = stringResource(R.string.new_version_msg),
            positiveButtonText = stringResource(R.string.yes_i_want_to_update),
            negativeButtonText = stringResource(R.string.no_thanks),
            icon = R.drawable.ic_alert,
            widthMultiplier = 0.7f,
            onPositiveTapped = {
                viewModel.onUpdateConfirmed()
                context.openURL("https://play.google.com/store/apps/details?id=${context.packageName}")
            },
            onNegativeTapped = { viewModel.onUpdateDeclined() }
        )
    }

    uiState.errorMessage?.let { msg ->
        LaunchedEffect(msg) {
            context.toastL(msg)
            viewModel.consumeError()
        }
    }

    uiState.noInternet?.consume {
        context.toastL(context.getString(R.string.no_internet))
        onFinishActivity()
    }

    uiState.finishActivity?.consume { onFinishActivity() }
    uiState.navigateToLoginHome?.consume { onNavigateToLoginHome() }
    uiState.navigateToHome?.consume { onNavigateToHome() }
}
