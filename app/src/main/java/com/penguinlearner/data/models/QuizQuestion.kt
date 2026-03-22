package com.penguinlearner.data.models

data class QuizAnswer(
    val text: String,
    val correct: Boolean
)

data class InferenceQuizQuestion(
    val id: String,
    val passage: String,
    val chapter: String,
    val question: String,
    val answers: List<QuizAnswer>
)

data class PredictionQuizQuestion(
    val id: String,
    val passage: String,
    val chapter: String,
    val scenario: String,
    val question: String,
    val answers: List<QuizAnswer>
)

data class ExplanationQuizQuestion(
    val id: String,
    val passage: String,
    val chapter: String,
    val question: String,
    val answers: List<QuizAnswer>
)

data class QuizData(
    val inference: List<InferenceQuizQuestion>,
    val prediction: List<PredictionQuizQuestion>,
    val explanation: List<ExplanationQuizQuestion>
)
