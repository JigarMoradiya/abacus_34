package com.jigar.me.ui.view.home.screens.math_game_zone.common

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path

// GENERATED from the character vector source (one source for iOS and Android): the Math Game Zone friends as layers of curved
// paths on a 100 x 100 grid. Do not edit by hand; GameCharacters.kt draws and animates them.

class CharLayer(d: String, val fill: Long?, val stroke: Long?, val sw: Float, val cap: Boolean) {
    /** Parsed once: "M x y L x y C ... Q ... Z" (absolute commands) on the 100 x 100 grid. */
    val path: Path = parseCharPath(d)
}

fun parseCharPath(d: String): Path {
    val p = Path()
    val nums = ArrayList<Float>()
    var cmd = 'M'
    fun flush() {
        when (cmd) {
            'M' -> if (nums.size >= 2) p.moveTo(nums[0], nums[1])
            'L' -> if (nums.size >= 2) p.lineTo(nums[0], nums[1])
            'C' -> if (nums.size >= 6) p.cubicTo(nums[0], nums[1], nums[2], nums[3], nums[4], nums[5])
            'Q' -> if (nums.size >= 4) p.quadraticTo(nums[0], nums[1], nums[2], nums[3])
            'Z' -> p.close()
        }
        nums.clear()
    }
    val token = StringBuilder()
    fun pushToken() {
        if (token.isNotEmpty()) { token.toString().toFloatOrNull()?.let { nums.add(it) }; token.setLength(0) }
    }
    for (ch in d) {
        when {
            ch in "MLCQZ" -> { pushToken(); flush(); cmd = ch; if (ch == 'Z') flush() }
            ch == ' ' -> pushToken()
            else -> token.append(ch)
        }
    }
    pushToken(); flush()
    return p
}

class CharArt(
    val back: List<CharLayer>,
    val tail: List<CharLayer>, val tailPivot: Offset,
    val body: List<CharLayer>,
    val arm: List<CharLayer>, val armPivot: Offset,
    val head: List<CharLayer>,
    val face: List<CharLayer>,
    val eyes: List<Offset>, val eyeScale: Float, val eyeRing: Long?, val eyeRingScale: Float,
    val nose: List<CharLayer>,
    val mouthY: Float?, val mouthW: Float, val cheekY: Float, val cheekDx: Float,
    val front: List<CharLayer>,
)

object CharArtLibrary {
    val fox = CharArt(
    back = emptyList(),
    tail = listOf(
        CharLayer("M64 92 C82 100 100 88 97 60 C96 52 93 47 89 44 C88 58 80 68 66 74 Z", 0xF28C28L, 0x965618L, 1.5f, false),
        CharLayer("M97 60 C96 52 93 47 89 44 C88 51 86 57 83 62 C90 67 95 65 97 60 Z", 0xFFFFFFL, 0x9E9E9EL, 1.5f, false)
    ), tailPivot = Offset(66f, 82f),
    body = listOf(
        CharLayer("M26 74 C26 60 36 56 50 56 C64 56 74 60 74 74 C74 88 64 96 50 96 C36 96 26 88 26 74 Z", 0xF28C28L, 0x965618L, 1.5f, false),
        CharLayer("M64.0 79.0 C64.0 86.7 57.7 93.0 50.0 93.0 C42.3 93.0 36.0 86.7 36.0 79.0 C36.0 71.3 42.3 65.0 50.0 65.0 C57.7 65.0 64.0 71.3 64.0 79.0 Z", 0xFFF1DCL, null, 1.5f, false),
        CharLayer("M47.0 95.0 C47.0 97.8 42.5 100.0 37.0 100.0 C31.5 100.0 27.0 97.8 27.0 95.0 C27.0 92.2 31.5 90.0 37.0 90.0 C42.5 90.0 47.0 92.2 47.0 95.0 Z", 0x4A2A1AL, 0x2D1A10L, 1.5f, false),
        CharLayer("M73.0 95.0 C73.0 97.8 68.5 100.0 63.0 100.0 C57.5 100.0 53.0 97.8 53.0 95.0 C53.0 92.2 57.5 90.0 63.0 90.0 C68.5 90.0 73.0 92.2 73.0 95.0 Z", 0x4A2A1AL, 0x2D1A10L, 1.5f, false)
    ),
    arm = listOf(
        CharLayer("M25.0 58.0 C17.0 63.0 15.0 74.0 17.0 83.0 C18.0 88.0 26.0 89.0 28.0 84.0 C31.0 76.0 33.0 68.0 35.0 61.0 Z", 0xF28C28L, 0x965618L, 1.5f, false),
        CharLayer("M28.2 84.0 C28.7 86.7 26.5 89.3 23.4 89.8 C20.2 90.4 17.3 88.7 16.8 86.0 C16.3 83.3 18.5 80.7 21.6 80.2 C24.8 79.6 27.7 81.3 28.2 84.0 Z", 0x4A2A1AL, 0x2D1A10L, 1.5f, false),
        CharLayer("M20.6 85.4 L20.9 88.6", null, 0x2C190FL, 0.9f, true),
        CharLayer("M23.9 85.4 L24.2 88.6", null, 0x2C190FL, 0.9f, true)
    ), armPivot = Offset(28f, 63f),
    head = listOf(
        CharLayer("M22 38 C18 25 19 13 24 5 C33 9 40 15 44 22 Z", 0xF28C28L, 0x965618L, 1.5f, false),
        CharLayer("M 78.0 38.0C 82.0 25.0 81.0 13.0 76.0 5.0C 67.0 9.0 60.0 15.0 56.0 22.0Z", 0xF28C28L, 0x965618L, 1.5f, false),
        CharLayer("M25 30 C24 22 25 15 27 11 C31 13 35 17 38 22 Z", 0x4A2A1AL, null, 1.5f, false),
        CharLayer("M 75.0 30.0C 76.0 22.0 75.0 15.0 73.0 11.0C 69.0 13.0 65.0 17.0 62.0 22.0Z", 0x4A2A1AL, null, 1.5f, false),
        CharLayer("M15 47 C15 29 31 17 50 17 C69 17 85 29 85 47 C85 53 82 58 76 62 C68 68 59 70 50 70 C41 70 32 68 24 62 C18 58 15 53 15 47 Z", 0xF28C28L, 0x965618L, 1.5f, false)
    ),
    face = listOf(
        CharLayer("M50 71 C38 71 27 63 21 53 C29 56 37 51 44 53 C47 54 49 54 50 54 C51 54 53 54 56 53 C63 51 71 56 79 53 C73 63 62 71 50 71 Z", 0xFFFFFFL, null, 1.5f, false)
    ),
    eyes = listOf(Offset(36f, 42f), Offset(64f, 42f)), eyeScale = 0.85f, eyeRing = null, eyeRingScale = 1.0f,
    nose = listOf(
        CharLayer("M54.4 56.0 C54.4 57.8 52.4 59.2 50.0 59.2 C47.6 59.2 45.6 57.8 45.6 56.0 C45.6 54.2 47.6 52.8 50.0 52.8 C52.4 52.8 54.4 54.2 54.4 56.0 Z", 0x2A2235L, null, 1.5f, false)
    ),
    mouthY = 62f, mouthW = 14f, cheekY = 52f, cheekDx = 26f,
    front = emptyList()
    )

