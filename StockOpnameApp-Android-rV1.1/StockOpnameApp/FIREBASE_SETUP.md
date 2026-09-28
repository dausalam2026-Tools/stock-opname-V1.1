# Setup Firebase (Wajib, Sekali Saja) — Fitur Tambah Tool & Foto Bersama Tim

Fitur "+" di Master Data (tambah tool baru + upload foto) sekarang memakai
**Firebase** (Firestore, paket gratis) supaya data yang ditambahkan satu
orang langsung terlihat oleh semua HP yang pakai APK yang sama, secara
real-time — bukan cuma tersimpan di HP masing-masing.

App **tidak akan bisa di-build** sebelum langkah-langkah di bawah selesai,
karena butuh file konfigurasi (`google-services.json`) dari project Firebase
Anda sendiri.

---

## 1. Buat Project Firebase

1. Buka https://console.firebase.google.com
2. Klik **Add project** → beri nama bebas (mis. `stock-opname-gmf`) → ikuti
   langkah-langkahnya (Google Analytics boleh dimatikan, tidak wajib).

## 2. Daftarkan Aplikasi Android

1. Di dashboard project, klik ikon Android untuk "Add app".
2. **Android package name** harus diisi **persis**: `com.gmf.stockopname`
3. Nickname app: bebas.
4. **Download `google-services.json`** yang ditawarkan.
5. Pindahkan file tersebut ke folder **`app/`** di project Android Studio
   (sejajar dengan `app/build.gradle.kts`, BUKAN di dalam `app/src/`).
6. Lewati langkah-langkah selanjutnya di wizard (dependency sudah saya
   siapkan di `build.gradle.kts`), langsung klik "Next" sampai selesai.

## 3. Aktifkan Firestore Database

1. Di menu kiri Firebase Console: **Build > Firestore Database**.
2. Klik **Create database**.
3. Pilih lokasi server (mis. `asia-southeast2` / Jakarta, atau terdekat).
4. Pilih mode **Start in test mode** dulu (supaya cepat jalan) — nanti di
   Langkah 5 kita ganti rule-nya jadi permanen.

## 4. Foto: tidak perlu Cloud Storage (tetap gratis)

Foto tool disimpan langsung di Firestore (koleksi `tool_photos`), jadi Anda
**tidak perlu** mengaktifkan Cloud Storage dan **tidak perlu** paket Blaze /
kartu kredit. Cukup paket Spark (gratis). Foto otomatis diperkecil (maks. sekitar
600 KB) supaya muat di batas ukuran dokumen Firestore.

## 5. Atur Security Rules

Karena app ini tidak pakai login/akun (semua tim pakai app yang sama tanpa
autentikasi), rule dibuat terbuka **hanya untuk 2 koleksi ini** (data tool &
foto tool) — bukan untuk seluruh project Anda.

Menu **Firestore Database > tab Rules**, ganti isinya jadi:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /tools/{document=**} {
      allow read, write: if true;
    }
    match /tool_photos/{document=**} {
      allow read, write: if true;
    }
  }
}
```

Klik **Publish**.

> Catatan keamanan: rule di atas artinya siapa pun yang punya file APK ini
> bisa membaca/menulis data tool & foto. Wajar untuk app internal tim kecil,
> tapi **jangan** sebar APK-nya ke luar tim, dan sebaiknya repo GitHub dibuat
> private (file `google-services.json` ada di dalamnya).

## 6. Build & Coba

1. Sync Gradle di Android Studio (karena ada plugin `google-services` baru).
2. Build & run seperti biasa.
3. Coba tambah tool + foto dari satu HP/emulator, lalu buka Master Data di
   HP lain (harus login & terhubung internet) — tool barunya harus muncul
   otomatis dalam beberapa detik, tanpa perlu refresh manual.

---

Kalau ada error saat build terkait `google-services.json` tidak ditemukan,
pastikan file itu ada persis di `app/google-services.json` (bukan di
`app/src/main/`).
