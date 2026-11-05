package com.jigar.me.ui.view.home.screens.settings.components

import android.graphics.LinearGradient
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.graphics.Shader
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.view.ab.AB_SF_TO_VECTOR
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbGlassPanel
import com.jigar.me.ui.view.ab.AbMinScaleText
import com.jigar.me.ui.view.ab.AbSize
import com.jigar.me.ui.view.ab.AbThemeController
import com.jigar.me.ui.view.ab.AbThemeMode
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.base.abacus_base.AbacusTheme
import com.jigar.me.ui.view.base.abacus_base.ColorPresets
import com.jigar.me.ui.view.base.abacus_base.ColorPresets.getMixColorListOfPoligonUnique
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusCanvasGeometry
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.ShapeBeadType
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.drawShapeBead
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.drawableToImageBitmap
import com.jigar.me.ui.view.ab.AbacusFrameStyle
import com.jigar.me.ui.view.home.screens.settings.viewmodels.SettingViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.mixWith

// Display label shown under each swatch in the theme picker.
fun themeDisplayName(id: String): String = when (id) {
    "poligon_rainbow" -> "Rainbow"
    "poligon_duotone" -> "Duo-Tone"
    "poligon_candy" -> "Candy"
    "poligon_heaven_earth" -> "Heaven & Earth"
    "poligon_hex_horizontal" -> "Gemstone"
    "poligon_rectangle" -> "Rectangle"
    "poligon_real_bead" -> "Happy Bead"
    "poligon_circle" -> "Circle"
    "poligon_purple" -> "Purple"
    "poligon_blue" -> "Blue"
    "poligon_skyblue" -> "Sky Blue"
    "poligon_red" -> "Red"
    "poligon_green" -> "Green"
    "poligon_orange" -> "Orange"
    "poligon_cyan" -> "Cyan"
    "poligon_pink" -> "Pink"
    "poligon_yellow" -> "Yellow"
    "poligon_silver" -> "Silver"
    "poligon_brown" -> "Brown"
    "poligon_black" -> "Black"
    else -> id.removePrefix("poligon_").replaceFirstChar { it.uppercase() }
}

/** Solid-colour beads, in the same order in Light and in Dark mode. */
private val firstRow = listOf(
    "poligon_purple", "poligon_blue", "poligon_skyblue", "poligon_red", "poligon_green", "poligon_orange",
    "poligon_cyan", "poligon_pink", "poligon_yellow", "poligon_silver", "poligon_brown", "poligon_black",
)

/** Multi-colour / hand-drawn shape themes. */
private val secondRow = listOf(
    "poligon_rainbow", "poligon_candy", "poligon_heaven_earth", "poligon_duotone",
    "poligon_hex_horizontal", "poligon_rectangle", "poligon_real_bead", "poligon_circle",
)

/** The swatch height: the size these beads always had in the app (40 on a phone, 60 / 80 on the tablets). */
private val swatchHeight: Dp get() = AppDimens.Dimens40

/** iOS `responsiveDimenstion`: 1x phone, 1.5x tablet, 1.7x large tablet. */
private val responsiveScale: Float get() = if (DeviceInfo.isLargeTablet) 1.7f else if (DeviceInfo.isTablet) 1.5f else 1f

/** The bead picker card (left) and the live preview of the chosen theme (right), centred against each other. */
@Composable
fun ThemeSection(
    viewModel: SettingViewModel,
    selectedTheme: String,
    onThemeSelected: (String) -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)
    ) {
        ThemePickerCard(
            selectedTheme = selectedTheme,
            onThemeSelected = onThemeSelected,
            modifier = Modifier.weight(1f)
        )
        ThemePreviewCard(viewModel = viewModel, selectedTheme = selectedTheme)
    }
}

@Composable
private fun ThemePickerCard(
    selectedTheme: String,
    onThemeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor

    AbGlassPanel(modifier) {
        Column(Modifier.padding(top = AppDimens.Dimens12)) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = AppDimens.Dimens16),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)
            ) {
                Text(
                    text = stringResource(R.string.free_abacus_theme),
                    style = AbTypography.headline(s),
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f)
                )
                ModeChip(AbThemeMode.Light, "☀️ Light")
                ModeChip(AbThemeMode.Dark, "🌙 Dark")
            }

            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = AppDimens.Dimens10, horizontal = AppDimens.Dimens16)
                    .height(2.dp)
                    .background(colors.outline)
            )

            Column(verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens2)) {
                // Row 1: plain solid-colour beads
                ThemeRow(
                    themes = firstRow,
                    selectedTheme = selectedTheme,
                    onThemeSelected = onThemeSelected
                )
                // Row 2: multi-colour / hand-drawn shape themes
                ThemeRow(
                    themes = secondRow,
                    selectedTheme = selectedTheme,
                    onThemeSelected = onThemeSelected
                )
            }
        }
    }
}

