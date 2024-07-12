package com.jigar.me.ui.view.dashboard.fragments.home

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.viewpager.widget.PagerAdapter
import com.jigar.me.data.model.data.SubscribedList
import com.jigar.me.databinding.RawCurrentPlanBannerPagerBinding
import com.jigar.me.utils.extensions.layoutInflater
import com.jigar.me.utils.extensions.onClick

class CurrentPlanPagerAdapter(var listData: List<SubscribedList>,val listener : OnItemClickListener) : PagerAdapter() {
    interface OnItemClickListener {
        fun onPurchaseRenewClick()
        fun onPurchaseItemClick()
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = RawCurrentPlanBannerPagerBinding.inflate(container.context.layoutInflater,container,false)
        binding.plan = listData[position]
        binding.btnRight.onClick {
            listener.onPurchaseRenewClick()
        }
        binding.root.onClick {
            listener.onPurchaseItemClick()
        }
        container.addView(binding.root)
        return binding.root
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayoutCompat
    }

    override fun getCount(): Int {
        return listData.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayoutCompat)
    }
}