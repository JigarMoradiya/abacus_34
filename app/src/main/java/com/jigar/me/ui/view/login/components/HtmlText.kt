package com.jigar.me.ui.view.login.components

import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.appScale

@Composable
fun HtmlText(
    html: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier,
        factory = { TextView(it) },
        update = { tv ->

            tv.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)

            // ✅ Set text size (in SP)
            tv.textSize = 14f * appScale()

            // ✅ Set font family (custom font)
            tv.typeface = ResourcesCompat.getFont(context, R.font.font_regular)

            // Optional: text color
            tv.setTextColor(android.graphics.Color.BLACK)
        }
    )
}