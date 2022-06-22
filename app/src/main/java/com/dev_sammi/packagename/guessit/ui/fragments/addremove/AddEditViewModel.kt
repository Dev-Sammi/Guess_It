package com.dev_sammi.packagename.guessit.ui.fragments.addremove

import android.provider.UserDictionary
import android.util.Log
import androidx.lifecycle.*
import com.dev_sammi.packagename.guessit.WordRepository
import com.dev_sammi.packagename.guessit.model.Word
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val TAG = "AddEditViewModel"
@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val wordRepository: WordRepository,
    ) : ViewModel() {
    private val wordDao = wordRepository.wordDao

    private val _mEventChannel = Channel<WordListEvent>()
    val eventChannel = _mEventChannel.receiveAsFlow()

    private val _searchQuery = state.getLiveData<String>("search_query", "")
    val searchQuery: LiveData<String> get() = _searchQuery

    var allWordsList = listOf<Word>()

    val displayedWords = _searchQuery.switchMap {
        wordDao.getAllWords(it)
    }


    fun setWordQuery(query: String) {
        _searchQuery.value = query
    }

    fun checkNewWord(newWord: String) {
        if (!newWord.isNullOrEmpty()) {
            val newText = newWord.lowercase()
            val word = Word(text = newText)
            if (!checkDatabase(newText)){
                addNewWord(word)
            }else{
                viewModelScope.launch {
                    _mEventChannel.send(WordListEvent.ShowErrorMessage("Word already exit!"))
                }
            }
        } else {
            viewModelScope.launch {
                _mEventChannel.send(WordListEvent.ShowErrorMessage("Invalid input"))
            }
        }
    }
    init {
        addWordsInBulk()
    }

    fun addWordsInBulk(){
        val message = "Hey, come, here"
        val newMessage = message.split(",").map {
            it.trim().lowercase()
        }
        for(i in newMessage){
            Log.d(TAG, "addWordsInBulk: $i")
        }
    }

    private fun checkDatabase(text: String): Boolean{
        var boolean = false
        allWordsList.forEach {
            if(text == it.text){
                boolean = true
            }

        }
        return boolean
    }

    private fun addNewWord(newWord: Word) {
        viewModelScope.launch {
            wordDao.insert(newWord)
            _mEventChannel.send(WordListEvent.ShowAddedWordMessage(newWord))
        }
    }

    fun deleteWord(word: Word){
        viewModelScope.launch {
            wordDao.delete(word)
            _mEventChannel.send(WordListEvent.ShowDeletedWordMessage(word.text))
        }
    }

    sealed class WordListEvent() {
        data class ShowAddedWordMessage(val addedWord: Word) : WordListEvent()
        data class ShowDeletedWordMessage(val deletedWord: String) : WordListEvent()
        data class ShowEditedWordMessage(val editedWord: String) : WordListEvent()
        data class ShowErrorMessage(val error: String) : WordListEvent()
    }

}