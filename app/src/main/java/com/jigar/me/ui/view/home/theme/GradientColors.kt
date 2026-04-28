package com.jigar.me.ui.view.home.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val gradientBrushList = listOf(

    // 🔴 solid (using gradient)
    Brush.linearGradient(listOf(Color(0xFFF44336),Color(0xFFF44336))),
    Brush.linearGradient(listOf(Color(0xFF9C27B0),Color(0xFF9C27B0))),
    Brush.linearGradient(listOf(Color(0xFF673AB7),Color(0xFF673AB7))),
    Brush.linearGradient(listOf(Color(0xFF3F51B5),Color(0xFF3F51B5))),
    Brush.linearGradient(listOf(Color(0xFF00BCD4),Color(0xFF00BCD4))),
    Brush.linearGradient(listOf(Color(0xFF4CAF50),Color(0xFF4CAF50))),
    Brush.linearGradient(listOf(Color(0xFF8BC34A),Color(0xFF8BC34A))),
    Brush.linearGradient(listOf(Color(0xFFFFEB3B),Color(0xFFFFEB3B))),
    Brush.linearGradient(listOf(Color(0xFFFF9800),Color(0xFFFF9800))),
    Brush.linearGradient(listOf(Color(0xFFFF5722),Color(0xFFFF5722))),

    // 🌈 real gradients
    Brush.linearGradient(listOf(Color(0xFFFF0700), Color(0xFF5623B0))),
    Brush.linearGradient(listOf(Color(0xFF1F35B0), Color(0xFFD03066))),
    Brush.linearGradient(listOf(Color(0xFF3199F5), Color(0xFF08867B))),
    Brush.linearGradient(listOf(Color(0xFFCDDC39), Color(0xFF4CAF50))),
    Brush.linearGradient(listOf(Color(0xFFFFEB3B), Color(0xFFFF5722))),
    Brush.linearGradient(listOf(Color(0xFF43A047), Color(0xFFFB8C00))),
)
val colorList = listOf(
    Color(0xFFF44336),
    Color(0xFFE91E63),
    Color(0xFF9C27B0),
    Color(0xFF673AB7),
    Color(0xFF3F51B5),
    Color(0xFF2196F3),
    Color(0xFF03A9F4),
    Color(0xFF00BCD4),
    Color(0xFF009688),
    Color(0xFF4CAF50),
    Color(0xFF8BC34A),
    Color(0xFFCDDC39),
    Color(0xFFFFEB3B),
    Color(0xFFFFC107),
    Color(0xFFFF9800),
    Color(0xFFFF5722),
)
val randomButtonType = listOf(
    ButtonType.BLUE,
    ButtonType.PINK,
    ButtonType.ORANGE,
    ButtonType.PURPLE,
    ButtonType.GREEN,
    ButtonType.RED,
    ButtonType.TEAL,
).random()
enum class ButtonType {
    BLUE,
    PINK,
    ORANGE,
    PURPLE,
    GREEN,
    RED,
    TEAL,
    NEGATIVE,
    DISABLE,
    POSITIVE,
    OPTIONS,
}

data class ButtonColors(
    val base: Color,
    val gradient: Brush
)

fun getButtonColors(type: ButtonType): ButtonColors {
    return when (type) {

        ButtonType.BLUE -> ButtonColors(
            base = Color(0xFF182480),
            gradient = Brush.horizontalGradient(
                listOf(
                    Color(0xFF2783CE),
                    PrimaryBlue,
                )
            )
        )

        ButtonType.PINK -> ButtonColors(
            base = Color(0xFFD5084E),
            gradient = Brush.horizontalGradient(
                listOf(
                    Color(0xFFF5538A),
                    Color(0xFFE91E63)
                )
            )
        )

        ButtonType.ORANGE -> ButtonColors(
            base = Color(0xFFC77601),
            gradient = Brush.horizontalGradient(
                listOf(
                    Color(0xFFEEAA47),
                    Color(0xFFFB8C00)
                )
            )
        )
        ButtonType.PURPLE -> ButtonColors(
            base = Color(0xFF5532D2),
            gradient = Brush.horizontalGradient(
                listOf(
                    Color(0xFF9374EF),
                    Color(0xFF6446CC)
                )
            )
        )

        ButtonType.GREEN -> ButtonColors(
            base = Color(0xFF108156),
            gradient = Brush.horizontalGradient(
                listOf(
                    Color(0xFF4CD09C),
                    Color(0xFF129864)
                )
            )
        )

        ButtonType.TEAL -> ButtonColors(
            base = Color(0xFF0B5960),
            gradient = Brush.horizontalGradient(
                listOf(
                    Color(0xFF46BBC5),
                    Color(0xFF107D86)
                )
            )
        )
        ButtonType.RED -> ButtonColors(
            base = Color(0xFF880606),
            gradient = Brush.horizontalGradient(
                listOf(
                    Color(0xFFD55E5E),
                    Color(0xFFAF0909)
                )
            )
        )
        ButtonType.NEGATIVE -> ButtonColors(
            base = Color(0xFF3D3C3C),
            gradient = Brush.horizontalGradient(
                listOf(
                    Color(0xFF8F8E8E),
                    Color(0xFF5D5D5D)
                )
            )
        )
        ButtonType.DISABLE -> ButtonColors(
            base = Color(0xFF607D8B),
            gradient = Brush.horizontalGradient(
                listOf(
                    Color(0xFFBDCBD2),
                    Color(0xFFB0BEC5)
                )
            )
        )
        ButtonType.POSITIVE -> ButtonColors(
            base = Color(0xFF1E5E22),
            gradient = Brush.horizontalGradient(
                listOf(
                    Color(0xFF4EA953),
                    Color(0xFF2E7D32)
                )
            )
        )
        ButtonType.OPTIONS -> ButtonColors(
            base = Color(0xFF757575),
            gradient = Brush.horizontalGradient(
                listOf(
                    Color(0xFFF5F5F5),
                    Color(0xFFE0E0E0)
                )
            )
        )
    }
}

fun colorFromWord(name: String): Color {
    return when (name.lowercase()) {
        "red" -> Color(0xFFFF0000)
        "blue" -> Color(0xFF3F51B5)
        "sky blue" -> Color(0xFF03A9F4)
        "yellow" -> Color(0xFFFFEB3B)
        "green" -> Color(0xFF4CAF50)
        "purple" -> Color(0xFF673AB7)
        "orange" -> Color(0xFFFF5722)
        "pink" -> Color(0xFFE91E63)
        "brown" -> Color(0xFF795548)
        "black" -> Color(0xFF000000)
        "white" -> Color(0xFFFFFFFF)
        "gray", "grey" -> Color(0xFF9E9E9E)
        else -> Color.Gray.copy(alpha = 0.5f)
    }
}
