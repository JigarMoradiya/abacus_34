package com.jigar.me.ui.view.jetpack.fragments.abacus_practice.category.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jigar.me.ui.view.jetpack.core.StatefulViewModel
import com.jigar.me.ui.view.jetpack.fragments.abacus_practice.category.CategoryRepository
import com.jigar.me.ui.view.jetpack.fragments.abacus_practice.category.viewmodels.CategoryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: CategoryRepository,
    savedStateHandle: SavedStateHandle
) : StatefulViewModel<CategoryUiState>() {

    override val TAG = "CategoryViewModel"

    override fun getInitialState() = CategoryUiState()
    val levelId: String? = savedStateHandle["levelId"]
    init {
        levelId?.let {
            load(it)
        }
    }

    fun load(levelId: String) {
        viewModelScope.launch {
            combine(
                repository.getPurchasedSku(),
                repository.getCategories(levelId)
            ) { sku, categories ->
                if (categories.isNotEmpty()) {
                    updateState_ {
                        copy(
                            purchasedSku = sku,
                            categories = categories,
                            selectedCategoryIndex = 0
                        )
                    }
                    loadPages(categories.first().id)
                }
            }.catch { onFailure(it) }.collect()
        }
    }

    fun selectCategory(index: Int) {
        updateState_ { copy(selectedCategoryIndex = index) }
        loadPages(state().categories[index].id)
    }

    private fun loadPages(categoryId: String) {
        viewModelScope.launch {
            repository.getPages(categoryId)
                .catch { onFailure(it) }
                .collect { pages ->
                    updateState_ {
                        copy(
                            pages = pages,
                            showNoData = pages.isEmpty(),
                            isLoading = false
                        )
                    }
                }
        }
    }

    override fun onFailure(throwable: Throwable) {
        updateState_ {
            copy(error = localizeCommonFailure(throwable))
        }
    }
}