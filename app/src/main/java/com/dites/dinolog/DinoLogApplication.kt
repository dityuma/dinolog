package com.dites.dinolog

import android.app.Application
import coil.Coil
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.dites.dinolog.data.local.DinoLogDatabase
import com.dites.dinolog.data.repository.DinoLogRepository

class DinoLogApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val imageLoader = ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.20)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(50 * 1024 * 1024) // 50MB
                    .build()
            }
            .crossfade(true)
            .build()

        Coil.setImageLoader(imageLoader)
    }

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
