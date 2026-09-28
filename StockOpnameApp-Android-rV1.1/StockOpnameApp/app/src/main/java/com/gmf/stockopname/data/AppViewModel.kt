package com.gmf.stockopname.data

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Holds the "current selection" (store/location/tool index) the same way the
 * web prototype keeps it in a plain `state` object — routes stay simple,
 * fixed strings; the actual data lives here so we never need to URL-encode
 * location names (some contain "/", "#", spaces, etc.) into a nav path.
 */
class AppViewModel(application: Application) : AndroidViewModel(application) {

    val repo = MasterDataRepository(application)
    val store = OpnameStore(application)

    /** Tools added from Master Data, synced live from Firebase (shared across the whole team). */
    val userTools = mutableStateListOf<Tool>()
    var saving by mutableStateOf(false)
        private set
    var saveError by mutableStateOf<String?>(null)

    init {
        viewModelScope.launch {
            RemoteMasterDataSource.observeAll().collect { tools ->
                userTools.clear()
                userTools.addAll(tools)
            }
        }
    }

    var loggedIn by mutableStateOf(store.loggedIn)
        private set
    var personnelName by mutableStateOf(store.personnelName)
        private set

    // Stock Op 6 selection
    var selectedStore by mutableStateOf<String?>(null)
    var selectedLocation by mutableStateOf<String?>(null)
    var scanCursor by mutableStateOf(0)
    var locationSearch by mutableStateOf("")

    // Master Data filters
    var mdSearch by mutableStateOf("")
    var mdStoreFilter by mutableStateOf<String?>(null)
    var mdStatusFilter by mutableStateOf<String?>(null)
    var mdLimit by mutableStateOf(30)
    var mdSelectedTool by mutableStateOf<Tool?>(null)

    // Riwayat
    var riwayatTab by mutableStateOf("Semua") // Semua | Selesai | Proses

    fun login(name: String) {
        store.setLoggedIn(true, name)
        loggedIn = true
        personnelName = name
    }

    /** Logs out and clears the current in-progress selection so the next login starts clean. */
    fun logout() {
        store.logout()
        loggedIn = false
        selectedStore = null
        selectedLocation = null
        scanCursor = 0
        locationSearch = ""
        mdSearch = ""
        mdStoreFilter = null
        mdStatusFilter = null
        mdSelectedTool = null
    }

    /** Tools for a store+location, merging the bundled DB with user-added tools. */
    fun toolsFor(storeId: String, location: String): List<Tool> =
        repo.tools(storeId, location) + userTools.filter { it.store == storeId && it.location == location }

    /** Locations for a store, including any new location introduced by a user-added tool. */
    fun locationsFor(storeId: String): List<String> {
        val base = repo.locations(storeId)
        val extra = userTools.filter { it.store == storeId }.map { it.location }.distinct()
            .filter { it !in base }
        return base + extra
    }

    fun storeTotalFor(storeId: String): Int =
        repo.storeTotal(storeId) + userTools.count { it.store == storeId }

    fun currentTools(): List<Tool> {
        val s = selectedStore ?: return emptyList()
        val l = selectedLocation ?: return emptyList()
        return toolsFor(s, l)
    }

    fun currentStats(): LocationStats {
        val s = selectedStore ?: return LocationStats(0, 0, 0, 0)
        val l = selectedLocation ?: return LocationStats(0, 0, 0, 0)
        return store.stats(s, l, toolsFor(s, l).size)
    }

    fun currentResults(): List<String> {
        val s = selectedStore ?: return emptyList()
        val l = selectedLocation ?: return emptyList()
        store.ensureResults(s, l, toolsFor(s, l).size)
        return store.getResults(s, l)
    }

    fun firstPendingIndex(): Int {
        val results = currentResults()
        return results.indexOfFirst { it == "PENDING" }
    }

    fun markCurrentTool(result: OpnameResult) {
        val s = selectedStore ?: return
        val l = selectedLocation ?: return
        store.setResult(s, l, scanCursor, result)
        if (firstPendingIndex() == -1) {
            store.markFinishedIfNeeded(s, l)
        }
    }

    fun mdFiltered(): List<Tool> {
        return (repo.flatIndex + userTools).filter { t ->
            (mdStoreFilter == null || t.store == mdStoreFilter) &&
                (mdStatusFilter == null || t.status == mdStatusFilter) &&
                (mdSearch.isBlank() ||
                    t.toolNumber.contains(mdSearch, ignoreCase = true) ||
                    t.partNumber.contains(mdSearch, ignoreCase = true) ||
                    t.serialNumber.contains(mdSearch, ignoreCase = true) ||
                    t.description.contains(mdSearch, ignoreCase = true) ||
                    t.location.contains(mdSearch, ignoreCase = true))
        }
    }

    /**
     * Adds a new tool to the shared master data (Firebase), optionally with a photo.
     * onDone(true) on success; onDone(false) with [saveError] set on failure.
     */
    fun addTool(tool: Tool, photoJpeg: ByteArray?, onDone: (Boolean) -> Unit) {
        saving = true
        saveError = null
        viewModelScope.launch {
            try {
                RemoteMasterDataSource.addTool(tool, photoJpeg)
                onDone(true)
            } catch (e: Exception) {
                saveError = e.message ?: "Gagal menyimpan tool. Cek koneksi internet."
                onDone(false)
            } finally {
                saving = false
            }
        }
    }

    fun historyEntries(): List<HistoryEntry> {
        return store.touchedKeys().map { (s, l) ->
            val total = toolsFor(s, l).size
            HistoryEntry(
                store = s,
                location = l,
                finishedAtMillis = store.finishedAtMillis(s, l),
                stats = store.stats(s, l, total)
            )
        }.sortedByDescending { it.finishedAtMillis ?: 0L }
    }
}
