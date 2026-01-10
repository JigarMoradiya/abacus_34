package com.jigar.me.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jigar.me.BuildConfig
import com.jigar.me.data.local.db.abacus_all_data.AbacusAllDataDao
import com.jigar.me.data.local.db.exam.ExamHistoryDao
import com.jigar.me.data.local.db.inapp.purchase.InAppPurchaseDao
import com.jigar.me.data.local.db.inapp.sku.InAppSKUDao
import com.jigar.me.data.model.dbtable.abacus_all_data.Abacus
import com.jigar.me.data.model.dbtable.abacus_all_data.Category
import com.jigar.me.data.model.dbtable.abacus_all_data.Level
import com.jigar.me.data.model.dbtable.abacus_all_data.Pages
import com.jigar.me.data.model.dbtable.abacus_all_data.Set
import com.jigar.me.data.model.dbtable.abacus_all_data.SetProgress
import com.jigar.me.data.model.dbtable.exam.ExamHistory
import com.jigar.me.data.model.dbtable.inapp.InAppPurchaseDetails
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.DataTypeConverter
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors


@Database(
    entities = [InAppSkuDetails::class,InAppPurchaseDetails::class, ExamHistory::class
         ,Level::class, Category::class, Pages::class, Set::class, SetProgress::class, Abacus::class],
    version = 1,
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

        private fun buildDatabase(context: Context) : AppDatabase{
            val passphrase: ByteArray = CommonUtils.getDatabaseKey().toByteArray(StandardCharsets.UTF_8)
              val factory = SupportOpenHelperFactory(passphrase)

            val database  = Room.databaseBuilder(context, AppDatabase::class.java, AppConstants.DB_NAME_NEW)
            if (!BuildConfig.DEBUG){
                database.openHelperFactory(factory)
            }

            val databaseBuild = database.build()
            return databaseBuild
        }

        private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

        /**
         * Utility method to run blocks on a dedicated background thread, used for io/database work.
         */
        fun ioThread(f: () -> Unit) {
            IO_EXECUTOR.execute(f)
        }

    }
}
