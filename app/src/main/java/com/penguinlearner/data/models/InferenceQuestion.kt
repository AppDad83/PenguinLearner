package com.penguinlearner.data.models

data class InferenceQuestion(
    val id: Int,
    val passage: String,
    val question: String,
    val modelAnswer: String,
    val hints: List<String>,
    val chapter: String
)

data class InferenceData(
    val questions: List<InferenceQuestion>
)
