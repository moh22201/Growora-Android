package com.growora.game

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.Gravity
import android.graphics.Color
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.content.Intent

class MainActivity : Activity() {
    private lateinit var webView: WebView
    private lateinit var progress: ProgressBar
    private lateinit var root: FrameLayout
    private lateinit var errorPanel: LinearLayout

    private val gameUrl = "https://moh22201.github.io/Growora.sunflawer/"
    private val trustedHosts = setOf("moh22201.github.io", "jsgysongbsoizndqlsvy.supabase.co")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        root = FrameLayout(this)
        webView = WebView(this)
        progress = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal)
        errorPanel = buildErrorPanel()
        root.addView(webView, FrameLayout.LayoutParams(-1, -1))
        root.addView(progress, FrameLayout.LayoutParams(-1, dp(3), Gravity.TOP))
        root.addView(errorPanel, FrameLayout.LayoutParams(-1, -1))
        errorPanel.visibility = View.GONE
        setContentView(root)
        configureWebView()
        if (savedInstanceState == null) webView.loadUrl(gameUrl) else webView.restoreState(savedInstanceState)
    }

    private fun configureWebView() {
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.databaseEnabled = true
        settings.loadsImagesAutomatically = true
        settings.javaScriptCanOpenWindowsAutomatically = false
        settings.setSupportMultipleWindows(false)
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.allowFileAccess = false
        settings.allowContentAccess = false
        settings.allowFileAccessFromFileURLs = false
        settings.allowUniversalAccessFromFileURLs = false
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
        settings.mediaPlaybackRequiresUserGesture = true
        CookieManager.getInstance().setAcceptCookie(true)
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progress.progress = newProgress
                progress.visibility = if (newProgress >= 100) View.GONE else View.VISIBLE
            }
        }
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val uri = request.url
                val scheme = uri.scheme?.lowercase()
                val host = uri.host?.lowercase()
                if (scheme == "https" && host in trustedHosts) return false
                if (scheme == "mailto" || scheme == "tel" || scheme == "https") {
                    return try { startActivity(Intent(Intent.ACTION_VIEW, uri)); true } catch (_: Exception) { true }
                }
                return true
            }
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                errorPanel.visibility = View.GONE
                progress.visibility = View.VISIBLE
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                progress.visibility = View.GONE
                errorPanel.visibility = View.GONE
            }
            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: android.webkit.WebResourceError?) {
                if (request?.isForMainFrame == true) {
                    progress.visibility = View.GONE
                    errorPanel.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun buildErrorPanel(): LinearLayout {
        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(28), dp(24), dp(28), dp(24))
            setBackgroundColor(Color.rgb(244, 248, 239))
        }
        val title = TextView(this).apply { text = "Connection unavailable"; textSize = 22f; setTextColor(Color.rgb(37, 67, 43)); gravity = Gravity.CENTER }
        val detail = TextView(this).apply { text = "Growora needs an internet connection to load the latest game."; textSize = 15f; setTextColor(Color.rgb(75, 88, 74)); gravity = Gravity.CENTER; setPadding(0, dp(12), 0, dp(20)) }
        val retry = Button(this).apply {
            text = "Try again"
            setOnClickListener { errorPanel.visibility = View.GONE; webView.reload(); if (webView.url.isNullOrBlank()) webView.loadUrl(gameUrl) }
        }
        panel.addView(title); panel.addView(detail); panel.addView(retry)
        return panel
    }

    override fun onSaveInstanceState(outState: Bundle) { webView.saveState(outState); super.onSaveInstanceState(outState) }
    @Deprecated("Deprecated in Android API")
    override fun onBackPressed() { if (::webView.isInitialized && webView.canGoBack()) webView.goBack() else super.onBackPressed() }
    override fun onDestroy() { if (::webView.isInitialized) { webView.stopLoading(); webView.destroy() }; super.onDestroy() }
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
