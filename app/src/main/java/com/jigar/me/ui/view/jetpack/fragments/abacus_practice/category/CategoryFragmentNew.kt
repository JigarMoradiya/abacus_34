package com.jigar.me.ui.view.jetpack.fragments.abacus_practice.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.fragments.abacus_practice.category.components.CategoryItem
import com.jigar.me.ui.view.jetpack.fragments.abacus_practice.category.components.PageItem
import com.jigar.me.ui.view.jetpack.fragments.abacus_practice.category.components.TopRightChips
import com.jigar.me.ui.view.jetpack.fragments.abacus_practice.category.viewmodels.CategoryViewModel
import com.jigar.me.ui.view.jetpack.fragments.common.BackButtonWithText
import com.jigar.me.ui.view.jetpack.fragments.home.viewmodels.HomeActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryFragmentNew : Fragment() {
    private val viewModel: CategoryViewModel by viewModels()
    private val dashboardViewModel: HomeActivityViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val allSets by dashboardViewModel.allSets.collectAsStateWithLifecycle()

                MaterialTheme {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                        BackButtonWithText(title = stringResource(R.string.practice_of_abacus), onBackClick = {findNavController().popBackStack()})
                            Spacer(modifier = Modifier.weight(1f))
                            TopRightChips()
                        }
                        Spacer(Modifier.weight(1f))

                        Row(modifier = Modifier.fillMaxSize()) {

                            // LEFT – Category list
                            LazyColumn(
                                modifier = Modifier.width(110.dp)
                            ) {
                                itemsIndexed(uiState.categories) { index, category ->
                                    CategoryItem(
                                        category = category,
                                        isSelected = index == uiState.selectedCategoryIndex
                                    ) {
                                        viewModel.selectCategory(index)
                                    }
                                }
                            }

                            // RIGHT – Pages
                            if (uiState.showNoData) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.ic_coming_soon),
                                        contentDescription = null
                                    )
                                }
                            } else {
                                val pageGridState = rememberLazyGridState()
                                var lastCategoryIndex by rememberSaveable { mutableIntStateOf(uiState.selectedCategoryIndex) }

                                LaunchedEffect(uiState.selectedCategoryIndex) {
                                    if (lastCategoryIndex != uiState.selectedCategoryIndex) {
                                        pageGridState.scrollToItem(0)
                                        lastCategoryIndex = uiState.selectedCategoryIndex
                                    }
                                }


                                LazyVerticalGrid(
                                    state = pageGridState,
                                    columns = GridCells.Fixed(2),
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(8.dp)
                                ) {
                                    items(uiState.pages) { page ->
                                        PageItem(
                                            page = page,
                                            allSets = allSets,
                                            onSetClick = { set ->
                                                findNavController().navigate(CategoryFragmentNewDirections.toAbacusDoPracticeFragment(set.id))
//                                                findNavController().navigate(CategoryFragmentNewDirections.toAbacusCalculationFragment(set.id))
                                            },
                                            onSetLongClick = {}
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
