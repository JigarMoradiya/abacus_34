package com.jigar.me.ui.view.home.screens.my_account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.fragments.common.BackButtonWithText
import com.jigar.me.ui.view.jetpack.fragments.other.faqs.components.FAQsListItem
import com.jigar.me.ui.view.jetpack.fragments.other.faqs.viewmodels.FAQsViewModel

@Composable
fun FAQsRoute(
    onBackClick: () -> Unit,
) {
    val viewModel: FAQsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Row {
            if (!uiState.isUserLoggedIn) {
                Spacer(Modifier.width(dimensionResource(R.dimen.activity_padding12)))
            }
            BackButtonWithText(
                title = stringResource(R.string.faqs),
                onBackClick = onBackClick
            )
        }
        FAQsListItem(uiState.faqsList)
    }
}
