package com.dev_sammi.packagename.guessit.model

data class DataStoreValues(
    val numberOfWord: Int,
    val highestScore: Int,
    val previousScore: Int,
    val savedGameTimeInLong: Long,
    val gameDurationHolder: GameDurationHolder
)
