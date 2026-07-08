package com.dites.dinolog.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ─────────────────────────────────────────────
// Reptile — master data hewan
// ─────────────────────────────────────────────
@Entity(tableName = "reptiles")
data class ReptileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val species: String,           // e.g. "Sulcata", "Parietal"
    val gender: String = "TIDAK_DIKETAHUI", // MALE / FEMALE / TIDAK_DIKETAHUI
    val isRescue: Boolean = false,
    val profilePhotoUri: String = "",
    val birthDate: Long? = null,
    val acquireDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
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
    val id: Long = 0,
    val reptileId: Long,
    val recordedAt: Long = System.currentTimeMillis(),
    val weightGrams: Float? = null,  // gram — lebih presisi untuk reptil kecil
    val lengthCm: Float? = null,
    val bodyCondition: String = "IDEAL", // UNDERWEIGHT / SLIM / IDEAL / HEAVY / OBESE
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
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
    val id: Long = 0,
    val growthLogId: Long,
    val photoUri: String,
    val takenAt: Long = System.currentTimeMillis()
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
    val id: Long = 0,
    val reptileId: Long,
    val feedingDate: Long = System.currentTimeMillis(),
    val foodType: String,          // e.g. "Kangkung", "Sawi"
    val foodAmount: String = "",   // e.g. "Segenggam"
    val accepted: Boolean = true,
    val notes: String = ""
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
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reptileId: Long,
    val recordedAt: Long = System.currentTimeMillis(),
    val condition: String = "NORMAL", // NORMAL / PIRAMIDING / SOFT_SHELL / RETAK / JAMUR
    val notes: String = ""
)

// ─────────────────────────────────────────────
// SoakingLog — Log perendaman (PRD v3)
// ─────────────────────────────────────────────
@Entity(
    tableName = "soaking_logs",
    foreignKeys = [ForeignKey(
        entity = ReptileEntity::class,
        parentColumns = ["id"],
        childColumns = ["reptileId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("reptileId")]
)
data class SoakingLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reptileId: Long,
    val soakingDate: Long = System.currentTimeMillis(),
    val durationMinutes: Int,
    val waterTempCelsius: Float? = null,
    val notes: String = ""
)

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
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reptileId: Long,
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long? = null,
    val weightBeforeGrams: Float? = null,
    val weightAfterGrams: Float? = null,
    val location: String = "",
    val notes: String = ""
)

// ─────────────────────────────────────────────
// UvbBasingLog — Log berjemur (PRD v3)
// ─────────────────────────────────────────────
@Entity(
    tableName = "uvb_basing_logs",
    foreignKeys = [ForeignKey(
        entity = ReptileEntity::class,
        parentColumns = ["id"],
        childColumns = ["reptileId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("reptileId")]
)
data class UvbBasingLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reptileId: Long,
    val sessionDate: Long = System.currentTimeMillis(),
    val durationMinutes: Int,
    val uvbType: String = "",
    val basikingTempCelsius: Float? = null,
    val notes: String = ""
)

// ─────────────────────────────────────────────
// DietLog — Log detail menu (PRD v3)
// ─────────────────────────────────────────────
@Entity(
    tableName = "diet_logs",
    foreignKeys = [ForeignKey(
        entity = ReptileEntity::class,
        parentColumns = ["id"],
        childColumns = ["reptileId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("reptileId")]
)
data class DietLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reptileId: Long,
    val recordedAt: Long = System.currentTimeMillis(),
    val vegetables: String = "",
    val fruits: String = "",
    val supplements: String = "",
    val notes: String = ""
)

// ─────────────────────────────────────────────
// SheddingLog — Log ganti kulit (PRD v3)
// ─────────────────────────────────────────────
@Entity(
    tableName = "shedding_logs",
    foreignKeys = [ForeignKey(
        entity = ReptileEntity::class,
        parentColumns = ["id"],
        childColumns = ["reptileId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("reptileId")]
)
data class SheddingLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reptileId: Long,
    val sheddingDate: Long = System.currentTimeMillis(),
    val status: String = "COMPLETE", // COMPLETE / PARTIAL / STUCK
    val notes: String = ""
)

// ─────────────────────────────────────────────
// HealthRecord — catatan kesehatan & reminder
// ─────────────────────────────────────────────
@Entity(
    tableName = "health_records",
    foreignKeys = [ForeignKey(
        entity = ReptileEntity::class,
        parentColumns = ["id"],
        childColumns = ["reptileId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("reptileId")]
)
data class HealthRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val reptileId: Long,
    val date: Long = System.currentTimeMillis(),
    val type: String,              // VET_VISIT / MEDICATION / PARASITE_TREATMENT / OTHER
    val title: String,
    val notes: String = "",
    val nextReminderAt: Long? = null // epoch ms untuk WorkManager reminder
)
