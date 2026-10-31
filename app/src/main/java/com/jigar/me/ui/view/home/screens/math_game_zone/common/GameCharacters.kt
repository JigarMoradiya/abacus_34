package com.jigar.me.ui.view.home.screens.math_game_zone.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import com.jigar.me.ui.view.ab.abDarkened
import kotlin.math.abs
import kotlin.math.sin

// Draws and animates the Math Game Zone's cartoon friends from the vector art in GameCharacterArt.kt (one source for iOS and
// Android). They bob, blink, wag their tails and wave while they wait, and jump with open arms and a big smile when the answer
// is right. Each game has its own friend (see GameCharacter.of). View-level only.

private val Ink = Color(0xFF2A2235)

private fun artFor(character: GameCharacter): CharArt = when (character) {
    GameCharacter.Fox -> CharArtLibrary.fox
    GameCharacter.Owl -> CharArtLibrary.owl
    GameCharacter.Parrot -> CharArtLibrary.parrot
    GameCharacter.Monkey -> CharArtLibrary.monkey
    GameCharacter.Unicorn -> CharArtLibrary.unicorn
    GameCharacter.Elephant -> CharArtLibrary.elephant
    GameCharacter.Panda -> CharArtLibrary.panda
    GameCharacter.Frog -> CharArtLibrary.frog
    GameCharacter.Giraffe -> CharArtLibrary.giraffe
    GameCharacter.Rabbit -> CharArtLibrary.rabbit
    GameCharacter.Dog -> CharArtLibrary.dog
}

private fun argb(v: Long): Color = Color(0xFF000000L or v)

@Composable
fun CartoonFriend(
    character: GameCharacter,
    size: Dp,
    t: Float,
    celebrate: Boolean,
    wink: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val art = artFor(character)
    val bob = sin(t * 2.2f) * 0.016f
    val jump = if (celebrate) -abs(sin(t * 9f)) * 0.08f else 0f
    Canvas(
        modifier
            .size(size)
            .graphicsLayer { translationY = (bob + jump) * this.size.height },
    ) {
        drawFriend(art, t, celebrate, wink)
    }
}

private fun DrawScope.drawFriend(a: CharArt, t: Float, celebrate: Boolean, wink: Boolean) {
    // units of the 100 x 100 art grid, with a small inset so tails never touch the edge
    val u = this.size.minDimension / 104f
    withTransform({
        translate(2f * u, 2f * u)
        scale(u, u, Offset.Zero)
    }) {
        paint(a.back)

        val wag = sin(t * 4f) * 8f + (if (celebrate) 6f else 0f)
        withTransform({ rotate(wag, a.tailPivot) }) { paint(a.tail) }

        paint(a.body)

        // arms: hang a little outward and sway; the right one waves; both go up and out when cheering
        val cheerAngle = 125f + sin(t * 10f) * 8f
        val leftAngle = if (celebrate) cheerAngle else 18f
        val rightAngle = if (celebrate) cheerAngle else 18f + sin(t * 3.2f) * 12f
        if (a.arm.isNotEmpty()) {
            withTransform({ rotate(leftAngle, a.armPivot) }) { paint(a.arm) }
            withTransform({
                translate(100f, 0f)
                scale(-1f, 1f, Offset.Zero)
                rotate(rightAngle, a.armPivot)
            }) { paint(a.arm) }
        }

        paint(a.head)
        paint(a.face)

        cheeks(a)
        eyes(a, t, wink)
        paint(a.nose)
        mouth(a, celebrate)
        paint(a.front)
    }
}

private fun DrawScope.paint(layers: List<CharLayer>) {
    for (l in layers) {
        l.fill?.let { drawPath(l.path, argb(it), style = Fill) }
        l.stroke?.let {
            drawPath(l.path, argb(it), style = Stroke(width = l.sw, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }
}

private fun DrawScope.oval(cx: Float, cy: Float, rx: Float, ry: Float, color: Color) {
    drawOval(color, topLeft = Offset(cx - rx, cy - ry), size = Size(rx * 2f, ry * 2f))
}

private fun DrawScope.ovalOutline(cx: Float, cy: Float, rx: Float, ry: Float, color: Color, width: Float) {
    drawOval(color, topLeft = Offset(cx - rx, cy - ry), size = Size(rx * 2f, ry * 2f), style = Stroke(width = width))
}

private fun DrawScope.cheeks(a: CharArt) {
    val pink = Color(0xFFFF8FAF).copy(alpha = 0.75f)
    oval(50f - a.cheekDx, a.cheekY, 5.5f, 4.2f, pink)
    oval(50f + a.cheekDx, a.cheekY, 5.5f, 4.2f, pink)
}

private fun DrawScope.eyes(a: CharArt, t: Float, wink: Boolean) {
    // blinks briefly every few seconds; a tap winks the right eye
    val blink = (t % 3.6f) < 0.13f
    a.eyes.forEachIndexed { i, p ->
        val closed = blink || (wink && i == 1)
        withTransform({
            translate(p.x, p.y)
            scale(1f, if (closed) 0.1f else 1f, Offset.Zero)
        }) {
            val s = a.eyeScale
            a.eyeRing?.let { ring ->
                val r = 12.5f * a.eyeRingScale
                oval(0f, 0f, r, r, argb(ring))
                ovalOutline(0f, 0f, r, r, argb(ring).abDarkened(0.38f), 1.4f)
            }
            oval(0f, 0f, 8f * s, 9.5f * s, Color.White)
            ovalOutline(0f, 0f, 8f * s, 9.5f * s, Color(0xFF777080), 0.5f)
            oval(0f, 1f * s, 4.8f * s, 6.3f * s, Ink)
            oval(0.9f * s, -0.6f * s, 1.9f * s, 1.9f * s, Color.White)
        }
    }
}

private fun DrawScope.mouth(a: CharArt, celebrate: Boolean) {
    val y = a.mouthY ?: return
    val w = a.mouthW
    if (celebrate) {
        val open = Path().apply {
            moveTo(50f - w / 2f, y - 2f)
            lineTo(50f + w / 2f, y - 2f)
            cubicTo(50f + w / 2f, y + w * 0.5f, 50f - w / 2f, y + w * 0.5f, 50f - w / 2f, y - 2f)
            close()
        }
        drawPath(open, Color(0xFF7A1F3A), style = Fill)
        oval(50f, y + w * 0.28f, w * 0.3f, w * 0.17f, Color(0xFFFF7A9A))
    } else {
        val smile = Path().apply {
            moveTo(50f - w / 2f, y)
            quadraticTo(50f, y + w * 0.35f, 50f + w / 2f, y)
        }
        drawPath(smile, Ink, style = Stroke(width = 2f, cap = StrokeCap.Round))
    }
}
