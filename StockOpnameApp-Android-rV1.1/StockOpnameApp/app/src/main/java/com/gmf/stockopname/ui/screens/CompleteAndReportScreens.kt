package com.gmf.stockopname.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gmf.stockopname.data.AppViewModel
import com.gmf.stockopname.pdf.PdfGenerator
import com.gmf.stockopname.ui.theme.Green
import com.gmf.stockopname.ui.theme.MutedLight
import com.gmf.stockopname.ui.theme.Red

@Composable
fun CompleteScreen(
    vm: AppViewModel,
    storeId: String,
    location: String,
    onBack: () -> Unit,
    onGenerateReport: () -> Unit,
    onHome: () -> Unit
) {
    val tools = vm.toolsFor(storeId, location)
    val stats = vm.store.stats(storeId, location, tools.size)

    Column(Modifier.fillMaxSize()) {
        TopHeader("Stock Op 6", location, onBack)
        Column(
            Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SectionCard {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier.size(64.dp).background(Green, shape = androidx.compose.foundation.shape.CircleShape),
                        contentAlignment = Alignment.Center
                    ) { Text("\u2713", color = androidx.compose.ui.graphics.Color.White, fontSize = 30.sp) }
                    Spacer(Modifier.height(12.dp))
                    Text("Pemeriksaan Selesai", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Semua tool di $location telah diperiksa (${stats.total} EA).",
                        color = MutedLight, textAlign = TextAlign.Center, fontSize = 12.5.sp
                    )
                    Spacer(Modifier.height(14.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatChip(stats.total.toString(), "Total Tool", modifier = Modifier.weight(1f))
                        StatChip(stats.ok.toString(), "OK", color = Green, modifier = Modifier.weight(1f))
                        StatChip(stats.discrepancy.toString(), "Discrepancy", color = Red, modifier = Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(14.dp))
                    Button(onClick = onGenerateReport, modifier = Modifier.fillMaxWidth()) {
                        Text("Generate PDF Report")
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = onHome, modifier = Modifier.fillMaxWidth()) {
                        Text("Kembali ke Menu")
                    }
                }
            }
        }
    }
}

@Composable
fun ReportScreen(
    vm: AppViewModel,
    storeId: String,
    location: String,
    onBack: () -> Unit,
    onHome: () -> Unit
) {
    val context = LocalContext.current
    val tools = vm.toolsFor(storeId, location)
    val results = vm.store.getResults(storeId, location)
    val stats = vm.store.stats(storeId, location, tools.size)

    Column(Modifier.fillMaxSize()) {
        TopHeader("Laporan Stock Op 6", location, onBack)
        Column(
            Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SectionCard {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("\uD83D\uDCC4", fontSize = 40.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Report $location siap dibuat",
                        fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center, fontSize = 15.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Berisi detail tool, hasil pemeriksaan (OK/Discrepancy), PIC, serta tanggal & waktu pemeriksaan.",
                        color = MutedLight, textAlign = TextAlign.Center, fontSize = 12.sp
                    )
                    Spacer(Modifier.height(14.dp))
                    Button(
                        onClick = {
                            sharePdf(context, storeId, location, tools, results, stats)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("\u2B07 Simpan / Bagikan PDF Report") }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = onHome, modifier = Modifier.fillMaxWidth()) {
                        Text("Kembali ke Menu")
                    }
                }
            }
        }
    }
}

private fun sharePdf(
    context: Context,
    storeId: String,
    location: String,
    tools: List<com.gmf.stockopname.data.Tool>,
    results: List<String>,
    stats: com.gmf.stockopname.data.LocationStats
) {
    val uri = PdfGenerator.generate(
        context = context,
        store = storeId,
        location = location,
        pic = "Firdaus Alamsyah",
        tools = tools,
        results = results,
        stats = stats
    )
    context.startActivity(PdfGenerator.shareIntent(uri))
}
