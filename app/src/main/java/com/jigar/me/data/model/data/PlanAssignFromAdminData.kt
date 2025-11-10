package com.jigar.me.data.model.data

data class PlanAssignFromAdminData(
    var id: String? = null,
    var student_id: String? = null,
    var plan_id: String? = null,
    var google_order_id: String? = null,
    var google_plan_id: String? = null,
    var start_date: String? = null,
    var end_date: String? = null,
    var purchased_from: String? = null,
    var status: String? = null
)