    val owl = CharArt(
    back = emptyList(),
    tail = emptyList(), tailPivot = Offset(66f, 84f),
    body = listOf(
        CharLayer("M26 72 C26 59 36 55 50 55 C64 55 74 59 74 72 C74 87 64 96 50 96 C36 96 26 87 26 72 Z", 0x8D5B3AL, 0x573823L, 1.5f, false),
        CharLayer("M65.0 77.0 C65.0 85.8 58.3 93.0 50.0 93.0 C41.7 93.0 35.0 85.8 35.0 77.0 C35.0 68.2 41.7 61.0 50.0 61.0 C58.3 61.0 65.0 68.2 65.0 77.0 Z", 0xF1D9B5L, null, 1.5f, false),
        CharLayer("M42 70 Q45 74 48 70", null, 0x5E3A22L, 1.3f, true),
        CharLayer("M52 70 Q55 74 58 70", null, 0x5E3A22L, 1.3f, true),
        CharLayer("M44 78 Q47 82 50 78", null, 0x5E3A22L, 1.3f, true),
        CharLayer("M50 78 Q53 82 56 78", null, 0x5E3A22L, 1.3f, true),
        CharLayer("M46 86 Q50 90 54 86", null, 0x5E3A22L, 1.3f, true),
        CharLayer("M48.0 96.0 C48.0 98.0 44.4 99.6 40.0 99.6 C35.6 99.6 32.0 98.0 32.0 96.0 C32.0 94.0 35.6 92.4 40.0 92.4 C44.4 92.4 48.0 94.0 48.0 96.0 Z", 0xFFB020L, 0x9E6D13L, 1.5f, false),
        CharLayer("M68.0 96.0 C68.0 98.0 64.4 99.6 60.0 99.6 C55.6 99.6 52.0 98.0 52.0 96.0 C52.0 94.0 55.6 92.4 60.0 92.4 C64.4 92.4 68.0 94.0 68.0 96.0 Z", 0xFFB020L, 0x9E6D13L, 1.5f, false)
    ),
    arm = listOf(
        CharLayer("M30 60 C20 62 13 76 16 91 C18 96 22 94 23 91 C24 96 28 95 29 91 C30 94 34 92 33 87 C35 77 34 67 32 60 Z", 0x5E3A22L, 0x3A2315L, 1.5f, false),
        CharLayer("M21 80 L21 88", null, 0x4A2A18L, 1.0f, true),
        CharLayer("M26 80 L26 90", null, 0x4A2A18L, 1.0f, true)
    ), armPivot = Offset(28f, 63f),
    head = listOf(
        CharLayer("M20 34 C19 22 22 12 27 6 C34 8 41 14 44 20 Z", 0x8D5B3AL, 0x573823L, 1.5f, false),
        CharLayer("M 80.0 34.0C 81.0 22.0 78.0 12.0 73.0 6.0C 66.0 8.0 59.0 14.0 56.0 20.0Z", 0x8D5B3AL, 0x573823L, 1.5f, false),
        CharLayer("M16 42 C16 24 31 12 50 12 C69 12 84 24 84 42 C84 58 70 68 50 68 C30 68 16 58 16 42 Z", 0x8D5B3AL, 0x573823L, 1.5f, false)
    ),
    face = listOf(
        CharLayer("M51.5 42.0 C51.5 50.6 44.6 57.5 36.0 57.5 C27.4 57.5 20.5 50.6 20.5 42.0 C20.5 33.4 27.4 26.5 36.0 26.5 C44.6 26.5 51.5 33.4 51.5 42.0 Z", 0xFAEBD0L, null, 1.5f, false),
        CharLayer("M79.5 42.0 C79.5 50.6 72.6 57.5 64.0 57.5 C55.4 57.5 48.5 50.6 48.5 42.0 C48.5 33.4 55.4 26.5 64.0 26.5 C72.6 26.5 79.5 33.4 79.5 42.0 Z", 0xFAEBD0L, null, 1.5f, false)
    ),
    eyes = listOf(Offset(36f, 42f), Offset(64f, 42f)), eyeScale = 1.05f, eyeRing = 0xFFE3A3L, eyeRingScale = 0.9f,
    nose = listOf(
        CharLayer("M45 49 C47 47 53 47 55 49 L50 60 Z", 0xFFB020L, 0x9E6D13L, 1.5f, false)
    ),
    mouthY = null, mouthW = 14f, cheekY = 56f, cheekDx = 28f,
    front = emptyList()
    )

    val parrot = CharArt(
    back = listOf(
        CharLayer("M45.0 94.7 C44.3 99.6 41.5 103.3 38.7 102.9 C36.0 102.5 34.4 98.2 35.0 93.3 C35.7 88.4 38.5 84.7 41.3 85.1 C44.0 85.5 45.6 89.8 45.0 94.7 Z", 0xF04E4EL, 0x943030L, 1.5f, false),
        CharLayer("M55.0 96.0 C55.0 101.0 52.8 105.0 50.0 105.0 C47.2 105.0 45.0 101.0 45.0 96.0 C45.0 91.0 47.2 87.0 50.0 87.0 C52.8 87.0 55.0 91.0 55.0 96.0 Z", 0x2F7BE0L, 0x1D4C8AL, 1.5f, false),
        CharLayer("M65.0 93.3 C65.6 98.2 64.0 102.5 61.3 102.9 C58.5 103.3 55.7 99.6 55.0 94.7 C54.4 89.8 56.0 85.5 58.7 85.1 C61.5 84.7 64.3 88.4 65.0 93.3 Z", 0xFFC21AL, 0x9E7810L, 1.5f, false)
    ),
    tail = emptyList(), tailPivot = Offset(66f, 84f),
    body = listOf(
        CharLayer("M27 74 C27 60 37 56 50 56 C63 56 73 60 73 74 C73 87 64 95 50 95 C36 95 27 87 27 74 Z", 0x37B35AL, 0x226E37L, 1.5f, false),
        CharLayer("M64.0 79.0 C64.0 86.7 57.7 93.0 50.0 93.0 C42.3 93.0 36.0 86.7 36.0 79.0 C36.0 71.3 42.3 65.0 50.0 65.0 C57.7 65.0 64.0 71.3 64.0 79.0 Z", 0xFFE08AL, null, 1.5f, false),
        CharLayer("M48.0 95.0 C48.0 97.4 44.0 99.4 39.0 99.4 C34.0 99.4 30.0 97.4 30.0 95.0 C30.0 92.6 34.0 90.6 39.0 90.6 C44.0 90.6 48.0 92.6 48.0 95.0 Z", 0xFF9F1CL, 0x9E6211L, 1.5f, false),
        CharLayer("M70.0 95.0 C70.0 97.4 66.0 99.4 61.0 99.4 C56.0 99.4 52.0 97.4 52.0 95.0 C52.0 92.6 56.0 90.6 61.0 90.6 C66.0 90.6 70.0 92.6 70.0 95.0 Z", 0xFF9F1CL, 0x9E6211L, 1.5f, false)
    ),
    arm = listOf(
        CharLayer("M25.0 58.0 C17.0 63.0 15.0 74.0 17.0 83.0 C18.0 88.0 26.0 89.0 28.0 84.0 C31.0 76.0 33.0 68.0 35.0 61.0 Z", 0x37B35AL, 0x226E37L, 1.5f, false),
        CharLayer("M28.2 84.0 C28.7 86.7 26.5 89.3 23.4 89.8 C20.2 90.4 17.3 88.7 16.8 86.0 C16.3 83.3 18.5 80.7 21.6 80.2 C24.8 79.6 27.7 81.3 28.2 84.0 Z", 0x2F7BE0L, 0x1D4C8AL, 1.5f, false)
    ), armPivot = Offset(28f, 63f),
    head = listOf(
        CharLayer("M43.9 12.4 C46.0 17.5 45.9 22.4 43.7 23.3 C41.6 24.1 38.2 20.7 36.1 15.6 C34.0 10.5 34.1 5.6 36.3 4.7 C38.4 3.9 41.8 7.3 43.9 12.4 Z", 0xF04E4EL, 0x943030L, 1.5f, false),
        CharLayer("M54.2 11.0 C54.2 17.1 52.3 22.0 50.0 22.0 C47.7 22.0 45.8 17.1 45.8 11.0 C45.8 4.9 47.7 0.0 50.0 0.0 C52.3 0.0 54.2 4.9 54.2 11.0 Z", 0xFFC21AL, 0x9E7810L, 1.5f, false),
        CharLayer("M63.9 15.6 C61.8 20.7 58.4 24.1 56.3 23.3 C54.1 22.4 54.0 17.5 56.1 12.4 C58.2 7.3 61.6 3.9 63.7 4.7 C65.9 5.6 66.0 10.5 63.9 15.6 Z", 0x2F7BE0L, 0x1D4C8AL, 1.5f, false),
        CharLayer("M81.0 40.0 C81.0 55.5 67.1 68.0 50.0 68.0 C32.9 68.0 19.0 55.5 19.0 40.0 C19.0 24.5 32.9 12.0 50.0 12.0 C67.1 12.0 81.0 24.5 81.0 40.0 Z", 0x37B35AL, 0x226E37L, 1.5f, false)
    ),
    face = listOf(
        CharLayer("M49.5 41.0 C49.5 47.9 43.9 53.5 37.0 53.5 C30.1 53.5 24.5 47.9 24.5 41.0 C24.5 34.1 30.1 28.5 37.0 28.5 C43.9 28.5 49.5 34.1 49.5 41.0 Z", 0xFFFFFFL, null, 1.5f, false),
        CharLayer("M75.5 41.0 C75.5 47.9 69.9 53.5 63.0 53.5 C56.1 53.5 50.5 47.9 50.5 41.0 C50.5 34.1 56.1 28.5 63.0 28.5 C69.9 28.5 75.5 34.1 75.5 41.0 Z", 0xFFFFFFL, null, 1.5f, false)
    ),
    eyes = listOf(Offset(37f, 41f), Offset(63f, 41f)), eyeScale = 0.85f, eyeRing = null, eyeRingScale = 1.0f,
    nose = listOf(
        CharLayer("M41 46 C41 40 59 40 59 46 C59 55 55 63 50 66 C45 63 41 55 41 46 Z", 0xFFB020L, 0x9E6D13L, 1.5f, false),
        CharLayer("M44 50 C48 52 52 52 56 50", 0xE8890AL, null, 1.5f, false),
        CharLayer("M48.2 46.0 C48.2 46.9 47.7 47.6 47.0 47.6 C46.3 47.6 45.8 46.9 45.8 46.0 C45.8 45.1 46.3 44.4 47.0 44.4 C47.7 44.4 48.2 45.1 48.2 46.0 Z", 0x8A4B12L, null, 1.5f, false),
        CharLayer("M54.2 46.0 C54.2 46.9 53.7 47.6 53.0 47.6 C52.3 47.6 51.8 46.9 51.8 46.0 C51.8 45.1 52.3 44.4 53.0 44.4 C53.7 44.4 54.2 45.1 54.2 46.0 Z", 0x8A4B12L, null, 1.5f, false)
    ),
    mouthY = null, mouthW = 14f, cheekY = 55f, cheekDx = 30f,
    front = emptyList()
    )

