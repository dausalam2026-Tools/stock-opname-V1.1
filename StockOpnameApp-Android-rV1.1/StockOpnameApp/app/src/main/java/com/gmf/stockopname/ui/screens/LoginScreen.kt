package com.gmf.stockopname.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gmf.stockopname.ui.theme.Navy
import com.gmf.stockopname.ui.theme.Navy2

@Composable
fun LoginScreen(onLoggedIn: (String) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(56.dp))
        Box(
            Modifier
                .size(64.dp)
                .background(Brush.linearGradient(listOf(Navy, Navy2)), RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("\uD83E\uDDF0", fontSize = 30.sp) // wrench emoji
        }
        Spacer(Modifier.height(14.dp))
        Text("Stock Opname\nTool Store", textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
        Spacer(Modifier.height(6.dp))
        Text("GMF AeroAsia", color = Color.Gray, fontSize = 12.sp)
        Spacer(Modifier.height(28.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nama Personil") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(18.dp))
        Button(
            onClick = { if (username.isNotBlank() && password.isNotBlank()) onLoggedIn(username.trim()) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Navy2)
        ) { Text("Login") }

        Spacer(Modifier.weight(1f))
        Text("Version 1.0.0 \u00B7 Native Android", color = Color.Gray, fontSize = 11.sp)
        Spacer(Modifier.height(16.dp))
    }
}
