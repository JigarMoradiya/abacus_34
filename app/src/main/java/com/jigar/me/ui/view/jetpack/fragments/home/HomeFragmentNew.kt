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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.jigar.me.R
import com.jigar.me.data.local.data.DataProvider.generateAdditionSubExerciseTemp
import com.jigar.me.ui.view.jetpack.core.presentation.theme.AbacusTheme
import com.jigar.me.ui.view.jetpack.fragments.common.Loader
import com.jigar.me.ui.view.jetpack.fragments.common.dialogs.CustomPopupView
import com.jigar.me.ui.view.jetpack.fragments.common.dialogs.FreeTrialDialog
import com.jigar.me.ui.view.jetpack.fragments.home.components.HomeHeaderLeft
import com.jigar.me.ui.view.jetpack.fragments.home.components.HomeHeaderRight
import com.jigar.me.ui.view.jetpack.fragments.home.components.HomeMenuScreen
import com.jigar.me.ui.view.jetpack.fragments.home.viewmodels.HomeFragmentViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Constants
import com.jigar.me.utils.checkPermissions
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
//                generateAdditionSubExerciseTemp()
                AbacusTheme {
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
                            }, modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                    // loader
                    if (uiState.isLoading == true) {
                        Loader()
                    }

                    // purchased device or user conflict
                    AnimatedVisibility(
                        visible = uiState.isShowPurchasedConflictPopup,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        var title = getString(R.string.your_device_purchases_is_associated_with_other_login)
                        if (uiState.purchasedConflictPopupType == AppConstants.APIStatus.ERROR_CODE_THIS_STUDENT_IS_ASSOCIATED_WITH_OTHER_ORDER){
                            title = getString(R.string.your_login_is_associated_with_other_purchases)
                        }
                        CustomPopupView(
                            title = title,
                            description = getString(R.string.want_to_move_purchase_with_this_login),
                            positiveButtonText = getString(R.string.yes_i_want_to_move),
                            negativeButtonText = getString(R.string.no_move_later),
                            notes = getString(R.string.no_move_later_msg),
                            widthMultiplier = 0.8f,
                            onPositiveTapped = {
                                viewModel.changePurchase()
                            },
                            onNegativeTapped = {
                                viewModel.closeConflictPopup()
                            }
                        )
                    }

                    // notification popup
                    uiState.checkNotificationPermission?.consume {
                        checkNotificationPermission()
                    }
                    AnimatedVisibility(
                        visible = uiState.isShowNotificationSettingPopup,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        CustomPopupView(
                            title = getString(R.string.permission_alert),
                            description = getString(R.string.notification_permission_msg),
                            positiveButtonText = getString(R.string.okay),
                            negativeButtonText = getString(R.string.give_later),
                            widthMultiplier = 0.7f,
                            onPositiveTapped = {
                                viewModel.showHideNotificationSettingPopup(false)
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri: Uri = Uri.fromParts("package", requireContext().packageName, null)
                                intent.data = uri
                                resumeActivityResultLauncher.launch(intent)
                            },
                            onNegativeTapped = {
                                viewModel.showHideNotificationSettingPopup(false)
                            }
                        )
                    }

                    // show free trial popup
                    AnimatedVisibility(
                        visible = uiState.isShowFreeTrialPopup,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        val freeTrialParam = uiState.freeTrialParam
                        freeTrialParam?.let {

                            FreeTrialDialog(
                                remainingDays = freeTrialParam.remainingDays,
                                discountPer = freeTrialParam.discountPer,
                                discountPerLifetime = freeTrialParam.discountPerLifeTime,
                                onYes = {
                                    viewModel.hideFreeTrialPopup()
                                    if (freeTrialParam.remainingDays >= 7){
                                        findNavController().navigate(R.id.toWhatsLearningNewFragment)
                                    }else{
                                        findNavController().navigate(R.id.toPurchaseFragment)
                                    }
                                },
                                onNo = {
                                    viewModel.hideFreeTrialPopup()
                                },
                                onDismiss = {
                                    viewModel.hideFreeTrialPopup()
                                }
                            )
                        }
                    }
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
                findNavController().navigate(R.id.toMyAccountFragment)
            }
            AppConstants.HomeClicks.Menu_Abacus_Exercise -> {
                findNavController().navigate(R.id.toExerciseFragment)
            }
            AppConstants.HomeClicks.Menu_Exam -> {
                findNavController().navigate(R.id.toExamHomeFragmentNew)
            }
            AppConstants.HomeClicks.Menu_CCM -> {
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

    // notification permission
    private fun checkNotificationPermission() {
        requireActivity().checkPermissions(Constants.NOTIFICATION_PERMISSION, requestMultiplePermissions)
    }

    // permission result
    private var requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.filter { !it.value }.also {
                if (it.isNotEmpty()) {
                    viewModel.showHideNotificationSettingPopup(true)
                }
            }
        }

    /**
     * Activity Result For Resume Result
     */
    private var resumeActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult -> }

}