    val monkey = CharArt(
    back = emptyList(),
    tail = listOf(
        CharLayer("M63 90 C82 98 96 84 89 70 C85 62 92 58 91 50", null, 0x5B3A1AL, 6.4f, true),
        CharLayer("M63 90 C82 98 96 84 89 70 C85 62 92 58 91 50", null, 0x8A5A2BL, 4.0f, true)
    ), tailPivot = Offset(64f, 88f),
    body = listOf(
        CharLayer("M26 74 C26 60 36 56 50 56 C64 56 74 60 74 74 C74 88 64 96 50 96 C36 96 26 88 26 74 Z", 0x8A5A2BL, 0x55371AL, 1.5f, false),
        CharLayer("M64.0 79.0 C64.0 86.7 57.7 93.0 50.0 93.0 C42.3 93.0 36.0 86.7 36.0 79.0 C36.0 71.3 42.3 65.0 50.0 65.0 C57.7 65.0 64.0 71.3 64.0 79.0 Z", 0xF3D3A0L, null, 1.5f, false),
        CharLayer("M47.0 95.0 C47.0 97.8 42.5 100.0 37.0 100.0 C31.5 100.0 27.0 97.8 27.0 95.0 C27.0 92.2 31.5 90.0 37.0 90.0 C42.5 90.0 47.0 92.2 47.0 95.0 Z", 0xF3D3A0L, 0x968263L, 1.5f, false),
        CharLayer("M73.0 95.0 C73.0 97.8 68.5 100.0 63.0 100.0 C57.5 100.0 53.0 97.8 53.0 95.0 C53.0 92.2 57.5 90.0 63.0 90.0 C68.5 90.0 73.0 92.2 73.0 95.0 Z", 0xF3D3A0L, 0x968263L, 1.5f, false)
    ),
    arm = listOf(
        CharLayer("M25.0 58.0 C17.0 63.0 15.0 74.0 17.0 83.0 C18.0 88.0 26.0 89.0 28.0 84.0 C31.0 76.0 33.0 68.0 35.0 61.0 Z", 0x8A5A2BL, 0x55371AL, 1.5f, false),
        CharLayer("M28.2 84.0 C28.7 86.7 26.5 89.3 23.4 89.8 C20.2 90.4 17.3 88.7 16.8 86.0 C16.3 83.3 18.5 80.7 21.6 80.2 C24.8 79.6 27.7 81.3 28.2 84.0 Z", 0xF3D3A0L, 0x968263L, 1.5f, false),
        CharLayer("M20.6 85.4 L20.9 88.6", null, 0x917E60L, 0.9f, true),
        CharLayer("M23.9 85.4 L24.2 88.6", null, 0x917E60L, 0.9f, true)
    ), armPivot = Offset(28f, 63f),
    head = listOf(
        CharLayer("M28.0 42.0 C28.0 48.1 23.1 53.0 17.0 53.0 C10.9 53.0 6.0 48.1 6.0 42.0 C6.0 35.9 10.9 31.0 17.0 31.0 C23.1 31.0 28.0 35.9 28.0 42.0 Z", 0x8A5A2BL, 0x55371AL, 1.5f, false),
        CharLayer("M23.0 42.0 C23.0 45.3 20.3 48.0 17.0 48.0 C13.7 48.0 11.0 45.3 11.0 42.0 C11.0 38.7 13.7 36.0 17.0 36.0 C20.3 36.0 23.0 38.7 23.0 42.0 Z", 0xFFB3A7L, null, 1.5f, false),
        CharLayer("M94.0 42.0 C94.0 48.1 89.1 53.0 83.0 53.0 C76.9 53.0 72.0 48.1 72.0 42.0 C72.0 35.9 76.9 31.0 83.0 31.0 C89.1 31.0 94.0 35.9 94.0 42.0 Z", 0x8A5A2BL, 0x55371AL, 1.5f, false),
        CharLayer("M89.0 42.0 C89.0 45.3 86.3 48.0 83.0 48.0 C79.7 48.0 77.0 45.3 77.0 42.0 C77.0 38.7 79.7 36.0 83.0 36.0 C86.3 36.0 89.0 38.7 89.0 42.0 Z", 0xFFB3A7L, null, 1.5f, false),
        CharLayer("M81.0 40.0 C81.0 55.5 67.1 68.0 50.0 68.0 C32.9 68.0 19.0 55.5 19.0 40.0 C19.0 24.5 32.9 12.0 50.0 12.0 C67.1 12.0 81.0 24.5 81.0 40.0 Z", 0x8A5A2BL, 0x55371AL, 1.5f, false)
    ),
    face = listOf(
        CharLayer("M50 33 C44 27 30 29 30 41 C30 54 40 64 50 64 C60 64 70 54 70 41 C70 29 56 27 50 33 Z", 0xF3D3A0L, null, 1.5f, false),
        CharLayer("M64.0 54.0 C64.0 59.2 57.7 63.5 50.0 63.5 C42.3 63.5 36.0 59.2 36.0 54.0 C36.0 48.8 42.3 44.5 50.0 44.5 C57.7 44.5 64.0 48.8 64.0 54.0 Z", 0xF8E3BCL, null, 1.5f, false)
    ),
    eyes = listOf(Offset(40f, 41f), Offset(60f, 41f)), eyeScale = 0.8f, eyeRing = null, eyeRingScale = 1.0f,
    nose = listOf(
        CharLayer("M48.6 51.0 C48.6 51.7 47.9 52.3 47.0 52.3 C46.1 52.3 45.4 51.7 45.4 51.0 C45.4 50.3 46.1 49.7 47.0 49.7 C47.9 49.7 48.6 50.3 48.6 51.0 Z", 0x5B3A1AL, null, 1.5f, false),
        CharLayer("M54.6 51.0 C54.6 51.7 53.9 52.3 53.0 52.3 C52.1 52.3 51.4 51.7 51.4 51.0 C51.4 50.3 52.1 49.7 53.0 49.7 C53.9 49.7 54.6 50.3 54.6 51.0 Z", 0x5B3A1AL, null, 1.5f, false)
    ),
    mouthY = 59f, mouthW = 15f, cheekY = 54f, cheekDx = 22f,
    front = emptyList()
    )

