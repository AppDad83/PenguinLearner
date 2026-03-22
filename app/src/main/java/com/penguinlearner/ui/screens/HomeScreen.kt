package com.penguinlearner.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.penguinlearner.data.repository.ContentRepository
import com.penguinlearner.data.repository.ProgressRepository
import com.penguinlearner.ui.theme.*

@Composable
fun HomeScreen(
    contentRepository: ContentRepository,
    progressRepository: ProgressRepository,
    onNavigateToVocabulary: () -> Unit,
    onNavigateToGrammar: () -> Unit,
    onNavigateToInference: () -> Unit,
    onNavigateToPrediction: () -> Unit,
    onNavigateToExplanation: () -> Unit,
    onNavigateToRetrieval: () -> Unit,
    onNavigateToSequencing: () -> Unit,
    onNavigateToChapters: () -> Unit,
    onNavigateToInferenceQuiz: () -> Unit,
    onNavigateToPredictionQuiz: () -> Unit,
    onNavigateToExplanationQuiz: () -> Unit
) {
    // Load content counts
    var vocabularyTotal by remember { mutableIntStateOf(0) }
    var grammarTotal by remember { mutableIntStateOf(0) }
    var inferenceTotal by remember { mutableIntStateOf(0) }
    var predictionTotal by remember { mutableIntStateOf(0) }
    var explanationTotal by remember { mutableIntStateOf(0) }
    var retrievalTotal by remember { mutableIntStateOf(0) }
    var sequencingTotal by remember { mutableIntStateOf(0) }
    var inferenceQuizTotal by remember { mutableIntStateOf(0) }
    var predictionQuizTotal by remember { mutableIntStateOf(0) }
    var explanationQuizTotal by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        vocabularyTotal = contentRepository.loadVocabulary().size
        grammarTotal = contentRepository.loadGrammarExercises().size
        inferenceTotal = contentRepository.loadInferenceQuestions().size
        predictionTotal = contentRepository.loadPredictionQuestions().size
        explanationTotal = contentRepository.loadExplanationQuestions().size
        retrievalTotal = contentRepository.loadRetrievalQuestions().size
        sequencingTotal = contentRepository.loadSequencingExercises().size
        inferenceQuizTotal = contentRepository.loadInferenceQuiz().size
        predictionQuizTotal = contentRepository.loadPredictionQuiz().size
        explanationQuizTotal = contentRepository.loadExplanationQuiz().size
    }

    // Collect progress
    val learnedVocabulary by progressRepository.learnedVocabularyIds.collectAsState(initial = emptySet())
    val completedGrammar by progressRepository.completedGrammarIds.collectAsState(initial = emptySet())
    val inferenceResults by progressRepository.inferenceResults.collectAsState(initial = emptyMap())
    val predictionResults by progressRepository.predictionResults.collectAsState(initial = emptyMap())
    val explanationResults by progressRepository.explanationResults.collectAsState(initial = emptyMap())
    val retrievalProgress by progressRepository.retrievalProgress.collectAsState(initial = Pair(0, 0))
    val completedSequencing by progressRepository.completedSequencingIds.collectAsState(initial = emptySet())
    val inferenceQuizProgress by progressRepository.inferenceQuizProgress.collectAsState(initial = Pair(0, 0))
    val predictionQuizProgress by progressRepository.predictionQuizProgress.collectAsState(initial = Pair(0, 0))
    val explanationQuizProgress by progressRepository.explanationQuizProgress.collectAsState(initial = Pair(0, 0))

    // Calculate stars for each section (0-3 stars based on percentage)
    val vocabularyStars = calculateStars(learnedVocabulary.size, vocabularyTotal)
    val grammarStars = calculateStars(completedGrammar.size, grammarTotal)
    val inferenceStars = calculateStars(inferenceResults.count { it.value }, inferenceTotal)
    val predictionStars = calculateStars(predictionResults.count { it.value }, predictionTotal)
    val explanationStars = calculateStars(explanationResults.count { it.value }, explanationTotal)
    val retrievalStars = calculateStars(retrievalProgress.first, retrievalTotal)
    val sequencingStars = calculateStars(completedSequencing.size, sequencingTotal)
    val inferenceQuizStars = calculateStars(inferenceQuizProgress.first, inferenceQuizTotal)
    val predictionQuizStars = calculateStars(predictionQuizProgress.first, predictionQuizTotal)
    val explanationQuizStars = calculateStars(explanationQuizProgress.first, explanationQuizTotal)

    // Total stars (max 30 = 10 exercises * 3 stars each)
    val totalStars = vocabularyStars + grammarStars + inferenceStars +
                     predictionStars + explanationStars + retrievalStars + sequencingStars +
                     inferenceQuizStars + predictionQuizStars + explanationQuizStars
    val maxStars = 30

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Welcome header with total stars
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
                Spacer(modifier = Modifier.height(12.dp))

                // Star reward counter
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = PenguinYellow
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$totalStars / $maxStars",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = PenguinYellow
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress bar
                LinearProgressIndicator(
                    progress = if (maxStars > 0) totalStars.toFloat() / maxStars else 0f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .padding(horizontal = 16.dp),
                    color = PenguinYellow,
                    trackColor = PenguinWhite.copy(alpha = 0.3f)
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Wähle eine Übung:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = PenguinWhite.copy(alpha = 0.9f)
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
            stars = vocabularyStars,
            completed = learnedVocabulary.size,
            total = vocabularyTotal,
            onClick = onNavigateToVocabulary
        )

        Spacer(modifier = Modifier.height(12.dp))

        ExerciseCard(
            title = "Grammatik",
            subtitle = "Wortarten erkennen",
            icon = Icons.Default.Edit,
            stars = grammarStars,
            completed = completedGrammar.size,
            total = grammarTotal,
            onClick = onNavigateToGrammar
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Section: Leseverständnis
        SectionHeader(title = "Leseverständnis")
        Spacer(modifier = Modifier.height(12.dp))

        // Inference Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExerciseCardCompact(
                title = "Schlussfolgerung",
                subtitle = "Freitext",
                icon = Icons.Default.Search,
                stars = inferenceStars,
                completed = inferenceResults.count { it.value },
                total = inferenceTotal,
                onClick = onNavigateToInference,
                modifier = Modifier.weight(1f)
            )
            QuizCard(
                title = "Inference Quiz",
                icon = Icons.Default.Search,
                stars = inferenceQuizStars,
                completed = inferenceQuizProgress.first,
                total = inferenceQuizTotal,
                onClick = onNavigateToInferenceQuiz,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Prediction Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExerciseCardCompact(
                title = "Vorhersage",
                subtitle = "Freitext",
                icon = Icons.Default.AutoAwesome,
                stars = predictionStars,
                completed = predictionResults.count { it.value },
                total = predictionTotal,
                onClick = onNavigateToPrediction,
                modifier = Modifier.weight(1f)
            )
            QuizCard(
                title = "Prediction Quiz",
                icon = Icons.Default.AutoAwesome,
                stars = predictionQuizStars,
                completed = predictionQuizProgress.first,
                total = predictionQuizTotal,
                onClick = onNavigateToPredictionQuiz,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Explanation Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExerciseCardCompact(
                title = "Erklärung",
                subtitle = "Freitext",
                icon = Icons.Default.Chat,
                stars = explanationStars,
                completed = explanationResults.count { it.value },
                total = explanationTotal,
                onClick = onNavigateToExplanation,
                modifier = Modifier.weight(1f)
            )
            QuizCard(
                title = "Explanation Quiz",
                icon = Icons.Default.Chat,
                stars = explanationQuizStars,
                completed = explanationQuizProgress.first,
                total = explanationQuizTotal,
                onClick = onNavigateToExplanationQuiz,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section: Wissen testen
        SectionHeader(title = "Wissen testen")
        Spacer(modifier = Modifier.height(12.dp))

        ExerciseCard(
            title = "Quiz",
            subtitle = "Multiple-Choice Fragen",
            icon = Icons.Default.Quiz,
            stars = retrievalStars,
            completed = retrievalProgress.first,
            total = retrievalTotal,
            onClick = onNavigateToRetrieval
        )

        Spacer(modifier = Modifier.height(12.dp))

        ExerciseCard(
            title = "Reihenfolge",
            subtitle = "Ereignisse sortieren",
            icon = Icons.Default.FormatListNumbered,
            stars = sequencingStars,
            completed = completedSequencing.size,
            total = sequencingTotal,
            onClick = onNavigateToSequencing
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

private fun calculateStars(completed: Int, total: Int): Int {
    if (total == 0) return 0
    val percentage = completed.toFloat() / total
    return when {
        percentage >= 1.0f -> 3
        percentage >= 0.66f -> 2
        percentage >= 0.33f -> 1
        else -> 0
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
    stars: Int,
    completed: Int,
    total: Int,
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
                Spacer(modifier = Modifier.height(4.dp))

                // Progress and stars row
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Stars
                    StarRating(stars = stars)

                    Spacer(modifier = Modifier.width(8.dp))

                    // Progress text
                    Text(
                        text = "$completed / $total",
                        style = MaterialTheme.typography.labelSmall,
                        color = PenguinGray
                    )
                }
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
private fun StarRating(stars: Int, maxStars: Int = 3) {
    Row {
        repeat(maxStars) { index ->
            val isFilled = index < stars
            val animatedScale by animateFloatAsState(
                targetValue = if (isFilled) 1f else 0.8f,
                animationSpec = tween(300),
                label = "starScale"
            )

            Icon(
                imageVector = if (isFilled) Icons.Default.Star else Icons.Default.StarOutline,
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
                    .scale(animatedScale),
                tint = if (isFilled) PenguinYellow else PenguinLightGray
            )
        }
    }
}

@Composable
private fun ExerciseMenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    backgroundColor: Color,
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

@Composable
private fun ExerciseCardCompact(
    title: String,
    subtitle: String,
    icon: ImageVector,
    stars: Int,
    completed: Int,
    total: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = IceBlue,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = NavyBlue
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = NavyBlueDark,
                        maxLines = 1
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = PenguinGray
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                StarRating(stars = stars)
                Text(
                    text = "$completed/$total",
                    style = MaterialTheme.typography.labelSmall,
                    color = PenguinGray
                )
            }
        }
    }
}

@Composable
private fun QuizCard(
    title: String,
    icon: ImageVector,
    stars: Int,
    completed: Int,
    total: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = NavyBlueLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PenguinWhite.copy(alpha = 0.2f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = PenguinWhite
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = PenguinWhite,
                        maxLines = 1
                    )
                    Text(
                        text = "Multiple Choice",
                        style = MaterialTheme.typography.labelSmall,
                        color = PenguinWhite.copy(alpha = 0.7f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                StarRating(stars = stars)
                Text(
                    text = "$completed/$total",
                    style = MaterialTheme.typography.labelSmall,
                    color = PenguinWhite.copy(alpha = 0.7f)
                )
            }
        }
    }
}
