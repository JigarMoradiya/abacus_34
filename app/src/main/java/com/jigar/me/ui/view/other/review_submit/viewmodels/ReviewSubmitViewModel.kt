package com.jigar.me.ui.view.other.review_submit.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jigar.me.R
import com.jigar.me.data.model.data.ReviewData
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.data.repositories.StudentApiRepository
import com.jigar.me.ui.jetpack.core.StatefulViewModel
import com.jigar.me.ui.jetpack.core.domain.ConsumableCommand
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Month3_Level1
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Month3_Level2
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Month3_Level3
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Month3_Level4
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Month3_Level5
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Constants
import com.jigar.me.utils.FileUtils
import com.jigar.me.utils.FileUtils.getPath
import com.jigar.me.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ReviewSubmitViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: AppPreferencesHelper,
    private val apiRepository: StudentApiRepository,
) : StatefulViewModel<ReviewSubmitUiState>() {

    override val TAG = "ReviewSubmitViewModel"

    private val planArray = listOf(
        "",
        PRODUCT_ID_Subscription_Month3_Level1,
        PRODUCT_ID_Subscription_Month3_Level2,
        PRODUCT_ID_Subscription_Month3_Level3,
        PRODUCT_ID_Subscription_Month3_Level4,
        PRODUCT_ID_Subscription_Month3_Level5,
    )

    private var existingReview: ReviewData? = null
    private var pickedFile: File? = null

    override fun getInitialState() = ReviewSubmitUiState()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        val levelOptions = listOf(
            context.getString(R.string.select_level__),
            context.getString(R.string.level_1),
            context.getString(R.string.level_2),
            context.getString(R.string.level_3),
            context.getString(R.string.level_4),
            context.getString(R.string.level_5),
        )
        updateState_ { copy(levelOptions = levelOptions) }

        val raw = prefs.getCustomParam(Constants.APP_REVIEW_DATA, "")
        if (raw.isEmpty()) return
        val list: List<ReviewData> = runCatching {
            Gson().fromJson<List<ReviewData>>(raw, object : TypeToken<List<ReviewData>>() {}.type)
        }.getOrNull().orEmpty()
        val first = list.firstOrNull() ?: return
        existingReview = first
        applyExistingReview(first, levelOptions)
    }

    private fun applyExistingReview(review: ReviewData, levelOptions: List<String>) {
        // Match the existing plan_id back to a level row
        val matchedIndex = levelOptions.indexOfFirst { label ->
            label.isNotEmpty() && review.plan_id?.contains(label.replace(" ", ""), true) == true
        }.takeIf { it > 0 } ?: 0

        val status = when {
            review.status.equals(Constants.APP_REVIEW_STATUS_REJECT, true) -> ReviewAdminStatus.REJECTED
            review.status.equals(Constants.APP_REVIEW_STATUS_APPROVE, true) -> ReviewAdminStatus.APPROVED
            else -> ReviewAdminStatus.PENDING
        }

        updateState_ {
            copy(
                selectedLevelIndex = matchedIndex,
                description = review.description.orEmpty(),
                existingImageUrl = review.image_1,
                adminStatus = status,
                adminMessage = if (status == ReviewAdminStatus.REJECTED) review.comment else null,
                isFormReadOnly = status == ReviewAdminStatus.APPROVED || status == ReviewAdminStatus.PENDING,
            )
        }
    }

    fun onLevelSelected(index: Int) {
        updateState_ { copy(selectedLevelIndex = index) }
    }

    fun onDescriptionChange(value: String) {
        updateState_ { copy(description = value) }
    }

    fun onImagePicked(uri: Uri) {
        val path = with(context) { getPath(uri) } ?: return
        val file = File(path)
        pickedFile = file
        updateState_ {
            copy(
                pickedImageUri = uri,
                pickedImagePath = path,
                existingImageUrl = null,
            )
        }
    }

    fun onSubmit() {
        val current = state()
        when {
            current.selectedLevelIndex == 0 -> {
                showToast(R.string.please_select_level)
                return
            }
            pickedFile == null -> {
                showToast(R.string.please_select_screenshot_of_application_review)
                return
            }
        }

        val file = pickedFile ?: return
        val multipartType = "multipart/form-data".toMediaTypeOrNull()
        val planId = planArray[current.selectedLevelIndex].toRequestBody(multipartType)
        val description = current.description.toRequestBody(multipartType)
        val mimeType = FileUtils.getMimeType(file)
        val fileBody = file.asRequestBody(mimeType?.toMediaType())
        val multipart = MultipartBody.Part.createFormData("image_1", file.name, fileBody)

        viewModelScope.launch {
            updateState_ { copy(isLoading = true) }
            when (val response = apiRepository.submitReview(planId, description, multipart)) {
                is Resource.Success -> {
                    if (response.value.status == AppConstants.APIStatus.SUCCESS) {
                        updateState_ {
                            copy(
                                isLoading = false,
                                toastMessageRes = R.string.your_request_successfully_submitted,
                                finishActivity = ConsumableCommand(Unit),
                            )
                        }
                    } else {
                        updateState_ {
                            copy(isLoading = false, errorMessage = response.value.error?.message)
                        }
                    }
                }
                is Resource.Failure -> {
                    updateState_ { copy(isLoading = false, errorMessage = response.errorBody) }
                }
                else -> Unit
            }
        }
    }

    fun onBack() = updateState_ { copy(finishActivity = ConsumableCommand(Unit)) }

    fun consumeError() = updateState_ { copy(errorMessage = null) }
    fun consumeToast() = updateState_ { copy(toastMessageRes = null) }

    private fun showToast(resId: Int) = updateState_ { copy(toastMessageRes = resId) }

    override fun onFailure(throwable: Throwable) {
        updateState_ { copy(isLoading = false, errorMessage = throwable.localizedMessage) }
    }
}
