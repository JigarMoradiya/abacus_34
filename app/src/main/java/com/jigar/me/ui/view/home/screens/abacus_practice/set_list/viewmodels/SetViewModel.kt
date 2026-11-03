package com.jigar.me.ui.view.home.screens.abacus_practice.set_list.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jigar.me.ui.jetpack.core.StatefulViewModel
import com.jigar.me.ui.jetpack.core.repository.abacus_data.AbacusDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetViewModel @Inject constructor(
    private val abacusDataRepository: AbacusDataRepository,
    savedStateHandle: SavedStateHandle
) : StatefulViewModel<SetUiState>() {

    override val TAG = "SetViewModel"

    override fun getInitialState() = SetUiState()
    val levelCategoryId: String? = savedStateHandle["levelCategoryId"]
    val name: String? = savedStateHandle["name"]

    // Declared BEFORE init: init starts load(), and when the database answers at once the result is delivered inside the constructor,
    // before a property declared further down has its value (title was null then: "Parameter specified as non-null is null: pageTitle").
    /** 1..10 for "level1".."level10", else null (the title then has no number); the words are read when drawn. */
    val pageLevel: Int? = name?.removePrefix("level")?.toIntOrNull()?.takeIf { name.startsWith("level") && it in 1..10 }

    init {
        levelCategoryId?.let {
            load(it)
        }
    }

    fun load(levelCategoryId: String)  = viewModelScope.launch {
        abacusDataRepository.getPages(levelCategoryId).catch { onFailure(it) }.collect { pages ->
            updateState_ {
                copy(
                    levelName = name?:"",
                    pageLevel = pageLevel,
                    pages = pages,
                    showNoData = pages.isEmpty(),
                    isLoading = false
                )
            }
        }
    }


    override fun onFailure(throwable: Throwable) {
        updateState_ {
            copy(error = localizeCommonFailure(throwable))
        }
    }
}
