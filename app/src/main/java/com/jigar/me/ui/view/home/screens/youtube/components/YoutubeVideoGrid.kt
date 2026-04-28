package com.jigar.me.ui.view.home.screens.youtube.components

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.jigar.me.data.model.VideoData
import androidx.core.net.toUri
import com.jigar.me.R
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorPrimaryLight

@Composable
fun YoutubeVideoGrid(
    videos: List<VideoData>,
    columns: Int = 3
) {
    val context = LocalContext.current

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.activity_padding8)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.activity_padding8)),
        modifier = Modifier.
        padding(vertical = dimensionResource(R.dimen.activity_padding8))
            .padding(start = dimensionResource(R.dimen.activity_padding4))
            .padding(end = dimensionResource(R.dimen.activity_padding16))
    ) {
        items(
            items = videos.sortedBy { it.so },
            key = { it.id }
        ) { video ->

            VideoGridItem(
                video = video,
                onClick = {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        "https://www.youtube.com/watch?v=${video.id}".toUri()
                    )
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun VideoGridItem(
    video: VideoData,
    onClick: () -> Unit
) {
    Card(
        onClick = { onClick() },
        modifier = Modifier.fillMaxWidth().padding(dimensionResource(R.dimen.activity_padding2)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = ColorPrimaryLight
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.activity_padding8)),
            modifier = Modifier.padding(bottom = dimensionResource(R.dimen.activity_padding8))
        ) {

            // Thumbnail (16:9)
            val videoUrl = "https://img.youtube.com/vi/${video.id}/maxresdefault.jpg"
            AsyncImage(
                model = videoUrl,
                contentDescription = video.txt,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            )

            // Title
            Text(
                text = video.txt,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium.copy(color = Color.Black, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.font_bold))),
                modifier = Modifier
                    .padding(horizontal = dimensionResource(R.dimen.activity_padding4))
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}