package com.example.ui.screens.playback

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import com.example.player.AspectRatioMode
import com.example.player.PlaybackSettingsState
import com.example.player.SubtitleBackground
import com.example.player.SubtitleColor
import com.example.player.SubtitleSize
import com.example.ui.theme.CinemaSurfaceHighlight
import com.example.ui.theme.CinemaSurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaybackSettingsSheet(
    settings: PlaybackSettingsState,
    onSettingsChanged: (PlaybackSettingsState) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Subtitles", "Audio", "Playback")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CinemaSurfaceVariant,
        contentColor = TextPrimary,
        modifier = Modifier.testTag("playback_settings_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Player Settings",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Tab Row
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
                                fontSize = 13.sp,
                                color = if (selectedTab == index) ElectricCyan else TextSecondary
                            )
                        }
                    )
                }
            }

            // Content
            when (selectedTab) {
                0 -> SubtitlesTabContent(settings, onSettingsChanged)
                1 -> AudioTabContent(settings, onSettingsChanged)
                2 -> PlaybackTabContent(settings, onSettingsChanged)
            }
        }
    }
}

@Composable
private fun SubtitlesTabContent(
    settings: PlaybackSettingsState,
    onUpdate: (PlaybackSettingsState) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Subtitle Live Preview Box
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "LIVE SUBTITLE PREVIEW",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(settings.subtitleBackground.color)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (settings.selectedSubtitleTrackIndex == 0) "[Subtitles Disabled]" else "“The decentralized stream has connected.”",
                            color = if (settings.selectedSubtitleTrackIndex == 0) TextSecondary else settings.subtitleColor.color,
                            fontSize = settings.subtitleSize.spSize.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Subtitle Track Selection
        item {
            Text("Subtitle Language Track", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                settings.subtitleTracks.forEachIndexed { index, trackName ->
                    val isSelected = settings.selectedSubtitleTrackIndex == index
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onUpdate(settings.copy(selectedSubtitleTrackIndex = index, subtitlesEnabled = index > 0))
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) CinemaSurfaceHighlight else Color.Transparent
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = trackName,
                                color = if (isSelected) ElectricCyan else TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.weight(1f)
                            )
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        // Subtitle Sync / Delay Adjuster
        item {
            Text("Subtitle Sync Delay", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CinemaSurfaceHighlight, RoundedCornerShape(10.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onUpdate(settings.copy(subtitleDelayMs = settings.subtitleDelayMs - 100)) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Minus 100ms", tint = ElectricCyan)
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "${if (settings.subtitleDelayMs > 0) "+" else ""}${settings.subtitleDelayMs} ms",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = { onUpdate(settings.copy(subtitleDelayMs = settings.subtitleDelayMs + 100)) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Plus 100ms", tint = ElectricCyan)
                }

                IconButton(
                    onClick = { onUpdate(settings.copy(subtitleDelayMs = 0)) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = "Reset Delay", tint = TextSecondary)
                }
            }
        }

        // Subtitle Font Size
        item {
            Text("Subtitle Text Size", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SubtitleSize.values().forEach { size ->
                    val isSelected = settings.subtitleSize == size
                    FilterChip(
                        selected = isSelected,
                        onClick = { onUpdate(settings.copy(subtitleSize = size)) },
                        label = { Text(size.label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricCyan,
                            selectedLabelColor = Color.Black,
                            containerColor = CinemaSurfaceHighlight,
                            labelColor = TextPrimary
                        )
                    )
                }
            }
        }

        // Subtitle Color Chips
        item {
            Text("Subtitle Color", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SubtitleColor.values().forEach { item ->
                    val isSelected = settings.subtitleColor == item
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) CinemaSurfaceHighlight else Color.Transparent)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) ElectricCyan else Color(0xFF2A3650),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onUpdate(settings.copy(subtitleColor = item)) }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(item.color)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = item.label, color = TextPrimary, fontSize = 11.sp)
                    }
                }
            }
        }

        // Subtitle Background / Box
        item {
            Text("Subtitle Background Box", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SubtitleBackground.values().forEach { bg ->
                    val isSelected = settings.subtitleBackground == bg
                    FilterChip(
                        selected = isSelected,
                        onClick = { onUpdate(settings.copy(subtitleBackground = bg)) },
                        label = { Text(bg.label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonViolet,
                            selectedLabelColor = Color.White,
                            containerColor = CinemaSurfaceHighlight,
                            labelColor = TextPrimary
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun AudioTabContent(
    settings: PlaybackSettingsState,
    onUpdate: (PlaybackSettingsState) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Audio Track Selector
        item {
            Text("Audio Stream & Language Track", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                settings.audioTracks.forEachIndexed { index, trackName ->
                    val isSelected = settings.selectedAudioTrackIndex == index
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onUpdate(settings.copy(selectedAudioTrackIndex = index)) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) CinemaSurfaceHighlight else Color.Transparent
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = if (isSelected) ElectricCyan else TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = trackName,
                                color = if (isSelected) ElectricCyan else TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.weight(1f)
                            )
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        // Audio Delay Sync Adjuster
        item {
            Text("Audio Sync Offset", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CinemaSurfaceHighlight, RoundedCornerShape(10.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onUpdate(settings.copy(audioDelayMs = settings.audioDelayMs - 50)) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Minus 50ms", tint = ElectricCyan)
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "${if (settings.audioDelayMs > 0) "+" else ""}${settings.audioDelayMs} ms",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = { onUpdate(settings.copy(audioDelayMs = settings.audioDelayMs + 50)) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Plus 50ms", tint = ElectricCyan)
                }

                IconButton(
                    onClick = { onUpdate(settings.copy(audioDelayMs = 0)) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = "Reset Delay", tint = TextSecondary)
                }
            }
        }

        // Dialogue Enhancement Toggle
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = CinemaSurfaceHighlight)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Hearing, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Speech & Dialogue Enhancement", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Boosts speech clarity over background effects and loud soundtracks", color = TextSecondary, fontSize = 11.sp)
                    }
                    Switch(
                        checked = settings.dialogueEnhanceEnabled,
                        onCheckedChange = { onUpdate(settings.copy(dialogueEnhanceEnabled = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = ElectricCyan, checkedTrackColor = ElectricCyan.copy(alpha = 0.3f))
                    )
                }
            }
        }

        // Audio Boost Toggle
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = CinemaSurfaceHighlight)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.GraphicEq, contentDescription = null, tint = NeonViolet, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Audio Volume Boost (+6 dB)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Increases overall output for quiet or vintage streams", color = TextSecondary, fontSize = 11.sp)
                    }
                    Switch(
                        checked = settings.audioBoostEnabled,
                        onCheckedChange = { onUpdate(settings.copy(audioBoostEnabled = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = NeonViolet, checkedTrackColor = NeonViolet.copy(alpha = 0.3f))
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaybackTabContent(
    settings: PlaybackSettingsState,
    onUpdate: (PlaybackSettingsState) -> Unit
) {
    val speedOptions = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f)
    val sleepOptions = listOf(0 to "Off", 15 to "15 min", 30 to "30 min", 45 to "45 min", 60 to "60 min")

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Playback Speed
        item {
            Text("Playback Speed", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                speedOptions.forEach { sp ->
                    val isSelected = settings.playbackSpeed == sp
                    FilterChip(
                        selected = isSelected,
                        onClick = { onUpdate(settings.copy(playbackSpeed = sp)) },
                        label = { Text("${sp}x", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricCyan,
                            selectedLabelColor = Color.Black,
                            containerColor = CinemaSurfaceHighlight,
                            labelColor = TextPrimary
                        )
                    )
                }
            }
        }

        // Aspect Ratio / Screen Fit
        item {
            Text("Aspect Ratio / Sizing", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AspectRatioMode.values().forEach { mode ->
                    val isSelected = settings.aspectRatioMode == mode
                    FilterChip(
                        selected = isSelected,
                        onClick = { onUpdate(settings.copy(aspectRatioMode = mode)) },
                        label = { Text(mode.label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonViolet,
                            selectedLabelColor = Color.White,
                            containerColor = CinemaSurfaceHighlight,
                            labelColor = TextPrimary
                        )
                    )
                }
            }
        }

        // Sleep Timer
        item {
            Text("Sleep Timer", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sleepOptions.forEach { (mins, label) ->
                    val isSelected = settings.sleepTimerMinutes == mins
                    FilterChip(
                        selected = isSelected,
                        onClick = { onUpdate(settings.copy(sleepTimerMinutes = mins)) },
                        label = { Text(label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricCyan,
                            selectedLabelColor = Color.Black,
                            containerColor = CinemaSurfaceHighlight,
                            labelColor = TextPrimary
                        )
                    )
                }
            }
        }

        // Loop Mode Switch
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = CinemaSurfaceHighlight)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Repeat Video (Loop)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Continuously replay stream when playback ends", color = TextSecondary, fontSize = 11.sp)
                    }
                    Switch(
                        checked = settings.loopMode,
                        onCheckedChange = { onUpdate(settings.copy(loopMode = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = ElectricCyan, checkedTrackColor = ElectricCyan.copy(alpha = 0.3f))
                    )
                }
            }
        }

        // Stream Quality Selection
        item {
            Text("Stream Video Quality", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                settings.availableQualities.forEach { q ->
                    val isSelected = settings.streamQuality == q
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onUpdate(settings.copy(streamQuality = q)) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) CinemaSurfaceHighlight else Color.Transparent
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = q,
                                color = if (isSelected) ElectricCyan else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.weight(1f)
                            )
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
