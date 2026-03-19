package com.penguinlearner.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.penguinlearner.data.models.SequenceEvent
import com.penguinlearner.data.models.SequencingExercise
import com.penguinlearner.data.repository.ContentRepository
import com.penguinlearner.data.repository.ProgressRepository
import com.penguinlearner.ui.components.*
import com.penguinlearner.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SequencingScreen(
    contentRepository: ContentRepository,
    progressRepository: ProgressRepository
) {
    val scope = rememberCoroutineScope()
    var exercises by remember { mutableStateOf<List<SequencingExercise>>(emptyList()) }
    var currentIndex by rememberSaveable { mutableIntStateOf(0) }
    var currentOrder by remember { mutableStateOf<List<SequenceEvent>>(emptyList()) }
    var showResults by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    val completedIds by progressRepository.completedSequencingIds.collectAsState(initial = emptySet())

    // Drag state
    var draggedItemIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        exercises = contentRepository.loadSequencingExercises()
    }

    LaunchedEffect(currentIndex, exercises) {
        if (exercises.isNotEmpty()) {
            currentOrder = exercises[currentIndex].events.shuffled()
            showResults = false
            isCorrect = false
            draggedItemIndex = null
            dragOffset = 0f
        }
    }

    if (exercises.isEmpty()) {
        EmptyStateMessage("Lade Reihenfolge-Übungen...")
        return
    }

    val currentExercise = exercises[currentIndex]
    val isCompleted = completedIds.contains(currentExercise.id)

    // Calculate item height for drag detection
    val density = LocalDensity.current
    val itemHeightDp = 80.dp
    val itemHeightPx = with(density) { itemHeightDp.toPx() }
    val spacingPx = with(density) { 8.dp.toPx() }
    val totalItemHeightPx = itemHeightPx + spacingPx

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
                    text = if (showResults) "Ergebnis:" else "Halte und ziehe die Karten in die richtige Reihenfolge",
                    style = MaterialTheme.typography.bodySmall,
                    color = PenguinWhite.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        ChapterReference(chapter = currentExercise.chapter)

        Spacer(modifier = Modifier.height(16.dp))

        // Drag & Drop list
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                currentOrder.forEachIndexed { index, event ->
                    val isDragging = draggedItemIndex == index
                    val targetOffset = when {
                        draggedItemIndex == null -> 0f
                        isDragging -> dragOffset
                        draggedItemIndex!! < index && dragOffset > (index - draggedItemIndex!!) * itemHeightPx - itemHeightPx / 2 -> -totalItemHeightPx
                        draggedItemIndex!! > index && dragOffset < (index - draggedItemIndex!!) * itemHeightPx + itemHeightPx / 2 -> totalItemHeightPx
                        else -> 0f
                    }

                    val animatedOffset by animateFloatAsState(
                        targetValue = if (isDragging) dragOffset else targetOffset,
                        label = "offset"
                    )

                    val scale by animateFloatAsState(
                        targetValue = if (isDragging) 1.05f else 1f,
                        label = "scale"
                    )

                    val elevation by animateDpAsState(
                        targetValue = if (isDragging) 8.dp else 0.dp,
                        label = "elevation"
                    )

                    DraggableSequenceCard(
                        event = event,
                        position = index + 1,
                        showResult = showResults,
                        isInCorrectPosition = event.correctPosition == index + 1,
                        correctPosition = event.correctPosition,
                        isDragging = isDragging,
                        dragEnabled = !showResults,
                        offsetY = animatedOffset,
                        scale = scale,
                        elevation = elevation,
                        onDragStart = {
                            if (!showResults) {
                                draggedItemIndex = index
                                dragOffset = 0f
                            }
                        },
                        onDrag = { change ->
                            if (!showResults && draggedItemIndex != null) {
                                dragOffset += change
                            }
                        },
                        onDragEnd = {
                            if (!showResults && draggedItemIndex != null) {
                                // Calculate new position
                                val draggedIdx = draggedItemIndex!!
                                val offsetInItems = (dragOffset / totalItemHeightPx).roundToInt()
                                val newIndex = (draggedIdx + offsetInItems).coerceIn(0, currentOrder.size - 1)

                                if (newIndex != draggedIdx) {
                                    val newList = currentOrder.toMutableList()
                                    val item = newList.removeAt(draggedIdx)
                                    newList.add(newIndex, item)
                                    currentOrder = newList
                                }

                                draggedItemIndex = null
                                dragOffset = 0f
                            }
                        },
                        onDragCancel = {
                            draggedItemIndex = null
                            dragOffset = 0f
                        }
                    )
                }
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
private fun DraggableSequenceCard(
    event: SequenceEvent,
    position: Int,
    showResult: Boolean,
    isInCorrectPosition: Boolean,
    correctPosition: Int,
    isDragging: Boolean,
    dragEnabled: Boolean,
    offsetY: Float,
    scale: Float,
    elevation: Dp,
    onDragStart: () -> Unit,
    onDrag: (Float) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit
) {
    val backgroundColor = when {
        showResult && isInCorrectPosition -> CorrectGreenLight
        showResult && !isInCorrectPosition -> WrongRedLight
        isDragging -> PenguinYellow.copy(alpha = 0.3f)
        else -> MaterialTheme.colorScheme.surface
    }

    val borderColor = when {
        showResult && isInCorrectPosition -> CorrectGreen
        showResult && !isInCorrectPosition -> WrongRed
        isDragging -> PenguinYellow
        else -> PenguinLightGray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .zIndex(if (isDragging) 1f else 0f)
            .graphicsLayer {
                translationY = offsetY
                scaleX = scale
                scaleY = scale
            }
            .shadow(elevation, RoundedCornerShape(12.dp))
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .then(
                if (dragEnabled) {
                    Modifier.pointerInput(Unit) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { onDragStart() },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                onDrag(dragAmount.y)
                            },
                            onDragEnd = { onDragEnd() },
                            onDragCancel = { onDragCancel() }
                        )
                    }
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Drag handle icon (only when not showing results)
            if (!showResult) {
                Icon(
                    imageVector = Icons.Default.DragHandle,
                    contentDescription = "Ziehen zum Sortieren",
                    tint = if (isDragging) PenguinOrange else PenguinGray,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

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
                    style = MaterialTheme.typography.bodyMedium,
                    color = NavyBlueDark,
                    maxLines = 2
                )
                if (showResult && !isInCorrectPosition) {
                    Text(
                        text = "Richtig: Position $correctPosition",
                        style = MaterialTheme.typography.labelSmall,
                        color = WrongRed
                    )
                }
            }
        }
    }
}
