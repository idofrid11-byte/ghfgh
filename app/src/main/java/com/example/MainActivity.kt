package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.NovaStreamViewModel
import com.example.ui.Screen
import androidx.lifecycle.lifecycleScope
import com.example.auth.AuthManager
import com.example.ui.screens.auth.SignInScreen
import com.example.ui.screens.addons.AddonCreatorScreen
import com.example.ui.screens.addons.CommunityAddonsScreen
import com.example.ui.screens.detail.MediaDetailScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.library.LibraryScreen
import com.example.ui.screens.playback.PlaybackScreen
import com.example.ui.screens.player.PlayerScreen
import com.example.ui.screens.search.SearchScreen
import com.example.ui.theme.CinemaBackground
import com.example.ui.theme.CinemaSurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NovaStreamTheme
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        authManager = AuthManager(this)
        authManager.attemptAutoSignIn(lifecycleScope)

        setContent {
            NovaStreamTheme {
                val currentUser by authManager.currentUser.collectAsState()
                if (currentUser == null) {
                    SignInScreen(
                        authManager = authManager,
                        onAuthSuccess = {}
                    )
                } else {
                    NovaStreamApp(
                        onSignOut = {
                            authManager.signOut(lifecycleScope) {}
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NovaStreamApp(
    onSignOut: () -> Unit = {},
    viewModel: NovaStreamViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    // Handle system back button for sub-screens
    BackHandler(enabled = currentScreen !is Screen.Discover) {
        viewModel.navigateBack()
    }

    val isFullScreenPlayer = currentScreen is Screen.Player

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = CinemaBackground,
        bottomBar = {
            // Hide bottom navigation bar when watching fullscreen stream
            AnimatedVisibility(
                visible = !isFullScreenPlayer,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                NavigationBar(
                    containerColor = CinemaSurfaceVariant,
                    contentColor = TextPrimary,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    // Discover Tab
                    NavigationBarItem(
                        selected = currentScreen is Screen.Discover,
                        onClick = { viewModel.navigateTo(Screen.Discover) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Explore,
                                contentDescription = "Discover",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Discover",
                                fontSize = 11.sp,
                                fontWeight = if (currentScreen is Screen.Discover) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = ElectricCyan,
                            indicatorColor = ElectricCyan,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_discover")
                    )

                    // Search Tab
                    NavigationBarItem(
                        selected = currentScreen is Screen.Search,
                        onClick = { viewModel.navigateTo(Screen.Search) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Search",
                                fontSize = 11.sp,
                                fontWeight = if (currentScreen is Screen.Search) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = ElectricCyan,
                            indicatorColor = ElectricCyan,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_search")
                    )

                    // Addons Tab
                    NavigationBarItem(
                        selected = currentScreen is Screen.CommunityAddons || currentScreen is Screen.AddonCreator,
                        onClick = { viewModel.navigateTo(Screen.CommunityAddons) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Extension,
                                contentDescription = "Addons",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Addons",
                                fontSize = 11.sp,
                                fontWeight = if (currentScreen is Screen.CommunityAddons) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = ElectricCyan,
                            indicatorColor = ElectricCyan,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_addons")
                    )

                    // Library Tab
                    NavigationBarItem(
                        selected = currentScreen is Screen.Library,
                        onClick = { viewModel.navigateTo(Screen.Library) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.VideoLibrary,
                                contentDescription = "Library",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Library",
                                fontSize = 11.sp,
                                fontWeight = if (currentScreen is Screen.Library) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = ElectricCyan,
                            indicatorColor = ElectricCyan,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_library")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isFullScreenPlayer) androidx.compose.foundation.layout.PaddingValues(0.dp) else innerPadding)
                .background(CinemaBackground)
        ) {
            when (currentScreen) {
                is Screen.Discover -> HomeScreen(viewModel = viewModel)
                is Screen.Search -> SearchScreen(viewModel = viewModel)
                is Screen.MediaDetail -> MediaDetailScreen(viewModel = viewModel)
                is Screen.Player -> {
                    val playerScreen = currentScreen as Screen.Player
                    val activeStream = viewModel.playerManager.playerState.collectAsState().value.activeStream
                    val streamUrl = activeStream?.streamPlayUrl?.takeIf { it.isNotBlank() }
                        ?: "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
                    PlaybackScreen(
                        streamUrl = streamUrl,
                        title = playerScreen.title,
                        onBack = { viewModel.navigateBack() }
                    )
                }
                is Screen.CommunityAddons -> CommunityAddonsScreen(viewModel = viewModel)
                is Screen.AddonCreator -> AddonCreatorScreen(viewModel = viewModel)
                is Screen.Library -> LibraryScreen(viewModel = viewModel, onSignOut = onSignOut)
            }
        }
    }
}
