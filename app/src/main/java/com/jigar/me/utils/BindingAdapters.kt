package com.jigar.me.utils

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import com.android.billingclient.api.BillingClient
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.jigar.me.R
import java.text.SimpleDateFormat
import java.time.Period
import java.util.*
import java.util.concurrent.TimeUnit

@BindingAdapter("icons")
fun setIcons(view: AppCompatImageView, icons: Int?) {
    icons?.let { view.setImageResource(it) }
}

@BindingAdapter("imgUrl","placeHolder")
fun setImgUrl(view: AppCompatImageView, profile_url: String?, placeHolder : Drawable?) {
    val context = view.context
    Glide.with(view.context)
        .load(profile_url)
        .apply(RequestOptions().placeholder(placeHolder?: ContextCompat.getDrawable(context, R.drawable.ic_default_user)))
        .into(view)
}
@BindingAdapter("loadImgInsideCache","placeHolder")
fun loadImgInsideCache(view: AppCompatImageView, loadImgURL: String?,placeHolder : Drawable) {
    if (loadImgURL.isNullOrEmpty()) {
        view.setImageDrawable(placeHolder)
    } else {
        Glide.with(view.context)
            .load(loadImgURL)
            .placeholder(ImageUtils.getCircleProgress(view.context))
            .error(placeHolder)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .fitCenter()
            .into(view)
    }

}

@BindingAdapter("cardColor")
fun cardBackgroundColor(view: MaterialCardView, cardColor: Int) {
    view.setCardBackgroundColor(ColorStateList.valueOf(cardColor))
}
@BindingAdapter("txtColorApp")
fun txtColorApp(view: MaterialTextView, tintColor: Int?) {
    view.setTextColor(ColorStateList.valueOf(tintColor!!))
}
@BindingAdapter("purchaseOrderId")
fun purchaseOrderId(view: MaterialTextView, orderId: String) {
    view.text = HtmlCompat.fromHtml("<b>Order Id : </b>$orderId", HtmlCompat.FROM_HTML_MODE_COMPACT)
}
@BindingAdapter("productType","purchaseTime","billingPeriod")
fun purchaseTime(view: MaterialTextView,productType : String, purchaseTime: Long,billingPeriod : String? = null) {
    if (purchaseTime > 0) {
        val formatter = SimpleDateFormat("dd MMMM yyyy")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = purchaseTime
        if (productType == BillingClient.ProductType.SUBS){
            val calendarEnd = Calendar.getInstance()
            calendarEnd.timeInMillis = purchaseTime

            val dateDiff = System.currentTimeMillis() - calendar.timeInMillis
            val day: Long = TimeUnit.MILLISECONDS.toDays(dateDiff)

            if (billingPeriod.equals("p1w",true)){
                var weeks = day/7
                weeks += 1
                calendarEnd.add(Calendar.WEEK_OF_MONTH,weeks.toInt())
            }else if (billingPeriod.equals("p1y",true)){
                var year = day/365
                year += 1
                calendarEnd.add(Calendar.YEAR,year.toInt())
            }else{
                val months = if (billingPeriod.equals("p1m",true)){
                    var months = day/30
                    months += 1
                    months
                }else if (billingPeriod.equals("p3m",true)){
                    var months = day/90
                    months += 3
                    months
                }else{ // if (billingPeriod.equals("p6m",true))
                    var months = day/180
                    months += 6
                    months
                }

                calendarEnd.add(Calendar.MONTH,months.toInt())
            }
            view.text = HtmlCompat.fromHtml("<b>Subscribed ON : </b>${formatter.format(calendar.time)}<br/><b>Expire ON : </b>${formatter.format(calendarEnd.time)}",HtmlCompat.FROM_HTML_MODE_COMPACT)
        }else{
            view.text = HtmlCompat.fromHtml("<b>Purchased ON : </b>${formatter.format(calendar.time)}",HtmlCompat.FROM_HTML_MODE_COMPACT)
        }

    }
}