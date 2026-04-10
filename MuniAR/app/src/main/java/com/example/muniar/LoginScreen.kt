package com.example.muniar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muniar.ui.theme.IndustrialBlack
import com.example.muniar.ui.theme.IndustrialPrimary

@Composable
fun LoginScreen(
    onLoginSuccess: (User) -> Unit,
    onContinueOffline: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IndustrialBlack)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 🏗️ INDUSTRIAL LOGO
        Icon(
            imageVector = Icons.Default.PrecisionManufacturing,
            contentDescription = null,
            tint = IndustrialPrimary,
            modifier = Modifier.size(80.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            "MuniAR",
            color = Color.White,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black
        )
        
        Text(
            "INDUSTRIAL UTILITY MAPPING",
            color = IndustrialPrimary,
            style = MaterialTheme.typography.labelLarge,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        // 👤 OVERSIZED INPUTS
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("OPERATOR ID") },
            leadingIcon = { Icon(Icons.Default.Badge, null, tint = IndustrialPrimary) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp),
            shape = RoundedCornerShape(8.dp),
            colors = industrialTextFieldColors()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("SECURITY ACCESS CODE") },
            leadingIcon = { Icon(Icons.Default.Lock, null, tint = IndustrialPrimary) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp),
            shape = RoundedCornerShape(8.dp),
            colors = industrialTextFieldColors()
        )

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 🔐 AUTH BUTTON
        Button(
            onClick = {
                val user = FakeAuth.login(username, password)
                if (user != null) {
                    errorMessage = ""
                    onLoginSuccess(user)
                } else {
                    errorMessage = "ACCESS DENIED: INVALID CREDENTIALS"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = IndustrialPrimary,
                contentColor = Color.Black
            )
        ) {
            Text("AUTHENTICATE & ENTER", style = MaterialTheme.typography.titleLarge)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 📴 OFFLINE ACTION
        OutlinedButton(
            onClick = { onContinueOffline() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
        ) {
            Icon(Icons.Default.CloudOff, null)
            Spacer(modifier = Modifier.width(12.dp))
            Text("CONTINUE OFFLINE MODE", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun industrialTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = IndustrialPrimary,
    unfocusedBorderColor = Color.Gray,
    focusedLabelColor = IndustrialPrimary,
    unfocusedLabelColor = Color.Gray,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White
)