    val unicorn = CharArt(
    back = listOf(
        CharLayer("M30 20 C10 22 4 48 12 70 C18 62 24 54 28 44 Z", 0xC77DFFL, 0x7B4D9EL, 1.5f, false),
        CharLayer("M26 26 C14 32 12 48 18 60 C22 52 24 44 28 38 Z", 0xFF8FD0L, null, 1.5f, false)
    ),
    tail = listOf(
        CharLayer("M66 84 C82 78 96 86 90 98 C84 94 74 96 66 94 Z", 0xC77DFFL, 0x7B4D9EL, 1.5f, false),
        CharLayer("M68 86 C80 84 88 88 86 94 C80 92 74 92 68 92 Z", 0xFF8FD0L, null, 1.5f, false)
    ), tailPivot = Offset(66f, 88f),
    body = listOf(
        CharLayer("M26 74 C26 60 36 56 50 56 C64 56 74 60 74 74 C74 88 64 96 50 96 C36 96 26 88 26 74 Z", 0xF4F0FFL, 0x97949EL, 1.5f, false),
        CharLayer("M64.0 79.0 C64.0 86.7 57.7 93.0 50.0 93.0 C42.3 93.0 36.0 86.7 36.0 79.0 C36.0 71.3 42.3 65.0 50.0 65.0 C57.7 65.0 64.0 71.3 64.0 79.0 Z", 0xFFE3F3L, null, 1.5f, false),
        CharLayer("M47.0 95.0 C47.0 97.8 42.5 100.0 37.0 100.0 C31.5 100.0 27.0 97.8 27.0 95.0 C27.0 92.2 31.5 90.0 37.0 90.0 C42.5 90.0 47.0 92.2 47.0 95.0 Z", 0xC9B6F2L, 0x7C7096L, 1.5f, false),
        CharLayer("M73.0 95.0 C73.0 97.8 68.5 100.0 63.0 100.0 C57.5 100.0 53.0 97.8 53.0 95.0 C53.0 92.2 57.5 90.0 63.0 90.0 C68.5 90.0 73.0 92.2 73.0 95.0 Z", 0xC9B6F2L, 0x7C7096L, 1.5f, false)
    ),
    arm = listOf(
        CharLayer("M25.0 58.0 C17.0 63.0 15.0 74.0 17.0 83.0 C18.0 88.0 26.0 89.0 28.0 84.0 C31.0 76.0 33.0 68.0 35.0 61.0 Z", 0xF4F0FFL, 0x97949EL, 1.5f, false),
        CharLayer("M28.2 84.0 C28.7 86.7 26.5 89.3 23.4 89.8 C20.2 90.4 17.3 88.7 16.8 86.0 C16.3 83.3 18.5 80.7 21.6 80.2 C24.8 79.6 27.7 81.3 28.2 84.0 Z", 0xC9B6F2L, 0x7C7096L, 1.5f, false),
        CharLayer("M20.6 85.4 L20.9 88.6", null, 0x786D91L, 0.9f, true),
        CharLayer("M23.9 85.4 L24.2 88.6", null, 0x786D91L, 0.9f, true)
    ), armPivot = Offset(28f, 63f),
    head = listOf(
        CharLayer("M24 30 C20 20 22 12 27 6 C34 10 40 16 43 22 Z", 0xFFFFFFL, 0x9E9E9EL, 1.5f, false),
        CharLayer("M 76.0 30.0C 80.0 20.0 78.0 12.0 73.0 6.0C 66.0 10.0 60.0 16.0 57.0 22.0Z", 0xFFFFFFL, 0x9E9E9EL, 1.5f, false),
        CharLayer("M27 26 C25 19 26 14 28 10 C32 13 36 16 38 20 Z", 0xFFB3D9L, null, 1.5f, false),
        CharLayer("M 73.0 26.0C 75.0 19.0 74.0 14.0 72.0 10.0C 68.0 13.0 64.0 16.0 62.0 20.0Z", 0xFFB3D9L, null, 1.5f, false),
        CharLayer("M82.0 40.0 C82.0 55.5 67.7 68.0 50.0 68.0 C32.3 68.0 18.0 55.5 18.0 40.0 C18.0 24.5 32.3 12.0 50.0 12.0 C67.7 12.0 82.0 24.5 82.0 40.0 Z", 0xFFFFFFL, 0x9E9E9EL, 1.5f, false)
    ),
    face = listOf(
        CharLayer("M61.5 56.0 C61.5 60.1 56.4 63.5 50.0 63.5 C43.6 63.5 38.5 60.1 38.5 56.0 C38.5 51.9 43.6 48.5 50.0 48.5 C56.4 48.5 61.5 51.9 61.5 56.0 Z", 0xFFB3D9L, null, 1.5f, false)
    ),
    eyes = listOf(Offset(37f, 41f), Offset(63f, 41f)), eyeScale = 0.9f, eyeRing = null, eyeRingScale = 1.0f,
    nose = listOf(
        CharLayer("M48.3 55.0 C48.3 55.6 47.7 56.1 47.0 56.1 C46.3 56.1 45.7 55.6 45.7 55.0 C45.7 54.4 46.3 53.9 47.0 53.9 C47.7 53.9 48.3 54.4 48.3 55.0 Z", 0xC2569BL, null, 1.5f, false),
        CharLayer("M54.3 55.0 C54.3 55.6 53.7 56.1 53.0 56.1 C52.3 56.1 51.7 55.6 51.7 55.0 C51.7 54.4 52.3 53.9 53.0 53.9 C53.7 53.9 54.3 54.4 54.3 55.0 Z", 0xC2569BL, null, 1.5f, false)
    ),
    mouthY = 61f, mouthW = 14f, cheekY = 50f, cheekDx = 24f,
    front = listOf(
        CharLayer("M36 18 C38 27 47 26 50 20 C53 27 62 26 64 18 C58 10 42 10 36 18 Z", 0xFF8FD0L, 0x9E5880L, 1.5f, false),
        CharLayer("M45.5 17 L50 3 L54.5 17 Z", 0xFFC21AL, 0x9E7810L, 1.5f, false),
        CharLayer("M47 12 L53 13.5", null, 0xE89A00L, 1.1f, true),
        CharLayer("M48 8 L52 9", null, 0xE89A00L, 1.1f, true)
    )
    )

    val elephant = CharArt(
    back = listOf(
        CharLayer("M26 24 C6 16 0 44 8 62 C14 72 26 66 30 54 Z", 0x8FB3E8L, 0x586E8FL, 1.5f, false),
        CharLayer("M24 32 C12 28 9 46 14 58 C18 62 24 58 26 52 Z", 0xF7B7C8L, null, 1.5f, false),
        CharLayer("M 74.0 24.0C 94.0 16.0 100.0 44.0 92.0 62.0C 86.0 72.0 74.0 66.0 70.0 54.0Z", 0x8FB3E8L, 0x586E8FL, 1.5f, false),
        CharLayer("M 76.0 32.0C 88.0 28.0 91.0 46.0 86.0 58.0C 82.0 62.0 76.0 58.0 74.0 52.0Z", 0xF7B7C8L, null, 1.5f, false)
    ),
    tail = listOf(
        CharLayer("M72 84 C80 82 86 88 84 95", null, 0x5E85C2L, 2.4f, true),
        CharLayer("M87.2 96.0 C87.2 98.1 85.8 99.8 84.0 99.8 C82.2 99.8 80.8 98.1 80.8 96.0 C80.8 93.9 82.2 92.2 84.0 92.2 C85.8 92.2 87.2 93.9 87.2 96.0 Z", 0x5E85C2L, 0x3A5278L, 1.5f, false)
    ), tailPivot = Offset(72f, 84f),
    body = listOf(
        CharLayer("M26 74 C26 60 36 56 50 56 C64 56 74 60 74 74 C74 88 64 96 50 96 C36 96 26 88 26 74 Z", 0x8FB3E8L, 0x586E8FL, 1.5f, false),
        CharLayer("M64.0 79.0 C64.0 86.7 57.7 93.0 50.0 93.0 C42.3 93.0 36.0 86.7 36.0 79.0 C36.0 71.3 42.3 65.0 50.0 65.0 C57.7 65.0 64.0 71.3 64.0 79.0 Z", 0xD6E6FBL, null, 1.5f, false),
        CharLayer("M47.0 95.0 C47.0 97.8 42.5 100.0 37.0 100.0 C31.5 100.0 27.0 97.8 27.0 95.0 C27.0 92.2 31.5 90.0 37.0 90.0 C42.5 90.0 47.0 92.2 47.0 95.0 Z", 0x5E85C2L, 0x3A5278L, 1.5f, false),
        CharLayer("M73.0 95.0 C73.0 97.8 68.5 100.0 63.0 100.0 C57.5 100.0 53.0 97.8 53.0 95.0 C53.0 92.2 57.5 90.0 63.0 90.0 C68.5 90.0 73.0 92.2 73.0 95.0 Z", 0x5E85C2L, 0x3A5278L, 1.5f, false)
    ),
    arm = listOf(
        CharLayer("M25.0 58.0 C17.0 63.0 15.0 74.0 17.0 83.0 C18.0 88.0 26.0 89.0 28.0 84.0 C31.0 76.0 33.0 68.0 35.0 61.0 Z", 0x8FB3E8L, 0x586E8FL, 1.5f, false),
        CharLayer("M28.2 84.0 C28.7 86.7 26.5 89.3 23.4 89.8 C20.2 90.4 17.3 88.7 16.8 86.0 C16.3 83.3 18.5 80.7 21.6 80.2 C24.8 79.6 27.7 81.3 28.2 84.0 Z", 0x5E85C2L, 0x3A5278L, 1.5f, false),
        CharLayer("M20.6 85.4 L20.9 88.6", null, 0x384F74L, 0.9f, true),
        CharLayer("M23.9 85.4 L24.2 88.6", null, 0x384F74L, 0.9f, true)
    ), armPivot = Offset(28f, 63f),
    head = listOf(
        CharLayer("M80.0 38.0 C80.0 52.9 66.6 65.0 50.0 65.0 C33.4 65.0 20.0 52.9 20.0 38.0 C20.0 23.1 33.4 11.0 50.0 11.0 C66.6 11.0 80.0 23.1 80.0 38.0 Z", 0x8FB3E8L, 0x586E8FL, 1.5f, false)
    ),
    face = emptyList(),
    eyes = listOf(Offset(38f, 37f), Offset(62f, 37f)), eyeScale = 0.85f, eyeRing = null, eyeRingScale = 1.0f,
    nose = listOf(
        CharLayer("M42 48 C41 60 40 70 44 78 C48 85 58 82 58 74 C54 76 52 70 54 61 C55 55 58 50 58 48 Z", 0x8FB3E8L, 0x586E8FL, 1.5f, false),
        CharLayer("M43 56 L49 57", null, 0x5E85C2L, 1.2f, true),
        CharLayer("M42 62 L48 63", null, 0x5E85C2L, 1.2f, true),
        CharLayer("M42 68 L48 69", null, 0x5E85C2L, 1.2f, true),
        CharLayer("M39 56 C33 61 33 69 37 74 C37 68 39 62 42 57 Z", 0xFFFFFFL, 0x9E9E9EL, 1.5f, false),
        CharLayer("M 61.0 56.0C 67.0 61.0 67.0 69.0 63.0 74.0C 63.0 68.0 61.0 62.0 58.0 57.0Z", 0xFFFFFFL, 0x9E9E9EL, 1.5f, false)
    ),
    mouthY = null, mouthW = 14f, cheekY = 50f, cheekDx = 26f,
    front = emptyList()
    )

