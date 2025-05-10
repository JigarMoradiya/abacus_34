package com.jigar.me.ui.view.dashboard.fragments.purchase

import android.content.res.ColorStateList
import android.graphics.Paint
import android.util.Log
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.BillingClient
import com.jigar.me.R
import com.jigar.me.data.local.data.ColorProvider
import com.jigar.me.data.model.data.DiscountData
import com.jigar.me.data.model.data.PlanAssignFromAdminData
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.databinding.RawPurchaseBinding
import com.jigar.me.ui.view.base.inapp.BillingRepository
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Month3
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Constants
import com.jigar.me.utils.DateTimeUtils
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.layoutInflater
import com.jigar.me.utils.extensions.show

class PurchaseAdapter(
    private var listData: List<InAppSkuDetails>,
    private val planListAssignFromAdmin: List<PlanAssignFromAdminData>,
    private val discountData: DiscountData? = null,
    private val mListener: OnItemClickListener
) :
    RecyclerView.Adapter<PurchaseAdapter.FormViewHolder>() {
    interface OnItemClickListener {
        fun onPurchaseItemClick(position: Int)
    }

    fun setData(listData: List<InAppSkuDetails>) {
        this.listData = listData
        notifyItemRangeChanged(0,listData.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormViewHolder {
        val binding = RawPurchaseBinding.inflate(parent.context.layoutInflater,parent,false)
        return FormViewHolder(binding, mListener)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val data: InAppSkuDetails = listData[position]
        with(holder.binding){
            val context = root.context
            sku = data
            txtPrice.text = data.price

            spaceTop.hide()
            btnRecommended.hide()
            txtDiscount.hide()
            txtOriginalPrice.hide()
            txtOfferDes.hide()

            var isPlanAssignFromAdmin = false
            planListAssignFromAdmin.find { it.google_order_id == null && (it.google_plan_id?.contains(data.sku) == true) }.also {
                isPlanAssignFromAdmin = it != null
                if (isPlanAssignFromAdmin){
                    if (it?.purchased_from.equals(Constants.PLAN_ASSIGN_FROM_ADMIN_FOR_REVIEW,true)){
                        txtPlanAssignFromAdminTitle.text = HtmlCompat.fromHtml(context.getString(R.string.plan_assign_from_admin_for_your_google_review_submitted),HtmlCompat.FROM_HTML_MODE_LEGACY)
                    }else if (it?.purchased_from.equals(Constants.PLAN_ASSIGN_FROM_ADMIN_ON_REQUEST,true)){
                        txtPlanAssignFromAdminTitle.text = HtmlCompat.fromHtml(context.getString(R.string.plan_assign_from_admin_on_your_request),HtmlCompat.FROM_HTML_MODE_LEGACY)
                    }else{
                        txtPlanAssignFromAdminTitle.text = context.getString(R.string.plan_assign_from_admin)
                    }
                    txtPlanAssignFromAdminMsg.text = HtmlCompat.fromHtml("<b>Assign ON : </b>${DateTimeUtils.convertDateFormat(it?.start_date?:"",DateTimeUtils.yyyy_MM_dd_T_HH_mm_ss_sssz,DateTimeUtils.dd_MMMM_yyyy)}", HtmlCompat.FROM_HTML_MODE_COMPACT)
                }
            }
            isPlanAssignFromBackend = isPlanAssignFromAdmin

            if (data.sku == PRODUCT_ID_Subscription_Year1){
                val discountPer = discountData?.per?:0
                if (discountPer > 0){
                    txtOriginalPrice.paintFlags = txtOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    txtDiscount.text = "$discountPer% OFF"
                    if (!data.price.isNullOrEmpty() && data.price_amount_micros != null){
                        try {
                            txtOriginalPrice.text = data.price?.get(0).toString()+((data.price_amount_micros/10000) / (100-discountPer)).toString()
                            txtOriginalPrice.show()
                            txtDiscount.show()

                            if (isPlanAssignFromAdmin || data.isPurchase){

                            }else{
                                txtOfferDes.show()
                                spaceTop.show()
                                btnRecommended.show()
                                btnRecommended.text = discountData?.name?:"Discount Offer"
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            val colorList = ColorProvider.getPurchaseColorsList()
            val colorPosition = ((position + 1) % colorList.size)
            btnRecommended.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,colorList[colorPosition].darkColor))
        }
    }

    class FormViewHolder(
        itemBinding: RawPurchaseBinding,
        private val mListener: OnItemClickListener
    ) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var binding: RawPurchaseBinding = itemBinding

        init {
            this.binding.txtPurchase.setOnClickListener {
                this.mListener.onPurchaseItemClick(
                    layoutPosition
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }

}