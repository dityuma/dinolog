# PRD — DinoLog v4.0
**Version:** 4.0  
**Status:** In Development  
**Last Updated:** Februari 2025  
**Perubahan dari v3.0:** 
- Implementasi **Riwayat Sakit** mendalam dengan dukungan foto (maks 4) dan status (sembuh/masih sakit).
- Dukungan foto untuk **Kondisi Karapas (Scute Log)**.
- Penambahan fitur **Edit/Update** untuk hampir semua jenis log (Feeding, Growth, Riwayat, Scute, Brumasi).
- Refinement skema database (v6) dan penghapusan field `location` pada Brumasi.
- Peningkatan manajemen asset foto di internal storage.

---

## 1. Product Overview

**DinoLog** adalah aplikasi Android offline-first khusus untuk mencatat pertumbuhan dan perawatan **kura-kura darat (tortoise)**. 

**Fitur Utama:**
1. **Profil Kura-kura:** Identitas lengkap (spesies, jenis kelamin, tanggal lahir/adopsi).
2. **Pertumbuhan:** Catat berat (gram) dan panjang (cm) beserta foto.
3. **Pemberian Makan:** Jenis pakan herbivora dan porsi.
4. **Kesehatan & Riwayat Sakit:** Tracking penyakit dari mulai hingga sembuh dengan dokumentasi foto.
5. **Kondisi Karapas:** Pantau gejala piramiding, shell lunak, atau jamur dengan foto.
6. **Perawatan Rutin:** Soaking (mandi), Sesi UVB/Basking, dan detail Diet/Suplemen.
7. **Brumasi:** Catat periode hibernasi dan perubahan berat badan.

**Filosofi:** 100% Offline, Privacy-focused, No cloud, No accounts.

---

## 2. Target User

**Keeper kura-kura darat (Tortoise Keepers)** yang ingin mendokumentasikan perkembangan peliharaan mereka secara detail dan terstruktur tanpa bergantung pada koneksi internet.

---

## 3. Tech Stack

| Layer | Teknologi |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material3 |
| Architecture | MVVM (ViewModel + StateFlow) |
| Database | Room (Version 6) |
| Image Loading | Coil (coil-compose) |
| Charts | Vico (pencatatan pertumbuhan) |
| Navigation | Navigation Compose |
| Reminders | WorkManager (untuk Health Records) |
| Min SDK | API 26 (Android 8.0) |

---

## 4. Database Schema (v6)

### 4.1 RiwayatEntity (`riwayat_logs`) — BARU/REFACTOR
Mencatat sejarah penyakit secara mendalam.
| Field | Type | Keterangan |
|---|---|---|
| id | Long (PK) | |
| reptileId | Long (FK) | |
| illnessName | String | Wajib diisi |
| notes | String | Deskripsi gejala/pengobatan |
| startDate | Long | Tanggal mulai sakit |
| isOngoing | Boolean | Status kesembuhan |
| endDate | Long? | Tanggal sembuh |
| createdAt | Long | Timestamp pembuatan |

### 4.2 RiwayatPhotoEntity (`riwayat_photos`) — BARU
| Field | Type | Keterangan |
|---|---|---|
| id | Long (PK) | |
| riwayatId | Long (FK) | |
| photoUri | String | Internal storage path |
| takenAt | Long | Timestamp foto |

### 4.3 ScutePhotoEntity (`scute_photos`) — BARU
Mendukung dokumentasi visual kondisi tempurung.
| Field | Type | Keterangan |
|---|---|---|
| id | Long (PK) | |
| scuteLogId | Long (FK) | |
| photoUri | String | |

### 4.4 BrumasiLogEntity (`brumasi_logs`) — UPDATE
Field `location` dihapus karena kurang relevan dibanding catatan detail.
| Field | Type | Keterangan |
|---|---|---|
| weightBeforeGrams | Float? | |
| weightAfterGrams | Float? | |
| notes | String | Deskripsi lokasi & kondisi digabung di sini |

---

## 5. Navigation & UI Flow

### 5.1 New Screens (v4)
- `AddRiwayatScreen`: Form input penyakit baru dengan multi-photo picker.
- `EditRiwayatScreen`: Update status kesembuhan atau tambah catatan pengobatan.
- `EditFeedingLogScreen`: Koreksi data pemberian makan.
- `EditScuteLogScreen`: Update kondisi karapas + foto.
- `EditGrowthLogScreen`: Koreksi data pertumbuhan.

### 5.2 Updated Detail Screen
`ReptileDetailScreen` kini memiliki tab yang lebih lengkap:
1. **Tumbuh**: Grafik & List pertumbuhan.
2. **Makan**: Histori pemberian pakan.
3. **Karapas**: Galeri kondisi tempurung.
4. **Medis**: Gabungan `Riwayat Sakit` (Penyakit) dan `Catatan Medis` (Vaksin/Obat cacing/Vet visit).
5. **Perawatan**: Soaking, UVB, Diet.
6. **Brumasi**: Histori istirahat panjang.

---

## 6. Aturan Bisnis & Validasi

1. **Batas Foto**: Maksimal 4 foto untuk Riwayat Sakit dan Growth Log untuk menjaga performa dan storage.
2. **Status "Sedang Sakit"**: Jika `isOngoing` true, record akan diberi highlight di UI agar keeper tetap waspada.
3. **Storage Management**: Foto disimpan di folder spesifik (`riwayat_photos`, `growth_photos`, `scute_photos`) di internal storage aplikasi.
4. **Data Integrity**: Menghapus data induk (Reptil) akan menghapus seluruh log dan file foto terkait (Cascade Delete + Manual Cleanup File).
5. **Sakit vs Medis**: 
   - `Riwayat Sakit` untuk kondisi patologis (Pneumonia, MBD, Jamur).
   - `Health Record` untuk tindakan preventif/rutin (Obat cacing, Check-up).

---

## 7. Roadmap Selanjutnya (v5.0)

- **Notification Engine**: Pengingat otomatis untuk jadwal jemur atau soaking berdasarkan input keeper.
- **Export Data**: Fitur ekspor seluruh data ke PDF/Excel untuk dibawa saat ke Dokter Hewan.
- **Dark Mode Support**: Optimasi tema UI.
- **Database Backup**: Local backup ke folder Downloads sebagai antisipasi ganti device.
