package com.example.muniar

import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.core.content.FileProvider
import com.example.muniar.ui.theme.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

@Composable
fun HazardReportScreen(
    location: LatLng,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = AppDatabase.getDatabase(context)
    val repo = HazardRepository(db.hazardDao())

    var step by remember { mutableStateOf(1) } // 1: Quick Report / Camera, 2: Categorization
    var selectedType by remember { mutableStateOf("UNSPECIFIED") }
    var otherDescription by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imageFile = remember {
        File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "hazard_${System.currentTimeMillis()}.jpg")
    }
    val cameraUri = remember {
        FileProvider.getUriForFile(context, "${context.packageName}.provider", imageFile)
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            imageUri = cameraUri
            step = 2 // Move to categorization after photo
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IndustrialBlack)
            .padding(16.dp)
    ) {
        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(56.dp)) {
                Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(32.dp))
            }
            Text(
                if (step == 1) "QUICK INCIDENT REPORT" else "SELECT HAZARD TYPE",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Black
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (step == 1) {
            // STEP 1: REFINED QUICK REPORT (Centered Layout)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // CENTERED CAMERA COMPONENT
                    Surface(
                        onClick = { cameraLauncher.launch(cameraUri) },
                        modifier = Modifier.size(200.dp),
                        shape = CircleShape,
                        color = HazardRed,
                        tonalElevation = 8.dp,
                        shadowElevation = 12.dp
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.PhotoCamera,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "CAPTURE",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(48.dp))
                    
                    // CENTERED COORDINATES BLOCK (Grouped)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .background(IndustrialSurface, RoundedCornerShape(12.dp))
                            .padding(horizontal = 32.dp, vertical = 20.dp)
                    ) {
                        Text(
                            "POINT LOGGED AT:",
                            color = MutedGrey,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "${String.format(Locale.US, "%.6f", location.latitude)}, ${String.format(Locale.US, "%.6f", location.longitude)}",
                            color = IndustrialPrimary,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        } else {
            // STEP 2: CATEGORIZATION
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                val types = listOf("BROKEN PIPE", "GAS LEAK", "CABLE EXPOSED", "SINKHOLE", "UNSPECIFIED")
                
                types.forEach { type ->
                    IndustrialOption(
                        title = type,
                        isSelected = selectedType == type,
                        onSelect = { selectedType = type }
                    )
                    
                    if (type == "UNSPECIFIED" && selectedType == "UNSPECIFIED") {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = otherDescription,
                            onValueChange = { otherDescription = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Explain the 'Other' hazard details...", color = MutedGrey) },
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = IndustrialSurface,
                                unfocusedContainerColor = IndustrialSurface,
                                focusedBorderColor = MutedGrey,
                                unfocusedBorderColor = MutedGrey,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = IndustrialPrimary
                            ),
                            minLines = 3
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        // ACTIONS
        if (step == 2) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    scope.launch {
                        repo.insert(HazardEntity(
                            lat = location.latitude,
                            lng = location.longitude,
                            type = selectedType,
                            description = if (selectedType == "UNSPECIFIED") otherDescription else "Industrial Report",
                            imageUri = imageUri?.toString()
                        ))
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(64.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IndustrialPrimary, contentColor = Color.Black)
            ) {
                Icon(Icons.Default.CloudUpload, null)
                Spacer(modifier = Modifier.width(12.dp))
                Text("SUBMIT REPORT", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun IndustrialOption(title: String, isSelected: Boolean, onSelect: () -> Unit) {
    val icon = when (title) {
        "BROKEN PIPE" -> Icons.Default.WaterDrop
        "GAS LEAK" -> Icons.Default.Air
        "CABLE EXPOSED" -> Icons.Default.ElectricalServices
        "SINKHOLE" -> Icons.Default.Warning
        else -> Icons.Default.Help
    }

    Surface(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp),
        color = if (isSelected) IndustrialPrimary else IndustrialSurface,
        shape = RoundedCornerShape(8.dp),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MutedGrey)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color.Black,
                    unselectedColor = Color.White
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                icon,
                contentDescription = null,
                tint = if (isSelected) Color.Black else Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                title,
                color = if (isSelected) Color.Black else Color.White,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
