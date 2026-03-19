package com.penguinlearner.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.penguinlearner.data.models.RetrievalQuestion
import com.penguinlearner.data.repository.ContentRepository
import com.penguinlearner.data.repository.ProgressRepository
import com.penguinlearner.ui.components.*
import com.penguinlearner.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RetrievalScreen(
    contentRepository: ContentRepository,
    progressRepository: ProgressRepository
) {
    val scope = rememberCoroutineScope()
    var questions by remember { mutableStateOf<List<RetrievalQuestion>>(emptyList()) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedAnswerIndex by remember { mutableStateOf<Int?>(null) }
    var hasAnswered by remember { mutableStateOf(false) }
    var localScore by remember { mutableIntStateOf(0) }
    var localTotal by remember { mutableIntStateOf(0) }
    val retrievalProgress by progressRepository.retrievalProgress.collectAsState(initial = Pair(0, 0))

    LaunchedEffect(Unit) {
        questions = contentRepository.loadRetrievalQuestions().shuffled()
    }

    if (questions.isEmpty()) {
        EmptyStateMessage("Lade Quiz...")
        return
    }

    val currentQuestion = questions[currentIndex]
    val isQuizComplete = currentIndex >= questions.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
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
                        text = "Quiz",
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
                        text = "$localScore / $localTotal",
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
            // Question
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = IceBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = currentQuestion.question,
                    modifier = Modifier.padding(20.dp),
                    style = MaterialTheme.typography.titleLarge,
                    color = NavyBlueDark,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Answer options
            currentQuestion.options.forEachIndexed { index, option ->
                val isSelected = selectedAnswerIndex == index
                val isCorrect = index == currentQuestion.correctAnswerIndex

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
                        verticalAlignment = Alignment.CenterVertically
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
                            text = option,
                            style = MaterialTheme.typography.bodyLarge,
                            color = textColor
                        )
                    }
                }
            }

            // Explanation after answering
            if (hasAnswered) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedAnswerIndex == currentQuestion.correctAnswerIndex)
                            CorrectGreenLight else WrongRedLight
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (selectedAnswerIndex == currentQuestion.correctAnswerIndex)
                                "Richtig!" else "Leider falsch",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedAnswerIndex == currentQuestion.correctAnswerIndex)
                                CorrectGreen else WrongRed
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentQuestion.explanation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = NavyBlueDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentQuestion.chapter,
                            style = MaterialTheme.typography.labelSmall,
                            color = PenguinGray
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
                        localTotal++
                        val isCorrect = selectedAnswerIndex == currentQuestion.correctAnswerIndex
                        if (isCorrect) {
                            localScore++
                        }
                        scope.launch {
                            progressRepository.updateRetrievalScore(isCorrect)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedAnswerIndex != null,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Antwort prüfen", fontWeight = FontWeight.Bold)
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (currentIndex == questions.size - 1) {
                    // Quiz complete - show restart
                    OutlinedButton(
                        onClick = {
                            currentIndex = 0
                            selectedAnswerIndex = null
                            hasAnswered = false
                            localScore = 0
                            localTotal = 0
                            questions = questions.shuffled()
                            scope.launch {
                                progressRepository.resetRetrievalScore()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Neu starten")
                    }
                }

                Button(
                    onClick = {
                        if (currentIndex < questions.size - 1) {
                            currentIndex++
                            selectedAnswerIndex = null
                            hasAnswered = false
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = currentIndex < questions.size - 1,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (currentIndex < questions.size - 1) "Nächste Frage" else "Fertig!",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
