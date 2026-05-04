package com.jigar.me.ui.view.home.screens.abacus_practice.level_list.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.data.model.dbtable.abacus_all_data.Category
import com.jigar.me.ui.view.home.screens.abacus_practice.level_list.viewmodels.LevelProgress
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.LevelSize

@Composable
fun CategoryItem(
    category: Category,
    progress: LevelProgress,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(LevelSize)
            .clip(RoundedCornerShape(AppDimens.Dimens16))
            .clickable { onClick() }
    ) {

        Box(
            modifier = Modifier
        ) {

            Image(
                painter = painterResource(
                    getCategoryDrawable(category.name)
                ),
                contentDescription = null,
                modifier = Modifier
                    .aspectRatio(379f / 519f),
            )
//            AsyncImage(
//                model = category.icon,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .aspectRatio(379f / 519f),
//                contentDescription = null,
//                contentScale = ContentScale.Crop
//            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                ProgressCandyRow(
                    completed = progress.stepCompleted,
                    total = progress.stepTotal,
                    colors = listOf(
                        colorResource(R.color.step_by_step_answer),
                        Color(0xFF5C6BC0)
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                ProgressCandyRow(
                    completed = progress.finalCompleted,
                    total = progress.finalTotal,
                    colors = listOf(
                        colorResource(R.color.final_answer),
                        Color(0xFF2E7D32)
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                ProgressCandyRow(
                    completed = progress.examCompleted,
                    total = progress.examTotal,
                    colors = listOf(
                        colorResource(R.color.formal_exam),
                        Color(0xFF8E24AA)
                    )
                )
            }
        }
    }
}

fun getCategoryDrawable(name: String): Int {
    return when (name.lowercase()) {
        "level1" -> R.drawable.level1
        "level2" -> R.drawable.level2
        "level3" -> R.drawable.level3
        "level4" -> R.drawable.level4
        "level5" -> R.drawable.level5
        "level6" -> R.drawable.level6
        "level7" -> R.drawable.level7
        "level8" -> R.drawable.level8
        else -> R.drawable.level1
    }
}