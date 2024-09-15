package com.example.tapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeScreenIntent {
   data object LoadStories : HomeScreenIntent()
}

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val repository: StoryRepository) : ViewModel() {
    private val _state = MutableStateFlow(HomeScreenState(isLoading = true))
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()

    init {
        handleIntent(HomeScreenIntent.LoadStories)
    }

    private fun handleIntent(intent: HomeScreenIntent) {
        viewModelScope.launch {
            when (intent) {
                HomeScreenIntent.LoadStories -> loadStories()
            }
        }
    }

    private suspend fun loadStories() {
        try {
            val stories = repository.getStories()
            val discountStories = repository.getDiscountStories()
            val messageStories = repository.getMessageStories()
            _state.value = HomeScreenState(
                stories = stories,
                discountStories = discountStories,
                messageStories = messageStories
            )
        } catch (e: Exception) {_state.value = HomeScreenState(error = e.message)
        } finally {
            _state.value = _state.value.copy(isLoading = false)
        }
    }
}


data class HomeScreenState(
    val stories: List<Story> = emptyList(),
    val discountStories: List<Story> = emptyList(),
    val messageStories: List<MessageStory> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class Story(
    val id: Int,
    val imageResId: Int,
    val description: String = "",
    val comments: List<Comment> = emptyList()
) {
    companion object {
        val storyData = listOf(
            Story(1, R.drawable.da, "Story 1 Description"),
            Story(2, R.drawable.dar, "Story 2 Description"),
            Story(3, R.drawable.dark, "Story 3 Description"),
            Story(4, R.drawable.darks, "Story 4 Description"),
            Story(5, R.drawable.darksi, "Story 5 Description"),
            Story(6, R.drawable.darksid, "Story 6 Description"),
        )

        val storyList = listOf(
            Story(1, R.drawable.da),
            Story(2, R.drawable.dar),
            Story(3, R.drawable.dark),
            Story(4, R.drawable.darks),
            Story(5, R.drawable.darksi),
            Story(6, R.drawable.darksid),
        )

        val discountData = listOf(
            Story(1, R.drawable.mvideo),
            Story(2, R.drawable.yandex_plus),
            Story(3, R.drawable.coffe)
        )
    }
}

data class MessageStory(val id: Int, val imgUser: Int) {
    companion object {
        val messageData = listOf(
            MessageStory(1, R.drawable.da),
            MessageStory(2, R.drawable.da),
            MessageStory(3, R.drawable.da),
        )
    }
}