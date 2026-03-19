package com.penguinlearner.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.penguinlearner.data.models.DictionaryData
import com.penguinlearner.data.models.DictionaryEntry
import java.io.InputStreamReader

class DictionaryRepository(private val context: Context) {

    private val gson = Gson()
    private var dictionary: Map<String, DictionaryEntry>? = null

    private fun loadDictionary(): Map<String, DictionaryEntry> {
        if (dictionary == null) {
            dictionary = try {
                val inputStream = context.assets.open("data/dictionary.json")
                val reader = InputStreamReader(inputStream)
                val data = gson.fromJson(reader, DictionaryData::class.java)
                reader.close()
                data.dictionary
            } catch (e: Exception) {
                e.printStackTrace()
                emptyMap()
            }
        }
        return dictionary!!
    }

    fun getTranslation(word: String): DictionaryEntry? {
        val normalizedWord = normalizeWord(word)

        // 1. Direkte Suche
        loadDictionary()[normalizedWord]?.let { return it }

        // 2. Stemming-Fallback
        val stemmed = stemWord(normalizedWord)
        if (stemmed != normalizedWord) {
            loadDictionary()[stemmed]?.let { return it }
        }

        return null
    }

    private fun stemWord(word: String): String {
        return when {
            word.endsWith("ing") && word.length > 4 -> word.dropLast(3)
            word.endsWith("ed") && word.length > 3 -> word.dropLast(2)
            word.endsWith("ies") -> word.dropLast(3) + "y"
            word.endsWith("es") && word.length > 3 -> word.dropLast(2)
            word.endsWith("s") && word.length > 2 -> word.dropLast(1)
            word.endsWith("ly") && word.length > 3 -> word.dropLast(2)
            else -> word
        }
    }

    private fun normalizeWord(word: String): String {
        return word
            .lowercase()
            .trim()
            .replace(Regex("[.,!?\"'();:—–-]"), "")
    }
}
