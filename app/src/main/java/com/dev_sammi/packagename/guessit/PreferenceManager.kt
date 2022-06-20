package com.dev_sammi.packagename.guessit

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dev_sammi.packagename.guessit.PreferenceManager.PreferenceKeys.HOUR_DURATION_KEY
import com.dev_sammi.packagename.guessit.PreferenceManager.PreferenceKeys.MINUTE_DURATION_KEY
import com.dev_sammi.packagename.guessit.PreferenceManager.PreferenceKeys.NUMBER_OF_WORDS
import com.dev_sammi.packagename.guessit.PreferenceManager.PreferenceKeys.PREFERENCE_MANAGER
import com.dev_sammi.packagename.guessit.PreferenceManager.PreferenceKeys.SAVED_GAME_TIME_IN_LONG
import com.dev_sammi.packagename.guessit.PreferenceManager.PreferenceKeys.SECOND_DURATION_KEY
import com.dev_sammi.packagename.guessit.model.DataStoreValues
import com.dev_sammi.packagename.guessit.model.GameDurationHolder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PreferenceManager"

@Singleton
class PreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    //DataStore keys
    private object PreferenceKeys {
        const val PREFERENCE_MANAGER = "preference_manager"
        val NUMBER_OF_WORDS = intPreferencesKey("number_of_words")
        val SAVED_GAME_TIME_IN_LONG = longPreferencesKey("saved_game_time_in_long")
        val HOUR_DURATION_KEY = intPreferencesKey("hour_key")
        val MINUTE_DURATION_KEY = intPreferencesKey("minute_key")
        val SECOND_DURATION_KEY = intPreferencesKey("second_key")
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
            val numberOfWord = preferences[NUMBER_OF_WORDS] ?: 10
            val savedGameTimeInLong = preferences[SAVED_GAME_TIME_IN_LONG] ?: 10000L

            val hrs = preferences[HOUR_DURATION_KEY] ?: 0
            val mins = preferences[MINUTE_DURATION_KEY] ?: 0
            val secs = preferences[SECOND_DURATION_KEY] ?: 10

            DataStoreValues(
                numberOfWord, savedGameTimeInLong, GameDurationHolder(hrs, mins, secs)
            )
        }


    //Saving data in the dataStore
    suspend fun saveGameTimeInLong(gameTime: Long) {
        context.dataStore.edit { preference ->
            preference[SAVED_GAME_TIME_IN_LONG] = gameTime
        }
    }

    suspend fun saveNumberOfWords(numOfWord: Int) {
        context.dataStore.edit { preference ->
            preference[NUMBER_OF_WORDS] = numOfWord
        }
    }

    suspend fun saveHrs(hrs: Int) {
        context.dataStore.edit { preference ->
            preference[HOUR_DURATION_KEY] = hrs
        }
    }

    suspend fun saveMins(mins: Int) {
        context.dataStore.edit { preference ->
            preference[MINUTE_DURATION_KEY] = mins
        }
    }

    suspend fun saveSecs(secs: Int) {
        context.dataStore.edit { preference ->
            preference[SECOND_DURATION_KEY] = secs
        }
    }

}

