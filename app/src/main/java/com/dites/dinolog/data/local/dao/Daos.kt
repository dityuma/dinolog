package com.dites.dinolog.data.local.dao

import androidx.room.*
import com.dites.dinolog.data.local.entity.*
import kotlinx.coroutines.flow.Flow

// ─────────────────────────────────────────────
// ReptileDao
// ─────────────────────────────────────────────
@Dao
interface ReptileDao {

    @Query("SELECT * FROM reptiles ORDER BY name ASC")
    fun getAllReptiles(): Flow<List<ReptileEntity>>

    @Query("SELECT * FROM reptiles WHERE id = :id")
    fun getReptileById(id: Long): Flow<ReptileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReptile(reptile: ReptileEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReptiles(reptiles: List<ReptileEntity>)

    @Update
    suspend fun updateReptile(reptile: ReptileEntity)

    @Delete
    suspend fun deleteReptile(reptile: ReptileEntity)

    @Query("SELECT COUNT(*) FROM reptiles")
    suspend fun getReptileCount(): Int

    @Query("SELECT * FROM reptiles")
    suspend fun getAllReptilesSync(): List<ReptileEntity>
}

// ─────────────────────────────────────────────
// GrowthLogDao
// ─────────────────────────────────────────────
@Dao
interface GrowthLogDao {

    @Query("SELECT * FROM growth_logs WHERE reptileId = :reptileId ORDER BY recordedAt DESC")
    fun getLogsForReptile(reptileId: Long): Flow<List<GrowthLogEntity>>

    @Query("SELECT * FROM growth_logs WHERE id = :id")
    suspend fun getLogById(id: Long): GrowthLogEntity?

    // Ambil log terbaru untuk tampil di profile card
    @Query("SELECT * FROM growth_logs WHERE reptileId = :reptileId ORDER BY recordedAt DESC LIMIT 1")
    suspend fun getLatestLog(reptileId: Long): GrowthLogEntity?

    // Data untuk grafik berat — ambil semua titik data
    @Query("SELECT recordedAt, weightGrams FROM growth_logs WHERE reptileId = :reptileId AND weightGrams IS NOT NULL ORDER BY recordedAt ASC")
    fun getWeightHistory(reptileId: Long): Flow<List<WeightPoint>>

    @Query("SELECT recordedAt, lengthCm FROM growth_logs WHERE reptileId = :reptileId AND lengthCm IS NOT NULL ORDER BY recordedAt ASC")
    fun getLengthHistory(reptileId: Long): Flow<List<LengthPoint>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: GrowthLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<GrowthLogEntity>)

    @Update
    suspend fun updateLog(log: GrowthLogEntity)

    @Delete
    suspend fun deleteLog(log: GrowthLogEntity)

    @Query("SELECT COUNT(*) FROM growth_logs WHERE reptileId = :reptileId")
    suspend fun getLogCount(reptileId: Long): Int

    @Query("SELECT * FROM growth_logs WHERE reptileId = :reptileId")
    suspend fun getLogsForReptileSync(reptileId: Long): List<GrowthLogEntity>
}

// Data class untuk query grafik (bukan entity Room)
data class WeightPoint(val recordedAt: Long, val weightGrams: Float)
data class LengthPoint(val recordedAt: Long, val lengthCm: Float)

// ─────────────────────────────────────────────
// GrowthPhotoDao
// ─────────────────────────────────────────────
@Dao
interface GrowthPhotoDao {

    @Query("SELECT * FROM growth_photos WHERE growthLogId = :logId ORDER BY takenAt ASC")
    fun getPhotosForLog(logId: Long): Flow<List<GrowthPhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: GrowthPhotoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<GrowthPhotoEntity>)

    @Delete
    suspend fun deletePhoto(photo: GrowthPhotoEntity)

    @Query("DELETE FROM growth_photos WHERE growthLogId = :logId")
    suspend fun deletePhotosForLog(logId: Long)

    @Query("SELECT * FROM growth_photos WHERE growthLogId = :logId ORDER BY takenAt ASC")
    suspend fun getPhotosForLogSync(logId: Long): List<GrowthPhotoEntity>
}

// ─────────────────────────────────────────────
// FeedingLogDao
// ─────────────────────────────────────────────
@Dao
interface FeedingLogDao {

    @Query("SELECT * FROM feeding_logs WHERE reptileId = :reptileId ORDER BY feedingDate DESC")
    fun getFeedingsForReptile(reptileId: Long): Flow<List<FeedingLogEntity>>

    // Feeding terakhir — untuk hitung interval di profile card
    @Query("SELECT * FROM feeding_logs WHERE reptileId = :reptileId ORDER BY feedingDate DESC LIMIT 1")
    suspend fun getLastFeeding(reptileId: Long): FeedingLogEntity?

