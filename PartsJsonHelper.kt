package com.example.util

import com.example.data.model.PartItem
import org.json.JSONArray
import org.json.JSONObject

object PartsJsonHelper {

    fun toJson(parts: List<PartItem>): String {
        val array = JSONArray()
        for (part in parts) {
            val obj = JSONObject()
            obj.put("id", part.id)
            obj.put("name", part.name)
            obj.put("count", part.count)
            obj.put("unitPrice", part.unitPrice)
            array.put(obj)
        }
        return array.toString()
    }

    fun fromJson(json: String?): List<PartItem> {
        if (json.isNullOrBlank()) return emptyList()
        val list = mutableListOf<PartItem>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val item = PartItem(
                    id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                    name = obj.optString("name", ""),
                    count = obj.optInt("count", 1),
                    unitPrice = obj.optDouble("unitPrice", 0.0)
                )
                list.add(item)
            }
        } catch (_: Exception) {
            // Return empty or fallback
        }
        return list
    }
}
