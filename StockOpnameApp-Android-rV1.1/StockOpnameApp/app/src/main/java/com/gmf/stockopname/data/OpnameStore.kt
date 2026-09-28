package com.gmf.stockopname.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * Persists opname progress (per store/location tool results), completion
 * timestamps (for Riwayat), and login state — all in one SharedPreferences
 * blob, mirroring the localStorage approach used in the web prototype.
 */
class OpnameStore(context: Context) {
    private val prefs = context.getSharedPreferences("stock_opname_prefs", Context.MODE_PRIVATE)
    private val KEY = "state_blob_v1"

    private var results: MutableMap<String, MutableList<String>> = mutableMapOf()
    private var finishedAt: MutableMap<String, Long> = mutableMapOf()
    var loggedIn: Boolean = false
        private set
    var personnelName: String = ""
        private set

    init {
        load()
    }

    private fun keyOf(store: String, location: String) = "$store||$location"

    private fun load() {
        val raw = prefs.getString(KEY, null) ?: return
        try {
            val root = JSONObject(raw)
            loggedIn = root.optBoolean("loggedIn", false)
            personnelName = root.optString("personnelName", "")
            val resObj = root.optJSONObject("results")
            if (resObj != null) {
                for (k in resObj.keys()) {
                    val arr = resObj.getJSONArray(k)
                    val list = MutableList(arr.length()) { i -> arr.getString(i) }
                    results[k] = list
                }
            }
            val finObj = root.optJSONObject("finishedAt")
            if (finObj != null) {
                for (k in finObj.keys()) {
                    finishedAt[k] = finObj.getLong(k)
                }
            }
        } catch (_: Exception) {
            // corrupt/old blob: start fresh rather than crash
        }
    }

    private fun persist() {
        val root = JSONObject()
        root.put("loggedIn", loggedIn)
        root.put("personnelName", personnelName)
        val resObj = JSONObject()
        for ((k, list) in results) {
            resObj.put(k, JSONArray(list))
        }
        root.put("results", resObj)
        val finObj = JSONObject()
        for ((k, v) in finishedAt) finObj.put(k, v)
        root.put("finishedAt", finObj)
        prefs.edit().putString(KEY, root.toString()).apply()
    }

    fun setLoggedIn(value: Boolean, personnel: String = "") {
        loggedIn = value
        if (value) personnelName = personnel
        persist()
    }

    fun logout() {
        loggedIn = false
        persist()
    }

    /** Ensures a results array exists for this location, sized to [total], defaulting to PENDING. */
    fun ensureResults(store: String, location: String, total: Int): MutableList<String> {
        val k = keyOf(store, location)
        val existing = results[k]
        if (existing != null && existing.size == total) return existing
        val fresh = MutableList(total) { i -> existing?.getOrNull(i) ?: "PENDING" }
        results[k] = fresh
        return fresh
    }

    fun getResults(store: String, location: String): List<String> =
        results[keyOf(store, location)] ?: emptyList()

    fun setResult(store: String, location: String, index: Int, value: OpnameResult) {
        val k = keyOf(store, location)
        val list = results[k] ?: return
        if (index in list.indices) {
            list[index] = value.name
            persist()
        }
    }

    fun markFinishedIfNeeded(store: String, location: String) {
        val k = keyOf(store, location)
        if (!finishedAt.containsKey(k)) {
            finishedAt[k] = System.currentTimeMillis()
            persist()
        }
    }

    fun finishedAtMillis(store: String, location: String): Long? = finishedAt[keyOf(store, location)]

    fun stats(store: String, location: String, total: Int): LocationStats {
        val list = ensureResults(store, location, total)
        val ok = list.count { it == "OK" }
        val disc = list.count { it == "DISCREPANCY" }
        return LocationStats(total = total, checked = ok + disc, ok = ok, discrepancy = disc)
    }

    /** Every store/location touched so far, for the Riwayat screen. */
    fun touchedKeys(): List<Pair<String, String>> =
        results.keys.map { k ->
            val parts = k.split("||", limit = 2)
            parts[0] to parts.getOrElse(1) { "" }
        }
}
