# 📱 Panduan Lengkap: Build & GitHub Release APK Android SiPresGu

Panduan ini berisi petunjuk lengkap langkah demi langkah untuk mengunggah proyek SiPresGu ke **GitHub Web**, menjalankan proses otomatisasi build APK Android, serta mengunduh dan menginstal aplikasi APK SiPresGu di smartphone Android.

---

## 📋 Ringkasan Komponen yang Telah Dibuat

1. **Aplikasi Android Native (Folder `android/`)**:
   - Berbasis WebView Modern teroptimasi untuk SiPresGu.
   - Mendukung penuh **GPS Geolocation** (validasi radius presensi guru).
   - Mendukung penuh **Kamera & Selfie Presensi** (HTML5 WebRTC Camera).
   - Mendukung **Upload Berkas** (Surat dokter/izin dari galeri/file manager).
   - Fitur **Pengaturan URL Server Dinamis** (bisa ganti URL hosting langsung di aplikasi tanpa build ulang).
   - Fitur **Swipe to Refresh** (tarik ke bawah untuk memuat ulang).
   - Layar **Offline / Error Handling** dengan tombol "Coba Lagi".

2. **Ikon & Logo Aplikasi Resmi (Folder `android/app/src/main/res/mipmap-*/`)**:
   - Menggunakan logo resmi Kementerian Agama resolusi tinggi untuk semua ukuran layar HP (`mdpi`, `hdpi`, `xhdpi`, `xxhdpi`, `xxxhdpi`).

3. **GitHub Actions CI/CD (Folder `.github/workflows/build-and-release.yml`)**:
   - Otomatis melakukan compile aplikasi Android menjadi file `.apk` di cloud GitHub (tanpa perlu install Android Studio di laptop/PC Anda).
   - Otomatis melampirkan file APK ke halaman **GitHub Releases**.

---

## 🚀 Langkah-Langkah di GitHub Web

### LANGKAH 1: Buat Repositori di GitHub Web

