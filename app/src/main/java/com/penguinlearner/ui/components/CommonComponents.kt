package com.penguinlearner.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
