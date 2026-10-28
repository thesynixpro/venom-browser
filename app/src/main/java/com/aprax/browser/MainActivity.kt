package com.aprax.browser

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.app.Activity

class MainActivity : Activity() {
    private lateinit var webView: WebView
    private lateinit var addressBar: EditText
    private lateinit var homeView: View
    private lateinit var progress: ProgressBar
    private lateinit var backButton: TextView
    private lateinit var forwardButton: TextView
    private var currentUrl: String? = null
    private val bookmarks = linkedSetOf<String>()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        webView = findViewById(R.id.webView)
        addressBar = findViewById(R.id.addressBar)
        homeView = findViewById(R.id.homeView)
        progress = findViewById(R.id.loadingProgress)
        backButton = findViewById(R.id.backButton)
        forwardButton = findViewById(R.id.forwardButton)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.loadsImagesAutomatically = true
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.setSupportZoom(true)
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val uri = request?.url ?: return false
                if (uri.scheme == "http" || uri.scheme == "https") return false
                return try { startActivity(Intent(Intent.ACTION_VIEW, uri)); true } catch (_: Exception) { true }
            }
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                currentUrl = url
                if (!url.isNullOrBlank()) addressBar.setText(url)
                homeView.visibility = View.GONE
                progress.visibility = View.VISIBLE
                updateNavigation()
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                currentUrl = url
                progress.visibility = View.GONE
                if (!url.isNullOrBlank()) addressBar.setText(url)
                updateNavigation()
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progress.progress = newProgress
                progress.visibility = if (newProgress >= 100) View.GONE else View.VISIBLE
            }
            override fun onReceivedTitle(view: WebView?, title: String?) {
                // The URL remains visible in the address bar for easy editing.
            }
        }

        findViewById<TextView>(R.id.goButton).setOnClickListener { navigateFromAddress() }
        addressBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                navigateFromAddress(); true
            } else false
        }
        findViewById<TextView>(R.id.backButton).setOnClickListener { if (webView.canGoBack()) webView.goBack() }
        findViewById<TextView>(R.id.forwardButton).setOnClickListener { if (webView.canGoForward()) webView.goForward() }
        findViewById<TextView>(R.id.homeButton).setOnClickListener { showHome() }
        findViewById<TextView>(R.id.reloadButton).setOnClickListener {
            if (currentUrl == null) showHome() else webView.reload()
        }
        findViewById<TextView>(R.id.bookmarkButton).setOnClickListener { toggleBookmark() }
        findViewById<TextView>(R.id.shortcutGoogle).setOnClickListener { loadUrl("https://www.google.com") }
        findViewById<TextView>(R.id.shortcutYouTube).setOnClickListener { loadUrl("https://m.youtube.com") }
        findViewById<TextView>(R.id.shortcutGitHub).setOnClickListener { loadUrl("https://github.com") }
        findViewById<TextView>(R.id.tabsButton).setOnClickListener {
            AlertDialog.Builder(this).setTitle("Tabs").setMessage("Aprax Browser currently has one active tab. Multi-tab support can be added next.")
                .setPositiveButton("OK", null).show()
        }
        findViewById<TextView>(R.id.menuButton).setOnClickListener { showMenu() }

        if (savedInstanceState != null) webView.restoreState(savedInstanceState) else showHome()
    }

    private fun navigateFromAddress() {
        val raw = addressBar.text.toString().trim()
        if (raw.isEmpty()) return
        val target = when {
            raw.startsWith("https://", true) || raw.startsWith("http://", true) -> raw
            raw.contains(" ") || !raw.contains(".") -> "https://www.google.com/search?q=" + Uri.encode(raw)
            else -> "https://$raw"
        }
        val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(addressBar.windowToken, 0)
        addressBar.clearFocus()
        loadUrl(target)
    }

    private fun loadUrl(url: String) {
        homeView.visibility = View.GONE
        webView.visibility = View.VISIBLE
        webView.loadUrl(url)
    }

    private fun showHome() {
        currentUrl = null
        addressBar.setText("")
        homeView.visibility = View.VISIBLE
        progress.visibility = View.GONE
        updateNavigation()
    }

    private fun updateNavigation() {
        backButton.alpha = if (webView.canGoBack()) 1f else 0.45f
        forwardButton.alpha = if (webView.canGoForward()) 1f else 0.45f
    }

    private fun toggleBookmark() {
        val url = currentUrl
        if (url.isNullOrBlank()) {
            Toast.makeText(this, "Open a page to bookmark it", Toast.LENGTH_SHORT).show()
            return
        }
        if (bookmarks.remove(url)) Toast.makeText(this, "Bookmark removed", Toast.LENGTH_SHORT).show()
        else {
            bookmarks.add(url)
            Toast.makeText(this, "Bookmarked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showMenu() {
        val options = arrayOf("Bookmarks", "Share page", "Open in external browser", "Clear browsing data", "About Aprax Browser")
        AlertDialog.Builder(this).setTitle("Aprax Browser").setItems(options) { _, which ->
            when (which) {
                0 -> showBookmarks()
                1 -> currentUrl?.let { url -> startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, url), "Share page")) }
                    ?: Toast.makeText(this, "No page to share", Toast.LENGTH_SHORT).show()
                2 -> currentUrl?.let { url -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
                    ?: Toast.makeText(this, "No page is open", Toast.LENGTH_SHORT).show()
                3 -> AlertDialog.Builder(this).setTitle("Clear browsing data?").setMessage("This clears website cache, cookies and form data on this device.")
                    .setNegativeButton("Cancel", null).setPositiveButton("Clear") { _, _ ->
                        webView.clearCache(true); android.webkit.CookieManager.getInstance().removeAllCookies(null)
                        webView.clearFormData(); Toast.makeText(this, "Browsing data cleared", Toast.LENGTH_SHORT).show()
                    }.show()
                4 -> AlertDialog.Builder(this).setTitle("Aprax Browser").setMessage("Version 1.0.0\nA clean, lightweight Android browser starter built with Kotlin and Android WebView.\n\nThis is not a Chromium-based engine build.")
                    .setPositiveButton("OK", null).show()
            }
        }.show()
    }

    private fun showBookmarks() {
        if (bookmarks.isEmpty()) {
            AlertDialog.Builder(this).setTitle("Bookmarks").setMessage("No bookmarks yet. Open a page and tap the star to save it.")
                .setPositiveButton("OK", null).show()
            return
        }
        val list = bookmarks.toTypedArray()
        AlertDialog.Builder(this).setTitle("Bookmarks").setItems(list) { _, index -> loadUrl(list[index]) }
            .setNegativeButton("Close", null).show()
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack()
        else if (homeView.visibility != View.VISIBLE) showHome()
        else super.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        webView.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        webView.stopLoading()
        webView.destroy()
        super.onDestroy()
    }
}
