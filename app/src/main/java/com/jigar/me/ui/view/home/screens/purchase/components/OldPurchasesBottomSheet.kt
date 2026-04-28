package com.jigar.me.ui.view.home.screens.purchase.components

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import com.jigar.me.R
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import com.jigar.me.ui.view.home.screens.activities.ccm.play.viewmodels.CCMPlayUiState
import com.jigar.me.ui.view.home.screens.purchase.viewmodels.PurchaseUiState
import com.jigar.me.utils.PlaySound

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
                                topStart = 24.dp, topEnd = 24.dp, bottomStart = 0.dp, bottomEnd = 0.dp
                            )
                        )
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
                    ) {

                        Spacer(modifier = Modifier.weight(1f))

                        Row( modifier = Modifier
                            .padding(vertical = dimensionResource(R.dimen.activity_padding12))
                            .clickable { onClose() },
                            verticalAlignment = Alignment.CenterVertically)
                        {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(dimensionResource(R.dimen.activity_padding4)))
                            Text(
                                stringResource(R.string.close), style = MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily(Font(R.font.font_semibold)))
                            )
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.activity_padding4)),
                    ) {
                        itemsIndexed(uiState.oldPurchasedSkuList) { index, plan ->
                            PurchasePlanCard(
                                uiState = uiState, plan = plan, isSelected = uiState.selectedIndex == index, onClick = {})
                        }
                    }

                }

            }
        }
    }
}