    val panda = CharArt(
    back = listOf(
        CharLayer("M34.5 19.0 C34.5 25.4 29.4 30.5 23.0 30.5 C16.6 30.5 11.5 25.4 11.5 19.0 C11.5 12.6 16.6 7.5 23.0 7.5 C29.4 7.5 34.5 12.6 34.5 19.0 Z", 0x2A2235L, 0x1A1520L, 1.5f, false),
        CharLayer("M88.5 19.0 C88.5 25.4 83.4 30.5 77.0 30.5 C70.6 30.5 65.5 25.4 65.5 19.0 C65.5 12.6 70.6 7.5 77.0 7.5 C83.4 7.5 88.5 12.6 88.5 19.0 Z", 0x2A2235L, 0x1A1520L, 1.5f, false)
    ),
    tail = listOf(
        CharLayer("M75.0 90.0 C75.0 92.8 72.8 95.0 70.0 95.0 C67.2 95.0 65.0 92.8 65.0 90.0 C65.0 87.2 67.2 85.0 70.0 85.0 C72.8 85.0 75.0 87.2 75.0 90.0 Z", 0xFFFFFFL, 0x9E9E9EL, 1.5f, false)
    ), tailPivot = Offset(70f, 90f),
    body = listOf(
        CharLayer("M26 74 C26 60 36 56 50 56 C64 56 74 60 74 74 C74 88 64 96 50 96 C36 96 26 88 26 74 Z", 0xFFFFFFL, 0x9E9E9EL, 1.5f, false),
        CharLayer("M64.0 80.0 C64.0 87.2 57.7 93.0 50.0 93.0 C42.3 93.0 36.0 87.2 36.0 80.0 C36.0 72.8 42.3 67.0 50.0 67.0 C57.7 67.0 64.0 72.8 64.0 80.0 Z", 0xF0F0F5L, null, 1.5f, false),
        CharLayer("M47.0 95.0 C47.0 97.8 42.5 100.0 37.0 100.0 C31.5 100.0 27.0 97.8 27.0 95.0 C27.0 92.2 31.5 90.0 37.0 90.0 C42.5 90.0 47.0 92.2 47.0 95.0 Z", 0x2A2235L, 0x1A1520L, 1.5f, false),
        CharLayer("M73.0 95.0 C73.0 97.8 68.5 100.0 63.0 100.0 C57.5 100.0 53.0 97.8 53.0 95.0 C53.0 92.2 57.5 90.0 63.0 90.0 C68.5 90.0 73.0 92.2 73.0 95.0 Z", 0x2A2235L, 0x1A1520L, 1.5f, false)
    ),
    arm = listOf(
        CharLayer("M25.0 58.0 C17.0 63.0 15.0 74.0 17.0 83.0 C18.0 88.0 26.0 89.0 28.0 84.0 C31.0 76.0 33.0 68.0 35.0 61.0 Z", 0x2A2235L, 0x1A1520L, 1.5f, false),
        CharLayer("M28.2 84.0 C28.7 86.7 26.5 89.3 23.4 89.8 C20.2 90.4 17.3 88.7 16.8 86.0 C16.3 83.3 18.5 80.7 21.6 80.2 C24.8 79.6 27.7 81.3 28.2 84.0 Z", 0x2A2235L, 0x1A1520L, 1.5f, false)
    ), armPivot = Offset(28f, 63f),
    head = listOf(
        CharLayer("M82.0 40.0 C82.0 55.5 67.7 68.0 50.0 68.0 C32.3 68.0 18.0 55.5 18.0 40.0 C18.0 24.5 32.3 12.0 50.0 12.0 C67.7 12.0 82.0 24.5 82.0 40.0 Z", 0xFFFFFFL, 0x9E9E9EL, 1.5f, false)
    ),
    face = listOf(
        CharLayer("M43.9 38.8 C46.2 44.4 44.5 50.4 40.1 52.2 C35.8 54.0 30.4 50.8 28.1 45.2 C25.8 39.6 27.5 33.6 31.9 31.8 C36.2 30.0 41.6 33.2 43.9 38.8 Z", 0x2A2235L, null, 1.5f, false),
        CharLayer("M71.9 45.2 C69.6 50.8 64.2 54.0 59.9 52.2 C55.5 50.4 53.8 44.4 56.1 38.8 C58.4 33.2 63.8 30.0 68.1 31.8 C72.5 33.6 74.2 39.6 71.9 45.2 Z", 0x2A2235L, null, 1.5f, false)
    ),
    eyes = listOf(Offset(36f, 42f), Offset(64f, 42f)), eyeScale = 0.7f, eyeRing = null, eyeRingScale = 1.0f,
    nose = listOf(
        CharLayer("M45 52 C45 49 55 49 55 52 C55 56 52 58 50 58 C48 58 45 56 45 52 Z", 0x2A2235L, null, 1.5f, false)
    ),
    mouthY = 60f, mouthW = 14f, cheekY = 55f, cheekDx = 28f,
    front = emptyList()
    )

