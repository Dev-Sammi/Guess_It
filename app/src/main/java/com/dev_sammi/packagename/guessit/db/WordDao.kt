package com.dev_sammi.packagename.guessit.db

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.room.*
import com.dev_sammi.packagename.guessit.model.Word
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

//    @Query("SELECT * FROM words_table ORDER BY text")
//    fun getAllWords(): LiveData<List<Word>>

    @Query("SELECT * FROM words_table WHERE text LIKE '%'|| :query ||'%' ORDER BY text")
    fun getAllWords(query: String): LiveData<List<Word>>

    /*@Query("SELECT text FROM words_table")
    suspend fun getListOfWords() : List<String>*/

    @Query("SELECT text FROM words_table")
    fun getAllWordsForGame() : Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(word: Word)

    @Update
    suspend fun update(word: Word)

    @Query("DELETE FROM words_table WHERE text = :text")
    suspend fun deleteWord(text: String)

    @Delete
    suspend fun delete(word: Word)


}