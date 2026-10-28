package com.jigar.me.ui.view.home.screens.abacus_practice.abacus_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jigar.me.data.model.dbtable.abacus_all_data.Abacus
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.home.theme.AppDimens

@Composable
fun AbacusListItem(abacusList: List<Abacus>) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val shape = RoundedCornerShape(AbShapes.sm * s)
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(AppDimens.Dimens12),
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens4),
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens4)
    ) {
        items(abacusList) { abacus ->
            Text(
                text = abacus.question,
                style = AbTypography.label(s),
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.bgSecondary, shape)
                    .border(2.dp, colors.outline, shape)
                    .padding(AppDimens.Dimens6)
            )
        }
    }
}