    val frog = CharArt(
    back = listOf(
        CharLayer("M33.3 81.7 C34.7 85.9 29.8 91.2 22.5 93.6 C15.1 96.0 8.1 94.5 6.7 90.3 C5.3 86.1 10.2 80.8 17.5 78.4 C24.9 76.0 31.9 77.5 33.3 81.7 Z", 0x5CC45FL, 0x39793AL, 1.5f, false),
        CharLayer("M93.3 90.3 C91.9 94.5 84.9 96.0 77.5 93.6 C70.2 91.2 65.3 85.9 66.7 81.7 C68.1 77.5 75.1 76.0 82.5 78.4 C89.8 80.8 94.7 86.1 93.3 90.3 Z", 0x5CC45FL, 0x39793AL, 1.5f, false)
    ),
    tail = emptyList(), tailPivot = Offset(66f, 84f),
    body = listOf(
        CharLayer("M28 74 C28 60 37 56 50 56 C63 56 72 60 72 74 C72 88 64 95 50 95 C36 95 28 88 28 74 Z", 0x5CC45FL, 0x39793AL, 1.5f, false),
        CharLayer("M64.0 79.0 C64.0 86.2 57.7 92.0 50.0 92.0 C42.3 92.0 36.0 86.2 36.0 79.0 C36.0 71.8 42.3 66.0 50.0 66.0 C57.7 66.0 64.0 71.8 64.0 79.0 Z", 0xD8F5B8L, null, 1.5f, false),
        CharLayer("M45.0 95.0 C45.0 97.8 40.1 100.0 34.0 100.0 C27.9 100.0 23.0 97.8 23.0 95.0 C23.0 92.2 27.9 90.0 34.0 90.0 C40.1 90.0 45.0 92.2 45.0 95.0 Z", 0x2F8F3AL, 0x1D5823L, 1.5f, false),
        CharLayer("M77.0 95.0 C77.0 97.8 72.1 100.0 66.0 100.0 C59.9 100.0 55.0 97.8 55.0 95.0 C55.0 92.2 59.9 90.0 66.0 90.0 C72.1 90.0 77.0 92.2 77.0 95.0 Z", 0x2F8F3AL, 0x1D5823L, 1.5f, false),
        CharLayer("M27.0 94.0 C27.0 95.7 25.7 97.0 24.0 97.0 C22.3 97.0 21.0 95.7 21.0 94.0 C21.0 92.3 22.3 91.0 24.0 91.0 C25.7 91.0 27.0 92.3 27.0 94.0 Z", 0x2F8F3AL, 0x1D5823L, 1.5f, false),
        CharLayer("M79.0 94.0 C79.0 95.7 77.7 97.0 76.0 97.0 C74.3 97.0 73.0 95.7 73.0 94.0 C73.0 92.3 74.3 91.0 76.0 91.0 C77.7 91.0 79.0 92.3 79.0 94.0 Z", 0x2F8F3AL, 0x1D5823L, 1.5f, false)
    ),
    arm = listOf(
        CharLayer("M25.0 58.0 C17.0 63.0 15.0 74.0 17.0 83.0 C18.0 88.0 26.0 89.0 28.0 84.0 C31.0 76.0 33.0 68.0 35.0 61.0 Z", 0x5CC45FL, 0x39793AL, 1.5f, false),
        CharLayer("M28.2 84.0 C28.7 86.7 26.5 89.3 23.4 89.8 C20.2 90.4 17.3 88.7 16.8 86.0 C16.3 83.3 18.5 80.7 21.6 80.2 C24.8 79.6 27.7 81.3 28.2 84.0 Z", 0x2F8F3AL, 0x1D5823L, 1.5f, false),
        CharLayer("M20.6 85.4 L20.9 88.6", null, 0x1C5522L, 0.9f, true),
        CharLayer("M23.9 85.4 L24.2 88.6", null, 0x1C5522L, 0.9f, true)
    ), armPivot = Offset(28f, 63f),
    head = listOf(
        CharLayer("M44.0 24.0 C44.0 31.7 37.7 38.0 30.0 38.0 C22.3 38.0 16.0 31.7 16.0 24.0 C16.0 16.3 22.3 10.0 30.0 10.0 C37.7 10.0 44.0 16.3 44.0 24.0 Z", 0x5CC45FL, 0x39793AL, 1.5f, false),
        CharLayer("M84.0 24.0 C84.0 31.7 77.7 38.0 70.0 38.0 C62.3 38.0 56.0 31.7 56.0 24.0 C56.0 16.3 62.3 10.0 70.0 10.0 C77.7 10.0 84.0 16.3 84.0 24.0 Z", 0x5CC45FL, 0x39793AL, 1.5f, false),
        CharLayer("M16 50 C16 36 30 28 50 28 C70 28 84 36 84 50 C84 62 70 68 50 68 C30 68 16 62 16 50 Z", 0x5CC45FL, 0x39793AL, 1.5f, false)
    ),
    face = emptyList(),
    eyes = listOf(Offset(30f, 24f), Offset(70f, 24f)), eyeScale = 1.0f, eyeRing = null, eyeRingScale = 1.0f,
    nose = listOf(
        CharLayer("M47.5 44.0 C47.5 44.7 46.8 45.2 46.0 45.2 C45.2 45.2 44.5 44.7 44.5 44.0 C44.5 43.3 45.2 42.8 46.0 42.8 C46.8 42.8 47.5 43.3 47.5 44.0 Z", 0x2F8F3AL, null, 1.5f, false),
        CharLayer("M55.5 44.0 C55.5 44.7 54.8 45.2 54.0 45.2 C53.2 45.2 52.5 44.7 52.5 44.0 C52.5 43.3 53.2 42.8 54.0 42.8 C54.8 42.8 55.5 43.3 55.5 44.0 Z", 0x2F8F3AL, null, 1.5f, false)
    ),
    mouthY = 53f, mouthW = 34f, cheekY = 52f, cheekDx = 28f,
    front = emptyList()
    )

