package com.penguinlearner.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.penguinlearner.data.models.Chapter
import com.penguinlearner.data.repository.ContentRepository
import com.penguinlearner.ui.components.EmptyStateMessage
import com.penguinlearner.ui.theme.*

@Composable
fun ChaptersScreen(
    contentRepository: ContentRepository
) {
    var chapters by remember { mutableStateOf<List<Chapter>>(emptyList()) }
    var expandedChapter by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        chapters = contentRepository.loadChapters()
    }

    if (chapters.isEmpty()) {
        EmptyStateMessage("Lade Kapitel...")
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = NavyBlue),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Mr. Popper's Penguins",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = PenguinWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Kapitelübersicht",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PenguinYellow
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Chapter list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chapters) { chapter ->
                ChapterCard(
                    chapter = chapter,
                    isExpanded = expandedChapter == chapter.number,
                    onToggle = {
                        expandedChapter = if (expandedChapter == chapter.number) null else chapter.number
                    }
                )
            }
        }
    }
}

@Composable
private fun ChapterCard(
    chapter: Chapter,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "rotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        colors = CardDefaults.cardColors(
            containerColor = if (isExpanded) IceBlue else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isExpanded) 4.dp else 1.dp
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Chapter header (always visible)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Chapter number badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = NavyBlue,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = chapter.number.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PenguinWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Chapter title
                Text(
                    text = chapter.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = NavyBlueDark,
                    modifier = Modifier.weight(1f)
                )

                // Expand/collapse icon
                Icon(
                    imageVector = Icons.Filled.ExpandMore,
                    contentDescription = if (isExpanded) "Einklappen" else "Ausklappen",
                    modifier = Modifier.rotate(rotationAngle),
                    tint = NavyBlue
                )
            }

            // Expandable summary content
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    Divider(
                        color = NavyBlue.copy(alpha = 0.2f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    chapter.summary.forEach { point ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "\uD83D\uDC27", // Penguin emoji
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = point,
                                style = MaterialTheme.typography.bodyMedium,
                                color = NavyBlueDark
                            )
                        }
                    }
                }
            }
        }
    }
}
