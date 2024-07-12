package com.jigar.me.data.model.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jigar.me.data.local.data.BeginnerExamPaper
import com.jigar.me.data.local.data.BeginnerExamQuestionType
import com.jigar.me.data.local.data.ExamProvider
import com.jigar.me.data.local.data.ExerciseList
import com.jigar.me.data.local.data.ImagesDataObjects
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Calculator
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.DateTimeUtils
import com.jigar.me.utils.extensions.isNotNullOrEmpty
import com.jigar.me.utils.extensions.secToTimeFormat

data class Statistics(
    var EXAM: StatisticsData? = null,
    var EXERCISE: StatisticsData? = null,
    var CCM: StatisticsData? = null,
    var PRACTISE: StatisticsData? = null,
    var timestamp: Long = 0
)

data class StatisticsData(
    var count: Any? = null,
    var last_exam_given_time: String? = null,
    var can_give_exam: Boolean = true,
)

data class AllExamData(
    var id: String? = null,
    var user_id: String? = null,

    // Exam submit
    var type: String? = null,
    var level: String? = null,
    var sub_type: String? = null,
    var total_time_taken: Int? = null,
    var no_of_questions: Int? = null,
    var no_of_right_answers: Int? = null,
    var theme: String? = null,
    var questions: String? = null,

    // Exercise
    var category: String? = null,
    var label: String? = null,
    var allowed_max_time: String? = null,

    // Practise
    var answer_type: String? = null,
    var is_left_end: Boolean? = null,
    var is_display_abacus_number: Boolean? = null,
    var is_abacus_hint_sount_enable: Boolean? = null,

    // CCM
    var total_set_of_question: Int? = null,
    var total_numbers_in_set: Int? = null,
    var gap_between_two_question: Int? = null,
    var question_min_length: Int? = null,
    var question_max_length: Int? = null,
    var is_question_speak: Boolean? = null,
    var is_question_show_in_number: Boolean? = null,
    var is_question_show_in_word: Boolean? = null,

    var created_at: String? = null,
) {
    fun dateTimeFormat() = DateTimeUtils.convertDateFormatFromUTC(created_at,DateTimeUtils.yyyy_MM_dd_T_HH_mm_ss_sssz,DateTimeUtils.at_dd_mmm_yy_hh_mm_a)
    fun totalTimeFormat() = (total_time_taken ?: 0).secToTimeFormat()
    fun maxTimeFormat() = (allowed_max_time ?: "0").toInt().secToTimeFormat()
    fun isAnswerCorrect() = correctAns() == userAns()
    fun fullQuestion(): String {
        var question = ""
        val listType = object : TypeToken<List<QuestionDataRequest>?>() {}.type
        val list: List<QuestionDataRequest> = Gson().fromJson(questions, listType)
        if (list.isNotNullOrEmpty()) {
            list.first().que?.let {
                question = it
            }
        }
        return question.replace("+", " + ").replace("-", " - ")
    }

    fun correctAns(): String {
        val resultObject = Calculator().getResult(fullQuestion(), fullQuestion())
        return CommonUtils.removeTrailingZero(resultObject)
    }

    fun userAns(): String {
        var userAns = ""
        val listType = object : TypeToken<List<QuestionDataRequest>?>() {}.type
        val list: List<QuestionDataRequest> = Gson().fromJson(questions, listType)
        if (list.isNotNullOrEmpty()) {
            userAns = list.first().user_answer ?: ""
        }
        return userAns
    }

    fun toExerciseResult(): ArrayList<ExerciseList> {
        val exerciseList: ArrayList<ExerciseList> = arrayListOf()
        val listType = object : TypeToken<List<QuestionDataRequest>?>() {}.type
        val list: List<QuestionDataRequest> = Gson().fromJson(questions, listType)
        if (list.isNotNullOrEmpty()) {
            list.map {
                with(it) {
                    val question = que ?: "0"
                    val resultObject = Calculator().getResult(question, question)
                    val correctAns = CommonUtils.removeTrailingZero(resultObject)
                    val userAnswer = if (user_answer.isNullOrEmpty()){"0"}else{user_answer?:"0"}
                    exerciseList.add(ExerciseList(question,correctAns.toInt(),userAnswer.toInt()))
                }
            }
        }
        return exerciseList
    }

    fun toExamResult(): ArrayList<BeginnerExamPaper> {
        val examBeginners: ArrayList<BeginnerExamPaper> = arrayListOf()
        val listType = object : TypeToken<List<QuestionDataRequest>?>() {}.type
        val list: List<QuestionDataRequest> = Gson().fromJson(questions, listType)
        if (list.isNotNullOrEmpty()) {
            list.map {
                with(it) {
                    val questionType = if (que?.contains("x") == true) {
                        BeginnerExamQuestionType.Multiplication
                    } else if (que?.contains("/") == true) {
                        BeginnerExamQuestionType.Division
                    } else if (que?.contains("-") == true) {
                        BeginnerExamQuestionType.Subtractions
                    } else if (que?.contains("+") == true) {
                        BeginnerExamQuestionType.Additions
                    } else {
                        BeginnerExamQuestionType.Count
                    }
                    var value = ""
                    var value2 = ""
                    var imageData: ImagesDataObjects? = null
                    val isAbacusQuestion =
                        if (que_type == AppConstants.ExamType.exam_Que_type_abacus) {
                            que?.let {
                                if (it.contains("-")) {
                                    val queList = it.split("-")
                                    value = queList[0]
                                    value2 = queList[1]
                                } else if (it.contains("+")) {
                                    val queList = it.split("+")
                                    value = queList[0]
                                    value2 = queList[1]
                                } else {
                                    value = it
                                }
                            }
                            true
                        } else if (que_type == AppConstants.ExamType.exam_Que_type_object) {
                            imageData = ExamProvider.findDataObjects(image)
                            que?.let {
                                if (it.contains("-")) {
                                    val queList = it.split("-")
                                    value = queList[0]
                                    value2 = queList[1]
                                } else if (it.contains("+")) {
                                    val queList = it.split("+")
                                    value = queList[0]
                                    value2 = queList[1]
                                } else {
                                    value = it
                                }
                            }
                            false
                        } else {
                            value = que ?: ""
                            false
                        }
                    examBeginners.add(
                        BeginnerExamPaper(
                            questionType,
                            value = value,
                            value2 = value2,
                            imageData = imageData,
                            userAnswer = user_answer,
                            isAbacusQuestion = isAbacusQuestion
                        )
                    )
                }
            }
        }
        return examBeginners
    }
}