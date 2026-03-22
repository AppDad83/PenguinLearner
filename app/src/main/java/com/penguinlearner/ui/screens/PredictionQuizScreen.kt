package com.penguinlearner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.penguinlearner.data.models.PredictionQuizQuestion
import com.penguinlearner.data.repository.ContentRepository
import com.penguinlearner.data.repository.ProgressRepository
import com.penguinlearner.ui.components.*
import com.penguinlearner.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictionQuizScreen(
    contentRepository: ContentRepository,
    progressRepository: ProgressRepository,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var questions by remember { mutableStateOf<List<PredictionQuizQuestion>>(emptyList()) }
    var currentIndex by rememberSaveable { mutableIntStateOf(0) }
    var selectedAnswerIndex by remember { mutableStateOf<Int?>(null) }
    var hasAnswered by remember { mutableStateOf(false) }
    var localScore by remember { mutableIntStateOf(0) }
    var showSummary by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        questions = contentRepository.loadPredictionQuiz().shuffled()
    }

    if (questions.isEmpty()) {
        EmptyStateMessage("Lade Quiz...")
        return
    }

    // Summary Screen
    if (showSummary) {
        PredictionQuizSummaryScreen(
            score = localScore,
            total = questions.size,
            onTryAgain = {
                currentIndex = 0
                selectedAnswerIndex = null
                hasAnswered = false
                localScore = 0
                showSummary = false
                questions = questions.shuffled()
                scope.launch {
                    progressRepository.resetPredictionQuizScore()
                }
            },
            onNavigateBack = onNavigateBack
        )
        return
    }

    val currentQuestion = questions[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Back button
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.offset(x = (-12).dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Zurueck",
                tint = NavyBlue
            )
        }

        // Score header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = NavyBlue),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Prediction Quiz",
                        style = MaterialTheme.typography.titleMedium,
                        color = PenguinWhite
                    )
                    Text(
                        text = "Frage ${currentIndex + 1} von ${questions.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = PenguinWhite.copy(alpha = 0.7f)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Punkte",
                        style = MaterialTheme.typography.labelSmall,
                        color = PenguinYellow
                    )
                    Text(
                        text = "$localScore / ${currentIndex + if (hasAnswered) 1 else 0}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = PenguinWhite
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress bar
        LinearProgressIndicator(
            progress = (currentIndex + 1).toFloat() / questions.size,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = PenguinYellow,
            trackColor = PenguinLightGray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Question content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Passage
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = IceBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Chapter ${currentQuestion.chapter}",
                        style = MaterialTheme.typography.labelMedium,
                        color = PenguinGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"${currentQuestion.passage}\"",
                        style = MaterialTheme.typography.bodyLarge,
                        color = NavyBlueDark,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Scenario Box (distinct styling)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PenguinYellow.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Scenario:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = PenguinOrange
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentQuestion.scenario,
                        style = MaterialTheme.typography.bodyMedium,
                        color = NavyBlueDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Question
            Text(
                text = currentQuestion.question,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = NavyBlueDark,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Answer options
            currentQuestion.answers.forEachIndexed { index, answer ->
                val isSelected = selectedAnswerIndex == index
                val isCorrect = answer.correct

                val backgroundColor = when {
                    !hasAnswered && isSelected -> PenguinYellow
                    hasAnswered && isCorrect -> CorrectGreen
                    hasAnswered && isSelected && !isCorrect -> WrongRed
                    else -> MaterialTheme.colorScheme.surface
                }

                val textColor = when {
                    !hasAnswered && isSelected -> NavyBlueDark
                    hasAnswered && isCorrect -> PenguinWhite
                    hasAnswered && isSelected && !isCorrect -> PenguinWhite
                    else -> NavyBlueDark
                }

                val borderColor = when {
                    !hasAnswered && isSelected -> PenguinOrange
                    hasAnswered && isCorrect -> CorrectGreen
                    hasAnswered && isSelected && !isCorrect -> WrongRed
                    else -> PenguinLightGray
                }

                Card(
                    onClick = {
                        if (!hasAnswered) {
                            selectedAnswerIndex = index
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = backgroundColor),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(borderColor)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Option letter
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (hasAnswered && isCorrect) PenguinWhite.copy(alpha = 0.2f)
                                    else NavyBlue.copy(alpha = 0.1f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = ('A' + index).toString(),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = answer.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = textColor
                        )
                    }
                }
            }

            // Feedback after answering
            if (hasAnswered) {
                Spacer(modifier = Modifier.height(16.dp))
                val selectedAnswer = currentQuestion.answers.getOrNull(selectedAnswerIndex ?: -1)
                val correctAnswer = currentQuestion.answers.find { it.correct }
                val isCorrectAnswer = selectedAnswer?.correct == true

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCorrectAnswer) CorrectGreenLight else WrongRedLight
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isCorrectAnswer) "Richtig!" else "Leider falsch",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isCorrectAnswer) CorrectGreen else WrongRed
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = correctAnswer?.text ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NavyBlueDark
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action buttons
        if (!hasAnswered) {
            Button(
                onClick = {
                    if (selectedAnswerIndex != null) {
                        hasAnswered = true
                        val selectedAnswer = currentQuestion.answers.getOrNull(selectedAnswerIndex!!)
                        val isCorrect = selectedAnswer?.correct == true
                        if (isCorrect) {
                            localScore++
                        }
                        scope.launch {
                            progressRepository.updatePredictionQuizScore(isCorrect)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedAnswerIndex != null,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Antwort pruefen", fontWeight = FontWeight.Bold)
            }
        } else {
            Button(
                onClick = {
                    if (currentIndex < questions.size - 1) {
                        currentIndex++
                        selectedAnswerIndex = null
                        hasAnswered = false
                    } else {
                        showSummary = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (currentIndex < questions.size - 1) "Naechste Frage" else "Ergebnis anzeigen",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PredictionQuizSummaryScreen(
    score: Int,
    total: Int,
    onTryAgain: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val percentage = if (total > 0) (score.toFloat() / total * 100).toInt() else 0
    val stars = when {
        percentage >= 80 -> 3
        percentage >= 60 -> 2
        percentage >= 40 -> 1
        else -> 0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Stars
        Row {
            repeat(3) { index ->
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = if (index < stars) PenguinYellow else PenguinLightGray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Quiz beendet!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = NavyBlueDark
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "$score von $total richtig",
            style = MaterialTheme.typography.titleLarge,
            color = NavyBlue
        )

        Text(
            text = "$percentage%",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = if (percentage >= 60) CorrectGreen else PenguinOrange
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onTryAgain,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Noch einmal", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Zurueck zum Menu")
        }
    }
}
