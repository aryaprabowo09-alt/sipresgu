<?php
require_once __DIR__ . '/config.php';
require_once __DIR__ . '/includes/functions.php';

if (isset($_SESSION['user_id'])) {
    $redirect = ($_SESSION['role'] === 'admin')
        ? url('/admin/dashboard')
        : url('/guru/dashboard');
    header('Location: ' . $redirect);
} else {
    header('Location: ' . url('/auth/login'));
}
exit;
