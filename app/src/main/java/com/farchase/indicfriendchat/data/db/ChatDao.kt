package com.farchase.indicfriendchat.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    fun observeAll(): Flow<List<ChatEntity>>

    @Insert
    suspend fun insert(msg: ChatEntity)

    @Query("DELETE FROM messages")
    suspend fun clear()
}
