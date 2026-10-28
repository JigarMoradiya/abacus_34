package com.jigar.me.ui.view.home.screens.youtube.components

import android.content.Intent
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.data.model.VideoData
import com.jigar.me.ui.view.ab.AB_SF_TO_VECTOR
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbShrinkText
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abChunkyFace
import com.jigar.me.ui.view.ab.abSquash
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
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
        contentPadding = PaddingValues(
            start = DeviceInfo.screenHorizontalPadding(),
            end = AppDimens.Dimens16,
            top = AppDimens.Dimens16,
            bottom = AppDimens.Dimens16
        )
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

/** One video: its thumbnail with a play button on it and its title, on a plate that squashes when pressed. */
@Composable
fun VideoGridItem(
    video: VideoData,
    onClick: () -> Unit
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val shape = RoundedCornerShape(AbShapes.md * s)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .abSquash(cornerRadius = AbShapes.md * s, edgeHeight = 0.dp, onClick = onClick)
            .abChunkyFace(
                face = colors.bgSecondary.copy(alpha = if (colors.isDark) 0.96f else 0.94f),
                edge = colors.outline, stroke = null,
                cornerRadius = AbShapes.md * s, edgeHeight = 3.dp * s,
            )
            .border(2.dp, colors.outline, shape),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp * s)
    ) {

        // Thumbnail (16:9) with a play button, so it reads as a video
        Box(
            Modifier
                .padding(start = 6.dp * s, end = 6.dp * s, top = 6.dp * s)
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(AbShapes.sm * s)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = "https://img.youtube.com/vi/${video.id}/hqdefault.jpg",
                contentDescription = video.txt,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
            Box(
                Modifier
                    .size(40.dp * s)
                    .background(Color.Black.copy(alpha = 0.35f), CircleShape)
                    .border(2.dp, Color.White.copy(alpha = 0.9f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.PlayArrow, null, Modifier.size((16f * AB_SF_TO_VECTOR).dp * s), tint = Color.White)
            }
        }

        // Title
        AbShrinkText(
            text = video.txt,
            style = AbTypography.fixed(12f * s, FontWeight.ExtraBold),
            color = colors.textPrimary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp * s, end = 8.dp * s, bottom = 8.dp * s),
            minScale = 0.8f,
            maxLines = 2,
            textAlign = TextAlign.Center
        )
    }
}
