package com.jigar.me.ui.view.home.screens.whats_learning.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.jigar.me.data.local.data.VideoTutorial
import com.jigar.me.ui.view.ab.AbShrinkText
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.home.theme.AppDimens

@UnstableApi
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoPager(
    videoList: List<VideoTutorial>,
    currentPosition: Int,
    onPageChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val pagerState = rememberPagerState(
        initialPage = currentPosition,
        pageCount = { videoList.size }
    )

    LaunchedEffect(pagerState.currentPage) {
        onPageChanged(pagerState.currentPage)
    }

    Column(modifier= modifier.fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {

        AbShrinkText(
            text = videoList[currentPosition].title,
            style = AbTypography.headline(s),
            color = colors.textPrimary,
            modifier = Modifier.padding(horizontal = AppDimens.Dimens16),
            minScale = 0.7f,
            maxLines = 2,
            textAlign = TextAlign.Center
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

        // Indicator: the current page is a longer pill in the brand colour
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp * s), verticalAlignment = Alignment.CenterVertically) {
            repeat(videoList.size) { index ->
                val current = index == pagerState.currentPage
                val width by animateDpAsState(
                    if (current) 22.dp * s else 8.dp * s,
                    spring(dampingRatio = 0.7f, stiffness = 322f), label = "pagerDot"
                )
                Box(
                    modifier = Modifier
                        .width(width)
                        .height(8.dp * s)
                        .clip(CircleShape)
                        .background(if (current) colors.brandPrimary else colors.outline)
                )
            }
        }
    }
}
