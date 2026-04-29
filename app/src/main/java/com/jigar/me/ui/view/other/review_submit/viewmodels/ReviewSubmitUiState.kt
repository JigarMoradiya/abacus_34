package com.jigar.me.ui.view.other.review_submit.viewmodels

import android.net.Uri
import com.jigar.me.ui.jetpack.core.domain.ConsumableCommand

enum class ReviewAdminStatus { NONE, PENDING, APPROVED, REJECTED }

data class ReviewSubmitUiState(
    val levelOptions: List<String> = emptyList(),
    val selectedLevelIndex: Int = 0,
    val description: String = "",
    val pickedImageUri: Uri? = null,
    val pickedImagePath: String? = null,
    val existingImageUrl: String? = null,
    val adminStatus: ReviewAdminStatus = ReviewAdminStatus.NONE,
    val adminMessage: String? = null,
    val isFormReadOnly: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val toastMessageRes: Int? = null,
    val finishActivity: ConsumableCommand<Unit>? = null,
)
