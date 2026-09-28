package com.gmf.stockopname.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.gmf.stockopname.data.RemoteMasterDataSource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gmf.stockopname.data.STATUS_LABELS
import com.gmf.stockopname.ui.theme.*

@Composable
fun TopHeader(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    actions: (@Composable () -> Unit)? = null
) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(Brush.horizontalGradient(listOf(Navy, Navy2)))
            .padding(horizontal = 18.dp, vertical = 18.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                }
                Spacer(Modifier.width(4.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(title, color = Color.White, style = MaterialTheme.typography.titleLarge)
                if (subtitle != null) {
                    Text(subtitle, color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.bodyMedium)
                }
            }
            if (actions != null) {
                actions()
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bg, fg) = when (status) {
        "available", "OK", "Selesai" -> GreenBg to Green
        "calibration", "maintenance", "Proses" -> AmberBg to Amber
        "borrowed" -> Color(0xFFDDE8FB) to Color(0xFF3B6FD6)
        "quarantined", "broken", "missing", "Discrepancy" -> RedBg to Red
        else -> BgLight to MutedLight
    }
    val label = STATUS_LABELS[status] ?: status
    Box(
        Modifier
            .background(bg, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(label, color = fg, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun SectionCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardLight),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(Modifier.padding(16.dp), content = content)
    }
}

/** Shows a tool's photo (uploaded via Master Data, stored in Firestore). Loaded on demand and cached in memory. */
private val photoCache = HashMap<String, ImageBitmap>()

@Composable
fun ToolPhoto(photoId: String, modifier: Modifier = Modifier) {
    val image by produceState<ImageBitmap?>(initialValue = photoCache[photoId], photoId) {
        if (value == null) {
            val bytes = try { RemoteMasterDataSource.getPhoto(photoId) } catch (_: Exception) { null }
            val bmp = bytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size)?.asImageBitmap() }
            if (bmp != null) photoCache[photoId] = bmp
            value = bmp
        }
    }
    Card(
        modifier.fillMaxWidth().height(200.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        val img = image
        if (img != null) {
            Image(
                bitmap = img,
                contentDescription = "Foto tool",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Memuat foto...", color = MutedLight)
            }
        }
    }
}

@Composable
fun StatChip(value: String, label: String, color: Color = TextLight, modifier: Modifier = Modifier) {
    Column(
        modifier
            .background(BgLight, RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, color = color, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
        Text(label, color = MutedLight, style = MaterialTheme.typography.labelSmall)
    }
}
