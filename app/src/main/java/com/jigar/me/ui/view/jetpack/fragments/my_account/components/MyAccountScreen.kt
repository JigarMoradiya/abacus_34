package com.jigar.me.ui.view.jetpack.fragments.my_account.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.fragments.my_account.viewmodels.MyAccountUiState
import com.jigar.me.ui.view.jetpack.fragments.my_account.viewmodels.getMenuList

@Composable
fun MyAccountScreen(
    uiState: MyAccountUiState, onMenuClick: (String) -> Unit
) {
    val menuList = remember { getMenuList() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(), contentPadding = PaddingValues(dimensionResource(R.dimen.activity_padding12))
    ) {

        // 🔹 STATISTICS
        uiState.statistics?.let { statistics ->
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.activity_padding10))
                ) {
                    StatisticsCard(
                        title = "Total Exam : ", count = statistics.EXAM?.count?.toString() ?: "-", time = statistics.EXAM?.last_exam_given_time ?: "", backgroundColor = Color(0xFFFFEBEE), countColor = Color(0xFFC62828), modifier = Modifier.weight(1f)
                    )

                    StatisticsCard(
                        title = "Total Exercise : ", count = statistics.EXERCISE?.count?.toString() ?: "-", time = statistics.EXERCISE?.last_exam_given_time ?: "", backgroundColor = Color(0xFFE8F5E9), countColor = Color(0xFF2E7D32), modifier = Modifier.weight(1f)
                    )

                    StatisticsCard(
                        title = "Total Custom Challenge Mode : ", count = statistics.CCM?.count?.toString() ?: "-", time = statistics.CCM?.last_exam_given_time ?: "", backgroundColor = Color(0xFFE3F2FD), countColor = Color(0xFF1565C0), modifier = Modifier.weight(1.3f)
                    )
                }
            }
        }
        item {
            // 🔹 MENU LIST (HORIZONTAL)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensionResource(R.dimen.activity_padding12)), horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.activity_padding10))
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
