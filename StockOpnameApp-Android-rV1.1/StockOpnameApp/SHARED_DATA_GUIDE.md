# Mengaktifkan Data Bersama (Shared) untuk Seluruh Tim

Secara default, hasil stock opname tersimpan **lokal per HP** (seperti
localStorage di browser) — cocok untuk uji coba sendiri, tapi belum bisa
dilihat tim lain. Untuk membuat hasil opname **real-time terlihat oleh
semua anggota tim**, gunakan **Firebase Firestore** (gratis untuk skala
kecil-menengah, tidak perlu server sendiri).

Ini sengaja **tidak saya pasang otomatis** ke project, supaya versi dasar
(lokal) tetap pasti bisa di-build tanpa syarat tambahan. Ikuti langkah di
bawah kalau siap mengaktifkannya.

## 1. Buat project Firebase
1. Buka https://console.firebase.google.com → **Add project**.
2. Beri nama bebas, misalnya "Stock Opname GMF".
3. Di dalam project, klik **Add app > Android**.
4. Isi **Package name**: `com.gmf.stockopname` (harus persis sama).
5. Download file **`google-services.json`** yang diberikan, lalu taruh di:
   `StockOpnameApp/app/google-services.json`

## 2. Aktifkan Firestore
1. Di console Firebase: **Build > Firestore Database > Create database**.
2. Pilih lokasi server (mis. `asia-southeast2` / Jakarta terdekat).
3. Mode: mulai dengan **test mode** untuk uji coba tim internal (30 hari),
   lalu ganti ke rules berikut sebelum dipakai jangka panjang:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /opname_results/{docId} {
      allow read, write: if request.auth != null;
    }
  }
}
```

   (Rules di atas mensyaratkan login — lihat langkah 4 kalau mau pakai ini.)

## 3. Tambahkan dependency di project
Di `build.gradle.kts` (root):
```kotlin
plugins {
    id("com.android.application") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}
```

Di `app/build.gradle.kts`, tambahkan di baris paling atas (setelah plugin
lain) dan di dependencies:
```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}
// ...
dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-firestore-ktx")
    // ...dependency lain yang sudah ada
}
```

## 4. (Opsional tapi disarankan) Login tim dengan akun Google
Supaya rules di atas (`request.auth != null`) bisa jalan dan setiap
perubahan tercatat "siapa yang opname", tambahkan Firebase Authentication:
- Console Firebase → **Build > Authentication > Sign-in method** → aktifkan
  **Google**.
- Tambahkan dependency `com.google.firebase:firebase-auth-ktx` dan ganti
  layar Login (`LoginScreen.kt`) untuk memakai Google Sign-In alih-alih
  username/password bebas.

Kalau ingin tetap sederhana dulu (tanpa auth), pakai rules test mode dan
lewati langkah ini.

## 5. Ganti OpnameStore agar sinkron ke Firestore
Struktur dokumen yang disarankan di koleksi `opname_results`:

```
opname_results/{store}_{location}
{
  "results": ["OK", "PENDING", "DISCREPANCY", ...],
  "finishedAt": 1699999999000,      // null kalau belum selesai
  "updatedBy": "firdaus",
  "updatedAt": 1699999999000
}
```

Ganti `OpnameStore.kt` (atau tambahkan kelas baru `FirestoreOpnameStore`)
agar:
- **setResult(...)** menulis ke `Firestore.getInstance().collection("opname_results").document("$store_$location")` (pakai `.set(data, SetOptions.merge())`), selain menyimpan ke SharedPreferences lokal sebagai cache offline.
- **AppViewModel** memasang `addSnapshotListener` pada dokumen yang sedang
  dibuka (`locationDetail`, `scan`), supaya kalau rekan kerja lain baru saja
  menandai tool sebagai OK/Discrepancy, layar Anda ikut update otomatis
  tanpa perlu refresh manual.
- Simpan cache lokal tetap dipakai sebagai fallback saat tidak ada koneksi
  internet (Firestore SDK sebenarnya sudah punya offline persistence
  bawaan — tinggal panggil
  `FirebaseFirestore.getInstance().firestoreSettings = FirestoreSettings.Builder().setPersistenceEnabled(true).build()`
  sekali di `Application.onCreate()`).

Kalau Anda mau, saya bisa langsung tuliskan kode `FirestoreOpnameStore.kt`
lengkap begitu `google-services.json` sudah didaftarkan — beri tahu saja
setelah project Firebase-nya dibuat.

## Master Data (referensi tool) — tetap lokal, dan itu sudah benar
`masterdata.json` (5.424 tool) **sengaja tidak** disinkronkan ke Firestore —
datanya statis/referensi yang sama untuk semua orang, jadi cukup dibundle
di dalam aplikasi. Yang perlu dibagikan real-time hanyalah **hasil opname**
(OK/Discrepancy per tool), bukan data master toolnya.
