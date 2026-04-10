package com.example.muniar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muniar.ui.theme.IndustrialBlack
import com.example.muniar.ui.theme.IndustrialPrimary
import com.example.muniar.ui.theme.IndustrialSurface
import com.example.muniar.ui.theme.MutedGrey

data class Project(
    val name: String,
    val distance: String,
    val size: String,
    val isDownloaded: Boolean = false,
    val isAvailable: Boolean = false,
    val type: String = "General"
)

@Composable
fun ProjectAreaScreen(
    isOnlineUser: Boolean,
    onProjectSelected: (String) -> Unit,
    onLogout: () -> Unit,
    onProfileClick: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    var searchQuery by remember { mutableStateOf("") }
    var projects by remember { mutableStateOf(emptyList<Project>()) }

    fun loadProjects() {
        // Location-Based Sorting simulation (nearest first)
        projects = listOf(
            Project("WCTM Project", "0.2 km", "2.0 GB", isAvailable = true, type = "Water"),
            Project("Sector 9 Water Line", "0.5 km", "1.1 GB", isAvailable = true, type = "Water"),
            Project("Sector 21 Road Repair", "1.8 km", "1.2 GB", type = "Road"),
            Project("Highway Fibre Install", "4.2 km", "900 MB", type = "Fibre"),
            Project("DLF Phase-3 Gas Line", "12.0 km", "1.5 GB", type = "Gas"),
            Project("Downtown Water Main", "15.5 km", "800 MB", type = "Water")
        ).map {
            it.copy(isDownloaded = sessionManager.isProjectDownloaded(it.name))
        }
    }

    LaunchedEffect(isOnlineUser) {
        loadProjects()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IndustrialBlack)
    ) {
        // TOP NAV - Anchored and Oversized
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "PROJECTS",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
            
            IconButton(
                onClick = onProfileClick,
                modifier = Modifier.size(56.dp) // Large touch target
            ) {
                Icon(Icons.Default.SyncAlt, "Sync Status", tint = IndustrialPrimary, modifier = Modifier.size(32.dp))
            }
        }

        // SEARCH - High Contrast
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("FILTER BY NAME OR ID", color = MutedGrey) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = IndustrialPrimary) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .heightIn(min = 64.dp),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = IndustrialPrimary,
                unfocusedBorderColor = Color.Gray,
                focusedContainerColor = IndustrialSurface,
                unfocusedContainerColor = IndustrialSurface
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp) // Space for bottom nav
        ) {
            items(projects.filter { it.name.contains(searchQuery, ignoreCase = true) }) { project ->
                IndustrialProjectCard(
                    project = project,
                    onClick = {
                        if (!project.isAvailable) return@IndustrialProjectCard
                        if (project.isDownloaded) {
                            onProjectSelected(project.name)
                        } else if (isOnlineUser) {
                            sessionManager.saveDownloadedProject(project.name)
                            loadProjects()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun IndustrialProjectCard(
    project: Project,
    onClick: () -> Unit
) {
    val isReady = project.isDownloaded
    val borderColor = if (isReady) IndustrialPrimary else Color.Transparent

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, borderColor, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(
            containerColor = IndustrialSurface,
            disabledContainerColor = IndustrialBlack
        ),
        enabled = project.isAvailable,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .heightIn(min = 64.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with type-based color
            val iconColor = when (project.type) {
                "Water" -> Color(0xFF2196F3)
                "Gas" -> Color(0xFFFFEB3B)
                "Fibre" -> Color(0xFF9C27B0)
                else -> MutedGrey
            }

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(iconColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when(project.type) {
                        "Water" -> Icons.Default.WaterDrop
                        "Gas" -> Icons.Default.LocalGasStation
                        "Fibre" -> Icons.Default.SettingsInputAntenna
                        else -> Icons.Default.Construction
                    },
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (project.isAvailable) Color.White else MutedGrey,
                    fontWeight = FontWeight.Bold
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Place, null, tint = IndustrialPrimary, modifier = Modifier.size(16.dp))
                    Text(
                        text = "${project.distance} • ${project.size}",
                        style = MaterialTheme.typography.labelLarge,
                        color = IndustrialPrimary
                    )
                }
            }

            // Sync Status Toggle/Indicator
            if (project.isAvailable) {
                Icon(
                    imageVector = if (isReady) Icons.Default.CheckCircle else Icons.Default.CloudDownload,
                    contentDescription = null,
                    tint = if (isReady) IndustrialPrimary else MutedGrey,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
