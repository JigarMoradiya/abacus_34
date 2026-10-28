package com.jigar.me.ui.view.ab

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jigar.me.data.local.data.CustomChallengeData
import com.jigar.me.data.local.data.CustomChallengeQuestion
import com.jigar.me.data.model.data.AllExamData
import com.jigar.me.data.model.data.Statistics
import com.jigar.me.data.model.data.StatisticsData
import com.jigar.me.ui.view.home.screens.activities.ccm.play.components.CCMCompleteBottomSheetCompose
import com.jigar.me.ui.view.home.screens.activities.ccm.play.viewmodels.CCMPlayUiState
import com.jigar.me.ui.view.home.screens.my_account.components.MyAccountScreen
import com.jigar.me.ui.view.home.screens.my_account.viewmodels.MyAccountUiState
import com.jigar.me.ui.view.home.screens.reports.components.ReportCard
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.WeeklySummaryManager

/**
 * Debug previews of screens that need a signed-in account or server data (`ab_screen=account|reports|ccmresult|ccmwrong`),
 * filled with sample data so they can be checked on an emulator without a login.
 */
@Composable
fun AbAccountPreview() {
    Box(Modifier.fillMaxSize()) {
        AbBackground(AbWorld.Lagoon, Modifier.fillMaxSize())
        Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
            AbTopBar(title = "Parent", onBack = {}, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            MyAccountScreen(
                uiState = MyAccountUiState(
                    isLoggedIn = true,
                    statistics = Statistics(
                        EXAM = StatisticsData(count = 12.0), EXERCISE = StatisticsData(count = 48.0),
                        CCM = StatisticsData(count = 7.0), PRACTISE = StatisticsData(count = 130.0),
                    ),
                    weeklyStats = WeeklySummaryManager.WeeklyStats(problems = 86, days = 4, streak = 3),
                ),
                onMenuClick = {},
            )
        }
    }
}

private fun sampleReport(type: String, level: String?, right: Int, total: Int, seconds: Int) = AllExamData(
    id = type + level, type = type, level = level, no_of_questions = total, no_of_right_answers = right,
    total_time_taken = seconds, allowed_max_time = "180", created_at = "2026-10-26T10:15:00.000Z",
    category = "Addition & Subtraction", label = "1 Digit, 5 Lines", sub_type = "Addition",
    total_set_of_question = total, total_numbers_in_set = 3, gap_between_two_question = 3,
    question_min_length = 1, question_max_length = 3,
    is_question_speak = true, is_question_show_in_number = true, is_question_show_in_word = false,
    questions = "[{\"que\":\"72+15-47\",\"user_answer\":\"38\",\"is_correct\":false,\"index\":0}]",
)

@Composable
fun AbReportsPreview() {
    val items = listOf(
        sampleReport(AppConstants.EXAM.type_Exam, "Beginner", 17, 20, 410),
        sampleReport(AppConstants.EXAM.type_Exercise, "3", 4, 5, 96),
        sampleReport(AppConstants.EXAM.type_CCM, null, 8, 10, 75),
        sampleReport("PRACTISE", "1", 5, 5, 140),
    )
    Box(Modifier.fillMaxSize()) {
        AbBackground(AbWorld.Lagoon, Modifier.fillMaxSize())
        Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
            AbTopBar(title = "Report History Cards", onBack = {}, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)) {
                items(items) { ReportCard(item = it, onCheckResultTapped = {}) }
            }
        }
    }
}

@Composable
fun AbCcmResultPreview(correct: Boolean) {
    Box(Modifier.fillMaxSize()) {
        AbBackground(AbWorld.Meadow, Modifier.fillMaxSize())
        CCMCompleteBottomSheetCompose(
            uiState = CCMPlayUiState(
                isAnswerTrue = correct,
                customChallengeData = CustomChallengeData(
                    questions = mutableListOf(CustomChallengeQuestion("+", 72), CustomChallengeQuestion("-", 47), CustomChallengeQuestion("+", 15)),
                    fullQuestion = "72-47+15", answer = 40,
                ),
                answerText = if (correct) "40" else "38",
            ),
            onContinue = {}, onClose = {},
        )
    }
}


/** Debug preview of the Practice question board with a lit step (`ab_screen=board`). */
@Composable
fun AbBoardPreview() {
    Box(Modifier.fillMaxSize()) {
        AbBackground(AbWorld.Meadow, Modifier.fillMaxSize())
        androidx.compose.foundation.layout.Row(
            Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).padding(24.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(24.dp, androidx.compose.ui.Alignment.CenterHorizontally),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            AbHintCard {
                AbHintTag("Big Friend (+10)", AbHintKind.BigFriend)
                androidx.compose.material3.Text("+2=-8+10", style = abFixedStyle(22f))
            }
            AbQuestionBoard {
                Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                    AbChalkText("68", tone = AbChalkTone.Dim, pill = true)
                    AbChalkText("52", tone = AbChalkTone.Active, pill = true)
                    AbChalkText("32", tone = AbChalkTone.Dim, pill = true)
                    AbChalkDash()
                    AbChalkAnswer("152", revealed = false)
                }
            }
        }
    }
}
