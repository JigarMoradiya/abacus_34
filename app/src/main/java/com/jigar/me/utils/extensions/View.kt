package com.jigar.me.utils.extensions

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.jigar.me.R
import com.jigar.me.utils.Constants
import java.util.regex.Matcher
import java.util.regex.Pattern


val View.res: Resources get() = resources
val View.ctx: Context get() = context

fun View.show() { visibility = View.VISIBLE }
fun View.hide() { visibility = View.GONE }
fun View.invisible() { visibility = View.INVISIBLE }
fun View.toggleVis() { if (visibility==View.VISIBLE){ visibility = View.GONE } else{ visibility = View.VISIBLE } }

inline fun <T : View> T.onClick(crossinline func: T.() -> Unit) {
    setOnClickListener { func() }
}

inline fun <T : View> T.onLongClick(crossinline func: T.() -> Unit) {
    setOnLongClickListener { func(); true }
}

fun View.setBlinkAnimation(){
    val animation: Animation = AlphaAnimation(1F, Constants.EXAM_CLICK_ON_CORRECT_ANSWER_ANIMATION_ALPHA) //to change visibility from visible to invisible
    animation.duration = Constants.EXAM_CLICK_ON_CORRECT_ANSWER_ANIMATION_DURATION //duration for each animation cycle
    animation.interpolator = LinearInterpolator()
    animation.repeatCount = Animation.INFINITE //repeating indefinitely
    animation.repeatMode = Animation.REVERSE //animation will start from end point once ended.
    startAnimation(animation) //to start animation
}
fun View.setExamObjectShakeAnimation(){
    val animShake: Animation = AnimationUtils.loadAnimation(context, R.anim.shake_exam_objects)
    startAnimation(animShake)
}

fun View.setAbacusResetShakeAnimation(isResetAction : Boolean = false){
    if (isResetAction){
        val animShake: Animation = AnimationUtils.loadAnimation(context, R.anim.shake_reset_abacus_action)
        startAnimation(animShake)
    }else{
        val animShake: Animation = AnimationUtils.loadAnimation(context, R.anim.shake_reset_abacus_limit)
        startAnimation(animShake)
    }
}

fun TextInputLayout.markRequiredInRed() {
    hint = buildSpannedString {
        append(hint)
        color(Color.RED) { append(" *") } // Mind the space prefix.
    }
}

fun MaterialTextView.makeSpannable(
    text: String?,
    regex: String?,
    startTag: String,
    endTag: String
): SpannableStringBuilder {
    val sb = StringBuffer()
    val spannable = SpannableStringBuilder()
    val pattern: Pattern = Pattern.compile(regex)
    val matcher: Matcher = pattern.matcher(text)
    while (matcher.find()) {
        sb.setLength(0)
        val group = matcher.group()
        val spanText = group.substring(startTag.length, group.length - endTag.length)
        matcher.appendReplacement(sb, spanText)
        spannable.append(sb.toString())
        val start = spannable.length - spanText.length
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            start,
            spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    sb.setLength(0)
    matcher.appendTail(sb)
    spannable.append(sb.toString())
    setText(spannable)
    return spannable
}

