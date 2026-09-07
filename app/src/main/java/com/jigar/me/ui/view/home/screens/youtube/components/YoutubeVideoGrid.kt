package com.jigar.me.ui.view.home.screens.youtube.components

import android.content.Intent
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.jigar.me.R
import com.jigar.me.data.model.VideoData
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorPrimaryLight
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.theme.AppDimens

// Gate state/rendering is owned by the caller (YoutubeVideoScreenRoute), not
// this composable -- so its scrim can cover the whole screen (including the
// header above this grid) instead of just this grid's own bounds.
@Composable
fun YoutubeVideoGrid(
    videos: List<VideoData>,
    columns: Int = 3,
    gateResolvedThisVisit: Boolean,
    onRequestGate: (action: () -> Unit) -> Unit
) {
    val context = LocalContext.current

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
        modifier = Modifier
            .padding(vertical = AppDimens.Dimens8)
            .padding(horizontal = AppDimens.Dimens16)
    ) {
        items(
            items = videos.sortedBy { it.so },
            key = { it.id }
        ) { video ->
            VideoGridItem(
                video = video,
                onClick = {
                    val action: () -> Unit = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            "https://www.youtube.com/watch?v=${video.id}".toUri()
                        )
                        context.startActivity(intent)
                    }
                    if (gateResolvedThisVisit) action() else onRequestGate(action)
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
        modifier = Modifier.fillMaxWidth().padding(AppDimens.Dimens2),
        shape = RoundedCornerShape(AppDimens.Dimens12),
        colors = CardDefaults.cardColors(
            containerColor = ColorPrimaryLight
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = AppDimens.Dimens2)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
            modifier = Modifier.padding(bottom = AppDimens.Dimens8)
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
                style = MaterialTheme.typography.labelMedium.scaled().copy(color = Color.Black, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.font_bold))),
                modifier = Modifier
                    .padding(horizontal = AppDimens.Dimens4)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}
