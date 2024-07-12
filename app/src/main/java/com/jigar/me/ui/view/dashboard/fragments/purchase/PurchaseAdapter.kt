package com.jigar.me.ui.view.dashboard.fragments.purchase

import android.content.res.ColorStateList
import android.graphics.Paint
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.BillingClient
import com.jigar.me.R
import com.jigar.me.data.local.data.ColorProvider
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.databinding.RawPurchaseBinding
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Month3
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Month6
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_material_maths
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_material_nursery
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.layoutInflater
import com.jigar.me.utils.extensions.show

class PurchaseAdapter(
    private var listData: List<InAppSkuDetails>,
    private val prefManager : AppPreferencesHelper,
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
        with(holder){
            val context = binding.root.context
            binding.sku = data
            val colorList = ColorProvider.getPurchaseColorsList()
            val colorPosition = ((position + 1) % colorList.size)
            if (data.isPurchase){
                binding.btnRecommended.hide()
            }else{
                binding.btnRecommended.show()
            }

            binding.spaceTop.show()

            binding.txtDiscount.hide()
            binding.txtOriginalPrice.hide()
            if (data.type == BillingClient.ProductType.SUBS){
                binding.txtPrice.text = data.price
                if (!data.originalPrice.isNullOrEmpty()){
                    binding.txtOriginalPrice.text = data.originalPrice
                    binding.txtOriginalPrice.paintFlags = binding.txtOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    binding.txtOriginalPrice.show()
                }
            }else{
                binding.txtPrice.text = data.price
            }
            when (data.sku) {
                PRODUCT_ID_Subscription_Month3 -> {
                    binding.btnRecommended.text = context.getString(R.string.favourite)
                }
                PRODUCT_ID_Subscription_Month6-> {
                    binding.btnRecommended.text = context.getString(R.string.popular)
                }
                PRODUCT_ID_Subscription_Year1 -> {
                    binding.btnRecommended.text = context.getString(R.string.recommended)
                }
                PRODUCT_ID_All_lifetime -> {
                    binding.btnRecommended.text = context.getString(R.string.hot)
                }
                else -> {
                    binding.btnRecommended.hide()
                    binding.spaceTop.hide()
                }
            }
            binding.btnRecommended.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,colorList[colorPosition].darkColor))
            binding.cardColor = ContextCompat.getColor(context,colorList[colorPosition].color)
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