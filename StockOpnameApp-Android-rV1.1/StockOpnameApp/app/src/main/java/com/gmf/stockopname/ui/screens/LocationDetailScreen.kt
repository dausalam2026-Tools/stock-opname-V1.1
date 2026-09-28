package com.gmf.stockopname.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gmf.stockopname.data.AppViewModel
import com.gmf.stockopname.ui.theme.Green
import com.gmf.stockopname.ui.theme.MutedLight
import com.gmf.stockopname.ui.theme.Red

@Composable
fun LocationDetailScreen(
    vm: AppViewModel,
    storeId: String,
    location: String,
    onBack: () -> Unit,
    onStartOrResume: () -> Unit,
    onFinished: () -> Unit
) {
    val tools = vm.toolsFor(storeId, location)
    val stats = vm.store.stats(storeId, location, tools.size)
    val results = vm.store.getResults(storeId, location)

    Column(Modifier.fillMaxSize()) {
        TopHeader("Stock Op 6", "$storeId \u00B7 $location", onBack)
        Column(Modifier.padding(16.dp)) {
            SectionCard {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    StatChip(stats.total.toString(), "Total Tool", modifier = Modifier.weight(1f))
                    StatChip(stats.checked.toString(), "Checked", color = Green, modifier = Modifier.weight(1f))
                    StatChip(stats.remaining.toString(), "Remaining", modifier = Modifier.weight(1f))
                    StatChip(stats.discrepancy.toString(), "Discrepancy", color = Red, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { if (stats.isComplete) onFinished() else onStartOrResume() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        when {
                            stats.isComplete -> "Lihat Ringkasan"
                            stats.checked > 0 -> "Lanjutkan Pemeriksaan"
                            else -> "Mulai Pemeriksaan"
                        }
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("Daftar Tool ($location)", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            LazyColumn {
                itemsIndexed(tools, results)
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.itemsIndexed(
    tools: List<com.gmf.stockopname.data.Tool>,
    results: List<String>
) {
    items(tools.size) { i ->
        val t = tools[i]
        val res = results.getOrElse(i) { "PENDING" }
        val displayStatus = when (res) {
            "OK" -> "OK"
            "DISCREPANCY" -> "Discrepancy"
            else -> "pending"
        }
        Row(
            Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(t.toolNumber, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text(t.serialNumber, color = MutedLight, fontSize = 11.sp)
            }
            StatusBadge(displayStatus)
        }
    }
}
