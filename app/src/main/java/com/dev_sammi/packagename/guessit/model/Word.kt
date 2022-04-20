package com.dev_sammi.packagename.guessit.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "words_table")
@Parcelize
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val text: String,
    val isCheckForDelete: Boolean = false
): Parcelable