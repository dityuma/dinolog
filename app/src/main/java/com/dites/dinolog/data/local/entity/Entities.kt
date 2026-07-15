package com.dites.dinolog.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

// ─────────────────────────────────────────────
// Reptile — master data hewan
// ─────────────────────────────────────────────
@Entity(tableName = "reptiles")
data class ReptileEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id") val id: Long = 0,
    @SerializedName("name") val name: String,
    @SerializedName("species") val species: String,
    @SerializedName("gender") val gender: String = "TIDAK_DIKETAHUI",
    @SerializedName("isRescue") val isRescue: Boolean = false,
    @SerializedName("profilePhotoUri") val profilePhotoUri: String = "",
    @SerializedName("birthDate") val birthDate: Long? = null,
    @SerializedName("acquireDate") val acquireDate: Long? = null,
    @SerializedName("createdAt") val createdAt: Long = System.currentTimeMillis()
)

// ─────────────────────────────────────────────
// GrowthLog — pencatatan pertumbuhan per sesi
// ─────────────────────────────────────────────
@Entity(
    tableName = "growth_logs",
    foreignKeys = [ForeignKey(
        entity = ReptileEntity::class,
        parentColumns = ["id"],
        childColumns = ["reptileId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("reptileId")]
)
data class GrowthLogEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id") val id: Long = 0,
    @SerializedName("reptileId") val reptileId: Long,
    @SerializedName("recordedAt") val recordedAt: Long = System.currentTimeMillis(),
    @SerializedName("weightGrams") val weightGrams: Float? = null,
    @SerializedName("lengthCm") val lengthCm: Float? = null,
    @SerializedName("bodyCondition") val bodyCondition: String = "IDEAL",
    @SerializedName("notes") val notes: String = "",
    @SerializedName("createdAt") val createdAt: Long = System.currentTimeMillis()
)

// ─────────────────────────────────────────────
// GrowthPhoto — foto per sesi pencatatan (many-to-one)
// ─────────────────────────────────────────────
@Entity(
    tableName = "growth_photos",
    foreignKeys = [ForeignKey(
        entity = GrowthLogEntity::class,
        parentColumns = ["id"],
        childColumns = ["growthLogId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("growthLogId")]
)
data class GrowthPhotoEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id") val id: Long = 0,
    @SerializedName("growthLogId") val growthLogId: Long,
    @SerializedName("photoUri") val photoUri: String,
    @SerializedName("takenAt") val takenAt: Long = System.currentTimeMillis()
)

// ─────────────────────────────────────────────
// FeedingLog — log pemberian makan
// ─────────────────────────────────────────────
@Entity(
    tableName = "feeding_logs",
    foreignKeys = [ForeignKey(
        entity = ReptileEntity::class,
        parentColumns = ["id"],
        childColumns = ["reptileId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("reptileId")]
)
data class FeedingLogEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id") val id: Long = 0,
    @SerializedName("reptileId") val reptileId: Long,
    @SerializedName("feedingDate") val feedingDate: Long = System.currentTimeMillis(),
    @SerializedName("foodType") val foodType: String,
    @SerializedName("foodAmount") val foodAmount: String = "",
    @SerializedName("accepted") val accepted: Boolean = true,
    @SerializedName("notes") val notes: String = ""
)

// ─────────────────────────────────────────────
// ScuteLog — Kondisi tempurung (PRD v3)
// ─────────────────────────────────────────────
@Entity(
    tableName = "scute_logs",
    foreignKeys = [ForeignKey(
        entity = ReptileEntity::class,
        parentColumns = ["id"],
        childColumns = ["reptileId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("reptileId")]
)
data class ScuteLogEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id") val id: Long = 0,
    @SerializedName("reptileId") val reptileId: Long,
    @SerializedName("recordedAt") val recordedAt: Long = System.currentTimeMillis(),
    @SerializedName("condition") val condition: String = "NORMAL",
    @SerializedName("notes") val notes: String = ""
)

// ─────────────────────────────────────────────
// ScutePhoto — foto kondisi karapas (many-to-one)
// ─────────────────────────────────────────────
@Entity(
    tableName = "scute_photos",
    foreignKeys = [ForeignKey(
        entity = ScuteLogEntity::class,
        parentColumns = ["id"],
        childColumns = ["scuteLogId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("scuteLogId")]
)
data class ScutePhotoEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id") val id: Long = 0,
    @SerializedName("scuteLogId") val scuteLogId: Long,
    @SerializedName("photoUri") val photoUri: String,
    @SerializedName("takenAt") val takenAt: Long = System.currentTimeMillis()
)

// ─────────────────────────────────────────────
// SoakingLog — Log perendaman (PRD v3)
// ─────────────────────────────────────────────
//@Entity(
//    tableName = "soaking_logs",
//    foreignKeys = [ForeignKey(
//        entity = ReptileEntity::class,
//        parentColumns = ["id"],
//        childColumns = ["reptileId"],
//        onDelete = ForeignKey.CASCADE
//    )],
//    indices = [Index("reptileId")]
//)
//data class SoakingLogEntity(
//    @PrimaryKey(autoGenerate = true) val id: Long = 0,
//    val reptileId: Long,
//    val soakingDate: Long = System.currentTimeMillis(),
//    val durationMinutes: Int,
//    val waterTempCelsius: Float? = null,
//    val notes: String = ""
//)

// ─────────────────────────────────────────────
// BrumasiLog — Log istirahat musim dingin (PRD v3)
// ─────────────────────────────────────────────
@Entity(
    tableName = "brumasi_logs",
    foreignKeys = [ForeignKey(
        entity = ReptileEntity::class,
        parentColumns = ["id"],
        childColumns = ["reptileId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("reptileId")]
)
data class BrumasiLogEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id") val id: Long = 0,
    @SerializedName("reptileId") val reptileId: Long,
    @SerializedName("startDate") val startDate: Long = System.currentTimeMillis(),
    @SerializedName("endDate") val endDate: Long? = null,
    @SerializedName("weightBeforeGrams") val weightBeforeGrams: Float? = null,
    @SerializedName("weightAfterGrams") val weightAfterGrams: Float? = null,
    @SerializedName("notes") val notes: String = ""
)

// ─────────────────────────────────────────────
// UvbBasingLog — Log berjemur (PRD v3)
// ─────────────────────────────────────────────
//@Entity(
//    tableName = "uvb_basing_logs",
//    foreignKeys = [ForeignKey(
//        entity = ReptileEntity::class,
//        parentColumns = ["id"],
//        childColumns = ["reptileId"],
//        onDelete = ForeignKey.CASCADE
//    )],
//    indices = [Index("reptileId")]
//)
//data class UvbBasingLogEntity(
//    @PrimaryKey(autoGenerate = true) val id: Long = 0,
//    val reptileId: Long,
//    val sessionDate: Long = System.currentTimeMillis(),
//    val durationMinutes: Int,
//    val uvbType: String = "",
//    val basikingTempCelsius: Float? = null,
//    val notes: String = ""
//)

// ─────────────────────────────────────────────
// DietLog — Log detail menu (PRD v3)
// ─────────────────────────────────────────────
//@Entity(
//    tableName = "diet_logs",
//    foreignKeys = [ForeignKey(
//        entity = ReptileEntity::class,
//        parentColumns = ["id"],
//        childColumns = ["reptileId"],
//        onDelete = ForeignKey.CASCADE
//    )],
//    indices = [Index("reptileId")]
//)
//data class DietLogEntity(
//    @PrimaryKey(autoGenerate = true) val id: Long = 0,
//    val reptileId: Long,
//    val recordedAt: Long = System.currentTimeMillis(),
//    val vegetables: String = "",
//    val fruits: String = "",
//    val supplements: String = "",
//    val notes: String = ""
//)

// ─────────────────────────────────────────────
// RiwayatLog — Riwayat Sakit (Refactor Kesehatan)
// ─────────────────────────────────────────────
@Entity(
    tableName = "riwayat_logs",
    foreignKeys = [ForeignKey(
        entity = ReptileEntity::class,
        parentColumns = ["id"],
        childColumns = ["reptileId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("reptileId")]
)
data class RiwayatEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id") val id: Long = 0,
    @SerializedName("reptileId") val reptileId: Long,
    @SerializedName("illnessName") val illnessName: String,
    @SerializedName("notes") val notes: String = "",
    @SerializedName("startDate") val startDate: Long = System.currentTimeMillis(),
    @SerializedName("isOngoing") val isOngoing: Boolean = false,
    @SerializedName("endDate") val endDate: Long? = null,
    @SerializedName("createdAt") val createdAt: Long = System.currentTimeMillis()
)

// ─────────────────────────────────────────────
// RiwayatPhoto — foto dokumentasi sakit (many-to-one)
// ─────────────────────────────────────────────
@Entity(
    tableName = "riwayat_photos",
    foreignKeys = [ForeignKey(
        entity = RiwayatEntity::class,
        parentColumns = ["id"],
        childColumns = ["riwayatId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("riwayatId")]
)
data class RiwayatPhotoEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id") val id: Long = 0,
    @SerializedName("riwayatId") val riwayatId: Long,
    @SerializedName("photoUri") val photoUri: String,
    @SerializedName("takenAt") val takenAt: Long = System.currentTimeMillis()
)

// ─────────────────────────────────────────────
// HealthRecord — catatan kesehatan & reminder
// ─────────────────────────────────────────────
//@Entity(
//    tableName = "health_records",
//    foreignKeys = [ForeignKey(
//        entity = ReptileEntity::class,
//        parentColumns = ["id"],
//        childColumns = ["reptileId"],
//        onDelete = ForeignKey.CASCADE
//    )],
//    indices = [Index("reptileId")]
//)
//data class HealthRecordEntity(
//    @PrimaryKey(autoGenerate = true)
//    val id: Long = 0,
//    val reptileId: Long,
//    val date: Long = System.currentTimeMillis(),
//    val type: String,              // VET_VISIT / MEDICATION / PARASITE_TREATMENT / OTHER
//    val title: String,
//    val notes: String = "",
//    val nextReminderAt: Long? = null // epoch ms untuk WorkManager reminder
//)
