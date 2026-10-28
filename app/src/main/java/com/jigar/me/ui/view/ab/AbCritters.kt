package com.jigar.me.ui.view.ab

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/** What each Practice level looks like: a character and the colours of its card. */
@Immutable
data class AbLevelLook(val level: Int, val name: String, val light: Color, val deep: Color) {
    companion object {
        private class LookRow(val name: String, val light: Int, val deep: Int)

        private val table = listOf(
            LookRow("Chick", 0xFFE27A, 0xF2A91C),
            LookRow("Bunny", 0xFFB9D8, 0xE8589B),
            LookRow("Kitten", 0xFFC58A, 0xF0801E),
            LookRow("Puppy", 0xA5D3FF, 0x3B8EE8),
            LookRow("Panda", 0x95E6C4, 0x2FB58A),
            LookRow("Fox", 0xFFA796, 0xE8523B),
            LookRow("Owl", 0xC9A8F7, 0x7F4FD6),
            LookRow("Lion", 0xFFD98A, 0xE0881A),
            LookRow("Unicorn", 0xA8E6F2, 0x2DA9C2),
            LookRow("Dragon", 0x9BE8A8, 0x2E9E54),
        )

        /** Level numbers wrap if the server ever sends more than ten. */
        fun of(level: Int): AbLevelLook {
            val l = max(1, level)
            val t = table[(l - 1) % 10]
            return AbLevelLook(l, t.name, abIconHex(t.light), abIconHex(t.deep))
        }
    }
}

/** Draws the character for a level, filling a square frame. */
@Composable
fun AbCritterView(level: Int, modifier: Modifier = Modifier) {
    Canvas(modifier.aspectRatio(1f)) {
        val pen = IconPen(this, min(size.width, size.height) / 100f)
        when ((max(1, level) - 1) % 10) {
            0 -> pen.chick()
            1 -> pen.bunny()
            2 -> pen.kitten()
            3 -> pen.puppy()
            4 -> pen.panda()
            5 -> pen.fox()
            6 -> pen.owl()
            7 -> pen.lion()
            8 -> pen.unicorn()
            else -> pen.dragon()
        }
    }
}

// MARK: Shared face parts

private val CritterInk = abIconHex(0x2B2140)
private val CritterBlush = abIconHex(0xFF7FA0)

private fun IconPen.eyes(y: Float = 52f, gap: Float = 11f, rx: Float = 3.6f, ry: Float = 4.6f, lashes: Boolean = false) {
    for (sx in listOf(-1f, 1f)) {
        val x = 50f + sx * gap
        flat(oval(x, y, rx, ry), CritterInk)
        flat(circle(x - 1.1f, y - 1.6f, 1.5f), Color.White)
        flat(circle(x + 1.1f, y + 1.6f, 0.8f), Color.White.copy(alpha = 0.8f))
        if (lashes) {
            line(x + sx * 3.2f, y - 3.4f, x + sx * 6.4f, y - 5.6f, CritterInk, 1.4f)
        }
    }
}

private fun IconPen.cheeks(y: Float = 62f, gap: Float = 20f, r: Float = 4.6f) {
    for (sx in listOf(-1f, 1f)) flat(oval(50f + sx * gap, y, r, r * 0.8f), CritterBlush.copy(alpha = 0.55f))
}

private fun IconPen.smile(y: Float = 64f, w: Float = 5.5f, tongue: Boolean = false) {
    val p = Path().apply {
        moveTo((50f - w) * u, y * u)
        quadraticTo((50f - w * 0.5f) * u, (y + 5f) * u, 50f * u, (y + 1f) * u)
        quadraticTo((50f + w * 0.5f) * u, (y + 5f) * u, (50f + w) * u, y * u)
    }
    if (tongue) flat(oval(50f, y + 4.6f, 2.8f, 2.6f), CritterBlush)
    strokePath(p, CritterInk, 1.7f, cap = StrokeCap.Round, join = StrokeJoin.Round)
}

