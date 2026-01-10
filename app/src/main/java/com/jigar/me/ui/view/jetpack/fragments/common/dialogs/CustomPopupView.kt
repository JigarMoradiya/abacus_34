package com.jigar.me.ui.view.jetpack.fragments.common.dialogs

import android.text.Html
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.toColorInt
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.core.presentation.components.PrimaryButton
import com.jigar.me.ui.view.jetpack.core.presentation.theme.ColorGreen

@Composable
fun CustomPopupView(
    title: String? = null,
    description: String? = null,
    notes: String? = null,
    position: Alignment = Alignment.Center,
    positiveButtonText: String? = null,
    negativeButtonText: String? = null,
    onPositiveTapped: (() -> Unit)? = null,
    onNegativeTapped: (() -> Unit)? = null,
    widthMultiplier: Float = 0.5f,
    icon: Int? = null // Drawable resource ID
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = position
    ) {
        // Calculate popup width based on screen width
        val popupWidth = LocalConfiguration.current.screenWidthDp * widthMultiplier

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .width(popupWidth.dp)
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(horizontal = dimensionResource(id = R.dimen.activity_padding20), vertical = dimensionResource(id = R.dimen.activity_padding4))
        ) {
            // 🔹 Optional Icon
            if (icon != null) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier
                        .height(dimensionResource(id = R.dimen.popup_icon_height))
                        .padding(top = dimensionResource(id = R.dimen.activity_padding12))
                )
            }

            // 🔹 Title
            if (!title.isNullOrEmpty()) {
                Text(
                    text = title,
                    fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                    fontSize = dimensionResource(id = R.dimen.textSize24).value.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = dimensionResource(id = R.dimen.activity_padding16))
                )
            }

            // 🔹 Description
            if (!description.isNullOrEmpty()) {
                AndroidView(
                    factory = { context ->
                        TextView(context).apply {
                            textSize = 15f
                            setTextColor("#000000".toColorInt())
                            textAlignment = TextView.TEXT_ALIGNMENT_CENTER

                            typeface = ResourcesCompat.getFont(context, R.font.font_medium)
                        }
                    },
                    update = { textView ->
                        textView.text = Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY)
                    },
                    modifier = Modifier.padding(top = dimensionResource(id = R.dimen.activity_padding4))
                )
            }

            if (!notes.isNullOrEmpty()) {
                Text(
                    text = notes,
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily(Font(R.font.font_semibold))),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = dimensionResource(id = R.dimen.activity_padding16))
                )
            }

            // 🔹 Buttons
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = dimensionResource(id = R.dimen.activity_padding8))) {

                // ✅ Positive Button
                if (!positiveButtonText.isNullOrEmpty() && onPositiveTapped != null) {
                    PrimaryButton(text = positiveButtonText, modifier = Modifier.fillMaxWidth(), color = ColorGreen, onClick = onPositiveTapped)
                }

                // ✅ Negative Button
                if (!negativeButtonText.isNullOrEmpty() && onNegativeTapped != null) {
                    TextButton(
                        onClick = onNegativeTapped,
                    ) {
                        Text(
                            text = negativeButtonText,
                            fontFamily = FontFamily(Font(R.font.font_medium)),
                            fontSize = dimensionResource(id = R.dimen.textSizeRegular).value.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}
