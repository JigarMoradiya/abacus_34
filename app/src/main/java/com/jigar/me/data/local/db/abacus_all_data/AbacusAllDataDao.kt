package com.jigar.me.data.local.db.abacus_all_data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jigar.me.data.model.dbtable.abacus_all_data.Abacus
import com.jigar.me.data.model.dbtable.abacus_all_data.Category
import com.jigar.me.data.model.dbtable.abacus_all_data.DisplayPages
import com.jigar.me.data.model.dbtable.abacus_all_data.Level
import com.jigar.me.data.model.dbtable.abacus_all_data.Pages
import com.jigar.me.data.model.dbtable.abacus_all_data.Set
import com.jigar.me.data.model.dbtable.abacus_all_data.SetProgress
import com.jigar.me.utils.AppConstants
import kotlinx.coroutines.flow.Flow

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
    suspend fun insertSetProgress(item: List<SetProgress>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAbacus(item: List<Abacus>)

    @Query("SELECT * FROM '${AppConstants.DBParam.table_level}' ORDER BY sort_order ASC")
    fun getLevel(): LiveData<List<Level>>
    @Query("SELECT * FROM '${AppConstants.DBParam.table_level}' WHERE name IN (:list) ORDER BY sort_order ASC")
    suspend fun getLevel(list : List<String>): List<Level>
    @Query("SELECT * FROM '${AppConstants.DBParam.table_category}' WHERE level_id = :id AND is_active = 1 ORDER BY sort_order ASC")
//    @Query("SELECT * FROM '${AppConstants.DBParam.table_category}' WHERE level_id = :id ORDER BY sort_order ASC")
    suspend fun getCategory(id : String): List<Category>
    @Query("SELECT * FROM '${AppConstants.DBParam.table_pages}' WHERE category_id = :id ORDER BY sort_order ASC,created_at ASC")
    suspend fun getPages(id : String): List<DisplayPages>
    @Query("SELECT * FROM '${AppConstants.DBParam.table_pages}'")
    suspend fun getAllPages(): List<DisplayPages>
    @Query("SELECT * FROM '${AppConstants.DBParam.table_pages}' WHERE category_id = :id AND is_active = 1 ORDER BY sort_order ASC,created_at ASC")
    suspend fun getPagesOnlyActive(id : String): List<DisplayPages>
    @Query("SELECT * FROM '${AppConstants.DBParam.table_sets}' WHERE page_id = :id ORDER BY sort_order ASC")
    suspend fun getSet(id : String): List<Set>
    @Query("SELECT s.id,s.page_id,s.name,s.answer_setting,s.show_time_setting,s.sort_order,s.created_at,s.is_active,s.description,s.hint,s.latest_abacus_id,s.totals_abacus," +
            "(select exists (select is_set_completed from '${AppConstants.DBParam.table_set_progress}' where set_id = s.id AND is_set_completed = 1)) as is_completed_set," +
            "(select exists (select is_set_completed from '${AppConstants.DBParam.table_set_progress}' where set_id = s.id AND is_set_completed = 0)) as is_running_set FROM '${AppConstants.DBParam.table_sets}' as s WHERE s.id = :setId")
    suspend fun getSetDetail(setId : String): Set?
    @Query("SELECT s.id,s.page_id,s.name,s.answer_setting,s.show_time_setting,s.sort_order,s.created_at,s.is_active,s.description,s.hint,s.latest_abacus_id,s.totals_abacus," +
            "(select exists (select is_set_completed from '${AppConstants.DBParam.table_set_progress}' where set_id = s.id AND is_set_completed = 1)) as is_completed_set," +
            "(select exists (select is_set_completed from '${AppConstants.DBParam.table_set_progress}' where set_id = s.id AND is_set_completed = 0)) as is_running_set FROM '${AppConstants.DBParam.table_sets}' as s WHERE s.id = :setId")
    fun getSetDetailFlow(setId : String): Flow<Set?>

    @Query("SELECT * FROM '${AppConstants.DBParam.table_set_progress}' WHERE set_id = :setId ORDER BY retry_count DESC LIMIT 1")
    suspend fun getSetProgress(setId : String): SetProgress?
    @Query("SELECT * FROM '${AppConstants.DBParam.table_set_progress}' WHERE set_id = :setId ORDER BY retry_count DESC LIMIT 1")
    fun getSetProgressFLow(setId : String): Flow<SetProgress?>
//    @Query("SELECT s.*,(select exists (select is_set_completed from '${AppConstants.DBParam.table_set_progress}' where set_id = s.id AND is_set_completed = 1)) as is_completed_set,(select exists (select is_set_completed from '${AppConstants.DBParam.table_set_progress}' where set_id = s.id AND is_set_completed = 0)) as is_running_set,(select COUNT(*) from '${AppConstants.DBParam.table_abacus}' where set_id = s.id) as totals_abacus FROM '${AppConstants.DBParam.table_sets}' as s WHERE s.is_active = 1 ORDER BY sort_order ASC")
    @Query("SELECT s.id,s.page_id,s.name,s.answer_setting,s.show_time_setting,s.sort_order,s.created_at,s.is_active,s.description,s.hint,s.latest_abacus_id,s.totals_abacus," +
            "(select exists (select is_set_completed from '${AppConstants.DBParam.table_set_progress}' where set_id = s.id AND is_set_completed = 1)) as is_completed_set," +
            "(select exists (select is_set_completed from '${AppConstants.DBParam.table_set_progress}' where set_id = s.id AND is_set_completed = 0)) as is_running_set FROM '${AppConstants.DBParam.table_sets}' as s WHERE s.is_active = 1 ORDER BY sort_order ASC")
    suspend fun getAllSet(): List<Set>

    @Query("""
    SELECT s.id,s.page_id,s.name,s.answer_setting,s.show_time_setting,
           s.sort_order,s.created_at,s.is_active,s.description,s.hint,
           s.latest_abacus_id,s.totals_abacus,
           (SELECT EXISTS (SELECT 1 FROM setProgress WHERE set_id = s.id AND  (retry_count > 1 OR is_set_completed = 1))) AS is_completed_set,
           (SELECT EXISTS (SELECT 1 FROM setProgress WHERE set_id = s.id AND is_set_completed = 0)) AS is_running_set
    FROM sets AS s
    WHERE s.is_active = 1
    ORDER BY sort_order ASC """)
    fun observeAllSets(): Flow<List<Set>>

    @Query("SELECT * FROM '${AppConstants.DBParam.table_abacus}' WHERE set_id = :id ORDER BY created_at ASC")
    suspend fun getAbacus(id : String): List<Abacus>

    @Query("SELECT * FROM '${AppConstants.DBParam.table_abacus}' WHERE set_id = :id ORDER BY created_at ASC")
    fun getAbacusFlow(id : String): Flow<List<Abacus>>
    @Query("SELECT c.name || ' > ' || p.name || ' > ' || s.name FROM '${AppConstants.DBParam.table_sets}' as s LEFT JOIN '${AppConstants.DBParam.table_pages}' as p on (p.id = s.page_id) LEFT JOIN '${AppConstants.DBParam.table_category}' as c on (c.id = p.category_id)  WHERE s.id = :setId")
    suspend fun getParentLevelOfSet(setId : String): String
    @Query("UPDATE '${AppConstants.DBParam.table_sets}' SET totals_abacus = :time WHERE id = :setId")
    suspend fun updateSetTimer(setId : String,time : Long)
    @Query("UPDATE '${AppConstants.DBParam.table_abacus}' SET userAnswer = :userAnswer WHERE id = :abacusId")
    suspend fun updateUserAnswer(abacusId : String,userAnswer : String)
    @Query("UPDATE '${AppConstants.DBParam.table_abacus}' SET userAnswer = null WHERE set_id = :setId")
    suspend fun removeUserAnswer(setId : String)
    @Query("DELETE FROM '${AppConstants.DBParam.table_set_progress}' WHERE set_id = :setId")
    suspend fun deleteSetProgress(setId : String)
    @Query("DELETE FROM '${AppConstants.DBParam.table_abacus}'")
    suspend fun deleteAllAbacus()
    @Query("DELETE FROM '${AppConstants.DBParam.table_level}'")
    suspend fun deleteAllLevel()
    @Query("DELETE FROM '${AppConstants.DBParam.table_pages}'")
    suspend fun deleteAllPages()
    @Query("DELETE FROM '${AppConstants.DBParam.table_category}'")
    suspend fun deleteAllCategory()
    @Query("DELETE FROM '${AppConstants.DBParam.table_sets}'")
    suspend fun deleteAllSet()
    @Query("DELETE FROM '${AppConstants.DBParam.table_set_progress}'")
    suspend fun deleteAllSetProgress()
}