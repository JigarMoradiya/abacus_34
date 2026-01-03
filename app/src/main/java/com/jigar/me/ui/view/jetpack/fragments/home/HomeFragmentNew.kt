package com.jigar.me.ui.view.jetpack.fragments.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.jigar.me.R
import com.jigar.me.data.model.data.PurchasedPlanCheckRequest
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.CommonConfirmationBottomSheet
import com.jigar.me.ui.view.confirm_alerts.dialogs.FreeTrialLeftDialog
import com.jigar.me.ui.view.confirm_alerts.dialogs.FreeTrialLeftDialog.DialogFreeTrialInterface
import com.jigar.me.ui.view.jetpack.fragments.common.Loader
import com.jigar.me.ui.view.jetpack.fragments.home.components.HomeHeaderLeft
import com.jigar.me.ui.view.jetpack.fragments.home.components.HomeHeaderRight
import com.jigar.me.ui.view.jetpack.fragments.home.components.HomeMenuScreen
import com.jigar.me.ui.view.jetpack.fragments.home.viewmodels.FreeTrialParam
import com.jigar.me.ui.view.jetpack.fragments.home.viewmodels.HomeFragmentViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Constants
import com.jigar.me.utils.checkPermissions
import com.jigar.me.utils.extensions.isNotNullOrEmpty
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragmentNew : Fragment() {
    private val viewModel: HomeFragmentViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                MaterialTheme {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row{
                            HomeHeaderLeft{
                                onMenuClick(AppConstants.HomeClicks.Menu_My_Account)
                            }
                            Spacer(Modifier.weight(1f))
                            HomeHeaderRight{
                                onMenuClick(it)
                            }
                        }
                        HomeMenuScreen(
                            uiState = uiState,
                            onMenuClick = {
                                onMenuClick(it.name,it.id)
                            }
                        )
                    }
                }
                // check notification permission
                uiState.checkNotificationPermission?.consume {
                    checkNotificationPermission()
                }
                // free trial popup
                uiState.showFreeTrialPopup?.consume {
                    freeTrialPopup(it)
                }
                // purchased device or user conflict
                uiState.purchasedConflictPopup?.consume {
                    purchasedConflictPopup(it)
                }
                // loader
                if (uiState.isLoading == true) {
                    Loader()
                }
            }
        }
    }

    fun onMenuClick(level: String,id : String? = null) {
        when (level) {
            AppConstants.HomeClicks.Menu_Practice_Abacus -> {
                id?.let{ levelId ->
                    val action = HomeFragmentNewDirections.toCategoryFragmentNew(levelId)
                    findNavController().navigate(action)
                }
            }
            AppConstants.HomeClicks.Menu_Abacus_Free_Mode -> {
                findNavController().navigate(R.id.toAbacusFreeModeFragment)
            }
            AppConstants.HomeClicks.Menu_Math_Game -> {
                findNavController().navigate(R.id.toMathGameZoneFragment)
            }
            AppConstants.HomeClicks.Menu_Settings -> {
                findNavController().navigate(R.id.toSettingsFragmentNew)
            }
            AppConstants.HomeClicks.Menu_My_Account -> {
                findNavController().navigate(R.id.action_homeFragment_to_myProfileFragment)
            }
            AppConstants.HomeClicks.Menu_Abacus_Exercise -> {
                findNavController().navigate(R.id.action_homeFragment_to_exerciseHomeFragment)
            }
            AppConstants.HomeClicks.Menu_Exam -> {
                findNavController().navigate(R.id.action_homeFragment_to_examHomeFragment)
//                findNavController().navigate(R.id.action_homeFragment_to_customChallengeHomeFragment)
            }
            AppConstants.HomeClicks.Menu_CCM -> {
//                findNavController().navigate(R.id.action_homeFragment_to_customChallengeHomeFragment)
                findNavController().navigate(R.id.toCCMHomeFragment)
            }
            AppConstants.HomeClicks.Menu_Purchase_Store -> {
                findNavController().navigate(R.id.toPurchaseFragment)
            }
            AppConstants.HomeClicks.Menu_Video_Tutorial -> {
                findNavController().navigate(R.id.toYoutubeVideoFragmentNew)
            }
        }
    }

    // purchased device or user conflict
    private fun purchasedConflictPopup(type: String) {
        var title = getString(R.string.your_device_purchases_is_associated_with_other_login)
        val msg = getString(R.string.want_to_move_purchase_with_this_login)
        val btnYes = getString(R.string.yes_i_want_to_move)
        val btnNo = getString(R.string.no_move_later)
        if (type == AppConstants.APIStatus.ERROR_CODE_THIS_STUDENT_IS_ASSOCIATED_WITH_OTHER_ORDER){
            title = getString(R.string.your_login_is_associated_with_other_purchases)
        }
        CommonConfirmationBottomSheet.showPopup(requireActivity(),title,msg,
            btnYes, btnNo, icon = R.drawable.ic_alert_not_purchased,
            clickListener = object : CommonConfirmationBottomSheet.OnItemClickListener{
                override fun onConfirmationYesClick(bundle: Bundle?) {
                    viewModel.changePurchase()
                }
                override fun onConfirmationNoClick(bundle: Bundle?) = Unit
            })
    }
    // free Trial Popup
    private fun freeTrialPopup(freeTrialParam: FreeTrialParam) {
        FreeTrialLeftDialog.showPopup(requireActivity(),freeTrialParam,object : DialogFreeTrialInterface{
            override fun onCloseClick() = Unit
            override fun onSubmitYesClick() {
                if (freeTrialParam.remainingDays >= 7){
                    findNavController().navigate(R.id.toVideoPreviewFragment)
                }else{
                    findNavController().navigate(R.id.toPurchaseFragment)
                }
            }
        })
    }
    // notification permission
    private fun checkNotificationPermission() {
        requireActivity().checkPermissions(Constants.NOTIFICATION_PERMISSION, requestMultiplePermissions)
    }

    // permission result
    private var requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.filter { !it.value }.also {
                if (it.isNotEmpty()) {
                    notificationPermissionPopup()
                }
            }
        }

    /**
     * Activity Result For Resume Result
     */
    private var resumeActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult -> }

    private fun notificationPermissionPopup() {
        CommonConfirmationBottomSheet.showPopup(requireActivity(),
            getString(R.string.permission_alert),
            getString(R.string.notification_permission_msg),
            getString(R.string.okay),
            getString(R.string.give_later),
            icon = R.drawable.ic_alert,
            clickListener = object : CommonConfirmationBottomSheet.OnItemClickListener {
                override fun onConfirmationYesClick(bundle: Bundle?) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri: Uri = Uri.fromParts("package", requireContext().packageName, null)
                    intent.data = uri
                    resumeActivityResultLauncher.launch(intent)
                }

                override fun onConfirmationNoClick(bundle: Bundle?) {
                }
            })
    }
}
