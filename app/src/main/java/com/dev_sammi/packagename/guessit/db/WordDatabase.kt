package com.dev_sammi.packagename.guessit.db

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dev_sammi.packagename.guessit.di.ApplicationScope
import com.dev_sammi.packagename.guessit.model.Word
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Word::class], version = 1, exportSchema = false)
abstract class WordDatabase() : RoomDatabase() {
    abstract fun getWordDao(): WordDao

    class WordCallback @Inject constructor(
        private val database: Provider<WordDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val wordDao = database.get().getWordDao()

            applicationScope.launch {
                wordDao.insert(Word(text = "table"))
                wordDao.insert(Word(text = "cat"))
                wordDao.insert(Word(text = "television"))
                wordDao.insert(Word(text = "radio"))
                wordDao.insert(Word(text = "teacher"))
                wordDao.insert(Word(text = "food"))
                wordDao.insert(Word(text = "robot"))
                wordDao.insert(Word(text = "sleep"))
                wordDao.insert(Word(text = "working"))
                wordDao.insert(Word(text = "cleaning"))
                wordDao.insert(Word(text = "baby"))
                wordDao.insert(Word(text = "basket"))
                wordDao.insert(Word(text = "walking"))
                wordDao.insert(Word(text = "school"))
                wordDao.insert(Word(text = "doctor"))
                wordDao.insert(Word(text = "book"))
                wordDao.insert(Word(text = "shirt"))
                wordDao.insert(Word(text = "swimming"))
                wordDao.insert(Word(text = "dancing"))
                wordDao.insert(Word(text = "bottle"))
                wordDao.insert(Word(text = "daddy"))
            }

        }
    }
}