package com.jigar.me.ui.view.jetpack.api.repository

import com.jigar.me.data.model.data.SubmitAllExamDataRequest
import kotlinx.coroutines.flow.Flow

interface ExamNewRepository {
    fun submitExamData(params: SubmitAllExamDataRequest): Flow<Unit>
}