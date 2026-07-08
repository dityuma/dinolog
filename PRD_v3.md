# PRD — DinoLog v3.0
**Version:** 3.0  
**Status:** In Development  
**Last Updated:** Juli 2025  
**Perubahan dari v2.0:** Fokus dipersempit khusus untuk kura-kura darat (tortoise), tambah 4 fitur baru: Brumasi, Soaking, UVB & Basking, Diet & Sayuran

---

## 1. Product Overview

**DinoLog** adalah aplikasi Android offline-first khusus untuk mencatat pertumbuhan dan perawatan **kura-kura darat (tortoise)**. Fitur mencakup: pertumbuhan, pemberian makan, ganti kulit, kesehatan, soaking (mandi), brumasi (hibernasi), sesi UVB & basking, serta catatan diet.

**Tagline:** Catat perjalanan kura-kuramu.

**Filosofi utama:** Semua fitur berjalan 100% offline. Tidak ada login, tidak ada cloud sync.

---

## 2. Target User

**Keeper kura-kura darat** — memiliki 1–10 kura-kura darat sebagai hewan peliharaan, ingin mencatat pertumbuhan dan rutinitas perawatan harian/mingguan.

**Spesies yang didukung (contoh):**
- Russian Tortoise (Agrionemys horsfieldii)
- Sulcata Tortoise (Centrochelys sulcata)
- Hermann's Tortoise (Testudo hermanni)
- Greek Tortoise (Testudo graeca)
- Indian Star Tortoise (Geochelone elegans)
- Leopard Tortoise (Stigmochelys pardalis)
- Red-footed Tortoise (Chelonoidis carbonarius)
- Aldabra Tortoise (Aldabrachelys gigantea)

---

## 3. Tech Stack

| Layer | Teknologi |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material3 |
| Architecture | MVVM (ViewModel + StateFlow) |
| Database | Room (SQLite, lokal only) |
| Image Loading | Coil |
| Charts | Vico (compose-m3) |
| Navigation | Navigation Compose |
| Reminders | WorkManager |
| Min SDK | API 26 (Android 8.0) |
| Package Name | com.dites.dinolog |
| Application Class | DinoLogApplication |

---

## 4. Project Structure

