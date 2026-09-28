# Tutorial Lengkap — Dari Nol Sampai Terpasang di HP

Rekap seluruh perjalanan project ini, supaya kalau butuh mengulang dari
awal (HP baru, install ulang, atau kirim ke rekan tim), semua langkahnya
ada di satu tempat.

## 1. Asal-usul project
- Berawal dari mockup gambar (konsep: Login → Menu → Pilih Rak → Scan →
  Verifikasi → PDF Report → Riwayat).
- Dibuatkan dulu **prototype interaktif berbasis web** (untuk uji coba
  alur cepat, sebelum masuk ke Android asli).
- Data asli **Master Tools Database** (5.424 tool, 3 store: W1, W2, LG,
  760 lokasi) diekstrak dari PDF yang Anda berikan, diubah ke format
  JSON, lalu dipakai di prototype web maupun di app Android ini.
- Setelah alur di prototype dianggap cocok, dibuatkan **project Android
  native (Kotlin + Jetpack Compose)** — inilah yang sekarang sudah
  ter-install di HP Anda.

## 2. Apa saja yang sudah dibangun di app Android
- **Login** (bebas, belum ada validasi server — murni tampilan).
- **Menu Utama**: Stock Op 6, Master Data.
- **Stock Op 6**: Pilih Store → Pilih Location (dengan pencarian) →
  layar detail (stat total/checked/discrepancy) → Scan Barcode (kamera
  asli + deteksi otomatis ML Kit, ada juga tombol manual cadangan) →
  Detail Tool → tombol OK/Discrepancy → otomatis lanjut ke tool
  berikutnya → setelah 100% → layar Selesai → Generate PDF Report
  (native, bisa langsung di-share/simpan).
- **Riwayat**: semua location yang pernah diperiksa, dengan status
  Selesai/Proses dan tanggal-jam.
- **Master Data**: pencarian + filter (Store, Status) di seluruh 5.424
  tool, plus detail read-only per tool.
- Progress opname tersimpan otomatis di memori HP (tidak hilang saat
  app ditutup), terpisah dari data master.

## 3. Cara build-nya (yang sudah berhasil Anda lakukan)
Karena tidak bisa pakai Android Studio, dipakai **GitHub Actions**:
1. Buat akun & repository di github.com.
2. Masukkan seluruh folder project ke repo (pakai GitHub Desktop).
3. File `.github/workflows/build-apk.yml` di dalam project otomatis
   terdeteksi GitHub, lalu setiap kali ada perubahan/push, server GitHub
   men-compile project jadi `.apk` (memakai Android SDK + Gradle yang
   sudah tersedia otomatis di server mereka — gratis).
4. Hasil `.apk` didownload dari tab **Actions > (run yang ✅ hijau) >
   Artifacts**.
5. File `.apk` dipindahkan ke HP, di-install manual (dengan mengizinkan
   "sumber tidak dikenal" satu kali).

Sempat ada 2 error di tengah jalan yang sudah diperbaiki:
- Langkah "Setup Android SDK" pakai tools pihak ketiga yang tidak
  stabil → diganti pakai SDK yang memang sudah tersedia di server
  GitHub.
- Ada baris kode di `MasterDataScreen.kt` (untuk dropdown filter) yang
  kurang lengkap importnya → sudah ditambahkan.

## 4. Cara update aplikasi ke depannya
Setiap kali ada perubahan kode (fitur baru, perbaikan, atau **update
data master** — lihat `CARA_UPDATE_DATA_MASTER.md`):
1. Ganti file yang berubah di folder project di komputer Anda (saya
   akan kasih tahu file mana & isi barunya).
2. Buka **GitHub Desktop** → akan otomatis terdeteksi ada perubahan →
   klik **"Commit to main"** → klik **"Push origin"**.
3. Buka tab **Actions** di github.com, tunggu build baru selesai
   (±3-6 menit).
4. Download APK baru dari **Artifacts**, install ulang ke HP (menimpa
   yang lama — data/progress yang sudah tersimpan di HP tidak akan
   hilang selama nama aplikasi/package tidak diganti).

## 5. Yang belum ada (kalau suatu saat dibutuhkan)
- **Sinkronisasi data antar-tim** (semua orang lihat hasil opname yang
  sama secara real-time) — perlu Firebase, panduannya sudah ada di
  `SHARED_DATA_GUIDE.md`, sengaja belum dipasang supaya build tetap
  simpel dulu.
- Menu "Stock Op 7" dan "Tool Borrowing" dari mockup awal — sengaja
  dihapus sebelumnya, tinggal minta kalau mau ditambahkan lagi.
- Login dengan akun sungguhan (saat ini semua username/password
  diterima, murni tampilan).

## 6. Peta dokumen di dalam project ini
| File | Isi |
|---|---|
| `README.md` | Ringkasan + link ke semua panduan |
| `RINGKASAN_PROJECT.md` | Penjelasan tiap file kode (untuk referensi teknis) |
| `TUTORIAL_LENGKAP.md` | Dokumen ini — rekap dari awal |
| `CARA_ANDROID_STUDIO.md` | Build lewat Android Studio (kalau nanti bisa pakai) |
| `CARA_DAPAT_APK.md` | Build lewat GitHub Actions (yang sudah berhasil dipakai) |
| `CARA_UPDATE_DATA_MASTER.md` | Cara mengganti/update data tool ke depannya |
| `SHARED_DATA_GUIDE.md` | Panduan opsional: sinkronisasi data antar-tim via Firebase |
