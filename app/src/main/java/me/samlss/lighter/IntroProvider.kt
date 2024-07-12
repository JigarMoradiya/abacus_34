package me.samlss.lighter

import android.view.View
import com.jigar.me.R
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.utils.AppConstants
import me.samlss.lighter.interfaces.OnLighterListener
import me.samlss.lighter.parameter.Direction
import me.samlss.lighter.parameter.LighterParameter
import me.samlss.lighter.parameter.MarginOffset
import me.samlss.lighter.shape.CircleShape
import me.samlss.lighter.shape.RectShape

object IntroProvider {

    interface IntroCloseClickListener {
        fun onIntroCloseClick()
    }
    fun videoTutorialSingleIntro(lighter: Lighter?, view: View, direction: Int, layoutId: Int, type : String = "rect"){
        try {
            val shape = if (type == "circle"){
                CircleShape()
            }else{
                val corner = view.context.resources.getDimension(R.dimen.home_menu_corner)
                RectShape(corner, corner, corner)
            }
            lighter?.setOnLighterListener(object : OnLighterListener {
                override fun onDismiss() = Unit
                override fun onShow(index: Int) = Unit
            })?.setBackgroundColor(0xB3000000.toInt())
                ?.addHighlight(
                    LighterParameter.Builder()
                        .setHighlightedView(view)
                        .setTipLayoutId(layoutId)
                        .setLighterShape(shape)
                        .setTipViewRelativeDirection(direction)
                        .setTipViewDisplayAnimation(LighterHelper.getScaleAnimation())
                        .setTipViewRelativeOffset(MarginOffset(0, 0, 0, 0))
                        .build()
                )
                ?.show()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
    fun videoTutorialIntro(prefManager : AppPreferencesHelper, lighter: Lighter?,settingView : View, freeModeView: View, videoTutorialView: View, exerciseView: View, examView: View, numberPuzzleView: View, ccmView: View){
        try {
            val corner = freeModeView.context.resources.getDimension(R.dimen.home_menu_corner)
            lighter?.setOnLighterListener(object : OnLighterListener {
                    override fun onDismiss() {
                        prefManager.setCustomParamBoolean(AppConstants.Settings.isHomeTourWatch, true)
                    }
                    override fun onShow(index: Int) = Unit
                })?.setBackgroundColor(0xB3000000.toInt())
                ?.addHighlight(
                    LighterParameter.Builder()
                        .setHighlightedView(freeModeView)
                        .setTipLayoutId(R.layout.layout_tip_free_mode)
                        .setLighterShape(RectShape(corner, corner, corner))
                        .setTipViewRelativeDirection(Direction.RIGHT)
                        .setTipViewDisplayAnimation(LighterHelper.getScaleAnimation())
                        .setTipViewRelativeOffset(MarginOffset(0, 0, 0, 0))
                        .build()
                )
                ?.addHighlight(
                    LighterParameter.Builder()
                        .setHighlightedView(videoTutorialView)
                        .setTipLayoutId(R.layout.layout_tip_video_tutorial)
                        .setLighterShape(RectShape(corner, corner, corner))
                        .setTipViewRelativeDirection(Direction.LEFT)
                        .setTipViewDisplayAnimation(LighterHelper.getScaleAnimation())
                        .setTipViewRelativeOffset(MarginOffset(0, 0, 0, 0))
                        .build()
                )
                ?.addHighlight(
                    LighterParameter.Builder()
                        .setHighlightedView(exerciseView)
                        .setTipLayoutId(R.layout.layout_tip_exercise)
                        .setLighterShape(RectShape(corner, corner, corner))
                        .setTipViewRelativeDirection(Direction.TOP)
                        .setTipViewDisplayAnimation(LighterHelper.getScaleAnimation())
                        .setTipViewRelativeOffset(MarginOffset(0, 0, 0, 0))
                        .build()
                )
                ?.addHighlight(
                    LighterParameter.Builder()
                        .setHighlightedView(examView)
                        .setTipLayoutId(R.layout.layout_tip_exam)
                        .setLighterShape(RectShape(corner, corner, corner))
                        .setTipViewRelativeDirection(Direction.RIGHT)
                        .setTipViewDisplayAnimation(LighterHelper.getScaleAnimation())
                        .setTipViewRelativeOffset(MarginOffset(0, 0, 0, 0))
                        .build()
                )
                ?.addHighlight(
                    LighterParameter.Builder()
                        .setHighlightedView(ccmView)
                        .setTipLayoutId(R.layout.layout_tip_ccm)
                        .setLighterShape(RectShape(corner, corner, corner))
                        .setTipViewRelativeDirection(Direction.TOP)
                        .setTipViewDisplayAnimation(LighterHelper.getScaleAnimation())
                        .setTipViewRelativeOffset(MarginOffset(0, 0, 0, 0))
                        .build()
                )
                ?.addHighlight(
                    LighterParameter.Builder()
                        .setHighlightedView(numberPuzzleView)
                        .setTipLayoutId(R.layout.layout_tip_number_sequence)
                        .setLighterShape(RectShape(corner, corner, corner))
                        .setTipViewRelativeDirection(Direction.TOP)
                        .setTipViewDisplayAnimation(LighterHelper.getScaleAnimation())
                        .setTipViewRelativeOffset(MarginOffset(0, 0, 0, 0))
                        .build()
                )
                ?.addHighlight(
                    LighterParameter.Builder()
                        .setHighlightedView(settingView)
                        .setTipLayoutId(R.layout.layout_tip_setting)
                        .setLighterShape(CircleShape())
                        .setTipViewRelativeDirection(Direction.LEFT)
                        .setTipViewDisplayAnimation(LighterHelper.getScaleAnimation())
                        .setTipViewRelativeOffset(MarginOffset(0, 0, 0, 0))
                        .build()
                )
                ?.show()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun abacusTopBottomBeadsIntro(lighter: Lighter?, viewTop: View, viewBottom : View,listener : IntroCloseClickListener? = null){
        try {
            val corner = viewTop.context.resources.getDimension(R.dimen.home_menu_elevation)
            lighter?.setOnLighterListener(object : OnLighterListener {
                override fun onDismiss() {
                    listener?.onIntroCloseClick()
                }
                override fun onShow(index: Int) = Unit
            })?.setBackgroundColor(0xB3000000.toInt())
                ?.addHighlight(
                    LighterParameter.Builder()
                        .setHighlightedView(viewTop)
                        .setTipLayoutId(R.layout.layout_tip_abacus_top_beads)
                        .setLighterShape(RectShape(corner, corner, corner))
                        .setTipViewRelativeDirection(Direction.BOTTOM)
                        .setTipViewDisplayAnimation(LighterHelper.getScaleAnimation())
                        .setTipViewRelativeOffset(MarginOffset(0, 0, 0, 0))
                        .build()
                )?.addHighlight(
                    LighterParameter.Builder()
                        .setHighlightedView(viewBottom)
                        .setTipLayoutId(R.layout.layout_tip_abacus_bottom_beads)
                        .setLighterShape(RectShape(corner, corner, corner))
                        .setTipViewRelativeDirection(Direction.TOP)
                        .setTipViewDisplayAnimation(LighterHelper.getScaleAnimation())
                        .setTipViewRelativeOffset(MarginOffset(0, 0, 0, 0))
                        .build()
                )
                ?.show()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun abacusRodIntro(lighter: Lighter?, view: View, direction : Int, layoutId : Int, listener : IntroCloseClickListener? = null){
        try {
            val corner = view.context.resources.getDimension(R.dimen.home_menu_elevation)
            lighter?.setOnLighterListener(object : OnLighterListener {
                override fun onDismiss() {
                    listener?.onIntroCloseClick()
                }
                override fun onShow(index: Int) = Unit
            })?.setBackgroundColor(0xB3000000.toInt())
                ?.addHighlight(
                    LighterParameter.Builder()
                        .setHighlightedView(view)
                        .setTipLayoutId(layoutId)
                        .setLighterShape(RectShape(corner, corner, corner))
                        .setTipViewRelativeDirection(direction)
                        .setTipViewDisplayAnimation(LighterHelper.getScaleAnimation())
                        .setTipViewRelativeOffset(MarginOffset(0, 0, 0, 0))
                        .build()
                )
                ?.show()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}