package com.farchase.indicfriendchat.data.net

import com.farchase.indicfriendchat.data.ChatRequest
import com.farchase.indicfriendchat.data.ChatResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatApi {
    @POST("/chat")
    suspend fun chat(@Body req: ChatRequest): ChatResponse
}
