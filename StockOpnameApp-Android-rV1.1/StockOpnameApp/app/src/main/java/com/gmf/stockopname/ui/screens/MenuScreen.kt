package com.gmf.stockopname.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gmf.stockopname.ui.theme.*

@Composable
fun MenuScreen(
    personnelName: String,
    onStockOp6: () -> Unit,
    onMasterData: () -> Unit,
    onLogout: () -> Unit
) {
    var showLogoutConfirm by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        TopHeader(
            "Stock Opname",
            "Tool Store \u2014 GMF AeroAsia" + if (personnelName.isNotBlank()) " \u00B7 $personnelName" else "",
            actions = {
                IconButton(onClick = { showLogoutConfirm = true }) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = Color.White)
                }
            }
        )
        Column(Modifier.padding(16.dp)) {
            MenuItem("\uD83E\uDDFE", "Stock Op 6", "Verifikasi Stock Tool di Rack", onStockOp6)
            Spacer(Modifier.height(10.dp))
            MenuItem("\u2699\uFE0F", "Master Data", "Informasi Tool", onMasterData)
        }
    }

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("Logout?") },
            text = { Text("Anda akan keluar dan kembali ke halaman login untuk ganti nama personil.") },
            confirmButton = {
                TextButton(onClick = { showLogoutConfirm = false; onLogout() }) { Text("Logout") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
private fun MenuItem(icon: String, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardLight)
            .border(1.dp, LineLight, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(40.dp)
                .background(Navy, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) { Text(icon, fontSize = 19.sp) }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.5.sp)
            Text(subtitle, color = MutedLight, fontSize = 12.sp)
        }
    }
}
