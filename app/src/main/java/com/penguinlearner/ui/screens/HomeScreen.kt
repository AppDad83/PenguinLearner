package com.penguinlearner.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.penguinlearner.ui.theme.*

@Composable
fun HomeScreen(
    onNavigateToVocabulary: () -> Unit,
    onNavigateToGrammar: () -> Unit,
    onNavigateToInference: () -> Unit,
    onNavigateToPrediction: () -> Unit,
    onNavigateToExplanation: () -> Unit,
    onNavigateToRetrieval: () -> Unit,
    onNavigateToSequencing: () -> Unit,
    onNavigateToChapters: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Welcome header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = NavyBlue),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Mr. Popper's Penguins",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = PenguinWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Wähle eine Übung:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = PenguinYellow
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Chapters reference card (special - always accessible)
        ExerciseMenuCard(
            title = "Kapitel",
            subtitle = "Nachschlagen & Zusammenfassungen",
            icon = Icons.Default.Book,
            backgroundColor = PenguinOrange,
            onClick = onNavigateToChapters
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Section: Wortschatz & Grammatik
        SectionHeader(title = "Wortschatz & Grammatik")
        Spacer(modifier = Modifier.height(12.dp))

        ExerciseCard(
            title = "Vokabeln",
            subtitle = "Karteikarten & Lückentext",
            icon = Icons.Default.MenuBook,
            onClick = onNavigateToVocabulary
        )

        Spacer(modifier = Modifier.height(12.dp))

        ExerciseCard(
            title = "Grammatik",
            subtitle = "Wortarten erkennen",
            icon = Icons.Default.Edit,
            onClick = onNavigateToGrammar
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Section: Leseverständnis
        SectionHeader(title = "Leseverständnis")
        Spacer(modifier = Modifier.height(12.dp))

        ExerciseCard(
            title = "Schlussfolgerung",
            subtitle = "Was bedeutet der Text?",
            icon = Icons.Default.Search,
            onClick = onNavigateToInference
        )

        Spacer(modifier = Modifier.height(12.dp))

        ExerciseCard(
            title = "Vorhersage",
            subtitle = "Was passiert als nächstes?",
            icon = Icons.Default.AutoAwesome,
            onClick = onNavigateToPrediction
        )

        Spacer(modifier = Modifier.height(12.dp))

        ExerciseCard(
            title = "Erklärung",
            subtitle = "Erkläre mit eigenen Worten",
            icon = Icons.Default.Chat,
            onClick = onNavigateToExplanation
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Section: Wissen testen
        SectionHeader(title = "Wissen testen")
        Spacer(modifier = Modifier.height(12.dp))

        ExerciseCard(
            title = "Quiz",
            subtitle = "Multiple-Choice Fragen",
            icon = Icons.Default.Quiz,
            onClick = onNavigateToRetrieval
        )

        Spacer(modifier = Modifier.height(12.dp))

        ExerciseCard(
            title = "Reihenfolge",
            subtitle = "Ereignisse sortieren",
            icon = Icons.Default.FormatListNumbered,
            onClick = onNavigateToSequencing
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = NavyBlueDark,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp)
    )
}

@Composable
private fun ExerciseCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = IceBlue,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = NavyBlue
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = NavyBlueDark
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = PenguinGray
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = PenguinGray
            )
        }
    }
}

@Composable
private fun ExerciseMenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    backgroundColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = PenguinWhite
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = PenguinWhite
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = PenguinWhite.copy(alpha = 0.9f)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = PenguinWhite.copy(alpha = 0.7f),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
