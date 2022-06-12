package com.dev_sammi.packagename.guessit

import androidx.lifecycle.asLiveData
import com.dev_sammi.packagename.guessit.db.WordDao
import javax.inject.Inject


class WordRepository @Inject constructor(
    private val mWordDao: WordDao
) {
    //This fun gets all word for the game and output the as livedata from the db flow.
    fun mGetAllWordsForGame() = mWordDao.getAllWordsForGame().asLiveData()

    val wordDao = mWordDao
}