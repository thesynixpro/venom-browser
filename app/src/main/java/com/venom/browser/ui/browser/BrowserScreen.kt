package com.venom.browser.ui.browser

import android.annotation.SuppressLint
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.venom.browser.core.browser.BrowserTab
import com.venom.browser.core.browser.BrowserViewModel
import com.venom.browser.ui.components.VenomButton
import com.venom.browser.ui.home.HomeScreen
import com.venom.browser.ui.theme.Background
import com.venom.browser.ui.theme.BorderColor
import com.venom.browser.ui.theme.PrimaryText
import com.venom.browser.ui.theme.SecondaryText
import com.venom.browser.ui.theme.Surface
import com.venom.browser.ui.theme.VenomGreen

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserScreen(viewModel: BrowserViewModel = viewModel()) {
    val tabs by viewModel.tabs.collectAsState()
    val activeTabId by viewModel.activeTabId.collectAsState()
    val addressBarText by viewModel.addressBarText.collectAsState()
    val venomButtonEnabled by viewModel.venomButtonEnabled.collectAsState()
    val activeTab = tabs.find { it.id == activeTabId }

    var webView by remember { mutableStateOf<WebView?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Address bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = addressBarText,
                    onValueChange = { viewModel.setAddressBarText(it) },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp)),
                    placeholder = { Text("Search or enter URL", color = SecondaryText) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = if (addressBarText.isNotEmpty()) VenomGreen else SecondaryText
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VenomGreen,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = PrimaryText,
                        unfocusedTextColor = PrimaryText,
                        cursorColor = VenomGreen
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                    keyboardActions = KeyboardActions(
                        onGo = {
                            viewModel.navigate(addressBarText)
                        }
                    )
                )
            }

            // Progress indicator
            if (activeTab?.isLoading == true) {
                LinearProgressIndicator(
                    progress = { (activeTab.progress / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(),
                    color = VenomGreen,
                    trackColor = Surface
                )
            }

            // Content area
            Box(modifier = Modifier.weight(1f)) {
                if (activeTab?.url == "about:home" || activeTab?.url?.isEmpty() == true) {
                    HomeScreen(
                        onNavigate = { query ->
                            viewModel.navigate(query)
                        },
                        onNewTab = { viewModel.createNewTab() }
                    )
                } else {
                    AndroidView(
                        factory = { context ->
                            WebView(context).apply {
                                webViewClient = object : WebViewClient() {
                                    override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                                        super.onPageStarted(view, url, favicon)
                                        viewModel.updateTab(activeTabId) { tab ->
                                            tab.copy(isLoading = true, url = url ?: tab.url, title = view.title ?: tab.title)
                                        }
                                        viewModel.setAddressBarText(url ?: "")
                                    }

                                    override fun onPageFinished(view: WebView?, url: String?) {
                                        super.onPageFinished(view, url)
                                        viewModel.updateTab(activeTabId) { tab ->
                                            tab.copy(
                                                isLoading = false,
                                                canGoBack = view?.canGoBack() ?: tab.canGoBack,
                                                canGoForward = view?.canGoForward() ?: tab.canGoForward,
                                                title = view?.title ?: tab.title
                                            )
                                        }
                                        viewModel.setAddressBarText(url ?: "")
                                    }
                                }
                                webChromeClient = object : WebChromeClient() {
                                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                        super.onProgressChanged(view, newProgress)
                                        viewModel.updateTab(activeTabId) { tab -> tab.copy(progress = newProgress) }
                                    }
                                }
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                settings.loadWithOverviewMode = true
                                settings.useWideViewPort = true
                                settings.setSupportZoom(true)
                                settings.builtInZoomControls = true
                                settings.displayZoomControls = false
                                settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                                loadUrl(activeTab.url)
                                webView = this
                            }
                        },
                        update = { view ->
                            if (view.url != activeTab.url && activeTab.url != "about:home") {
                                view.loadUrl(activeTab.url)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Bottom toolbar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface)
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { webView?.goBack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = if (activeTab?.canGoBack == true) PrimaryText else SecondaryText)
                }
                IconButton(onClick = { webView?.goForward() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Forward", tint = if (activeTab?.canGoForward == true) PrimaryText else SecondaryText)
                }
                IconButton(onClick = { 
                    if (activeTab?.url == "about:home") {
                        // refresh home
                    } else {
                        webView?.reload()
                    }
                }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reload", tint = PrimaryText)
                }
                IconButton(onClick = { viewModel.createNewTab("about:home") }) {
                    Icon(Icons.Default.Home, contentDescription = "Home", tint = PrimaryText)
                }
                IconButton(onClick = { /* Tab switcher */ }) {
                    Icon(Icons.Default.Tab, contentDescription = "Tabs", tint = PrimaryText)
                    Text(
                        text = tabs.size.toString(),
                        color = VenomGreen,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                IconButton(onClick = { /* Menu */ }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = PrimaryText)
                }
            }
        }

        // Venom Button
        VenomButton(
            visible = venomButtonEnabled,
            onNewTab = { viewModel.createNewTab("about:home") },
            onCloseTab = { if (tabs.size > 1) viewModel.closeTab(activeTabId) },
            onBookmarks = { },
            onDownloads = { },
            onFindInPage = { },
            onShare = { },
            onSettings = { },
            onPrivacy = { },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 16.dp)
        )
    }
}
