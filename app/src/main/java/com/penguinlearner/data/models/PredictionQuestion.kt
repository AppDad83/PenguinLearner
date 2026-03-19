package com.penguinlearner.data.models

data class PredictionQuestion(
    val id: Int,
    val passage: String,
    val question: String,
    val modelAnswer: String,
    val hints: List<String>,
    val chapter: String
)

data class PredictionData(
    val questions: List<PredictionQuestion>
)
