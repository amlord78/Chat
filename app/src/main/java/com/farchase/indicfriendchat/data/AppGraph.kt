package com.farchase.indicfriendchat.data

import android.content.Context

class AppGraph(context: Context) {
    private val ctx = context.applicationContext

    val profileStore: ProfileStore = ProfileStore(ctx)

    // ✅ You must match YOUR ChatRepository constructor here
    val repo: ChatRepository = provideChatRepository(ctx)
}

private fun provideChatRepository(ctx: Context): ChatRepository {
    // ✅ Replace this ONE line based on ChatRepository.kt constructor
    return ChatRepository(/* your required params here */)
}

package com.farchase.indicfriendchat.data

import android.content.Context

class AppGraph(context: Context) {
    val appContext: Context = context.applicationContext

    val profileStore: ProfileStore = ProfileStore(appContext)

    // ✅ Create repo via helper (choose correct line inside below)
    val repo: ChatRepository = provideChatRepository(appContext)
}

/**
 * ✅ IMPORTANT:
 * Uncomment ONLY ONE return line below depending on ChatRepository constructor.
 */
private fun provideChatRepository(ctx: Context): ChatRepository {

    // --- Case A: ChatRepository() has EMPTY constructor
    // return ChatRepository()

    // --- Case B: ChatRepository(context: Context)
    // return ChatRepository(ctx)

    // --- Case C: ChatRepository(baseUrl: String)
    // return ChatRepository("http://YOUR_SERVER_IP:8000")

    // --- Case D: ChatRepository(api: ChatApi)
    // return ChatRepository(Network.api)

    // --- Case E: ChatRepository(api: ChatApi, context: Context)
    // return ChatRepository(Network.api, ctx)

    // --- Case F: ChatRepository(api: ChatApi, profileStore: ProfileStore)
    // return ChatRepository(Network.api, ProfileStore(ctx))

    // If you don't know, start with EMPTY constructor check first.
    throw IllegalStateException("Select correct ChatRepository constructor in provideChatRepository()")
}