    val giraffe = CharArt(
    back = emptyList(),
    tail = listOf(
        CharLayer("M72 86 C82 86 86 92 84 97", null, 0xC8924AL, 2.2f, true),
        CharLayer("M87.4 98.0 C87.4 100.1 85.9 101.8 84.0 101.8 C82.1 101.8 80.6 100.1 80.6 98.0 C80.6 95.9 82.1 94.2 84.0 94.2 C85.9 94.2 87.4 95.9 87.4 98.0 Z", 0xB5651DL, 0x703E11L, 1.5f, false)
    ), tailPivot = Offset(72f, 86f),
    body = listOf(
        CharLayer("M26 74 C26 60 36 56 50 56 C64 56 74 60 74 74 C74 88 64 96 50 96 C36 96 26 88 26 74 Z", 0xF6CD5BL, 0x987F38L, 1.5f, false),
        CharLayer("M64.0 79.0 C64.0 86.7 57.7 93.0 50.0 93.0 C42.3 93.0 36.0 86.7 36.0 79.0 C36.0 71.3 42.3 65.0 50.0 65.0 C57.7 65.0 64.0 71.3 64.0 79.0 Z", 0xFFF1C2L, null, 1.5f, false),
        CharLayer("M47.0 95.0 C47.0 97.8 42.5 100.0 37.0 100.0 C31.5 100.0 27.0 97.8 27.0 95.0 C27.0 92.2 31.5 90.0 37.0 90.0 C42.5 90.0 47.0 92.2 47.0 95.0 Z", 0xB5651DL, 0x703E11L, 1.5f, false),
        CharLayer("M73.0 95.0 C73.0 97.8 68.5 100.0 63.0 100.0 C57.5 100.0 53.0 97.8 53.0 95.0 C53.0 92.2 57.5 90.0 63.0 90.0 C68.5 90.0 73.0 92.2 73.0 95.0 Z", 0xB5651DL, 0x703E11L, 1.5f, false),
        CharLayer("M40.5 68.0 C40.5 70.2 38.5 72.0 36.0 72.0 C33.5 72.0 31.5 70.2 31.5 68.0 C31.5 65.8 33.5 64.0 36.0 64.0 C38.5 64.0 40.5 65.8 40.5 68.0 Z", 0xB5651DL, null, 1.5f, false),
        CharLayer("M67.0 70.0 C67.0 72.4 64.8 74.4 62.0 74.4 C59.2 74.4 57.0 72.4 57.0 70.0 C57.0 67.6 59.2 65.6 62.0 65.6 C64.8 65.6 67.0 67.6 67.0 70.0 Z", 0xB5651DL, null, 1.5f, false),
        CharLayer("M48.0 88.0 C48.0 90.0 46.2 91.6 44.0 91.6 C41.8 91.6 40.0 90.0 40.0 88.0 C40.0 86.0 41.8 84.4 44.0 84.4 C46.2 84.4 48.0 86.0 48.0 88.0 Z", 0xB5651DL, null, 1.5f, false),
        CharLayer("M61.6 86.0 C61.6 87.8 60.0 89.2 58.0 89.2 C56.0 89.2 54.4 87.8 54.4 86.0 C54.4 84.2 56.0 82.8 58.0 82.8 C60.0 82.8 61.6 84.2 61.6 86.0 Z", 0xB5651DL, null, 1.5f, false)
    ),
    arm = listOf(
        CharLayer("M25.0 58.0 C17.0 63.0 15.0 74.0 17.0 83.0 C18.0 88.0 26.0 89.0 28.0 84.0 C31.0 76.0 33.0 68.0 35.0 61.0 Z", 0xF6CD5BL, 0x987F38L, 1.5f, false),
        CharLayer("M28.2 84.0 C28.7 86.7 26.5 89.3 23.4 89.8 C20.2 90.4 17.3 88.7 16.8 86.0 C16.3 83.3 18.5 80.7 21.6 80.2 C24.8 79.6 27.7 81.3 28.2 84.0 Z", 0xB5651DL, 0x703E11L, 1.5f, false),
        CharLayer("M20.6 85.4 L20.9 88.6", null, 0x6C3C11L, 0.9f, true),
        CharLayer("M23.9 85.4 L24.2 88.6", null, 0x6C3C11L, 0.9f, true)
    ), armPivot = Offset(28f, 63f),
    head = listOf(
        CharLayer("M44.4 8.7 C44.8 12.9 44.1 16.3 42.8 16.5 C41.5 16.6 40.0 13.4 39.6 9.3 C39.2 5.1 39.9 1.7 41.2 1.5 C42.5 1.4 44.0 4.6 44.4 8.7 Z", 0xC8924AL, 0x7C5A2DL, 1.5f, false),
        CharLayer("M45.6 4.0 C45.6 6.0 44.0 7.6 42.0 7.6 C40.0 7.6 38.4 6.0 38.4 4.0 C38.4 2.0 40.0 0.4 42.0 0.4 C44.0 0.4 45.6 2.0 45.6 4.0 Z", 0xB5651DL, 0x703E11L, 1.5f, false),
        CharLayer("M60.4 9.3 C60.0 13.4 58.5 16.6 57.2 16.5 C55.9 16.3 55.2 12.9 55.6 8.7 C56.0 4.6 57.5 1.4 58.8 1.5 C60.1 1.7 60.8 5.1 60.4 9.3 Z", 0xC8924AL, 0x7C5A2DL, 1.5f, false),
        CharLayer("M61.6 4.0 C61.6 6.0 60.0 7.6 58.0 7.6 C56.0 7.6 54.4 6.0 54.4 4.0 C54.4 2.0 56.0 0.4 58.0 0.4 C60.0 0.4 61.6 2.0 61.6 4.0 Z", 0xB5651DL, 0x703E11L, 1.5f, false),
        CharLayer("M27.7 24.8 C29.1 27.5 25.9 32.0 20.6 34.9 C15.2 37.7 9.7 37.8 8.3 35.2 C6.9 32.5 10.1 28.0 15.4 25.1 C20.8 22.3 26.3 22.2 27.7 24.8 Z", 0xF6CD5BL, 0x987F38L, 1.5f, false),
        CharLayer("M23.7 26.9 C24.5 28.3 22.5 30.8 19.3 32.5 C16.1 34.2 13.0 34.4 12.3 33.1 C11.5 31.7 13.5 29.2 16.7 27.5 C19.9 25.8 23.0 25.6 23.7 26.9 Z", 0xF7B7C8L, null, 1.5f, false),
        CharLayer("M91.7 35.2 C90.3 37.8 84.8 37.7 79.4 34.9 C74.1 32.0 70.9 27.5 72.3 24.8 C73.7 22.2 79.2 22.3 84.6 25.1 C89.9 28.0 93.1 32.5 91.7 35.2 Z", 0xF6CD5BL, 0x987F38L, 1.5f, false),
        CharLayer("M87.7 33.1 C87.0 34.4 83.9 34.2 80.7 32.5 C77.5 30.8 75.5 28.3 76.3 26.9 C77.0 25.6 80.1 25.8 83.3 27.5 C86.5 29.2 88.5 31.7 87.7 33.1 Z", 0xF7B7C8L, null, 1.5f, false),
        CharLayer("M79.0 38.0 C79.0 52.9 66.0 65.0 50.0 65.0 C34.0 65.0 21.0 52.9 21.0 38.0 C21.0 23.1 34.0 11.0 50.0 11.0 C66.0 11.0 79.0 23.1 79.0 38.0 Z", 0xF6CD5BL, 0x987F38L, 1.5f, false),
        CharLayer("M38.2 24.0 C38.2 26.0 36.3 27.6 34.0 27.6 C31.7 27.6 29.8 26.0 29.8 24.0 C29.8 22.0 31.7 20.4 34.0 20.4 C36.3 20.4 38.2 22.0 38.2 24.0 Z", 0xB5651DL, null, 1.5f, false),
        CharLayer("M68.0 22.0 C68.0 24.3 65.8 26.2 63.0 26.2 C60.2 26.2 58.0 24.3 58.0 22.0 C58.0 19.7 60.2 17.8 63.0 17.8 C65.8 17.8 68.0 19.7 68.0 22.0 Z", 0xB5651DL, null, 1.5f, false),
        CharLayer("M29.6 44.0 C29.6 45.8 28.0 47.2 26.0 47.2 C24.0 47.2 22.4 45.8 22.4 44.0 C22.4 42.2 24.0 40.8 26.0 40.8 C28.0 40.8 29.6 42.2 29.6 44.0 Z", 0xB5651DL, null, 1.5f, false),
        CharLayer("M75.4 36.0 C75.4 37.7 73.9 39.0 72.0 39.0 C70.1 39.0 68.6 37.7 68.6 36.0 C68.6 34.3 70.1 33.0 72.0 33.0 C73.9 33.0 75.4 34.3 75.4 36.0 Z", 0xB5651DL, null, 1.5f, false)
    ),
    face = listOf(
        CharLayer("M68.0 55.0 C68.0 61.6 59.9 67.0 50.0 67.0 C40.1 67.0 32.0 61.6 32.0 55.0 C32.0 48.4 40.1 43.0 50.0 43.0 C59.9 43.0 68.0 48.4 68.0 55.0 Z", 0xFFF1C2L, null, 1.5f, false)
    ),
    eyes = listOf(Offset(38f, 38f), Offset(62f, 38f)), eyeScale = 0.85f, eyeRing = null, eyeRingScale = 1.0f,
    nose = listOf(
        CharLayer("M46.0 54.0 C46.0 54.9 45.1 55.6 44.0 55.6 C42.9 55.6 42.0 54.9 42.0 54.0 C42.0 53.1 42.9 52.4 44.0 52.4 C45.1 52.4 46.0 53.1 46.0 54.0 Z", 0xB5651DL, null, 1.5f, false),
        CharLayer("M58.0 54.0 C58.0 54.9 57.1 55.6 56.0 55.6 C54.9 55.6 54.0 54.9 54.0 54.0 C54.0 53.1 54.9 52.4 56.0 52.4 C57.1 52.4 58.0 53.1 58.0 54.0 Z", 0xB5651DL, null, 1.5f, false)
    ),
    mouthY = 61f, mouthW = 14f, cheekY = 49f, cheekDx = 26f,
    front = emptyList()
    )

