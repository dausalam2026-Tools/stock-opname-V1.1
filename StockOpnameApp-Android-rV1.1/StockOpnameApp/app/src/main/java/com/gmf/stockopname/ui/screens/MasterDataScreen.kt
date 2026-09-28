package com.gmf.stockopname.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.gmf.stockopname.data.AppViewModel
import com.gmf.stockopname.data.STATUS_LABELS
import com.gmf.stockopname.data.STORE_LABELS
import com.gmf.stockopname.data.Tool
import com.gmf.stockopname.ui.theme.CardLight
import com.gmf.stockopname.ui.theme.LineLight
import com.gmf.stockopname.ui.theme.MutedLight

@Composable
fun MasterDataScreen(
    vm: AppViewModel,
    onBack: () -> Unit,
    onOpenTool: (Tool) -> Unit,
    onAddTool: () -> Unit
) {
    val storeIds = vm.repo.storeIds()
    val results = vm.mdFiltered()
    val shown = results.take(vm.mdLimit)

    Column(Modifier.fillMaxSize()) {
        TopHeader(
            "Master Data", "Informasi Tool \u2014 semua store", onBack,
            actions = {
                IconButton(onClick = onAddTool) {
                    Icon(Icons.Filled.Add, contentDescription = "Tambah Tool", tint = Color.White)
                }
            }
        )
        Column(Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = vm.mdSearch,
                onValueChange = { vm.mdSearch = it; vm.mdLimit = 30 },
                placeholder = { Text("Cari tool number, PN, SN, deskripsi, lokasi...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth()) {
                var storeExpanded by remember { mutableStateOf(false) }
                var statusExpanded by remember { mutableStateOf(false) }

                Box(Modifier.weight(1f)) {
                    OutlinedButton(onClick = { storeExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(vm.mdStoreFilter?.let { STORE_LABELS[it] ?: it } ?: "Semua Store", fontSize = 12.sp)
                    }
                    DropdownMenu(expanded = storeExpanded, onDismissRequest = { storeExpanded = false }) {
                        DropdownMenuItem(text = { Text("Semua Store") }, onClick = { vm.mdStoreFilter = null; vm.mdLimit = 30; storeExpanded = false })
                        storeIds.forEach { s ->
                            DropdownMenuItem(text = { Text(STORE_LABELS[s] ?: s) }, onClick = { vm.mdStoreFilter = s; vm.mdLimit = 30; storeExpanded = false })
                        }
                    }
                }
                Spacer(Modifier.width(8.dp))
                Box(Modifier.weight(1f)) {
                    OutlinedButton(onClick = { statusExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(vm.mdStatusFilter?.let { STATUS_LABELS[it] ?: it } ?: "Semua Status", fontSize = 12.sp)
                    }
                    DropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
                        DropdownMenuItem(text = { Text("Semua Status") }, onClick = { vm.mdStatusFilter = null; vm.mdLimit = 30; statusExpanded = false })
                        STATUS_LABELS.forEach { (k, v) ->
                            DropdownMenuItem(text = { Text(v) }, onClick = { vm.mdStatusFilter = k; vm.mdLimit = 30; statusExpanded = false })
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "${results.size} tool ditemukan" + if (results.size > shown.size) " \u00B7 menampilkan ${shown.size}" else "",
                color = MutedLight, fontSize = 11.sp
            )
            Spacer(Modifier.height(6.dp))
            LazyColumn(Modifier.weight(1f)) {
                items(shown) { t ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardLight)
                            .border(1.dp, LineLight, RoundedCornerShape(12.dp))
                            .clickable { onOpenTool(t) }
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(t.toolNumber, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            Text(
                                "${t.description} \u00B7 ${t.store} / ${t.location}",
                                color = MutedLight, fontSize = 11.sp, maxLines = 1
                            )
                        }
                        StatusBadge(t.status)
                    }
                    Spacer(Modifier.height(8.dp))
                }
                if (results.size > shown.size) {
                    item {
                        OutlinedButton(onClick = { vm.mdLimit += 30 }, modifier = Modifier.fillMaxWidth()) {
                            Text("Tampilkan 30 lagi")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MasterDataDetailScreen(tool: Tool, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        TopHeader("Detail Tool", "${tool.store} / ${tool.location}", onBack)
        Column(Modifier.padding(16.dp)) {
            if (tool.photoId != null) {
                ToolPhoto(tool.photoId)
                Spacer(Modifier.height(12.dp))
            }
            SectionCard {
                StatusBadge(tool.status)
                Spacer(Modifier.height(10.dp))
                listOf(
                    "Tool Number" to tool.toolNumber,
                    "Part Number" to tool.partNumber,
                    "Key Number" to tool.keyNumber.ifBlank { "-" },
                    "Serial Number" to tool.serialNumber,
                    "Deskripsi" to tool.description,
                    "Manufacture" to tool.manufacture.ifBlank { "-" },
                    "Model" to tool.model.ifBlank { "-" },
                    "Store" to tool.store,
                    "Location" to tool.location
                ).forEach { (label, value) ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text(label, color = MutedLight, modifier = Modifier.width(118.dp), fontSize = 13.sp)
                        Text(value, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
