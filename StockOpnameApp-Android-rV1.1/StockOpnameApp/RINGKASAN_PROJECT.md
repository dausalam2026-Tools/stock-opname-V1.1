# Ringkasan Source Code — Stock Opname Tool Store

Dokumen ini menjelaskan setiap bagian kode: apa isinya, dan kalau mau
mengubah sesuatu, file mana yang harus disentuh.

## Gambaran besar
Aplikasi Android native, ditulis dengan **Kotlin** + **Jetpack Compose**
(framework UI modern dari Google, ganti XML layout lama). Alurnya:

```
Login → Menu Utama → [Stock Op 6: Pilih Store → Pilih Location →
Verifikasi (stat + daftar tool) → Scan Barcode (kamera) → Detail Tool →
OK/Discrepancy → ulang sampai 100% → Selesai → Generate & share PDF]
                  → [Master Data: cari & lihat detail tool]
                  → [Riwayat: histori tiap location yang pernah diperiksa]
```

## Struktur folder
```
StockOpnameApp/
├─ app/src/main/
│  ├─ assets/masterdata.json        ← DATA MASTER TOOL (lihat CARA_UPDATE_DATA_MASTER.md)
│  ├─ AndroidManifest.xml           ← izin (kamera), FileProvider untuk share PDF
│  ├─ res/                          ← ikon aplikasi, strings.xml, tema dasar
│  └─ java/com/gmf/stockopname/
│     ├─ MainActivity.kt            ← titik masuk aplikasi (satu-satunya Activity)
│     ├─ data/                      ← "otak" aplikasi: data & state
│     ├─ pdf/PdfGenerator.kt        ← bikin PDF report + share
│     └─ ui/
│        ├─ theme/                  ← warna, tipografi (branding navy)
│        ├─ navigation/AppNavGraph.kt ← rute antar layar
│        └─ screens/                ← satu file per layar
├─ .github/workflows/build-apk.yml  ← resep build otomatis APK di GitHub
├─ README.md, CARA_*.md             ← panduan-panduan yang sudah dikirim sebelumnya
```

## Folder `data/` (paling penting untuk logika aplikasi)

| File | Isi |
|---|---|
| `Models.kt` | Bentuk data: `Tool` (satu baris tool), `LocationStats` (total/checked/OK/discrepancy), `STORE_LABELS` & `STATUS_LABELS` (nama tampilan untuk kode Store/Status) |
| `MasterDataRepository.kt` | Baca `assets/masterdata.json` sekali saat app dibuka, ubah jadi struktur `Store → Location → List<Tool>` yang dipakai semua layar |
| `OpnameStore.kt` | Simpan **hasil pemeriksaan** (OK/Discrepancy per tool) & riwayat tanggal selesai, ke `SharedPreferences` (memori lokal HP) — ini yang bikin progress tidak hilang walau app ditutup |
| `AppViewModel.kt` | Penghubung antara layar (UI) dan data di atas — nyimpen "lagi buka store/location mana", filter pencarian, dsb |

**Penting**: `masterdata.json` (data referensi tool — statis, dari database
Anda) dan `OpnameStore` (hasil opname — berubah tiap Anda pakai app) itu
**dua hal terpisah**. Update data master TIDAK menghapus histori
pemeriksaan yang sudah ada, karena disimpan di tempat berbeda.

## Folder `ui/screens/` (satu file = satu layar)

| File | Layar |
|---|---|
| `LoginScreen.kt` | Login (username/password apa saja) |
| `MenuScreen.kt` | Menu Utama (Stock Op 6, Master Data) |
| `PilihStoreScreen.kt` | Pilih Store (langkah 1) + link ke Riwayat |
| `PilihLocationScreen.kt` | Pilih Location + kotak cari (langkah 2) |
| `LocationDetailScreen.kt` | Stat total/checked/discrepancy + daftar tool + tombol Mulai/Lanjutkan |
| `ScanScreen.kt` | Kamera (CameraX) + deteksi barcode otomatis (ML Kit) + tombol manual |
| `DetailToolScreen.kt` | Detail 1 tool + tombol OK / Discrepancy |
| `CompleteAndReportScreens.kt` | Layar "Selesai" + layar Generate/Share PDF |
| `RiwayatScreen.kt` | Daftar histori (tab Semua/Selesai/Proses) |
| `MasterDataScreen.kt` | Cari & filter semua tool + layar detail read-only |
| `Common.kt` | Komponen kecil yang dipakai berulang (header biru, badge status, kartu, dst) |

## `pdf/PdfGenerator.kt`
Bikin file PDF native pakai `android.graphics.pdf.PdfDocument` (bawaan
Android, tanpa library tambahan), lalu bagikan lewat share-sheet Android
biasa (`Intent.ACTION_SEND`) — bisa disimpan ke Drive, kirim WhatsApp,
dsb.

## `ui/navigation/AppNavGraph.kt`
Mengatur "layar mana yang dibuka setelah tombol X ditekan". Rute-nya
sengaja dibuat sederhana (mis. `"scan"`, `"detail"`) — data sebenarnya
(store/location yang dipilih) disimpan di `AppViewModel`, bukan di URL
rute, karena nama location di database asli banyak yang mengandung
karakter aneh (`/`, `#`, spasi) yang akan bikin masalah kalau dijadikan
bagian dari rute navigasi.

## `.github/workflows/build-apk.yml`
Resep untuk GitHub Actions: setiap kali Anda `push`/commit ke GitHub,
server GitHub otomatis menjalankan build dan menghasilkan `app-debug.apk`
yang bisa didownload dari tab **Actions > (build terkait) > Artifacts**.
Ini yang sudah berhasil Anda pakai untuk install aplikasinya.
