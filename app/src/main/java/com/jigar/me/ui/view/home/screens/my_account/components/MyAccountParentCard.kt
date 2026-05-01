package com.jigar.me.ui.view.home.screens.my_account.components

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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.screens.my_account.viewmodels.MyAccountMenu
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8


@Composable
fun MyAccountParentCard(
    menu: MyAccountMenu,
    onItemClick: (String) -> Unit,
    modifier: Modifier
) {
    Column(modifier = modifier) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimens16), // ↓ reduced
            elevation = CardDefaults.cardElevation(Dimens4), // ↓ reduced
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF8E1)
            )
        ) {
            Column(modifier = Modifier.padding(vertical = Dimens8)) { // ↓ reduced

                Text(
                    text = menu.menuTitle,
                    modifier = Modifier.padding(horizontal = Dimens12),
                    style = MaterialTheme.typography.titleSmall.scaled().copy( // ↓ slightly smaller
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily(Font(R.font.font_bold)),
                        color = Color(0xFF5D4037)
                    )
                )

                Spacer(modifier = Modifier.height(Dimens4)) // ↓ reduced

                menu.subMenu.forEach { child ->
                    MyAccountChildRow(
                        item = child,
                        onClick = onItemClick
                    )
                }
            }
        }
    }
}
