package com.farchase.indicfriendchat.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farchase.indicfriendchat.data.BotProfile
import com.farchase.indicfriendchat.data.FriendType
import com.farchase.indicfriendchat.data.IndianState
import com.farchase.indicfriendchat.data.ProfileStore
import com.farchase.indicfriendchat.data.ScriptPreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class SetupUiState(
    val botName: String = "Dost",
    val avatarUri: String? = null,
    val selectedState: IndianState = IndianState.OTHER,
    val scriptPreference: ScriptPreference = ScriptPreference.MATCH_USER,
    val friendType: FriendType = FriendType.NEUTRAL,
    val voiceReplyEnabled: Boolean = false,
    val voiceInputEnabled: Boolean = true
)

class SetupViewModel(private val store: ProfileStore) : ViewModel() {
    private val _ui = MutableStateFlow(SetupUiState())
    val ui: StateFlow<SetupUiState> = _ui

    fun load() {
        viewModelScope.launch {
            val p = store.profileFlow.first()
            _ui.value = SetupUiState(
                botName = p.botName,
                avatarUri = p.botAvatarUri,
                selectedState = runCatching { IndianState.valueOf(p.stateName) }.getOrDefault(IndianState.OTHER),
                scriptPreference = p.scriptPreference,
                friendType = p.friendType,
                voiceReplyEnabled = p.voiceReplyEnabled,
                voiceInputEnabled = p.voiceInputEnabled
            )
        }
    }

    fun setName(name: String) { _ui.value = _ui.value.copy(botName = name) }
    fun setAvatar(uri: String?) { _ui.value = _ui.value.copy(avatarUri = uri) }
    fun setState(state: IndianState) { _ui.value = _ui.value.copy(selectedState = state) }
    fun setScriptPreference(pref: ScriptPreference) { _ui.value = _ui.value.copy(scriptPreference = pref) }
    fun setFriendType(t: FriendType) { _ui.value = _ui.value.copy(friendType = t) }
    fun setVoiceReplyEnabled(v: Boolean) { _ui.value = _ui.value.copy(voiceReplyEnabled = v) }
    fun setVoiceInputEnabled(v: Boolean) { _ui.value = _ui.value.copy(voiceInputEnabled = v) }

    fun save(onDone: () -> Unit) {
        viewModelScope.launch {
            val s = _ui.value
            store.save(
                BotProfile(
                    botName = s.botName.ifBlank { "Dost" },
                    botAvatarUri = s.avatarUri,
                    stateName = s.selectedState.name,
                    scriptPreference = s.scriptPreference,
                    friendType = s.friendType,
                    voiceReplyEnabled = s.voiceReplyEnabled,
                    voiceInputEnabled = s.voiceInputEnabled
                )
            )
            onDone()
        }
    }
}
