<?php
// ============================================================
// Konfigurasi Aplikasi SiPresGu
// Sesuaikan DB_USER dan DB_PASS sesuai server Anda
// ============================================================

// Set Zona Waktu Indonesia Tengah (WITA)
date_default_timezone_set('Asia/Makassar');

define('DB_HOST', 'localhost');
define('DB_NAME', 'gzerqunn_db_presensi');
define('DB_USER', 'gzerqunn_dbuserpresensi');
define('DB_PASS', 'ARya100518@');

define('APP_NAME', 'SiPresGu');

// ── Dynamic BASE_URL ──────────────────────────────────────────
// Otomatis mendeteksi protocol, domain, dan subfolder
// Sehingga bisa diakses dari www.sipresgu.web.id, sipresgu.web.id,
// localhost, atau IP address manapun tanpa error.
$_protocol = 'https';
if (
    (!empty($_SERVER['HTTPS']) && $_SERVER['HTTPS'] !== 'off')
    || (!empty($_SERVER['HTTP_X_FORWARDED_PROTO']) && $_SERVER['HTTP_X_FORWARDED_PROTO'] === 'https')
    || (!empty($_SERVER['HTTP_X_FORWARDED_SSL']) && $_SERVER['HTTP_X_FORWARDED_SSL'] === 'on')
    || (isset($_SERVER['SERVER_PORT']) && (int)$_SERVER['SERVER_PORT'] === 443)
) {
    $_protocol = 'https';
} else {
    // Cek apakah ini localhost/development
    $host = $_SERVER['HTTP_HOST'] ?? 'localhost';
    if (
        strpos($host, 'localhost') !== false
        || strpos($host, '127.0.0.1') !== false
        || strpos($host, '192.168.') !== false
        || strpos($host, '10.') === 0
    ) {
        $_protocol = 'http';
    } else {
        // Production — default ke https
        $_protocol = 'https';
    }
}

// Deteksi host dan path
$_host = $_SERVER['HTTP_HOST'] ?? 'sipresgu.web.id';
// Hapus www. jika ada, agar konsisten
$_host = preg_replace('/^www\./i', '', $_host);

// Deteksi subfolder (jika app di-deploy di subfolder)
$_scriptDir = str_replace('\\', '/', dirname($_SERVER['SCRIPT_NAME'] ?? ''));
$_basePath = ($_scriptDir === '/' || $_scriptDir === '.') ? '' : rtrim($_scriptDir, '/');

// Hapus subfolder yang merupakan bagian internal app (admin, guru, api, dll)
$_internalDirs = ['admin', 'guru', 'api', 'auth', 'errors', 'includes', 'layouts', 'assets'];
foreach ($_internalDirs as $_dir) {
    if (preg_match('#/' . $_dir . '(/|$)#', $_basePath)) {
        $_basePath = preg_replace('#/' . $_dir . '(/.*)?$#', '', $_basePath);
        break;
    }
}

define('BASE_URL', $_protocol . '://' . $_host . $_basePath);

// Cleanup temp vars
unset($_protocol, $_host, $_scriptDir, $_basePath, $_internalDirs, $_dir);

define('UPLOAD_DIR', __DIR__ . DIRECTORY_SEPARATOR . 'uploads' . DIRECTORY_SEPARATOR . 'bukti' . DIRECTORY_SEPARATOR);
define('UPLOAD_URL', BASE_URL . '/uploads/bukti/');
define('SELFIE_DIR', __DIR__ . DIRECTORY_SEPARATOR . 'uploads' . DIRECTORY_SEPARATOR . 'selfie' . DIRECTORY_SEPARATOR);
define('SELFIE_URL', BASE_URL . '/uploads/selfie/');
define('LOGO_UPLOAD_DIR', __DIR__ . DIRECTORY_SEPARATOR . 'uploads' . DIRECTORY_SEPARATOR . 'logo' . DIRECTORY_SEPARATOR);
define('LOGO_UPLOAD_URL', BASE_URL . '/uploads/logo/');
define('DEFAULT_LOGO_MARKER', '__sipresgu_default__');
define('DEFAULT_LOGO_URL', BASE_URL . '/uploads/logo/default.png');
define('MAX_FILE_SIZE', 1 * 1024 * 1024); // 1 MB
define('MAX_BUKTI_SIZE', 1024 * 1024); // 1 MB
define('ALLOWED_MIME', ['image/jpeg', 'image/png', 'image/jpg', 'application/pdf']);
define('ALLOWED_IMAGE_MIME', ['image/jpeg', 'image/jpg', 'image/png']);

define('MAX_SEKOLAH', 500);


define('SESSION_TIMEOUT', 7200); // 2 jam


// Session start (singleton)
if (session_status() === PHP_SESSION_NONE) {
    // Pengaturan session yang lebih aman dan terpercaya untuk mobile
    ini_set('session.cookie_httponly', '1');
    ini_set('session.use_strict_mode', '1');
    ini_set('session.cookie_samesite', 'Lax');
    
    // Deteksi HTTPS termasuk reverse proxy (Cloudflare, dll)
    $isHttps = (
        (!empty($_SERVER['HTTPS']) && $_SERVER['HTTPS'] !== 'off')
        || (!empty($_SERVER['HTTP_X_FORWARDED_PROTO']) && $_SERVER['HTTP_X_FORWARDED_PROTO'] === 'https')
        || (!empty($_SERVER['HTTP_X_FORWARDED_SSL']) && $_SERVER['HTTP_X_FORWARDED_SSL'] === 'on')
        || (isset($_SERVER['SERVER_PORT']) && (int)$_SERVER['SERVER_PORT'] === 443)
    );
    
    if ($isHttps) {
        ini_set('session.cookie_secure', '1');
        header('Strict-Transport-Security: max-age=31536000; includeSubDomains; preload');
    }
    
    // Session lifetime lebih panjang untuk mobile (agar tidak sering logout)
    ini_set('session.gc_maxlifetime', (string)SESSION_TIMEOUT);
    ini_set('session.cookie_lifetime', '0'); // Sampai browser ditutup
    
    session_start();
}
