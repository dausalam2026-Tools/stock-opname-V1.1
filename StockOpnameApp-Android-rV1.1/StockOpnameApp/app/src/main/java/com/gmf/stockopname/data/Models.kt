package com.gmf.stockopname.data

/** One tool record, exactly as it appears in the Master Tools Database. */
data class Tool(
    val toolNumber: String,
    val partNumber: String,
    val keyNumber: String,
    val serialNumber: String,
    val description: String,
    val manufacture: String,
    val model: String,
    val status: String, // available/calibration/borrowed/quarantined/maintenance/broken/missing
    val store: String,
    val location: String,
    val photoId: String? = null // ID foto di Firestore (koleksi tool_photos); null = tidak ada foto
)

/** Opname (verification) result for one tool: pending / OK / Discrepancy. */
enum class OpnameResult { PENDING, OK, DISCREPANCY }

data class LocationStats(
    val total: Int,
    val checked: Int,
    val ok: Int,
    val discrepancy: Int
) {
    val remaining: Int get() = total - checked
    val isComplete: Boolean get() = total > 0 && checked == total
    val percent: Float get() = if (total == 0) 0f else checked.toFloat() / total
}

data class HistoryEntry(
    val store: String,
    val location: String,
    val finishedAtMillis: Long?,
    val stats: LocationStats
)

val STATUS_LABELS = mapOf(
    "available" to "Available",
    "calibration" to "Kalibrasi",
    "borrowed" to "Dipinjam",
    "quarantined" to "Quarantine",
    "maintenance" to "Maintenance",
    "broken" to "Rusak",
    "missing" to "Hilang"
)

val STORE_LABELS = mapOf(
    "W1" to "Store W1",
    "W2" to "Store W2",
    "LG" to "Store LG (Landing Gear)"
)
