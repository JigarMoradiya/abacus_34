package com.jigar.me.ui.view.home.screens.whats_learning.components

import com.jigar.me.ui.view.home.theme.AppDimens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.jigar.me.R
import com.jigar.me.data.local.data.VideoTutorial
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorPrimary
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled

@UnstableApi
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoPager(
    videoList: List<VideoTutorial>,
    currentPosition: Int,
    onPageChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(
        initialPage = currentPosition,
        pageCount = { videoList.size }
    )

    LaunchedEffect(pagerState.currentPage) {
        onPageChanged(pagerState.currentPage)
    }

    Column(modifier= modifier.fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {

        Text(
            text = videoList[currentPosition].title,
            style = MaterialTheme.typography.bodyLarge.scaled().copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.font_bold))),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = AppDimens.Dimens16)
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
        ) { page ->
            AssetVideoPlayer(
                assetFileName = videoList[page].videoName,
                isPlaying = pagerState.currentPage == page,
                modifier = Modifier.padding(horizontal = AppDimens.Dimens4, vertical = AppDimens.Dimens12)
            )
        }

        Spacer(Modifier.height(AppDimens.Dimens8))

        // 🔘 Indicator (Compose replacement of ScrollingPagerIndicator)
        Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6)) {
            repeat(videoList.size) { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == pagerState.currentPage) AppDimens.Dimens8 else AppDimens.Dimens6)
                        .clip(CircleShape)
                        .background(
                            if (index == pagerState.currentPage)
                                ColorPrimary
                            else
                                Color.Gray.copy(alpha = 0.4f)
                        )
                )
            }
        }
    }
}
