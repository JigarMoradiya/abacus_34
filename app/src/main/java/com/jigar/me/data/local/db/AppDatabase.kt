package com.jigar.me.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jigar.me.data.local.db.exam.ExamHistoryDao
import com.jigar.me.data.local.db.inapp.purchase.InAppPurchaseDao
import com.jigar.me.data.local.db.inapp.sku.InAppSKUDao
import com.jigar.me.data.model.dbtable.exam.ExamHistory
import com.jigar.me.data.model.dbtable.inapp.InAppPurchaseDetails
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import com.jigar.me.data.model.dbtable.suduko.Suduko
import com.jigar.me.data.model.dbtable.suduko.SudukoAnswerStatus
import com.jigar.me.data.model.dbtable.suduko.SudukoLevel
import com.jigar.me.data.model.dbtable.suduko.SudukoPlay
import com.jigar.me.utils.DataTypeConverter


@Database(
    entities = [InAppSkuDetails::class,InAppPurchaseDetails::class, ExamHistory::class
        , Suduko::class, SudukoPlay::class, SudukoAnswerStatus::class, SudukoLevel::class],
    version = 10,
    exportSchema = false
)
@TypeConverters(DataTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun inAppSKUDao(): InAppSKUDao
    abstract fun inAppPurchaseDao(): InAppPurchaseDao
    abstract fun examHistoryDao(): ExamHistoryDao
}
