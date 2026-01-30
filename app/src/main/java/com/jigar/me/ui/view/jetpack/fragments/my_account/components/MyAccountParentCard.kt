package com.jigar.me.ui.view.jetpack.fragments.my_account.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.core.presentation.theme.Black
import com.jigar.me.ui.view.jetpack.fragments.my_account.viewmodels.MyAccountMenu


@Composable
fun MyAccountParentCard(
    menu: MyAccountMenu, onItemClick: (String) -> Unit, modifier: Modifier
) {
    Column(
        modifier = modifier
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column {

                // Title
                Text(
                    modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.activity_padding12)).padding(top = dimensionResource(R.dimen.activity_padding8)), text = menu.menuTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Black,
                        fontFamily = FontFamily(Font(R.font.font_bold))
                    )
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.activity_padding8)))

                menu.subMenu.forEach { child ->
                    MyAccountChildRow(
                        item = child, onClick = onItemClick
                    )
                }
            }
        }

    }
}
