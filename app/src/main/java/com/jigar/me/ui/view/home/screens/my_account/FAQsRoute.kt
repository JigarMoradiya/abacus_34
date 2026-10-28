package com.jigar.me.ui.view.home.screens.my_account

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.AbTopBar
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.home.screens.faqs.components.FAQsListItem
import com.jigar.me.ui.view.home.screens.faqs.viewmodels.FAQsViewModel
import com.jigar.me.ui.view.home.theme.AppDimens

@Composable
fun FAQsRoute(
    onBackClick: () -> Unit,
) {
    val viewModel: FAQsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AbTheme {
        Box(Modifier.fillMaxSize()) {
            AbBackground(AbWorld.Lagoon, Modifier.fillMaxSize())

            Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
                AbTopBar(
                    title = stringResource(R.string.faqs),
                    onBack = onBackClick,
                    modifier = Modifier.padding(
                        start = DeviceInfo.screenHorizontalPadding(),
                        end = AppDimens.Dimens16,
                        top = DeviceInfo.screenTopPadding()
                    )
                )
                FAQsListItem(uiState.faqsList)
            }
        }
    }
}
