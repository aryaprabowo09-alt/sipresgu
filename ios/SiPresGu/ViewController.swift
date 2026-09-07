import UIKit
import WebKit

class ViewController: UIViewController, WKNavigationDelegate, WKUIDelegate {

    private var webView: WKWebView!
    private let refreshControl = UIRefreshControl()
    private let progressView = UIProgressView(progressViewStyle: .bar)
    private let serverURL = URL(string: "https://sipresgu.web.id/")!

    // Splash / Loading overlay
    private var splashView: UIView!

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white

        setupWebView()
        setupProgressView()
        setupSplashView()

        loadURL()
    }

    private func setupWebView() {
        let configuration = WKWebViewConfiguration()
        configuration.allowsInlineMediaPlayback = true
        configuration.mediaTypesRequiringUserActionForPlayback = []

        // Enable camera & microphone access in iOS 15+
        if #available(iOS 15.0, *) {
            configuration.preferences.isElementFullscreenEnabled = true
        }

        webView = WKWebView(frame: view.bounds, configuration: configuration)
        webView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        webView.navigationDelegate = self
        webView.uiDelegate = self
        webView.scrollView.bounces = true
        webView.allowsBackForwardNavigationGestures = true
        webView.customUserAgent = (webView.customUserAgent ?? "") + " SiPresGuApp-iOS/1.0"

        // Pull to refresh
        refreshControl.addTarget(self, action: #selector(handleRefresh), for: .valueChanged)
        refreshControl.tintColor = UIColor(red: 13/255, green: 148/255, blue: 136/255, alpha: 1.0)
        webView.scrollView.addSubview(refreshControl)

        view.addSubview(webView)

        // Observe loading progress
        webView.addObserver(self, forKeyPath: #keyPath(WKWebView.estimatedProgress), options: .new, context: nil)
    }

    private func setupProgressView() {
        progressView.translatesAutoresizingMaskIntoConstraints = false
        progressView.progressTintColor = UIColor(red: 13/255, green: 148/255, blue: 136/255, alpha: 1.0)
        progressView.trackTintColor = .clear
        view.addSubview(progressView)

        NSLayoutConstraint.activate([
            progressView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            progressView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            progressView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            progressView.heightAnchor.constraint(equalToConstant: 2.5)
        ])
    }

    private func setupSplashView() {
        splashView = UIView(frame: view.bounds)
        splashView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        splashView.backgroundColor = .white

        let logoImageView = UIImageView()
        logoImageView.image = UIImage(named: "AppIcon")
        logoImageView.contentMode = .scaleAspectFit
        logoImageView.layer.cornerRadius = 20
        logoImageView.clipsToBounds = true
        logoImageView.translatesAutoresizingMaskIntoConstraints = false

        let titleLabel = UILabel()
        titleLabel.text = "SiPresGu"
        titleLabel.font = UIFont.systemFont(ofSize: 24, weight: .bold)
        titleLabel.textColor = UIColor(red: 15/255, green: 118/255, blue: 110/255, alpha: 1.0)
        titleLabel.translatesAutoresizingMaskIntoConstraints = false

        let subtitleLabel = UILabel()
        subtitleLabel.text = "Sistem Presensi Guru Terpadu"
        subtitleLabel.font = UIFont.systemFont(ofSize: 14, weight: .medium)
        subtitleLabel.textColor = UIColor(red: 100/255, green: 116/255, blue: 139/255, alpha: 1.0)
        subtitleLabel.translatesAutoresizingMaskIntoConstraints = false

        let spinner = UIActivityIndicatorView(style: .medium)
        spinner.color = UIColor(red: 13/255, green: 148/255, blue: 136/255, alpha: 1.0)
        spinner.startAnimating()
        spinner.translatesAutoresizingMaskIntoConstraints = false

        splashView.addSubview(logoImageView)
        splashView.addSubview(titleLabel)
        splashView.addSubview(subtitleLabel)
        splashView.addSubview(spinner)

        NSLayoutConstraint.activate([
            logoImageView.centerXAnchor.constraint(equalTo: splashView.centerXAnchor),
            logoImageView.centerYAnchor.constraint(equalTo: splashView.centerYAnchor, constant: -60),
            logoImageView.widthAnchor.constraint(equalToConstant: 90),
            logoImageView.heightAnchor.constraint(equalToConstant: 90),

            titleLabel.centerXAnchor.constraint(equalTo: splashView.centerXAnchor),
            titleLabel.topAnchor.constraint(equalTo: logoImageView.bottomAnchor, constant: 16),

            subtitleLabel.centerXAnchor.constraint(equalTo: splashView.centerXAnchor),
            subtitleLabel.topAnchor.constraint(equalTo: titleLabel.bottomAnchor, constant: 6),

            spinner.centerXAnchor.constraint(equalTo: splashView.centerXAnchor),
            spinner.topAnchor.constraint(equalTo: subtitleLabel.bottomAnchor, constant: 28)
        ])

        view.addSubview(splashView)
    }

    private func loadURL() {
        let request = URLRequest(url: serverURL, cachePolicy: .useProtocolCachePolicy, timeoutInterval: 20)
        webView.load(request)
    }

    @objc private func handleRefresh() {
        webView.reload()
    }

    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        if keyPath == #keyPath(WKWebView.estimatedProgress) {
            progressView.progress = Float(webView.estimatedProgress)
            progressView.isHidden = (webView.estimatedProgress >= 1.0)
        }
    }

    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        refreshControl.endRefreshing()

        // Fade out splash view
        if splashView != nil && splashView.alpha > 0 {
            UIView.animate(withDuration: 0.35, animations: {
                self.splashView.alpha = 0
            }) { _ in
                self.splashView.removeFromSuperview()
            }
        }
    }

    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        refreshControl.endRefreshing()
        showErrorAlert(message: error.localizedDescription)
    }

    func webView(_ webView: WKWebView, didFailProvisionalNavigation navigation: WKNavigation!, withError error: Error) {
        refreshControl.endRefreshing()
        showErrorAlert(message: "Tidak dapat terhubung ke server SiPresGu. Pastikan koneksi internet Anda aktif.")
    }

    private func showErrorAlert(message: String) {
        let alert = UIAlertController(title: "Koneksi Terputus", message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "Coba Lagi", style: .default, handler: { _ in
            self.loadURL()
        }))
        alert.addAction(UIAlertAction(title: "Tutup", style: .cancel, handler: nil))
        present(alert, animated: true)
    }

    // Permission request handling in iOS 15+ for Camera and Microphone
    @available(iOS 15.0, *)
    func webView(_ webView: WKWebView, requestMediaCapturePermissionFor origin: WKSecurityOrigin, initiatedByFrame frame: WKFrameInfo, type: WKMediaCaptureType, decisionHandler: @escaping (WKPermissionDecision) -> Void) {
        decisionHandler(.grant)
    }

    deinit {
        webView?.removeObserver(self, forKeyPath: #keyPath(WKWebView.estimatedProgress))
    }
}
