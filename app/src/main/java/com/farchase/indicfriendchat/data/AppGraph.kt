package com.farchase.indicfriendchat.data

import android.content.Context

class AppGraph {
    private var inited = false
    lateinit var profileStore: ProfileStore
        private set
    lateinit var repo: ChatRepository
        private set

    fun init(context: Context) {
        if (inited) return
        profileStore = ProfileStore(context.applicationContext)
        repo = ChatRepository(context.applicationContext)
        inited = true
    }
}
