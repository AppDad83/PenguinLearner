package com.penguinlearner.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.penguinlearner.data.repository.ContentRepository
import com.penguinlearner.data.repository.ProgressRepository
import com.penguinlearner.ui.screens.*

@Composable
fun PenguinNavHost(
    navController: NavHostController,
    contentRepository: ContentRepository,
    progressRepository: ProgressRepository
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Vocabulary.route
    ) {
        composable(BottomNavItem.Vocabulary.route) {
            VocabularyScreen(
                contentRepository = contentRepository,
                progressRepository = progressRepository
            )
        }

        composable(BottomNavItem.Grammar.route) {
            GrammarScreen(
                contentRepository = contentRepository,
                progressRepository = progressRepository
            )
        }

        composable(BottomNavItem.Inference.route) {
            InferenceScreen(
                contentRepository = contentRepository,
                progressRepository = progressRepository
            )
        }

        composable(BottomNavItem.Prediction.route) {
            PredictionScreen(
                contentRepository = contentRepository,
                progressRepository = progressRepository
            )
        }

        composable(BottomNavItem.Explanation.route) {
            ExplanationScreen(
                contentRepository = contentRepository,
                progressRepository = progressRepository
            )
        }

        composable(BottomNavItem.Retrieval.route) {
            RetrievalScreen(
                contentRepository = contentRepository,
                progressRepository = progressRepository
            )
        }

        composable(BottomNavItem.Sequencing.route) {
            SequencingScreen(
                contentRepository = contentRepository,
                progressRepository = progressRepository
            )
        }
    }
}
