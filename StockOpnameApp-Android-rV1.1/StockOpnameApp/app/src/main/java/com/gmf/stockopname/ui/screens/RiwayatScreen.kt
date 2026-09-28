package com.gmf.stockopname.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gmf.stockopname.data.AppViewModel
import com.gmf.stockopname.data.STORE_LABELS
import com.gmf.stockopname.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RiwayatScreen(
    vm: AppViewModel,
    onBack: () -> Unit,
    onOpen: (String, String) -> Unit
) {
    val entries = vm.historyEntries()
    val tab = vm.riwayatTab
    val filtered = when (tab) {
        "Selesai" -> entries.filter { it.stats.isComplete }
        "Proses" -> entries.filter { !it.stats.isComplete }
        else -> entries
    }
    val df = remember { SimpleDateFormat("dd-MM-yyyy HH:mm", Locale("id", "ID")) }

    Column(Modifier.fillMaxSize()) {
        TopHeader("Riwayat Stock Op 6", "Histori pemeriksaan per lokasi", onBack)
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth()) {
                listOf("Semua", "Selesai", "Proses").forEach { t ->
                    val selected = tab == t
                    Box(
                        Modifier
                            .weight(1f)
                            .padding(end = 6.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selected) Accent else BgLight)
                            .clickable { vm.riwayatTab = t }
                            .padding(vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(t, color = if (selected) Color.White else TextLight, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            if (filtered.isEmpty()) {
                Text("Belum ada riwayat pemeriksaan.", color = MutedLight)
            } else {
                LazyColumn {
                    items(filtered) { entry ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(CardLight)
                                .border(1.dp, LineLight, RoundedCornerShape(12.dp))
                                .clickable { onOpen(entry.store, entry.location) }
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(entry.location, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                val dateTxt = entry.finishedAtMillis?.let { " \u00B7 ${df.format(Date(it))}" } ?: ""
                                Text(
                                    "${STORE_LABELS[entry.store] ?: entry.store} \u00B7 ${entry.stats.checked}/${entry.stats.total}$dateTxt",
                                    color = MutedLight, fontSize = 11.5.sp
                                )
                            }
                            val done = entry.stats.isComplete
                            Box(
                                Modifier
                                    .background(if (done) GreenBg else AmberBg, RoundedCornerShape(20.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(if (done) "Selesai" else "Proses", color = if (done) Green else Amber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
