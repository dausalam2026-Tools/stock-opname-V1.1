# Stock Opname – Tool Store (Android, Kotlin + Jetpack Compose)

Aplikasi Android native untuk verifikasi stock tool GMF AeroAsia, dibangun
berdasarkan prototype web yang sudah divalidasi sebelumnya. Alur dan data
sama persis dengan prototype, tapi kamera & PDF sekarang benar-benar native.

**Fitur tambah tool (Master Data) sudah pakai Firebase** — supaya tool baru
+ foto yang ditambahkan satu HP langsung terlihat semua tim, lihat
`FIREBASE_SETUP.md` (**wajib** di-setup sekali sebelum app ini bisa di-build).
Hasil pemeriksaan Stock Opname (OK/Discrepancy per lokasi) masih tersimpan
lokal per HP — kalau itu juga mau dibuat real-time antar tim, itu fitur
terpisah, lihat `SHARED_DATA_GUIDE.md` (opsional, belum dipasang).

## Peta dokumen
| Butuh apa? | Buka file |
|---|---|
| Paham alur & kode secara singkat | `RINGKASAN_PROJECT.md` |
| Rekap lengkap dari awal project | `TUTORIAL_LENGKAP.md` |
| **Setup Firebase untuk tambah tool + foto (wajib sekali)** | `FIREBASE_SETUP.md` |
| **Update data master tool** (paling sering dipakai ke depan) | `CARA_UPDATE_DATA_MASTER.md` |
| Build via GitHub Actions (tanpa Android Studio) | `CARA_DAPAT_APK.md` |
| Build via Android Studio | `CARA_ANDROID_STUDIO.md` |
| Sinkronisasi hasil opname antar-tim (opsional, terpisah) | `SHARED_DATA_GUIDE.md` |

## Dapatkan file .apk TANPA install Android Studio
Kalau belum bisa pakai Android Studio, build APK-nya lewat **GitHub
Actions** — komputer Google/GitHub yang meng-compile untuk Anda, gratis,
cukup lewat browser. Workflow-nya sudah disiapkan di
`.github/workflows/build-apk.yml`.

👉 **Ikuti panduan super detail, klik-per-klik, di file
[`CARA_DAPAT_APK.md`](./CARA_DAPAT_APK.md)** — mulai dari buat akun
GitHub sampai APK ter-install di HP.

Ringkasnya: buat repo GitHub → masukkan folder project ini ke repo
(pakai GitHub Desktop atau upload lewat web) → tab **Actions** akan
build otomatis (±3–6 menit) → download APK dari bagian **Artifacts** →
pindahkan ke HP → install.

## Yang sudah berfungsi native (bukan simulasi lagi)
- **Kamera & scan barcode**: CameraX + ML Kit Barcode Scanning (on-device,
  tanpa internet). Mendukung Code128, EAN-13/8, UPC-A/E, QR, Code39, Codabar.
- **PDF Report**: dibuat dengan `android.graphics.pdf.PdfDocument` (tanpa
  library tambahan), lalu dibagikan/disimpan lewat share-sheet Android biasa
  (`Intent.ACTION_SEND` + FileProvider) — bisa disimpan ke Drive, dikirim ke
  WhatsApp, dicetak, dsb.
- **Login, Menu, Pilih Store → Pilih Location, Verifikasi, Riwayat, Master
  Data** — replika 1:1 dari alur di prototype web.

## Data
`app/src/main/assets/masterdata.json` berisi seluruh 5.424 tool asli dari
Master Tools Database (Store W1/W2/LG, 760 lokasi) yang sudah diekstrak
sebelumnya. Data ini di-bundle di dalam aplikasi (read-only, sama untuk semua
device) — bukan progress opname, yang tersimpan terpisah per device.

## Cara build lewat Android Studio
👉 **Panduan super detail, klik-per-klik, ada di
[`CARA_ANDROID_STUDIO.md`](./CARA_ANDROID_STUDIO.md)** — mulai dari
install Android Studio sampai APK ter-install di HP.

Ringkasnya:
1. Install **Android Studio** (Koala/2024.1 atau lebih baru).
2. `File > Open` → pilih folder `StockOpnameApp` ini.
3. Tunggu Gradle sync (butuh internet untuk download dependency pertama
   kali — CameraX, ML Kit, Compose, dsb dari Google Maven).
4. Sambungkan HP Android (aktifkan USB debugging) atau pakai emulator, lalu
   tekan **Run ▶**.

Minimum Android 7.0 (API 24). Kamera perlu Android 5.0+ untuk CameraX, sudah
lebih dari cukup.

## Struktur proyek
```
app/src/main/java/com/gmf/stockopname/
  data/         → model data, repository (load masterdata.json), penyimpanan
                  progress lokal (OpnameStore), dan AppViewModel
  pdf/          → PdfGenerator.kt (generate + share PDF)
  ui/screens/   → satu file per layar (Login, Menu, PilihStore, dst.)
  ui/navigation/→ AppNavGraph.kt (rute antar layar)
  ui/theme/     → warna & tipografi (menyamai branding navy di web)
```

## Progress tersimpan di mana?
Saat ini progress opname (hasil OK/Discrepancy per tool, riwayat) disimpan
lokal di tiap HP (SharedPreferences) — persis seperti localStorage di
prototype web. **Kalau butuh data yang sama terlihat oleh semua anggota
tim** (misalnya Anda opname di HP A, rekan kerja langsung lihat hasilnya
di HP B), lihat `SHARED_DATA_GUIDE.md` — panduan mengaktifkan sinkronisasi
lewat Firebase Firestore.

## Belum termasuk di versi ini
- Menu "Stock Op 7" dan "Tool Borrowing" (sengaja dihapus sesuai permintaan
  sebelumnya — tinggal bilang kalau mau ditambahkan kembali).
- Sinkronisasi data antar-device (lihat `SHARED_DATA_GUIDE.md`).
- Ikon aplikasi masih ikon sederhana (kunci pas) — bisa diganti dengan logo
  resmi GMF AeroAsia kapan saja.
