# 📱 Panduan Pemasangan Aplikasi SiPresGu (Android & iOS)

Aplikasi **SiPresGu** kini mendukung dua sistem operasi utama: **Android** dan **iOS (Apple iPhone / iPad)** dengan logo sidik jari bergradasi tosca modern.

---

## 🍏 Bagian 1: Pengguna Apple iOS (iPhone & iPad)

Di perangkat Apple (iOS), terdapat dua metode resmi:

### Metode 1: Pasang Langsung via Safari (Rekomendasi - Paling Cepat & Praktis)
Teknologi **PWA (Progressive Web App)** memungkinkan iPhone memasang SiPresGu menjadi aplikasi mandiri (*standalone*) dalam waktu 5 detik tanpa perlu unduh file rumit:

1. Buka browser **Safari** di iPhone Anda.
2. Kunjungi website: **`https://sipresgu.web.id/`**
3. Di bagian bawah layar Safari, ketuk tombol **Share** (ikon kotak dengan tanda panah ke atas: ⎋ / ⬆️).
4. Gulir menu ke bawah dan pilih: 👉 **"Add to Home Screen"** (*Tambahkan ke Layar Utama*).
5. Nama aplikasi akan otomatis terisi **SiPresGu** dengan logo sidik jari baru. Ketuk **"Add"** (*Tambah*) di pojok kanan atas.
6. Selesai! Ikon aplikasi SiPresGu akan langsung muncul di halaman utama iPhone Anda.
7. Saat dibuka, aplikasi akan berjalan **Full-Screen** tanpa batas address bar browser, lengkap dengan akses GPS dan kamera selfie presensi.

---

### Metode 2: Proyek Xcode Native iOS (Folder `ios/`)
Bagi pengembang yang memiliki laptop Mac dan ingin mendistribusikan via Xcode / TestFlight / Apple Developer:
1. Proyek native iOS tersedia di folder: `ios/SiPresGu.xcodeproj`.
2. Buka proyek tersebut di aplikasi **Xcode** pada komputer Mac Anda.
3. Konfigurasi Signing & Capabilities dengan Apple ID Anda.
4. Sambungkan iPhone via kabel USB dan klik tombol **Run ▶️** (atau buat arsip IPA untuk TestFlight).

---

## 🤖 Bagian 2: Pengguna Smartphone Android

### Langkah Pasang APK di Android:
1. Unduh file rilis **`SiPresGu-v1.0.2.apk`** dari tab **Releases** repositori GitHub Anda.
2. Buka file APK di HP Android Anda.
3. Jika muncul peringatan keamanan:
   - Pilih **Izinkan dari sumber ini (Allow from this source)**.
   - Pada Google Play Protect, pilih **Tetap Install (Install Anyway)**.
4. Buka aplikasi SiPresGu yang baru terpasang (ikon sidik jari tosca).
5. Izinkan akses **Lokasi (GPS)** dan **Kamera** saat pertama kali diminta.
6. Aplikasi akan langsung memuat `https://sipresgu.web.id/` dengan tampilan layar penuh (*edge-to-edge*) modern.

---

## 📋 Ringkasan File Baru yang Ditambahkan:
- 🍏 **iOS Xcode Project**: Folder `ios/` (`SiPresGu.xcodeproj`, `ViewController.swift`, `Info.plist`, `AppIcon.appiconset`).
- 🌐 **PWA Engine**: `manifest.json`, `sw.js`, dan `assets/img/pwa/`.
- 🤖 **Android Native Wrapper**: Folder `android/` teroptimasi full-screen dengan logo baru.
