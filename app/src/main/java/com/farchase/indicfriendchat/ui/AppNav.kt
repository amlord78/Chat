package com.farchase.indicfriendchat.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.farchase.indicfriendchat.data.AppGraph
import com.farchase.indicfriendchat.ui.screens.ChatScreen
import com.farchase.indicfriendchat.ui.screens.SetupScreen

@Composable
fun AppNav() {
    val nav = rememberNavController()
    val graph = remember { AppGraph() }
    val ctx = LocalContext.current
    LaunchedEffect(Unit) { graph.init(ctx) }

    NavHost(navController = nav, startDestination = "setup") {
        composable("setup") { SetupScreen(graph = graph, onDone = { nav.navigate("chat") }) }
        composable("chat") { ChatScreen(graph = graph) }
    }
}
