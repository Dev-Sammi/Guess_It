package com.dev_sammi.packagename.guessit.ui.addremove

import androidx.lifecycle.*
import com.dev_sammi.packagename.guessit.PreferenceManager
import com.dev_sammi.packagename.guessit.db.WordDao
import com.dev_sammi.packagename.guessit.model.Word
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val wordDao: WordDao,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _EventChannel = Channel<WordListEvent>()
    val eventChannel = _EventChannel.receiveAsFlow()

    val highestScore = preferenceManager.getPreferenceData.asLiveData()

    private val _searchQuery = state.getLiveData<String>("search_query", "")
    val searchQuery: LiveData<String> get() = _searchQuery

    val allWords = _searchQuery.switchMap {
        wordDao.getAllWords(it)
    }


    fun setWordQuery(query: String) {
        _searchQuery.value = query
    }

    fun checkNewWord(newWord: String) {
        if (!newWord.isNullOrEmpty()) {
            val word = Word(text = newWord)
            addNewWord(word)
        } else {
            viewModelScope.launch {
                _EventChannel.send(WordListEvent.ShowErrorMessage("Invalid input"))
            }
        }
    }

    private fun addNewWord(newWord: Word) {
        viewModelScope.launch {
            wordDao.insert(newWord)
            _EventChannel.send(WordListEvent.ShowAddedWordMessage(newWord.text))
        }
    }

    sealed class WordListEvent() {
        data class ShowAddedWordMessage(val addedWord: String) : WordListEvent()
        data class ShowDeletedWordMessage(val deletedWord: String) : WordListEvent()
        data class ShowEditedWordMessage(val editedWord: String) : WordListEvent()
        data class ShowErrorMessage(val error: String) : WordListEvent()
    }

}