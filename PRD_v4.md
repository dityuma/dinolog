# PRD — DinoLog v4.0
**Version:** 4.0  
**Status:** In Development  
**Last Updated:** Agustus 2025  
**Perubahan dari v3.0:** Penambahan fitur Edit untuk semua record, dukungan multi-foto untuk Karapas (Scute), visualisasi pertumbuhan yang lebih baik (Grafik Berat & Panjang), dan peningkatan UI Detail Reptil.

---

## 1. Product Overview

**DinoLog** adalah aplikasi Android offline-first khusus untuk mencatat pertumbuhan dan perawatan **kura-kura darat (tortoise)**. 

**Visi v4.0:** Memberikan fleksibilitas bagi pengguna untuk memperbaiki data yang salah (Edit) dan pemantauan visual kondisi karapas yang lebih mendalam.

---

## 2. Fitur Utama (Update v4.0)

1.  **Sistem Pengeditan Record (NEW):**
    *   Pengguna dapat mengedit profil kura-kura.
    *   Pengguna dapat mengedit catatan makan (Feeding Log).
    *   Pengguna dapat mengedit catatan pertumbuhan (Growth Log).
    *   Pengguna dapat mengedit kondisi karapas (Scute Log).
2.  **Pemantauan Karapas dengan Foto (NEW):**
    *   Dukungan hingga 4 foto per catatan kondisi karapas.
    *   Penyimpanan internal yang aman dan terisolasi.
3.  **Visualisasi Pertumbuhan Ganda (IMPROVED):**
    *   Grafik interaktif menggunakan Vico.
    *   Toggle antara grafik **Berat (gram)** dan **Panjang (cm)**.
4.  **Dialog Pratinjau Foto:**
    *   Menampilkan foto profil atau foto log dalam ukuran penuh dengan latar belakang redup (overlay).

---

## 3. Tech Stack Update

*   **Charts:** Vico 2.0.1 (mendukung grafik garis interaktif).
*   **Database:** Room v3 (dengan migrasi otomatis/manual dari v1 dan v2).
*   **Image Preview:** Custom Dialog + Coil.

---

## 4. Database Schema Update (v3)

### 4.1 ScutePhotoEntity (`scute_photos`) — NEW
Menyimpan banyak foto untuk satu catatan kondisi karapas.
| Field | Type | Keterangan |
|---|---|---|
| id | Long (PK) | |
| scuteLogId | Long (FK) | Relasi ke `scute_logs.id` (CASCADE) |
| photoUri | String | Path internal |
| takenAt | Long | Timestamp |

### 4.2 GrowthPhotoEntity (`growth_photos`) — EXISTING
Digunakan untuk menyimpan foto pada log pertumbuhan.

---

## 5. Navigation & Screen Map

| Route | Screen | Deskripsi |
|---|---|---|
| `reptile_list` | ReptileListScreen | Daftar kura-kura |
| `reptile_detail/{id}`| ReptileDetailScreen | Detail & Grafik |
| `edit_reptile/{id}` | EditReptileScreen | **(NEW)** Edit profil |
| `edit_feeding_log/{rid}/{lid}` | EditFeedingLogScreen | **(NEW)** Edit catatan makan |
| `edit_growth_log/{rid}/{lid}` | EditGrowthLogScreen | **(NEW)** Edit catatan tumbuh |
| `edit_scute_log/{rid}/{lid}` | EditScuteLogScreen | **(NEW)** Edit catatan karapas |
| ... | (Add screens) | Sesuai PRD v3.0 |

---

## 6. Spesifikasi UI Baru

### 6.1 ReptileDetailScreen Improvements
*   **Header:** Klik pada kartu header membuka `EditReptileScreen`.
*   **Tab Tumbuh:**
    *   Segmented Button untuk switch Grafik Berat / Panjang.
    *   Grafik tampil jika minimal ada 2 data.
    *   Klik pada kartu log pertumbuhan membuka `EditGrowthLogScreen`.
*   **Tab Makan:** Klik pada kartu log makan membuka `EditFeedingLogScreen`.
*   **Tab Karapas:** Menampilkan grid foto (maks 4) per log. Klik kartu membuka `EditScuteLogScreen`.

### 6.2 Add/Edit ScuteLogScreen
*   **Photo Picker:** Pilihan "Ambil Foto" atau "Pilih dari Galeri".
*   **Limit:** Maksimal 4 foto per sesi.
*   **Warning UI:** 
    *   "Piramiding" -> Background Oranye.
    *   "Shell Lunak" -> Background Merah (Error).

---

## 7. Aturan Bisnis (Update)

1.  **Migrasi Data:** Pastikan data dari v2 (shedding_logs) bermigrasi ke v3 (scute_logs) tanpa kehilangan catatan.
2.  **Manajemen File:** Foto yang dihapus saat pengeditan harus dibersihkan dari penyimpanan internal untuk menghemat ruang.
3.  **Validation:** Field utama (seperti `foodType` atau `recordedAt`) tetap wajib diisi pada mode edit.
4.  **Grafik:** Data yang ditampilkan di grafik adalah data yang memiliki nilai (non-null) untuk field terkait.

---

## 8. Development Roadmap

*   [x] Migrasi Room Database ke v3.
*   [x] Implementasi UI Edit untuk Feeding & Growth.
*   [x] Implementasi Multi-photo support untuk Scute.
*   [x] Integrasi Grafik Vico untuk pertumbuhan.
*   [ ] Integrasi WorkManager untuk pengingat (Next Phase).
*   [ ] Export data ke CSV/Excel (Next Phase).
