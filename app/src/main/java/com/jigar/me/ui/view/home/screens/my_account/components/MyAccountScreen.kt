package com.jigar.me.ui.view.home.screens.my_account.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PersonOff
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.view.ab.AB_SF_TO_VECTOR
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbShrinkText
import com.jigar.me.ui.view.ab.AbSize
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abChunkyFace
import com.jigar.me.ui.view.home.screens.home.WeeklyReportContent
import com.jigar.me.ui.view.home.screens.my_account.viewmodels.MyAccountUiState
import com.jigar.me.ui.view.home.screens.my_account.viewmodels.getMenuList
import com.jigar.me.ui.view.home.theme.AppDimens

@Composable
fun MyAccountScreen(
    uiState: MyAccountUiState, modifier: Modifier = Modifier.fillMaxSize(), onMenuClick: (String) -> Unit
) {
    val context = LocalContext.current
    val colors = LocalAbColors.current
    val menuList = remember(uiState.isLoggedIn) { getMenuList(context, uiState.isLoggedIn) }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = DeviceInfo.screenHorizontalPadding(),
            end = AppDimens.Dimens16,
            top = AppDimens.Dimens10,
            bottom = AppDimens.Dimens16
        ),
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens10)
    ) {

        // Same layout as iOS:
        //  - logged in  → weekly report LEFT + 3 stat cards stacked vertically RIGHT (centred against each other)
        //  - logged out → centered compact weekly report + login banner
        if (uiState.isLoggedIn && uiState.statistics != null) {
            item {
                val statistics = uiState.statistics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens10)
                ) {
                    // padded so the card's shadow is not clipped, like iOS
                    Box(Modifier.weight(1f).padding(AppDimens.Dimens8)) {
                        WeeklyReportContent(
                            stats = uiState.weeklyStats,
                            compact = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens10)
                    ) {
                        StatisticsCard(
                            title = "Total Exam", count = statistics.EXAM?.count?.toString() ?: "-", time = statistics.EXAM?.last_exam_given_time ?: "",
                            tone = if (colors.isDark) Color(0xFFFF453A) else Color(0xFFFF3B30)
                        )
                        StatisticsCard(
                            title = "Total Exercise", count = statistics.EXERCISE?.count?.toString() ?: "-", time = statistics.EXERCISE?.last_exam_given_time ?: "",
                            tone = if (colors.isDark) Color(0xFF30D158) else Color(0xFF34C759)
                        )
                        StatisticsCard(
                            title = "Total CCM", count = statistics.CCM?.count?.toString() ?: "-", time = statistics.CCM?.last_exam_given_time ?: "",
                            tone = if (colors.isDark) Color(0xFF0A84FF) else Color(0xFF007AFF)
                        )
                    }
                }
            }
        } else {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(AppDimens.Dimens8),
                    contentAlignment = Alignment.Center
                ) {
                    WeeklyReportContent(
                        stats = uiState.weeklyStats,
                        compact = true,
                        modifier = Modifier.widthIn(max = 480.dp).fillMaxWidth()
                    )
                }
            }
            if (!uiState.isLoggedIn) {
                item {
                    NotLoggedInBanner(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = AppDimens.Dimens10),
                        onSignInClick = { onMenuClick("login") }
                    )
                }
            }
        }
        item {
            // Menu list (horizontal)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
                verticalAlignment = Alignment.Top
            ) {

                // Column 1 → My Account
                MyAccountParentCard(
                    menu = menuList[0], onItemClick = onMenuClick, modifier = Modifier.weight(1f)
                )

                // Column 2 → More
                MyAccountParentCard(
                    menu = menuList[1], onItemClick = onMenuClick, modifier = Modifier.weight(1f)
                )
            }

        }
    }
}

/** "Sign in to view your account": a friendly strip with a sign-in button. */
@Composable
private fun NotLoggedInBanner(
    modifier: Modifier = Modifier,
    onSignInClick: () -> Unit
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    Row(
        modifier = modifier
            .abChunkyFace(
                face = colors.bgSecondary.copy(alpha = if (colors.isDark) 0.96f else 0.94f),
                edge = colors.outline, stroke = colors.outline,
                cornerRadius = AbShapes.md * s, edgeHeight = 3.dp * s,
            )
            .padding(horizontal = 16.dp * s, vertical = 10.dp * s),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp * s)
    ) {
        Icon(
            imageVector = Icons.Outlined.PersonOff,
            contentDescription = null,
            tint = colors.brandPrimary,
            modifier = Modifier.size((26f * AB_SF_TO_VECTOR).dp * s)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.login_to_view_your_account),
                style = AbTypography.fixed(15f * s, FontWeight.ExtraBold),
                color = colors.textPrimary
            )
            AbShrinkText(
                text = stringResource(R.string.login_to_view_account_description),
                style = AbTypography.fixed(12f * s, FontWeight.SemiBold),
                color = colors.textSecondary,
                minScale = 0.8f,
                maxLines = 2
            )
        }

        AbButton(
            text = stringResource(R.string.login_now), tone = AbTone.Primary, size = AbSize.Small,
            icon = Icons.Rounded.ChevronRight, iconAtEnd = true, onClick = onSignInClick
        )
    }
}
