package com.farchase.indicfriendchat.ui.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.tasks.await
import androidx.lifecycle.viewModelScope
import com.farchase.indicfriendchat.data.BotProfile
import com.farchase.indicfriendchat.data.ChatRepository
import com.farchase.indicfriendchat.data.ProfileStore
import com.google.mlkit.nl.languageid.LanguageIdentification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ChatUiState(
    val profile: BotProfile = BotProfile(),
    val isSending: Boolean = false,
    val error: String? = null
)

class ChatViewModel(
    private val context: Context,
    private val store: ProfileStore,
    private val repo: ChatRepository
) : ViewModel() {

    val messages = repo.observeMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _ui = MutableStateFlow(ChatUiState())
    val ui: StateFlow<ChatUiState> = _ui

    fun loadProfile() {
        viewModelScope.launch {
            store.profileFlow.collect { p -> _ui.value = _ui.value.copy(profile = p) }
        }
    }

    fun send(userText: String) {
        if (userText.isBlank()) return
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isSending = true, error = null)
            try {
                val langTag = detectLang(userText)
                val history = messages.value.takeLast(20)
                repo.send(_ui.value.profile, langTag, userText, history)
                _ui.value = _ui.value.copy(isSending = false)
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(isSending = false, error = e.message ?: "Failed")
            }
        }
    }

    fun clearChat() { viewModelScope.launch { repo.clear() } }

    private suspend fun detectLang(text: String): String {
        val id = LanguageIdentification.getClient()
        val code = runCatching { id.identifyLanguage(text).await() }.getOrNull()
        return if (code == null || code == "und") "en" else code
    }
}
