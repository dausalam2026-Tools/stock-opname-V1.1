# Cara Build Lewat Android Studio (Step by Step)

Butuh komputer Windows atau Mac dengan koneksi internet yang lumayan
stabil (Android Studio + komponen pertamanya total unduhan sekitar
2-4 GB). Proses instalasi pertama kali makan waktu 20-40 menit
tergantung koneksi.

---

## BAGIAN 1 — Install Android Studio

### Langkah 1: Download
1. Buka **https://developer.android.com/studio** di browser.
2. Klik tombol hijau/biru besar **"Download Android Studio"**.
3. Centang kotak persetujuan Terms, klik **"Download Android Studio for
   Windows/Mac"** (otomatis sesuai OS Anda).
4. Tunggu file `.exe` (Windows) atau `.dmg` (Mac) selesai terdownload
   (sekitar 1 GB).

### Langkah 2: Install
**Windows:**
1. Buka file `.exe` yang terdownload.
2. Klik **Next** terus sampai muncul tombol **Install**, klik itu.
3. Tunggu proses install selesai, klik **Next** lalu **Finish**.

**Mac:**
1. Buka file `.dmg` yang terdownload.
2. Drag ikon **Android Studio** ke folder **Applications**.
3. Buka **Applications > Android Studio** (klik kanan > Open kalau ada
   peringatan "unidentified developer", lalu klik **Open** lagi).

### Langkah 3: Setup Wizard (hanya muncul sekali)
1. Saat pertama dibuka, muncul **"Android Studio Setup Wizard"** → klik
   **Next**.
2. Pilih **"Standard"** install type → klik **Next**.
3. Pilih tema (UI theme) bebas → klik **Next**.
4. Halaman **"Verify Settings"** menampilkan daftar komponen yang akan
   diunduh (Android SDK, SDK Platform, emulator, dst) → klik **Next**.
5. Baca & klik **Accept** pada setiap lisensi yang muncul di panel kiri
   (biasanya ada 2-3 lisensi) → tombol **Finish** akan aktif → klik
   **Finish**.
6. Tunggu proses download & install komponen SDK (bar progress) —
   ini bagian yang paling lama, 10-30 menit.
7. Setelah selesai, klik **Finish** — akan muncul layar **"Welcome to
   Android Studio"**.

---

## BAGIAN 2 — Buka Project Stock Opname

### Langkah 4: Extract & buka project
1. Extract file zip **`StockOpnameApp-Android.zip`** yang saya berikan,
   sampai dapat folder **`StockOpnameApp`** (isinya ada folder `app`,
   `gradle`, file `README.md`, dll — kalau hasil extract malah ada
   folder `StockOpnameApp` di dalam `StockOpnameApp` lagi, masuk satu
   level supaya tepat di folder yang berisi `app`).
2. Di layar "Welcome to Android Studio", klik **"Open"** (atau
   **"Open an Existing Project"**).
3. Cari dan pilih folder **`StockOpnameApp`** tadi → klik **"Open"**
   (di Mac: **Choose**).
4. Kalau muncul dialog **"Trust and Open Project?"** → klik **"Trust
   Project"**.

### Langkah 5: Tunggu Gradle Sync (WAJIB ditunggu sampai selesai)
1. Di pojok kanan bawah/bar bawah jendela, akan muncul progress bar
   bertuliskan **"Gradle Sync"** atau **"Downloading..."** — ini proses
   Android Studio mengunduh semua library yang dipakai app (Compose,
   CameraX, ML Kit, dll).
2. **JANGAN klik tombol Run dulu sebelum ini selesai.** Proses ini bisa
   makan waktu 5-15 menit tergantung koneksi internet (unduhan pertama
   kali paling lama; setelah itu jadi cepat).
3. Kalau muncul notifikasi kuning/merah di bagian atas file dengan
   tombol **"Sync Now"** — klik itu.
4. Sync selesai kalau tidak ada lagi progress bar berjalan dan tidak
   ada garis merah error besar di bagian bawah.

**Kalau sync gagal**, pesan error biasanya muncul di tab **"Build"**
(bagian bawah jendela). Kirimkan screenshot/copy pesan errornya ke saya
— beberapa penyebab umum:
- *"SDK platform 34 not found"* → klik link **"Install missing SDK
  package(s)"** yang muncul di pesan error tsb.
- *"Failed to resolve..."* / timeout → koneksi internet terputus saat
  sync, coba klik **File > Sync Project with Gradle Files** lagi.

---

## BAGIAN 3 — Jalankan / Build APK

Ada dua tujuan berbeda — pilih sesuai kebutuhan:

### Opsi A: Langsung coba di HP Anda (paling cepat untuk testing)
1. Di HP Android: buka **Settings > About phone**, tap **"Build
   number"** sebanyak 7x berturut-turut sampai muncul pesan "You are
   now a developer!".
2. Kembali ke **Settings**, cari menu baru **"Developer options"** →
   masuk, aktifkan toggle **"USB debugging"**.
3. Sambungkan HP ke komputer pakai kabel USB. Di HP akan muncul
   popup **"Allow USB debugging?"** → centang **"Always allow"** →
   tap **OK**.
4. Di Android Studio, lihat toolbar atas: ada dropdown nama device —
   pastikan nama HP Anda muncul di situ (kalau tidak muncul, cabut-pasang
   kabel USB, atau ganti kabel — sebagian kabel charger-only tidak
   mendukung transfer data).
5. Klik tombol **▶ (segitiga hijau, "Run")** di toolbar atas.
6. Tunggu proses build (bar progress di bawah) — aplikasi akan otomatis
   ter-install dan terbuka di HP Anda.

### Opsi B: Bikin file .apk untuk disimpan/dibagikan
1. Di menu atas Android Studio, klik **Build > Build Bundle(s) / APK(s)
   > Build APK(s)**.
2. Tunggu proses build (bar progress di kanan bawah bertuliskan
   "Gradle Build Running").
3. Setelah selesai, muncul notifikasi kecil di kanan bawah:
   **"APK(s) generated successfully"** dengan link **"locate"** —
   klik link itu untuk langsung membuka folder tempat file `.apk`
   berada (biasanya di
   `StockOpnameApp/app/build/outputs/apk/debug/app-debug.apk`).
4. File **`app-debug.apk`** itulah yang dipindahkan ke HP untuk
   di-install (lihat Bagian 4 di bawah) — bisa juga langsung dipakai
   untuk HP lain selain yang tersambung USB.

---

## BAGIAN 4 — Pasang APK ke HP (kalau pakai Opsi B)

1. Pindahkan `app-debug.apk` ke HP: lewat Google Drive, email ke diri
   sendiri, atau kabel USB ke folder Download.
2. Di HP, buka **File Manager**, cari file itu, tap.
3. Kalau muncul peringatan *"blocked from installing unknown apps"* →
   tap **Settings** pada peringatan → aktifkan **"Allow from this
   source"** → kembali → tap file `.apk` lagi.
4. Tap **Install** → tunggu → tap **Open**.

---

## Ringkasan super singkat
Buka project di Android Studio → tunggu Gradle Sync selesai →
**Build > Build Bundle(s)/APK(s) > Build APK(s)** → klik "locate" →
pindahkan `app-debug.apk` ke HP → install.

Kalau ada error di langkah mana pun, screenshot atau copy-paste pesan
errornya (biasanya di tab **Build** bagian bawah jendela Android
Studio) dan kirim ke saya — saya bantu diagnosa.