1. Buka browser dan login ke akun Anda di [https://github.com](https://github.com).
2. Di pojok kanan atas, klik tanda **`+`** lalu pilih **New repository**.
3. Isi kolom:
   - **Repository name**: `sipresgu` (atau nama lain yang Anda inginkan).
   - **Description**: `Aplikasi Presensi Guru Berbasis GPS & Android APK`.
   - **Visibility**: Pilih **Public** (agar GitHub Actions gratis tanpa batas) atau **Private**.
   - *Jangan centang "Add a README file" karena kita sudah memiliki file proyek lokal.*
4. Klik tombol hijau **Create repository**.

---

### LANGKAH 2: Upload Proyek ke GitHub

Pilih salah satu cara berikut yang paling mudah bagi Anda:

#### Pilihan A: Lewat Web GitHub Langsung (Tanpa Install Apapun)
1. Setelah repositori baru dibuat di GitHub, klik tautan bertuliskan **"uploading an existing file"** (atau tombol **Add file** -> **Upload files**).
2. Buka folder `c:\Users\user\Downloads\sipresgu new` di File Explorer laptop Anda.
3. Seret (drag & drop) seluruh folder dan file (terutama folder `.github`, `android`, dan file-file utama) ke area kotak upload di browser GitHub.
4. Di bagian bawah kotak upload (Commit changes), tulis pesan: `Initial commit SiPresGu Android`.
5. Klik tombol hijau **Commit changes**.

> *Catatan:* Jika file terlalu banyak untuk sekali upload di web, Anda bisa mengupload folder utama terlebih dahulu, atau gunakan **Pilihan B** (sangat praktis).

#### Pilihan B: Lewat Aplikasi "GitHub Desktop" (Paling Praktis & Beres)
1. Unduh dan pasang aplikasi gratis [GitHub Desktop](https://desktop.github.com/).
2. Login dengan akun GitHub Anda.
3. Klik menu **File** -> **Add Local Repository...**.
4. Pilih folder: `C:\Users\user\Downloads\sipresgu new`.
5. Jika muncul pesan *"This directory does not appear to be a Git repository"*, klik tautan **create a repository here**.
6. Klik **Add Repository**.
7. Klik tombol biru **Publish repository** di bagian atas untuk langsung mengunggah semua file ke akun GitHub Anda.

#### Pilihan C: Lewat Git Terminal (Jika Memasang Git for Windows)
Jika Anda menginstal [Git for Windows](https://git-scm.com/):
```powershell
cd "c:\Users\user\Downloads\sipresgu new"
git init
git add .
git commit -m "Initial commit: SiPresGu Web dan Android App Wrapper"
git branch -M main
git remote add origin https://github.com/USERNAME/REPO_NAME.git
git push -u origin main
```

> **Tips:** Jika diminta login GitHub di terminal, gunakan browser login atau masukkan GitHub Personal Access Token (PAT).

---

### LANGKAH 3: Status URL Server SiPresGu

Aplikasi Android sudah secara default dikonfigurasi langsung ke domain hosting Anda:
```
https://sipresgu.web.id/
```
Sehingga saat guru menginstal dan membuka aplikasi di HP, aplikasi akan langsung memuat website `https://sipresgu.web.id/` secara otomatis tanpa perlu konfigurasi apa pun.

> **Fleksibilitas Tambahan:** Jika sewaktu-waktu server atau domain berpindah, Anda atau guru tetap dapat mengubah alamat server langsung dari aplikasi HP melalui menu titik tiga (⋮) di pojok kanan atas -> **Atur URL Server**.

---

### LANGKAH 4: Build APK Otomatis via GitHub Web (Tab Actions)

Anda tidak perlu menginstal Android Studio di laptop. GitHub yang akan merakit APK untuk Anda:

1. Buka repositori Anda di browser: `https://github.com/USERNAME/REPO_NAME`.
2. Klik tab **Actions** di menu atas repositori.
3. Di bilah sisi kiri (Actions), klik alur kerja **Build & Release SiPresGu APK**.
4. Di sebelah kanan akan muncul kotak menu **Run workflow**:
   - Klik tombol **Run workflow**.
   - Masukkan nomor versi: misalnya `v1.0.0`.
   - Klik tombol hijau **Run workflow**.
5. Tunggu proses build berjalan (sekitar 2–4 menit). Indikator kuning akan berubah menjadi **Centang Hijau** (Success).

---

### LANGKAH 5: Unduh APK dari Tab GitHub Releases

Setelah proses build selesai, file APK siap diunduh:

1. Di halaman utama repositori GitHub Anda, perhatikan kolom sebelah kanan bertuliskan **Releases**.
2. Klik nama rilis terbaru (misal: **Release v1.0.0**).
3. Di bagian **Assets** (di bawah deskripsi rilis), Anda akan melihat file:
   - 📦 **`SiPresGu-v1.0.0.apk`**
4. Klik file tersebut untuk mengunduh APK langsung ke laptop atau HP Anda!
5. Anda juga bisa membagikan link halaman Releases tersebut kepada guru-guru untuk diunduh.

---

### LANGKAH 6: Cara Install APK di HP Android

1. Kirim file `SiPresGu-v1.0.0.apk` ke HP Android (via WhatsApp, Google Drive, Telegram, atau download langsung dari browser HP via link GitHub Releases).
2. Ketuk file APK untuk memasangnya.
3. Jika muncul dialog keamanan:
   - **Google Play Protect**: Pilih **Tetap Install (Install Anyway)**.
   - **Install Unknown Apps**: Aktifkan toggle **Izinkan dari sumber ini (Allow from this source)**.
4. Klik **Install / Pasang** dan tunggu hingga selesai.
5. Saat aplikasi pertama kali dibuka:
   - Pilih **Izinkan / Allow** saat diminta izin **Lokasi (GPS)** -> Pilih *"Saat aplikasi digunakan (While using the app)"*.
   - Pilih **Izinkan / Allow** saat diminta izin **Kamera** (untuk foto selfie presensi).
6. Guru kini dapat melakukan presensi dengan validasi GPS dan foto selfie langsung dari aplikasi Android!

---

## 🛠️ Pengaturan Hak Akses Token GitHub (Jika Diperlukan)

Jika workflow GitHub Actions gagal pada langkah rilis dengan pesan *"Resource not accessible by integration"*:
1. Di repositori GitHub, buka menu **Settings** (tab paling kanan).
2. Di menu kiri, pilih **Actions** -> **General**.
3. Gulir ke bawah ke bagian **Workflow permissions**.
4. Pilih opsi **Read and write permissions**.
5. Centang **Allow GitHub Actions to approve pull request reviews** jika ada.
6. Klik **Save**.
7. Jalankan ulang workflow di tab **Actions**.
