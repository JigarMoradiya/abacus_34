package com.jigar.me.ui.view.dashboard.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.jigar.me.R
import com.jigar.me.data.local.data.DataProvider
import com.jigar.me.data.local.data.MyAccountProvider
import com.jigar.me.data.model.data.ChangePasswordRequest
import com.jigar.me.data.model.data.LoginData
import com.jigar.me.data.model.data.Statistics
import com.jigar.me.databinding.FragmentMyProfileBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.ChangePasswordDialog
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.CommonConfirmationBottomSheet
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.SelectAvatarProfileDialog
import com.jigar.me.ui.view.login.LoginDashboardActivity
import com.jigar.me.ui.view.other.ContactUsActivity
import com.jigar.me.ui.viewmodel.ExamViewModel
import com.jigar.me.ui.viewmodel.StudentViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.Constants
import com.jigar.me.utils.DateTimeUtils.at_dd_mmm_yy_hh_mm_a
import com.jigar.me.utils.DateTimeUtils.formatTo
import com.jigar.me.utils.DateTimeUtils.toDate
import com.jigar.me.utils.Resource
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.openMail
import com.jigar.me.utils.extensions.openURL
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MyProfileFragment : BaseFragment(), SelectAvatarProfileDialog.AvatarProfileDialogInterface,
    MyAccountListAdapter.OnItemClickListener, ChangePasswordDialog.DialogChangePasswordInterface {
    private lateinit var binding: FragmentMyProfileBinding
    private lateinit var mNavController: NavController
    private var loginData: LoginData? = null
    private lateinit var adapter: MyAccountListAdapter
    private val viewModel by viewModels<ExamViewModel>()
    private val studentViewModel by viewModels<StudentViewModel>()
    private var root : View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObserver()
    }

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        if (root == null){
            binding = FragmentMyProfileBinding.inflate(inflater, container, false)
            root = binding.root
            setNavigationGraph()
            initViews()
            initListener()
        }
        return root!!
    }
    private fun setNavigationGraph() {
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }
    private fun initViews() {
        avatarProfileCloseDialog()
        adapter = MyAccountListAdapter(MyAccountProvider.getMenuList(requireContext()),this)
        with(binding){
            recyclerview.adapter = adapter
        }
        viewModel.getStatistics()
    }

    private fun initListener() {
        with(binding){
            cardBack.onClick { onBack() }
            cardEditImage.onClick {
                SelectAvatarProfileDialog.showPopup(requireActivity(),prefManager,this@MyProfileFragment,isChooseProfileDirect = true)
            }
        }
    }

    private fun initObserver() {
        viewModel.getStatisticsResponse.observe(this) {
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    if (it.value.status == AppConstants.APIStatus.SUCCESS)
                        onSuccess(it.value.data)
                }
                is Resource.Failure -> {
                }
            }
        }
        studentViewModel.changePasswordResponse.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    showLoading()
                }
                is Resource.Success -> {
                    hideLoading()
                    if (it.value.status == AppConstants.APIStatus.SUCCESS){
                        onFailure(getString(R.string.your_password_has_been_updated))
                        ChangePasswordDialog.bottomSheetDialog?.dismiss()
                    }
                }
                is Resource.Failure -> {
                    hideLoading()
                    onFailure(it.errorBody)
                }
            }
        }
    }

    private fun onSuccess(data: JsonObject?) {
        val response = Gson().fromJson(data, Statistics::class.java)
        with(binding){
            if (response.EXAM?.last_exam_given_time != null){
                txtStatisticsCount1.text = response.EXAM?.count.toString()
                txtStatisticsTime1.text = buildString {
                    append("Last attend ")
                    append(response.EXAM?.last_exam_given_time?.toDate()?.formatTo(at_dd_mmm_yy_hh_mm_a)?.lowercase(Locale.getDefault()))
                }
            }
            if (response.EXERCISE?.last_exam_given_time != null){
                txtStatisticsCount2.text = response.EXERCISE?.count.toString()
                txtStatisticsTime2.text = buildString {
                    append("Last attend ")
                    append(response.EXERCISE?.last_exam_given_time?.toDate()?.formatTo(at_dd_mmm_yy_hh_mm_a)?.lowercase(Locale.getDefault()))
                }
            }
            if (response.CCM?.last_exam_given_time != null){
                txtStatisticsCount3.text = response.CCM?.count.toString()
                txtStatisticsTime3.text = buildString {
                    append("Last attend ")
                    append(response.CCM?.last_exam_given_time?.toDate()?.formatTo(at_dd_mmm_yy_hh_mm_a)?.lowercase(Locale.getDefault()))
                }
            }
        }
    }
    private fun paidPlanDialog() {
        CommonConfirmationBottomSheet.showPopup(requireActivity(),getString(R.string.txt_purchase_alert), getString(R.string.need_paid_plan_msg),
            getString(R.string.yes_i_want_to_purchase),getString(R.string.no_purchase_later), icon = R.drawable.ic_alert_not_purchased,
            clickListener = object : CommonConfirmationBottomSheet.OnItemClickListener{
                override fun onConfirmationYesClick(bundle: Bundle?) {
                    goToInAppPurchase()
                }
                override fun onConfirmationNoClick(bundle: Bundle?) = Unit
            })
    }
    override fun onMenuItemClick(tag: String) {
        when (tag) {
            "faqs" -> {
                mNavController.navigate(R.id.toFAQsFragment)
            }
            "subscription" -> {
                goToInAppPurchase()
            }
            "setting" -> {
                goToSetting()
            }
            "report_history" -> {
                if (prefManager.getCustomParam(AppConstants.Purchase.Purchase_All, "") == "Y"){
                    mNavController.navigate(R.id.action_myProfileFragment_to_reportsHomeFragment)
                }else{
                    paidPlanDialog()
                }
            }
            "edit_profile" -> {
                mNavController.navigate(R.id.action_myProfileFragment_to_editProfileFragment)
            }
            "change_password" -> {
                ChangePasswordDialog.showPopup(requireActivity(),this)
            }
            "about_us" -> {

            }
            "rate_us_on_the_play_store" -> {
                requireContext().openURL("https://play.google.com/store/apps/details?id=${requireContext().packageName}")
            }
            "need_help" -> {
//                requireContext().openMail(prefManager)
                ContactUsActivity.getInstance(requireContext(),AppConstants.extras_Comman.typeNeedHelp)
            }
            "privacy_policy" -> {
                requireContext().openURL(prefManager.getCustomParam(AppConstants.RemoteConfig.privacyPolicyUrl,""))
            }
            "logout" -> {
                logout()
            }
        }
    }

    override fun changePassword(oldPassword: String, newPassword: String) {
        studentViewModel.changePassword(ChangePasswordRequest(oldPassword,newPassword))
    }

    private fun logout() {
        CommonConfirmationBottomSheet.showPopup(requireActivity(),
            getString(R.string.logout_alert),
            getString(R.string.logout_alert_msg),
            getString(R.string.yes_i_m_sure),
            getString(R.string.no),
            icon = R.drawable.ic_alert,
            clickListener = object : CommonConfirmationBottomSheet.OnItemClickListener {
                override fun onConfirmationYesClick(bundle: Bundle?) {
                    prefManager.setAccessToken("")
                    prefManager.setLoginData("")
                    prefManager.setUserLoggedIn(false)
                    LoginDashboardActivity.getInstance(requireContext())
                }

                override fun onConfirmationNoClick(bundle: Bundle?) = Unit
            })
    }

    private fun onBack() {
        mNavController.navigateUp()
    }

    override fun onResume() {
        super.onResume()
        loginData = Gson().fromJson(prefManager.getLoginData(), LoginData::class.java)
        binding.txtWelcomeTitle.text = CommonUtils.getCurrentTimeMessage(requireContext()).plus(" "+loginData?.name+"!")
    }
    override fun avatarProfileCloseDialog() {
        val id = prefManager.getCustomParamInt(Constants.avatarId,1)
        val avatarList = DataProvider.getAvatarList()
        avatarList.find { it.id == id }?.also {
            binding.imgUserProfile.setImageResource(it.image)
        }
    }
}