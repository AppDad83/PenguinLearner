package com.penguinlearner.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.BorderColor
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.penguinlearner.ui.theme.*

@Composable
fun ProgressHeader(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$current / $total",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = if (total > 0) current.toFloat() / total else 0f,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = PenguinYellow,
            trackColor = PenguinLightGray
        )
    }
}

@Composable
fun NavigationButtons(
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    isPreviousEnabled: Boolean,
    isNextEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = onPrevious,
            enabled = isPreviousEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Icon(Icons.Default.NavigateBefore, contentDescription = "Zurück")
            Spacer(modifier = Modifier.width(4.dp))
            Text("Zurück")
        }

        Button(
            onClick = onNext,
            enabled = isNextEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Weiter")
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.NavigateNext, contentDescription = "Weiter")
        }
    }
}

@Composable
fun LearnedCheckbox(
    isLearned: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onToggle(!isLearned) }
            .background(
                if (isLearned) CorrectGreenLight else Color.Transparent
            )
            .border(
                width = 2.dp,
                color = if (isLearned) CorrectGreen else PenguinGray,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isLearned) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = CorrectGreen,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = if (isLearned) "Gelernt!" else "Als gelernt markieren",
            color = if (isLearned) CorrectGreen else PenguinGray,
            fontWeight = if (isLearned) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun PassageCard(
    passage: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = IceBlue
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = passage,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = NavyBlueDark
        )
    }
}

@Composable
fun QuestionCard(
    question: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = question,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun SelfAssessmentButtons(
    onGotIt: () -> Unit,
    onNotYet: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = onNotYet,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = WrongRedLight,
                contentColor = WrongRed
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Noch nicht",
                fontWeight = FontWeight.Bold
            )
        }

        Button(
            onClick = onGotIt,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = CorrectGreen,
                contentColor = PenguinWhite
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Ich hab's!",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ModelAnswerCard(
    modelAnswer: String,
    isVisible: Boolean,
    onShowAnswer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (!isVisible) {
            OutlinedButton(
                onClick = onShowAnswer,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Musterantwort zeigen")
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = CorrectGreenLight
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Musterantwort:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = CorrectGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = modelAnswer,
                        style = MaterialTheme.typography.bodyMedium,
                        color = NavyBlueDark
                    )
                }
            }
        }
    }
}

@Composable
fun ChapterReference(
    chapter: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = chapter,
        modifier = modifier,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun EmptyStateMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// Highlighter components

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HighlighterToggle(
    isEnabled: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isEnabled,
        onClick = onToggle,
        label = { Text(if (isEnabled) "Markieren AN" else "Markieren") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.BorderColor,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = if (isEnabled) PenguinOrange else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = PenguinYellow.copy(alpha = 0.3f),
            selectedLabelColor = PenguinOrange,
            selectedLeadingIconColor = PenguinOrange
        ),
        modifier = modifier
    )
}

@Composable
fun ClearHighlightsButton(
    hasHighlights: Boolean,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClear,
        enabled = hasHighlights,
        modifier = modifier,
        colors = ButtonDefaults.textButtonColors(
            contentColor = WrongRed,
            disabledContentColor = PenguinGray
        )
    ) {
        Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text("Löschen")
    }
}

@Composable
fun HighlightableText(
    text: String,
    highlightedIndices: Set<Int>,
    onWordClick: (Int) -> Unit,
    highlightEnabled: Boolean,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    textColor: Color = NavyBlueDark,
    modifier: Modifier = Modifier
) {
    val words = text.split(Regex("(?<=\\s)|(?=\\s)")) // Split keeping spaces

    val annotatedString = buildAnnotatedString {
        words.forEachIndexed { index, word ->
            val isHighlighted = highlightedIndices.contains(index)

            if (word.isBlank()) {
                append(word)
            } else {
                pushStringAnnotation(tag = "word", annotation = index.toString())
                withStyle(
                    style = SpanStyle(
                        background = if (isHighlighted) HighlightYellow else Color.Transparent,
                        color = textColor
                    )
                ) {
                    append(word)
                }
                pop()
            }
        }
    }

    if (highlightEnabled) {
        ClickableText(
            text = annotatedString,
            style = style,
            modifier = modifier,
            onClick = { offset ->
                annotatedString.getStringAnnotations(tag = "word", start = offset, end = offset)
                    .firstOrNull()?.let { annotation ->
                        onWordClick(annotation.item.toInt())
                    }
            }
        )
    } else {
        Text(
            text = annotatedString,
            style = style,
            modifier = modifier
        )
    }
}

@Composable
fun HighlightablePassageCard(
    passage: String,
    highlightedIndices: Set<Int>,
    onWordClick: (Int) -> Unit,
    highlightEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = IceBlue
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        HighlightableText(
            text = passage,
            highlightedIndices = highlightedIndices,
            onWordClick = onWordClick,
            highlightEnabled = highlightEnabled,
            style = MaterialTheme.typography.bodyLarge,
            textColor = NavyBlueDark,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun HighlightableQuestionCard(
    question: String,
    highlightedIndices: Set<Int>,
    onWordClick: (Int) -> Unit,
    highlightEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        HighlightableText(
            text = question,
            highlightedIndices = highlightedIndices,
            onWordClick = onWordClick,
            highlightEnabled = highlightEnabled,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            textColor = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun HighlightableHintsCard(
    hints: List<String>,
    highlightedIndices: Map<Int, Set<Int>>, // Map of hint index to highlighted word indices
    onWordClick: (hintIndex: Int, wordIndex: Int) -> Unit,
    highlightEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PenguinYellow.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Hinweise:",
                style = MaterialTheme.typography.labelLarge,
                color = PenguinOrange
            )
            hints.forEachIndexed { hintIndex, hint ->
                Row(
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                ) {
                    Text(
                        text = "• ",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    HighlightableText(
                        text = hint,
                        highlightedIndices = highlightedIndices[hintIndex] ?: emptySet(),
                        onWordClick = { wordIndex -> onWordClick(hintIndex, wordIndex) },
                        highlightEnabled = highlightEnabled,
                        style = MaterialTheme.typography.bodyMedium,
                        textColor = NavyBlueDark
                    )
                }
            }
        }
    }
}

@Composable
fun HighlightableModelAnswerCard(
    modelAnswer: String,
    highlightedIndices: Set<Int>,
    onWordClick: (Int) -> Unit,
    highlightEnabled: Boolean,
    isVisible: Boolean,
    onShowAnswer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (!isVisible) {
            OutlinedButton(
                onClick = onShowAnswer,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Musterantwort zeigen")
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = CorrectGreenLight
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Musterantwort:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = CorrectGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HighlightableText(
                        text = modelAnswer,
                        highlightedIndices = highlightedIndices,
                        onWordClick = onWordClick,
                        highlightEnabled = highlightEnabled,
                        style = MaterialTheme.typography.bodyMedium,
                        textColor = NavyBlueDark
                    )
                }
            }
        }
    }
}
