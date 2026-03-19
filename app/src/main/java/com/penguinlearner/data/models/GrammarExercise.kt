package com.penguinlearner.data.models

data class GrammarExercise(
    val id: Int,
    val passage: String,
    val taskType: String, // "nouns", "adverb", "fronted_adverbial"
    val taskInstruction: String,
    val words: List<GrammarWord>,
    val chapter: String
)

data class GrammarWord(
    val word: String,
    val isCorrect: Boolean,
    val startIndex: Int,
    val endIndex: Int
)

data class GrammarData(
    val exercises: List<GrammarExercise>
)
