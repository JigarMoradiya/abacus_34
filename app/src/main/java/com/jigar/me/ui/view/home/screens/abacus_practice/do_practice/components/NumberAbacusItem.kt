package com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jigar.me.data.model.dbtable.abacus_all_data.Abacus
import com.jigar.me.ui.view.ab.AbChalk
import com.jigar.me.ui.view.ab.AbQuestionBoard
import com.jigar.me.ui.view.ab.AbShrinkText
import com.jigar.me.ui.view.ab.abFixedStyle
import com.jigar.me.ui.view.ab.abQuestionMetrics
import com.jigar.me.utils.extensions.convertNumberToWords

/** The number to set on the abacus, written big on the chalkboard with its name in words under it. */
@Composable
fun NumberAbacusItem(
    abacus: Abacus, modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val density = LocalDensity.current.density
    AbQuestionBoard(modifier, contentWidth = abQuestionMetrics().queBoxWidth.dp * 2.1f) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            AbShrinkText(
                text = abacus.question,
                style = abFixedStyle(practiceResponsive(60f), FontWeight.ExtraBold).copy(
                    shadow = Shadow(AbChalk.Yellow.copy(alpha = 0.5f), Offset.Zero, 8f * density)
                ),
                color = AbChalk.Yellow,
                minScale = 0.5f,
                maxLines = 1,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            val questionWord = context.convertNumberToWords(abacus.question.toInt())
            Text(
                text = questionWord,
                textAlign = TextAlign.Center,
                style = abFixedStyle(practiceResponsive(15f), FontWeight.Bold),
                color = AbChalk.White.copy(alpha = 0.88f),
            )
        }
    }
}
