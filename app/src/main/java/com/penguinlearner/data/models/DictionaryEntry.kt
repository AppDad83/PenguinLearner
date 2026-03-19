package com.penguinlearner.data.models

data class DictionaryEntry(
    val translation: String,
    val partOfSpeech: String? = null  // "noun", "verb", "adjective", etc.
)

data class DictionaryData(
    val dictionary: Map<String, DictionaryEntry>
)
