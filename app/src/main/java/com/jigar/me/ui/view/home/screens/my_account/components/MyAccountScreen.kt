package com.jigar.me.ui.view.home.screens.my_account.components

import com.jigar.me.ui.view.home.theme.AppDimens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.outlined.PersonOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.jigar.me.R
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorPrimary
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.screens.my_account.viewmodels.MyAccountUiState
import com.jigar.me.ui.view.home.screens.my_account.viewmodels.getMenuList
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens10
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens2
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens20
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens40

@Composable
fun MyAccountScreen(
    uiState: MyAccountUiState, modifier: Modifier = Modifier.fillMaxSize(), onMenuClick: (String) -> Unit
) {
    val context = LocalContext.current
    val menuList = remember(uiState.isLoggedIn) { getMenuList(context, uiState.isLoggedIn) }

    LazyColumn(
        modifier = modifier, contentPadding = PaddingValues(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens12)
    ) {

        // Same layout as iOS:
        //  - logged in  → weekly report LEFT + 3 stat cards stacked vertically RIGHT
        //  - logged out → centered compact weekly report + login banner
        if (uiState.isLoggedIn && uiState.statistics != null) {
            item {
                val statistics = uiState.statistics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens10)
                ) {
                    com.jigar.me.ui.view.home.screens.home.WeeklyReportContent(
                        stats = uiState.weeklyStats,
                        modifier = Modifier.weight(1f)
                    )
                    androidx.compose.foundation.layout.Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens10)
                    ) {
                        StatisticsCard(
                            title = "Total Exam : ", count = statistics.EXAM?.count?.toString() ?: "-", time = statistics.EXAM?.last_exam_given_time ?: "", backgroundColor = Color(0xFFFFEBEE), countColor = Color(0xFFC62828), modifier = Modifier.fillMaxWidth()
                        )
                        StatisticsCard(
                            title = "Total Exercise : ", count = statistics.EXERCISE?.count?.toString() ?: "-", time = statistics.EXERCISE?.last_exam_given_time ?: "", backgroundColor = Color(0xFFE8F5E9), countColor = Color(0xFF2E7D32), modifier = Modifier.fillMaxWidth()
                        )
                        StatisticsCard(
                            title = "Total CCM : ", count = statistics.CCM?.count?.toString() ?: "-", time = statistics.CCM?.last_exam_given_time ?: "", backgroundColor = Color(0xFFE3F2FD), countColor = Color(0xFF1565C0), modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                androidx.compose.foundation.layout.Spacer(Modifier.padding(top = AppDimens.Dimens12))
            }
        } else {
            item {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    com.jigar.me.ui.view.home.screens.home.WeeklyReportContent(
                        stats = uiState.weeklyStats,
                        compact = !uiState.isLoggedIn
                    )
                }
                androidx.compose.foundation.layout.Spacer(Modifier.padding(top = AppDimens.Dimens12))
            }
            if (!uiState.isLoggedIn) {
                item {
                    NotLoggedInBanner(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = AppDimens.Dimens12),
                        onSignInClick = { onMenuClick("login") }
                    )
                }
            }
        }
        item {
            // Menu list (horizontal)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppDimens.Dimens16), horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16)
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

@Composable
private fun NotLoggedInBanner(
    modifier: Modifier = Modifier,
    onSignInClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens12))
            .background(ColorPrimary.copy(alpha = 0.08f))
            .clickable { onSignInClick() }
            .padding(horizontal = Dimens16, vertical = Dimens10),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens10)
    ) {
        Box(
            modifier = Modifier
                .size(Dimens40)
                .background(ColorPrimary.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.PersonOff,
                contentDescription = null,
                tint = ColorPrimary,
                modifier = Modifier.size(Dimens20)
            )
        }

        androidx.compose.foundation.layout.Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimens2)
        ) {
            Text(
                text = stringResource(R.string.login_to_view_your_account),
                style = MaterialTheme.typography.labelMedium.scaled().copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.font_bold)),
                    color = ColorPrimary
                )
            )
            Text(
                text = stringResource(R.string.login_to_view_account_description),
                style = MaterialTheme.typography.labelSmall.scaled().copy(
                    fontFamily = FontFamily(Font(R.font.font_regular)),
                    color = Color.Black.copy(alpha = 0.55f)
                ),
                maxLines = 1
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(100))
                .background(ColorPrimary)
                .padding(horizontal = Dimens12, vertical = AppDimens.Dimens6),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens4)
            ) {
                Text(
                    text = stringResource(R.string.login_now),
                    style = MaterialTheme.typography.labelSmall.scaled().copy(
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily(Font(R.font.font_semibold)),
                        color = Color.White
                    )
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Login,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(AppDimens.Dimens14)
                )
            }
        }
    }
}