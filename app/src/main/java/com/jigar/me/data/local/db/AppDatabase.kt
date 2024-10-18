package com.jigar.me.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jigar.me.data.local.db.abacus_all_data.AbacusAllDataDao
import com.jigar.me.data.local.db.exam.ExamHistoryDao
import com.jigar.me.data.local.db.inapp.purchase.InAppPurchaseDao
import com.jigar.me.data.local.db.inapp.sku.InAppSKUDao
import com.jigar.me.data.model.dbtable.abacus_all_data.Abacus
import com.jigar.me.data.model.dbtable.abacus_all_data.Category
import com.jigar.me.data.model.dbtable.abacus_all_data.Level
import com.jigar.me.data.model.dbtable.abacus_all_data.Pages
import com.jigar.me.data.model.dbtable.abacus_all_data.Set
import com.jigar.me.data.model.dbtable.exam.ExamHistory
import com.jigar.me.data.model.dbtable.inapp.InAppPurchaseDetails
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Constants
import com.jigar.me.utils.DataTypeConverter
import java.util.concurrent.Executors


@Database(
    entities = [InAppSkuDetails::class,InAppPurchaseDetails::class, ExamHistory::class
         ,Level::class, Category::class, Pages::class, Set::class, Abacus::class],
    version = 14,
    exportSchema = false
)
@TypeConverters(DataTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun inAppSKUDao(): InAppSKUDao
    abstract fun inAppPurchaseDao(): InAppPurchaseDao
    abstract fun examHistoryDao(): ExamHistoryDao
    abstract fun abacusAllDataDao(): AbacusAllDataDao
    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
        }

        fun buildDatabase(context: Context) = Room.databaseBuilder(context, AppDatabase::class.java, AppConstants.DB_NAME)
            // Delete Database, when something changed
            .fallbackToDestructiveMigration()
            .addMigrations(
                Migrations.MIGRATION_1_2,
                Migrations.MIGRATION_2_3,
                Migrations.MIGRATION_3_4,
                Migrations.MIGRATION_4_5,
                Migrations.MIGRATION_5_6,
                Migrations.MIGRATION_6_7,
                Migrations.MIGRATION_7_8,
                Migrations.MIGRATION_8_9,
                Migrations.MIGRATION_9_10,
                Migrations.MIGRATION_10_11
            )
            .addCallback(
                object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // insert the data on the IO Thread
                        ioThread {

                        }
                    }
                }
            )
            .build()

        private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

        /**
         * Utility method to run blocks on a dedicated background thread, used for io/database work.
         */
        fun ioThread(f: () -> Unit) {
            IO_EXECUTOR.execute(f)
        }

    }
}
