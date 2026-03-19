package com.penguinlearner.data.models

data class Chapter(
    val number: Int,
    val title: String,
    val summary: List<String>
)

data class ChaptersData(
    val chapters: List<Chapter>
)
