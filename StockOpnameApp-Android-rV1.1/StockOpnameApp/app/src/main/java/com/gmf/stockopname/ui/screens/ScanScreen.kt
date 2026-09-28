package com.gmf.stockopname.ui.screens

import android.Manifest
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.border
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.gmf.stockopname.data.AppViewModel
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanScreen(
    vm: AppViewModel,
    storeId: String,
    location: String,
    onBack: () -> Unit,
    onBarcodeDetected: (String) -> Unit,
    onManualConfirm: () -> Unit
) {
    val stats = vm.store.stats(storeId, location, vm.toolsFor(storeId, location).size)
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    var lastCode by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize()) {
        TopHeader("Scan Barcode", "$location \u00B7 ${stats.checked}/${stats.total}", onBack)
        Column(Modifier.padding(16.dp)) {
            LinearProgressIndicator(
                progress = stats.percent,
                modifier = Modifier.fillMaxWidth().height(8.dp)
            )
            Spacer(Modifier.height(12.dp))

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(Color(0xFF0A1220), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (cameraPermission.status.isGranted) {
                    CameraPreviewWithBarcodeScanner(
                        onDetected = { code ->
                            if (lastCode != code) {
                                lastCode = code
                                onBarcodeDetected(code)
                            }
                        }
                    )
                    // viewfinder guide overlay
                    Box(
                        Modifier
                            .size(190.dp, 130.dp)
                            .border(2.5.dp, Color(0xFF4C9BFF), RoundedCornerShape(10.dp))
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Kamera belum aktif", color = Color.White)
                        Spacer(Modifier.height(10.dp))
                        Button(onClick = { cameraPermission.launchPermissionRequest() }) {
                            Text("\uD83C\uDF9E\uFE0F Aktifkan Kamera")
                        }
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
            Button(onClick = onManualConfirm, modifier = Modifier.fillMaxWidth()) {
                Text("\uD83D\uDCF7 Tandai Terbaca (manual)")
            }
            val denied = cameraPermission.status as? PermissionStatus.Denied
            if (denied != null && denied.shouldShowRationale) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Izin kamera ditolak. Buka Setelan aplikasi > Izin > Kamera untuk mengaktifkan, atau gunakan tombol manual di atas.",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * Live CameraX preview piped through ML Kit's on-device barcode scanner.
 * Calls [onDetected] with the raw barcode value the moment one is read.
 */
@Composable
private fun CameraPreviewWithBarcodeScanner(onDetected: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val executor = remember { Executors.newSingleThreadExecutor() }
    val scanner = remember {
        BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                    Barcode.FORMAT_CODE_128, Barcode.FORMAT_CODE_39, Barcode.FORMAT_EAN_13,
                    Barcode.FORMAT_EAN_8, Barcode.FORMAT_UPC_A, Barcode.FORMAT_UPC_E,
                    Barcode.FORMAT_QR_CODE, Barcode.FORMAT_CODABAR
                )
                .build()
        )
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val providerFuture = ProcessCameraProvider.getInstance(ctx)
            providerFuture.addListener({
                val provider = providerFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                analysis.setAnalyzer(executor) { imageProxy ->
                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                        scanner.process(image)
                            .addOnSuccessListener { barcodes ->
                                val value = barcodes.firstOrNull()?.rawValue
                                if (value != null) onDetected(value)
                            }
                            .addOnCompleteListener { imageProxy.close() }
                    } else {
                        imageProxy.close()
                    }
                }
                try {
                    provider.unbindAll()
                    provider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        analysis
                    )
                } catch (_: Exception) {
                    // binding failed (e.g. no camera hardware) — screen falls back to the manual button
                }
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        }
    )
}
