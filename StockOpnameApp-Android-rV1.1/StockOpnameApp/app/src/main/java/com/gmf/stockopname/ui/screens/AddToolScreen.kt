package com.gmf.stockopname.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gmf.stockopname.data.AppViewModel
import com.gmf.stockopname.data.STATUS_LABELS
import com.gmf.stockopname.data.STORE_LABELS
import com.gmf.stockopname.data.Tool
import com.gmf.stockopname.ui.theme.LineLight
import com.gmf.stockopname.ui.theme.MutedLight

@Composable
fun AddToolScreen(
    vm: AppViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    val storeIds = vm.repo.storeIds()

    var toolNumber by remember { mutableStateOf("") }
    var partNumber by remember { mutableStateOf("") }
    var keyNumber by remember { mutableStateOf("") }
    var serialNumber by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var manufacture by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("available") }
    var selectedStore by remember { mutableStateOf(storeIds.firstOrNull() ?: "") }
    var location by remember { mutableStateOf("") }
    var photo by remember { mutableStateOf<Bitmap?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap -> if (bitmap != null) photo = scaleDown(bitmap) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) uriToBitmap(context, uri)?.let { photo = scaleDown(it) } }

    Column(Modifier.fillMaxSize()) {
        TopHeader("Tambah Tool", "Master Data \u2014 Tool baru", onBack)
        Column(
            Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SectionCard {
                Text("Foto Tool", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                Spacer(Modifier.height(8.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(MutedLight.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                        .border(1.dp, LineLight, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    val bmp = photo
                    if (bmp != null) {
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = "Foto tool",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text("Belum ada foto", color = MutedLight, fontSize = 12.sp)
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { cameraLauncher.launch(null) },
                        modifier = Modifier.weight(1f)
                    ) { Text("\uD83D\uDCF7 Ambil Foto") }
                    Spacer(Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = {
                            galleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("\uD83D\uDDBC\uFE0F Galeri") }
                }
            }

            Spacer(Modifier.height(12.dp))

            SectionCard {
                Text("Data Tool", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                Spacer(Modifier.height(8.dp))
                LabeledField("Tool Number *", toolNumber) { toolNumber = it }
                LabeledField("Part Number", partNumber) { partNumber = it }
                LabeledField("Key Number", keyNumber) { keyNumber = it }
                LabeledField("Serial Number", serialNumber) { serialNumber = it }
                LabeledField("Deskripsi", description) { description = it }
                LabeledField("Manufacture", manufacture) { manufacture = it }
                LabeledField("Model", model) { model = it }

                Spacer(Modifier.height(4.dp))
                Text("Status", color = MutedLight, fontSize = 12.sp)
                Spacer(Modifier.height(4.dp))
                var statusExpanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(onClick = { statusExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(STATUS_LABELS[status] ?: status)
                    }
                    DropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
                        STATUS_LABELS.forEach { (k, v) ->
                            DropdownMenuItem(text = { Text(v) }, onClick = { status = k; statusExpanded = false })
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))
                Text("Store *", color = MutedLight, fontSize = 12.sp)
                Spacer(Modifier.height(4.dp))
                var storeExpanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(onClick = { storeExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(STORE_LABELS[selectedStore] ?: selectedStore.ifBlank { "Pilih store" })
                    }
                    DropdownMenu(expanded = storeExpanded, onDismissRequest = { storeExpanded = false }) {
                        storeIds.forEach { s ->
                            DropdownMenuItem(
                                text = { Text(STORE_LABELS[s] ?: s) },
                                onClick = { selectedStore = s; storeExpanded = false }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))
                LabeledField("Location *", location) { location = it }
                if (selectedStore.isNotBlank()) {
                    val suggestions = vm.locationsFor(selectedStore)
                        .filter { it.contains(location, ignoreCase = true) && location.isNotBlank() }
                        .take(5)
                    if (suggestions.isNotEmpty()) {
                        Column(Modifier.padding(top = 2.dp)) {
                            suggestions.forEach { s ->
                                Text(
                                    s,
                                    color = MutedLight,
                                    fontSize = 11.5.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickableSimple { location = s }
                                )
                            }
                        }
                    }
                }
            }

            if (errorMsg != null) {
                Spacer(Modifier.height(8.dp))
                Text(errorMsg ?: "", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }
            if (vm.saveError != null) {
                Spacer(Modifier.height(8.dp))
                Text(vm.saveError ?: "", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }

            Spacer(Modifier.height(14.dp))
            Button(
                onClick = {
                    if (toolNumber.isBlank() || selectedStore.isBlank() || location.isBlank()) {
                        errorMsg = "Tool Number, Store, dan Location wajib diisi."
                        return@Button
                    }
                    errorMsg = null
                    val jpegBytes = photo?.let { compressForFirestore(it) }
                    vm.addTool(
                        Tool(
                            toolNumber = toolNumber.trim(),
                            partNumber = partNumber.trim(),
                            keyNumber = keyNumber.trim(),
                            serialNumber = serialNumber.trim(),
                            description = description.trim(),
                            manufacture = manufacture.trim(),
                            model = model.trim(),
                            status = status,
                            store = selectedStore,
                            location = location.trim()
                        ),
                        jpegBytes
                    ) { success -> if (success) onSaved() }
                },
                enabled = !vm.saving,
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (vm.saving) "Menyimpan..." else "Simpan Tool") }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun LabeledField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}

private fun Modifier.clickableSimple(onClick: () -> Unit): Modifier =
    this.clickable(onClick = onClick)

/** Decodes a picked gallery image (content Uri) into a Bitmap. */
private fun uriToBitmap(context: Context, uri: Uri): Bitmap? = try {
    @Suppress("DEPRECATION")
    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
} catch (_: Exception) {
    null
}

/** Downscales a bitmap so uploads to Firestore stay small and fast. */
private fun scaleDown(bitmap: Bitmap, maxDim: Int = 1024): Bitmap {
    val w = bitmap.width
    val h = bitmap.height
    val largest = maxOf(w, h)
    if (largest <= maxDim) return bitmap
    val scale = maxDim.toFloat() / largest
    return Bitmap.createScaledBitmap(bitmap, (w * scale).toInt(), (h * scale).toInt(), true)
}

/** Firestore documents max out at 1 MiB, so keep the photo well under that (target <= ~600 KB). */
private fun compressForFirestore(bitmap: Bitmap): ByteArray {
    var quality = 80
    var bytes: ByteArray
    do {
        val out = java.io.ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
        bytes = out.toByteArray()
        quality -= 10
    } while (bytes.size > 600_000 && quality >= 30)
    return bytes
}
