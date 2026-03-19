package com.penguinlearner.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.penguinlearner.data.repository.ContentRepository
import com.penguinlearner.data.repository.ProgressRepository
import com.penguinlearner.ui.screens.*

object NavRoutes {
    const val HOME = "home"
    const val VOCABULARY = "vocabulary"
    const val GRAMMAR = "grammar"
    const val INFERENCE = "inference"
    const val PREDICTION = "prediction"
    const val EXPLANATION = "explanation"
    const val RETRIEVAL = "retrieval"
    const val SEQUENCING = "sequencing"
    const val CHAPTERS = "chapters"
}

@Composable
fun PenguinNavHost(
    navController: NavHostController,
    contentRepository: ContentRepository,
    progressRepository: ProgressRepository
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME
    ) {
        composable(NavRoutes.HOME) {
            HomeScreen(
                contentRepository = contentRepository,
                progressRepository = progressRepository,
                onNavigateToVocabulary = { navController.navigate(NavRoutes.VOCABULARY) },
                onNavigateToGrammar = { navController.navigate(NavRoutes.GRAMMAR) },
                onNavigateToInference = { navController.navigate(NavRoutes.INFERENCE) },
                onNavigateToPrediction = { navController.navigate(NavRoutes.PREDICTION) },
                onNavigateToExplanation = { navController.navigate(NavRoutes.EXPLANATION) },
                onNavigateToRetrieval = { navController.navigate(NavRoutes.RETRIEVAL) },
                onNavigateToSequencing = { navController.navigate(NavRoutes.SEQUENCING) },
                onNavigateToChapters = { navController.navigate(NavRoutes.CHAPTERS) }
            )
        }

        composable(NavRoutes.VOCABULARY) {
            VocabularyScreen(
                contentRepository = contentRepository,
                progressRepository = progressRepository,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.GRAMMAR) {
            GrammarScreen(
                contentRepository = contentRepository,
                progressRepository = progressRepository,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.INFERENCE) {
            InferenceScreen(
                contentRepository = contentRepository,
                progressRepository = progressRepository,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToChapters = { navController.navigate(NavRoutes.CHAPTERS) }
            )
        }

        composable(NavRoutes.PREDICTION) {
            PredictionScreen(
                contentRepository = contentRepository,
                progressRepository = progressRepository,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToChapters = { navController.navigate(NavRoutes.CHAPTERS) }
            )
        }

        composable(NavRoutes.EXPLANATION) {
            ExplanationScreen(
                contentRepository = contentRepository,
                progressRepository = progressRepository,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToChapters = { navController.navigate(NavRoutes.CHAPTERS) }
            )
        }

        composable(NavRoutes.RETRIEVAL) {
            RetrievalScreen(
                contentRepository = contentRepository,
                progressRepository = progressRepository,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.SEQUENCING) {
            SequencingScreen(
                contentRepository = contentRepository,
                progressRepository = progressRepository,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.CHAPTERS) {
            ChaptersScreen(
                contentRepository = contentRepository,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
