# Cara Update Data Master Tool (ke Depannya)

Data referensi tool (5.424 tool, Store W1/W2/LG) tersimpan di **satu
file**: `app/src/main/assets/masterdata.json`. Kalau database sumbernya
berubah (tool baru ditambah, lokasi dipindah, status diperbarui, dst),
file ini yang perlu diganti, lalu aplikasi di-build ulang.

**Progress opname yang sudah tersimpan di HP (hasil OK/Discrepancy)
TIDAK ikut hilang** saat data master diganti — keduanya disimpan
terpisah (lihat `RINGKASAN_PROJECT.md` bagian `data/`).

Ada 2 cara. Pilih yang paling nyaman untuk Anda.

---

## Cara 1 (paling gampang): kirim ke Claude
1. Export database terbaru dari Google Sheets/Excel Anda ke format
   **Excel (.xlsx)**, **CSV**, atau tetap **PDF** seperti sebelumnya.
2. Upload file itu ke saya di chat, bilang "ini update data master yang
   baru, tolong perbarui".
3. Saya akan proses & konversi datanya, lalu kirimkan **satu file
   pengganti** (`masterdata.json`) atau **project zip lengkap** yang
   sudah ter-update.
4. Anda tinggal ganti file itu di GitHub (lewat GitHub Desktop atau edit
   langsung di web), lalu build ulang seperti biasa (lihat
   `CARA_DAPAT_APK.md` bagian akhir, "Kalau Ingin Update Aplikasi
   Nanti").

Ini cara yang disarankan kalau Anda tidak terbiasa menjalankan program/
script sendiri.

---

## Cara 2 (mandiri): jalankan script konversi sendiri
Kalau di komputer Anda sudah ada **Python 3**, Anda bisa mengubah data
sendiri tanpa menunggu saya, pakai `tools/convert_masterdata.py` yang
sudah disiapkan di dalam project.

### Syarat file sumber
Export database Anda (dari Google Sheets: **File > Download > Microsoft
Excel (.xlsx)** atau **Comma-separated values (.csv)**) harus punya
kolom-kolom ini (nama boleh huruf besar/kecil bebas, urutan kolom bebas):

```
Status | Tool Number | Part Number | Key Number | Serial Number |
Tool Description | Manufacture | Model | Category | Store | Location
```

(Persis seperti struktur database asli yang sudah Anda pakai — kalau
Anda hanya menambah baris baru di Google Sheets yang sama, formatnya
otomatis sudah cocok.)

### Langkah menjalankan
1. Install Python 3 kalau belum ada: **https://www.python.org/downloads/**
   (saat install, centang **"Add Python to PATH"**).
2. Buka **Command Prompt** (Windows) atau **Terminal** (Mac), lalu
   install 2 library yang dibutuhkan:
   ```
   pip install pandas openpyxl
   ```
3. Masuk ke folder project:
   ```
   cd path/ke/StockOpnameApp/tools
   ```
4. Jalankan (ganti path sesuai lokasi file Excel/CSV Anda):
   ```
   python convert_masterdata.py "C:\Users\Anda\Downloads\database_baru.xlsx"
   ```
5. Script akan menampilkan ringkasan (berapa tool, store apa saja yang
   ditemukan) dan otomatis menimpa file
   `app/src/main/assets/masterdata.json` dengan data baru.
6. Lanjutkan seperti biasa: buka **GitHub Desktop** → **Commit to main**
   → **Push origin** → tunggu build di tab **Actions** → download APK
   baru → install ke HP.

### Kalau muncul store baru (bukan W1/W2/LG)
Aplikasi tetap jalan normal (nama store mentah akan tampil apa adanya),
tapi supaya tampil rapi dengan nama lengkap, tambahkan satu baris di:

`app/src/main/java/com/gmf/stockopname/data/Models.kt`

cari bagian:
```kotlin
val STORE_LABELS = mapOf(
    "W1" to "Store W1",
    "W2" to "Store W2",
    "LG" to "Store LG (Landing Gear)"
)
```
tambahkan baris baru sesuai kode store barunya, misalnya:
```kotlin
    "W3" to "Store W3 (nama yang Anda mau)",
```

### Kalau ragu / hasil terasa aneh
Kirim saja file sumbernya ke saya sekalian — saya bisa bantu cek atau
langsung proses lewat Cara 1 di atas.
