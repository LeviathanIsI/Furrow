package com.furrow.app.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object SeedLoader {
    fun loadJsonArray(context: Context, filename: String): JSONArray {
        val json = context.assets.open("seeds/$filename").bufferedReader().use { it.readText() }
        return JSONArray(json)
    }

    fun loadJsonObject(context: Context, filename: String): JSONObject {
        val json = context.assets.open("seeds/$filename").bufferedReader().use { it.readText() }
        return JSONObject(json)
    }
}
