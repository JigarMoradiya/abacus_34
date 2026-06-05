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
                    pageTitle = title,
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

    val title : String = when (name) {
        "level1" -> {
            "Pages of Level 1"
        }
        "level2" -> {
            "Pages of Level 2"
        }
        "level3" -> {
            "Pages of Level 3"
        }
        "level4" -> {
            "Pages of Level 4"
        }
        "level5" -> {
            "Pages of Level 5"
        }
        "level6" -> {
            "Pages of Level 6"
        }
        "level7" -> {
            "Pages of Level 7"
        }
        "level8" -> {
            "Pages of Level 8"
        }
        "level9" -> {
            "Pages of Level 9"
        }
        "level10" -> {
            "Pages of Level 10"
        }
        else -> {
            "Pages of Level"
        }
    }
}