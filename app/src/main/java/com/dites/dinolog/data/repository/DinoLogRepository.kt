package com.dites.dinolog.data.repository

import com.dites.dinolog.data.local.dao.*
import com.dites.dinolog.data.local.entity.*
import kotlinx.coroutines.flow.Flow

class DinoLogRepository(
    private val reptileDao: ReptileDao,
    private val growthLogDao: GrowthLogDao,
    private val growthPhotoDao: GrowthPhotoDao,
    private val feedingLogDao: FeedingLogDao,
    private val scuteLogDao: ScuteLogDao,
    private val soakingLogDao: SoakingLogDao,
    private val brumasiLogDao: BrumasiLogDao,
    private val uvbBasingLogDao: UvbBasingLogDao,
    private val dietLogDao: DietLogDao,
    private val healthRecordDao: HealthRecordDao,
    private val scutePhotoDao: ScutePhotoDao,
    private val riwayatDao: RiwayatDao,
    private val riwayatPhotoDao: RiwayatPhotoDao
) {

    // ── Reptile ──────────────────────────────
    val allReptiles: Flow<List<ReptileEntity>> = reptileDao.getAllReptiles()

    fun getReptileById(id: Long) = reptileDao.getReptileById(id)

    suspend fun addReptile(reptile: ReptileEntity): Long =
        reptileDao.insertReptile(reptile)

    suspend fun updateReptile(reptile: ReptileEntity) =
        reptileDao.updateReptile(reptile)

    suspend fun deleteReptile(reptile: ReptileEntity) =
        reptileDao.deleteReptile(reptile)

    suspend fun getAllReptilesSync(): List<ReptileEntity> =
        reptileDao.getAllReptilesSync()

    suspend fun insertReptiles(reptiles: List<ReptileEntity>) =
        reptileDao.insertReptiles(reptiles)

    // ── Growth Log ───────────────────────────
    fun getGrowthLogs(reptileId: Long): Flow<List<GrowthLogEntity>> =
        growthLogDao.getLogsForReptile(reptileId)

    suspend fun getLatestGrowthLog(reptileId: Long): GrowthLogEntity? =
        growthLogDao.getLatestLog(reptileId)

    fun getWeightHistory(reptileId: Long): Flow<List<WeightPoint>> =
        growthLogDao.getWeightHistory(reptileId)

    fun getLengthHistory(reptileId: Long): Flow<List<LengthPoint>> =
        growthLogDao.getLengthHistory(reptileId)

    suspend fun getGrowthLogCount(reptileId: Long): Int =
        growthLogDao.getLogCount(reptileId)

    // Insert log + photos dalam satu operasi atomik
    suspend fun addGrowthLog(
        log: GrowthLogEntity,
        photoUris: List<String> = emptyList()
    ): Long {
        val logId = growthLogDao.insertLog(log)
        if (photoUris.isNotEmpty()) {
            val photos = photoUris.map { uri ->
                GrowthPhotoEntity(growthLogId = logId, photoUri = uri)
            }
            growthPhotoDao.insertPhotos(photos)
        }
        return logId
    }

    suspend fun updateGrowthLog(log: GrowthLogEntity) =
        growthLogDao.updateLog(log)

    suspend fun deleteGrowthLog(log: GrowthLogEntity) =
        growthLogDao.deleteLog(log)

    suspend fun getGrowthLogsSync(reptileId: Long): List<GrowthLogEntity> =
        growthLogDao.getLogsForReptileSync(reptileId)

    suspend fun insertGrowthLogs(logs: List<GrowthLogEntity>) =
        growthLogDao.insertLogs(logs)

    // ── Growth Photos ─────────────────────────
    fun getPhotosForLog(logId: Long): Flow<List<GrowthPhotoEntity>> =
        growthPhotoDao.getPhotosForLog(logId)

    suspend fun addPhoto(photo: GrowthPhotoEntity): Long =
        growthPhotoDao.insertPhoto(photo)

    suspend fun deletePhoto(photo: GrowthPhotoEntity) =
        growthPhotoDao.deletePhoto(photo)

    // ── Feeding Log ───────────────────────────
    fun getFeedingLogs(reptileId: Long): Flow<List<FeedingLogEntity>> =
        feedingLogDao.getFeedingsForReptile(reptileId)

    suspend fun getLastFeeding(reptileId: Long): FeedingLogEntity? =
        feedingLogDao.getLastFeeding(reptileId)

    suspend fun getRefusedFeedCount(reptileId: Long, since: Long): Int =
        feedingLogDao.getRefusedFeedCount(reptileId, since)

    suspend fun addFeedingLog(feeding: FeedingLogEntity): Long =
        feedingLogDao.insertFeeding(feeding)

    suspend fun updateFeedingLog(feeding: FeedingLogEntity) =
        feedingLogDao.updateFeeding(feeding)

    suspend fun deleteFeedingLog(feeding: FeedingLogEntity) =
        feedingLogDao.deleteFeeding(feeding)

    suspend fun getFeedingLogsSync(reptileId: Long): List<FeedingLogEntity> =
        feedingLogDao.getFeedingsForReptileSync(reptileId)

    suspend fun insertFeedingLogs(feedings: List<FeedingLogEntity>) =
        feedingLogDao.insertFeedings(feedings)

    // ── Scute Log (PRD v3) ──────────────────
    fun getScuteLogs(reptileId: Long) = scuteLogDao.getScuteLogsForReptile(reptileId)
    suspend fun getLatestScuteLog(reptileId: Long) = scuteLogDao.getLatestScuteLog(reptileId)
    suspend fun addScuteLog(log: ScuteLogEntity) = scuteLogDao.insertScuteLog(log)
    suspend fun updateScuteLog(log: ScuteLogEntity) = scuteLogDao.updateScuteLog(log)
    suspend fun deleteScuteLog(log: ScuteLogEntity) = scuteLogDao.deleteScuteLog(log)
    suspend fun getScuteLogsSync(reptileId: Long) = scuteLogDao.getScuteLogsForReptileSync(reptileId)
    suspend fun insertScuteLogs(logs: List<ScuteLogEntity>) = scuteLogDao.insertScuteLogs(logs)

    // ── Soaking Log (PRD v3) ─────────────────
    fun getSoakingLogs(reptileId: Long) = soakingLogDao.getSoakingLogsForReptile(reptileId)
    suspend fun getLastSoaking(reptileId: Long) = soakingLogDao.getLastSoaking(reptileId)
    suspend fun addSoakingLog(log: SoakingLogEntity) = soakingLogDao.insertSoakingLog(log)
    suspend fun deleteSoakingLog(log: SoakingLogEntity) = soakingLogDao.deleteSoakingLog(log)
    suspend fun getSoakingLogsSync(reptileId: Long) = soakingLogDao.getSoakingLogsForReptileSync(reptileId)
    suspend fun insertSoakingLogs(logs: List<SoakingLogEntity>) = soakingLogDao.insertSoakingLogs(logs)

    // ── Brumasi Log (PRD v3) ─────────────────
    fun getBrumasiLogs(reptileId: Long) = brumasiLogDao.getBrumasiLogsForReptile(reptileId)
    suspend fun getActiveBrumasi(reptileId: Long) = brumasiLogDao.getActiveBrumasi(reptileId)
    suspend fun addBrumasiLog(log: BrumasiLogEntity) = brumasiLogDao.insertBrumasiLog(log)
    suspend fun updateBrumasiLog(log: BrumasiLogEntity) = brumasiLogDao.updateBrumasiLog(log)
    suspend fun deleteBrumasiLog(log: BrumasiLogEntity) = brumasiLogDao.deleteBrumasiLog(log)
    suspend fun getBrumasiLogsSync(reptileId: Long) = brumasiLogDao.getBrumasiLogsForReptileSync(reptileId)
    suspend fun insertBrumasiLogs(logs: List<BrumasiLogEntity>) = brumasiLogDao.insertBrumasiLogs(logs)

    // ── Uvb Log (PRD v3) ─────────────────────
    fun getUvbLogs(reptileId: Long) = uvbBasingLogDao.getUvbLogsForReptile(reptileId)
    suspend fun addUvbLog(log: UvbBasingLogEntity) = uvbBasingLogDao.insertUvbLog(log)
    suspend fun deleteUvbLog(log: UvbBasingLogEntity) = uvbBasingLogDao.deleteUvbLog(log)
    suspend fun getUvbLogsSync(reptileId: Long) = uvbBasingLogDao.getUvbLogsForReptileSync(reptileId)
    suspend fun insertUvbLogs(logs: List<UvbBasingLogEntity>) = uvbBasingLogDao.insertUvbLogs(logs)

    // ── Diet Log (PRD v3) ────────────────────
    fun getDietLogs(reptileId: Long) = dietLogDao.getDietLogsForReptile(reptileId)
    suspend fun addDietLog(log: DietLogEntity) = dietLogDao.insertDietLog(log)
    suspend fun updateDietLog(log: DietLogEntity) = dietLogDao.updateDietLog(log)
    suspend fun deleteDietLog(log: DietLogEntity) = dietLogDao.deleteDietLog(log)
    suspend fun getDietLogsSync(reptileId: Long) = dietLogDao.getDietLogsForReptileSync(reptileId)
    suspend fun insertDietLogs(logs: List<DietLogEntity>) = dietLogDao.insertDietLogs(logs)

    // ── Scute Photo (PRD v3) ────────────────
    fun getPhotosForScuteLog(scuteLogId: Long) = scutePhotoDao.getPhotosForScuteLog(scuteLogId)
    suspend fun addScutePhoto(photo: ScutePhotoEntity) = scutePhotoDao.insertPhoto(photo)
    suspend fun addScutePhotos(photos: List<ScutePhotoEntity>) = scutePhotoDao.insertPhotos(photos)
    suspend fun deleteScutePhoto(photo: ScutePhotoEntity) = scutePhotoDao.deletePhoto(photo)

    // ── Health Record ─────────────────────────
    fun getHealthRecords(reptileId: Long): Flow<List<HealthRecordEntity>> =
        healthRecordDao.getHealthRecordsForReptile(reptileId)

    suspend fun getPendingReminders(): List<HealthRecordEntity> =
        healthRecordDao.getPendingReminders()

    suspend fun addHealthRecord(record: HealthRecordEntity): Long =
        healthRecordDao.insertHealthRecord(record)

    suspend fun updateHealthRecord(record: HealthRecordEntity) =
        healthRecordDao.updateHealthRecord(record)

    suspend fun deleteHealthRecord(record: HealthRecordEntity) =
        healthRecordDao.deleteHealthRecord(record)

    suspend fun getHealthRecordsSync(reptileId: Long): List<HealthRecordEntity> =
        healthRecordDao.getHealthRecordsForReptileSync(reptileId)

    suspend fun insertHealthRecords(records: List<HealthRecordEntity>) =
        healthRecordDao.insertHealthRecords(records)

    // ── Riwayat Log ──────────────────────────
    fun getRiwayat(reptileId: Long) = riwayatDao.getRiwayatForReptile(reptileId)
    suspend fun getActiveIllness(reptileId: Long) = riwayatDao.getActiveIllness(reptileId)
    suspend fun addRiwayat(riwayat: RiwayatEntity) = riwayatDao.insertRiwayat(riwayat)
    suspend fun updateRiwayat(riwayat: RiwayatEntity) = riwayatDao.updateRiwayat(riwayat)
    suspend fun deleteRiwayat(riwayat: RiwayatEntity) = riwayatDao.deleteRiwayat(riwayat)
    suspend fun getRiwayatSync(reptileId: Long) = riwayatDao.getRiwayatForReptileSync(reptileId)
    suspend fun insertRiwayatLogs(logs: List<RiwayatEntity>) = riwayatDao.insertRiwayatLogs(logs)

    // ── Riwayat Photo ────────────────────────
    fun getPhotosForRiwayat(riwayatId: Long) = riwayatPhotoDao.getPhotosForRiwayat(riwayatId)
    suspend fun addRiwayatPhoto(photo: RiwayatPhotoEntity) = riwayatPhotoDao.insertPhoto(photo)
    suspend fun addRiwayatPhotos(photos: List<RiwayatPhotoEntity>) = riwayatPhotoDao.insertPhotos(photos)
    suspend fun deleteRiwayatPhoto(photo: RiwayatPhotoEntity) = riwayatPhotoDao.deletePhoto(photo)
}