private fun IconPen.smallNose(y: Float = 59.5f, c: Color = abIconHex(0x4A2B2B)) {
    flat(oval(50f, y, 3.2f, 2.4f), c)
    flat(oval(49f, y - 0.8f, 1f, 0.6f), Color.White.copy(alpha = 0.7f))
}

// MARK: Level 1: chick

internal fun IconPen.chick() {
    groundShadow(50f, 90f, 26f)
    // wings
    sticker(oval(21f, 62f, 8f, 11f), h(0xFFC93C), lw = 2.2f)
    sticker(oval(79f, 62f, 8f, 11f), h(0xFFC93C), lw = 2.2f)
    // feet
    for (x in listOf(42f, 58f)) {
        sticker(
            poly(x - 5f to 86f, x + 5f to 86f, x + 7f to 91f, x to 89f, x - 7f to 91f),
            h(0xFF9A2E), lw = 1.6f, gloss = false,
        )
    }
    // body / head in one round shape
    sticker(oval(50f, 56f, 32f, 31f), h(0xFFE05A), lw = 3f)
    // tuft
    for ((dx, hh) in listOf(-4f to 9f, 0f to 12f, 4f to 9f)) {
        sticker(oval(50f + dx, 24f - hh * 0.1f, 3f, hh / 2f), h(0xFFE05A), lw = 1.8f, gloss = false)
    }
    eyes(y = 54f, gap = 12f)
    cheeks(y = 64f, gap = 21f)
    // beak
    sticker(poly(44f to 60f, 56f to 60f, 50f to 68f), h(0xFF9A2E), lw = 1.8f, gloss = false)
    twinkle(84f, 20f, 6f)
}

// MARK: Level 2: bunny

internal fun IconPen.bunny() {
    groundShadow(50f, 91f, 28f)
    for ((sx, tilt) in listOf(-1f to -8f, 1f to 8f)) {
        rotated(tilt, 50f + sx * 12f, 36f) { p ->
            p.sticker(p.oval(50f + sx * 13f, 22f, 8.5f, 20f), h(0xF4EEFF), lw = 2.6f, outline = h(0x9B86C9))
            p.flat(p.oval(50f + sx * 13f, 24f, 4f, 14f), h(0xFFB3CF))
        }
    }
    sticker(oval(50f, 62f, 32f, 28f), h(0xF4EEFF), lw = 3f, outline = h(0x9B86C9))
    eyes(y = 58f, gap = 12f)
    cheeks(y = 67f, gap = 20f)
    smallNose(y = 65f, c = h(0xFF7FA0))
    smile(y = 69f, w = 4.5f)
    // whisker dots
    for (sx in listOf(-1f, 1f)) {
        for (dy in listOf(0f, 3f)) flat(circle(50f + sx * 14f, 70f + dy, 0.9f), h(0x9B86C9))
    }
    // little carrot sticker
    sticker(poly(78f to 78f, 90f to 80f, 82f to 94f), h(0xFF8A2E), lw = 1.6f, gloss = false)
    sticker(poly(82f to 77f, 80f to 71f, 86f to 75f), h(0x4CC764), lw = 1.4f, gloss = false)
}

// MARK: Level 3: kitten

