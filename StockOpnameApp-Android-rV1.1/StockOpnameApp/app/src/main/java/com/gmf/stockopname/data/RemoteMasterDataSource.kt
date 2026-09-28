package com.gmf.stockopname.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Talks to Firebase Firestore (free Spark plan is enough — no Cloud Storage).
 * A tool added from the Master Data "+" button, and its photo, becomes visible
 * to every phone on the team in real time.
 *
 * Layout:
 *  - collection "tools":       one document per added tool (light, no photo bytes)
 *  - collection "tool_photos": one document per photo (field "data" = JPEG bytes),
 *                              referenced from the tool via its "photoId".
 * Photos live in their own collection so the tool list stays small and fast;
 * a photo is only downloaded when a detail screen actually shows it.
 *
 * Firestore also caches the last-synced data on-device automatically.
 */
object RemoteMasterDataSource {

    private const val TOOLS = "tools"
    private const val PHOTOS = "tool_photos"
    private val db by lazy { FirebaseFirestore.getInstance() }

    /** Emits the full shared-tool list, and again every time it changes on the server. */
    fun observeAll(): Flow<List<Tool>> = callbackFlow {
        val registration = db.collection(TOOLS).addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) return@addSnapshotListener
            trySend(snapshot.documents.mapNotNull { it.toTool() })
        }
        awaitClose { registration.remove() }
    }

    /** Saves the photo (if any) first, then the tool. Returns the tool with its photoId set. */
    suspend fun addTool(tool: Tool, photoJpeg: ByteArray?): Tool {
        var photoId: String? = null
        if (photoJpeg != null) {
            photoId = UUID.randomUUID().toString()
            db.collection(PHOTOS).document(photoId)
                .set(hashMapOf("data" to Blob.fromBytes(photoJpeg)))
                .await()
        }
        val data = hashMapOf(
            "toolNumber" to tool.toolNumber,
            "partNumber" to tool.partNumber,
            "keyNumber" to tool.keyNumber,
            "serialNumber" to tool.serialNumber,
            "description" to tool.description,
            "manufacture" to tool.manufacture,
            "model" to tool.model,
            "status" to tool.status,
            "store" to tool.store,
            "location" to tool.location,
            "photoId" to photoId,
            "addedAt" to Timestamp.now()
        )
        db.collection(TOOLS).add(data).await()
        return tool.copy(photoId = photoId)
    }

    /** Downloads a photo's JPEG bytes, or null if it doesn't exist. */
    suspend fun getPhoto(photoId: String): ByteArray? =
        db.collection(PHOTOS).document(photoId).get().await().getBlob("data")?.toBytes()

    private fun DocumentSnapshot.toTool(): Tool? {
        val toolNumber = getString("toolNumber") ?: return null
        return Tool(
            toolNumber = toolNumber,
            partNumber = getString("partNumber") ?: "",
            keyNumber = getString("keyNumber") ?: "",
            serialNumber = getString("serialNumber") ?: "",
            description = getString("description") ?: "",
            manufacture = getString("manufacture") ?: "",
            model = getString("model") ?: "",
            status = getString("status") ?: "available",
            store = getString("store") ?: "",
            location = getString("location") ?: "",
            photoId = getString("photoId")
        )
    }
}
