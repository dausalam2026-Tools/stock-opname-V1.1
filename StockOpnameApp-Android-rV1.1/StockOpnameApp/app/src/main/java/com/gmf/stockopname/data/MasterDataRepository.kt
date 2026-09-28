package com.gmf.stockopname.data

import android.content.Context
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Loads assets/masterdata.json (exported from the real Master Tools Database
 * PDF) once, and exposes it as: Store -> Location -> List<Tool>.
 *
 * JSON shape: { "W1": { "RACK I-2": [ {tn,pn,kn,sn,desc,mfr,model,status}, ... ] } }
 */
class MasterDataRepository(context: Context) {

    // storeId -> (location -> tools)
    val data: Map<String, Map<String, List<Tool>>> by lazy { load(context) }

    private fun load(context: Context): Map<String, Map<String, List<Tool>>> {
        val json = context.assets.open("masterdata.json").use { stream ->
            BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).readText()
        }
        val root = JSONObject(json)
        val result = LinkedHashMap<String, Map<String, List<Tool>>>()
        for (storeId in root.keys()) {
            val locObj = root.getJSONObject(storeId)
            val locMap = LinkedHashMap<String, List<Tool>>()
            for (locName in locObj.keys()) {
                val arr = locObj.getJSONArray(locName)
                val tools = ArrayList<Tool>(arr.length())
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    tools.add(
                        Tool(
                            toolNumber = o.optString("tn"),
                            partNumber = o.optString("pn"),
                            keyNumber = o.optString("kn"),
                            serialNumber = o.optString("sn"),
                            description = o.optString("desc"),
                            manufacture = o.optString("mfr"),
                            model = o.optString("model"),
                            status = o.optString("status"),
                            store = storeId,
                            location = locName
                        )
                    )
                }
                locMap[locName] = tools
            }
            result[storeId] = locMap
        }
        return result
    }

    fun storeIds(): List<String> = data.keys.sorted()

    fun locations(storeId: String): List<String> =
        data[storeId]?.keys?.sortedByDescending { data[storeId]?.get(it)?.size ?: 0 } ?: emptyList()

    fun tools(storeId: String, location: String): List<Tool> =
        data[storeId]?.get(location) ?: emptyList()

    fun storeTotal(storeId: String): Int =
        data[storeId]?.values?.sumOf { it.size } ?: 0

    /** Flat list of every tool across every store/location, for Master Data search. */
    val flatIndex: List<Tool> by lazy {
        data.flatMap { (_, locs) -> locs.flatMap { (_, tools) -> tools } }
    }
}