internal fun IconPen.kitten() {
    groundShadow(50f, 90f, 28f)
    for (sx in listOf(-1f, 1f)) {
        sticker(poly((50f + sx * 31f) to 52f, (50f + sx * 27f) to 20f, (50f + sx * 9f) to 36f), h(0xFFA64D), lw = 2.6f)
        flat(poly((50f + sx * 27f) to 44f, (50f + sx * 25f) to 28f, (50f + sx * 15f) to 37f), h(0xFFB3CF))
    }
    sticker(oval(50f, 60f, 33f, 29f), h(0xFFA64D), lw = 3f)
    // forehead stripes
    for (dx in listOf(-6f, 0f, 6f)) line(50f + dx, 33f, 50f + dx, if (dx == 0f) 42f else 40f, h(0xC26A10), 2f)
    eyes(y = 56f, gap = 12f, ry = 5f)
    cheeks(y = 66f, gap = 21f)
    // muzzle
    flat(oval(50f, 66f, 8f, 5.4f), h(0xFFE3C2))
    flat(poly(47f to 61.5f, 53f to 61.5f, 50f to 65f), h(0xFF7FA0))
    smile(y = 67f, w = 4f)
    for (sx in listOf(-1f, 1f)) {
        for (dy in listOf(-1.5f, 2.5f)) line(50f + sx * 14f, 64f + dy, 50f + sx * 27f, 62f + dy * 2f, h(0x8A4A0C), 1.2f)
    }
}

// MARK: Level 4: puppy

internal fun IconPen.puppy() {
    groundShadow(50f, 91f, 28f)
    // floppy ears
    for (sx in listOf(-1f, 1f)) {
        rotated(sx * 16f, 50f + sx * 28f, 36f) { p ->
            p.sticker(p.oval(50f + sx * 31f, 46f, 10f, 19f), h(0x8A5A2B), lw = 2.6f)
        }
    }
    sticker(oval(50f, 57f, 31f, 30f), h(0xE0B07A), lw = 3f, outline = h(0x8A5A2B))
    // eye patch
    flat(oval(66f, 52f, 8f, 9f), h(0x9A6A3C).copy(alpha = 0.75f))
    eyes(y = 52f, gap = 12f)
    // muzzle
    sticker(oval(50f, 68f, 13f, 10f), h(0xFFF0D8), lw = 1.8f, outline = h(0xC79A60), gloss = false)
    flat(oval(50f, 62.5f, 5f, 3.6f), h(0x3A2A2A))
    flat(oval(48.5f, 61.5f, 1.4f, 0.9f), Color.White.copy(alpha = 0.75f))
    line(50f, 66f, 50f, 70f, h(0x3A2A2A), 1.4f)
    smile(y = 70f, w = 5f, tongue = true)
    cheeks(y = 64f, gap = 24f, r = 4f)
}

// MARK: Level 5: panda

internal fun IconPen.panda() {
    groundShadow(50f, 91f, 28f)
    for (sx in listOf(-1f, 1f)) sticker(circle(50f + sx * 25f, 28f, 11f), h(0x33323F), lw = 2.4f, outline = h(0x1B1A24))
    sticker(oval(50f, 58f, 33f, 30f), h(0xFFFFFF), lw = 3f, outline = h(0x33323F))
    // eye patches
    for (sx in listOf(-1f, 1f)) {
        rotated(sx * 18f, 50f + sx * 13f, 54f) { p ->
            p.flat(p.oval(50f + sx * 13f, 54f, 7.5f, 10f), h(0x33323F))
        }
    }
    for (sx in listOf(-1f, 1f)) {
        val x = 50f + sx * 13f
        flat(oval(x, 53f, 3.4f, 4.2f), Color.White)
        flat(circle(x + 0.4f, 53.4f, 2.2f), CritterInk)
        flat(circle(x - 0.4f, 52.4f, 0.9f), Color.White)
    }
    smallNose(y = 63f, c = h(0x33323F))
    cheeks(y = 67f, gap = 22f)
    smile(y = 68f, w = 5f)
    // bamboo leaf
    sticker(poly(80f to 82f, 92f to 74f, 88f to 90f), h(0x4CC764), lw = 1.6f, gloss = false)
}

// MARK: Level 6: fox