```
com.dites.dinolog
├── DinoLogApplication.kt
├── MainActivity.kt
├── data
│   ├── local
│   │   ├── DinoLogDatabase.kt
│   │   ├── dao
│   │   │   └── Daos.kt
│   │   └── entity
│   │       └── Entities.kt
│   └── repository
│       └── DinoLogRepository.kt
└── ui
    ├── navigation
    │   └── NavGraph.kt
    ├── screen
    │   ├── ReptileListScreen.kt
    │   ├── ReptileDetailScreen.kt
    │   ├── AddReptileScreen.kt
    │   ├── AddGrowthLogScreen.kt
    │   ├── AddFeedingLogScreen.kt
    │   ├── AddSheddingLogScreen.kt
    │   ├── AddHealthRecordScreen.kt
    │   ├── AddSoakingLogScreen.kt       ← BARU
    │   ├── AddBrumasiLogScreen.kt       ← BARU
    │   ├── AddUvbBasingLogScreen.kt     ← BARU
    │   └── AddDietLogScreen.kt          ← BARU
    ├── viewmodel
    │   └── ViewModels.kt
    └── theme
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

---

## 5. Database Schema

### Entity yang sudah ada (tidak berubah):
- ReptileEntity
- GrowthLogEntity
- GrowthPhotoEntity
- HealthRecordEntity

### Entity yang dimodifikasi:

### 5.1 ReptileEntity (`reptiles`) — update field
| Field | Type | Keterangan |
|---|---|---|
| id | Long (PK, autoGenerate) | |
| name | String | Wajib diisi |
| species | String | Wajib diisi. e.g. "Sulcata Tortoise" |
| gender | String | JANTAN / BETINA / TIDAK_DIKETAHUI |
| isRescue | Boolean | Default false |
| profilePhotoUri | String | Path ke internal storage |
| birthDate | Long? | Epoch ms, nullable |
| acquireDate | Long? | Epoch ms, nullable |
| createdAt | Long | Epoch ms |

> ⚠️ Field `morph` dihapus — tidak relevan untuk kura-kura darat

### 5.2 FeedingLogEntity (`feeding_logs`) — update field
| Field | Type | Keterangan |
|---|---|---|
| id | Long (PK, autoGenerate) | |
| reptileId | Long (FK → reptiles.id) | CASCADE DELETE |
| feedingDate | Long | Epoch ms |
| foodType | String | Jenis makanan (sayuran/buah/suplemen) |
| foodAmount | String | e.g. "Segenggam", "2 lembar" |
| accepted | Boolean | true = dimakan, false = ditolak |
| notes | String | Default "" |

> ⚠️ Field `preyType` dan `preySizeGrams` diganti dengan `foodType` dan `foodAmount` — kura-kura darat herbivora

### 5.3 SheddingLogEntity (`shedding_logs`) — rename menjadi lebih relevan
> ⚠️ Kura-kura darat tidak shed kulit seperti gecko/snake. Entity ini diganti menjadi **ScuteLogEntity** untuk mencatat kondisi karapas (shell/scute).

### 5.4 ScuteLogEntity (`scute_logs`) — GANTI dari shedding_logs
| Field | Type | Keterangan |
|---|---|---|
| id | Long (PK, autoGenerate) | |
| reptileId | Long (FK → reptiles.id) | CASCADE DELETE |
| recordedAt | Long | Epoch ms |
| condition | String | NORMAL / PIRAMIDING / SOFT_SHELL / RETAK / JAMUR |
| notes | String | Default "" |

### Entity baru:

### 5.5 SoakingLogEntity (`soaking_logs`)
| Field | Type | Keterangan |
|---|---|---|
| id | Long (PK, autoGenerate) | |
| reptileId | Long (FK → reptiles.id) | CASCADE DELETE |
| soakingDate | Long | Epoch ms |
| durationMinutes | Int | Durasi dalam menit |
| waterTempCelsius | Float? | Suhu air (opsional) |
| notes | String | Default "" |

### 5.6 BrumasiLogEntity (`brumasi_logs`)
| Field | Type | Keterangan |
|---|---|---|
| id | Long (PK, autoGenerate) | |
| reptileId | Long (FK → reptiles.id) | CASCADE DELETE |
| startDate | Long | Epoch ms — mulai brumasi |
| endDate | Long? | Epoch ms — selesai brumasi, nullable jika masih berlangsung |
| weightBeforeGrams | Float? | Berat sebelum brumasi |
| weightAfterGrams | Float? | Berat setelah brumasi |
| location | String | Tempat brumasi e.g. "Kulkas", "Outdoor", "Kotak kayu" |
| notes | String | Default "" |

### 5.7 UvbBasingLogEntity (`uvb_basing_logs`)
| Field | Type | Keterangan |
|---|---|---|
| id | Long (PK, autoGenerate) | |
| reptileId | Long (FK → reptiles.id) | CASCADE DELETE |
| sessionDate | Long | Epoch ms |
| durationMinutes | Int | Durasi sesi dalam menit |
| uvbType | String | e.g. "UVB 10.0", "UVB 5.0", "Sinar Matahari Langsung" |
| basikingTempCelsius | Float? | Suhu basking spot (opsional) |
| notes | String | Default "" |

### 5.8 DietLogEntity (`diet_logs`)
| Field | Type | Keterangan |
|---|---|---|
| id | Long (PK, autoGenerate) | |
| reptileId | Long (FK → reptiles.id) | CASCADE DELETE |
| recordedAt | Long | Epoch ms |
| vegetables | String | Sayuran yang diberikan (multi-item, comma separated) |
| fruits | String | Buah yang diberikan (opsional) |
| supplements | String | Suplemen e.g. "Kalsium", "Multivitamin" (opsional) |
| notes | String | Default "" |

---

## 6. Navigation Routes

| Route | Screen | Deskripsi |
|---|---|---|
| `reptile_list` | ReptileListScreen | Home — daftar semua kura-kura |
| `add_reptile` | AddReptileScreen | Form tambah kura-kura baru |
| `reptile_detail/{reptileId}` | ReptileDetailScreen | Profil + tab detail |
| `add_growth_log/{reptileId}` | AddGrowthLogScreen | Form tambah catatan pertumbuhan |
| `add_feeding_log/{reptileId}` | AddFeedingLogScreen | Form tambah catatan makan |
| `add_scute_log/{reptileId}` | AddScuteLogScreen | Form tambah kondisi karapas |
| `add_health_record/{reptileId}` | AddHealthRecordScreen | Form tambah catatan kesehatan |
| `add_soaking_log/{reptileId}` | AddSoakingLogScreen | Form tambah catatan soaking |
| `add_brumasi_log/{reptileId}` | AddBrumasiLogScreen | Form tambah catatan brumasi |
| `add_uvb_basing_log/{reptileId}` | AddUvbBasingLogScreen | Form tambah sesi UVB & basking |
| `add_diet_log/{reptileId}` | AddDietLogScreen | Form tambah catatan diet |

---

## 7. Spesifikasi Screen & Teks UI

### 7.1 ReptileListScreen
| Elemen | Teks |
|---|---|
| TopAppBar title | "DinoLog" |
| Empty state | "Belum ada kura-kura. Ketuk + untuk menambahkan kura-kura pertamamu!" |
| FAB tooltip | "Tambah Kura-kura" |

### 7.2 AddReptileScreen
| Elemen | Teks |
|---|---|
| TopAppBar title | "Tambah Kura-kura" |
| Field: name | Label: "Nama", placeholder: "e.g. Kopi" |
| Field: species | Label: "Spesies", placeholder: "e.g. Sulcata Tortoise" |
| Field: gender | Label: "Jenis Kelamin" |
| Dropdown gender | "Jantan", "Betina", "Tidak Diketahui" |
| Field: isRescue | Label: "Kura-kura Rescue?" |
| Field: birthDate | Label: "Tanggal Lahir (opsional)" |
| Field: acquireDate | Label: "Tanggal Adopsi / Beli" |
| Field: photo | Label: "Foto Profil", button: "Pilih Foto" |
| Validasi name kosong | "Nama wajib diisi" |
| Validasi species kosong | "Spesies wajib diisi" |
| Tombol simpan | "Simpan" |

### 7.3 ReptileDetailScreen — Tab Layout (6 Tab)
| Elemen | Teks |
|---|---|
| Tab 1 | "Tumbuh" |
| Tab 2 | "Makan" |
| Tab 3 | "Karapas" |
| Tab 4 | "Kesehatan" |
| Tab 5 | "Perawatan" |
| Tab 6 | "Brumasi" |

**Tab Perawatan** menggabungkan 3 sub-fitur dalam satu tab:
- Soaking (Mandi)
- UVB & Basking
- Diet & Sayuran

Tampilkan dengan section header masing-masing + FAB dengan dropdown pilihan ketiga jenis catatan.

### 7.4 AddFeedingLogScreen
| Elemen | Teks |
|---|---|
| TopAppBar title | "Tambah Catatan Makan" |
| Field: date | Label: "Tanggal Pemberian Makan" |
| Field: foodType | Label: "Jenis Makanan", placeholder: "e.g. Kangkung" |
| Preset suggestions | "Kangkung", "Selada", "Wortel", "Timun", "Labu", "Pepaya", "Pisang", "Semangka", "Kaktus (Opuntia)", "Rumput Gajah", "Dandelion" |
| Field: foodAmount | Label: "Jumlah / Porsi", placeholder: "e.g. Segenggam, 3 lembar" |
| Toggle | "Dimakan" / "Ditolak" |
| Warning ditolak | "Kura-kura menolak makan — perhatikan kondisinya" |
| Field: notes | Label: "Catatan" |
| Tombol simpan | "Simpan" |

### 7.5 AddScuteLogScreen (ganti dari AddSheddingLogScreen)
| Elemen | Teks |
|---|---|
| TopAppBar title | "Tambah Kondisi Karapas" |
| Field: date | Label: "Tanggal Pencatatan" |
| Field: condition | Label: "Kondisi Karapas" |
| Dropdown condition | "Normal", "Piramiding", "Shell Lunak", "Retak / Luka", "Jamur / Bercak" |
| Warning piramiding | "Piramiding bisa disebabkan kelembaban rendah atau protein berlebih" |
| Warning soft shell | "Shell lunak memerlukan penanganan segera — hubungi dokter hewan!" |
| Field: notes | Label: "Catatan Detail" |
| Tombol simpan | "Simpan" |

### 7.6 AddSoakingLogScreen
| Elemen | Teks |
|---|---|
| TopAppBar title | "Tambah Catatan Soaking" |
| Field: date | Label: "Tanggal Soaking" |
| Field: duration | Label: "Durasi (menit)", placeholder: "e.g. 20" |
| Field: waterTemp | Label: "Suhu Air (°C, opsional)", placeholder: "e.g. 30" |
| Field: notes | Label: "Catatan", placeholder: "e.g. Kura-kura aktif minum" |
| Tombol simpan | "Simpan" |

### 7.7 AddBrumasiLogScreen
| Elemen | Teks |
|---|---|
| TopAppBar title | "Tambah Catatan Brumasi" |
| Field: startDate | Label: "Tanggal Mulai Brumasi" |
| Field: endDate | Label: "Tanggal Selesai Brumasi (opsional)" |
| Field: weightBefore | Label: "Berat Sebelum Brumasi (gram, opsional)" |
| Field: weightAfter | Label: "Berat Setelah Brumasi (gram, opsional)" |
| Field: location | Label: "Lokasi Brumasi" |
| Preset lokasi | "Kulkas", "Outdoor", "Kotak Kayu", "Basement", "Lainnya" |
| Field: notes | Label: "Catatan" |
| Info box | "Brumasi normal untuk beberapa spesies. Pantau berat badan secara berkala." |
| Tombol simpan | "Simpan" |

### 7.8 AddUvbBasingLogScreen
| Elemen | Teks |
|---|---|
| TopAppBar title | "Tambah Sesi UVB & Basking" |
| Field: date | Label: "Tanggal Sesi" |
| Field: duration | Label: "Durasi (menit)", placeholder: "e.g. 30" |
| Field: uvbType | Label: "Jenis UVB / Sumber Cahaya" |
| Preset UVB | "Sinar Matahari Langsung", "UVB 10.0", "UVB 5.0", "UVB 2.0", "Lampu Pijar Biasa" |
| Field: basikingTemp | Label: "Suhu Basking Spot (°C, opsional)", placeholder: "e.g. 40" |
| Field: notes | Label: "Catatan" |
| Tombol simpan | "Simpan" |

### 7.9 AddDietLogScreen
| Elemen | Teks |
|---|---|
| TopAppBar title | "Tambah Catatan Diet" |
| Field: date | Label: "Tanggal" |
| Field: vegetables | Label: "Sayuran", placeholder: "e.g. Kangkung, Selada" |
| Preset sayuran | "Kangkung", "Selada", "Wortel", "Timun", "Labu", "Kaktus (Opuntia)", "Rumput Gajah", "Dandelion" |
| Field: fruits | Label: "Buah (opsional)", placeholder: "e.g. Pepaya, Pisang (jarang)" |
| Field: supplements | Label: "Suplemen (opsional)" |
| Preset suplemen | "Kalsium", "Multivitamin", "Kalsium + D3" |
| Field: notes | Label: "Catatan" |
| Info box | "Sayuran berdaun hijau gelap = menu utama. Buah diberikan sesekali saja." |
| Tombol simpan | "Simpan" |

---

## 8. ViewModel Baru yang Dibutuhkan

Tambahkan ke ViewModels.kt:

### TortoiseCareViewModel
Mengelola semua log perawatan (soaking, UVB, diet, brumasi, scute):
- `soakingLogs: StateFlow<List<SoakingLogEntity>>`
- `brumasiLogs: StateFlow<List<BrumasiLogEntity>>`
- `uvbBasingLogs: StateFlow<List<UvbBasingLogEntity>>`
- `dietLogs: StateFlow<List<DietLogEntity>>`
- `scuteLogs: StateFlow<List<ScuteLogEntity>>`
- Methods: add/delete untuk masing-masing entity
- Factory: `TortoiseCareViewModelFactory(repository, reptileId)`

---

## 9. Aturan Bisnis

1. **Offline only** — tidak ada network call
2. **Cascade delete** — hapus kura-kura = hapus semua log terkait
3. **Berat dalam gram** — konsisten dengan v2.0
4. **Tolak makan** — `accepted = false` tampil dengan warna warning
5. **Shell lunak (SOFT_SHELL)** — tampil dengan warna error merah, kondisi darurat
6. **Piramiding** — tampil dengan warna warning oranye
7. **Brumasi aktif** — jika `endDate` null, tampilkan badge "Sedang Brumasi" di profil kura-kura
8. **Foto storage** — disimpan di `context.filesDir`, Room hanya simpan path
9. **Field morph dihapus** — tidak relevan untuk kura-kura darat
10. **Pakan herbivora** — preset makanan semua sayuran/buah, tidak ada prey hewan

---

## 10. Fitur Diluar Scope MVP

- Breeding / egg incubation tracking
- Enclosure sensor integration
- Export PDF / laporan
- Cloud backup
- Pengingat jadwal soaking otomatis (WorkManager — fase berikutnya)
- Kalkulator UVB exposure
- Forum / komunitas

---

## 11. Dependencies (tidak berubah dari v2.0)

```kotlin
implementation("androidx.room:room-runtime:2.7.1")
implementation("androidx.room:room-ktx:2.7.1")
ksp("androidx.room:room-compiler:2.7.1")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
implementation("androidx.navigation:navigation-compose:2.8.9")
implementation("io.coil-kt:coil-compose:2.7.0")
implementation("com.patrykandpatrick.vico:compose-m3:2.0.1")
implementation("androidx.work:work-runtime-ktx:2.10.1")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
```
