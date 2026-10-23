package com.example.ui.screens.addons

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.NovaStreamViewModel
import com.example.ui.theme.CinemaBackground
import com.example.ui.theme.CinemaSurfaceHighlight
import com.example.ui.theme.CinemaSurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AddonCreatorScreen(
    viewModel: NovaStreamViewModel,
    modifier: Modifier = Modifier
) {
    var addonId by remember { mutableStateOf("com.community.mycustomstream") }
    var addonName by remember { mutableStateOf("Decentralized Cinema Node") }
    var addonVersion by remember { mutableStateOf("1.0.0") }
    var addonDesc by remember { mutableStateOf("Community decentralized media catalog and stream endpoint.") }
    var catalogName by remember { mutableStateOf("Featured Node Streams") }

    val availableTypes = listOf("movie", "series", "channel", "other")
    val selectedTypes = remember { mutableStateListOf("movie", "series") }

    val availableResources = listOf("catalog", "meta", "stream", "subtitles")
    val selectedResources = remember { mutableStateListOf("catalog", "meta", "stream") }

    val jsonPreview = remember(addonId, addonName, addonVersion, addonDesc, catalogName, selectedTypes.toList(), selectedResources.toList()) {
        """
        {
          "id": "$addonId",
          "name": "$addonName",
          "version": "$addonVersion",
          "description": "$addonDesc",
          "types": ${selectedTypes.joinToString(prefix = "[", postfix = "]") { "\"$it\"" }},
          "resources": ${selectedResources.joinToString(prefix = "[", postfix = "]") { "\"$it\"" }},
          "catalogs": [
            {
              "type": "${selectedTypes.firstOrNull() ?: "movie"}",
              "id": "node_catalog",
              "name": "$catalogName"
            }
          ]
        }
        """.trimIndent()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaBackground)
            .testTag("addon_creator_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(CinemaSurfaceHighlight)
                        .testTag("creator_back_button")
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
                        text = "Addon Manifest Creator",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Build Stremio v3 Compatible Decentralized Addon",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Form Fields
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CinemaSurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Manifest Configuration",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Addon ID", color = TextSecondary, fontSize = 11.sp)
                    OutlinedTextField(
                        value = addonId,
                        onValueChange = { addonId = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = CinemaSurfaceHighlight,
                            unfocusedContainerColor = CinemaSurfaceHighlight,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedIndicatorColor = ElectricCyan
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Display Name", color = TextSecondary, fontSize = 11.sp)
                    OutlinedTextField(
                        value = addonName,
                        onValueChange = { addonName = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = CinemaSurfaceHighlight,
                            unfocusedContainerColor = CinemaSurfaceHighlight,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedIndicatorColor = ElectricCyan
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Version", color = TextSecondary, fontSize = 11.sp)
                    OutlinedTextField(
                        value = addonVersion,
                        onValueChange = { addonVersion = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = CinemaSurfaceHighlight,
                            unfocusedContainerColor = CinemaSurfaceHighlight,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedIndicatorColor = ElectricCyan
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Description", color = TextSecondary, fontSize = 11.sp)
                    OutlinedTextField(
                        value = addonDesc,
                        onValueChange = { addonDesc = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = CinemaSurfaceHighlight,
                            unfocusedContainerColor = CinemaSurfaceHighlight,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedIndicatorColor = ElectricCyan
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Media Types checkboxes
                    Text("Supported Media Types", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableTypes.forEach { type ->
                            val isChecked = selectedTypes.contains(type)
                            FilterChip(
                                selected = isChecked,
                                onClick = {
                                    if (isChecked) selectedTypes.remove(type) else selectedTypes.add(type)
                                },
                                label = { Text(type.uppercase(), fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ElectricCyan,
                                    selectedLabelColor = Color.Black,
                                    containerColor = CinemaSurfaceHighlight,
                                    labelColor = TextPrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Resources checkboxes
                    Text("Supported Resources", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableResources.forEach { res ->
                            val isChecked = selectedResources.contains(res)
                            FilterChip(
                                selected = isChecked,
                                onClick = {
                                    if (isChecked) selectedResources.remove(res) else selectedResources.add(res)
                                },
                                label = { Text(res.uppercase(), fontSize = 11.sp) },
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

        // Live JSON Preview Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CinemaSurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Live Manifest JSON (v3 Protocol)",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = { viewModel.copyToClipboard("Addon Manifest JSON", jsonPreview) },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy JSON", tint = ElectricCyan, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF090D17), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = jsonPreview,
                            color = ElectricCyan,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Action Buttons: Install & Register
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.createCustomAddon(
                            id = addonId,
                            name = addonName,
                            version = addonVersion,
                            description = addonDesc,
                            types = selectedTypes.toList(),
                            resources = selectedResources.toList(),
                            catalogName = catalogName
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(46.dp).testTag("save_custom_addon_button")
                ) {
                    Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Register & Install Addon", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
