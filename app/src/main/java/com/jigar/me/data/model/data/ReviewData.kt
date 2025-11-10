package com.jigar.me.data.model.data

data class ReviewData(
    var id: String? = null,
    var description: String? = null,
    var image_1: String? = null,
    var plan_id: String? = null,

    var comment: String? = null,
    var status: String? = null, // REJECT
)