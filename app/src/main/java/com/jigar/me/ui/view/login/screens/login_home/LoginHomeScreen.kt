package com.jigar.me.ui.view.login.screens.login_home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.Loader
import com.jigar.me.ui.view.home.common_ui.buttons.KidsIconButton
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.KidIconMedium
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.login.components.HtmlText
import com.jigar.me.ui.view.login.screens.login_home.viewmodels.LoginHomeViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.openURL
import com.jigar.me.utils.extensions.toastL

@Composable
fun LoginHomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToFAQs: () -> Unit,
    onNavigateToContactUs: (type: String) -> Unit,
    onNavigateToHome: () -> Unit,
) {
    val viewModel: LoginHomeViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Box(modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        // Top bar with logo + FAQ icon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimens.Dimens8),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(AppDimens.Dimens45)
            )
            Spacer(modifier = Modifier.weight(1f))
            KidsIconButton(
                icon = Icons.Default.QuestionAnswer,
                onClick = onNavigateToFAQs,
                type = ButtonType.PINK,
                size = KidIconMedium
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = AppDimens.Dimens56, bottom = AppDimens.Dimens72),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(AppDimens.Dimens20),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.login),
                    style = MaterialTheme.typography.titleLarge.scaled(),
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(AppDimens.Dimens16))
                Text(
                    text = stringResource(R.string.welcome_to_abacus_child_leaning_app_portraint),
                    style = MaterialTheme.typography.bodyMedium.scaled(),
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.black_light),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(AppDimens.Dimens6))
                Text(
                    text = stringResource(R.string.login_msg),
                    style = MaterialTheme.typography.labelMedium.scaled(),
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(AppDimens.Dimens24))

                GoogleLoginButton(onClick = { viewModel.signInWithGoogle(context) })

                Spacer(modifier = Modifier.height(AppDimens.Dimens20))
                Text(
                    text = "OR",
                    style = MaterialTheme.typography.bodyLarge.scaled(),
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(AppDimens.Dimens20))

                CredentialsLoginButton(onClick = onNavigateToLogin)

                Spacer(modifier = Modifier.height(AppDimens.Dimens16))
                Text(
                    text = stringResource(R.string.i_agree_policy_login_portrait),
                    style = MaterialTheme.typography.bodyMedium.scaled(),
                    color = colorResource(id = R.color.colorPrimaryDark),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(AppDimens.Dimens16)
                        .clickable {
                            if (uiState.privacyPolicyUrl.isNotEmpty()) {
                                context.openURL(uiState.privacyPolicyUrl)
                            }
                        }
                )
                Spacer(modifier = Modifier.height(AppDimens.Dimens8))
                PoweredBy()
            }
        }

        // Bottom-aligned notes / bulk login
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
        ) {
            if (uiState.notesHtml.isNotEmpty()) {
                HtmlText(
                    html = uiState.notesHtml,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens16)
                )
            }
            if (uiState.bulkLoginHtml.isNotEmpty()) {
                HtmlText(
                    html = uiState.bulkLoginHtml,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onNavigateToContactUs(AppConstants.extras_Comman.typeBulkLogin)
                        }
                        .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens16)
                )
            }
        }
    }

    if (uiState.isLoading) {
        Loader()
    }

    uiState.errorMessage?.let { msg ->
        LaunchedEffect(msg) {
            context.toastL(msg)
            viewModel.consumeError()
        }
    }

    uiState.navigateToHome?.consume { onNavigateToHome() }
}

@Composable
private fun GoogleLoginButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(AppDimens.Dimens25))
            .background(Color.Black)
            .clickable { onClick() }
            .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens10),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = null,
            modifier = Modifier.size(AppDimens.Dimens20)
        )
        Spacer(modifier = Modifier.width(AppDimens.Dimens10))
        Text(
            text = stringResource(R.string.login_with_google).uppercase(),
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge.scaled(),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun CredentialsLoginButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(AppDimens.Dimens25))
            .background(Color.Black)
            .clickable { onClick() }
            .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens10),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.login_with_credentials).uppercase(),
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge.scaled(),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun PoweredBy() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.power_by),
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelMedium.scaled(),
        )
        Image(
            painter = painterResource(id = R.drawable.ic_logo_company_ver),
            contentDescription = null,
            modifier = Modifier.height(AppDimens.Dimens48)
        )
    }
}
