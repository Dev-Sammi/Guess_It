package com.dev_sammi.packagename.guessit.ui.fragments.dialogs.settings

import androidx.lifecycle.*
import com.dev_sammi.packagename.guessit.PreferenceManager
import com.dev_sammi.packagename.guessit.WordRepository
import com.dev_sammi.packagename.guessit.model.DataStoreValues
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SettingsFragmentViewMod"

@HiltViewModel
class SettingsFragmentViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val wordRepository: WordRepository
) : ViewModel() {
    private var _inputHr = MutableLiveData<Int>(0)
    val inputhr: LiveData<Int> get() = _inputHr

    private var _inputMin = MutableLiveData<Int>(0)
    val inputMin: LiveData<Int> get() = _inputMin

    private var _inputSec = MutableLiveData<Int>(10)
    val inputSec: LiveData<Int> get() = _inputSec

    private var _numOfWords = MutableLiveData<Int>(10)
    val numOfWords: LiveData<Int> get() = _numOfWords

    private var _numOfAllWordsInDatabase = MutableLiveData<Int>(0)
    val numOfAllWordsInDatabase: LiveData<Int> get() = _numOfAllWordsInDatabase

    val allWordsListForGame = wordRepository.mGetAllWordsForGame()


    private val _settingsEvents = Channel<SettingValidator>()
    val settingsEvents get() = _settingsEvents.receiveAsFlow()
    val dataStoreValues= wordRepository.dataStore.getPreferenceData


    val settings = wordRepository.dataStore.getPreferenceData.asLiveData()
    private val wordDao = wordRepository.wordDao


    private var _wordsPerGame = MutableLiveData<Int>()
    val wordsPerGame: LiveData<Int> get() = _wordsPerGame

    val gameDuration = state.getLiveData("gameDuration", 1)

    fun setNumOfAllWordsInDatabase(num: Int){
        _numOfAllWordsInDatabase.value = num
    }

    private fun saveDuration(hr: Int, min: Int, sec: Int) {
        val duration = (hr * 3600000) + (min * 60000) + (sec * 1000)
        var durationInLong = duration.toLong()
        viewModelScope.launch(Dispatchers.IO) {
            wordRepository.dataStore.saveGameTimeInLong(durationInLong)
        }
    }

    fun restoreValues(storedSetting: DataStoreValues) {
        _inputHr.value = storedSetting.gameDurationHolder.hour
        _inputMin.value = storedSetting.gameDurationHolder.minute
        _inputSec.value = storedSetting.gameDurationHolder.second
        _numOfWords.value = storedSetting.numberOfWord
    }

    fun checkInputs(hrs: Int, mins: Int, secs: Int, numOfWord: Int) {
        val time = hrs in 0..12 && mins in 0..60 && secs in 0..60
        val wordsNum = numOfWord in 1.._numOfAllWordsInDatabase.value!!
        if (time && wordsNum){
            saveInputs(hrs,mins,secs,numOfWord)
            viewModelScope.launch {
                _settingsEvents.send(
                    SettingValidator.ValidInput
                )
            }
        }else{
            if(!time){
                viewModelScope.launch {
                    _settingsEvents.send(
                        SettingValidator.InvalidInput("Invalid Duration Input!")
                    )
                }
                return
            }
            if(!wordsNum){
                viewModelScope.launch {
                    _settingsEvents.send(
                        SettingValidator.InvalidInput("Invalid Number Of Words!")
                    )
                }
            }
        }
    }

    private fun saveInputs(hrs: Int, mins: Int, secs: Int, numOfWord: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            wordRepository.dataStore.saveHrs(hrs)
            wordRepository.dataStore.saveMins(mins)
            wordRepository.dataStore.saveSecs(secs)
            wordRepository.dataStore.saveNumberOfWords(numOfWord)
        }
        _inputHr.value = hrs
        _inputMin.value = mins
        _inputSec.value = secs
        _numOfWords.value = numOfWord
        saveDuration(hrs,mins,secs)
    }

    sealed class SettingValidator() {
        data class InvalidInput(val message: String) : SettingValidator()
        object ValidInput: SettingValidator()
    }


}