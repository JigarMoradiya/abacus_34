package com.jigar.me.data.model.data

import com.jigar.me.data.model.dbtable.abacus_all_data.Abacus
import com.jigar.me.data.model.dbtable.abacus_all_data.Category
import com.jigar.me.data.model.dbtable.abacus_all_data.Level
import com.jigar.me.data.model.dbtable.abacus_all_data.Pages
import com.jigar.me.data.model.dbtable.abacus_all_data.Set

data class AbacusAllData(
    var levels: List<Level>? = null,
    var categories: List<Category>? = null,
    var pages: List<Pages>? = null,
    var set: List<Set>? = null,
    var abacus: List<Abacus>? = null,
    var last_sync_time: String? = null,
)

