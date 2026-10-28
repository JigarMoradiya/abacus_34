package com.jigar.me.ui.view.home.screens.abacus_practice.set_list.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.data.model.dbtable.abacus_all_data.DisplayPages
import com.jigar.me.data.model.dbtable.abacus_all_data.Set
import com.jigar.me.ui.view.ab.AbCritterView
import com.jigar.me.ui.view.ab.AbLevelLook
import com.jigar.me.ui.view.ab.AbShrinkText
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.home.theme.AppDimens
import kotlin.math.floor
import kotlin.math.max

private val PremiumGold = Color(0xFFFFB800)

/**
 * One page of a level as a card: the level's character, the page name and what it covers, then the page's sets as round
 * stage buttons. A locked page (premium) is dimmed a little and a tap anywhere on it calls [onLockedTapped].
 */
@Composable
fun PageItem(
    page: DisplayPages,
    allSets: List<Set>,
    level: Int,
    isLocked: Boolean = false,
    onSetClick: (Set) -> Unit,
    onSetLongClick: (Set) -> Unit,
    onLockedTapped: () -> Unit
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val look = AbLevelLook.of(level)
    val sets = remember(page, allSets) {
        allSets.filter { it.page_id == page.id }
    }
    val shape = RoundedCornerShape(24.dp * s)
    val edge = 6.dp * s
    val rim = look.deep.copy(alpha = if (colors.isDark) 0.7f else 0.45f)

    Box(Modifier.fillMaxWidth().padding(AppDimens.Dimens4)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = edge)
                .alpha(if (isLocked) 0.92f else 1f)
                .clip(shape)
                .drawBehind {
                    drawRect(colors.bgSecondary)
                    val lw = 3.dp.toPx()
                    drawRoundRect(
                        rim, topLeft = Offset(lw / 2f, lw / 2f), size = Size(size.width - lw, size.height - lw),
                        cornerRadius = CornerRadius(24.dp.toPx() * s - lw / 2f), style = Stroke(lw)
                    )
                }
        ) {
            PageHeader(page, look, level, isLocked)

            SetGrid(
                sets = sets,
                isLocked = isLocked,
                onSetClick = onSetClick,
                onSetLongClick = onSetLongClick,
                modifier = Modifier.padding(start = 12.dp * s, end = 12.dp * s, bottom = 8.dp * s)
            )
        }

        // Invisible tap target for locked cards
        if (isLocked) {
            Box(
                Modifier
                    .matchParentSize()
                    .padding(bottom = edge)
                    .clip(shape)
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onLockedTapped() }
            )
        }
    }
}

/** Header: the level's character, the page name and what it covers. */
@Composable
private fun PageHeader(
    page: DisplayPages,
    look: AbLevelLook,
    level: Int,
    isLocked: Boolean
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(look.light.copy(alpha = if (colors.isDark) 0.22f else 0.38f), look.light.copy(alpha = 0f))
                )
            )
            .padding(horizontal = 12.dp * s, vertical = 6.dp * s),
        horizontalArrangement = Arrangement.spacedBy(10.dp * s),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val glow = look.deep.copy(alpha = 0.35f)
        Box(
            modifier = Modifier
                .size(44.dp * s)
                .shadow(3.dp * s, CircleShape, ambientColor = glow, spotColor = glow)
                .background(Brush.verticalGradient(listOf(look.light, look.deep)), CircleShape)
                .border(2.5.dp, Color.White.copy(alpha = 0.95f), CircleShape)
        ) {
            AbCritterView(
                level,
                Modifier
                    .fillMaxSize()
                    .padding(2.dp * s)
                    .then(if (isLocked) Modifier.desaturated(0.2f) else Modifier)
            )
        }

        // Baloo's line box is tall, so the title gets a tight box and the subtitle pulls up under it.
        Column(Modifier.weight(1f)) {
            Box(Modifier.fillMaxWidth().height(18.dp * s * 1.1f), contentAlignment = Alignment.CenterStart) {
                AbShrinkText(
                    text = page.name,
                    style = AbTypography.fixed(18f * s, FontWeight.ExtraBold),
                    color = if (isLocked) colors.textSecondary else colors.textPrimary,
                    modifier = Modifier.wrapContentHeight(unbounded = true),
                    minScale = 0.7f,
                    maxLines = 1,
                )
            }
            if (!page.description.isNullOrEmpty()) {
                AbShrinkText(
                    text = page.description,
                    style = AbTypography.fixed(12.5f * s, FontWeight.SemiBold),
                    color = colors.textSecondary,
                    modifier = Modifier.fillMaxWidth().offset(y = -(2.dp * s)),
                    minScale = 0.8f,
                    maxLines = 2,
                )
            }
        }

        if (isLocked) {
            Box(
                Modifier
                    .size(38.dp * s)
                    .background(PremiumGold.copy(alpha = 0.18f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Crown(Modifier.size(20.dp * s, 16.dp * s))
            }
        } else if (!page.is_active) {
            Image(
                painter = painterResource(R.drawable.ic_disable),
                contentDescription = null,
                modifier = Modifier.size(AppDimens.Dimens14)
            )
        }
    }
}

/** The sets of a page, as many to a row as fit: every cell the same width, the button centred in it. */
@Composable
private fun SetGrid(
    sets: List<Set>,
    isLocked: Boolean,
    onSetClick: (Set) -> Unit,
    onSetLongClick: (Set) -> Unit,
    modifier: Modifier = Modifier
) {
    val s = LocalAbScale.current.factor
    val size = 42.dp * s
    val gap = 8.dp * s
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val columns = max(1, floor((maxWidth + gap) / (size + gap + gap)).toInt())
        Column(verticalArrangement = Arrangement.spacedBy(gap)) {
            sets.chunked(columns).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gap)) {
                    row.forEach { set ->
                        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            SetItem(
                                set = set,
                                size = size,
                                onClick = { onSetClick(set) },
                                onLongClick = { onSetLongClick(set) },
                                isLocked = isLocked
                            )
                        }
                    }
                    repeat(columns - row.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

/** The page's premium mark: a small flat gold crown. */
@Composable
private fun Crown(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val body = Path().apply {
            moveTo(w * 0.04f, h * 0.22f)
            lineTo(w * 0.28f, h * 0.5f)
            lineTo(w * 0.5f, h * 0.04f)
            lineTo(w * 0.72f, h * 0.5f)
            lineTo(w * 0.96f, h * 0.22f)
            lineTo(w * 0.86f, h * 0.74f)
            lineTo(w * 0.14f, h * 0.74f)
            close()
        }
        val round = Stroke(width = w * 0.08f, join = StrokeJoin.Round)
        drawPath(body, PremiumGold)
        drawPath(body, PremiumGold, style = round)
        drawRoundRect(
            PremiumGold, topLeft = Offset(w * 0.14f, h * 0.82f), size = Size(w * 0.72f, h * 0.16f),
            cornerRadius = CornerRadius(h * 0.08f)
        )
    }
}

/** Draws the content with its colours drained to [saturation] (1 = untouched, 0 = grey): a locked page's character. */
private fun Modifier.desaturated(saturation: Float): Modifier = drawWithContent {
    val paint = Paint().apply {
        colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(saturation) })
    }
    drawIntoCanvas { canvas ->
        canvas.saveLayer(Rect(0f, 0f, size.width, size.height), paint)
        drawContent()
        canvas.restore()
    }
}
