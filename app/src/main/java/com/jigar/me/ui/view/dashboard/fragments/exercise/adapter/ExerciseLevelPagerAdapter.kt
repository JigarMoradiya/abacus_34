package com.jigar.me.ui.view.dashboard.fragments.exercise.adapter

import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.jigar.me.R
import com.jigar.me.data.local.data.AbacusContent
import com.jigar.me.data.local.data.ExerciseLevel
import com.jigar.me.data.local.data.ExerciseLevelDetail
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.databinding.RawExerciseLevelBinding
import com.jigar.me.databinding.RawExerciseLevelPagerBinding
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.extensions.layoutInflater
import com.jigar.me.utils.extensions.onClick

class ExerciseLevelPagerAdapter(var listData: ArrayList<ExerciseLevel>, val prefManager : AppPreferencesHelper,
                                private val mListener: OnItemClickListener,
                                private val themeContent : AbacusContent? = null) : PagerAdapter() {
    var selectedParentPosition: Int = 0
    var selectedChildPosition : Int = 0
    interface OnItemClickListener {
        fun onExerciseStartClick()
    }
    interface OnChildItemClickListener {
        fun onExerciseLevelClick(parentPosition : Int,childPosition : Int, child: ExerciseLevelDetail)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = RawExerciseLevelPagerBinding.inflate(container.context.layoutInflater,container,false)
        val context = binding.root.context
        themeContent?.resetBtnColor8?.let{
            binding.txtTitle.setTextColor(ContextCompat.getColor(context,it))
        }
        with(listData[position]){
            binding.data = this
            val childData = this.list[this.selectedChildPos]
            val isClearExercises = prefManager.getCustomParamBoolean("Exercise_"+childData.id,false)
            if (isClearExercises){
                binding.simpleRatingBar.rating = 3F
            }else{
                val previousClearExercises = prefManager.getCustomParamInt("Exercise_count_"+childData.id,0)
                binding.simpleRatingBar.rating = previousClearExercises.toFloat()
            }
            when (this.id) {
                "1" -> {
                    binding.desc = "${childData.digits} Digits ${childData.queLines} Lines X ${childData.totalQue} Questions in ${childData.totalTime} minute"
                }
                "2" -> {
                    binding.desc = "The answer is ${childData.digits} Digits X ${childData.totalQue} Questions in ${childData.totalTime} minute"
                }
                "3" -> {
                    binding.desc = "The dividend is MAX ${childData.digits} Digits X ${childData.totalQue} Questions in ${childData.totalTime} minute"
                }
            }

            if (this.id == "2"){
                binding.recyclerviewExercise.layoutManager = GridLayoutManager(container.context,5)
            }else{
                binding.recyclerviewExercise.layoutManager = GridLayoutManager(container.context,6)
            }
            val adapter = ExerciseLevelAdapter(this.list,position,this.selectedChildPos,themeContent, object : OnChildItemClickListener {
                override fun onExerciseLevelClick(parentPosition: Int,childPosition : Int, child: ExerciseLevelDetail) {
                    selectedParentPosition = parentPosition
                    selectedChildPosition = childPosition
                    listData[parentPosition].selectedChildPos = childPosition
                    val isClearExercises = prefManager.getCustomParamBoolean("Exercise_"+child.id,false)
                    if (isClearExercises){
                        binding.simpleRatingBar.rating = 3F
                    }else{
                        val previousClearExercises = prefManager.getCustomParamInt("Exercise_count_"+child.id,0)
                        binding.simpleRatingBar.rating = previousClearExercises.toFloat()
                    }
                    when (listData[parentPosition].id) {
                        "1" -> {
                            binding.desc = "${child.digits} Digits ${child.queLines} Lines X ${child.totalQue} Questions in ${child.totalTime} minute"
                        }
                        "2" -> {
                            binding.desc = "The answer is ${child.digits} Digits X ${child.totalQue} Questions in ${child.totalTime} minute"
                        }
                        "3" -> {
                            binding.desc = "The dividend is MAX ${child.digits} Digits X ${child.totalQue} Questions in ${child.totalTime} minute"
                        }
                    }
                }
            })
            binding.recyclerviewExercise.adapter = adapter
            binding.btnYes.onClick {
                mListener.onExerciseStartClick()
            }
        }
        container.addView(binding.root)
        return binding.root
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as ConstraintLayout
    }

    override fun getCount(): Int {
        return listData.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }


    class ExerciseLevelAdapter(private var listData: ArrayList<ExerciseLevelDetail>, val parentPosition: Int, val childPosition: Int,
                               private val themeContent : AbacusContent? = null, private val mListener: OnChildItemClickListener) :
        RecyclerView.Adapter<ExerciseLevelAdapter.FormViewHolder>() {
        var selectedPosition = childPosition
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormViewHolder {
            val binding = RawExerciseLevelBinding.inflate(parent.context.layoutInflater,parent,false)
            return FormViewHolder(binding)
        }

        override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
            val data: ExerciseLevelDetail = listData[position]
            val context = holder.binding.root.context

            with(holder.binding){
                txtSubTitle.text = (position + 1).toString()
                if (selectedPosition == position){
                    txtSubTitle.setTextColor(ContextCompat.getColor(context, R.color.white))
                    txtSubTitle.setBackgroundResource(R.drawable.circle_primary)
                    themeContent?.resetBtnColor8?.let{
                        val finalColor = CommonUtils.mixTwoColors(ContextCompat.getColor(context,R.color.white), ContextCompat.getColor(context,it), 0.25f)
                        txtSubTitle.backgroundTintList = ColorStateList.valueOf(finalColor)
                    }
                }else {
                    txtSubTitle.setBackgroundResource(R.drawable.circle_primary_border)
                    themeContent?.resetBtnColor8?.let{
                        val finalColor = CommonUtils.mixTwoColors(ContextCompat.getColor(context,R.color.white), ContextCompat.getColor(context,it), 0.25f)
                        txtSubTitle.backgroundTintList = ColorStateList.valueOf(finalColor)
                        txtSubTitle.setTextColor(finalColor)
                    }
                }

            }

            holder.binding.root.setOnClickListener {
                val previousPos = selectedPosition
                selectedPosition = position
                notifyItemChanged(selectedPosition)
                notifyItemChanged(previousPos)
                this.mListener.onExerciseLevelClick(parentPosition, position, data)
            }
        }

        class FormViewHolder(itemBinding: RawExerciseLevelBinding) :
            RecyclerView.ViewHolder(itemBinding.root) {
            var binding: RawExerciseLevelBinding = itemBinding
        }

        override fun getItemCount(): Int {
            return listData.size
        }

    }
}