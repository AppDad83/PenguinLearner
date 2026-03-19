package com.penguinlearner.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.penguinlearner.data.models.SectionProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "penguin_progress")

class ProgressRepository(private val context: Context) {

    companion object {
        private val LEARNED_VOCABULARY = stringSetPreferencesKey("learned_vocabulary")
        private val COMPLETED_GRAMMAR = stringSetPreferencesKey("completed_grammar")
        private val INFERENCE_RESULTS = stringSetPreferencesKey("inference_results")
        private val PREDICTION_RESULTS = stringSetPreferencesKey("prediction_results")
        private val EXPLANATION_RESULTS = stringSetPreferencesKey("explanation_results")
        private val RETRIEVAL_SCORE = intPreferencesKey("retrieval_score")
        private val RETRIEVAL_TOTAL = intPreferencesKey("retrieval_total")
        private val COMPLETED_SEQUENCING = stringSetPreferencesKey("completed_sequencing")
    }

    // Vocabulary Progress
    val learnedVocabularyIds: Flow<Set<Int>> = context.dataStore.data.map { prefs ->
        prefs[LEARNED_VOCABULARY]?.map { it.toInt() }?.toSet() ?: emptySet()
    }

    suspend fun markVocabularyLearned(id: Int, learned: Boolean) {
        context.dataStore.edit { prefs ->
            val current = prefs[LEARNED_VOCABULARY]?.toMutableSet() ?: mutableSetOf()
            if (learned) {
                current.add(id.toString())
            } else {
                current.remove(id.toString())
            }
            prefs[LEARNED_VOCABULARY] = current
        }
    }

    // Grammar Progress
    val completedGrammarIds: Flow<Set<Int>> = context.dataStore.data.map { prefs ->
        prefs[COMPLETED_GRAMMAR]?.map { it.toInt() }?.toSet() ?: emptySet()
    }

    suspend fun markGrammarCompleted(id: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[COMPLETED_GRAMMAR]?.toMutableSet() ?: mutableSetOf()
            current.add(id.toString())
            prefs[COMPLETED_GRAMMAR] = current
        }
    }

    // Inference Progress
    val inferenceResults: Flow<Map<Int, Boolean>> = context.dataStore.data.map { prefs ->
        prefs[INFERENCE_RESULTS]?.associate {
            val parts = it.split(":")
            parts[0].toInt() to (parts[1] == "true")
        } ?: emptyMap()
    }

    suspend fun saveInferenceResult(id: Int, gotIt: Boolean) {
        context.dataStore.edit { prefs ->
            val current = prefs[INFERENCE_RESULTS]?.toMutableSet() ?: mutableSetOf()
            current.removeAll { it.startsWith("$id:") }
            current.add("$id:$gotIt")
            prefs[INFERENCE_RESULTS] = current
        }
    }

    // Prediction Progress
    val predictionResults: Flow<Map<Int, Boolean>> = context.dataStore.data.map { prefs ->
        prefs[PREDICTION_RESULTS]?.associate {
            val parts = it.split(":")
            parts[0].toInt() to (parts[1] == "true")
        } ?: emptyMap()
    }

    suspend fun savePredictionResult(id: Int, gotIt: Boolean) {
        context.dataStore.edit { prefs ->
            val current = prefs[PREDICTION_RESULTS]?.toMutableSet() ?: mutableSetOf()
            current.removeAll { it.startsWith("$id:") }
            current.add("$id:$gotIt")
            prefs[PREDICTION_RESULTS] = current
        }
    }

    // Explanation Progress
    val explanationResults: Flow<Map<Int, Boolean>> = context.dataStore.data.map { prefs ->
        prefs[EXPLANATION_RESULTS]?.associate {
            val parts = it.split(":")
            parts[0].toInt() to (parts[1] == "true")
        } ?: emptyMap()
    }

    suspend fun saveExplanationResult(id: Int, gotIt: Boolean) {
        context.dataStore.edit { prefs ->
            val current = prefs[EXPLANATION_RESULTS]?.toMutableSet() ?: mutableSetOf()
            current.removeAll { it.startsWith("$id:") }
            current.add("$id:$gotIt")
            prefs[EXPLANATION_RESULTS] = current
        }
    }

    // Retrieval Quiz Progress
    val retrievalProgress: Flow<Pair<Int, Int>> = context.dataStore.data.map { prefs ->
        Pair(prefs[RETRIEVAL_SCORE] ?: 0, prefs[RETRIEVAL_TOTAL] ?: 0)
    }

    suspend fun updateRetrievalScore(correct: Boolean) {
        context.dataStore.edit { prefs ->
            val currentScore = prefs[RETRIEVAL_SCORE] ?: 0
            val currentTotal = prefs[RETRIEVAL_TOTAL] ?: 0
            prefs[RETRIEVAL_TOTAL] = currentTotal + 1
            if (correct) {
                prefs[RETRIEVAL_SCORE] = currentScore + 1
            }
        }
    }

    suspend fun resetRetrievalScore() {
        context.dataStore.edit { prefs ->
            prefs[RETRIEVAL_SCORE] = 0
            prefs[RETRIEVAL_TOTAL] = 0
        }
    }

    // Sequencing Progress
    val completedSequencingIds: Flow<Set<Int>> = context.dataStore.data.map { prefs ->
        prefs[COMPLETED_SEQUENCING]?.map { it.toInt() }?.toSet() ?: emptySet()
    }

    suspend fun markSequencingCompleted(id: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[COMPLETED_SEQUENCING]?.toMutableSet() ?: mutableSetOf()
            current.add(id.toString())
            prefs[COMPLETED_SEQUENCING] = current
        }
    }

    // Get progress for all sections
    fun getVocabularyProgress(total: Int): Flow<SectionProgress> = learnedVocabularyIds.map {
        SectionProgress(it.size, total)
    }

    fun getGrammarProgress(total: Int): Flow<SectionProgress> = completedGrammarIds.map {
        SectionProgress(it.size, total)
    }

    fun getInferenceProgress(total: Int): Flow<SectionProgress> = inferenceResults.map {
        SectionProgress(it.count { entry -> entry.value }, total)
    }

    fun getPredictionProgress(total: Int): Flow<SectionProgress> = predictionResults.map {
        SectionProgress(it.count { entry -> entry.value }, total)
    }

    fun getExplanationProgress(total: Int): Flow<SectionProgress> = explanationResults.map {
        SectionProgress(it.count { entry -> entry.value }, total)
    }

    fun getRetrievalSectionProgress(): Flow<SectionProgress> = retrievalProgress.map { (score, total) ->
        SectionProgress(score, total)
    }

    fun getSequencingProgress(total: Int): Flow<SectionProgress> = completedSequencingIds.map {
        SectionProgress(it.size, total)
    }
}
