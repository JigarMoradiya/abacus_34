package com.jigar.me.data.local.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE `ExamHistory` (`id` INTEGER NOT NULL, `examTotalTime` INTEGER NOT NULL, `examType` TEXT NOT NULL, `examDetails` TEXT NOT NULL,`examBeginners` TEXT NOT NULL, `addedOn` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        }
    }
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE `Suduko` (`id` INTEGER NOT NULL, `cellPosition` TEXT NOT NULL, `cellValue` TEXT NOT NULL, `roomID` TEXT NOT NULL,`notes` TEXT NOT NULL, `addedOn` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE `Suduko_Play` (`id` INTEGER NOT NULL, `cellPosition` TEXT NOT NULL, `cellValue` TEXT NOT NULL, `roomID` TEXT NOT NULL,`notes` TEXT NOT NULL, `level` TEXT NOT NULL, `defaultSet` TEXT NOT NULL, `valueStatus` TEXT NOT NULL, `addedOn` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE `Suduko_AnswerStatus` (`id` INTEGER NOT NULL, `cellPosition` TEXT NOT NULL, `cellValue` TEXT NOT NULL, `roomID` TEXT NOT NULL,`otherCellPosition` TEXT NOT NULL, `addedOn` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE `Suduko_Level` (`id` INTEGER NOT NULL, `level` TEXT NOT NULL, `status` TEXT NOT NULL, `roomID` TEXT NOT NULL,`playTime` TEXT NOT NULL, `addedOn` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE tableInAppSKU ADD COLUMN offerToken TEXT;")
        }
    }
    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE tableInAppPurchase ADD COLUMN isAutoRenewing INTEGER not null default 0;")
        }
    }
    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE tableInAppSKU ADD COLUMN billingPeriod TEXT;")
        }
    }
    val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE tableInAppSKU ADD COLUMN originalPrice TEXT;")
            database.execSQL("ALTER TABLE tableInAppSKU ADD COLUMN discountPer TEXT;")
        }
    }
    val MIGRATION_7_8 = object : Migration(7, 8) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE ExamHistory ADD COLUMN theme TEXT;")
        }
    }
    val MIGRATION_8_9 = object : Migration(8, 9) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE ExamHistory ADD COLUMN examFor TEXT;")
        }
    }
    val MIGRATION_9_10 = object : Migration(9, 10) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE tableInAppSKU ADD COLUMN sortOrder INTEGER not null default 0;")
        }
    }

}