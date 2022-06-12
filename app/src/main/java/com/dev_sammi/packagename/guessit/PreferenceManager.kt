package com.dev_sammi.packagename.guessit

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dev_sammi.packagename.guessit.PreferenceManager.PreferenceKeys.GAME_DURATION
import com.dev_sammi.packagename.guessit.PreferenceManager.PreferenceKeys.HIGHEST_SCORE_KEY
import com.dev_sammi.packagename.guessit.PreferenceManager.PreferenceKeys.NUMBER_OF_WORDS
import com.dev_sammi.packagename.guessit.PreferenceManager.PreferenceKeys.PREFERENCE_MANAGER
import com.dev_sammi.packagename.guessit.PreferenceManager.PreferenceKeys.PREVIOUS_SCORE
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
private const val TAG = "PreferenceManager"

class PreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    //DataStore keys
    private object PreferenceKeys {
        const val PREFERENCE_MANAGER = "preference_manager"
        val NUMBER_OF_WORDS = intPreferencesKey("number_of_words")
        val GAME_DURATION = intPreferencesKey("game_duration")
        val PREVIOUS_SCORE = intPreferencesKey("previous_score")
        val HIGHEST_SCORE_KEY = intPreferencesKey("highest_key")
    }

    //Creating dataStore
    private val Context.dataStore by preferencesDataStore(PREFERENCE_MANAGER)


    //Getting data from the dataStore
    val getPreferenceData = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences: ", exception)
                Timber.e(exception, "Error reading preferences ")
                emit(emptyPreferences())
            }
        }
        .map { preferences ->
            val previousScore = preferences[PREVIOUS_SCORE] ?: 0
            val highestScore = preferences[HIGHEST_SCORE_KEY] ?: 0
            val gameDuration = preferences[GAME_DURATION] ?: 1
            val numberOfWord = preferences[NUMBER_OF_WORDS] ?: 1
            StoredValues(numberOfWord,gameDuration,highestScore,previousScore)
        }


    //Saving data in the dataStore
    suspend fun savePreviousScore(previousScore: Int) {
        context.dataStore.edit { preference ->
            preference[PREVIOUS_SCORE] = previousScore
        }
    }

    suspend fun saveHighestScore(highestScore: Int) {
        context.dataStore.edit { preference ->
            preference[HIGHEST_SCORE_KEY] = highestScore
        }
    }

    suspend fun saveGameDuration(gameTime: Int) {
        context.dataStore.edit { preference ->
            preference[GAME_DURATION] = gameTime
        }
    }

    suspend fun saveNumberOfWords(numOfWord: Int) {
        context.dataStore.edit { preference ->
            preference[NUMBER_OF_WORDS] = numOfWord
        }
    }

}

data class StoredValues(
    val numberOfWord: Int,
    val gameDuration: Int,
    val highestScore: Int,
    val previousScore: Int
)