/** The in-app Light / Dark choice (never the device setting). */
@Composable
private fun ModeChip(mode: AbThemeMode, label: String) {
    AbButton(
        text = label,
        tone = if (AbThemeController.mode == mode) AbTone.Primary else AbTone.Ghost,
        size = AbSize.Compact,
        onClick = { AbThemeController.set(mode) }
    )
}

/** The live preview of the chosen bead theme. */
@Composable
private fun ThemePreviewCard(viewModel: SettingViewModel, selectedTheme: String) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp * s)
    ) {
        AbGlassPanel(padding = 10.dp) {
            AbacusWithDecimalCanvas(
                selectedTheme = selectedTheme,
                screenType = AppConstants.AbacusScreen.screenTypeSettingPreview,
                abacusData = viewModel.abacusCalc,
                numberOfColumns = 3,
                rodMovement = emptyList(),
                showDirectionHint = false,
                isBeadSoundOn = false,
                isDisplayCurrentAbacusInput = false,
                frameStyle = AbacusFrameStyle.Toy,
                onRodMovementChange = { },
                onShowDirectionHintsChange = { },
                onShowHighlighterChange = { },
                onReset = {},
                onNext = {},
            )
        }
        Text(
            text = stringResource(R.string.preview_of_theme),
            style = AbTypography.label(s),
            color = colors.textSecondary
        )
    }
}

@Composable
private fun ThemeRow(
    themes: List<String>,
    selectedTheme: String,
    onThemeSelected: (String) -> Unit
) {
    // Every item of both rows has the same width, so the beads are evenly spaced and both rows have the same gaps. A name
    // that is wider than that (Heaven & Earth) just extends a little past its slot: its neighbours' names are short.
    val slotWidth = swatchHeight * 1.21f
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
        contentPadding = PaddingValues(horizontal = AppDimens.Dimens16)
    ) {
        this.items(themes) { theme ->
            // These 4 render as hand-drawn flat/wide shapes in the real abacus, not the
            // hexagon bitmap -- preview the actual shape instead of a generic hexagon,
            // and give each its own distinct (muted) hue so they're tellable apart.
            val shapeType = when (theme) {
                "poligon_hex_horizontal" -> ShapeBeadType.HexHorizontal
                "poligon_rectangle" -> ShapeBeadType.Rectangle
                "poligon_real_bead" -> ShapeBeadType.RealBead
                "poligon_circle" -> ShapeBeadType.Circle
                else -> null
            }

            val gradientColors =
                if (theme.contains("poligon"))
                    when {
                        theme.contains("rainbow") -> getMixColorListOfPoligonUnique()
                        theme == "poligon_candy" -> ColorPresets.getCandyColorList()
                        theme == "poligon_heaven_earth" -> listOf(ColorPresets.heavenBeadColor, ColorPresets.earthBeadColor)
                        theme == "poligon_duotone" -> ColorPresets.getDuoToneColorList()
                        else -> {
                            val preset = AbacusTheme.colorPreset(theme)
                            val baseColor = preset.abacusTopGradient
                            listOf(baseColor.mixWith(Color.White, 0.40f), baseColor.mixWith(Color.White, 0.10f))
                        }
                    }
                else arrayListOf()

            val context = LocalContext.current
            val beadImage = remember {
                drawableToImageBitmap(context, R.drawable.poligon_gray_light)
            }
            val isSelected = theme == selectedTheme
            Column(
                Modifier.width(slotWidth).padding(bottom = AppDimens.Dimens6),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // the swatch area is as tall as the bead drawn in it, so the name sits right under the bead
                Box(Modifier.height(swatchHeight * 0.7f), contentAlignment = Alignment.Center) {
                if (shapeType != null) {
                    val swatchColor = when (shapeType) {
                        ShapeBeadType.HexHorizontal -> ColorPresets.hex("5D7A99") // muted slate-blue
                        ShapeBeadType.Rectangle -> ColorPresets.hex("8B5A5A") // muted maroon
                        ShapeBeadType.Circle -> ColorPresets.hex("A1887F") // coffee
                        // Real Bead cycles the Candy palette by position, not the
                        // rainbow-by-rod palette -- preview that actual first color.
                        ShapeBeadType.RealBead -> ColorPresets.getCandyColorList().first()
                    }
                    ShapeSwatchCanvas(
                        shapeType = shapeType,
                        baseColor = swatchColor,
                        isSelected = isSelected,
                        onClick = { onThemeSelected(theme) }
                    )
                } else {
                    BeadThemeItemCanvas(
                        beadImage = beadImage,
                        gradientColors = gradientColors,
                        isSelected = isSelected,
                        isPolygon = theme.contains("poligon"),
                        onClick = { onThemeSelected(theme) }
                    )
                }
                }
                ThemeLabel(theme, isSelected)
            }
        }
    }
}

