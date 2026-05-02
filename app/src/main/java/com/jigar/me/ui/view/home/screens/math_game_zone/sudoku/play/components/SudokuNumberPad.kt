package com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.view.home.common_ui.buttons.KidsKeyPad
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.viewmodel.SudokuPlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.theme.ButtonType


@Composable
fun SudokuNumberPad(vm: SudokuPlayViewModel) {

    val max = vm.puzzle.size.grid
    val columns = if (max == 4) 2 else 3
    val numbers = (1..max).toList()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(if (DeviceInfo.isTablet)AppDimens.Dimens10 else AppDimens.Dimens6)
    ) {

        // ✅ Numbers grid
        numbers.chunked(columns).forEach { rowItems ->

            Row(
                horizontalArrangement = Arrangement.spacedBy(if (DeviceInfo.isTablet)AppDimens.Dimens10 else AppDimens.Dimens6),
                verticalAlignment = Alignment.CenterVertically
            ) {
                rowItems.forEach { n ->
                    KidsKeyPad(
                        text = "$n",
                        type = ButtonType.GREEN,
                        onClick = { vm.enter(n) }
                    )
                }
            }
        }

        // ✅ Erase button (centered, separate row)
        KidsKeyPad(
            icon = painterResource(R.drawable.ic_backspace),
            type = ButtonType.RED,
            onClick = { vm.eraseSelected() }
        )
    }
}