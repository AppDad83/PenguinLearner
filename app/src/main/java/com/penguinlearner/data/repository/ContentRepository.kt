package com.penguinlearner.data.repository

import android.content.Context
import com.google.gson.Gson
import com.penguinlearner.data.models.*
import java.io.InputStreamReader

class ContentRepository(private val context: Context) {

    private val gson = Gson()

    fun loadVocabulary(): List<VocabularyItem> {
        return try {
            val inputStream = context.assets.open("data/vocabulary.json")
            val reader = InputStreamReader(inputStream)
            val data = gson.fromJson(reader, VocabularyData::class.java)
            reader.close()
            data.vocabulary
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun loadGrammarExercises(): List<GrammarExercise> {
        return try {
            val inputStream = context.assets.open("data/grammar.json")
            val reader = InputStreamReader(inputStream)
            val data = gson.fromJson(reader, GrammarData::class.java)
            reader.close()
            data.exercises
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun loadInferenceQuestions(): List<InferenceQuestion> {
        return try {
            val inputStream = context.assets.open("data/inference.json")
            val reader = InputStreamReader(inputStream)
            val data = gson.fromJson(reader, InferenceData::class.java)
            reader.close()
            data.questions
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun loadPredictionQuestions(): List<PredictionQuestion> {
        return try {
            val inputStream = context.assets.open("data/prediction.json")
            val reader = InputStreamReader(inputStream)
            val data = gson.fromJson(reader, PredictionData::class.java)
            reader.close()
            data.questions
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun loadExplanationQuestions(): List<ExplanationQuestion> {
        return try {
            val inputStream = context.assets.open("data/explanation.json")
            val reader = InputStreamReader(inputStream)
            val data = gson.fromJson(reader, ExplanationData::class.java)
            reader.close()
            data.questions
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun loadRetrievalQuestions(): List<RetrievalQuestion> {
        return try {
            val inputStream = context.assets.open("data/retrieval.json")
            val reader = InputStreamReader(inputStream)
            val data = gson.fromJson(reader, RetrievalData::class.java)
            reader.close()
            data.questions
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun loadSequencingExercises(): List<SequencingExercise> {
        return try {
            val inputStream = context.assets.open("data/sequencing.json")
            val reader = InputStreamReader(inputStream)
            val data = gson.fromJson(reader, SequencingData::class.java)
            reader.close()
            data.exercises
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun loadChapters(): List<Chapter> {
        return try {
            val inputStream = context.assets.open("data/chapters.json")
            val reader = InputStreamReader(inputStream)
            val data = gson.fromJson(reader, ChaptersData::class.java)
            reader.close()
            data.chapters
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
