package com.example.ui.screens.addons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.NovaStreamViewModel
import com.example.ui.Screen
import com.example.ui.components.AddonCard
import com.example.ui.theme.CinemaBackground
import com.example.ui.theme.CinemaRed
import com.example.ui.theme.CinemaSurfaceHighlight
import com.example.ui.theme.CinemaSurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.StreamGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CommunityAddonsScreen(
    viewModel: NovaStreamViewModel,
    modifier: Modifier = Modifier
) {
    val installedAddons by viewModel.installedAddons.collectAsState()
    val communityRepos by viewModel.communityRepos.collectAsState()
    val addonUrlInput by viewModel.addonUrlInput.collectAsState()
    val installState by viewModel.addonInstallState.collectAsState()
    val isInstalling by viewModel.isInstallingAddon.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    var pingResultDialogText by remember { mutableStateOf<String?>(null) }
    var showAddRepoDialog by remember { mutableStateOf(false) }

    val tabs = listOf("Installed", "Community Repos", "Install by URL")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaBackground)
            .testTag("community_addons_screen")
    ) {
        // Screen Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CinemaSurfaceHighlight)
                    .testTag("addons_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Community Addons",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Decentralized Streaming Protocol",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Addon Creator button
            Button(
                onClick = { viewModel.navigateTo(Screen.AddonCreator) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonViolet,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(36.dp).testTag("create_addon_button")
            ) {
                Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Creator", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Tabs Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = CinemaSurfaceVariant,
            contentColor = ElectricCyan,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = ElectricCyan
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp,
                            color = if (selectedTab == index) ElectricCyan else TextSecondary
                        )
                    }
                )
            }
        }

        // Tab Contents
        when (selectedTab) {
            0 -> {
                // Installed Addons
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${installedAddons.size} Installed Addons",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "${installedAddons.count { it.isEnabled }} Active",
                                color = StreamGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    items(installedAddons) { addon ->
                        AddonCard(
                            addon = addon,
                            onToggle = { viewModel.toggleAddon(addon.manifest.id) },
                            onUninstall = { viewModel.uninstallAddon(addon.manifest.id) },
                            onTestPing = {
                                viewModel.testAddon(addon.transportUrl) { msg ->
                                    pingResultDialogText = msg
                                }
                            }
                        )
                    }
                }
            }
            1 -> {
                // Community Repositories
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Federated Repositories",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            OutlinedButton(
                                onClick = { showAddRepoDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(34.dp).testTag("add_custom_repo_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Repo", fontSize = 11.sp)
                            }
                        }
                    }

                    items(communityRepos) { repo ->
                        Card(
                            modifier = Modifier.fillMaxWidth().testTag("repo_card_${repo.id}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CinemaSurfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Public,
                                        contentDescription = null,
                                        tint = ElectricCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = repo.name,
                                        color = TextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (repo.isOfficial) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .background(ElectricCyan.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "VERIFIED",
                                                color = ElectricCyan,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = repo.description,
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = repo.url,
                                    color = NeonViolet,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
            2 -> {
                // Install by URL
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CinemaSurfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Install Decentralized Addon",
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Enter any Stremio v3 protocol manifest URL or IPFS gateway URL (e.g., https://.../manifest.json or stremio://...)",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedTextField(
                                    value = addonUrlInput,
                                    onValueChange = { viewModel.setAddonUrlInput(it) },
                                    placeholder = { Text("https://my-addon.decentralized.stream/manifest.json", fontSize = 12.sp, color = TextSecondary) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("addon_url_input"),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = CinemaSurfaceHighlight,
                                        unfocusedContainerColor = CinemaSurfaceHighlight,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedIndicatorColor = ElectricCyan
                                    )
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.installAddonFromInput() },
                                        enabled = addonUrlInput.isNotBlank() && !isInstalling,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = ElectricCyan,
                                            contentColor = Color.Black
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).testTag("install_addon_button")
                                    ) {
                                        if (isInstalling) {
                                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(16.dp))
                                        } else {
                                            Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Install Addon", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            viewModel.testAddon(addonUrlInput) { msg ->
                                                pingResultDialogText = msg
                                            }
                                        },
                                        enabled = addonUrlInput.isNotBlank(),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.testTag("test_addon_url_button")
                                    ) {
                                        Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Test Ping", fontSize = 12.sp)
                                    }
                                }

                                if (installState != null) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = installState ?: "",
                                        color = if (installState!!.startsWith("Failed")) CinemaRed else StreamGreen,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    // Popular Community Addon Presets to quickly try
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Popular Community Presets",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val presets = listOf(
                            Pair("Cinemeta Worldwide v3", "https://v3-cinemeta.strem.io/manifest.json"),
                            Pair("Open Subtitles v3", "https://opensubtitles-v3.strem.io/manifest.json"),
                            Pair("Anime Kitsu Catalog", "https://anime-kitsu.strem.fun/manifest.json"),
                            Pair("Cyberflix Community", "https://cyberflix.kodi.tv/manifest.json")
                        )

                        presets.forEach { (name, url) ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { viewModel.setAddonUrlInput(url) },
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = CinemaSurfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(text = url, color = TextSecondary, fontSize = 10.sp)
                                    }
                                    Text(
                                        text = "USE",
                                        color = ElectricCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Ping Result Dialog
    if (pingResultDialogText != null) {
        AlertDialog(
            onDismissRequest = { pingResultDialogText = null },
            confirmButton = {
                TextButton(onClick = { pingResultDialogText = null }) {
                    Text("OK", color = ElectricCyan)
                }
            },
            title = { Text("Addon Endpoint Status", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text(pingResultDialogText ?: "", color = TextSecondary, fontSize = 13.sp) },
            containerColor = CinemaSurfaceVariant
        )
    }
}
