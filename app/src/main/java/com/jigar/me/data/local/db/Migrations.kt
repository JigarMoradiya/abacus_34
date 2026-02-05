package com.jigar.me.data.local.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jigar.me.utils.AppConstants

object Migrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {

            database.execSQL("""
            CREATE TABLE set_progress_new (
                set_id TEXT NOT NULL,
                latest_abacus_id TEXT,
                is_set_completed INTEGER NOT NULL,
                total_time_taken INTEGER NOT NULL,
                retry_count INTEGER NOT NULL,
                PRIMARY KEY(set_id)
            )
        """)

            database.execSQL("""
            INSERT INTO set_progress_new (
                set_id,
                latest_abacus_id,
                is_set_completed,
                total_time_taken,
                retry_count
            )
            SELECT
                sp.set_id,
                sp.latest_abacus_id,
                sp.is_set_completed,
                sp.total_time_taken,
                sp.retry_count
            FROM ${AppConstants.DBParam.table_set_progress} sp
            INNER JOIN (
                SELECT
                    set_id,
                    MAX(retry_count) AS max_retry
                FROM ${AppConstants.DBParam.table_set_progress}
                GROUP BY set_id
            ) grouped
            ON sp.set_id = grouped.set_id
            AND sp.retry_count = grouped.max_retry
        """)

            database.execSQL("DROP TABLE ${AppConstants.DBParam.table_set_progress}")

            database.execSQL("""
            ALTER TABLE set_progress_new
            RENAME TO ${AppConstants.DBParam.table_set_progress}
        """)
        }
    }

}