package com.penguinlearner.data.models

data class ExplanationQuestion(
    val id: Int,
    val passage: String,
    val question: String,
    val modelAnswer: String,
    val hints: List<String>,
    val chapter: String
)

data class ExplanationData(
    val questions: List<ExplanationQuestion>
)
