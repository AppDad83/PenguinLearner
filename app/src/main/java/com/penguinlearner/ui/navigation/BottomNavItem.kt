package com.penguinlearner.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val titleDe: String,
    val titleEn: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Vocabulary : BottomNavItem(
        route = "vocabulary",
        titleDe = "Vokabeln",
        titleEn = "Vocabulary",
        selectedIcon = Icons.Filled.MenuBook,
        unselectedIcon = Icons.Outlined.MenuBook
    )

    data object Grammar : BottomNavItem(
        route = "grammar",
        titleDe = "Grammatik",
        titleEn = "Grammar",
        selectedIcon = Icons.Filled.Edit,
        unselectedIcon = Icons.Outlined.Edit
    )

    data object Inference : BottomNavItem(
        route = "inference",
        titleDe = "Schluss-\nfolgerung",
        titleEn = "Inference",
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search
    )

    data object Prediction : BottomNavItem(
        route = "prediction",
        titleDe = "Vorhersage",
        titleEn = "Prediction",
        selectedIcon = Icons.Filled.AutoAwesome,
        unselectedIcon = Icons.Outlined.AutoAwesome
    )

    data object Explanation : BottomNavItem(
        route = "explanation",
        titleDe = "Erklärung",
        titleEn = "Explanation",
        selectedIcon = Icons.Filled.Chat,
        unselectedIcon = Icons.Outlined.Chat
    )

    data object Retrieval : BottomNavItem(
        route = "retrieval",
        titleDe = "Quiz",
        titleEn = "Quiz",
        selectedIcon = Icons.Filled.Quiz,
        unselectedIcon = Icons.Outlined.Quiz
    )

    data object Sequencing : BottomNavItem(
        route = "sequencing",
        titleDe = "Reihenfolge",
        titleEn = "Sequencing",
        selectedIcon = Icons.Filled.FormatListNumbered,
        unselectedIcon = Icons.Outlined.FormatListNumbered
    )

    companion object {
        val items = listOf(
            Vocabulary,
            Grammar,
            Inference,
            Prediction,
            Explanation,
            Retrieval,
            Sequencing
        )
    }
}
