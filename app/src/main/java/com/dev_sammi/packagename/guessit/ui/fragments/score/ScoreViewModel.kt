package com.dev_sammi.packagename.guessit.ui.fragments.score

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class ScoreViewModel(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private var _score = savedStateHandle.getLiveData("totalScore", 0)
    val totalScore get() = _score

    private var _numOfWords = savedStateHandle.getLiveData("numOfWords", 0)
    val numOfWords get() = _numOfWords

}