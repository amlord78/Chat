package com.farchase.indicfriendchat.data

import android.content.Context
import androidx.room.Room

class AppGraph(context: Context) {

    // ✅ Room DB (if you have one)
    private val db: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "indicfriendchat.db"
    ).fallbackToDestructiveMigration()
     .build()

    // ✅ Repo + ProfileStore always initialized
    val profileStore: ProfileStore = ProfileStore(context.applicationContext)
    val repo: ChatRepository = ChatRepository(
        api = ApiClient.create(),   // if your project has ApiClient/Network
        dao = db.chatDao()
    )
}
