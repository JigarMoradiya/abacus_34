package com.jigar.me.ui.viewmodel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.*
import com.android.billingclient.api.BillingClient
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import com.jigar.me.ui.view.base.inapp.BillingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InAppViewModel @Inject constructor(private val billingRepository: BillingRepository) : ViewModel() {

    fun inAppInit() {
        billingRepository.startDataSourceConnections()
    }
    fun makePurchase(context: Activity, augmentedSkuDetails: InAppSkuDetails) {
        billingRepository.launchBillingFlow(context, augmentedSkuDetails)
    }
}