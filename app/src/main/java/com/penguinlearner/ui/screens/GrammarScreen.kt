package com.penguinlearner.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.penguinlearner.data.models.GrammarExercise
import com.penguinlearner.data.models.GrammarWord
import com.penguinlearner.data.repository.ContentRepository
import com.penguinlearner.data.repository.ProgressRepository
import com.penguinlearner.ui.components.*
import com.penguinlearner.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GrammarScreen(
    contentRepository: ContentRepository,
    progressRepository: ProgressRepository,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var exercises by remember { mutableStateOf<List<GrammarExercise>>(emptyList()) }
    var currentIndex by rememberSaveable { mutableIntStateOf(0) }
    var selectedWords by remember { mutableStateOf<Set<String>>(emptySet()) }
    var showResults by remember { mutableStateOf(false) }
    val completedIds by progressRepository.completedGrammarIds.collectAsState(initial = emptySet())

    LaunchedEffect(Unit) {
        exercises = contentRepository.loadGrammarExercises()
    }

    if (exercises.isEmpty()) {
        EmptyStateMessage("Lade Grammatik-Übungen...")
        return
    }

    val currentExercise = exercises[currentIndex]
    val isCompleted = completedIds.contains(currentExercise.id)

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
                contentDescription = "Zurück",
                tint = NavyBlue
            )
        }

        // Progress
        ProgressHeader(
            current = currentIndex + 1,
            total = exercises.size
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Scrollable content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Task instruction
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = NavyBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = getTaskTypeLabel(currentExercise.taskType),
                        style = MaterialTheme.typography.labelMedium,
                        color = PenguinYellow
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentExercise.taskInstruction,
                        style = MaterialTheme.typography.titleMedium,
                        color = PenguinWhite,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Passage with tappable words
            TappablePassage(
                exercise = currentExercise,
                selectedWords = selectedWords,
                showResults = showResults,
                onWordTap = { word ->
                    if (!showResults) {
                        selectedWords = if (selectedWords.contains(word)) {
                            selectedWords - word
                        } else {
                            selectedWords + word
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Chapter reference
            ChapterReference(chapter = currentExercise.chapter)

            Spacer(modifier = Modifier.height(24.dp))

            // Check button or results
            if (!showResults) {
                Button(
                    onClick = { showResults = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedWords.isNotEmpty(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Prüfen", fontWeight = FontWeight.Bold)
                }
            } else {
                ResultSummary(
                    exercise = currentExercise,
                    selectedWords = selectedWords,
                    onComplete = {
                        scope.launch {
                            progressRepository.markGrammarCompleted(currentExercise.id)
                        }
                    },
                    isCompleted = isCompleted
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation
        NavigationButtons(
            onPrevious = {
                if (currentIndex > 0) {
                    currentIndex--
                    selectedWords = emptySet()
                    showResults = false
                }
            },
            onNext = {
                if (currentIndex < exercises.size - 1) {
                    currentIndex++
                    selectedWords = emptySet()
                    showResults = false
                }
            },
            isPreviousEnabled = currentIndex > 0,
            isNextEnabled = currentIndex < exercises.size - 1
        )
    }
}

@Composable
private fun TappablePassage(
    exercise: GrammarExercise,
    selectedWords: Set<String>,
    showResults: Boolean,
    onWordTap: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = IceBlue),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Display words as chips that can be tapped
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                exercise.words.forEach { grammarWord ->
                    val isSelected = selectedWords.contains(grammarWord.word)
                    val backgroundColor = when {
                        showResults && grammarWord.isCorrect && isSelected -> CorrectGreen
                        showResults && grammarWord.isCorrect && !isSelected -> CorrectGreenLight
                        showResults && !grammarWord.isCorrect && isSelected -> WrongRed
                        isSelected -> PenguinYellow
                        else -> Color.Transparent
                    }
                    val textColor = when {
                        showResults && grammarWord.isCorrect && isSelected -> PenguinWhite
                        showResults && grammarWord.isCorrect && !isSelected -> CorrectGreen
                        showResults && !grammarWord.isCorrect && isSelected -> PenguinWhite
                        isSelected -> NavyBlueDark
                        else -> NavyBlueDark
                    }
                    val borderColor = when {
                        showResults && grammarWord.isCorrect -> CorrectGreen
                        showResults && isSelected && !grammarWord.isCorrect -> WrongRed
                        isSelected -> PenguinYellow
                        else -> Color.Transparent
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(backgroundColor)
                            .then(
                                if (borderColor != Color.Transparent) {
                                    Modifier.border(2.dp, borderColor, RoundedCornerShape(8.dp))
                                } else Modifier
                            )
                            .clickable(enabled = !showResults) { onWordTap(grammarWord.word) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = grammarWord.word,
                            style = MaterialTheme.typography.bodyLarge,
                            color = textColor,
                            fontWeight = if (isSelected || (showResults && grammarWord.isCorrect))
                                FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultSummary(
    exercise: GrammarExercise,
    selectedWords: Set<String>,
    onComplete: () -> Unit,
    isCompleted: Boolean
) {
    val correctWords = exercise.words.filter { it.isCorrect }.map { it.word }.toSet()
    val correctSelections = selectedWords.intersect(correctWords)
    val incorrectSelections = selectedWords - correctWords
    val missedWords = correctWords - selectedWords

    val score = correctSelections.size
    val total = correctWords.size

    LaunchedEffect(score, total) {
        if (score == total && incorrectSelections.isEmpty()) {
            onComplete()
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (score == total && incorrectSelections.isEmpty())
                CorrectGreenLight else WrongRedLight
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (score == total && incorrectSelections.isEmpty())
                    "Perfekt!" else "Ergebnis",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (score == total && incorrectSelections.isEmpty())
                    CorrectGreen else WrongRed
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$score von $total richtig",
                style = MaterialTheme.typography.bodyLarge,
                color = NavyBlueDark
            )

            if (missedWords.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Vergessen: ${missedWords.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PenguinOrange
                )
            }

            if (incorrectSelections.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Falsch markiert: ${incorrectSelections.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = WrongRed
                )
            }

            if (isCompleted) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Abgeschlossen!",
                    style = MaterialTheme.typography.labelMedium,
                    color = CorrectGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun getTaskTypeLabel(taskType: String): String {
    return when (taskType) {
        "nouns" -> "Nomen finden"
        "adverb" -> "Adverbien finden"
        "fronted_adverbial" -> "Vorangestellte Adverbiale"
        else -> "Grammatik-Übung"
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    // Simple flow row implementation using layout
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement
    ) {
        content()
    }
}
