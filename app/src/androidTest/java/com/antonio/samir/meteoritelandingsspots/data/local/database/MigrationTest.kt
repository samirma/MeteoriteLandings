package com.antonio.samir.meteoritelandingsspots.data.local.database

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Exercises [MIGRATION_1_2] against a v1 database built to match the shipped
 * `meteorites_v2.db` asset, which is where every install starts.
 *
 * The v1 table is created by hand rather than through MigrationTestHelper so the test pins the
 * *asset's* real schema — TEXT coordinates and no exported v1 schema file.
 */
@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private val databaseName = "migration-test.db"
    private lateinit var helper: SupportSQLiteOpenHelper

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        context.deleteDatabase(databaseName)
        helper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(databaseName)
                .callback(object : SupportSQLiteOpenHelper.Callback(1) {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        db.execSQL(
                            """
                            CREATE TABLE `meteorites` (
                                `id` INTEGER NOT NULL, `mass` TEXT, `nametype` TEXT,
                                `recclass` TEXT, `name` TEXT, `fall` TEXT, `year` TEXT,
                                `reclong` TEXT, `reclat` TEXT, `address` TEXT,
                                PRIMARY KEY(`id`)
                            )
                            """.trimIndent()
                        )
                        db.execSQL("CREATE INDEX `index_meteorites_id` ON `meteorites` (`id`)")
                    }

                    override fun onUpgrade(
                        db: SupportSQLiteDatabase,
                        oldVersion: Int,
                        newVersion: Int,
                    ) = Unit
                })
                .build()
        )
    }

    @After
    fun tearDown() {
        helper.close()
        context.deleteDatabase(databaseName)
    }

    @Test
    fun migration_1_2_convertsCoordinatesRepairsAddressesAndDropsTheRedundantIndex() {
        helper.writableDatabase.apply {
            execSQL(
                """
                INSERT INTO meteorites (id, mass, nametype, recclass, name, fall, year, reclong, reclat, address)
                VALUES
                  (1, '21', 'Valid', 'L5', 'Aachen', 'Fell', '1880-01-01T00:00:00.000', '6.083330', '50.775000', 'Aachen, Germany'),
                  (2, '720', 'Valid', 'H6', 'Aarhus', 'Fell', '1951-01-01T00:00:00.000', '10.233330', '56.183330', ' '),
                  (3, null, 'Valid', 'L6', 'Nowhere', 'Found', null, '', '', null)
                """.trimIndent()
            )

            MIGRATION_1_2.migrate(this)

            // Coordinates are REAL now, so arithmetic in the ordering query is exact.
            query("SELECT typeof(reclat), typeof(reclong), reclat FROM meteorites WHERE id = 1").use {
                it.moveToFirst()
                assertEquals("real", it.getString(0))
                assertEquals("real", it.getString(1))
                assertEquals(50.775, it.getDouble(2), 0.0001)
            }

            // Empty coordinate strings become NULL rather than a spurious 0,0 in the Atlantic.
            query("SELECT reclat, reclong FROM meteorites WHERE id = 3").use {
                it.moveToFirst()
                assertNull(it.getString(0))
                assertNull(it.getString(1))
            }

            // A real address survives; the old " " failure sentinel is reset so the row can be
            // geocoded again.
            query("SELECT address FROM meteorites WHERE id = 1").use {
                it.moveToFirst()
                assertEquals("Aachen, Germany", it.getString(0))
            }
            query("SELECT address FROM meteorites WHERE id = 2").use {
                it.moveToFirst()
                assertNull(it.getString(0))
            }

            query("SELECT count(*) FROM meteorites").use {
                it.moveToFirst()
                assertEquals(3, it.getInt(0))
            }

            query("SELECT name FROM sqlite_master WHERE type='index' AND name='index_meteorites_id'").use {
                assertFalse("the redundant id index should be gone", it.moveToFirst())
            }
        }
    }
}
