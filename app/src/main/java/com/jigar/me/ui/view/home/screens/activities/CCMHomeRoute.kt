package com.jigar.me.ui.view.home.screens.activities

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.view.home.screens.activities.ccm.home.components.CCMHomeScreen
import com.jigar.me.ui.view.home.screens.activities.ccm.home.viewmodels.CCMHomeViewModel
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel
import com.jigar.me.utils.extensions.toastS

@Composable
fun CCMHomeRoute(
    homeActivityViewModel: HomeActivityViewModel,
    onBackClick: () -> Unit,
    onStartPlay: () -> Unit,
    onPurchase: () -> Unit,
) {
    val viewModel: CCMHomeViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val purchasedSKU by homeActivityViewModel.purchasedSku.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val pleaseSelectMsg = stringResource(R.string.please_select_at_least_one_checkbox)

    Column(modifier = Modifier.fillMaxSize()) {
        BackButtonWithText(
            title = stringResource(R.string.custom_challenge_mode),
            onBackClick = onBackClick
        )
        CCMHomeScreen(uiState, viewModel) {
            if (homeActivityViewModel.isPurchasedForModule(purchasedSKU)) {
                if (!uiState.isQuestionSpeak && !uiState.isQuestionShowWord && !uiState.isQuestionShowNumber) {
                    context.toastS(pleaseSelectMsg)
                } else {
                    onStartPlay()
                }
            } else {
                onPurchase()
            }
        }
    }
}
