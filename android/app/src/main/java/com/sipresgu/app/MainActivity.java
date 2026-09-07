package com.sipresgu.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String PREF_NAME = "sipresgu_prefs";
    private static final String KEY_SERVER_URL = "server_url";

    private static final int REQ_PERMISSIONS_ALL = 1001;
    private static final int REQ_FILE_CHOOSER = 1002;

    private WebView webView;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private LinearLayout layoutError;
    private TextView tvErrorDetails;
    private Button btnRetry;
    private Button btnChangeUrl;

    private SharedPreferences prefs;
    private String currentUrl;

    // File chooser & camera capture variables
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;

    // Geolocation pending callback
    private GeolocationPermissions.Callback mGeoCallback;
    private String mGeoOrigin;

    // WebRTC / Camera permission request
    private PermissionRequest mPermissionRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        initViews();
        setupToolbar();
        checkAndRequestPermissions();
        setupWebView();

        loadSavedUrl();
    }

    private void initViews() {
        webView = findViewById(R.id.webView);
        swipeRefresh = findViewById(R.id.swipeRefreshLayout);
        progressBar = findViewById(R.id.progressBar);
        layoutError = findViewById(R.id.layoutError);
        tvErrorDetails = findViewById(R.id.tvErrorDetails);
        btnRetry = findViewById(R.id.btnRetry);
        btnChangeUrl = findViewById(R.id.btnChangeUrl);

        swipeRefresh.setColorSchemeResources(R.color.primary, R.color.accent);
        swipeRefresh.setOnRefreshListener(() -> {
            layoutError.setVisibility(View.GONE);
            webView.reload();
        });

        btnRetry.setOnClickListener(v -> {
            layoutError.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            webView.loadUrl(currentUrl);
        });

        btnChangeUrl.setOnClickListener(v -> showServerUrlDialog());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            layoutError.setVisibility(View.GONE);
            webView.reload();
            return true;
        } else if (id == R.id.action_set_url) {
            showServerUrlDialog();
            return true;
        } else if (id == R.id.action_about) {
            showAboutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkAndRequestPermissions() {
        List<String> neededPermissions = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            neededPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            neededPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            neededPermissions.add(Manifest.permission.CAMERA);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                neededPermissions.add(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                neededPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }

        if (!neededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, neededPermissions.toArray(new String[0]), REQ_PERMISSIONS_ALL);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setGeolocationEnabled(true);
        settings.setGeolocationDatabasePath(getFilesDir().getPath());
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // User Agent kustom agar web mengenali aplikasi Android
        String defaultUa = settings.getUserAgentString();
        settings.setUserAgentString(defaultUa + " SiPresGuApp/1.0");

        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                // Hanya tangani error jika request adalah main frame (bukan gambar/iklan yang gagal load)
                if (request.isForMainFrame()) {
                    progressBar.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(false);
                    webView.setVisibility(View.GONE);
                    layoutError.setVisibility(View.VISIBLE);
                    tvErrorDetails.setText("Gagal membuka halaman: " + request.getUrl().toString() + "\nPeriksa koneksi atau URL server.");
                }
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            // Tangani izin Geolocation / GPS di web
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    callback.invoke(origin, true, false);
                } else {
                    mGeoCallback = callback;
                    mGeoOrigin = origin;
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            REQ_PERMISSIONS_ALL);
                }
            }

            // Tangani izin WebRTC / Kamera di HTML5 (getUserMedia untuk selfie presensi)
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                mPermissionRequest = request;
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    request.grant(request.getResources());
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            REQ_PERMISSIONS_ALL);
                }
            }

            // Tangani upload berkas dan pengambilan foto dari form
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;

                Intent takePictureIntent = null;
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                                "com.sipresgu.app.fileprovider", photoFile);
                        takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("*/*");
                String[] mimes = {"image/*", "application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
                contentSelectionIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimes);

                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Pilih Sumber Berkas / Foto");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                startActivityForResult(chooserIntent, REQ_FILE_CHOOSER);
                return true;
            }
        });

        // Tangani Download File (PDF Rekap & CSV)
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                try {
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.setMimeType(mimeType);
                    String cookies = CookieManager.getInstance().getCookie(url);
                    request.addRequestHeader("cookie", cookies);
                    request.addRequestHeader("User-Agent", userAgent);
                    request.setDescription("Mengunduh laporan SiPresGu...");
                    String filename = URLUtil.guessFileName(url, contentDisposition, mimeType);
                    request.setTitle(filename);
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);

                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    if (dm != null) {
                        dm.enqueue(request);
                        Toast.makeText(MainActivity.this, "Mengunduh: " + filename, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // Fallback buka di browser eksternal
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            }
        });
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCameraPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void loadSavedUrl() {
        String defaultUrl = getString(R.string.default_server_url);
        currentUrl = prefs.getString(KEY_SERVER_URL, defaultUrl);
        webView.setVisibility(View.VISIBLE);
        layoutError.setVisibility(View.GONE);
        webView.loadUrl(currentUrl);
    }

    private void showServerUrlDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_url_title);

        final EditText input = new EditText(this);
        input.setHint(R.string.dialog_url_hint);
        input.setText(currentUrl);
        input.setSelection(currentUrl != null ? currentUrl.length() : 0);

        int paddingPx = (int) (18 * getResources().getDisplayMetrics().density);
        input.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
        builder.setView(input);

        builder.setPositiveButton(R.string.btn_save, (dialog, which) -> {
            String newUrl = input.getText().toString().trim();
            if (newUrl.startsWith("http://") || newUrl.startsWith("https://")) {
                prefs.edit().putString(KEY_SERVER_URL, newUrl).apply();
                currentUrl = newUrl;
                Toast.makeText(this, R.string.url_saved, Toast.LENGTH_SHORT).show();
                layoutError.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                webView.loadUrl(currentUrl);
            } else {
                Toast.makeText(this, R.string.invalid_url, Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton(R.string.btn_cancel, (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Tentang SiPresGu")
                .setMessage("SiPresGu (Sistem Presensi Guru)\nVersi Android 1.0.0\n\nAplikasi Presensi Berbasis GPS dengan Validasi Radius & Pengambilan Foto Selfie.")
                .setPositiveButton("Tutup", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQ_FILE_CHOOSER) {
            if (mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }

            Uri[] results = null;
            if (resultCode == RESULT_OK) {
                if (data == null || data.getData() == null) {
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }

            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_PERMISSIONS_ALL) {
            boolean fineLocationGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            if (fineLocationGranted && mGeoCallback != null && mGeoOrigin != null) {
                mGeoCallback.invoke(mGeoOrigin, true, false);
                mGeoCallback = null;
                mGeoOrigin = null;
            }

            boolean cameraGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
            if (cameraGranted && mPermissionRequest != null) {
                mPermissionRequest.grant(mPermissionRequest.getResources());
                mPermissionRequest = null;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
