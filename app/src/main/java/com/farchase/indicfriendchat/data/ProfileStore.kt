package com.farchase.indicfriendchat.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "bot_profile")

class ProfileStore(private val context: Context) {
    private val KEY_NAME = stringPreferencesKey("botName")
    private val KEY_AVATAR = stringPreferencesKey("botAvatarUri")
    private val KEY_STATE = stringPreferencesKey("stateName")
    private val KEY_SCRIPT = stringPreferencesKey("scriptPref")

    private val KEY_FRIEND_TYPE = stringPreferencesKey("friendType")
    private val KEY_VOICE_REPLY = stringPreferencesKey("voiceReplyEnabled")
    private val KEY_VOICE_INPUT = stringPreferencesKey("voiceInputEnabled")

    val profileFlow: Flow<BotProfile> = context.dataStore.data.map { prefs ->
        val ft = prefs[KEY_FRIEND_TYPE] ?: FriendType.NEUTRAL.name
        val voiceReply = (prefs[KEY_VOICE_REPLY] ?: "false").toBoolean()
        val voiceInput = (prefs[KEY_VOICE_INPUT] ?: "true").toBoolean()

        BotProfile(
            botName = prefs[KEY_NAME] ?: "Dost",
            botAvatarUri = prefs[KEY_AVATAR],
            stateName = prefs[KEY_STATE] ?: IndianState.OTHER.name,
            scriptPreference = runCatching {
                ScriptPreference.valueOf(prefs[KEY_SCRIPT] ?: ScriptPreference.MATCH_USER.name)
            }.getOrDefault(ScriptPreference.MATCH_USER),
            friendType = runCatching { FriendType.valueOf(ft) }.getOrDefault(FriendType.NEUTRAL),
            voiceReplyEnabled = voiceReply,
            voiceInputEnabled = voiceInput
        )
    }

    suspend fun save(profile: BotProfile) {
        context.dataStore.edit { prefs ->
            prefs[KEY_NAME] = profile.botName
            if (profile.botAvatarUri != null) prefs[KEY_AVATAR] = profile.botAvatarUri else prefs.remove(KEY_AVATAR)
            prefs[KEY_STATE] = profile.stateName
            prefs[KEY_SCRIPT] = profile.scriptPreference.name

            prefs[KEY_FRIEND_TYPE] = profile.friendType.name
            prefs[KEY_VOICE_REPLY] = profile.voiceReplyEnabled.toString()
            prefs[KEY_VOICE_INPUT] = profile.voiceInputEnabled.toString()
        }
    }
}
