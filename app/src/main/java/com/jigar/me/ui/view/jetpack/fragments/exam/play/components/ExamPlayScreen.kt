package com.jigar.me.ui.view.jetpack.fragments.exam.play.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.view.jetpack.fragments.exam.play.viewmodels.ExamPlayUiState
import com.jigar.me.ui.view.jetpack.fragments.exam.play.viewmodels.ExamPlayViewModel
import com.jigar.me.utils.Calculator
import com.jigar.me.utils.CommonUtils

@Composable
fun ExamPlayScreen(
    viewModel: ExamPlayViewModel,
    uiState: ExamPlayUiState
) {
    val currentItem = uiState.examPaper.getOrNull(uiState.currentIndex)

    // ---------- Derived Question ----------
    val questionText = remember(currentItem) {
        currentItem?.value
            ?.replace("+", " + ")
            ?.replace("-", " - ")
            ?.replace("x", " x ")
            ?.replace("/", " ÷ ")
            ?.plus(" = ")
            ?: ""
    }

    // ---------- Derived Correct Answer ----------
    val correctAnswer = remember(currentItem) {
        currentItem?.let {
            if (it.value.contains("x")) {
                val parts = it.value.split("x")
                (parts[0].toLong() * parts[1].toLong()).toString()
            } else {
                CommonUtils.removeTrailingZero(Calculator().getResult(it.value, it.value))
            }
        } ?: ""
    }

    // ---------- Derived Options ----------
    val options = remember(correctAnswer) {
        if (correctAnswer.isEmpty()) emptyList()
        else {
            val base = correctAnswer.toLong()
            val temp = mutableListOf<Long>()

            listOf(-1, -2, -3, 1, 2, 3).forEach {
                if (base + it > 0) temp.add(base + it)
            }

            temp.shuffled().take(3)
                .plus(base)
                .shuffled()
                .map { it.toString() }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // ---------- QUESTION ----------
        Text(
            text = questionText,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(24.dp))

        // ---------- OPTIONS ----------
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            options.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { option ->
                        AnswerCard(
                            text = option,
                            onClick = {
                                viewModel.onOptionSelected(option, correctAnswer)
                            }
                        )
                    }
                }
            }
        }
    }


}

