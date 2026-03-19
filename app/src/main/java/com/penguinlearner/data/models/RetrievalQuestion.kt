package com.penguinlearner.data.models

data class RetrievalQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String,
    val chapter: String
)

data class RetrievalData(
    val questions: List<RetrievalQuestion>
)
