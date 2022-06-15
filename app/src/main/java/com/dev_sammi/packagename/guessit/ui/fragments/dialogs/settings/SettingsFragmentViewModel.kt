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
    private val wordRepository: WordRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {
    private var _inputHr = MutableLiveData<Int>(0)
    val inputhr: LiveData<Int> get() = _inputHr

    private var _inputMin = MutableLiveData<Int>(0)
    val inputMin: LiveData<Int> get() = _inputMin

    private var _inputSec = MutableLiveData<Int>(10)
    val inputSec: LiveData<Int> get() = _inputSec

    private var _numOfWords = MutableLiveData<Int>(10)
    val numOfWords: LiveData<Int> get() = _numOfWords

    private var _highestScore = MutableLiveData<Int>(0)
    val highestScore: LiveData<Int> get() = _highestScore

    private var _previousScore = MutableLiveData<Int>(0)
    val previousScore: LiveData<Int> get() = _previousScore


    private val _settingsEvents = Channel<SettingValidator>()
    val settingsEvents get() = _settingsEvents.receiveAsFlow()
    val dataStoreValues= preferenceManager.getPreferenceData


    val settings = preferenceManager.getPreferenceData.asLiveData()
    private val wordDao = wordRepository.wordDao


    private var _wordsPerGame = MutableLiveData<Int>()
    val wordsPerGame: LiveData<Int> get() = _wordsPerGame

    val gameDuration = state.getLiveData("gameDuration", 1)

    private fun calculateNumOfWords(numOfWord: List<String>) {
        _numOfWords.value = numOfWord.size
    }

//    fun saveHour(hour: Int) {
//        if (hour in 1..12) {
//            _inputHr = hour
//            saveDuration(_inputHr,_inputMin,_inputSec)
//        } else {
//            viewModelScope.launch {
//                _settingsEvents.send(
//                    SettingValidator.InvalidDuration
//                )
//            }
//        }
//    }
//
//    fun saveSecond(second: Int) {
//        if (second in 1..12) {
//            _inputSec = second
//            saveDuration(_inputHr,_inputMin,_inputSec)
//        } else {
//            viewModelScope.launch {
//                _settingsEvents.send(
//                    SettingValidator.InvalidDuration
//                )
//            }
//        }
//    }

    private fun saveDuration(hr: Int, min: Int, sec: Int) {
        val duration = (hr * 3600000) + (min * 60000) + (sec * 1000)
        var durationInLong = duration.toLong()
        viewModelScope.launch {
//            preferenceManager.saveGameDuration(durationInLong)
        }
    }

    fun restoreValues(storedSetting: DataStoreValues) {
        _inputHr.value = storedSetting.gameDurationHolder.hour
        _inputMin.value = storedSetting.gameDurationHolder.minute
        _inputSec.value = storedSetting.gameDurationHolder.second
        _numOfWords.value = storedSetting.numberOfWord
        _highestScore.value = storedSetting.highestScore
        _previousScore.value = storedSetting.previousScore
    }

    fun checkInputs(hrs: Int, mins: Int, secs: Int, numOfWord: Int) {
        val time = hrs in 0..12 && mins in 0..60 && secs in 0..60
        val wordsNum = numOfWord in 1..numOfWords.value!!
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
            preferenceManager.saveHrs(hrs)
            preferenceManager.saveMins(mins)
            preferenceManager.saveSecs(secs)
            preferenceManager.saveNumberOfWords(numOfWord)
        }
        _inputHr.value = hrs
        _inputMin.value = mins
        _inputSec.value = secs
        _numOfWords.value = numOfWord
    }

//    init {
//        viewModelScope.launch(Dispatchers.IO){
//            val dataStoreValues = preferenceManager.getPreferenceData
//        }
//    }

    sealed class SettingValidator() {
        data class InvalidInput(val message: String) : SettingValidator()
        object ValidInput: SettingValidator()
    }


}