internal fun IconPen.fox() {
    groundShadow(50f, 90f, 28f)
    for (sx in listOf(-1f, 1f)) {
        sticker(poly((50f + sx * 33f) to 54f, (50f + sx * 30f) to 17f, (50f + sx * 10f) to 36f), h(0xFF8A3D), lw = 2.6f)
        flat(poly((50f + sx * 30f) to 28f, (50f + sx * 29f) to 18f, (50f + sx * 20f) to 25f), h(0x33323F))
        flat(poly((50f + sx * 27f) to 46f, (50f + sx * 25f) to 30f, (50f + sx * 15f) to 38f), h(0xFFE3C2))
    }
    sticker(oval(50f, 60f, 34f, 29f), h(0xFF8A3D), lw = 3f)
    // white muzzle + cheeks
    val lower = Path().apply {
        moveTo(18f * u, 66f * u)
        quadraticTo(32f * u, 55f * u, 50f * u, 51f * u)
        quadraticTo(68f * u, 55f * u, 82f * u, 66f * u)
        quadraticTo(78f * u, 84f * u, 50f * u, 87f * u)
        quadraticTo(22f * u, 84f * u, 18f * u, 66f * u)
    }
    flat(lower, h(0xFFF3E4))
    eyes(y = 54f, gap = 12f, ry = 4.8f)
    flat(oval(50f, 64.5f, 3.6f, 2.8f), h(0x33323F))
    flat(oval(49f, 63.6f, 1.1f, 0.8f), Color.White.copy(alpha = 0.7f))
    smile(y = 69f, w = 4.5f)
}

// MARK: Level 7: owl

