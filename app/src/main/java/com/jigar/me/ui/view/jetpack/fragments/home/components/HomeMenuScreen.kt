package com.jigar.me.ui.view.jetpack.fragments.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.jigar.me.R
import com.jigar.me.data.model.dbtable.abacus_all_data.Level
import com.jigar.me.ui.view.jetpack.fragments.home.viewmodels.HomeUiState

@Composable
fun HomeMenuScreen(
    uiState: HomeUiState,
    onMenuClick: (Level) -> Unit
) {
    LazyRow(
        modifier = Modifier.padding(vertical = dimensionResource(R.dimen.activity_padding16)),
        contentPadding = PaddingValues(horizontal = dimensionResource(R.dimen.activity_padding12)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.activity_padding8))
    ) {
        items(uiState.menuLevels) { level ->
            HomeMenuItem(
                level = level,
                onClick = { onMenuClick(level) }
            )
        }
    }
}

@Composable
fun HomeMenuItem(
    level: Level,
    onClick: () -> Unit
) {
    Box(modifier = Modifier.aspectRatio(1f)) {
        Card(
            onClick= { onClick() },
            shape = CircleShape,
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorResource(R.color.home_menu_icon_bg)
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.activity_padding8))
        ) {
            AsyncImage(
                model = level.icon,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (level.tag.length > 2) {
            Surface(
                color = colorResource(R.color.yellow_300),
                shape = RoundedCornerShape(20.dp),
                tonalElevation = dimensionResource(R.dimen.activity_padding8),
                shadowElevation = dimensionResource(R.dimen.activity_padding8),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                Text(
                    text = level.tag,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                        color = Color.Black
                    ),
                    modifier = Modifier.padding(
                        horizontal = dimensionResource(R.dimen.activity_padding10),
                        vertical = dimensionResource(R.dimen.activity_padding4)
                    )
                )
            }
        }
    }
}
