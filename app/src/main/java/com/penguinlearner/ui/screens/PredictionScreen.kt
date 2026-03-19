package com.penguinlearner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.penguinlearner.data.models.PredictionQuestion
import com.penguinlearner.data.repository.ContentRepository
import com.penguinlearner.data.repository.DictionaryRepository
import com.penguinlearner.data.repository.ProgressRepository
import com.penguinlearner.ui.components.*
import com.penguinlearner.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictionScreen(
    contentRepository: ContentRepository,
    progressRepository: ProgressRepository,
    dictionaryRepository: DictionaryRepository,
    onNavigateBack: () -> Unit,
    onNavigateToChapters: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var questions by remember { mutableStateOf<List<PredictionQuestion>>(emptyList()) }
    var currentIndex by rememberSaveable { mutableIntStateOf(0) }
    var userAnswer by remember { mutableStateOf("") }
    var showModelAnswer by remember { mutableStateOf(false) }
    var hasAnswered by remember { mutableStateOf(false) }
    val results by progressRepository.predictionResults.collectAsState(initial = emptyMap())

    // Highlighter state
    var highlightEnabled by remember { mutableStateOf(false) }
    var passageHighlights by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var questionHighlights by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var hintsHighlights by remember { mutableStateOf<Map<Int, Set<Int>>>(emptyMap()) }
    var modelAnswerHighlights by remember { mutableStateOf<Set<Int>>(emptySet()) }

    // Translation popup state
    var popupState by remember { mutableStateOf<PopupState?>(null) }

    fun clearHighlights() {
        passageHighlights = emptySet()
        questionHighlights = emptySet()
        hintsHighlights = emptyMap()
        modelAnswerHighlights = emptySet()
    }

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
        // Top bar with back and chapters buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
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
            TextButton(onClick = onNavigateToChapters) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Kapitel")
            }
        }

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
            // Section title with highlighter toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Vorhersage",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val hasHighlights = passageHighlights.isNotEmpty() ||
                                       questionHighlights.isNotEmpty() ||
                                       hintsHighlights.values.any { it.isNotEmpty() } ||
                                       modelAnswerHighlights.isNotEmpty()
                    if (highlightEnabled) {
                        ClearHighlightsButton(
                            hasHighlights = hasHighlights,
                            onClear = { clearHighlights() }
                        )
                    }
                    HighlighterToggle(
                        isEnabled = highlightEnabled,
                        onToggle = { highlightEnabled = !highlightEnabled }
                    )
                }
            }

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
            HighlightablePassageCard(
                passage = currentQuestion.passage,
                highlightedIndices = passageHighlights,
                onWordClick = { index ->
                    passageHighlights = if (passageHighlights.contains(index)) {
                        passageHighlights - index
                    } else {
                        passageHighlights + index
                    }
                    // Show translation popup
                    val word = extractWordFromText(currentQuestion.passage, index)
                    if (word.isNotEmpty()) {
                        val entry = dictionaryRepository.getTranslation(word)
                        popupState = PopupState(word, entry)
                    }
                },
                highlightEnabled = highlightEnabled
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Question
            HighlightableQuestionCard(
                question = currentQuestion.question,
                highlightedIndices = questionHighlights,
                onWordClick = { index ->
                    questionHighlights = if (questionHighlights.contains(index)) {
                        questionHighlights - index
                    } else {
                        questionHighlights + index
                    }
                    // Show translation popup
                    val word = extractWordFromText(currentQuestion.question, index)
                    if (word.isNotEmpty()) {
                        val entry = dictionaryRepository.getTranslation(word)
                        popupState = PopupState(word, entry)
                    }
                },
                highlightEnabled = highlightEnabled
            )

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
                    HighlightableHintsCard(
                        hints = currentQuestion.hints,
                        highlightedIndices = hintsHighlights,
                        onWordClick = { hintIndex, wordIndex ->
                            val currentHintHighlights = hintsHighlights[hintIndex] ?: emptySet()
                            hintsHighlights = if (currentHintHighlights.contains(wordIndex)) {
                                hintsHighlights + (hintIndex to (currentHintHighlights - wordIndex))
                            } else {
                                hintsHighlights + (hintIndex to (currentHintHighlights + wordIndex))
                            }
                            // Show translation popup
                            val hint = currentQuestion.hints.getOrNull(hintIndex) ?: ""
                            val word = extractWordFromText(hint, wordIndex)
                            if (word.isNotEmpty()) {
                                val entry = dictionaryRepository.getTranslation(word)
                                popupState = PopupState(word, entry)
                            }
                        },
                        highlightEnabled = highlightEnabled
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Model answer
            HighlightableModelAnswerCard(
                modelAnswer = currentQuestion.modelAnswer,
                highlightedIndices = modelAnswerHighlights,
                onWordClick = { index ->
                    modelAnswerHighlights = if (modelAnswerHighlights.contains(index)) {
                        modelAnswerHighlights - index
                    } else {
                        modelAnswerHighlights + index
                    }
                    // Show translation popup
                    val word = extractWordFromText(currentQuestion.modelAnswer, index)
                    if (word.isNotEmpty()) {
                        val entry = dictionaryRepository.getTranslation(word)
                        popupState = PopupState(word, entry)
                    }
                },
                highlightEnabled = highlightEnabled,
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
                    clearHighlights()
                }
            },
            onNext = {
                if (currentIndex < questions.size - 1) {
                    currentIndex++
                    userAnswer = ""
                    showModelAnswer = false
                    hasAnswered = false
                    clearHighlights()
                }
            },
            isPreviousEnabled = currentIndex > 0,
            isNextEnabled = currentIndex < questions.size - 1
        )
    }

    // Translation popup
    popupState?.let { state ->
        TranslationPopup(
            popupState = state,
            onDismiss = { popupState = null }
        )
    }
}