/** The name under a swatch: the black beads are called Pearl while Dark mode is on. */
private fun themeLabelName(theme: String): String =
    (if (AbThemeController.mode == AbThemeMode.Dark) ColorPresets.darkDisplayName(theme) else null) ?: themeDisplayName(theme)

@Composable
private fun ThemeLabel(theme: String, isSelected: Boolean) {
    val colors = LocalAbColors.current
    val name = themeLabelName(theme)
    // a name wider than its slot (Heaven & Earth) shrinks to fit a little past it, always leaving a clear gap to its neighbours
    AbMinScaleText(
        text = name,
        style = AbTypography.fixed(10f * responsiveScale, FontWeight.Bold),
        color = if (isSelected) colors.textPrimary else colors.textSecondary,
        modifier = Modifier.requiredWidth(swatchHeight * 1.21f * 1.14f),
        minScale = 0.7f,
        maxLines = 1,
    )
}

/** The green tick on the chosen swatch: small, in the swatch's top-right corner, so it takes no height of its own. */
@Composable
private fun BoxScope.SelectionMark(topInset: Dp = 0.dp, endInset: Dp = 0.dp) {
    val colors = LocalAbColors.current
    val diameter = swatchHeight * 0.36f
    val radius = diameter / 2
    Box(
        Modifier
            .align(Alignment.TopEnd)
            // a badge centred on the top-right corner of the VISIBLE bead (the swatch box has some empty room around the bead it holds)
            .offset(x = radius * 0.45f - endInset, y = maxOf(0.dp, topInset - radius))
            .size(diameter)
            .background(Color.White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = colors.accentGreen,
            modifier = Modifier.requiredSize(diameter * AB_SF_TO_VECTOR)
        )
    }
}

@Composable
private fun ShapeSwatchCanvas(
    shapeType: ShapeBeadType,
    baseColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(contentAlignment = Alignment.BottomCenter) {
        Box(
            modifier = Modifier
                .height(swatchHeight * 0.7f)
                // Width is derived from the real bead's own aspect ratio (the 9:5
                // beadWidth:beadHeight ratio from AbacusTheme.kt, times drawShapeBead's
                // own 0.90/0.94 fill fractions) instead of a guessed number, so the
                // swatch box matches what's actually drawn on the abacus.
                // matchHeightConstraintsFirst=true: without it, aspectRatio derives
                // height from the (loose) width constraint it gets inside a LazyRow,
                // which grows the swatch taller than its height for a ratio this wide --
                // it must respect our fixed height and size width off of that instead.
                .aspectRatio((9f / 5f) * 0.90f / 0.94f, matchHeightConstraintsFirst = true)
                .clip(RoundedCornerShape(AppDimens.Dimens8))
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            // drawShapeBead fills ~94% of whatever height it's given -- more than the
            // hexagon bitmap swatches (Rainbow, Candy, ...) fill of theirs, which made
            // these visibly bigger despite an identical box. Shrinking the canvas itself
            // (not the box, so the tap target stays the same size as every other swatch)
            // by an equal fraction on both axes -- via fillMaxSize(fraction), not
            // padding -- brings it down to match their visual size without distorting
            // the shape's own width:height ratio.
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Reuses the exact real-bead draw code (drawShapeBead) via a minimal fake
                // geometry sized to this swatch, so the picker always matches the real
                // render -- never a hand-approximated stand-in that can drift from it.
                val fakeGeometry = AbacusCanvasGeometry(
                    columnCentersX = listOf(size.width / 2f),
                    rowTop = listOf(0f),
                    rowBottom = listOf(size.height),
                    beadWidthPx = size.width,
                    beadHeightPx = size.height,
                    beamHeightPx = 0f,
                    columnSpacesPx = 0f,
                    extraSpacePx = 0f
                )
                drawShapeBead(
                    shapeType = shapeType,
                    xCenter = size.width / 2f,
                    beadTop = 0f,
                    geometry = fakeGeometry,
                    baseColor = baseColor,
                    beadIsActive = true
                )
            }
        }

        // the shape fills 90% x 94% of its box: the badge goes on that drawn bead's corner
        if (isSelected) SelectionMark(topInset = swatchHeight * 0.7f * 0.03f, endInset = swatchHeight * 1.21f * 0.05f)
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
    Box(contentAlignment = Alignment.BottomCenter) {
        Box(
            modifier = modifier
                .width(swatchHeight * 1.15f)
                .height(minOf(swatchHeight * 0.7f, swatchHeight * 1.15f * beadImage.height / beadImage.width))
                .clip(RoundedCornerShape(AppDimens.Dimens8))
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick),
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
                    } else {
                        native.drawBitmap(bitmap, null, dstRect, null)
                    }
                }
            }
        }

        if (isSelected) SelectionMark()
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
