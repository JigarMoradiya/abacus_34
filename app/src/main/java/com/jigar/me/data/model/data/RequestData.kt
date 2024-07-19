package com.jigar.me.data.model.data

data class FetchAbacusDataRequest(
    var get_levels: Boolean = false,
    var get_categories: Boolean = false,
    var get_pages: Boolean = false,
    var get_sets: Boolean = false,
    var get_abacus: Boolean = false,
    var last_sync_time: String? = null,
)
data class SignupV2Request(
    var name: String? = null,
    var email: String? = null,
    var password: String? = null,
    var phone: String? = null,
    var country_code: String? = null,
)
data class VerifyEmailRequest(
    var email: String? = null,
    var type: String? = null,
    var otp: String? = null
)
data class LoginRequest(
    var email: String? = null,
    var password: String? = null
)
data class SocialLoginRequest(
    var email: String? = null,
    var token: String? = null
)

data class PurchasedPlanCheckRequest(
    var purchased_plan: ArrayList<GooglePurchasedPlanRequest>? = null
)

data class ForgotPasswordRequest(
    var email: String? = null
)

data class ResetPasswordRequest(
    var email: String? = null,
    var otp: String? = null,
    var password: String? = null
)

data class ChangePasswordRequest(
    var current_password: String? = null,
    var new_password: String? = null
)

data class UpdateProfileRequest(
    var name: String? = null,
    var email: String? = null,
    var country_code: String? = null,
    var phone: String? = null,
    var city: String? = null,
    var state: String? = null,
    var country: String? = null
)
data class ContactUsRequest(
    var type: String? = null,
    var name: String? = null,
    var email: String? = null,
    var phone: String? = null,
    var desription: String? = null,
    var country: String? = null,
    var city: String? = null,
)

data class ResendOTPRequest(
    var email: String? = null,
)
data class SubmitAllExamDataRequest(
    // Exam submit
    var type: String? = null,
    var level: String? = null,
    var sub_type: String? = null,
    var total_time_taken: Int? = null,
    var no_of_questions: Int? = null,
    var no_of_right_answers: Int? = null,
    var theme: String? = null,
    var questions: ArrayList<Any>? = null,

    // Exercise
    var category: String? = null,
    var label: String? = null,
    var allowed_max_time: Int? = null,

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
)

data class QuestionDataRequest(
    var que: String? = null,
    var user_answer: String? = null,
    var is_correct: Boolean? = null,
    var que_type: String? = null,
    var image: String? = null
)

data class GooglePurchasedPlanRequest(
    var google_plan_id: String,
    var google_order_id: String,
    var is_lifetime_plan: Boolean = false,
    var is_all_feature: Boolean = false,
    var start_date: Long = 0L,
    var end_date: Long = 0L,
    var purchase_price: Double = 0.0,
    var purchase_currency: String? = null,
    var no_of_renewals: Int = 0
)

data class PurchasePlanCreateRequest(
    var plan_id: String? = null,
    var currency: String? = null
)
data class PurchaseSuccessRequest(
    var payment_intent_id: String? = null,
)

data class CancelReactivePlanRequest(
    var is_cancelled : Boolean? = null,
    var cancellation_reason : String? = null,
    var cancellation_description : String? = null,
)