package com.gmf.stockopname.pdf

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.gmf.stockopname.data.LocationStats
import com.gmf.stockopname.data.Tool
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfGenerator {

    private const val PAGE_W = 595 // A4 at 72dpi width
    private const val PAGE_H = 842

    /**
     * Builds a Stock Op 6 report PDF for one store/location, writes it to
     * the app's private "reports" folder, and returns a content:// Uri
     * (via FileProvider) ready to share or view.
     */
    fun generate(
        context: Context,
        store: String,
        location: String,
        pic: String,
        tools: List<Tool>,
        results: List<String>,
        stats: LocationStats
    ): android.net.Uri {
        val doc = PdfDocument()
        val titlePaint = Paint().apply { textSize = 16f; isFakeBoldText = true }
        val labelPaint = Paint().apply { textSize = 10f }
        val rowPaint = Paint().apply { textSize = 8.5f }
        val headerPaint = Paint().apply { textSize = 8.5f; isFakeBoldText = true }

        var page = doc.startPage(PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, 1).create())
        var canvas: Canvas = page.canvas
        var y = 40f

        canvas.drawText("GMF AeroAsia \u2014 Stock Op 6 Tool Store", 32f, y, titlePaint); y += 22f
        canvas.drawText("Store: $store   Location: $location", 32f, y, labelPaint); y += 16f
        val dateStr = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale("id", "ID")).format(Date())
        canvas.drawText("Tanggal: $dateStr", 32f, y, labelPaint); y += 16f
        canvas.drawText("PIC: $pic", 32f, y, labelPaint); y += 16f
        canvas.drawText(
            "Total: ${stats.total}   OK: ${stats.ok}   Discrepancy: ${stats.discrepancy}",
            32f, y, labelPaint
        ); y += 26f

        canvas.drawText("No", 32f, y, headerPaint)
        canvas.drawText("Tool Number", 60f, y, headerPaint)
        canvas.drawText("Part Number", 210f, y, headerPaint)
        canvas.drawText("Serial Number", 360f, y, headerPaint)
        canvas.drawText("Status", 490f, y, headerPaint)
        y += 12f
        canvas.drawLine(32f, y - 8f, 563f, y - 8f, labelPaint)

        for (i in tools.indices) {
            if (y > PAGE_H - 40f) {
                doc.finishPage(page)
                page = doc.startPage(PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, doc.pages.size + 1).create())
                canvas = page.canvas
                y = 40f
            }
            val t = tools[i]
            val res = results.getOrElse(i) { "PENDING" }
            canvas.drawText("${i + 1}", 32f, y, rowPaint)
            canvas.drawText(t.toolNumber.take(22), 60f, y, rowPaint)
            canvas.drawText(t.partNumber.take(20), 210f, y, rowPaint)
            canvas.drawText(t.serialNumber.take(18), 360f, y, rowPaint)
            canvas.drawText(res, 490f, y, rowPaint)
            y += 13f
        }
        doc.finishPage(page)

        val reportsDir = File(context.filesDir, "reports").apply { mkdirs() }
        val safeLocation = location.replace(Regex("[^a-zA-Z0-9]+"), "_")
        val file = File(reportsDir, "StockOp6_${store}_${safeLocation}.pdf")
        FileOutputStream(file).use { doc.writeTo(it) }
        doc.close()

        return FileProvider.getUriForFile(context, "com.gmf.stockopname.fileprovider", file)
    }

    /** Builds a share/open Intent chooser for the generated PDF Uri. */
    fun shareIntent(uri: android.net.Uri): Intent {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        return Intent.createChooser(send, "Simpan atau bagikan Report")
    }
}