    val rabbit = CharArt(
    back = emptyList(),
    tail = listOf(
        CharLayer("M79.0 91.0 C79.0 94.9 75.9 98.0 72.0 98.0 C68.1 98.0 65.0 94.9 65.0 91.0 C65.0 87.1 68.1 84.0 72.0 84.0 C75.9 84.0 79.0 87.1 79.0 91.0 Z", 0xFFFFFFL, 0x9E9E9EL, 1.5f, false)
    ), tailPivot = Offset(72f, 91f),
    body = listOf(
        CharLayer("M28 78 C28 65 37 61 50 61 C63 61 72 65 72 78 C72 90 64 97 50 97 C36 97 28 90 28 78 Z", 0xEFEAF7L, 0x949199L, 1.5f, false),
        CharLayer("M63.0 83.0 C63.0 89.6 57.2 95.0 50.0 95.0 C42.8 95.0 37.0 89.6 37.0 83.0 C37.0 76.4 42.8 71.0 50.0 71.0 C57.2 71.0 63.0 76.4 63.0 83.0 Z", 0xFFFFFFL, null, 1.5f, false),
        CharLayer("M48.0 96.0 C48.0 98.5 43.1 100.6 37.0 100.6 C30.9 100.6 26.0 98.5 26.0 96.0 C26.0 93.5 30.9 91.4 37.0 91.4 C43.1 91.4 48.0 93.5 48.0 96.0 Z", 0xEFEAF7L, 0x949199L, 1.5f, false),
        CharLayer("M74.0 96.0 C74.0 98.5 69.1 100.6 63.0 100.6 C56.9 100.6 52.0 98.5 52.0 96.0 C52.0 93.5 56.9 91.4 63.0 91.4 C69.1 91.4 74.0 93.5 74.0 96.0 Z", 0xEFEAF7L, 0x949199L, 1.5f, false)
    ),
    arm = listOf(
        CharLayer("M25.6 64.5 C18.8 68.7 17.1 78.0 18.8 85.7 C19.6 90.0 26.4 90.8 28.1 86.5 C30.7 79.8 32.4 73.0 34.1 67.0 Z", 0xEFEAF7L, 0x949199L, 1.5f, false),
        CharLayer("M28.3 86.5 C28.7 88.8 26.9 91.0 24.2 91.5 C21.5 92.0 19.0 90.5 18.6 88.3 C18.2 86.0 20.1 83.8 22.8 83.3 C25.4 82.8 27.9 84.3 28.3 86.5 Z", 0xFFFFFFL, 0x9E9E9EL, 1.5f, false),
        CharLayer("M21.9 87.7 L22.1 90.5", null, 0x999999L, 0.9f, true),
        CharLayer("M24.7 87.7 L24.9 90.5", null, 0x999999L, 0.9f, true)
    ), armPivot = Offset(29f, 67f),
    head = listOf(
        CharLayer("M43.4 19.1 C44.7 29.0 42.3 37.4 38.2 37.9 C34.1 38.4 29.8 30.8 28.6 20.9 C27.3 11.0 29.7 2.6 33.8 2.1 C37.9 1.6 42.2 9.2 43.4 19.1 Z", 0xEFEAF7L, 0x949199L, 1.5f, false),
        CharLayer("M39.8 21.5 C40.6 28.7 39.7 34.6 37.6 34.9 C35.5 35.2 33.1 29.6 32.2 22.5 C31.4 15.3 32.3 9.4 34.4 9.1 C36.5 8.8 38.9 14.4 39.8 21.5 Z", 0xFFB3C7L, null, 1.5f, false),
        CharLayer("M71.4 20.9 C70.2 30.8 65.9 38.4 61.8 37.9 C57.7 37.4 55.3 29.0 56.6 19.1 C57.8 9.2 62.1 1.6 66.2 2.1 C70.3 2.6 72.7 11.0 71.4 20.9 Z", 0xEFEAF7L, 0x949199L, 1.5f, false),
        CharLayer("M67.8 22.5 C66.9 29.6 64.5 35.2 62.4 34.9 C60.3 34.6 59.4 28.7 60.2 21.5 C61.1 14.4 63.5 8.8 65.6 9.1 C67.7 9.4 68.6 15.3 67.8 22.5 Z", 0xFFB3C7L, null, 1.5f, false),
        CharLayer("M81.0 48.0 C81.0 62.4 67.1 74.0 50.0 74.0 C32.9 74.0 19.0 62.4 19.0 48.0 C19.0 33.6 32.9 22.0 50.0 22.0 C67.1 22.0 81.0 33.6 81.0 48.0 Z", 0xEFEAF7L, 0x949199L, 1.5f, false)
    ),
    face = emptyList(),
    eyes = listOf(Offset(38f, 47f), Offset(62f, 47f)), eyeScale = 0.85f, eyeRing = null, eyeRingScale = 1.0f,
    nose = listOf(
        CharLayer("M53.4 55.0 C53.4 56.4 51.9 57.6 50.0 57.6 C48.1 57.6 46.6 56.4 46.6 55.0 C46.6 53.6 48.1 52.4 50.0 52.4 C51.9 52.4 53.4 53.6 53.4 55.0 Z", 0xFF8FB0L, null, 1.5f, false),
        CharLayer("M24 54 L36 56", null, 0xB9B0D4L, 1.0f, true),
        CharLayer("M24 60 L36 59", null, 0xB9B0D4L, 1.0f, true),
        CharLayer("M76 54 L64 56", null, 0xB9B0D4L, 1.0f, true),
        CharLayer("M76 60 L64 59", null, 0xB9B0D4L, 1.0f, true)
    ),
    mouthY = 59f, mouthW = 12f, cheekY = 57f, cheekDx = 26f,
    front = emptyList()
    )

    val dog = CharArt(
    back = emptyList(),
    tail = listOf(
        CharLayer("M68 88 C78 82 86 76 87 64 C90 62 92 68 91 72 C90 82 80 92 68 94 Z", 0xD1945AL, 0x815B37L, 1.5f, false)
    ), tailPivot = Offset(68f, 88f),
    body = listOf(
        CharLayer("M26 74 C26 60 36 56 50 56 C64 56 74 60 74 74 C74 88 64 96 50 96 C36 96 26 88 26 74 Z", 0xD1945AL, 0x815B37L, 1.5f, false),
        CharLayer("M64.0 79.0 C64.0 86.7 57.7 93.0 50.0 93.0 C42.3 93.0 36.0 86.7 36.0 79.0 C36.0 71.3 42.3 65.0 50.0 65.0 C57.7 65.0 64.0 71.3 64.0 79.0 Z", 0xFFF1DCL, null, 1.5f, false),
        CharLayer("M47.0 95.0 C47.0 97.8 42.5 100.0 37.0 100.0 C31.5 100.0 27.0 97.8 27.0 95.0 C27.0 92.2 31.5 90.0 37.0 90.0 C42.5 90.0 47.0 92.2 47.0 95.0 Z", 0x7A4A24L, 0x4B2D16L, 1.5f, false),
        CharLayer("M73.0 95.0 C73.0 97.8 68.5 100.0 63.0 100.0 C57.5 100.0 53.0 97.8 53.0 95.0 C53.0 92.2 57.5 90.0 63.0 90.0 C68.5 90.0 73.0 92.2 73.0 95.0 Z", 0x7A4A24L, 0x4B2D16L, 1.5f, false),
        CharLayer("M35 60 C44 67 56 67 65 60 L65 65 C56 72 44 72 35 65 Z", 0xE53935L, 0x8D2320L, 1.5f, false),
        CharLayer("M53.4 70.0 C53.4 71.9 51.9 73.4 50.0 73.4 C48.1 73.4 46.6 71.9 46.6 70.0 C46.6 68.1 48.1 66.6 50.0 66.6 C51.9 66.6 53.4 68.1 53.4 70.0 Z", 0xFFC21AL, 0x9E7810L, 1.5f, false)
    ),
    arm = listOf(
        CharLayer("M25.0 58.0 C17.0 63.0 15.0 74.0 17.0 83.0 C18.0 88.0 26.0 89.0 28.0 84.0 C31.0 76.0 33.0 68.0 35.0 61.0 Z", 0xD1945AL, 0x815B37L, 1.5f, false),
        CharLayer("M28.2 84.0 C28.7 86.7 26.5 89.3 23.4 89.8 C20.2 90.4 17.3 88.7 16.8 86.0 C16.3 83.3 18.5 80.7 21.6 80.2 C24.8 79.6 27.7 81.3 28.2 84.0 Z", 0xFFF1DCL, 0x9E9588L, 1.5f, false),
        CharLayer("M20.6 85.4 L20.9 88.6", null, 0x999084L, 0.9f, true),
        CharLayer("M23.9 85.4 L24.2 88.6", null, 0x999084L, 0.9f, true)
    ), armPivot = Offset(28f, 63f),
    head = listOf(
        CharLayer("M22 24 C8 26 4 48 11 62 C19 67 28 57 29 44 Z", 0x7A4A24L, 0x4B2D16L, 1.5f, false),
        CharLayer("M 78.0 24.0C 92.0 26.0 96.0 48.0 89.0 62.0C 81.0 67.0 72.0 57.0 71.0 44.0Z", 0x7A4A24L, 0x4B2D16L, 1.5f, false),
        CharLayer("M80.0 40.0 C80.0 55.5 66.6 68.0 50.0 68.0 C33.4 68.0 20.0 55.5 20.0 40.0 C20.0 24.5 33.4 12.0 50.0 12.0 C66.6 12.0 80.0 24.5 80.0 40.0 Z", 0xD1945AL, 0x815B37L, 1.5f, false),
        CharLayer("M46.5 36.0 C46.5 41.8 41.8 46.5 36.0 46.5 C30.2 46.5 25.5 41.8 25.5 36.0 C25.5 30.2 30.2 25.5 36.0 25.5 C41.8 25.5 46.5 30.2 46.5 36.0 Z", 0x7A4A24L, null, 1.5f, false)
    ),
    face = listOf(
        CharLayer("M66.0 54.0 C66.0 60.6 58.8 66.0 50.0 66.0 C41.2 66.0 34.0 60.6 34.0 54.0 C34.0 47.4 41.2 42.0 50.0 42.0 C58.8 42.0 66.0 47.4 66.0 54.0 Z", 0xFFF1DCL, null, 1.5f, false)
    ),
    eyes = listOf(Offset(38f, 38f), Offset(62f, 38f)), eyeScale = 0.9f, eyeRing = null, eyeRingScale = 1.0f,
    nose = listOf(
        CharLayer("M57.0 49.0 C57.0 51.7 53.9 53.8 50.0 53.8 C46.1 53.8 43.0 51.7 43.0 49.0 C43.0 46.3 46.1 44.2 50.0 44.2 C53.9 44.2 57.0 46.3 57.0 49.0 Z", 0x2A2235L, null, 1.5f, false),
        CharLayer("M50.0 47.5 C50.0 48.2 49.1 48.7 48.0 48.7 C46.9 48.7 46.0 48.2 46.0 47.5 C46.0 46.8 46.9 46.3 48.0 46.3 C49.1 46.3 50.0 46.8 50.0 47.5 Z", 0xFFFFFFL, null, 1.5f, false)
    ),
    mouthY = 57f, mouthW = 16f, cheekY = 53f, cheekDx = 27f,
    front = emptyList()
    )
}
