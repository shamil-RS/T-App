package com.example.tapp

import android.content.ClipboardManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideStoryRepository(): StoryRepository {
        return StoryRepositoryImpl() // Replace with your actual implementation
    }

    @Provides
    fun provideClipboardManager(@ApplicationContext context: Context): ClipboardManager {
        return context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }
}