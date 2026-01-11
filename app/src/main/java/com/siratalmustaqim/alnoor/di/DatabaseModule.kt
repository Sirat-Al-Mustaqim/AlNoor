package com.siratalmustaqim.alnoor.di

import android.content.Context
import androidx.room.Room
import com.siratalmustaqim.alnoor.data.local.QuranDatabase
import com.siratalmustaqim.alnoor.data.local.dao.AyahDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing database dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides the QuranDatabase instance.
     *
     * Uses createFromAsset to pre-populate the database from assets/quran.db
     * on first access. The database is read-only for Quran text.
     */
    @Provides
    @Singleton
    fun provideQuranDatabase(
        @ApplicationContext context: Context
    ): QuranDatabase {
        return Room.databaseBuilder(
            context,
            QuranDatabase::class.java,
            QuranDatabase.DATABASE_NAME
        )
            .createFromAsset("quran.db")
            .fallbackToDestructiveMigration(false)
            .build()
    }

    /**
     * Provides the AyahDao for accessing Quran verses.
     */
    @Provides
    @Singleton
    fun provideAyahDao(database: QuranDatabase): AyahDao {
        return database.ayahDao()
    }
}
