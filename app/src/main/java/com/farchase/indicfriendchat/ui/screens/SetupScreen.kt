package com.farchase.indicfriendchat.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.farchase.indicfriendchat.data.AppGraph
import com.farchase.indicfriendchat.data.FriendType
import com.farchase.indicfriendchat.data.IndianState
import com.farchase.indicfriendchat.data.ScriptPreference
import com.farchase.indicfriendchat.ui.vm.SetupViewModel

@Composable
fun SetupScreen(graph: AppGraph, onDone: () -> Unit) {
    val vm = remember { SetupViewModel(graph.profileStore) }
    LaunchedEffect(Unit) { vm.load() }
    val ui by vm.ui.collectAsState()

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        vm.setAvatar(uri?.toString())
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Set up your AI friend") }) }) { pad ->
        Column(
            modifier = Modifier.padding(pad).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Bot Profile", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                val avatar = ui.avatarUri
                Surface(
                    modifier = Modifier.size(72.dp).clip(CircleShape).clickable { imagePicker.launch("image/*") },
                    tonalElevation = 2.dp
                ) {
                    if (avatar != null) {
                        Image(painter = rememberAsyncImagePainter(avatar), contentDescription = "avatar", modifier = Modifier.fillMaxSize())
                    } else {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Add\nPhoto") }
                    }
                }

                OutlinedTextField(
                    value = ui.botName,
                    onValueChange = vm::setName,
                    label = { Text("Bot name") },
                    modifier = Modifier.weight(1f)
                )
            }

            StatePicker(selected = ui.selectedState, onSelect = vm::setState)
            FriendTypePicker(selected = ui.friendType, onSelect = vm::setFriendType)
            ScriptPrefPicker(selected = ui.scriptPreference, onSelect = vm::setScriptPreference)

            Text("Voice", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Bot speaks replies", modifier = Modifier.weight(1f))
                Switch(checked = ui.voiceReplyEnabled, onCheckedChange = vm::setVoiceReplyEnabled)
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Enable mic input", modifier = Modifier.weight(1f))
                Switch(checked = ui.voiceInputEnabled, onCheckedChange = vm::setVoiceInputEnabled)
            }

            Button(onClick = { vm.save(onDone) }, modifier = Modifier.fillMaxWidth()) { Text("Continue to Chat") }

            Text("Backend URL: data/net/Network.kt", style = MaterialTheme.typography.labelMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatePicker(selected: IndianState, onSelect: (IndianState) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected.display,
            onValueChange = {},
            readOnly = true,
            label = { Text("Your state") },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            IndianState.values().forEach { st ->
                DropdownMenuItem(text = { Text(st.display) }, onClick = { onSelect(st); expanded = false })
            }
        }
    }
}

@Composable
private fun FriendTypePicker(selected: FriendType, onSelect: (FriendType) -> Unit) {
    Text("Friend type", style = MaterialTheme.typography.titleMedium)
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        FriendType.values().forEach { ft ->
            val title = when (ft) {
                FriendType.FEMALE -> "Female friend (soft, caring)"
                FriendType.MALE -> "Male friend (buddy, casual)"
                FriendType.NEUTRAL -> "Neutral (default)"
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = (ft == selected), onClick = { onSelect(ft) })
                Text(title)
            }
        }
    }
}

@Composable
private fun ScriptPrefPicker(selected: ScriptPreference, onSelect: (ScriptPreference) -> Unit) {
    Text("Script preference", style = MaterialTheme.typography.titleMedium)
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        ScriptPreference.values().forEach { pref ->
            val title = when (pref) {
                ScriptPreference.MATCH_USER -> "Match user's style (recommended)"
                ScriptPreference.ALWAYS_NATIVE_SCRIPT -> "Always native script (हिन्दी/தமிழ்/తెలుగు etc.)"
                ScriptPreference.ALWAYS_LATIN -> "Always Latin letters (Hinglish/Tanglish)"
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = (pref == selected), onClick = { onSelect(pref) })
                Text(title)
            }
        }
    }
}
