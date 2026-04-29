package com.jigar.me.ui.view.other.contactus.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.hbb20.CountryCodePicker

/**
 * Wraps the legacy `com.hbb20:ccp` widget so it can be used inside Compose.
 * Selection changes flow back through [onCountryChanged] (name code + display name).
 */
@Composable
fun CountryCodePickerView(
    initialNameCode: String?,
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
            if (!initialNameCode.isNullOrEmpty() &&
                !view.selectedCountryNameCode.equals(initialNameCode, ignoreCase = true)
            ) {
                view.setCountryForNameCode(initialNameCode)
            }
        }
    )
}
