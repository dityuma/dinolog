# PRD — DinoLog v5.0
**Version:** 5.0  
**Status:** Stabilizing  
**Last Updated:** Maret 2025  

## Perubahan dari v4.0 ke v5.0:
- **Data Portability:** Implementasi penuh fitur **Export & Import Data** (JSON) ke folder Downloads.
- **Full CRUD Logs:** Finalisasi fitur **Edit/Update** untuk seluruh modul (Feeding, Growth, Riwayat, Scute, Brumasi).
- **Security & Stability:** Migrasi database Room v6 selesai (refactoring tabel Brumasi dan penambahan Scute/Riwayat Photos).
- **Monetization/Support:** Penambahan **Support Card** (Saweria) di menu Pengaturan untuk dukungan pengembangan.
- **Modernization:** Update Target SDK ke 35 (Android 15) dan pembersihan dependensi.

---

## 1. Product Overview

**DinoLog** adalah aplikasi pendamping (companion app) offline-first untuk pemilik reptil (khususnya kura-kura darat) guna mendokumentasikan setiap aspek pertumbuhan dan kesehatan hewan kesayangan secara lokal dan privat.

**Fitur Utama:**
1. **Multi-Pet Profile:** Kelola banyak reptil dengan identitas unik.
2. **Growth Analytics:** Grafik tren berat (gram) dan panjang (cm) menggunakan Vico Charts.
3. **Smart Logs:** Pencatatan Makan, Perendaman (Soaking), dan Sesi UVB/Basking.
4. **Medical Case Tracking:** Dokumentasi riwayat penyakit (Riwayat Sakit) dari gejala hingga sembuh dengan bukti foto.
5. **Shell Monitoring:** Pemantauan kondisi karapas (Scute Log) khusus untuk tortoise.
6. **Data Safety:** Backup manual ke format JSON yang bisa dipindahkan antar perangkat tanpa cloud.

---

## 2. Technical Stack

| Komponen | Teknologi |
|---|---|
| **Bahasa** | Kotlin |
| **UI Framework** | Jetpack Compose (Material3) |
| **Penyimpanan** | Room Persistence Library (Schema v6) |
| **Visualisasi Data** | Vico (Compose-M3 integration) |
| **Background Task** | WorkManager (Reminders) |
| **Serialization** | Gson (Export/Import JSON) |
| **Image Engine** | Coil |
| **Target SDK** | 35 (Android 15) |

---

## 3. Database & Data Integrity (v6)

### 3.1 Entitas Utama
- `ReptileEntity`: Data master identitas.
- `GrowthLog` & `GrowthPhoto`: Tracking fisik & visual pertumbuhan.
- `FeedingLog`: Histori nutrisi & nafsu makan.
- `Riwayat` & `RiwayatPhoto`: Tracking patologi (penyakit) & pemulihan.
- `ScuteLog` & `ScutePhoto`: Tracking kondisi tempurung.
- `BrumasiLog`: Tracking periode istirahat/hibernasi.
- `HealthRecord`: Catatan medis preventif (vaksin/obat cacing).

### 3.2 Data Management
- **Manual Backup**: File JSON disimpan di `Public Downloads/DinoLog_Backup_TIMESTAMP.json`.
- **Manual Restore**: Mengimpor file JSON untuk menggabung atau memulihkan database.

---

## 4. User Experience (UI) Flow

1. **Dashboard**: Daftar reptil dengan ringkasan status terakhir.
2. **Detail View**: 
   - Tab navigasi untuk tiap jenis log.
   - Grafik interaktif untuk memantau pertumbuhan.
   - Tombol aksi cepat untuk menambah catatan harian.
3. **Settings**: 
   - Manajemen Backup/Restore.
   - Informasi Versi.
   - Link Feedback (Email/GitHub).
   - Donasi/Dukungan (Saweria).

---

## 5. Business Rules

1. **Privacy First**: Tidak ada data yang dikirim ke server. Foto disimpan di internal storage aplikasi.
2. **Cascade Action**: Menghapus profil reptil akan menghapus seluruh log dan file foto terkait secara otomatis.
3. **Data Validation**: Berat badan dan panjang harus bernilai positif; tanggal log tidak boleh di masa depan.
4. **Photo Limits**: Direkomendasikan maksimal 4 foto per log record untuk efisiensi penyimpanan.

---

## 6. Roadmap Selanjutnya (v6.0)

- **Report Generator**: Ekspor rangkuman kesehatan ke format PDF/Excel untuk konsultasi ke Dokter Hewan.
- **Smart Reminders**: Notifikasi proaktif untuk jadwal pemberian vitamin atau penggantian lampu UVB.
- **Dark Mode Support**: Tema gelap yang dioptimalkan untuk kenyamanan penggunaan di malam hari.
- **Species Preset**: Template jenis pakan dan rentang suhu ideal berdasarkan spesies reptil.
