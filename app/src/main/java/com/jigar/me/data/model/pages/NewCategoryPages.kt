package com.jigar.me.data.model.pages

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class CategoryPages(
    var level_id: String? = null, // 2 = addition, 3 = addition subtraction, 5 = number, multiplication = 7, division = 8
    var category_id: String? = null,
    var category_name: String? = null,
    var category_name_ar: String? = null,
    var pages: List<Pages> = arrayListOf()
)

@Parcelize
data class Pages(
    var page_id: String? = null,
    var category_id: String? = null,
    var page_name: String? = null,
    var descriptions: String? = null,
    var hint: String? = null,

    var file: String? = null, // for addition subtraction

    var page_name_ar: String? = null,
    var descriptions_ar: String? = null,

    val from: Int = 0, // for number
    val to: Int = 100, // for number
    val type_random: Boolean = false, // for number

    val que2_str: String = "", // for multiplication and division
    val que2_type: String = "", // for multiplication and division
    val que1_digit_type: Int = 0, // for multiplication

    val isGenerate: Boolean = false, // addition subtraction local generate
    val maxQuestionLength : Int = 4, // addition subtraction local generate
    val totalSubQuestion : Int = 4, // addition subtraction local generate

    var ref_pages: String? = null, // for mix formula

    // common
    var answer_setting: String? = null,
    var timer_setting: String? = null
) : Parcelable
