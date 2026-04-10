package com.example.muniar

import android.annotation.SuppressLint
import android.location.Location
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import com.example.muniar.ui.theme.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import java.util.Locale

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    projectName: String,
    onAddHazard: (LatLng) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var hazards by remember { mutableStateOf<List<HazardEntity>>(emptyList()) }
    var currentLocation by remember { mutableStateOf(LatLng(28.4436, 76.7723)) }
    var gpsAccuracy by remember { mutableStateOf(0.5f) } // Simulated ±0.5m
    
    var selectedUtility by remember { mutableStateOf<UtilityLine?>(null) }
    var showLayersSheet by remember { mutableStateOf(false) }

    // Layer States
    var showWater by remember { mutableStateOf(true) }
    var showElectric by remember { mutableStateOf(true) }
    var showFibre by remember { mutableStateOf(true) }
    var showSewage by remember { mutableStateOf(true) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    // Dynamic Data Loading: Reactive to projectName change
    val utilities = remember(projectName) {
        when {
            projectName.contains("WCTM", ignoreCase = true) -> 
                JsonLoader.loadUtilities(context, "wctm_data.json")
            projectName.contains("Sector 9", ignoreCase = true) -> 
                JsonLoader.loadUtilities(context, "sector9_data.json")
            else -> emptyList()
        }
    }

    LaunchedEffect(Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener { loc: Location? ->
            loc?.let {
                currentLocation = LatLng(it.latitude, it.longitude)
                gpsAccuracy = it.accuracy
            }
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation, 19f)
    }

    // Camera Animation (LaunchedEffect): Animates map when project changes
    LaunchedEffect(projectName) {
        val target = when {
            projectName.contains("WCTM", ignoreCase = true) -> LatLng(28.4438, 76.7715)
            projectName.contains("Sector 9", ignoreCase = true) -> LatLng(28.4615, 77.0000)
            else -> null
        }
        
        target?.let {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(it, 19f),
                durationMs = 1500
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(IndustrialBlack)) {
        BackHandler { onBack() }

        // 🗺️ Muted Basemap (Von Restorff Effect)
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style),
                isMyLocationEnabled = true
            ),
            uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false),
            onMapClick = { selectedUtility = null }
        ) {
            // Draw Utilities with "Tap Buffers"
            utilities.forEach { utility ->
                val visible = when(utility.type) {
                    "WATER" -> showWater
                    "ELECTRIC" -> showElectric
                    "ETHERNET" -> showFibre
                    "SEWAGE" -> showSewage
                    else -> true
                }
                
                if (visible) {
                    // Hidden wider polyline for easier glove-tapping
                    Polyline(
                        points = utility.points,
                        color = Color.Transparent,
                        width = 40f,
                        clickable = true,
                        onClick = { selectedUtility = utility }
                    )
                    // Visible utility line
                    Polyline(
                        points = utility.points,
                        color = utility.color,
                        width = 12f,
                        startCap = com.google.android.gms.maps.model.RoundCap(),
                        endCap = com.google.android.gms.maps.model.RoundCap()
                    )
                }
            }
        }

        // 🛡️ GPS ACCURACY INDICATOR
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
                .background(IndustrialSurface.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                .border(1.dp, if (gpsAccuracy < 1.0f) Color.Green else Color.Red, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.GpsFixed,
                    null,
                    tint = if (gpsAccuracy < 1.0f) Color.Green else Color.Red,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "GPS ACCURACY: ±${String.format(Locale.US, "%.1f", gpsAccuracy)}m",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 🕹️ FLOATING QUICK ACTION - HAZARD (Oversized Red FAB)
        LargeFloatingActionButton(
            onClick = { onAddHazard(currentLocation) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 100.dp, end = 24.dp),
            containerColor = HazardRed,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Warning, contentDescription = "Quick Report", modifier = Modifier.size(36.dp))
        }

        // 📑 BOTTOM CONTROLS (Layer Toggle & Project Title)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = IndustrialSurface,
            tonalElevation = 8.dp,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            projectName.uppercase(Locale.US),
                            style = MaterialTheme.typography.labelLarge,
                            color = IndustrialPrimary,
                            fontWeight = FontWeight.Black
                        )
                        Text("ACTIVE INSPECTION MODE", color = MutedGrey, fontSize = 10.sp)
                    }
                    
                    Button(
                        onClick = { showLayersSheet = true },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkGrey),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(Icons.Default.Layers, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("LAYERS")
                    }
                }
            }
        }

        // 🛠️ LAYER SELECTION BOTTOM SHEET
        if (showLayersSheet) {
            ModalBottomSheet(
                onDismissRequest = { showLayersSheet = false },
                containerColor = IndustrialSurface,
                dragHandle = { BottomSheetDefaults.DragHandle(color = IndustrialPrimary) }
            ) {
                Column(modifier = Modifier.padding(24.dp).padding(bottom = 32.dp)) {
                    Text("UTILITY VISIBILITY", style = MaterialTheme.typography.titleLarge, color = Color.White)
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    LayerToggle("WATER LINES", WaterBlue, showWater) { showWater = it }
                    LayerToggle("ELECTRICAL GRID", ElectricRed, showElectric) { showElectric = it }
                    LayerToggle("FIBRE OPTICS", FibrePurple, showFibre) { showFibre = it }
                    LayerToggle("SEWAGE SYSTEM", SewageBrown, showSewage) { showSewage = it }
                }
            }
        }

        // 📦 QUICK INFO OVERLAY (Progressive Disclosure)
        AnimatedVisibility(
            visible = selectedUtility != null,
            modifier = Modifier.align(Alignment.Center)
        ) {
            selectedUtility?.let { utility ->
                Card(
                    modifier = Modifier.padding(32.dp).border(2.dp, utility.color, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = IndustrialBlack.copy(alpha = 0.95f))
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Info, null, tint = utility.color, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(utility.label, style = MaterialTheme.typography.titleLarge, color = Color.White)
                        Text("TYPE: ${utility.type}", color = utility.color, fontWeight = FontWeight.Bold)
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(
                            onClick = { selectedUtility = null },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = IndustrialPrimary, contentColor = Color.Black)
                        ) {
                            Text("ACKNOWLEDGE")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LayerToggle(label: String, color: Color, isEnabled: Boolean, onToggle: (Boolean) -> Unit) {
    Surface(
        onClick = { onToggle(!isEnabled) },
        modifier = Modifier.fillMaxWidth().height(64.dp).padding(vertical = 4.dp),
        color = if (isEnabled) color.copy(alpha = 0.2f) else Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isEnabled) color else DarkGrey)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(12.dp).background(color, RoundedCornerShape(2.dp)))
                Spacer(modifier = Modifier.width(16.dp))
                Text(label, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(checkedThumbColor = color, checkedTrackColor = color.copy(alpha = 0.5f))
            )
        }
    }
}