internal fun IconPen.owl() {
    groundShadow(50f, 91f, 28f)
    // ear tufts
    for (sx in listOf(-1f, 1f)) {
        sticker(poly((50f + sx * 30f) to 40f, (50f + sx * 26f) to 16f, (50f + sx * 10f) to 31f), h(0x8A5F3A), lw = 2.4f)
    }
    // wings
    for (sx in listOf(-1f, 1f)) sticker(oval(50f + sx * 33f, 66f, 8f, 17f), h(0x8A5F3A), lw = 2.2f)
    sticker(oval(50f, 58f, 31f, 34f), h(0xB98550), lw = 3f, outline = h(0x6B4426))
    // belly chevrons
    flat(oval(50f, 76f, 17f, 15f), h(0xF1D3A4))
    for (y in listOf(70f, 76f, 82f)) {
        val v = Path().apply {
            moveTo(43f * u, (y - 2f) * u)
            lineTo(50f * u, (y + 2f) * u)
            lineTo(57f * u, (y - 2f) * u)
        }
        strokePath(v, h(0xC99A5F), 1.6f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    }
    // big eyes
    for (sx in listOf(-1f, 1f)) {
        val x = 50f + sx * 12.5f
        sticker(circle(x, 50f, 11.5f), h(0xFFFFFF), lw = 2f, outline = h(0x6B4426), gloss = false)
        flat(circle(x + sx * -0.5f, 51f, 6f), CritterInk)
        flat(circle(x - 1.8f, 48.4f, 2.2f), Color.White)
    }
    sticker(poly(45f to 58f, 55f to 58f, 50f to 67f), h(0xFF9A2E), lw = 1.8f, gloss = false)
}

// MARK: Level 8: lion

internal fun IconPen.lion() {
    groundShadow(50f, 91f, 30f)
    // mane: ring of round puffs
    for (i in 0 until 12) {
        val a = i * Math.PI.toFloat() / 6f
        sticker(circle(50f + 33f * cos(a), 55f + 32f * sin(a), 11f), h(0xE58A1F), lw = 2.2f, gloss = false)
    }
    sticker(circle(50f, 55f, 34f), h(0xE58A1F), lw = 2.4f, gloss = false)
    for (sx in listOf(-1f, 1f)) sticker(circle(50f + sx * 21f, 30f, 6.5f), h(0xFFC857), lw = 2f, gloss = false)
    sticker(circle(50f, 57f, 26f), h(0xFFC857), lw = 2.8f, outline = h(0xA8620F))
    eyes(y = 53f, gap = 10.5f)
    cheeks(y = 62f, gap = 17f)
    flat(oval(50f, 65f, 9f, 6.5f), h(0xFFEBC2))
    flat(poly(46.5f to 60.5f, 53.5f to 60.5f, 50f to 64.5f), h(0x8A4A2B))
    smile(y = 66f, w = 4.2f)
}

// MARK: Level 9: unicorn

internal fun IconPen.unicorn() {
    groundShadow(50f, 91f, 28f)
    // rainbow mane puffs on the left
    class Puff(val x: Float, val y: Float, val color: Int)
    val mane = listOf(Puff(20f, 44f, 0xFF8AB8), Puff(16f, 56f, 0xB57CFF), Puff(19f, 68f, 0x45C4FF), Puff(27f, 34f, 0xFFD23F))
    for (m in mane) sticker(circle(m.x, m.y, 9f), h(m.color), lw = 2f, gloss = false)
    // ears
    for (sx in listOf(-1f, 1f)) {
        sticker(
            poly((50f + sx * 20f) to 36f, (50f + sx * 27f) to 18f, (50f + sx * 9f) to 31f),
            h(0xFFFFFF), lw = 2.2f, outline = h(0x8E7BD1),
        )
    }
    sticker(oval(50f, 60f, 31f, 29f), h(0xFFFFFF), lw = 3f, outline = h(0x8E7BD1))
    // horn with twist lines
    val horn = poly(44f to 36f, 50f to 4f, 56f to 36f)
    sticker(horn, h(0xFFD23F), lw = 2.2f, outline = h(0xC98A00))
    for (y in listOf(14f, 22f, 30f)) {
        line(46.5f + (30f - y) * 0.06f, y, 53.5f - (30f - y) * 0.06f, y + 3f, h(0xC98A00), 1.2f)
    }
    eyes(y = 56f, gap = 12f, ry = 5f, lashes = true)
    cheeks(y = 66f, gap = 21f)
    smallNose(y = 66f, c = h(0xFF7FA0))
    smile(y = 69f, w = 4.5f)
    twinkle(80f, 36f, 6f)
}

// MARK: Level 10: crowned dragon

internal fun IconPen.dragon() {
    groundShadow(50f, 91f, 30f)
    // wings
    for (sx in listOf(-1f, 1f)) {
        sticker(
            poly((50f + sx * 26f) to 56f, (50f + sx * 46f) to 26f, (50f + sx * 44f) to 62f, (50f + sx * 34f) to 70f),
            h(0xFF8A3D), lw = 2.4f,
        )
    }
    // horns + spikes
    for (sx in listOf(-1f, 1f)) {
        sticker(
            poly((50f + sx * 18f) to 36f, (50f + sx * 26f) to 14f, (50f + sx * 10f) to 32f),
            h(0xFFF0D0), lw = 2f, outline = h(0xB59A5A), gloss = false,
        )
    }
    sticker(oval(50f, 60f, 32f, 30f), h(0x5FD68A), lw = 3f, outline = h(0x1F8A50))
    // belly-coloured snout
    sticker(oval(50f, 68f, 17f, 12f), h(0xC9F5D5), lw = 2f, outline = h(0x1F8A50), gloss = false)
    flat(oval(44f, 66f, 1.8f, 1.4f), h(0x1F8A50))
    flat(oval(56f, 66f, 1.8f, 1.4f), h(0x1F8A50))
    eyes(y = 52f, gap = 12f, ry = 5f)
    cheeks(y = 63f, gap = 24f, r = 4f)
    smile(y = 71f, w = 6f, tongue = false)
    // crown
    val crown = poly(36f to 34f, 36f to 20f, 43f to 27f, 50f to 14f, 57f to 27f, 64f to 20f, 64f to 34f)
    sticker(crown, h(0xFFD23F), lw = 2.4f, outline = h(0xC98A00))
    flat(circle(43f, 31f, 2f), h(0xE23A3A))
    flat(circle(50f, 31f, 2f), h(0x45C4FF))
    flat(circle(57f, 31f, 2f), h(0xE23A3A))
    twinkle(14f, 24f, 6f)
}
