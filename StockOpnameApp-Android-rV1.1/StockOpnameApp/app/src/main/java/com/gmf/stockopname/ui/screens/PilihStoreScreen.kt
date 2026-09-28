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
import com.gmf.stockopname.ui.theme.CardLight
import com.gmf.stockopname.ui.theme.LineLight
import com.gmf.stockopname.ui.theme.MutedLight

@Composable
fun PilihStoreScreen(
    vm: AppViewModel,
    onBack: () -> Unit,
    onPickStore: (String) -> Unit,
    onRiwayat: () -> Unit
) {
    val storeIds = vm.repo.storeIds()
    Column(Modifier.fillMaxSize()) {
        TopHeader("Stock Op 6", "Langkah 1 \u2014 Pilih Store", onBack)
        LazyColumn(Modifier.padding(16.dp)) {
            item {
                Text("Pilih Store", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            }
            items(storeIds) { storeId ->
                val total = vm.storeTotalFor(storeId)
                val locCount = vm.locationsFor(storeId).size
                StoreRow(
                    title = STORE_LABELS[storeId] ?: storeId,
                    subtitle = "$total tool \u00B7 $locCount lokasi",
                    badge = "Pilih",
                    onClick = { onPickStore(storeId) }
                )
                Spacer(Modifier.height(8.dp))
            }
            item {
                Spacer(Modifier.height(6.dp))
                StoreRow(
                    title = "\uD83D\uDD52 Riwayat Pemeriksaan",
                    subtitle = "Histori Stock Op per lokasi",
                    badge = null,
                    onClick = onRiwayat
                )
            }
        }
    }
}

@Composable
private fun StoreRow(title: String, subtitle: String, badge: String?, onClick: () -> Unit) {
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
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(subtitle, color = MutedLight, fontSize = 12.sp)
        }
        if (badge != null) {
            Text(badge, color = MutedLight, fontSize = 11.sp)
        }
    }
}
