package com.tcl.moviearkclone

import android.content.Context
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        setContentView(webView)

        val s = webView.settings
        s.javaScriptEnabled = true
        s.domStorageEnabled = true
        s.allowFileAccess = true
        s.mediaPlaybackRequiresUserGesture = false
        s.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = WebViewClient()
        webView.addJavascriptInterface(AndroidBridge(this), "Android")
        webView.loadUrl("file:///android_asset/index.html")
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }

    class AndroidBridge(private val ctx: Context) {
        private val prefs = ctx.getSharedPreferences("iptv", Context.MODE_PRIVATE)

        @JavascriptInterface
        fun getCache(id: String): String? = prefs.getString("tv_src_$id", null)

        @JavascriptInterface
        fun putCache(id: String, text: String): Boolean {
            prefs.edit().putString("tv_src_$id", text).apply()
            return true
        }

        @JavascriptInterface
        fun delCache(id: String) {
            prefs.edit().remove("tv_src_$id").apply()
        }

        @JavascriptInterface
        fun getUserAgent(): String = prefs.getString("tv_ua", "") ?: ""

        @JavascriptInterface
        fun setUserAgent(ua: String) {
            prefs.edit().putString("tv_ua", ua).apply()
        }

        @JavascriptInterface
        fun getLocalLogo(key: String): String? = null

        @JavascriptInterface
        fun setBackground(color: String) { }
    }
}
