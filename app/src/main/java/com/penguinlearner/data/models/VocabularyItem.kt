package com.penguinlearner.data.models

data class VocabularyItem(
    val id: Int,
    val englishWord: String,
    val germanTranslation: String,
    val definition: String,
    val exampleSentence: String,
    val blankSentence: String, // Sentence with ___ for fill-in-blank
    val answer: String, // The word that fills the blank
    val chapter: String
)

data class VocabularyData(
    val vocabulary: List<VocabularyItem>
)
