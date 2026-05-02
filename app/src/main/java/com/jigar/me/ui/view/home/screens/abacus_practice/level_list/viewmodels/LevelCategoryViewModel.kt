package com.jigar.me.ui.view.home.screens.abacus_practice.level_list.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jigar.me.data.model.dbtable.abacus_all_data.Category
import com.jigar.me.data.model.dbtable.abacus_all_data.DisplayPages
import com.jigar.me.ui.jetpack.core.StatefulViewModel
import com.jigar.me.ui.jetpack.core.repository.abacus_data.AbacusDataRepository
import com.jigar.me.ui.jetpack.core.repository.abacus_data.PurchaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import com.jigar.me.data.model.dbtable.abacus_all_data.Set
import com.jigar.me.utils.AppConstants
import javax.inject.Inject

@HiltViewModel
class LevelCategoryViewModel @Inject constructor(
    private val abacusDataRepository: AbacusDataRepository,
    private val purchaseRepository: PurchaseRepository,
    savedStateHandle: SavedStateHandle
) : StatefulViewModel<LevelCategoryUiState>() {

    override val TAG = "LevelViewModel"

    override fun getInitialState() = LevelCategoryUiState()
    val levelId: String? = savedStateHandle["levelId"]

    fun loadLoad(allSets: List<Set>) {
        levelId?.let {
            load(it,allSets)
        }
    }

    fun load(levelId: String, allSets: List<Set>)  = viewModelScope.launch {
        combine(
            purchaseRepository.getPurchasedSku(),
            abacusDataRepository.getCategories(levelId),
            abacusDataRepository.getAllPages()
        ) { sku, categories,pages ->
            if (categories.isNotEmpty()) {
                val progressMap = calculateAllProgress(
                    categories = categories,
                    pages = pages,
                    sets = allSets
                )
                updateState_ {
                    copy(
                        categories = categories,
                        allPages = pages,
                        progressMap = progressMap
                    )
                }

            }
        }.catch { onFailure(it) }.collect()
    }

    private fun calculateAllProgress(
        categories: List<Category>,
        pages: List<DisplayPages>,
        sets: List<Set>
    ): Map<String, LevelProgress> {

        val progressMap = mutableMapOf<String, LevelProgress>()

        categories.forEach { category ->
            progressMap[category.id] = calculateProgress(
                categoryId = category.id,
                pages = pages,
                sets = sets
            )
        }

        return progressMap
    }

    private fun calculateProgress(
        categoryId: String,
        pages: List<DisplayPages>,
        sets: List<Set>
    ): LevelProgress {

        val pageIds = pages
            .filter { it.category_id == categoryId }
            .map { it.id }

        val categorySets = sets.filter { pageIds.contains(it.page_id) }

        var stepCompleted = 0
        var stepTotal = 0

        var finalCompleted = 0
        var finalTotal = 0

        var examCompleted = 0
        var examTotal = 0

        categorySets.forEach { set ->

            when (set.answer_setting.lowercase()) {

                AppConstants.apiParams.answerStepByStep.lowercase() -> {
                    stepTotal++
                    if (set.is_completed_set) {
                        stepCompleted++
                    }
                }

                AppConstants.apiParams.answerFinalAnswer.lowercase() -> {
                    finalTotal++
                    if (set.is_completed_set) {
                        finalCompleted++
                    }
                }

                AppConstants.apiParams.answerFormalExam.lowercase() -> {
                    examTotal++
                    if (set.is_completed_set) {
                        examCompleted++
                    }
                }
            }
        }

        return LevelProgress(
            stepCompleted = stepCompleted,
            stepTotal = stepTotal,
            finalCompleted = finalCompleted,
            finalTotal = finalTotal,
            examCompleted = examCompleted,
            examTotal = examTotal
        )
    }

    override fun onFailure(throwable: Throwable) {
        updateState_ {
            copy(error = localizeCommonFailure(throwable))
        }
    }
}