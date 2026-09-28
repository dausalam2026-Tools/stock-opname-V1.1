# Cara Mendapatkan File .apk Tanpa Android Studio (Detail Lengkap)

Ikuti urut dari atas. Semua bisa lewat browser + (opsional) satu aplikasi
ringan (GitHub Desktop) — bukan Android Studio, tidak butuh Android SDK.

---

## BAGIAN 1 — Siapkan Akun & Repository GitHub

### Langkah 1: Buat akun GitHub (lewati kalau sudah punya)
1. Buka **https://github.com** di browser HP atau komputer.
2. Klik **Sign up** (pojok kanan atas).
3. Isi email → klik **Continue**.
4. Buat password → klik **Continue**.
5. Buat username (bebas, unik) → klik **Continue**.
6. Selesaikan verifikasi puzzle/captcha kalau muncul.
7. Cek email Anda, buka email dari GitHub, masukkan kode verifikasi yang
   diminta.
8. Akun jadi, Anda otomatis login.

### Langkah 2: Buat repository baru
1. Setelah login, klik ikon **"+"** di pojok kanan atas halaman.
2. Pilih **"New repository"**.
3. Di kolom **Repository name**, ketik: `stock-opname-app` (boleh nama
   lain, bebas, tanpa spasi).
4. Biarkan pilihan **Private** dipilih (supaya tidak publik — disarankan,
   tapi Public juga tidak masalah untuk project ini).
5. **PENTING**: jangan centang kotak **"Add a README file"** — biarkan
   semua opsi di bagian bawah kosong/default.
6. Klik tombol hijau **"Create repository"** di bagian bawah.
7. Anda akan diarahkan ke halaman repo kosong — biarkan tab ini terbuka,
   nanti dipakai lagi di Langkah 6.

---

## BAGIAN 2 — Masukkan File Project ke Repository

Pilih **salah satu** cara di bawah (Cara A lebih gampang & lebih jarang
gagal untuk project sebanyak ini).

### Cara A (disarankan): pakai GitHub Desktop
1. Buka **https://desktop.github.com** di komputer, klik tombol download
   sesuai sistem operasi Anda (Windows/Mac), install seperti aplikasi
   biasa (next-next-finish).
2. Buka aplikasi **GitHub Desktop** yang baru terinstall.
3. Klik **"Sign in to GitHub.com"** → browser akan terbuka → klik
   **Authorize** → kembali ke aplikasi GitHub Desktop, sekarang sudah
   login.
4. Di menu atas, klik **File > Add local repository...**
5. Klik tombol **"Choose..."**, cari dan pilih folder **`StockOpnameApp`**
   (folder yang muncul setelah Anda extract file zip yang saya berikan —
   pastikan pilih folder yang isinya ada `app`, `gradle`, `README.md`,
   dst, bukan folder zip-nya).
6. Kalau muncul kotak pesan "This directory does not appear to be a Git
   repository. Would you like to create a repository here instead?" →
   klik **"create a repository"**.
7. Akan muncul form "Create a Repository" — biarkan default, klik tombol
   **"Create Repository"** di kanan bawah.
8. Sekarang folder project sudah "dikenali" GitHub Desktop. Di kanan atas
   aplikasi, klik tombol biru **"Publish repository"**.
9. Di kotak yang muncul: pastikan **Name** sama dengan nama repo di
   Langkah 2 (`stock-opname-app`). Kalau ingin repo tetap privat, centang
   **"Keep this code private"**.
10. Klik **"Publish Repository"**. Tunggu sampai proses upload selesai
    (ada progress bar kecil, tunggu sampai hilang/selesai) — ini bisa
    makan waktu 1-3 menit tergantung koneksi internet.

### Cara B (tanpa install apa pun): upload lewat browser
1. Kembali ke tab browser repo kosong dari Langkah 2 bagian 1.
2. Klik link **"uploading an existing file"** di tengah halaman.
3. Di komputer, buka folder hasil extract zip **`StockOpnameApp`**.
4. Blok/select SEMUA isi di dalam folder tersebut (Ctrl+A di
   Windows/Cmd+A di Mac) — termasuk folder `.github` yang tersembunyi
   (di Windows aktifkan dulu "Show hidden files" di File Explorer kalau
   perlu, atau lihat catatan di bawah).
5. Drag seluruh yang terpilih itu ke area kotak putus-putus di halaman
   GitHub ("Drag files here...").
