package com.jigar.me.ui.view.jetpack.fragments.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.jigar.me.R
import com.jigar.me.data.model.dbtable.abacus_all_data.Level
import com.jigar.me.ui.view.jetpack.fragments.home.viewmodels.HomeUiState
import com.jigar.me.utils.CommonUtils

@Composable
fun HomeHeaderLeft(
    onMyAccountClick: () -> Unit
) {
    val context = LocalContext.current
    Row(modifier = Modifier.padding(start = dimensionResource(R.dimen.activity_padding12),top = dimensionResource(R.dimen.activity_padding12))) {
        Image(
            painter = painterResource(id = R.drawable.logo_banner),
            contentDescription = "Abacus",
            modifier = Modifier.size(56.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(0.dp), modifier = Modifier.padding(start = dimensionResource(R.dimen.activity_padding10))) {
            Text(
                lineHeight = 10.sp,
                text = CommonUtils.getCurrentTimeMessage(context),
                style = MaterialTheme.typography.labelMedium.copy(color = Color.Black, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.font_bold))),
            )

            Text(
                lineHeight = 10.sp,
                text = stringResource(R.string.welcome_to_abacus_application),
                style = MaterialTheme.typography.labelSmall.copy(color = Color.DarkGray, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily(Font(R.font.font_semibold))),
            )
            CompositionLocalProvider(
                LocalMinimumInteractiveComponentSize provides Dp.Unspecified
            ) {
                Surface(
                    onClick = onMyAccountClick,
                    shape = RoundedCornerShape(3.dp),
                    color = colorResource(R.color.colorPrimary),
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.activity_padding2)),
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp
                ) {
                    Text(
                        lineHeight = 10.sp,
                        text = stringResource(R.string.my_account),
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.font_bold))),
                        modifier = Modifier.padding(
                            horizontal = dimensionResource(R.dimen.activity_padding4),
                            vertical = dimensionResource(R.dimen.activity_padding2)
                        )
                    )
                }
            }

        }
    }
}

