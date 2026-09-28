package com.gmf.stockopname.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gmf.stockopname.data.OpnameResult
import com.gmf.stockopname.data.Tool
import com.gmf.stockopname.ui.theme.Green
import com.gmf.stockopname.ui.theme.MutedLight
import com.gmf.stockopname.ui.theme.Red
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DetailToolScreen(
    tool: Tool,
    onBack: () -> Unit,
    onResult: (OpnameResult) -> Unit
) {
    val dateStr = remember(tool) { SimpleDateFormat("dd-MM-yyyy", Locale("id", "ID")).format(Date()) }

    Column(Modifier.fillMaxSize()) {
        TopHeader("Detail Tool", tool.location, onBack)
        Column(Modifier.padding(16.dp)) {
            if (tool.photoId != null) {
                ToolPhoto(tool.photoId)
                Spacer(Modifier.height(12.dp))
            }
            SectionCard {
                StatusBadge(tool.status)
                Spacer(Modifier.height(10.dp))
                DetailRow("Tool Number", tool.toolNumber)
                DetailRow("Part Number", tool.partNumber)
                DetailRow("Key Number", tool.keyNumber.ifBlank { "-" })
                DetailRow("Serial Number", tool.serialNumber)
                DetailRow("Deskripsi", tool.description)
                DetailRow("Manufacture", tool.manufacture.ifBlank { "-" })
                DetailRow("Model", tool.model.ifBlank { "-" })
                DetailRow("Store", tool.store)
                DetailRow("Location", tool.location)
                DetailRow("Tgl Cek", dateStr)
                DetailRow("PIC", "Firdaus Alamsyah")
            }
            Spacer(Modifier.height(10.dp))
            Text(
                "Bandingkan data di atas dengan kondisi fisik tool, lalu pilih hasil pemeriksaan.",
                color = MutedLight, style = MaterialTheme.typography.labelSmall
            )
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth()) {
                Button(
                    onClick = { onResult(OpnameResult.DISCREPANCY) },
                    colors = ButtonDefaults.buttonColors(containerColor = Red),
                    modifier = Modifier.weight(1f)
                ) { Text("Discrepancy", color = Color.White) }
                Spacer(Modifier.width(10.dp))
                Button(
                    onClick = { onResult(OpnameResult.OK) },
                    colors = ButtonDefaults.buttonColors(containerColor = Green),
                    modifier = Modifier.weight(1f)
                ) { Text("OK (Sesuai)", color = Color.White) }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        androidx.compose.material3.Text(
            label, color = MutedLight, modifier = Modifier.width(118.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        androidx.compose.material3.Text(
            value, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium
        )
    }
}
