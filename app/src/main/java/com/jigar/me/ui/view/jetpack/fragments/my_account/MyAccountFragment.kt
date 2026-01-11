package com.jigar.me.ui.view.jetpack.fragments.my_account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.core.presentation.theme.AbacusTheme
import com.jigar.me.ui.view.jetpack.fragments.common.BackButtonWithText
import com.jigar.me.ui.view.jetpack.fragments.common.dialogs.CustomPopupView
import com.jigar.me.ui.view.jetpack.fragments.my_account.components.MyAccountScreen
import com.jigar.me.ui.view.jetpack.fragments.my_account.viewmodels.MyAccountViewModel
import com.jigar.me.ui.view.login.LoginDashboardActivity
import com.jigar.me.ui.view.other.ContactUsActivity
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.openURL
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyAccountFragment : Fragment() {
    private val viewModel: MyAccountViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                AbacusTheme {
                    Column(modifier = Modifier.fillMaxSize()) {
                        BackButtonWithText(title = stringResource(R.string.my_account), onBackClick = {findNavController().popBackStack()})
                        MyAccountScreen(uiState){ tag ->
                            when (tag) {
                                "faqs" -> {
                                    findNavController().navigate(R.id.toFAQsFragmentNew)
                                }
                                "subscription" -> {
                                    findNavController().navigate(R.id.toPurchaseFragment)
                                }
                                "setting" -> {
//                                    findNavController().navigate(R.id.toSettingsFragmentNew)
                                    findNavController().navigate(R.id.action_myProfileFragment_to_reportsHomeFragment)
                                }
                                "report_history" -> {
                                    findNavController().navigate(R.id.toReportHistoryFragment)
                                }
                                "about_app" -> {
                                    findNavController().navigate(R.id.toVideoPreviewFragment)
                                }
                                "rate_us_on_the_play_store" -> {
                                    requireContext().openURL("https://play.google.com/store/apps/details?id=${requireContext().packageName}")
                                }
                                "need_help" -> {
                                    ContactUsActivity.getInstance(requireContext(),AppConstants.extras_Comman.typeNeedHelp)
                                }
                                "privacy_policy" -> {
                                    uiState.privacyPolicyUrl?.let {
                                        requireContext().openURL(it)
                                    }
                                }
                                "logout" -> {
                                    viewModel.logoutOpen()
                                }
                            }
                        }

                    }

                    // logout popup
                    AnimatedVisibility(
                        visible = uiState.isShowLogoutPopup,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        CustomPopupView(
                            title = stringResource(R.string.logout_alert),
                            description = stringResource(R.string.logout_alert_msg),
                            positiveButtonText = stringResource(R.string.yes_i_m_sure),
                            negativeButtonText = stringResource(R.string.no),
                            icon = R.drawable.ic_alert,
                            widthMultiplier = 0.5f,
                            onPositiveTapped = {
                                viewModel.makeLogout()
                                LoginDashboardActivity.getInstance(requireContext())
                            },
                            onNegativeTapped = {}
                        )
                    }
                }

            }
        }
    }

}
