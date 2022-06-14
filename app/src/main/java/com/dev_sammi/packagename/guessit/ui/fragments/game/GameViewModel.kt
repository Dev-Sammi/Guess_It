package com.dev_sammi.packagename.guessit.ui.fragments.game

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_sammi.packagename.guessit.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

private const val TAG = "GameViewModel"


@HiltViewModel
class GameViewModel @Inject constructor(
    private val wordRepository: WordRepository
) : ViewModel() {

    private var _score: MutableLiveData<Int> = MutableLiveData(0)
    val score: LiveData<Int> get() = _score

    private var _count: MutableLiveData<Int> = MutableLiveData(0)
    val count: LiveData<Int> get() = _count

//    var count = 1
//    var score = 0
    val takeNumOfWord = 10
    var countDownFrm = 10000L
    val allWordsListForGame = wordRepository.mGetAllWordsForGame()
    private val _selectedWordsListForGame = mutableListOf<String>()
    private var _displayNextWord = MutableLiveData<String>()
    val displayNextWord: LiveData<String> get() = _displayNextWord
    private var _displayTimer = MutableLiveData<String>()
    val displayTimer: LiveData<String> get() = _displayTimer

    private var _displayStarterTimer = MutableLiveData<Long>()
    val displayStarterTimer: LiveData<Long> get() = _displayStarterTimer

    //Event channel for communication from the ViewModel to the GameFragment
    private val _eventChannel = Channel<GameEvents>()
    val eventChannel get() = _eventChannel.receiveAsFlow()

    /*This fun sorts and select a num of word to play the game depending on users setting*/
    fun sortAndSelectTenWords(list: List<String>?) {
        if (list != null) {
            Log.d(TAG, "sortAndSelectTenWords: ${list.size}")
            val list = list.shuffled().take(takeNumOfWord)
            for (i in list) {
                _selectedWordsListForGame.add(i)
            }
            Log.d(TAG, "sortAndSelectTenWords: ${_selectedWordsListForGame.size}")
        }
        nextWord()
    }

    fun nextWord() {
        val list = _selectedWordsListForGame
        var index = 0
        if (index != _selectedWordsListForGame.size) {
            _displayNextWord.value = list[index]
            list.removeAt(index)
            index++
        }
    }

    fun correctAnswer() {
        if (_count.value == takeNumOfWord) {
            return
        } else {
            _score.value = _score.value?.plus(1)
            _count.value = _count.value?.plus(1)
            nextWord()
        }
        Log.d(TAG, "GameScore: $score")
    }

    fun skipAnswer() {
        if (count.value == takeNumOfWord) {
            return
        } else {
            _count.value = _count.value?.plus(1)
            nextWord()
        }
        Log.d(TAG, "GameScore: $score")
    }

    //This fun starts get-ready starter-timer
    fun startGetReadyTimer() {
        starterTimer.start()
    }

    //This fun cancels the get ready starter-timer
    fun cancelGetReadyTimer() {
        starterTimer.cancel()
        }

    /*This fun Starts and cancel the main timer depending on the passed it boolean*/
    fun startMainGameTimer(cancel: Boolean){
        mainGameTimer(countDownFrm, cancel)
    }

    /*This fun calculates the user input time and convert to millisec and store the value in countDownFrm*/
    fun calculateTime() {
        var inputHr = 0
        var inputMin = 0
        var inputSec = 20
        val tNow = (inputHr*3600000)+(inputMin*60000)+(inputSec*1000)
        countDownFrm = tNow.toLong()
        Log.d(TAG, "calculateTime: onTick: $tNow ")
    }

    /*This fun resets all values and restart the game*/
    fun resetGame(){
        _score.value = 0
        _count.value = 0
    }

    /*Get ready timer*/
    private val starterTimer = object : CountDownTimer(GET_READY, ONE_SECOND) {
        override fun onTick(millisUntilFinished: Long) {
            val timeTillFinish = millisUntilFinished / ONE_SECOND
            Log.d(TAG, "onTick: ${timeTillFinish}")
//            if (timeTillFinish == 0L) {
//                viewModelScope.launch {
//                    _eventChannel.send(GameEvents.FinishStartingCountDown)
//                }
//            }
            _displayStarterTimer.postValue(timeTillFinish)
        }
        override fun onFinish() {
            viewModelScope.launch {
                _eventChannel.send(GameEvents.FinishStartingCountDown)
            }
            cancelGetReadyTimer()
        }
    }

    /*Main game timer*/
    private fun mainGameTimer(time: Long, cancel: Boolean) {
        val mainGameTimer = object : CountDownTimer(time, ONE_SECOND) {
            override fun onTick(millisUntilFinished: Long) {
                val timeTillFinish = millisUntilFinished / ONE_SECOND
                Log.d(TAG, "onTick: ${timeTillFinish}")
                var seconds = millisUntilFinished / 1000
                val minutes = (seconds / 60) % 60
                val hours = ((millisUntilFinished / (1000*60*60)) % 24)
                seconds %= 60
                val formattedTime = "${String.format("%02d", hours)}:${String.format("%02d", minutes)}:${String.format("%02d", seconds)}"
                Log.d(TAG, "onTick: ${formattedTime} ///// $millisUntilFinished")
                _displayTimer.postValue(formattedTime)
            }
            override fun onFinish() {
                viewModelScope.launch {
                    _eventChannel.send(GameEvents.NavigateToScoreFragment)
                }
            }
        }
        if(cancel)mainGameTimer.cancel() else mainGameTimer.start()
    }


    companion object {
        const val ZERO_TIME = 0L
        const val GET_READY = 3000L
        const val ONE_SECOND = 1000L
        const val COUNTDOWN_PANIC_SECONDS = 10L
    }

    sealed class GameEvents() {
        object FinishStartingCountDown : GameEvents()
        object NavigateToScoreFragment : GameEvents()
    }
}

