package com.example.tapp

import kotlinx.coroutines.delay
import javax.inject.Inject

interface StoryRepository {
    suspend fun getStories(): List<Story>
    suspend fun getDiscountStories(): List<Story>suspend fun getMessageStories(): List<MessageStory>
    suspend fun getStory(storyId: Int): Story
    suspend fun getCommentsForStory(storyId: Int): List<Comment>
}

class StoryRepositoryImpl @Inject constructor() : StoryRepository {

    override suspend fun getStories():List<Story> {
        // Simulate network delay
        delay(1000)
        return Story.storyList
    }

    override suspend fun getDiscountStories(): List<Story> {
        delay(1000)
        return Story.discountData
    }

    override suspend fun getMessageStories(): List<MessageStory> {
        delay(1000)
        return MessageStory.messageData
    }

    override suspend fun getStory(storyId: Int): Story {
        delay(1000)
        return Story.storyData.first { it.id == storyId }
    }

    override suspend fun getCommentsForStory(storyId: Int): List<Comment> {
        delay(1000)
        // In a real app, you would fetch comments from a database or API
        // based on the storyId
        return if (storyId == 1) {
            RatingUiState.mock.first().allComment
        } else {
            emptyList()
        }
    }
}