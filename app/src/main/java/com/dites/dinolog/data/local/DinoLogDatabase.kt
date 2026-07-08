package com.dites.dinolog.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dites.dinolog.data.local.dao.*
import com.dites.dinolog.data.local.entity.*

@Database(
    entities = [
        ReptileEntity::class,
        GrowthLogEntity::class,
        GrowthPhotoEntity::class,
        FeedingLogEntity::class,
        ScuteLogEntity::class,
        SoakingLogEntity::class,
        BrumasiLogEntity::class,
        UvbBasingLogEntity::class,
        DietLogEntity::class,
        SheddingLogEntity::class,
        HealthRecordEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class DinoLogDatabase : RoomDatabase() {

    abstract fun reptileDao(): ReptileDao
    abstract fun growthLogDao(): GrowthLogDao
    abstract fun growthPhotoDao(): GrowthPhotoDao
    abstract fun feedingLogDao(): FeedingLogDao
    abstract fun scuteLogDao(): ScuteLogDao
    abstract fun soakingLogDao(): SoakingLogDao
    abstract fun brumasiLogDao(): BrumasiLogDao
    abstract fun uvbBasingLogDao(): UvbBasingLogDao
    abstract fun dietLogDao(): DietLogDao
    abstract fun sheddingLogDao(): SheddingLogDao
    abstract fun healthRecordDao(): HealthRecordDao

    companion object {
        @Volatile
        private var INSTANCE: DinoLogDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Hapus tabel lama
                database.execSQL("DROP TABLE IF EXISTS shedding_logs")

                // Update feeding_logs — rename columns
                database.execSQL("ALTER TABLE feeding_logs RENAME TO feeding_logs_old")
                database.execSQL("""
                    CREATE TABLE feeding_logs (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        reptileId INTEGER NOT NULL,
                        feedingDate INTEGER NOT NULL,
                        foodType TEXT NOT NULL DEFAULT '',
                        foodAmount TEXT NOT NULL DEFAULT '',
                        accepted INTEGER NOT NULL DEFAULT 1,
                        notes TEXT NOT NULL DEFAULT '',
                        FOREIGN KEY(reptileId) REFERENCES reptiles(id) ON DELETE CASCADE
                    )
                """)
                database.execSQL("""
                    INSERT INTO feeding_logs (id, reptileId, feedingDate, foodType, foodAmount, accepted, notes)
                    SELECT id, reptileId, feedingDate, preyType, '', accepted, notes
                    FROM feeding_logs_old
                """)
                database.execSQL("DROP TABLE feeding_logs_old")

                // Hapus kolom morph dari reptiles
                database.execSQL("ALTER TABLE reptiles RENAME TO reptiles_old")
                database.execSQL("""
                    CREATE TABLE reptiles (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        species TEXT NOT NULL,
                        gender TEXT NOT NULL DEFAULT 'TIDAK_DIKETAHUI',
                        isRescue INTEGER NOT NULL DEFAULT 0,
                        profilePhotoUri TEXT NOT NULL DEFAULT '',
                        birthDate INTEGER,
                        acquireDate INTEGER,
                        createdAt INTEGER NOT NULL
                    )
                """)
                database.execSQL("""
                    INSERT INTO reptiles (id, name, species, gender, isRescue, profilePhotoUri, birthDate, acquireDate, createdAt)
                    SELECT id, name, species, gender, isRescue, profilePhotoUri, birthDate, acquireDate, createdAt
                    FROM reptiles_old
                """)
                database.execSQL("DROP TABLE reptiles_old")

                // Buat tabel baru
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS scute_logs (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        reptileId INTEGER NOT NULL,
                        recordedAt INTEGER NOT NULL,
                        condition TEXT NOT NULL DEFAULT 'NORMAL',
                        notes TEXT NOT NULL DEFAULT '',
                        FOREIGN KEY(reptileId) REFERENCES reptiles(id) ON DELETE CASCADE
                    )
                """)
                database.execSQL("CREATE INDEX IF NOT EXISTS index_scute_logs_reptileId ON scute_logs(reptileId)")

                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS soaking_logs (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        reptileId INTEGER NOT NULL,
                        soakingDate INTEGER NOT NULL,
                        durationMinutes INTEGER NOT NULL,
                        waterTempCelsius REAL,
                        notes TEXT NOT NULL DEFAULT '',
                        FOREIGN KEY(reptileId) REFERENCES reptiles(id) ON DELETE CASCADE
                    )
                """)
                database.execSQL("CREATE INDEX IF NOT EXISTS index_soaking_logs_reptileId ON soaking_logs(reptileId)")

                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS brumasi_logs (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        reptileId INTEGER NOT NULL,
                        startDate INTEGER NOT NULL,
                        endDate INTEGER,
                        weightBeforeGrams REAL,
                        weightAfterGrams REAL,
                        location TEXT NOT NULL DEFAULT '',
                        notes TEXT NOT NULL DEFAULT '',
                        FOREIGN KEY(reptileId) REFERENCES reptiles(id) ON DELETE CASCADE
                    )
                """)
                database.execSQL("CREATE INDEX IF NOT EXISTS index_brumasi_logs_reptileId ON brumasi_logs(reptileId)")

                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS uvb_basing_logs (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        reptileId INTEGER NOT NULL,
                        sessionDate INTEGER NOT NULL,
                        durationMinutes INTEGER NOT NULL,
                        uvbType TEXT NOT NULL DEFAULT '',
                        basikingTempCelsius REAL,
                        notes TEXT NOT NULL DEFAULT '',
                        FOREIGN KEY(reptileId) REFERENCES reptiles(id) ON DELETE CASCADE
                    )
                """)
                database.execSQL("CREATE INDEX IF NOT EXISTS index_uvb_basing_logs_reptileId ON uvb_basing_logs(reptileId)")

                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS diet_logs (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        reptileId INTEGER NOT NULL,
                        recordedAt INTEGER NOT NULL,
                        vegetables TEXT NOT NULL DEFAULT '',
                        fruits TEXT NOT NULL DEFAULT '',
                        supplements TEXT NOT NULL DEFAULT '',
                        notes TEXT NOT NULL DEFAULT '',
                        FOREIGN KEY(reptileId) REFERENCES reptiles(id) ON DELETE CASCADE
                    )
                """)
                database.execSQL("CREATE INDEX IF NOT EXISTS index_diet_logs_reptileId ON diet_logs(reptileId)")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS shedding_logs (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        reptileId INTEGER NOT NULL,
                        sheddingDate INTEGER NOT NULL,
                        status TEXT NOT NULL DEFAULT 'COMPLETE',
                        notes TEXT NOT NULL DEFAULT '',
                        FOREIGN KEY(reptileId) REFERENCES reptiles(id) ON DELETE CASCADE
                    )
                """)
                database.execSQL("CREATE INDEX IF NOT EXISTS index_shedding_logs_reptileId ON shedding_logs(reptileId)")
            }
        }

        fun getInstance(context: Context): DinoLogDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    DinoLogDatabase::class.java,
                    "dinolog.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