    // Hitung refused feed dalam 30 hari terakhir
    @Query("SELECT COUNT(*) FROM feeding_logs WHERE reptileId = :reptileId AND accepted = 0 AND feedingDate >= :since")
    suspend fun getRefusedFeedCount(reptileId: Long, since: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeeding(feeding: FeedingLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedings(feedings: List<FeedingLogEntity>)

    @Update
    suspend fun updateFeeding(feeding: FeedingLogEntity)

    @Delete
    suspend fun deleteFeeding(feeding: FeedingLogEntity)

    @Query("SELECT * FROM feeding_logs WHERE reptileId = :reptileId")
    suspend fun getFeedingsForReptileSync(reptileId: Long): List<FeedingLogEntity>
}

// ─────────────────────────────────────────────
// ScuteLogDao (PRD v3)
// ─────────────────────────────────────────────
@Dao
interface ScuteLogDao {
    @Query("SELECT * FROM scute_logs WHERE reptileId = :reptileId ORDER BY recordedAt DESC")
    fun getScuteLogsForReptile(reptileId: Long): Flow<List<ScuteLogEntity>>

    @Query("SELECT * FROM scute_logs WHERE reptileId = :reptileId ORDER BY recordedAt DESC LIMIT 1")
    suspend fun getLatestScuteLog(reptileId: Long): ScuteLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScuteLog(log: ScuteLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScuteLogs(logs: List<ScuteLogEntity>)

    @Update
    suspend fun updateScuteLog(log: ScuteLogEntity)

    @Delete
    suspend fun deleteScuteLog(log: ScuteLogEntity)

    @Query("SELECT * FROM scute_logs WHERE reptileId = :reptileId")
    suspend fun getScuteLogsForReptileSync(reptileId: Long): List<ScuteLogEntity>
}

// ─────────────────────────────────────────────
// ScutePhotoDao (PRD v3)
// ─────────────────────────────────────────────
@Dao
interface ScutePhotoDao {
    @Query("SELECT * FROM scute_photos WHERE scuteLogId = :scuteLogId ORDER BY takenAt ASC")
    fun getPhotosForScuteLog(scuteLogId: Long): Flow<List<ScutePhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: ScutePhotoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<ScutePhotoEntity>)

    @Delete
    suspend fun deletePhoto(photo: ScutePhotoEntity)

    @Query("DELETE FROM scute_photos WHERE scuteLogId = :scuteLogId")
    suspend fun deletePhotosForScuteLog(scuteLogId: Long)

    @Query("SELECT * FROM scute_photos WHERE scuteLogId = :scuteLogId ORDER BY takenAt ASC")
    suspend fun getPhotosForScuteLogSync(scuteLogId: Long): List<ScutePhotoEntity>
}

// ─────────────────────────────────────────────
// SoakingLogDao (PRD v3)
// ─────────────────────────────────────────────
//@Dao
//interface SoakingLogDao {
//    @Query("SELECT * FROM soaking_logs WHERE reptileId = :reptileId ORDER BY soakingDate DESC")
//    fun getSoakingLogsForReptile(reptileId: Long): Flow<List<SoakingLogEntity>>

//    @Query("SELECT * FROM soaking_logs WHERE reptileId = :reptileId ORDER BY soakingDate DESC LIMIT 1")
//    suspend fun getLastSoaking(reptileId: Long): SoakingLogEntity?

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertSoakingLog(log: SoakingLogEntity): Long

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertSoakingLogs(logs: List<SoakingLogEntity>)

//    @Update
//    suspend fun updateSoakingLog(log: SoakingLogEntity)

//    @Delete
//    suspend fun deleteSoakingLog(log: SoakingLogEntity)

//    @Query("SELECT * FROM soaking_logs WHERE reptileId = :reptileId")
//    suspend fun getSoakingLogsForReptileSync(reptileId: Long): List<SoakingLogEntity>
//}

// ─────────────────────────────────────────────
// BrumasiLogDao (PRD v3)
// ─────────────────────────────────────────────
@Dao
interface BrumasiLogDao {
    @Query("SELECT * FROM brumasi_logs WHERE reptileId = :reptileId ORDER BY startDate DESC")
    fun getBrumasiLogsForReptile(reptileId: Long): Flow<List<BrumasiLogEntity>>

    @Query("SELECT * FROM brumasi_logs WHERE reptileId = :reptileId AND endDate IS NULL LIMIT 1")
    suspend fun getActiveBrumasi(reptileId: Long): BrumasiLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrumasiLog(log: BrumasiLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrumasiLogs(logs: List<BrumasiLogEntity>)

    @Update
    suspend fun updateBrumasiLog(log: BrumasiLogEntity)

    @Delete
    suspend fun deleteBrumasiLog(log: BrumasiLogEntity)

    @Query("SELECT * FROM brumasi_logs WHERE reptileId = :reptileId")
    suspend fun getBrumasiLogsForReptileSync(reptileId: Long): List<BrumasiLogEntity>
}

// ─────────────────────────────────────────────
// UvbBasingLogDao (PRD v3)
// ─────────────────────────────────────────────
//@Dao
//interface UvbBasingLogDao {
//    @Query("SELECT * FROM uvb_basing_logs WHERE reptileId = :reptileId ORDER BY sessionDate DESC")
//    fun getUvbLogsForReptile(reptileId: Long): Flow<List<UvbBasingLogEntity>>

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertUvbLog(log: UvbBasingLogEntity): Long

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertUvbLogs(logs: List<UvbBasingLogEntity>)

//    @Update
//    suspend fun updateUvbLog(log: UvbBasingLogEntity)

//    @Delete
//    suspend fun deleteUvbLog(log: UvbBasingLogEntity)

//    @Query("SELECT * FROM uvb_basing_logs WHERE reptileId = :reptileId")
//    suspend fun getUvbLogsForReptileSync(reptileId: Long): List<UvbBasingLogEntity>
//}

// ─────────────────────────────────────────────
// DietLogDao (PRD v3)
// ─────────────────────────────────────────────
//@Dao
//interface DietLogDao {
//    @Query("SELECT * FROM diet_logs WHERE reptileId = :reptileId ORDER BY recordedAt DESC")
//    fun getDietLogsForReptile(reptileId: Long): Flow<List<DietLogEntity>>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertDietLog(log: DietLogEntity): Long
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertDietLogs(logs: List<DietLogEntity>)
//
//    @Update
//    suspend fun updateDietLog(log: DietLogEntity)
//
//    @Delete
//    suspend fun deleteDietLog(log: DietLogEntity)
//
//    @Query("SELECT * FROM diet_logs WHERE reptileId = :reptileId")
//    suspend fun getDietLogsForReptileSync(reptileId: Long): List<DietLogEntity>
//}

// ─────────────────────────────────────────────
// HealthRecordDao
// ─────────────────────────────────────────────
//@Dao
//interface HealthRecordDao {
//
//    @Query("SELECT * FROM health_records WHERE reptileId = :reptileId ORDER BY date DESC")
//    fun getHealthRecordsForReptile(reptileId: Long): Flow<List<HealthRecordEntity>>
//
//    // Untuk reminder — ambil semua record yang punya nextReminderAt di masa depan
//    @Query("SELECT * FROM health_records WHERE nextReminderAt IS NOT NULL AND nextReminderAt > :now")
//    suspend fun getPendingReminders(now: Long = System.currentTimeMillis()): List<HealthRecordEntity>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertHealthRecord(record: HealthRecordEntity): Long
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertHealthRecords(records: List<HealthRecordEntity>)
//
//    @Update
//    suspend fun updateHealthRecord(record: HealthRecordEntity)
//
//    @Delete
//    suspend fun deleteHealthRecord(record: HealthRecordEntity)
//
//    @Query("SELECT * FROM health_records WHERE reptileId = :reptileId")
//    suspend fun getHealthRecordsForReptileSync(reptileId: Long): List<HealthRecordEntity>
//}

// ─────────────────────────────────────────────
// RiwayatDao
// ─────────────────────────────────────────────
@Dao
interface RiwayatDao {
    @Query("SELECT * FROM riwayat_logs WHERE reptileId = :reptileId ORDER BY startDate DESC")
    fun getRiwayatForReptile(reptileId: Long): Flow<List<RiwayatEntity>>

    @Query("SELECT * FROM riwayat_logs WHERE reptileId = :reptileId AND isOngoing = 1 LIMIT 1")
    suspend fun getActiveIllness(reptileId: Long): RiwayatEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRiwayat(riwayat: RiwayatEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRiwayatLogs(logs: List<RiwayatEntity>)

    @Update
    suspend fun updateRiwayat(riwayat: RiwayatEntity)

    @Delete
    suspend fun deleteRiwayat(riwayat: RiwayatEntity)

    @Query("SELECT * FROM riwayat_logs WHERE reptileId = :reptileId")
    suspend fun getRiwayatForReptileSync(reptileId: Long): List<RiwayatEntity>
}

// ─────────────────────────────────────────────
// RiwayatPhotoDao
// ─────────────────────────────────────────────
@Dao
interface RiwayatPhotoDao {
    @Query("SELECT * FROM riwayat_photos WHERE riwayatId = :riwayatId ORDER BY takenAt ASC")
    fun getPhotosForRiwayat(riwayatId: Long): Flow<List<RiwayatPhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: RiwayatPhotoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<RiwayatPhotoEntity>)

    @Delete
    suspend fun deletePhoto(photo: RiwayatPhotoEntity)

    @Query("DELETE FROM riwayat_photos WHERE riwayatId = :riwayatId")
    suspend fun deletePhotosForRiwayat(riwayatId: Long)

    @Query("SELECT * FROM riwayat_photos WHERE riwayatId = :riwayatId ORDER BY takenAt ASC")
    suspend fun getPhotosForRiwayatSync(riwayatId: Long): List<RiwayatPhotoEntity>
}
