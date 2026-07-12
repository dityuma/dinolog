package com.dites.dinolog.ui.viewmodel

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dites.dinolog.data.local.entity.*
import com.dites.dinolog.data.repository.DinoLogRepository
import com.dites.dinolog.ui.theme.AppTheme
import com.dites.dinolog.ui.theme.ThemePreference
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

// ─────────────────────────────────────────────
// ReptileListViewModel — layar daftar semua reptil
// ─────────────────────────────────────────────
class ReptileListViewModel(
    private val repository: DinoLogRepository
) : ViewModel() {

    val reptiles = repository.allReptiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addReptile(reptile: ReptileEntity) = viewModelScope.launch {
        repository.addReptile(reptile)
    }

    fun deleteReptile(reptile: ReptileEntity) = viewModelScope.launch {
        repository.deleteReptile(reptile)
    }
}

// ─────────────────────────────────────────────
// ReptileDetailViewModel — layar detail satu reptil
// ─────────────────────────────────────────────
class ReptileDetailViewModel(
    private val repository: DinoLogRepository,
    private val reptileId: Long
) : ViewModel() {

    val reptile = repository.getReptileById(reptileId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val growthLogs = repository.getGrowthLogs(reptileId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val feedingLogs = repository.getFeedingLogs(reptileId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val scuteLogs = repository.getScuteLogs(reptileId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val soakingLogs = repository.getSoakingLogs(reptileId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val brumasiLogs = repository.getBrumasiLogs(reptileId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val uvbLogs = repository.getUvbLogs(reptileId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dietLogs = repository.getDietLogs(reptileId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val riwayatLogs = repository.getRiwayat(reptileId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val healthRecords = repository.getHealthRecords(reptileId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weightHistory = repository.getWeightHistory(reptileId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lengthHistory = repository.getLengthHistory(reptileId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateReptile(reptile: ReptileEntity) = viewModelScope.launch {
        repository.updateReptile(reptile)
    }

    fun addGrowthLog(log: GrowthLogEntity, photoUris: List<String> = emptyList()) =
        viewModelScope.launch {
            repository.addGrowthLog(log, photoUris)
        }

    fun updateGrowthLog(log: GrowthLogEntity) = viewModelScope.launch {
        repository.updateGrowthLog(log)
    }

    fun deleteGrowthLog(log: GrowthLogEntity) = viewModelScope.launch {
        repository.deleteGrowthLog(log)
    }

    fun getPhotosForLog(logId: Long) = repository.getPhotosForLog(logId)

    fun addPhotoToLog(logId: Long, uri: String) = viewModelScope.launch {
        repository.addPhoto(GrowthPhotoEntity(growthLogId = logId, photoUri = uri))
    }

    fun deletePhoto(photo: GrowthPhotoEntity) = viewModelScope.launch {
        repository.deletePhoto(photo)
    }

    fun addFeedingLog(feeding: FeedingLogEntity) = viewModelScope.launch {
        repository.addFeedingLog(feeding)
    }

    fun updateFeedingLog(feeding: FeedingLogEntity) = viewModelScope.launch {
        repository.updateFeedingLog(feeding)
    }

    fun deleteFeedingLog(feeding: FeedingLogEntity) = viewModelScope.launch {
        repository.deleteFeedingLog(feeding)
    }

    // New Logs (PRD v3)
    fun getPhotosForScuteLog(scuteLogId: Long) = repository.getPhotosForScuteLog(scuteLogId)

    fun addScuteLog(log: ScuteLogEntity, photoUris: List<String> = emptyList()) = viewModelScope.launch {
        val logId = repository.addScuteLog(log)
        if (photoUris.isNotEmpty()) {
            val photos = photoUris.map { ScutePhotoEntity(scuteLogId = logId, photoUri = it) }
            repository.addScutePhotos(photos)
        }
    }

    fun updateScuteLog(log: ScuteLogEntity) = viewModelScope.launch { repository.updateScuteLog(log) }

    fun deleteScuteLog(log: ScuteLogEntity) = viewModelScope.launch { repository.deleteScuteLog(log) }

    fun addScutePhotos(photos: List<ScutePhotoEntity>) = viewModelScope.launch { repository.addScutePhotos(photos) }

    fun deleteScutePhoto(photo: ScutePhotoEntity) = viewModelScope.launch { repository.deleteScutePhoto(photo) }

    fun addSoakingLog(log: SoakingLogEntity) = viewModelScope.launch { repository.addSoakingLog(log) }
    fun deleteSoakingLog(log: SoakingLogEntity) = viewModelScope.launch { repository.deleteSoakingLog(log) }

    fun addBrumasiLog(log: BrumasiLogEntity) = viewModelScope.launch { repository.addBrumasiLog(log) }
    fun updateBrumasiLog(log: BrumasiLogEntity) = viewModelScope.launch { repository.updateBrumasiLog(log) }
    fun deleteBrumasiLog(log: BrumasiLogEntity) = viewModelScope.launch { repository.deleteBrumasiLog(log) }

    fun addUvbLog(log: UvbBasingLogEntity) = viewModelScope.launch { repository.addUvbLog(log) }
    fun deleteUvbLog(log: UvbBasingLogEntity) = viewModelScope.launch { repository.deleteUvbLog(log) }

    fun addDietLog(log: DietLogEntity) = viewModelScope.launch { repository.addDietLog(log) }
    fun updateDietLog(log: DietLogEntity) = viewModelScope.launch { repository.updateDietLog(log) }
    fun deleteDietLog(log: DietLogEntity) = viewModelScope.launch { repository.deleteDietLog(log) }

    fun addRiwayat(riwayat: RiwayatEntity) = viewModelScope.launch {
        repository.addRiwayat(riwayat)
    }
    fun updateRiwayat(riwayat: RiwayatEntity) = viewModelScope.launch {
        repository.updateRiwayat(riwayat)
    }
    fun deleteRiwayat(riwayat: RiwayatEntity) = viewModelScope.launch {
        repository.deleteRiwayat(riwayat)
    }

    fun getPhotosForRiwayat(riwayatId: Long): Flow<List<RiwayatPhotoEntity>> =
        repository.getPhotosForRiwayat(riwayatId)

    fun addRiwayatWithPhotos(riwayat: RiwayatEntity, photoUris: List<String>) =
        viewModelScope.launch {
            val riwayatId = repository.addRiwayat(riwayat)
            if (photoUris.isNotEmpty()) {
                val photos = photoUris.map { RiwayatPhotoEntity(riwayatId = riwayatId, photoUri = it) }
                repository.addRiwayatPhotos(photos)
            }
        }

    fun deleteRiwayatPhoto(photo: RiwayatPhotoEntity) = viewModelScope.launch {
        repository.deleteRiwayatPhoto(photo)
    }

    fun addRiwayatPhotos(photos: List<RiwayatPhotoEntity>) = viewModelScope.launch {
        repository.addRiwayatPhotos(photos)
    }

    fun addHealthRecord(record: HealthRecordEntity) = viewModelScope.launch {
        repository.addHealthRecord(record)
    }

    fun updateHealthRecord(record: HealthRecordEntity) = viewModelScope.launch {
        repository.updateHealthRecord(record)
    }

    fun deleteHealthRecord(record: HealthRecordEntity) = viewModelScope.launch {
        repository.deleteHealthRecord(record)
    }
}

// ─────────────────────────────────────────────
// TortoiseCareViewModel (PRD v3)
// ─────────────────────────────────────────────
class TortoiseCareViewModel(
    private val repository: DinoLogRepository,
    private val reptileId: Long
) : ViewModel() {

    val soakingLogs = repository.getSoakingLogs(reptileId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val brumasiLogs = repository.getBrumasiLogs(reptileId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val uvbLogs = repository.getUvbLogs(reptileId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dietLogs = repository.getDietLogs(reptileId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addSoakingLog(log: SoakingLogEntity) = viewModelScope.launch { repository.addSoakingLog(log) }
    fun deleteSoakingLog(log: SoakingLogEntity) = viewModelScope.launch { repository.deleteSoakingLog(log) }

    fun addBrumasiLog(log: BrumasiLogEntity) = viewModelScope.launch { repository.addBrumasiLog(log) }
    fun updateBrumasiLog(log: BrumasiLogEntity) = viewModelScope.launch { repository.updateBrumasiLog(log) }
    fun deleteBrumasiLog(log: BrumasiLogEntity) = viewModelScope.launch { repository.deleteBrumasiLog(log) }

    fun addUvbLog(log: UvbBasingLogEntity) = viewModelScope.launch { repository.addUvbLog(log) }
    fun deleteUvbLog(log: UvbBasingLogEntity) = viewModelScope.launch { repository.deleteUvbLog(log) }

    fun addDietLog(log: DietLogEntity) = viewModelScope.launch { repository.addDietLog(log) }
    fun updateDietLog(log: DietLogEntity) = viewModelScope.launch { repository.updateDietLog(log) }
    fun deleteDietLog(log: DietLogEntity) = viewModelScope.launch { repository.deleteDietLog(log) }
}

// ─────────────────────────────────────────────
// ViewModelFactory — karena ViewModel butuh parameter
// ─────────────────────────────────────────────
class ReptileListViewModelFactory(
    private val repository: DinoLogRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReptileListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReptileListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}

class ReptileDetailViewModelFactory(
    private val repository: DinoLogRepository,
    private val reptileId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReptileDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReptileDetailViewModel(repository, reptileId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}

class TortoiseCareViewModelFactory(
    private val repository: DinoLogRepository,
    private val reptileId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TortoiseCareViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TortoiseCareViewModel(repository, reptileId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}

// ─────────────────────────────────────────────
// SettingsViewModel — Export & Import
// ─────────────────────────────────────────────
class SettingsViewModel(
    private val repository: DinoLogRepository,
    private val context: Context,
    private val themePreference: ThemePreference
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val selectedTheme: Flow<String> = themePreference.selectedTheme

    fun setTheme(theme: AppTheme) = viewModelScope.launch {
        themePreference.setTheme(theme.name)
    }

    private val gson = GsonBuilder().setPrettyPrinting().create()

    data class ExportData(
        val exportDate: String,
        val appVersion: Int,
        val reptiles: List<ReptileBackup>
    )

    data class ReptileBackup(
        val reptile: ReptileEntity,
        val growthLogs: List<GrowthLogBackup>,
        val feedingLogs: List<FeedingLogEntity>,
        val scuteLogs: List<ScuteLogBackup>,
        val riwayatLogs: List<RiwayatBackup>,
        val brumasiLogs: List<BrumasiLogEntity>
    )

    data class GrowthLogBackup(
        val id: Long,
        val reptileId: Long,
        val recordedAt: Long,
        val weightGrams: Float?,
        val lengthCm: Float?,
        val bodyCondition: String,
        val notes: String,
        val createdAt: Long,
        val photos: List<GrowthPhotoEntity>
    )

    data class ScuteLogBackup(
        val id: Long,
        val reptileId: Long,
        val recordedAt: Long,
        val condition: String,
        val notes: String,
        val photos: List<ScutePhotoEntity>
    )

    data class RiwayatBackup(
        val id: Long,
        val reptileId: Long,
        val illnessName: String,
        val notes: String,
        val startDate: Long,
        val isOngoing: Boolean,
        val endDate: Long?,
        val createdAt: Long,
        val photos: List<RiwayatPhotoEntity>
    )

    fun exportData(onSuccess: (String) -> Unit, onError: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        _isLoading.value = true
        try {
            val allReptiles = repository.getAllReptilesSync()
            val reptilesBackup = allReptiles.map { reptile ->
                val growthLogs = repository.getGrowthLogsSync(reptile.id).map { log ->
                    GrowthLogBackup(
                        id = log.id,
                        reptileId = log.reptileId,
                        recordedAt = log.recordedAt,
                        weightGrams = log.weightGrams,
                        lengthCm = log.lengthCm,
                        bodyCondition = log.bodyCondition,
                        notes = log.notes,
                        createdAt = log.createdAt,
                        photos = repository.getPhotosForLogSync(log.id)
                    )
                }
                val scuteLogs = repository.getScuteLogsSync(reptile.id).map { log ->
                    ScuteLogBackup(
                        id = log.id,
                        reptileId = log.reptileId,
                        recordedAt = log.recordedAt,
                        condition = log.condition,
                        notes = log.notes,
                        photos = repository.getPhotosForScuteLogSync(log.id)
                    )
                }
                val riwayatLogs = repository.getRiwayatSync(reptile.id).map { log ->
                    RiwayatBackup(
                        id = log.id,
                        reptileId = log.reptileId,
                        illnessName = log.illnessName,
                        notes = log.notes,
                        startDate = log.startDate,
                        isOngoing = log.isOngoing,
                        endDate = log.endDate,
                        createdAt = log.createdAt,
                        photos = repository.getPhotosForRiwayatSync(log.id)
                    )
                }

                ReptileBackup(
                    reptile = reptile,
                    growthLogs = growthLogs,
                    feedingLogs = repository.getFeedingLogsSync(reptile.id),
                    scuteLogs = scuteLogs,
                    riwayatLogs = riwayatLogs,
                    brumasiLogs = repository.getBrumasiLogsSync(reptile.id)
                )
            }

            val exportData = ExportData(
                exportDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date()),
                appVersion = 1,
                reptiles = reptilesBackup
            )

            val jsonString = gson.toJson(exportData)
            val fileName = "dinolog_backup_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.json"
            
            saveJsonToDownloads(context, jsonString, fileName)
            
            withContext(Dispatchers.Main) {
                onSuccess(fileName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                onError()
            }
        } finally {
            _isLoading.value = false
        }
    }

    private fun saveJsonToDownloads(context: Context, json: String, fileName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                resolver.openOutputStream(it)?.use { outputStream ->
                    outputStream.write(json.toByteArray())
                }
            } ?: throw Exception("Failed to create MediaStore entry")
        } else {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            FileOutputStream(file).use { it.write(json.toByteArray()) }
        }
    }

    fun importData(uri: Uri, onSuccess: () -> Unit, onError: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        _isLoading.value = true
        try {
            val jsonString = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                ?: throw Exception("Cannot read file")

            val importData = gson.fromJson(jsonString, ExportData::class.java)
            
            importData.reptiles.forEach { item ->
                repository.insertReptiles(listOf(item.reptile))
                
                // Growth
                repository.insertGrowthLogs(item.growthLogs.map { log ->
                    GrowthLogEntity(
                        id = log.id,
                        reptileId = log.reptileId,
                        recordedAt = log.recordedAt,
                        weightGrams = log.weightGrams,
                        lengthCm = log.lengthCm,
                        bodyCondition = log.bodyCondition,
                        notes = log.notes,
                        createdAt = log.createdAt
                    )
                })
                item.growthLogs.forEach { log ->
                    log.photos.forEach { photo ->
                        repository.insertGrowthPhoto(photo)
                    }
                }
                
                // Feeding
                repository.insertFeedingLogs(item.feedingLogs)
                
                // Scute
                repository.insertScuteLogs(item.scuteLogs.map { log ->
                    ScuteLogEntity(
                        id = log.id,
                        reptileId = log.reptileId,
                        recordedAt = log.recordedAt,
                        condition = log.condition,
                        notes = log.notes
                    )
                })
                item.scuteLogs.forEach { log ->
                    log.photos.forEach { photo ->
                        repository.insertScutePhoto(photo)
                    }
                }
                
                // Riwayat
                repository.insertRiwayatLogs(item.riwayatLogs.map { log ->
                    RiwayatEntity(
                        id = log.id,
                        reptileId = log.reptileId,
                        illnessName = log.illnessName,
                        notes = log.notes,
                        startDate = log.startDate,
                        isOngoing = log.isOngoing,
                        endDate = log.endDate,
                        createdAt = log.createdAt
                    )
                })
                item.riwayatLogs.forEach { log ->
                    log.photos.forEach { photo ->
                        repository.insertRiwayatPhoto(photo)
                    }
                }
                
                // Brumasi
                repository.insertBrumasiLogs(item.brumasiLogs)
            }
            
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                onError()
            }
        } finally {
            _isLoading.value = false
        }
    }
}

class SettingsViewModelFactory(
    private val repository: DinoLogRepository,
    private val context: Context,
    private val themePreference: ThemePreference
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repository, context, themePreference) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}
