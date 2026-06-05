package com.jigar.me.ui.view.home.screens.purchase.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.PurchaseUiState
import com.jigar.me.ui.view.home.theme.AppDimens

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OldPurchasesBottomSheet(
    uiState: PurchaseUiState, onClose: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val popupWidth = (configuration.screenWidthDp * 0.6f).dp
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onClose, sheetState = sheetState, sheetGesturesEnabled = false, properties = ModalBottomSheetProperties(shouldDismissOnClickOutside = false), containerColor = Color.Transparent, scrimColor = Color.Black.copy(alpha = 0.5f), dragHandle = null
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.width(popupWidth)
            ) {
                // This is the ACTUAL popup
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color.White, shape = RoundedCornerShape(
                                topStart = AppDimens.Dimens24, topEnd = AppDimens.Dimens24, bottomStart = 0.dp, bottomEnd = 0.dp
                            )
                        )
                        .padding(horizontal = AppDimens.Dimens16)
                        .padding(bottom = AppDimens.Dimens16), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
                    ) {

                        Spacer(modifier = Modifier.weight(1f))

                        Row( modifier = Modifier
                            .padding(vertical = AppDimens.Dimens12)
                            .clickable { onClose() },
                            verticalAlignment = Alignment.CenterVertically)
                        {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(AppDimens.Dimens20))
                            Spacer(Modifier.width(AppDimens.Dimens4))
                            Text(
                                stringResource(R.string.close), style = MaterialTheme.typography.bodyMedium.scaled().copy(color = Color.DarkGray, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily(Font(R.font.font_semibold)))
                            )
                        }
                    }

                    // Old subscription plans managed by RevenueCat

                }

            }
        }
    }
}
