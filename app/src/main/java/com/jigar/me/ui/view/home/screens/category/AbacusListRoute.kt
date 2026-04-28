package com.jigar.me.ui.view.home.screens.category

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.view.home.screens.category.abacus_list.components.AbacusListItem
import com.jigar.me.ui.view.home.screens.category.abacus_list.viewmodels.AbacusListViewModel
import com.jigar.me.ui.view.home.common.BackButtonWithText

@Composable
fun AbacusListRoute(
    onBackClick: () -> Unit,
) {
    val viewModel: AbacusListViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        BackButtonWithText(
            title = stringResource(R.string.list_of_abacus),
            onBackClick = onBackClick
        )
        AbacusListItem(uiState.abacus)
    }
}
