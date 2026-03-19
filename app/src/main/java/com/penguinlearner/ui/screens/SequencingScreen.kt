package com.penguinlearner.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.penguinlearner.data.models.SequenceEvent
import com.penguinlearner.data.models.SequencingExercise
import com.penguinlearner.data.repository.ContentRepository
import com.penguinlearner.data.repository.ProgressRepository
import com.penguinlearner.ui.components.*
import com.penguinlearner.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun SequencingScreen(
    contentRepository: ContentRepository,
    progressRepository: ProgressRepository
) {
    val scope = rememberCoroutineScope()
    var exercises by remember { mutableStateOf<List<SequencingExercise>>(emptyList()) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var currentOrder by remember { mutableStateOf<List<SequenceEvent>>(emptyList()) }
    var showResults by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    val completedIds by progressRepository.completedSequencingIds.collectAsState(initial = emptySet())

    LaunchedEffect(Unit) {
        exercises = contentRepository.loadSequencingExercises()
    }

    LaunchedEffect(currentIndex, exercises) {
        if (exercises.isNotEmpty()) {
            currentOrder = exercises[currentIndex].events.shuffled()
            showResults = false
            isCorrect = false
        }
    }

    if (exercises.isEmpty()) {
        EmptyStateMessage("Lade Reihenfolge-Übungen...")
        return
    }

    val currentExercise = exercises[currentIndex]
    val isCompleted = completedIds.contains(currentExercise.id)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Progress
        ProgressHeader(
            current = currentIndex + 1,
            total = exercises.size
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = NavyBlue),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Reihenfolge",
                    style = MaterialTheme.typography.labelMedium,
                    color = PenguinYellow
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currentExercise.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PenguinWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Bringe die Ereignisse in die richtige Reihenfolge",
                    style = MaterialTheme.typography.bodySmall,
                    color = PenguinWhite.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        ChapterReference(chapter = currentExercise.chapter)

        Spacer(modifier = Modifier.height(16.dp))

        // Sortable list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(
                items = currentOrder,
                key = { _, item -> item.id }
            ) { index, event ->
                SequenceCard(
                    event = event,
                    position = index + 1,
                    showResult = showResults,
                    isInCorrectPosition = event.correctPosition == index + 1,
                    correctPosition = event.correctPosition,
                    onMoveUp = {
                        if (index > 0 && !showResults) {
                            val newList = currentOrder.toMutableList()
                            val item = newList.removeAt(index)
                            newList.add(index - 1, item)
                            currentOrder = newList
                        }
                    },
                    onMoveDown = {
                        if (index < currentOrder.size - 1 && !showResults) {
                            val newList = currentOrder.toMutableList()
                            val item = newList.removeAt(index)
                            newList.add(index + 1, item)
                            currentOrder = newList
                        }
                    },
                    canMoveUp = index > 0 && !showResults,
                    canMoveDown = index < currentOrder.size - 1 && !showResults
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action buttons
        if (!showResults) {
            Button(
                onClick = {
                    showResults = true
                    isCorrect = currentOrder.mapIndexed { index, event ->
                        event.correctPosition == index + 1
                    }.all { it }

                    if (isCorrect) {
                        scope.launch {
                            progressRepository.markSequencingCompleted(currentExercise.id)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Prüfen", fontWeight = FontWeight.Bold)
            }
        } else {
            // Result card
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
                        text = if (isCorrect) "Perfekt!" else "Nicht ganz richtig",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isCorrect) CorrectGreen else WrongRed
                    )
                    if (!isCorrect) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Die richtige Reihenfolge ist oben mit Nummern markiert.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NavyBlueDark,
                            textAlign = TextAlign.Center
                        )
                    }
                    if (isCompleted || isCorrect) {
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

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        currentOrder = currentExercise.events.shuffled()
                        showResults = false
                        isCorrect = false
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Nochmal")
                }

                if (currentIndex < exercises.size - 1) {
                    Button(
                        onClick = {
                            currentIndex++
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Weiter")
                    }
                }
            }
        }

        // Navigation for non-result state
        if (!showResults) {
            Spacer(modifier = Modifier.height(8.dp))
            NavigationButtons(
                onPrevious = {
                    if (currentIndex > 0) {
                        currentIndex--
                    }
                },
                onNext = {
                    if (currentIndex < exercises.size - 1) {
                        currentIndex++
                    }
                },
                isPreviousEnabled = currentIndex > 0,
                isNextEnabled = currentIndex < exercises.size - 1
            )
        }
    }
}

@Composable
private fun SequenceCard(
    event: SequenceEvent,
    position: Int,
    showResult: Boolean,
    isInCorrectPosition: Boolean,
    correctPosition: Int,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    canMoveUp: Boolean,
    canMoveDown: Boolean
) {
    val backgroundColor = when {
        showResult && isInCorrectPosition -> CorrectGreenLight
        showResult && !isInCorrectPosition -> WrongRedLight
        else -> MaterialTheme.colorScheme.surface
    }

    val borderColor = when {
        showResult && isInCorrectPosition -> CorrectGreen
        showResult && !isInCorrectPosition -> WrongRed
        else -> PenguinLightGray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, borderColor, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Position number
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = NavyBlue,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = position.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PenguinWhite
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Event text
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = NavyBlueDark
                )
                if (showResult && !isInCorrectPosition) {
                    Text(
                        text = "Richtig: Position $correctPosition",
                        style = MaterialTheme.typography.labelSmall,
                        color = WrongRed
                    )
                }
            }

            // Move buttons (only when not showing results)
            if (!showResult) {
                Column {
                    IconButton(
                        onClick = onMoveUp,
                        enabled = canMoveUp,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Nach oben",
                            tint = if (canMoveUp) NavyBlue else PenguinLightGray
                        )
                    }
                    IconButton(
                        onClick = onMoveDown,
                        enabled = canMoveDown,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Nach unten",
                            tint = if (canMoveDown) NavyBlue else PenguinLightGray
                        )
                    }
                }
            }
        }
    }
}
