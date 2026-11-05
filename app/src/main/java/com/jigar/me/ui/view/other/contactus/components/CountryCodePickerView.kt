package com.jigar.me.ui.view.other.contactus.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import com.hbb20.CountryCodePicker
import com.jigar.me.R

/**
 * Wraps the legacy `com.hbb20:ccp` widget so it can be used inside Compose.
 * Selection changes flow back through [onCountryChanged] (name code + display name).
 * The widget is a classic view, so its text, arrow and list are painted with the kit colours and font here.
 *
 * @param textSizePx the size of the code text, in pixels.
 */
@Composable
fun CountryCodePickerView(
    initialNameCode: String?,
    contentColor: Color,
    listBackgroundColor: Color,
    textSizePx: Int,
    modifier: Modifier = Modifier,
    onCountryChanged: (nameCode: String, name: String) -> Unit,
) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            CountryCodePicker(ctx).apply {
                setAutoDetectedCountry(true)
                showFlag(false)
                showNameCode(false)
                ResourcesCompat.getFont(ctx, R.font.baloo2_bold)?.let {
                    setTypeFace(it)
                    setDialogTypeFace(it)
                }
                if (!initialNameCode.isNullOrEmpty()) {
                    setCountryForNameCode(initialNameCode)
                }
                setOnCountryChangeListener {
                    onCountryChanged(
                        selectedCountryNameCode.orEmpty(),
                        selectedCountryName.orEmpty()
                    )
                }
            }
        },
        update = { view ->
            view.setContentColor(contentColor.toArgb())
            view.setArrowColor(contentColor.toArgb())
            view.setDialogTextColor(contentColor.toArgb())
            view.setDialogSearchEditTextTintColor(contentColor.toArgb())
            view.setDialogBackgroundColor(listBackgroundColor.toArgb())
            view.setTextSize(textSizePx)
            if (!initialNameCode.isNullOrEmpty() &&
                !view.selectedCountryNameCode.equals(initialNameCode, ignoreCase = true)
            ) {
                view.setCountryForNameCode(initialNameCode)
            }
        }
    )
}
