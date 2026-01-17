package com.farchase.indicfriendchat.data

import android.content.Context

class AppGraph(context: Context) {

    val profileStore: ProfileStore = ProfileStore(context.applicationContext)

    val repo: ChatRepository = ChatRepository(
        context.applicationContext
    )
}
