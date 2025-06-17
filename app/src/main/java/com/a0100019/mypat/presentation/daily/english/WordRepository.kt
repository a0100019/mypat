package com.a0100019.mypat.presentation.daily.english

import android.content.Context
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object WordRepository {
    fun loadWords(context: Context): List<String> {
        val jsonString = context.assets.open("words/words.json")
            .bufferedReader()
            .use { it.readText() }
        return Json.decodeFromString(jsonString)
    }
}