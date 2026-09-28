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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gmf.stockopname.data.AppViewModel
import com.gmf.stockopname.data.STORE_LABELS
import com.gmf.stockopname.ui.theme.*

@Composable
fun PilihLocationScreen(
    vm: AppViewModel,
    storeId: String,
    onBack: () -> Unit,
    onPickLocation: (String) -> Unit
) {
    val allLocations = vm.locationsFor(storeId)
    val query = vm.locationSearch
    val filtered = if (query.isBlank()) allLocations else allLocations.filter {
        it.contains(query, ignoreCase = true)
    }

    Column(Modifier.fillMaxSize()) {
        TopHeader("Stock Op 6 \u2014 ${STORE_LABELS[storeId] ?: storeId}", "Langkah 2 \u2014 Pilih Location", onBack)
        Column(Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = { vm.locationSearch = it },
                placeholder = { Text("Cari location...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            LazyColumn {
                items(filtered) { loc ->
                    val tools = vm.toolsFor(storeId, loc)
                    val stats = vm.store.stats(storeId, loc, tools.size)
                    LocationRow(
                        name = loc,
                        count = tools.size,
                        checked = stats.checked,
                        done = stats.isComplete,
                        onClick = { onPickLocation(loc) }
                    )
                    Spacer(Modifier.height(8.dp))
                }
                if (filtered.isEmpty()) {
                    item { Text("Lokasi tidak ditemukan.", color = MutedLight, modifier = Modifier.padding(top = 16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun LocationRow(name: String, count: Int, checked: Int, done: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardLight)
            .border(1.dp, LineLight, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(
                "$count EA" + if (checked > 0) " \u00B7 $checked/$count diperiksa" else "",
                color = MutedLight, fontSize = 11.5.sp
            )
        }
        val (label, bg, fg) = when {
            done -> Triple("Selesai", GreenBg, Green)
            checked > 0 -> Triple("Proses", AmberBg, Amber)
            else -> Triple("Belum", BgLight, MutedLight)
        }
        Box(Modifier.background(bg, RoundedCornerShape(20.dp)).padding(horizontal = 10.dp, vertical = 4.dp)) {
            Text(label, color = fg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}
