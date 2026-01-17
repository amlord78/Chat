package com.farchase.indicfriendchat.data

import kotlinx.serialization.Serializable

enum class Region { SOUTH, NORTH, WEST, EAST, GENERAL }

enum class IndianState(val display: String, val region: Region) {
    ANDHRA_PRADESH("Andhra Pradesh", Region.SOUTH),
    TELANGANA("Telangana", Region.SOUTH),
    TAMIL_NADU("Tamil Nadu", Region.SOUTH),
    KARNATAKA("Karnataka", Region.SOUTH),
    KERALA("Kerala", Region.SOUTH),

    MAHARASHTRA("Maharashtra", Region.WEST),
    GUJARAT("Gujarat", Region.WEST),
    GOA("Goa", Region.WEST),
    MADHYA_PRADESH("Madhya Pradesh", Region.WEST),

    DELHI("Delhi", Region.NORTH),
    UTTAR_PRADESH("Uttar Pradesh", Region.NORTH),
    RAJASTHAN("Rajasthan", Region.NORTH),
    PUNJAB("Punjab", Region.NORTH),
    HARYANA("Haryana", Region.NORTH),

    WEST_BENGAL("West Bengal", Region.EAST),
    ODISHA("Odisha", Region.EAST),
    BIHAR("Bihar", Region.EAST),
    ASSAM("Assam", Region.EAST),

    OTHER("Other / Prefer not to say", Region.GENERAL)
}

enum class ScriptPreference { MATCH_USER, ALWAYS_NATIVE_SCRIPT, ALWAYS_LATIN }
enum class FriendType { FEMALE, MALE, NEUTRAL }

@Serializable
data class BotProfile(
    val botName: String = "Dost",
    val botAvatarUri: String? = null,
    val stateName: String = IndianState.OTHER.name,
    val scriptPreference: ScriptPreference = ScriptPreference.MATCH_USER,
    val friendType: FriendType = FriendType.NEUTRAL,
    val voiceReplyEnabled: Boolean = false,
    val voiceInputEnabled: Boolean = true
)

@Serializable
data class ChatMessage(
    val id: Long = 0L,
    val role: String, // user/assistant
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class ChatRequest(
    val profile: BotProfile,
    val detectedLanguageTag: String,
    val userMessage: String,
    val history: List<ChatMessage> = emptyList()
)

@Serializable
data class ChatResponse(val reply: String)

@Serializable
data class ChatRequest(
    val message: String,        // ✅ MUST be "message"
    val profile: Profile,
    val historyWindow: List<ChatMsg> = emptyList()
)
