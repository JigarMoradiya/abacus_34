package com.jigar.me.data.local.db.abacus_all_data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jigar.me.data.model.dbtable.abacus_all_data.Abacus
import com.jigar.me.data.model.dbtable.abacus_all_data.Category
import com.jigar.me.data.model.dbtable.abacus_all_data.Level
import com.jigar.me.data.model.dbtable.abacus_all_data.Pages
import com.jigar.me.data.model.dbtable.abacus_all_data.Set
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import com.jigar.me.utils.AppConstants

@Dao
interface AbacusAllDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLevel(item: List<Level>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategory(item: List<Category>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPages(item: List<Pages>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSet(item: List<Set>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAbacus(item: List<Abacus>)

    @Query("SELECT * FROM '${AppConstants.DBParam.table_level}' ORDER BY sort_order ASC")
    fun getLevel(): LiveData<List<Level>>
    @Query("SELECT * FROM '${AppConstants.DBParam.table_category}' WHERE level_id = :id ORDER BY sort_order ASC")
    suspend fun getCategory(id : String): List<Category>
    @Query("SELECT * FROM '${AppConstants.DBParam.table_pages}' WHERE category_id = :id ORDER BY sort_order ASC")
    suspend fun getPages(id : String): List<Pages>
    @Query("SELECT * FROM '${AppConstants.DBParam.table_sets}' WHERE page_id = :id ORDER BY sort_order ASC")
    suspend fun getSet(id : String): List<Set>
    @Query("SELECT * FROM '${AppConstants.DBParam.table_sets}' ORDER BY sort_order ASC")
    suspend fun getAllSet(): List<Set>
    @Query("SELECT * FROM '${AppConstants.DBParam.table_abacus}' WHERE set_id = :id")
    suspend fun getAbacus(id : String): List<Abacus>
}