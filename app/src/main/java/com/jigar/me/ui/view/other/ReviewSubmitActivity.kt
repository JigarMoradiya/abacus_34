package com.jigar.me.ui.view.other

import CommonAlertDialog
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jigar.me.R
import com.jigar.me.data.model.data.ContactUsRequest
import com.jigar.me.data.model.data.LoginData
import com.jigar.me.data.model.data.ReviewData
import com.jigar.me.databinding.ActivityContactUsBinding
import com.jigar.me.databinding.ActivityReviewSubmitBinding
import com.jigar.me.ui.view.base.BaseActivity
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Month3_Level1
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Month3_Level2
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Month3_Level3
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Month3_Level4
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Month3_Level5
import com.jigar.me.ui.viewmodel.StudentViewModel
import com.jigar.me.ui.viewmodel.UserViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.Constants
import com.jigar.me.utils.FileUtils.getMimeType
import com.jigar.me.utils.FileUtils.getPath
import com.jigar.me.utils.Resource
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.hideKeyboard
import com.jigar.me.utils.extensions.isNotNullOrEmpty
import com.jigar.me.utils.extensions.markRequiredInRed
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.show
import com.jigar.me.utils.setImgUrl
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.Objects

@AndroidEntryPoint
class ReviewSubmitActivity : BaseActivity() {
    lateinit var binding: ActivityReviewSubmitBinding
    private val studentViewModel by viewModels<StudentViewModel>()
    private var multiPart: MultipartBody.Part? = null
    private var planArray = listOf("",PRODUCT_ID_Subscription_Month3_Level1,PRODUCT_ID_Subscription_Month3_Level2,
        PRODUCT_ID_Subscription_Month3_Level3,PRODUCT_ID_Subscription_Month3_Level4,PRODUCT_ID_Subscription_Month3_Level5)
    private var reviewListData : List<ReviewData> = arrayListOf()
    companion object {
        fun getInstance(context: Context?) {
            Intent(context, ReviewSubmitActivity::class.java).apply {
                context?.startActivity(this)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReviewSubmitBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        initListener()
        initObserver()

    }

    private fun initObserver() {
        studentViewModel.submitReviewResult.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    showLoading()
                }
                is Resource.Success -> {
                    hideLoading()
                    if (it.value.status == AppConstants.APIStatus.SUCCESS){
                        showToast(getString(R.string.your_request_successfully_submitted))
                        finish()
                    }else
                        onFailure(it.value.error?.message)
                }
                is Resource.Failure -> {
                    hideLoading()
                    onFailure(it.errorBody)
                }
            }
        }
    }

    private fun initViews() = with(binding){
        onMainActivityBack()
        val list : ArrayList<String> = arrayListOf()
        with(list) {
            add(getString(R.string.select_level__))
            add(getString(R.string.level_1))
            add(getString(R.string.level_2))
            add(getString(R.string.level_3))
            add(getString(R.string.level_4))
            add(getString(R.string.level_5))
        }

        val adapter = ArrayAdapter(this@ReviewSubmitActivity, android.R.layout.simple_spinner_item, list)
        adapter.setDropDownViewResource(R.layout.spinner_item_dropdown)
        spinnerFilter.adapter = adapter
        spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 > 0){
                    txtFilter.text = list[p2]
                }else{
                    txtFilter.text = getString(R.string.select_level)
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) = Unit
        }

        if (prefManager.getCustomParam(Constants.APP_REVIEW_DATA,"").isNotEmpty()){
            reviewListData =  Gson().fromJson(prefManager.getCustomParam(Constants.APP_REVIEW_DATA,""), object : TypeToken<List<ReviewData>>() {}.type)
            if (reviewListData.isNotNullOrEmpty()){
                reviewListData.first().let{
                    list.mapIndexed { index, s ->
                        if (it.plan_id?.contains(s.replace(" ",""),true) == true){
                            txtFilter.text = s
                            spinnerFilter.setSelection(index)
                        }
                    }

                    linearReviewStatus.show()
                    if (it.status?.equals(Constants.APP_REVIEW_STATUS_REJECT,true) == true){
                        btnSubmit.text = getString(R.string.submit_new_request)
                        txtAddReviewImage.text = getString(R.string.change_screenshot_of_application_review)
                        txtReviewAdminStatus.text = getString(R.string.sorry_your_submitted_review_is_rejected_please_try_again)
                        txtReviewAdminStatus.setTextColor(ContextCompat.getColor(this@ReviewSubmitActivity,R.color.red_900))
                        linearReviewStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this@ReviewSubmitActivity,R.color.red_50))
                        txtReviewAdminMsg.text = it.comment
                    }else if (it.status?.equals(Constants.APP_REVIEW_STATUS_APPROVE,true) == true){
                        txtReviewAdminStatus.text = getString(R.string.congratulations_your_submitted_review_is_approved)
                        txtReviewAdminStatus.setTextColor(ContextCompat.getColor(this@ReviewSubmitActivity,R.color.green_900))
                        linearReviewStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this@ReviewSubmitActivity,R.color.green_50))
                        txtReviewAdminMsg.hide()
                        btnSubmit.hide()
                        txtAddReviewImage.hide()
                        txtNotes.hide()
                        txtFilter.isEnabled = false
                        etDescription.isEnabled = false
                    }else{

                        txtReviewAdminStatus.text = getString(R.string.your_request_successfully_submitted)
                        linearReviewStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this@ReviewSubmitActivity,R.color.blue_grey_50))
                        txtReviewAdminStatus.setTextColor(ContextCompat.getColor(this@ReviewSubmitActivity,R.color.black))
                        txtReviewAdminMsg.text = getString(R.string.please_wait_white_we_are_reviewing_your_request)
                        btnSubmit.hide()
                        txtAddReviewImage.hide()
                        txtNotes.hide()
                        txtFilter.isEnabled = false
                        etDescription.isEnabled = false
                    }
                    etDescription.setText(it.description)
                    binding.imgReview.show()
                    setImgUrl(binding.imgReview,it.image_1,ContextCompat.getDrawable(this@ReviewSubmitActivity,R.drawable.placeholder))
                }
            }
        }
    }

    private fun initListener() = with(binding){
        cardBack.onClick {
            finish()
        }
        txtAddReviewImage.onClick {
            imagePickerAlert()
        }
        txtFilter.onClick {
            spinnerFilter.performClick()
        }
        btnSubmit.onClick {
            if (spinnerFilter.selectedItemPosition == 0){
                showToast(getString(R.string.please_select_level))
            }else if (multiPart == null){
                showToast(getString(R.string.please_select_screenshot_of_application_review))
            }else{
                val planId = planArray[spinnerFilter.selectedItemPosition].toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val description = etDescription.text.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                studentViewModel.submitReview(planId,description,multiPart)
            }
        }
    }
    private fun imagePickerAlert() {
        ImagePicker.with(this)
            .compress(700)        //Final image size will be less than 1 MB(Optional)
            .galleryOnly()
            .maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
            .createIntent { intent ->
                startForProfileImageResult.launch(intent)
            }
    }
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                data?.data?.let { fileUri ->
                    //                val file = ImagePicker.getFile(data)
                    val path = getPath(fileUri)
                    val file = File(path)
                    binding.imgReview.show()
                    setImgUrl(binding.imgReview,path,ContextCompat.getDrawable(this,R.drawable.placeholder))
//                    binding.imgReview.setImageURI(fileUri)

                    val mimeType = file.let { getMimeType(it) }
                    val fileRequestBody = file.asRequestBody(mimeType?.toMediaType())
                    multiPart = MultipartBody.Part.createFormData("image_1",
                        file.name, fileRequestBody)
                }
            }
        }
    private fun onMainActivityBack() {
        onBackPressedDispatcher.addCallback(
            this, // lifecycle owner
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    binding.cardBack.performClick()
                }
            })
    }


}