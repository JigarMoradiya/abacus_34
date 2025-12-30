package com.jigar.me.ui.view.jetpack.fragments.faqs.viewmodels

import android.content.Context
import com.jigar.me.data.local.data.DataProvider
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.jetpack.core.StatefulViewModel
import com.jigar.me.utils.AppConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class FAQsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    prefs: AppPreferencesHelper,
) : StatefulViewModel<FAQUiState>() {

    override val TAG = "FAQsViewModel"

    override fun getInitialState() = FAQUiState()

    init {
        val emailId = prefs.getCustomParam(AppConstants.RemoteConfig.supportEmail,"")
        val faqsList = DataProvider.getFaqsList(context,emailId)
        updateState_ {
            copy(faqsList = faqsList)
        }
    }

    override fun onFailure(throwable: Throwable) {
        updateState_ {
            copy(error = localizeCommonFailure(throwable))
        }
    }
}