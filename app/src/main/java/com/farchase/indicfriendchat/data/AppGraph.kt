package com.farchase.indicfriendchat.data

import android.content.Context
import com.farchase.indicfriendchat.data.net.Network

class AppGraph(context: Context) {
    private val ctx = context.applicationContext

    val profileStore: ProfileStore = ProfileStore(ctx)

    // ✅ ChatRepository(api, profileStore)
    val repo: ChatRepository = ChatRepository(
        api = Network.api,
        profileStore = profileStore
    )
}
