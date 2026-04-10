package com.example.muniar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import com.example.muniar.ui.theme.IndustrialTheme
import com.google.android.gms.maps.model.LatLng

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Industrial Look: Makes the app go behind status/nav bars
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            IndustrialTheme {
                val context = LocalContext.current
                val session = remember { SessionManager(context) }

                var currentScreen by rememberSaveable { mutableStateOf("login") }
                var isOnlineUser by remember { mutableStateOf(false) }
                var currentUser by remember { mutableStateOf<User?>(null) }
                var selectedProject by rememberSaveable { mutableStateOf<String?>(null) }
                var selectedLocation by remember { mutableStateOf<LatLng?>(null) }

                LaunchedEffect(Unit) {
                    when {
                        session.isLoggedIn() -> {
                            val username = session.getUsername()
                            currentUser = FakeAuth.login(username!!, username)
                            isOnlineUser = true
                            currentScreen = "project"
                        }
                        session.isOffline() -> {
                            isOnlineUser = false
                            currentScreen = "project"
                        }
                    }
                }

                // 🔥 FIXED: Surface with correct Modifier and Padding
                Surface(
                    modifier = Modifier
                        .fillMaxSize() // Requires import androidx.compose.foundation.layout.fillMaxSize
                        .systemBarsPadding(), // Requires import androidx.compose.foundation.layout.systemBarsPadding
                    color = MaterialTheme.colorScheme.background
                ) {
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            val enterTrans = slideInHorizontally(animationSpec = tween(400)) { width -> width } +
                                    fadeIn(animationSpec = tween(400))

                            val exitTrans = slideOutHorizontally(animationSpec = tween(400)) { width -> -width } +
                                    fadeOut(animationSpec = tween(400))

                            enterTrans togetherWith exitTrans
                        },
                        label = "WaveT_Navigation"
                    ) { screen ->
                        when (screen) {
                            "login" -> LoginScreen(
                                onLoginSuccess = { user ->
                                    session.saveLogin(user.username)
                                    currentUser = user
                                    isOnlineUser = true
                                    currentScreen = "project"
                                },
                                onContinueOffline = {
                                    session.saveOffline()
                                    currentUser = null
                                    isOnlineUser = false
                                    currentScreen = "project"
                                }
                            )

                            "project" -> ProjectAreaScreen(
                                isOnlineUser = isOnlineUser,
                                onProjectSelected = { projectName ->
                                    selectedProject = projectName
                                    currentScreen = "map"
                                },
                                onLogout = {
                                    session.logout()
                                    currentUser = null
                                    currentScreen = "login"
                                },
                                onProfileClick = { currentScreen = "profile" }
                            )

                            "profile" -> {
                                BackHandler { currentScreen = "project" }
                                ProfileScreen(
                                    user = currentUser,
                                    onBack = { currentScreen = "project" },
                                    onLogout = {
                                        session.logout()
                                        currentUser = null
                                        currentScreen = "login"
                                    }
                                )
                            }

                            "map" -> {
                                BackHandler { currentScreen = "project" }
                                selectedProject?.let { projectName ->
                                    MapScreen(
                                        projectName = projectName,
                                        onAddHazard = { latLng ->
                                            selectedLocation = latLng
                                            currentScreen = "hazard"
                                        },
                                        onBack = { currentScreen = "project" }
                                    )
                                }
                            }

                            "hazard" -> {
                                BackHandler { currentScreen = "map" }
                                selectedLocation?.let { location ->
                                    HazardReportScreen(
                                        location = location,
                                        onBack = { currentScreen = "map" }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}