package com.jigar.me.ui.view.home.screens.category.do_practice.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.jigar.me.R
import com.jigar.me.data.model.dbtable.abacus_all_data.Abacus
import com.jigar.me.utils.extensions.convertNumberToWords

@Composable
fun NumberAbacusItem(
    abacus: Abacus, modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(modifier = modifier,horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = abacus.question,
            style = MaterialTheme.typography.displayLarge.copy(color = MaterialTheme.colorScheme.error,fontWeight = FontWeight.Black,
                fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.6f),
                    offset = Offset(3f, 3f),
                    blurRadius = 6f
                )
            ),
        )
        val questionWord = context.convertNumberToWords(abacus.question.toInt())
        Text(
            text = questionWord,
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground,fontWeight = FontWeight.Bold,fontFamily = FontFamily(Font(R.font.font_bold))),
        )
    }
}
