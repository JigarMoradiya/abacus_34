package com.jigar.me.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jigar.me.BuildConfig
import com.jigar.me.data.local.db.Migrations.MIGRATION_1_2
import com.jigar.me.data.local.db.Migrations.MIGRATION_2_3
import com.jigar.me.data.local.db.Migrations.MIGRATION_3_4
import com.jigar.me.data.local.db.abacus_all_data.AbacusAllDataDao
import com.jigar.me.data.model.dbtable.abacus_all_data.Abacus
import com.jigar.me.data.model.dbtable.abacus_all_data.Category
import com.jigar.me.data.model.dbtable.abacus_all_data.Level
import com.jigar.me.data.model.dbtable.abacus_all_data.Pages
import com.jigar.me.data.model.dbtable.abacus_all_data.Set
import com.jigar.me.data.model.dbtable.abacus_all_data.SetProgress
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.Constants
import com.jigar.me.utils.DataTypeConverter
import android.database.sqlite.SQLiteDatabaseCorruptException
import net.zetetic.database.sqlcipher.SQLiteNotADatabaseException
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors


@Database(
    entities = [Level::class, Category::class, Pages::class, Set::class, SetProgress::class, Abacus::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(DataTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
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
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            if (!BuildConfig.DEBUG){
                database.openHelperFactory(SelfHealingOpenHelperFactory(factory, context.applicationContext))
            }

            val databaseBuild = database.build()
            return databaseBuild
        }

        private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

        /** The "file is not a database" family (wrong key, damaged or half-written file), including a wrapped cause. */
        internal fun isUnreadableDatabase(error: Throwable): Boolean {
            var t: Throwable? = error
            while (t != null) {
                val message = t.message.orEmpty()
                if (t is SQLiteNotADatabaseException || t is SQLiteDatabaseCorruptException ||
                    message.contains("not a database", ignoreCase = true) || message.contains("malformed", ignoreCase = true)
                ) return true
                t = t.cause
            }
            return false
        }

        /**
         * Utility method to run blocks on a dedicated background thread, used for io/database work.
         */
        fun ioThread(f: () -> Unit) {
            IO_EXECUTOR.execute(f)
        }

    }
}

/**
 * Opens the encrypted database and, when the file cannot be read ("file is not a database", seen in Crashlytics now and then and not
 * on every launch), recovers by itself instead of crashing: one more try first (the failure may be a one-off), then the unreadable
 * file is removed (with its -wal / -shm files) and a fresh one is created. Everything in it is downloaded from the server, so the
 * next sync (reset to "everything") fills it again: lessons, pages and the user's progress.
 */
private class SelfHealingOpenHelperFactory(
    private val delegate: SupportSQLiteOpenHelper.Factory,
    private val context: Context,
) : SupportSQLiteOpenHelper.Factory {

    override fun create(configuration: SupportSQLiteOpenHelper.Configuration): SupportSQLiteOpenHelper {
        val inner = delegate.create(configuration)
        return object : SupportSQLiteOpenHelper by inner {
            override val writableDatabase: SupportSQLiteDatabase
                get() = openOrRebuild(inner.databaseName) { inner.writableDatabase }
            override val readableDatabase: SupportSQLiteDatabase
                get() = openOrRebuild(inner.databaseName) { inner.readableDatabase }
        }
    }

    private fun <T> openOrRebuild(name: String?, open: () -> T): T {
        try {
            return open()
        } catch (first: Throwable) {
            if (!AppDatabase.isUnreadableDatabase(first)) throw first
            // a one-off failure may not come back: try once more before touching the file
            try {
                return open().also { report(first, rebuilt = false) }
            } catch (second: Throwable) {
                if (!AppDatabase.isUnreadableDatabase(second)) throw second
                // really unreadable: remove it and start a fresh one, then download everything again at the next sync
                name?.let { context.deleteDatabase(it) }
                AppPreferencesHelper(context, AppConstants.PREF_NAME).setCustomParam(Constants.last_sync_time, Constants.last_sync_default_time)
                report(second, rebuilt = true)
                return open()
            }
        }
    }

    private fun report(error: Throwable, rebuilt: Boolean) {
        runCatching {
            FirebaseCrashlytics.getInstance().log("database was unreadable, rebuilt=$rebuilt")
            FirebaseCrashlytics.getInstance().recordException(error)   // a non-fatal event: shows how often this happens
        }
    }
}
