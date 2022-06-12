package com.dev_sammi.packagename.guessit.di

import android.app.Application
import androidx.room.Room
import com.dev_sammi.packagename.guessit.db.WordDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun getWordDatabase(
        app: Application,
        callback: WordDatabase.WordCallback
    ) =
        Room.databaseBuilder(app.applicationContext, WordDatabase::class.java, "word_database")
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()


    @Provides
    fun getWordDao(db: WordDatabase) = db.getWordDao()

    @Provides
    @Singleton
    @ApplicationScope
    fun applicationScope() = CoroutineScope(SupervisorJob())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope