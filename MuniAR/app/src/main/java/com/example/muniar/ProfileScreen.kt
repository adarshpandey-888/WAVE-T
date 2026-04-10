package com.example.muniar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muniar.ui.theme.IndustrialBlack
import com.example.muniar.ui.theme.IndustrialPrimary
import com.example.muniar.ui.theme.IndustrialSurface
import com.example.muniar.ui.theme.MutedGrey

@Composable
fun ProfileScreen(
    user: User?,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IndustrialBlack)
            .padding(24.dp)
    ) {
        // HEADER - Anchored to bottom conceptually but top for layout
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.ArrowBack, "Back", tint = Color.White, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                "DATA & SYNC MANAGER",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Black
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 📊 SYNC STATUS SECTION (Replacing standard profile info)
        Text("SYSTEM STATUS", color = IndustrialPrimary, style = MaterialTheme.typography.labelLarge, letterSpacing = 2.sp)
        
        Spacer(modifier = Modifier.height(16.dp))

        SyncProgressBar(label = "OFFLINE MAP DATA", progress = 0.85f, status = "1.2 GB / 1.5 GB")
        
        Spacer(modifier = Modifier.height(24.dp))

        SyncProgressBar(label = "PENDING HAZARD REPORTS", progress = 0.3f, status = "3 TO UPLOAD", color = Color.Red)

        Spacer(modifier = Modifier.height(40.dp))

        // 👤 OPERATOR INFO (Progressive Disclosure)
        Text("OPERATOR PROFILE", color = IndustrialPrimary, style = MaterialTheme.typography.labelLarge, letterSpacing = 2.sp)
        
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Text(
                    text = user?.name ?: "OFFLINE OPERATOR",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = user?.role ?: "NO ACTIVE SESSION",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MutedGrey
                )
                
                if (user != null) {
                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.DarkGray)
                    ProfileDetailItem(label = "LICENSE ID", value = user.license)
                    ProfileDetailItem(label = "COMPANY", value = user.company)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 🚪 SIGN OUT - High Visibility
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Logout, null, tint = Color.White)
            Spacer(modifier = Modifier.width(12.dp))
            Text("TERMINATE SESSION", color = Color.White, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun SyncProgressBar(
    label: String,
    progress: Float,
    status: String,
    color: Color = IndustrialPrimary
) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = Color.White, style = MaterialTheme.typography.labelLarge)
            Text(status, color = color, style = MaterialTheme.typography.labelLarge)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp),
            color = color,
            trackColor = IndustrialSurface,
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}

@Composable
fun ProfileDetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, color = MutedGrey, style = MaterialTheme.typography.labelSmall)
        Text(value, color = Color.White, style = MaterialTheme.typography.bodyLarge)
    }
}
