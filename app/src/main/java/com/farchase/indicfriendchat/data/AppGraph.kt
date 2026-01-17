package com.farchase.indicfriendchat.data

import android.content.Context

class AppGraph(context: Context) {
    val profileStore = ProfileStore(context.applicationContext)

    // ✅ IMPORTANT: Use the repo class your project already has
    // If your repo class name is different, change it here.
    val repo = ChatRepo(context.applicationContext)
}