private val CORRECT_BUZZ_PATTERN = longArrayOf(100, 100, 100, 100, 100, 100)
private val GET_READY_BUZZ_PATTERN = longArrayOf(0, 100)
private val PANIC_BUZZ_PATTERN = longArrayOf(0, 200)
private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0, 2000)
private val START_BUZZ_PATTERN = longArrayOf(0, 500)
private val NO_BUZZ_PATTERN = longArrayOf(0)

/*


    //livedata to update the ui in case data was successfully fetched from the database
    private val state = MutableLiveData<Status>()

    //List from roomDatabase
    private var initialList = listOf<String>()

    //number of words to select from the list from the database
    private val numberOfWordToSelect = 10

    //selected list out of those from the database
    private var wordsList = mutableListOf<String>()

    //livedata to display current selected word in ui
    private var _newWordToDisplay = MutableLiveData<String>()
    val newWordToDisplay: LiveData<String> get() = _newWordToDisplay

    //livedata to display score selected word in ui
    private var _currentGameScore = MutableLiveData<Int>(0)
    val currentGameScore: LiveData<Int> get() = _currentGameScore

    //livedata to set possible highest score in ui
    private var _possibleHighestScore = MutableLiveData<Int>(numberOfWordToSelect)
    val possibleHighestScore: LiveData<Int> get() = _possibleHighestScore

    //var to help calculate the score
    private var invisibleScore = 1

    private val _timeToShow = MutableLiveData<Long>()
    val timeToShow: LiveData<Long> get() = _timeToShow

    private val _buzzPattern = MutableLiveData<Buzz>()
    val buzzPattern: LiveData<Buzz> get() = _buzzPattern

    private val _eventChannel = Channel<GameEvents>()
    val eventChannel get() = _eventChannel.receiveAsFlow()

    private var timeToCountFrom = 40000L


    //timer
    val timer = object : CountDownTimer(GET_READY, ONE_SECOND) {
            override fun onTick(millisUntilFinished: Long) {
                _timeToShow.value = millisUntilFinished / ONE_SECOND
                _buzzPattern.value = Buzz.GET_READY_BUZZ
            }

            override fun onFinish() {
                _buzzPattern.value = Buzz.START_BUZZ
                viewModelScope.launch {
                    _eventChannel.send(GameEvents.FinishCountDown)
                }
            }
        }


    //timer
    val timer2 = object : CountDownTimer(timeToCountFrom, ONE_SECOND) {
        override fun onTick(millisUntilFinished: Long) {
            if (millisUntilFinished / ONE_SECOND <= COUNTDOWN_PANIC_SECONDS) {
                _buzzPattern.value = Buzz.PANIC_BUZZ
            }
            _timeToShow.value = millisUntilFinished / ONE_SECOND
        }

        override fun onFinish() {
            viewModelScope.launch {
                _buzzPattern.value = Buzz.GAME_OVER_BUZZ
            }
        }
    }

    fun getNextWord() {
        state.value = Status.LOADING
        if (initialList.isNullOrEmpty()) {
            Log.i(TAG, "getNextWord: I'm empty")
            viewModelScope.launch {
                val job = async { wordRepository.wordDao.getListOfWords() }
                initialList = job.await()
                setWordsList(initialList)
                nextWord()
            }
        } else {
            Log.i(TAG, "getNextWord: I'm not empty")
            nextWord()
        }

    }

    private fun nextWord() {
        Log.i(TAG, "nextWord: --------------$numberOfWordToSelect")
        state.value = Status.SUCCESS
        val list = wordsList
        var index = 0
        if (index != list.size) {
            _newWordToDisplay.value = list[index]
            list.removeAt(index)
            index++
        }
    }

    private fun setWordsList(list: List<String>?) {
        if (!list.isNullOrEmpty()) {
            list.shuffled().take(numberOfWordToSelect).forEach { wordsList.add(it) }
        }
        state.value = Status.ERROR
    }

    fun correctAnswer() {
        _buzzPattern.value = Buzz.CORRECT_BUZZ
        if (invisibleScore <= numberOfWordToSelect) {
            _currentGameScore.value = _currentGameScore.value?.plus(1)
        }
        invisibleScore++
        getNextWord()
    }

    fun skipAnswer() {
        getNextWord()
        invisibleScore++
    }


    override fun onCleared() {
        super.onCleared()
        timer.cancel()
        timer2.cancel()
    }

    companion object {
        const val ZERO_TIME = 0L
        const val GET_READY = 3000L
        const val ONE_SECOND = 1000L
        const val COUNTDOWN_PANIC_SECONDS =10L



    }

    enum class Status { LOADING, SUCCESS, ERROR }

    sealed class GameEvents() {
        object FinishCountDown : GameEvents()
    }

    enum class Buzz(val buzzPattern: LongArray){
        CORRECT_BUZZ (CORRECT_BUZZ_PATTERN),
        PANIC_BUZZ (PANIC_BUZZ_PATTERN),
        GET_READY_BUZZ (GET_READY_BUZZ_PATTERN),
        GAME_OVER_BUZZ(GAME_OVER_BUZZ_PATTERN),
        START_BUZZ(START_BUZZ_PATTERN),
        NO_BUZZ_BUZZ(NO_BUZZ_PATTERN),

    }


}

private val CORRECT_BUZZ_PATTERN = longArrayOf(100, 100, 100, 100, 100, 100)
private val GET_READY_BUZZ_PATTERN = longArrayOf(0, 100)
private val PANIC_BUZZ_PATTERN = longArrayOf(0, 200)
private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0,2000)
private val START_BUZZ_PATTERN = longArrayOf(0, 500)
private val NO_BUZZ_PATTERN = longArrayOf(0)

*/







