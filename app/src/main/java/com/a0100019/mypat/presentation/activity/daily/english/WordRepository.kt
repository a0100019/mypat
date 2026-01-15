package com.a0100019.mypat.presentation.activity.daily.english

import android.content.Context
import kotlinx.serialization.json.Json

object WordRepository {
    fun loadWords(context: Context): List<String> {
        val jsonString = context.assets.open("text/words.json")
            .bufferedReader()
            .use { it.readText() }
        return Json.decodeFromString<List<String>>(jsonString)
    }
}
