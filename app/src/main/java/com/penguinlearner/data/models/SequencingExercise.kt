package com.penguinlearner.data.models

data class SequencingExercise(
    val id: Int,
    val title: String,
    val events: List<SequenceEvent>,
    val chapter: String
)

data class SequenceEvent(
    val id: Int,
    val text: String,
    val correctPosition: Int
)

data class SequencingData(
    val exercises: List<SequencingExercise>
)
