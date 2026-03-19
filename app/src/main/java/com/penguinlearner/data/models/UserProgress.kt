package com.penguinlearner.data.models

data class UserProgress(
    val learnedVocabularyIds: Set<Int> = emptySet(),
    val completedGrammarIds: Set<Int> = emptySet(),
    val inferenceResults: Map<Int, Boolean> = emptyMap(), // id -> gotIt
    val predictionResults: Map<Int, Boolean> = emptyMap(),
    val explanationResults: Map<Int, Boolean> = emptyMap(),
    val retrievalScore: Int = 0,
    val retrievalTotal: Int = 0,
    val completedSequencingIds: Set<Int> = emptySet()
)

data class SectionProgress(
    val completed: Int,
    val total: Int
) {
    val percentage: Float
        get() = if (total > 0) completed.toFloat() / total else 0f
}
