package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.WatchProgress
import com.example.ui.NovaStreamViewModel
import com.example.ui.Screen
import com.example.ui.components.HeroBanner
import com.example.ui.components.MediaCard
import com.example.ui.theme.CinemaBackground
import com.example.ui.theme.CinemaSurfaceHighlight
import com.example.ui.theme.CinemaSurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    viewModel: NovaStreamViewModel,
    modifier: Modifier = Modifier
) {
    val catalogs by viewModel.catalogs.collectAsState()
    val featuredMedia by viewModel.featuredMedia.collectAsState()
    val watchHistory by viewModel.watchHistory.collectAsState()
    val selectedFilter by viewModel.selectedTypeFilter.collectAsState()
    val installedAddons by viewModel.installedAddons.collectAsState()
    val enabledAddonsCount = installedAddons.count { it.isEnabled }

    val filterOptions = listOf("All", "Movies", "Series", "Live TV")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaBackground)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // App Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo & Title
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ElectricCyan),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "NovaStream",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Decentralized Streaming Hub",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Active Addons indicator button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(CinemaSurfaceHighlight)
                        .clickable { viewModel.navigateTo(Screen.CommunityAddons) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("active_addons_badge")
                ) {
                    Icon(
                        imageVector = Icons.Default.Extension,
                        contentDescription = "Addons",
                        tint = ElectricCyan,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "$enabledAddonsCount Active",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Quick Search icon
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.Search) },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CinemaSurfaceHighlight)
                        .testTag("header_search_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Filter chips row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterOptions.forEach { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setTypeFilter(filter) },
                        label = {
                            Text(
                                text = filter,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricCyan,
                            selectedLabelColor = Color.Black,
                            containerColor = CinemaSurfaceVariant,
                            labelColor = TextPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = Color(0xFF2A3650),
                            selectedBorderColor = ElectricCyan
                        )
                    )
                }
            }
        }

        // Hero Banner
        item {
            val featured = featuredMedia.firstOrNull()
            Box(modifier = Modifier.padding(16.dp)) {
                HeroBanner(
                    featured = featured,
                    onPlayClick = {
                        if (featured != null) {
                            viewModel.openMediaDetail(featured.id, featured.type)
                        }
                    },
                    onDetailsClick = {
                        if (featured != null) {
                            viewModel.openMediaDetail(featured.id, featured.type)
                        }
                    }
                )
            }
        }

        // Continue Watching section (if any)
        if (watchHistory.isNotEmpty()) {
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Text(
                        text = "Continue Watching",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(watchHistory) { progress ->
                            ContinueWatchingCard(
                                progress = progress,
                                onClick = {
                                    viewModel.openMediaDetail(progress.mediaId, progress.type)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Dynamic Catalogs by Addon / Category
        catalogs.forEach { (categoryTitle, items) ->
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = categoryTitle,
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "${items.size} titles",
                            color = NeonViolet,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        items(items) { media ->
                            MediaCard(
                                media = media,
                                onClick = { viewModel.openMediaDetail(media.id, media.type) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContinueWatchingCard(
    progress: WatchProgress,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .clickable(onClick = onClick)
            .testTag("continue_card_${progress.mediaId}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CinemaSurfaceVariant)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .background(Color(0xFF131A2B))
            ) {
                if (!progress.poster.isNullOrBlank()) {
                    AsyncImage(
                        model = progress.poster,
                        contentDescription = progress.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Play icon overlay
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.65f))
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Resume",
                        tint = ElectricCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Progress Bar at bottom
                LinearProgressIndicator(
                    progress = { progress.progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .align(Alignment.BottomCenter),
                    color = ElectricCyan,
                    trackColor = Color.White.copy(alpha = 0.2f),
                )
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = progress.title,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (progress.season != null && progress.episode != null) {
                        "S${progress.season}:E${progress.episode} • Resume"
                    } else {
                        "Resume playback"
                    },
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}
