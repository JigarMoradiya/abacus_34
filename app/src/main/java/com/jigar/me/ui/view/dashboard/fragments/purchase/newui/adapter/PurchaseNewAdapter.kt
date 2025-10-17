package com.jigar.me.ui.view.dashboard.fragments.purchase.newui.adapter

import android.content.res.ColorStateList
import android.graphics.Paint
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.jigar.me.R
import com.jigar.me.data.model.data.PlanAssignFromAdminData
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import com.jigar.me.databinding.RawPurchaseNewBinding
import com.jigar.me.ui.view.base.inapp.BillingRepository
import com.jigar.me.utils.Constants
import com.jigar.me.utils.DateTimeUtils
import com.jigar.me.utils.extensions.dp
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.layoutInflater
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.show

class PurchaseNewAdapter(
    private var listData: List<InAppSkuDetails>,
    private val planListAssignFromAdmin: List<PlanAssignFromAdminData>,
    private val discountPer: Int = 0,
    private val rv : RecyclerView,
    private val isOnlyView: Boolean = false,
) :
    RecyclerView.Adapter<PurchaseNewAdapter.FormViewHolder>() {
    var selectedPosition = 0
    fun getSelectedData() = listData[selectedPosition]
    private var original1YearData : InAppSkuDetails? = null
    fun setData(listData: List<InAppSkuDetails>, original1YearData : InAppSkuDetails? = null) {
        this.listData = listData
        this.original1YearData = original1YearData
        notifyItemRangeChanged(0, listData.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormViewHolder {
        val binding =
            RawPurchaseNewBinding.inflate(parent.context.layoutInflater, parent, false)
        return FormViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val data: InAppSkuDetails = listData[position]
        with(holder.binding) {
            val context = root.context
            sku = data
            cardMain.onClick {
                if (selectedPosition != position){
                    val oldPosition = selectedPosition
                    selectedPosition = position
                    cardMain.strokeWidth = 1.dp

                    val viewHolder = (rv.findViewHolderForAdapterPosition(oldPosition) as FormViewHolder?)
                    if (viewHolder != null){
                        viewHolder.binding.cardMain.strokeWidth = 0
                    }
                }
            }

            if (isOnlyView){
                isOnlyViews = true
                cardMain.strokeWidth = 0
                txtTitle.text = data.title +" - "+data.getDurationTxt()
            }else{
                txtTitle.text = data.getDurationTxt()
                if (selectedPosition == position){
                    cardMain.strokeWidth = 1.dp
                }else{
                    cardMain.strokeWidth = 0
                }

                var isPlanAssignFromAdmin = false
                planListAssignFromAdmin.find { it.google_order_id == null && data.sku.contains(it.google_plan_id?:"") }.also {
                    isPlanAssignFromAdmin = it != null
                    if (isPlanAssignFromAdmin){
                        if (it?.purchased_from.equals(Constants.PLAN_ASSIGN_FROM_ADMIN_FOR_REVIEW,true)){
                            txtPlanAssignFromAdminTitle.text = HtmlCompat.fromHtml(context.getString(
                                R.string.plan_assign_from_admin_for_your_google_review_submitted),
                                HtmlCompat.FROM_HTML_MODE_LEGACY)
                        }else if (it?.purchased_from.equals(Constants.PLAN_ASSIGN_FROM_ADMIN_ON_REQUEST,true)){
                            txtPlanAssignFromAdminTitle.text = HtmlCompat.fromHtml(context.getString(
                                R.string.plan_assign_from_admin_on_your_request), HtmlCompat.FROM_HTML_MODE_LEGACY)
                        }else{
                            txtPlanAssignFromAdminTitle.text = context.getString(
                                R.string.plan_assign_from_admin)
                        }
                        txtPlanAssignFromAdminMsg.text = HtmlCompat.fromHtml("<b>Assign ON : </b>${
                            DateTimeUtils.convertDateFormat(it?.start_date?:"",
                                DateTimeUtils.yyyy_MM_dd_T_HH_mm_ss_sssz,
                                DateTimeUtils.dd_MMMM_yyyy)}", HtmlCompat.FROM_HTML_MODE_COMPACT)
                        cardMain.isEnabled = false
                    }
                }
                isPlanAssignFromBackend = isPlanAssignFromAdmin
            }

            if (data.isPurchase){
                cardMain.isEnabled = false
            }

            if (discountPer > 0){
                if(data.sku == BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1_Offer){
                    if (original1YearData != null){
                        if ((original1YearData?.price_amount_micros ?: 0) > (data.price_amount_micros?:0)){
                            txtPriceOld.text = original1YearData?.price
                            txtPriceOld.paintFlags = txtPriceOld.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                            txtPriceOld.show()
                        }
                    }
                }
            }

            if(data.sku == BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1 || data.sku == BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1_Offer){
                listData.find { it.sku == BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Month1 }.also {
                    if (it != null){
                        val yearlyMicros = if (original1YearData != null ){ original1YearData?.price_amount_micros }else { data.price_amount_micros }
                        val savingPercent = data.calculateSavings(it.price_amount_micros?:0, yearlyMicros?:0)
                        txtSave.text = HtmlCompat.fromHtml("save <b>${savingPercent}%</b> vs monthly", HtmlCompat.FROM_HTML_MODE_LEGACY)
                        isSaveAmountShow = true
                        if (discountPer > 0){
                            txtDiscount.show()
                            txtDiscount.text = " + ${discountPer}% OFF"
                        }else{
                            txtDiscount.hide()
                        }
                    }else{
                        isSaveAmountShow = false
                        txtDiscount.hide()
                    }
                }
            }else{
                isSaveAmountShow = false
                txtDiscount.hide()
            }
        }
    }

    class FormViewHolder(
        itemBinding: RawPurchaseNewBinding
    ) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var binding: RawPurchaseNewBinding = itemBinding
    }

    override fun getItemCount(): Int {
        return listData.size
    }

}