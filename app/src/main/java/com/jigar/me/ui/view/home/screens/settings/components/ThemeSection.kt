package com.jigar.me.ui.view.home.screens.settings.components

import com.jigar.me.ui.view.home.theme.AppDimens

import android.graphics.LinearGradient
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.graphics.Shader
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorCoffee
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorLightYellow
import com.jigar.me.ui.view.base.abacus_base.AbacusTheme
import com.jigar.me.ui.view.base.abacus_base.ColorPresets.getMixColorListOfPoligonUnique
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.drawableToImageBitmap
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorPrimary
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.screens.settings.viewmodels.SettingViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.mixWith

@Composable
fun ThemeSection(
    viewModel: SettingViewModel,
    selectedTheme: String,
    onThemeSelected: (String) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically,horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)) {
        Card(shape = RoundedCornerShape(AppDimens.Dimens16),
            colors = CardDefaults.cardColors(
                containerColor = ColorLightYellow
            ), elevation = CardDefaults.cardElevation(defaultElevation = AppDimens.Dimens3), modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            Column(modifier = Modifier.padding(vertical = AppDimens.Dimens6)) {
                Text(
                    text = stringResource(R.string.free_abacus_theme),
                    modifier = Modifier.padding(horizontal = AppDimens.Dimens12),
                    style = MaterialTheme.typography.bodyLarge.scaled().copy(
                        fontWeight = FontWeight.Medium, fontFamily = FontFamily(Font(R.font.font_medium)), color = ColorCoffee
                    )
                )

                LazyRow(
                    modifier = Modifier.padding(horizontal = AppDimens.Dimens10, vertical = AppDimens.Dimens4)
                        .clip(RoundedCornerShape(AppDimens.Dimens12))
                        .background(Color(0xFFE8F5E9)),
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
                    contentPadding = PaddingValues(horizontal = AppDimens.Dimens12)
                ) {
                    this.items(
                        listOf(
                            "poligon_rainbow",
                            "poligon_purple", "poligon_blue", "poligon_skyblue",
                            "poligon_red", "poligon_green", "poligon_orange", "poligon_cyan",
                            "poligon_pink", "poligon_yellow", "poligon_silver", "poligon_brown",
                            "poligon_black"
                        )
                    ) { theme ->
                        val gradientColors =
                            if (theme.contains("poligon"))
                                if (theme.contains("rainbow")){
                                    getMixColorListOfPoligonUnique()
                                }else{
                                    val preset = AbacusTheme.colorPreset(theme)
                                    val baseColor = preset.abacusTopGradient
                                    listOf(baseColor.mixWith(Color.White, 0.40f), baseColor.mixWith(Color.White,0.10f))
                                }
                            else arrayListOf()


                        val context = LocalContext.current
                        val beadImage = remember {
                            drawableToImageBitmap(context, R.drawable.poligon_gray_light)
                        }
                        BeadThemeItemCanvas(
                            beadImage = beadImage,
                            gradientColors = gradientColors,
                            isSelected = theme == selectedTheme,
                            isPolygon = theme.contains("poligon"),
                            onClick = { onThemeSelected(theme) }
                        )
                    }
                }
            }
        }

        AbacusWithDecimalCanvas(
            selectedTheme = selectedTheme,
            screenType = AppConstants.AbacusScreen.screenTypeSettingPreview,
            abacusData = viewModel.abacusCalc,
            numberOfColumns = 3,
            rodMovement = emptyList(),
            showDirectionHint = false,
            isBeadSoundOn = false,
            isDisplayCurrentAbacusInput = false,
            onRodMovementChange = { },
            onShowDirectionHintsChange = { },
            onShowHighlighterChange = { },
            onReset = {},
            onNext = {},
        )
    }
}

@Composable
fun BeadThemeItemCanvas(
    modifier: Modifier = Modifier,
    beadImage: ImageBitmap,
    gradientColors: List<Color>,
    isSelected: Boolean,
    isPolygon: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(AppDimens.Dimens40)
            .aspectRatio(1.15f)
            .clip(RoundedCornerShape(AppDimens.Dimens8))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {

            val bitmap = beadImage.asAndroidBitmap()

            val dstRect = calculateFitRect(
                canvasWidth = size.width,
                canvasHeight = size.height,
                bitmapWidth = bitmap.width,
                bitmapHeight = bitmap.height
            )

            drawIntoCanvas { canvas ->
                val native = canvas.nativeCanvas
                if (isPolygon) {
                    //  Save isolated layer
                    val layerId = native.saveLayer(dstRect, null)

                    // Draw bitmap (FIT, centered)
                    native.drawBitmap(bitmap, null, dstRect, null)

                    // Apply gradient mask
                    val shader = LinearGradient(
                        /* x0 = */ 0f,
                        /* y0 = */ dstRect.top,
                        /* x1 = */ 0f,
                        /* y1 = */ dstRect.bottom,
                        /* colors = */ gradientColors.map { it.toArgb() }.toIntArray(),
                        /* positions = */ null, // or provide custom stops
                        Shader.TileMode.CLAMP
                    )


                    val paint = android.graphics.Paint().apply {
                        isAntiAlias = true
                        this.shader = shader
                        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                    }

                    native.drawRect(dstRect, paint)

                    // Restore layer
                    native.restoreToCount(layerId)
                }else{
                    native.drawBitmap(bitmap, null, dstRect, null)
                }
            }
        }


        // ✅ Selection mark
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color.Green,
                modifier = Modifier
                    .size(AppDimens.Dimens12)
                    .align(Alignment.BottomCenter)
                    .offset(y = (-2).dp)
                    .background(Color.White, CircleShape)
            )
        }
    }
}

fun calculateFitRect(
    canvasWidth: Float,
    canvasHeight: Float,
    bitmapWidth: Int,
    bitmapHeight: Int
): RectF {
    val bitmapRatio = bitmapWidth.toFloat() / bitmapHeight
    val canvasRatio = canvasWidth / canvasHeight

    val drawWidth: Float
    val drawHeight: Float

    if (bitmapRatio > canvasRatio) {
        // Fit width
        drawWidth = canvasWidth
        drawHeight = canvasWidth / bitmapRatio
    } else {
        // Fit height
        drawHeight = canvasHeight
        drawWidth = canvasHeight * bitmapRatio
    }

    val left = (canvasWidth - drawWidth) / 2f
    val top = (canvasHeight - drawHeight) / 2f

    return RectF(
        left,
        top,
        left + drawWidth,
        top + drawHeight
    )
}

