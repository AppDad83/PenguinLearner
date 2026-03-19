package com.penguinlearner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.penguinlearner.data.models.PredictionQuestion
import com.penguinlearner.data.repository.ContentRepository
import com.penguinlearner.data.repository.ProgressRepository
import com.penguinlearner.ui.components.*
import com.penguinlearner.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun PredictionScreen(
    contentRepository: ContentRepository,
    progressRepository: ProgressRepository
) {
    val scope = rememberCoroutineScope()
    var questions by remember { mutableStateOf<List<PredictionQuestion>>(emptyList()) }
    var currentIndex by rememberSaveable { mutableIntStateOf(0) }
    var userAnswer by remember { mutableStateOf("") }
    var showModelAnswer by remember { mutableStateOf(false) }
    var hasAnswered by remember { mutableStateOf(false) }
    val results by progressRepository.predictionResults.collectAsState(initial = emptyMap())

    LaunchedEffect(Unit) {
        questions = contentRepository.loadPredictionQuestions()
    }

    if (questions.isEmpty()) {
        EmptyStateMessage("Lade Vorhersagen...")
        return
    }

    val currentQuestion = questions[currentIndex]
    val previousResult = results[currentQuestion.id]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Progress
        ProgressHeader(
            current = currentIndex + 1,
            total = questions.size
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Scrollable content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Section title
            Text(
                text = "Vorhersage",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Previous result indicator
            if (previousResult != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (previousResult) CorrectGreenLight else WrongRedLight
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (previousResult) "Bereits geschafft!" else "Noch einmal versuchen",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (previousResult) CorrectGreen else WrongRed
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Passage
            PassageCard(passage = currentQuestion.passage)

            Spacer(modifier = Modifier.height(16.dp))

            // Question
            QuestionCard(question = currentQuestion.question)

            Spacer(modifier = Modifier.height(16.dp))

            // User answer input
            OutlinedTextField(
                value = userAnswer,
                onValueChange = { userAnswer = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                label = { Text("Deine Vorhersage") },
                placeholder = { Text("Was denkst du, wird passieren?") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NavyBlue,
                    unfocusedBorderColor = PenguinGray
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Hints
            if (currentQuestion.hints.isNotEmpty() && !showModelAnswer) {
                var showHints by remember { mutableStateOf(false) }

                if (!showHints) {
                    TextButton(onClick = { showHints = true }) {
                        Text("Hinweise anzeigen")
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PenguinYellow.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Hinweise:",
                                style = MaterialTheme.typography.labelLarge,
                                color = PenguinOrange
                            )
                            currentQuestion.hints.forEach { hint ->
                                Text(
                                    text = "• $hint",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Model answer
            ModelAnswerCard(
                modelAnswer = currentQuestion.modelAnswer,
                isVisible = showModelAnswer,
                onShowAnswer = { showModelAnswer = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Self assessment
            if (showModelAnswer && !hasAnswered) {
                SelfAssessmentButtons(
                    onGotIt = {
                        scope.launch {
                            progressRepository.savePredictionResult(currentQuestion.id, true)
                        }
                        hasAnswered = true
                    },
                    onNotYet = {
                        scope.launch {
                            progressRepository.savePredictionResult(currentQuestion.id, false)
                        }
                        hasAnswered = true
                    }
                )
            }

            // Chapter reference
            Spacer(modifier = Modifier.height(8.dp))
            ChapterReference(chapter = currentQuestion.chapter)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation
        NavigationButtons(
            onPrevious = {
                if (currentIndex > 0) {
                    currentIndex--
                    userAnswer = ""
                    showModelAnswer = false
                    hasAnswered = false
                }
            },
            onNext = {
                if (currentIndex < questions.size - 1) {
                    currentIndex++
                    userAnswer = ""
                    showModelAnswer = false
                    hasAnswered = false
                }
            },
            isPreviousEnabled = currentIndex > 0,
            isNextEnabled = currentIndex < questions.size - 1
        )
    }
}
