package com.farchase.indicfriendchat.data

import android.content.Context

class AppGraph(context: Context) {
    val profileStore: ProfileStore = ProfileStore(context.applicationContext)

    // TODO: set correct repo constructor in Step 2
    val repo: ChatRepository = provideChatRepository(context.applicationContext)
}
