package com.jigar.me.ui.view.home.screens.abacus_practice.level_list.viewmodels

import com.jigar.me.data.model.dbtable.abacus_all_data.Category
import com.jigar.me.data.model.dbtable.abacus_all_data.DisplayPages

data class LevelCategoryUiState(
    val categories: List<Category> = emptyList(),
    val allPages: List<DisplayPages> = emptyList(),
    val progressMap: Map<String, LevelProgress> = emptyMap(),
    val error: Int? = null
)

data class LevelProgress(
    val stepCompleted: Int = 0,
    val stepTotal: Int = 0,

    val finalCompleted: Int = 0,
    val finalTotal: Int = 0,

    val examCompleted: Int = 0,
    val examTotal: Int = 0
)