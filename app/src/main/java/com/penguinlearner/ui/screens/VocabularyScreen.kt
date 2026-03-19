package com.penguinlearner.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.penguinlearner.data.models.VocabularyItem
import com.penguinlearner.data.repository.ContentRepository
import com.penguinlearner.data.repository.ProgressRepository
import com.penguinlearner.ui.components.*
import com.penguinlearner.ui.theme.*
import kotlinx.coroutines.launch

enum class VocabularyMode {
    FLASHCARD,
    FILL_BLANK
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyScreen(
    contentRepository: ContentRepository,
    progressRepository: ProgressRepository
) {
    val scope = rememberCoroutineScope()
    var vocabulary by remember { mutableStateOf<List<VocabularyItem>>(emptyList()) }
    var currentIndex by rememberSaveable { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var mode by remember { mutableStateOf(VocabularyMode.FLASHCARD) }
    var userAnswer by remember { mutableStateOf("") }
    var showFillResult by remember { mutableStateOf(false) }
    val learnedIds by progressRepository.learnedVocabularyIds.collectAsState(initial = emptySet())

    LaunchedEffect(Unit) {
        vocabulary = contentRepository.loadVocabulary()
    }

    if (vocabulary.isEmpty()) {
        EmptyStateMessage("Lade Vokabeln...")
        return
    }

    val currentItem = vocabulary[currentIndex]
    val isLearned = learnedIds.contains(currentItem.id)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Mode Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            FilterChip(
                selected = mode == VocabularyMode.FLASHCARD,
                onClick = {
                    mode = VocabularyMode.FLASHCARD
                    isFlipped = false
                    showFillResult = false
                    userAnswer = ""
                },
                label = { Text("Karteikarten") },
                leadingIcon = {
                    Icon(Icons.Default.Flip, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            )
            Spacer(modifier = Modifier.width(12.dp))
            FilterChip(
                selected = mode == VocabularyMode.FILL_BLANK,
                onClick = {
                    mode = VocabularyMode.FILL_BLANK
                    isFlipped = false
                    showFillResult = false
                    userAnswer = ""
                },
                label = { Text("Lückentext") },
                leadingIcon = {
                    Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress
        ProgressHeader(
            current = currentIndex + 1,
            total = vocabulary.size
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Content based on mode
        when (mode) {
            VocabularyMode.FLASHCARD -> {
                FlashcardContent(
                    item = currentItem,
                    isFlipped = isFlipped,
                    onFlip = { isFlipped = !isFlipped },
                    modifier = Modifier.weight(1f)
                )
            }
            VocabularyMode.FILL_BLANK -> {
                FillBlankContent(
                    item = currentItem,
                    userAnswer = userAnswer,
                    onAnswerChange = { userAnswer = it },
                    showResult = showFillResult,
                    onCheck = { showFillResult = true },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Learned checkbox
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            LearnedCheckbox(
                isLearned = isLearned,
                onToggle = { learned ->
                    scope.launch {
                        progressRepository.markVocabularyLearned(currentItem.id, learned)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation
        NavigationButtons(
            onPrevious = {
                if (currentIndex > 0) {
                    currentIndex--
                    isFlipped = false
                    showFillResult = false
                    userAnswer = ""
                }
            },
            onNext = {
                if (currentIndex < vocabulary.size - 1) {
                    currentIndex++
                    isFlipped = false
                    showFillResult = false
                    userAnswer = ""
                }
            },
            isPreviousEnabled = currentIndex > 0,
            isNextEnabled = currentIndex < vocabulary.size - 1
        )
    }
}

@Composable
private fun FlashcardContent(
    item: VocabularyItem,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(400),
        label = "flip"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onFlip),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.5f)
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12f * density
                },
            colors = CardDefaults.cardColors(
                containerColor = if (rotation <= 90f) NavyBlue else CorrectGreenLight
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (rotation <= 90f) {
                    // Front - English word
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = item.englishWord,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = PenguinWhite,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Tippen zum Umdrehen",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PenguinWhite.copy(alpha = 0.7f)
                        )
                    }
                } else {
                    // Back - German translation & definition
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(24.dp)
                            .graphicsLayer { rotationY = 180f }
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = item.germanTranslation,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = NavyBlueDark,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = item.definition,
                            style = MaterialTheme.typography.bodyLarge,
                            color = NavyBlue,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "\"${item.exampleSentence}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NavyBlue.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FillBlankContent(
    item: VocabularyItem,
    userAnswer: String,
    onAnswerChange: (String) -> Unit,
    showResult: Boolean,
    onCheck: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val isCorrect = userAnswer.trim().equals(item.answer, ignoreCase = true)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // German translation as hint (English word is hidden - user must type it)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = NavyBlue),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Übersetze:",
                    style = MaterialTheme.typography.labelMedium,
                    color = PenguinYellow
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.germanTranslation,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = PenguinWhite,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sentence with blank
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = IceBlue),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = item.blankSentence,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = NavyBlueDark,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Input field
        OutlinedTextField(
            value = userAnswer,
            onValueChange = onAnswerChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Deine Antwort") },
            singleLine = true,
            enabled = !showResult,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                if (userAnswer.isNotBlank()) onCheck()
            }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NavyBlue,
                unfocusedBorderColor = PenguinGray
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!showResult) {
            Button(
                onClick = {
                    focusManager.clearFocus()
                    onCheck()
                },
                enabled = userAnswer.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Prüfen")
            }
        } else {
            // Show result
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCorrect) CorrectGreenLight else WrongRedLight
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isCorrect) "Richtig!" else "Nicht ganz...",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isCorrect) CorrectGreen else WrongRed
                    )
                    if (!isCorrect) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Richtige Antwort: ${item.answer}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = NavyBlueDark
                        )
                    }
                }
            }
        }
    }
}
