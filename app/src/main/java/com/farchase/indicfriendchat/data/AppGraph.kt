package com.farchase.indicfriendchat.data

import android.content.Context

class AppGraph(context: Context) {
    private val ctx = context.applicationContext

    val profileStore: ProfileStore = ProfileStore(ctx)
    val repo: ChatRepository = ChatRepository(ctx)
}
