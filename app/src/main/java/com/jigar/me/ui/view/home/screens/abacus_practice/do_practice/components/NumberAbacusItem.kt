package com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import com.jigar.me.R
import com.jigar.me.data.model.dbtable.abacus_all_data.Abacus
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.theme.AppDimens.doPracticeNumberFonts
import com.jigar.me.utils.extensions.convertNumberToWords

@Composable
fun NumberAbacusItem(
    abacus: Abacus, modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(modifier = modifier,horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = abacus.question,
            style = MaterialTheme.typography.displayLarge.scaled().copy(fontSize = doPracticeNumberFonts,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Black,
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.6f),
                    offset = Offset(3f, 3f),
                    blurRadius = 6f
                )
            ),
        )
        val questionWord = context.convertNumberToWords(abacus.question.toInt())
        Text(
            modifier = Modifier.padding(horizontal = Dimens8),
            text = questionWord,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge.scaled().copy(color = MaterialTheme.colorScheme.onBackground,fontWeight = FontWeight.Bold,fontFamily = FontFamily(Font(R.font.font_bold))),
        )
    }
}
