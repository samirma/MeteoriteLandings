package com.antonio.samir.meteoritelandingsspots.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * v1 -> v2
 *
 * Three fixes, all of which need a table rebuild in SQLite:
 *  - `reclat`/`reclong` become REAL. They are used in arithmetic by the distance ordering, so
 *    storing them as TEXT meant a per-row coercion and sorted malformed values as 0.
 *  - Drops `index_meteorites_id`, which was redundant: `id INTEGER PRIMARY KEY` is a rowid alias.
 *  - Rewrites the `" "` addresses that the old geocoder wrote on failure back to NULL. They did
 *    not satisfy `LENGTH(address) = 0`, so those rows were permanently excluded from retry.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `meteorites_new` (
                `id` INTEGER NOT NULL,
                `mass` TEXT,
                `nametype` TEXT,
                `recclass` TEXT,
                `name` TEXT,
                `fall` TEXT,
                `year` TEXT,
                `reclong` REAL,
                `reclat` REAL,
                `address` TEXT,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO `meteorites_new`
                (`id`, `mass`, `nametype`, `recclass`, `name`, `fall`, `year`, `reclong`, `reclat`, `address`)
            SELECT
                `id`, `mass`, `nametype`, `recclass`, `name`, `fall`, `year`,
                CASE WHEN `reclong` IS NULL OR TRIM(`reclong`) = '' THEN NULL ELSE CAST(`reclong` AS REAL) END,
                CASE WHEN `reclat`  IS NULL OR TRIM(`reclat`)  = '' THEN NULL ELSE CAST(`reclat`  AS REAL) END,
                NULLIF(TRIM(`address`), '')
            FROM `meteorites`
            """.trimIndent()
        )
        db.execSQL("DROP TABLE `meteorites`")
        db.execSQL("ALTER TABLE `meteorites_new` RENAME TO `meteorites`")
    }
}
