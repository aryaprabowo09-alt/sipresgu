# SiPresGu
**Sistem Presensi Guru Berbasis GPS dengan Validasi Radius**

---

## Fitur Utama

| Modul | Fitur |
|---|---|
| **Autentikasi** | Login NIP + Password, session timeout, CSRF protection |
| **Admin — Pengguna** | CRUD guru, reset password, upload foto profil |
| **Admin — Sekolah** | Atur koordinat via peta interaktif Leaflet, slider radius |
| **Admin — Presensi** | Lihat semua data, input manual, edit, hapus |
| **Admin — Laporan** | Rekap bulanan per guru, progress bar kehadiran, export CSV |
| **Admin — Laporan (Export PDF Detail)** | Export PDF per guru dengan detail waktu masuk, pulang, dan status kehadiran |
| **Admin — Pengaturan Aplikasi** | Unggah logo aplikasi agar muncul di sidebar/brand |
| **Guru — Dashboard** | Status hari ini, rekap bulan, riwayat 7 hari |
| **Guru — Presensi GPS** | Peta real-time, validasi radius server-side, notifikasi SweetAlert |
| **Guru — Izin/Sakit** | Form modal dengan upload bukti |
| **Guru — Riwayat** | Filter bulan/tahun, tabel rekap detail |
| **Guru — Profil** | Edit nama, foto, ganti password |

---

## Persyaratan

- **PHP** 8.0 ke atas
- **MySQL** 5.7 / MariaDB 10.3 ke atas
- **Web server**: Apache / Nginx (XAMPP / Laragon direkomendasikan)
- Browser modern dengan dukungan **GPS/Geolocation API**
- Koneksi internet (untuk CDN Bootstrap, Leaflet, Chart.js)

---

## Cara Instalasi

### 1. Tempatkan di web server

```
# XAMPP
C:\xampp\htdocs\e-presensi-guru\

# Laragon
C:\laragon\www\e-presensi-guru\
```

### 2. Buat database

Buka **phpMyAdmin** → pilih **Import** → upload file `database.sql`

Atau jalankan via MySQL CLI:
```bash
mysql -u root -p < database.sql
```

### 3. Konfigurasi

Edit file `config.php`:

```php
define('DB_HOST', 'localhost');
define('DB_NAME', 'db_presensi');
define('DB_USER', 'root');      // sesuaikan
define('DB_PASS', '');          // sesuaikan
define('BASE_URL', 'http://localhost/e-presensi-guru');
```

### 4. Buat folder uploads

```bash
mkdir uploads\bukti
```
Pastikan folder `uploads/bukti/` dapat ditulis (writable).

### 5. Akses aplikasi

Buka browser: `http://localhost/e-presensi-guru`

---

## Akun Default

| Role | NIP | Password |
|---|---|---|
| Admin | `000000000000000001` | `password` |
| Guru 1 | `197501012005011001` | `password` |
| Guru 2 | `198003152006012002` | `password` |

> **Ganti password segera setelah login pertama!**

---

## Alur Presensi Guru

```
Login → Dashboard → Presensi GPS
  ↓
Browser minta izin GPS
  ↓
Ambil koordinat (navigator.geolocation)
  ↓
Klik "Presensi Masuk"
  ↓
AJAX POST → api/presensi.php
  ↓
Server hitung jarak (Haversine Formula)
  ↓
Jarak ≤ radius? → BERHASIL ✅
Jarak > radius? → GAGAL ❌ + notifikasi
```

---

## Struktur Direktori

```
e-presensi-guru/
├── config.php              ← Konfigurasi DB & app
├── database.sql            ← Script setup database
├── index.php               ← Redirect ke login/dashboard
├── api/
│   └── presensi.php        ← AJAX endpoint GPS presensi
├── auth/
│   ├── login.php
│   └── logout.php
├── admin/
│   ├── dashboard.php
│   ├── users/              ← CRUD pengguna
│   ├── sekolah/            ← Pengaturan koordinat & radius
│   ├── presensi/           ← Manajemen data presensi
│   └── laporan/            ← Rekap & export CSV
├── guru/
│   ├── dashboard.php
│   ├── presensi/           ← Halaman GPS presensi
│   ├── riwayat.php
│   └── profil.php
├── includes/
│   ├── db.php              ← Koneksi PDO
│   ├── functions.php       ← Helper (Haversine, flash, dll)
│   ├── auth_check.php
│   └── admin_check.php
├── layouts/
│   ├── header.php          ← Sidebar + topbar
│   └── footer.php          ← Scripts closing
├── assets/
│   ├── css/style.css
│   └── js/app.js
└── uploads/
    └── bukti/              ← Upload bukti izin/sakit & foto
```

---

## Keamanan

- **SQL Injection**: PDO Prepared Statements
- **XSS**: `htmlspecialchars()` pada semua output
- **CSRF**: Token per-session pada semua form POST
- **Password**: `password_hash()` + `password_verify()` (Bcrypt)
- **GPS Validation**: Validasi jarak dilakukan **server-side** (tidak bisa dimanipulasi client)
- **Session**: Timeout 2 jam, regenerasi ID berkala setiap 30 menit
- **File Upload**: Validasi MIME type via `finfo`, batas ukuran 2MB

---

## Teknologi

| Komponen | Library/Versi |
|---|---|
| Backend | PHP 8+ Native + PDO |
| Database | MySQL / MariaDB |
| CSS Framework | Bootstrap 5.3 |
| Maps | Leaflet.js 1.9.4 + OpenStreetMap |
| Charts | Chart.js 4.4 |
| Alerts | SweetAlert2 11 |
| Icons | Bootstrap Icons 1.11 |
| Font | Plus Jakarta Sans |
