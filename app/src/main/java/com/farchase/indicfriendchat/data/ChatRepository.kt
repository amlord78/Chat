class ChatRepository(private val api: ChatApi)
private fun provideChatRepository(context: Context): ChatRepository {
    return ChatRepository(Network.api) // or Network.service depending your file
}
class ChatRepository(private val baseUrl: String)
private fun provideChatRepository(context: Context): ChatRepository {
    return ChatRepository(BuildConfig.BASE_URL)
}

class ChatRepository(
    private val api: ChatApi,
    private val profileStore: ProfileStore
)

private fun provideChatRepository(context: Context): ChatRepository {
    val store = ProfileStore(context)
    return ChatRepository(Network.api, store)
}

package com.farchase.indicfriendchat.data

import android.content.Context
import com.farchase.indicfriendchat.data.db.AppDatabase
import com.farchase.indicfriendchat.data.db.ChatEntity
import com.farchase.indicfriendchat.data.net.Network
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepository(context: Context) {
    private val dao = AppDatabase.get(context).chatDao()
    private val api = Network.createApi()

    fun observeMessages(): Flow<List<ChatMessage>> =
        dao.observeAll().map { list ->
            list.map { ChatMessage(id = it.id, role = it.role, content = it.content, timestamp = it.timestamp) }
        }

    suspend fun send(profile: BotProfile, detectedLanguageTag: String, userMessage: String, historyWindow: List<ChatMessage>) {
        dao.insert(ChatEntity(role = "user", content = userMessage, timestamp = System.currentTimeMillis()))
        val resp = api.chat(ChatRequest(profile, detectedLanguageTag, userMessage, historyWindow))
        dao.insert(ChatEntity(role = "assistant", content = resp.reply, timestamp = System.currentTimeMillis()))
    }

    suspend fun clear() = dao.clear()
}
