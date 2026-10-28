package com.venom.browser.core.browser

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URLEncoder

class BrowserViewModel(application: Application) : AndroidViewModel(application) {
    private val _tabs = MutableStateFlow<List<BrowserTab>>(listOf(BrowserTab()))
    val tabs: StateFlow<List<BrowserTab>> = _tabs.asStateFlow()

    private val _activeTabId = MutableStateFlow(_tabs.value.first().id)
    val activeTabId: StateFlow<String> = _activeTabId.asStateFlow()

    private val _addressBarText = MutableStateFlow("")
    val addressBarText: StateFlow<String> = _addressBarText.asStateFlow()

    private val _venomButtonEnabled = MutableStateFlow(true)
    val venomButtonEnabled: StateFlow<Boolean> = _venomButtonEnabled.asStateFlow()

    fun getActiveTab(): BrowserTab? = _tabs.value.find { it.id == _activeTabId.value }

    fun setAddressBarText(text: String) {
        _addressBarText.value = text
    }

    fun createNewTab(url: String = "about:home") {
        val newTab = BrowserTab(url = url)
        _tabs.value = _tabs.value + newTab
        _activeTabId.value = newTab.id
        _addressBarText.value = if (url == "about:home") "" else url
    }

    fun closeTab(tabId: String) {
        if (_tabs.value.size <= 1) return
        val newTabs = _tabs.value.filter { it.id != tabId }
        _tabs.value = newTabs
        if (_activeTabId.value == tabId) {
            _activeTabId.value = newTabs.last().id
        }
    }

    fun switchToTab(tabId: String) {
        _activeTabId.value = tabId
        val tab = getActiveTab()
        _addressBarText.value = if (tab?.url == "about:home") "" else tab?.url.orEmpty()
    }

    fun navigate(input: String) {
        val processedUrl = processInput(input)
        val activeId = _activeTabId.value
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == activeId) {
                tab.copy(url = processedUrl, title = processedUrl, isLoading = true)
            } else {
                tab
            }
        }
        if (processedUrl.startsWith("about:")) {
            _addressBarText.value = ""
        } else {
            _addressBarText.value = processedUrl
        }
    }

    private fun processInput(input: String): String {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return "about:home"
        if (trimmed == "about:home") return "about:home"
        if (Patterns.WEB_URL.matcher(trimmed).matches() || trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) trimmed else "https://$trimmed"
        }
        // Use Google as default search engine
        val query = URLEncoder.encode(trimmed, "UTF-8")
        return "https://www.google.com/search?q=$query"
    }

    fun updateTab(tabId: String, update: (BrowserTab) -> BrowserTab) {
        _tabs.value = _tabs.value.map { if (it.id == tabId) update(it) else it }
    }

    fun toggleVenomButton(enabled: Boolean) {
        _venomButtonEnabled.value = enabled
    }
}
