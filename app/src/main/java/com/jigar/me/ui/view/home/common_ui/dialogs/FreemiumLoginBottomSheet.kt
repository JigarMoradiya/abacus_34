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
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.rounded.HowToReg
import androidx.compose.material.icons.rounded.Key
import androidx.compose.ui.text.style.TextAlign
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbHeroBadge
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbSize
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abSquash
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
import com.jigar.me.ui.view.home.common_ui.dialogs.ParentalGateDialog
import com.jigar.me.ui.view.ab.LocalAbThemed
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.login.screens.login_home.viewmodels.LoginHomeViewModel
import com.jigar.me.utils.extensions.toastL

@Composable
fun FreemiumLoginBottomSheet(
    showContinueWithoutSaving: Boolean = true,
    subtitle: String? = null,
    skipParentalGate: Boolean = false,
    onLoginSuccess: () -> Unit,
    onContinueWithoutSaving: (() -> Unit)? = null,
    onNavigateToCredentials: (() -> Unit)? = null,
    onDismiss: () -> Unit,
) {
    var parentalGatePassed by remember { mutableStateOf(skipParentalGate) }
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

    val themed = LocalAbThemed.current
    KidsBottomSheet(
        visible = true,
        onDismiss = onDismiss,
        widthFraction = if (themed) 0.8f else 0.75f,
        showHandle = !themed,
        overlay = {
            if (!parentalGatePassed) {
                ParentalGateDialog(
                    onPassed = { parentalGatePassed = true },
                    onCancelled = { onDismiss() }
                )
            }
        }
    ) {
      if (themed) {
        LoginKitContent(
            subtitle = subtitle ?: if (showContinueWithoutSaving) stringResource(R.string.login_to_save_results)
                       else stringResource(R.string.login_is_required_to_access_this_feature),
            showContinueWithoutSaving = showContinueWithoutSaving && onContinueWithoutSaving != null,
            showCredentials = onNavigateToCredentials != null,
            onGoogle = { viewModel.signInWithGoogle(context) },
            onContinueWithoutSaving = { onContinueWithoutSaving?.invoke() },
            onCredentials = { onNavigateToCredentials?.invoke() },
        )
      } else {
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
                        .shadow(elevation = 6.dp, shape = CircleShape, ambientColor = Color(0xFF6446CC), spotColor = Color(0xFF6446CC))
                        .background(Brush.linearGradient(listOf(Color(0xFF9374EF), Color(0xFF6446CC))), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(AppDimens.Dimens20)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.login_required),
                        style = MaterialTheme.typography.titleSmall.scaled().copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.font_bold)),
                            color = Color(0xFF6446CC)
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


/** The kit look of the sheet (iOS `kitContent`): a lock badge, a friendly title, the sign-in pill and quiet links. */
@Composable
private fun LoginKitContent(
    subtitle: String,
    showContinueWithoutSaving: Boolean,
    showCredentials: Boolean,
    onGoogle: () -> Unit,
    onContinueWithoutSaving: () -> Unit,
    onCredentials: () -> Unit,
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    Column(
        Modifier.fillMaxWidth().padding(bottom = 16.dp * s),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp * s),
    ) {
        Box(Modifier.padding(top = 10.dp * s).size(44.dp * s, 5.dp * s).clip(RoundedCornerShape(3.dp * s)).background(colors.outline))

        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp * s),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp * s),
        ) {
            AbHeroBadge(Icons.Rounded.HowToReg, accent = colors.accentPurple, size = 48.dp * s)
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.login_required), style = AbTypography.headline(s), color = colors.textPrimary)
                Text(
                    subtitle, style = AbTypography.fixed(13f * s, FontWeight.SemiBold), color = colors.textSecondary,
                    maxLines = 2,
                )
            }
        }

        // the pill is black in Light and white in Dark, outlined like the other kit pills
        val pillFace = if (colors.isDark) Color.White else Color.Black
        val pillInk = if (colors.isDark) Color.Black else Color.White
        Row(
            Modifier
                .padding(horizontal = 20.dp * s)
                .fillMaxWidth()
                .abSquash(face = pillFace, stroke = colors.outline, cornerRadius = AbShapes.pill, edgeHeight = 0.dp, onClick = onGoogle)
                .height(52.dp * s),
            horizontalArrangement = Arrangement.spacedBy(8.dp * s, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(painterResource(R.drawable.ic_google), null, Modifier.size(20.dp * s))
            Text(stringResource(R.string.sign_in_with_google), style = AbTypography.fixed(16f * s, FontWeight.ExtraBold), color = pillInk, maxLines = 1)
        }

        if (showContinueWithoutSaving) {
            Text(
                stringResource(R.string.continue_without_saving),
                modifier = Modifier
                    .abSquash(cornerRadius = AbShapes.sm * s, onClick = onContinueWithoutSaving)
                    .padding(horizontal = 8.dp * s, vertical = 4.dp * s),
                style = AbTypography.label(s).copy(textDecoration = TextDecoration.Underline),
                color = colors.textSecondary,
            )
        }

        if (showCredentials) {
            AbButton(
                stringResource(R.string.login_with_credentials),
                Modifier.padding(horizontal = 20.dp * s),
                tone = AbTone.Ghost, size = AbSize.Small, icon = Icons.Rounded.Key, fillWidth = true, onClick = onCredentials,
            )
            Text(
                stringResource(R.string.login_credentials_old_user_note),
                Modifier.padding(horizontal = 20.dp * s),
                style = AbTypography.fixed(12f * s, FontWeight.SemiBold), color = colors.textSecondary, textAlign = TextAlign.Center,
            )
        }
    }
}
