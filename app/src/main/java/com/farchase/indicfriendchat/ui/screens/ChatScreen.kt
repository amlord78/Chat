package com.farchase.indicfriendchat.ui.screens

import android.Manifest
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.farchase.indicfriendchat.data.AppGraph
import com.farchase.indicfriendchat.data.ChatMessage
import com.farchase.indicfriendchat.ui.vm.ChatViewModel
import java.util.Locale

@Composable
fun ChatScreen(graph: AppGraph) {
    val ctx = LocalContext.current
    val vm = remember { ChatViewModel(ctx, graph.profileStore, graph.repo) }
    LaunchedEffect(Unit) { vm.loadProfile() }

    val ui by vm.ui.collectAsState()
    val msgs by vm.messages.collectAsState()

    var input by remember { mutableStateOf("") }

    // ✅ TTS (safe)
    val ttsRef = remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(ctx) {
        val tts = TextToSpeech(ctx) { /* ignore */ }
        ttsRef.value = tts
        onDispose {
            try { ttsRef.value?.stop() } catch (_: Exception) {}
            try { ttsRef.value?.shutdown() } catch (_: Exception) {}
            ttsRef.value = null
        }
    }

    fun speak(text: String) {
        val tts = ttsRef.value ?: return
        try {
            tts.language = Locale.getDefault()
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "BOT_REPLY")
        } catch (_: Exception) { }
    }

    // Auto speak last assistant reply if enabled
    LaunchedEffect(msgs.size, ui.profile.voiceReplyEnabled) {
        if (!ui.profile.voiceReplyEnabled) return@LaunchedEffect
        val last = msgs.lastOrNull() ?: return@LaunchedEffect
        if (last.role == "assistant") speak(last.content)
    }

    // Mic permission
    var hasMicPermission by remember { mutableStateOf(false) }
    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasMicPermission = granted
    }

    // Voice recognition
    val voiceLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        val spokenText = matches?.firstOrNull()
        if (!spokenText.isNullOrBlank()) input = spokenText
    }

    fun startVoiceInput() {
        if (!hasMicPermission) {
            permLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak…")
        }
        voiceLauncher.launch(intent)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(ui.profile.botName, fontWeight = FontWeight.Bold)
                        Text("State + Language + Voice", style = MaterialTheme.typography.labelMedium)
                    }
                },
                actions = {
                    IconButton(onClick = { vm.clearChat() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear chat")
                    }
                }
            )
        }
    ) { pad ->
        Column(modifier = Modifier.padding(pad).fillMaxSize()) {
            ui.error?.let {
                Text(
                    "Error: $it",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(12.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(msgs) { m -> MessageBubble(m) }
                if (ui.isSending) item { TypingBubble(ui.profile.botName) }
            }

            Divider()

            Row(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text("Type a message…") },
                    modifier = Modifier.weight(1f)
                )

                if (ui.profile.voiceInputEnabled) {
                    IconButton(onClick = { startVoiceInput() }) {
                        Icon(Icons.Default.Mic, contentDescription = "Voice input")
                    }
                }

                IconButton(onClick = {
                    val last = msgs.lastOrNull()
                    if (last != null && last.role == "assistant") speak(last.content)
                }) {
                    Icon(Icons.Default.VolumeUp, contentDescription = "Speak last reply")
                }

                Spacer(Modifier.width(6.dp))

                Button(
                    onClick = {
                        val t = input.trim()
                        if (t.isNotEmpty()) vm.send(t)
                        input = ""
                    },
                    enabled = !ui.isSending
                ) { Text("Send") }
            }
        }
    }
}

@Composable
private fun MessageBubble(m: ChatMessage) {
    val isUser = m.role == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(shape = RoundedCornerShape(16.dp), tonalElevation = 1.dp) {
            Text(m.content, modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp))
        }
    }
}

@Composable
private fun TypingBubble(botName: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Surface(shape = RoundedCornerShape(16.dp), tonalElevation = 1.dp) {
            Text("$botName is typing…", modifier = Modifier.padding(12.dp))
        }
    }
}
