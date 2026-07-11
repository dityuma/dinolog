package com.dites.dinolog

import android.app.Application
import com.dites.dinolog.data.local.DinoLogDatabase
import com.dites.dinolog.data.repository.DinoLogRepository

class DinoLogApplication : Application() {
    val database by lazy { DinoLogDatabase.getInstance(this) }
    val repository by lazy {
        DinoLogRepository(
            database.reptileDao(),
            database.growthLogDao(),
            database.growthPhotoDao(),
            database.feedingLogDao(),
            database.scuteLogDao(),
            database.soakingLogDao(),
            database.brumasiLogDao(),
            database.uvbBasingLogDao(),
            database.dietLogDao(),
            database.healthRecordDao(),
            database.scutePhotoDao(),
            database.riwayatDao(),
            database.riwayatPhotoDao()
        )
    }
}
