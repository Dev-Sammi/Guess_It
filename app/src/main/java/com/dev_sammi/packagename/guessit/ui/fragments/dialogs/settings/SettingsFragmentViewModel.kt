package com.dev_sammi.packagename.guessit.ui.fragments.dialogs.settings

import android.util.Log
import androidx.lifecycle.*
import com.dev_sammi.packagename.guessit.PreferenceManager
import com.dev_sammi.packagename.guessit.WordRepository
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SettingsFragmentViewMod"

@HiltViewModel
class SettingsFragmentViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val wordRepository: WordRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {
    val settings = preferenceManager.getPreferenceData.asLiveData()
    private val wordDao = wordRepository.wordDao
    private var _numOfWords = MutableLiveData<Int>()
    val numOfWords: LiveData<Int> get() = _numOfWords

    private var _wordsPerGame = MutableLiveData<Int>()
    val wordsPerGame: LiveData<Int> get() = _wordsPerGame

    val gameDuration = state.getLiveData("gameDuration", 1)

//    init {
//        viewModelScope.launch {
//            val job = async { wordDao.getListOfWords() }
//            val numOfWord = job.await()
//            calculateNumOfWords(numOfWord)
//        }
//    }

    private fun calculateNumOfWords(numOfWord: List<String>) {
        _numOfWords.value = numOfWord.size
    }

    fun saveGameDuration(time: Int) {
        if (time in 1..60) {
            gameDuration.value = time
            Log.i(TAG, "saveGameDuration: $time")
        }
    }


}