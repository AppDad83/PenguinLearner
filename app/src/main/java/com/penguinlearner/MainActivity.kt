package com.penguinlearner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.penguinlearner.data.repository.ContentRepository
import com.penguinlearner.data.repository.ProgressRepository
import com.penguinlearner.ui.navigation.PenguinNavHost
import com.penguinlearner.ui.theme.PenguinLearnerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentRepository = ContentRepository(this)
        val progressRepository = ProgressRepository(this)

        setContent {
            PenguinLearnerTheme {
                PenguinLearnerApp(
                    contentRepository = contentRepository,
                    progressRepository = progressRepository
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PenguinLearnerApp(
    contentRepository: ContentRepository,
    progressRepository: ProgressRepository
) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Penguin Learner",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PenguinNavHost(
                navController = navController,
                contentRepository = contentRepository,
                progressRepository = progressRepository
            )
        }
    }
}