6. Tunggu semua file selesai ter-upload (progress terlihat di daftar).
7. Scroll ke bawah, klik tombol hijau **"Commit changes"**.

   > **Catatan penting Cara B**: folder `.github` (berisi file
   > `workflows/build-apk.yml`) kadang tidak ikut ter-drag kalau file
   > manager Anda menyembunyikannya. Kalau setelah upload Anda tidak
   > melihat folder `.github` muncul di repo, ulangi: klik **"Add file >
   > Upload files"** lagi, lalu kali ini masuk manual ke dalam folder
   > `.github/workflows/` di komputer, dan upload file `build-apk.yml`
   > satu-satu ke path yang sama di GitHub (GitHub akan otomatis membuat
   > foldernya). Ini titik yang paling sering terlewat di Cara B — kalau
   > ragu, pakai **Cara A** saja.

---

## BAGIAN 3 — Tunggu & Download Hasil Build

### Langkah 3: Buka tab Actions
1. Di halaman repo GitHub Anda (github.com/USERNAME/stock-opname-app),
   klik tab **"Actions"** di baris menu atas (sejajar dengan "Code",
   "Issues", "Pull requests").
2. Kalau ini kunjungan pertama ke tab Actions, mungkin muncul tombol
   hijau **"I understand my workflows, go ahead and enable them"** —
   klik itu dulu.

### Langkah 4: Tunggu proses build
1. Akan muncul satu baris berjudul sama seperti pesan commit Anda
   (mis. "Initial commit"), dengan ikon bulat kuning berputar 🟡 di
   sebelah kiri — artinya sedang di-build.
2. Proses ini memakan waktu **kira-kira 3–6 menit**. Anda bisa
   meninggalkan tab ini dan cek lagi nanti, atau klik tombol refresh
   browser sesekali.
3. Kalau ikon berubah jadi **centang hijau ✅** → berhasil, lanjut ke
   Langkah 5.
4. Kalau ikon berubah jadi **silang merah ❌** → build gagal. Klik baris
   itu, lalu klik job "build" di dalamnya untuk melihat log merah/error.
   **Copy pesan errornya dan kirimkan ke saya** — saya bantu perbaiki
   kodenya.

### Langkah 5: Download APK
1. Klik baris build yang sudah ✅ hijau tadi.
2. Scroll ke bagian paling bawah halaman, cari bagian berjudul
   **"Artifacts"**.
3. Klik **"stock-opname-debug-apk"** — sebuah file `.zip` akan
   terdownload ke komputer Anda (ini otomatis dari GitHub, isinya file
   `.apk`, bukan aplikasi zip yang berbahaya).
4. Extract file zip tersebut, akan ada satu file: **`app-debug.apk`**.

---

## BAGIAN 4 — Pasang ke HP

### Langkah 6: Pindahkan APK ke HP
Pilih cara termudah untuk Anda:
- **Google Drive**: upload `app-debug.apk` ke Drive dari komputer, lalu
  buka Google Drive di HP, download file itu ke HP.
- **Email**: kirim email ke diri sendiri dengan `app-debug.apk` sebagai
  lampiran, buka email itu di HP, download lampirannya.
- **Kabel USB**: sambungkan HP ke komputer, copy `app-debug.apk` langsung
  ke folder **Download** di penyimpanan HP.

### Langkah 7: Install
1. Di HP, buka aplikasi **File Manager** (atau buka notifikasi download
   kalau baru saja mengunduh lewat Drive/email).
2. Cari dan tap file **`app-debug.apk`**.
3. Kalau muncul peringatan *"For your security, your phone is blocked
   from installing unknown apps from this source"*:
   - Tap **"Settings"** pada peringatan tersebut.
   - Aktifkan toggle **"Allow from this source"**.
   - Tekan tombol back (‹) untuk kembali.
   - Tap file `.apk` itu lagi.
4. Layar detail aplikasi "Stock Opname" akan muncul → tap **"Install"**.
5. Tunggu beberapa detik → tap **"Open"** untuk langsung membuka
   aplikasinya, atau cari ikon "Stock Opname" di home screen HP.

Selesai — aplikasi Android asli, offline, tanpa Play Store.

---

## Kalau Ingin Update Aplikasi Nanti
Setiap kali saya kirimkan perubahan kode baru:
1. Ganti/tambahkan file yang berubah di folder `StockOpnameApp` di
   komputer Anda.
2. Buka **GitHub Desktop** → aplikasi akan otomatis mendeteksi
   perubahan → klik **"Commit to main"** (isi pesan bebas) → klik
   **"Push origin"**.
3. Ulangi **Langkah 3–7** di atas untuk dapat APK versi terbaru.
