package com.jigar.me.ui.view.home.screens.purchase.components

import com.jigar.me.ui.view.home.theme.AppDimens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens2
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.PurchaseUiState
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled

@Composable
fun PurchaseScreen(
    uiState: PurchaseUiState,
    onPlanSelected: (Int) -> Unit,
    onShowOldSubClick: () -> Unit,
    oldSubPopupCloseClick: () -> Unit,
    onSubscribe: () -> Unit,
    onClose: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        Row(modifier = Modifier.fillMaxSize()) {

            // LEFT SIDE (info)
            InfoSection(
                modifier = Modifier.weight(1.25f),
                infoList = uiState.benefitList,stringResource(R.string.go_premium_all_levels_all_features)
            )

            // RIGHT SIDE (plans)
            PurchasePlanSection(
                modifier = Modifier.weight(1f),
                uiState = uiState,
                onPlanSelected = onPlanSelected,
                onSubscribe = onSubscribe
            )
        }

        if (uiState.isOldSubscriptionThere){
            Surface(
                onClick = {onShowOldSubClick()},
                shape = RoundedCornerShape(Dimens4),
                color = colorResource(R.color.green_500),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = Dimens12),
                tonalElevation = 0.dp,
                shadowElevation = 0.dp
            ) {
                Text(
                    text = stringResource(R.string.old_purchases),
                    style = MaterialTheme.typography.labelMedium.scaled().copy(color = Color.White, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.font_bold))),
                    modifier = Modifier.padding(
                        horizontal = Dimens8,
                        vertical = Dimens2
                    )
                )
            }
        }

        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(Dimens16)
                .clickable {
                    AudioPlayerManager.playSoundBtnBack()
                    onClose()
                }
        )
    }

    // set Old Subscription popup
    AnimatedVisibility(
        visible = uiState.showOldSubscriptionPopup,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        OldPurchasesBottomSheet(
            uiState = uiState,onClose = {
                oldSubPopupCloseClick()
            }
        )
    }
}
