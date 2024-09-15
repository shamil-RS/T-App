package com.example.tapp

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

sealed class StoryViewerIntent {
    data class LoadStory(val storyId: Int) : StoryViewerIntent()
    data class AddComment(val commentText: String) : StoryViewerIntent()
    data class UpdateReaction(val commentIndex: Int, val reactionId: Int) : StoryViewerIntent()
    data class CopyComment(val commentText: String) : StoryViewerIntent()
}

@HiltViewModel
class StoryViewModel @Inject constructor(
    private val repository: StoryRepository,
    private val clipboardManager: ClipboardManager
) : ViewModel() {

    private val _state = MutableStateFlow(StoryViewerState(isLoading = true))
    val state: StateFlow<StoryViewerState> = _state.asStateFlow()

    fun handleIntent(intent: StoryViewerIntent) {
        viewModelScope.launch {
            when (intent) {
                is StoryViewerIntent.LoadStory -> loadStory(intent.storyId)
                is StoryViewerIntent.AddComment -> addComment(intent.commentText)
                is StoryViewerIntent.UpdateReaction -> updateReaction(
                    intent.commentIndex,
                    intent.reactionId
                )

                is StoryViewerIntent.CopyComment -> copyComment(intent.commentText)
            }
        }
    }

    private suspend fun loadStory(storyId: Int) {
        try {
            val story = repository.getStory(storyId)
            val comments = repository.getCommentsForStory(storyId)
            _state.value = StoryViewerState(story = story, comments = comments)
        } catch (e: Exception) {
            _state.value = StoryViewerState(error = e.message)
        } finally {
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    private fun addComment(commentText: String) {
        val newComment = Comment(
            userName = "Mark",
            text = commentText,
            timestamp = LocalDateTime.now()
        )
        _state.value = _state.value.copy(comments = _state.value.comments + newComment)
        // You would typically also send this new comment to your repository
    }

    private fun updateReaction(commentIndex: Int, reactionId: Int) {
        val updatedComments = _state.value.comments.mapIndexed { index, comment ->
            if (index == commentIndex) {
                comment.copy(reaction = if (reactionId == 0) 0 else reactionId)
            } else {
                comment
            }
        }
        _state.value = _state.value.copy(comments = updatedComments)
        // You would typically also send this update to your repository
    }

    private fun copyComment(commentText: String) {
        val clip = ClipData.newPlainText("Comment Text", commentText)
        clipboardManager.setPrimaryClip(clip)
    }

    fun getCommentCountText(count: Int): String {
        return when {
            count % 10 == 1 && count % 100 != 11 -> "$count Комментарий"
            count % 10 in 2..4 && count % 100 !in 12..14 -> "$count Комментария"
            else -> "$count Комментариев"
        }
    }
}


data class StoryViewerState(
    val story: Story? = null,
    val comments: List<Comment> = emptyList(),
    val reactions: List<Reactions> = Reactions.reactionData,
    val isLoading: Boolean = false, val error: String? = null
)

data class RatingUiState(
    val listArticle: List<Article> = mock,
    val reactionList: List<Reactions> = Reactions.reactionData,
    val currentArticle: Int = 0,
) {
    companion object {
        val mock = listOf(
            Article(
                allComment = listOf(
                    Comment(
                        text = "My Love Game.",
                        userName = "May Gera",
                    ),
                    Comment(
                        text = "Early reviews from critics and influencers can shape public perception before a game’s release. " +
                                "Positive previews can elevate expectations significantly, " +
                                "while negative feedback can lead to skepticism.\n",
                        userName = "John Genry",
                    ),
                )
            ),
            Article(
                allComment = listOf(
                    Comment(
                        text = "In most cases, expectations were high",
                        userName = "John Poesano",
                    )
                )
            )
        )
    }
}

data class Article(
    val allComment: List<Comment> = emptyList(),
)

data class Comment(
    val text: String,
    val userName: String = "",
    val reaction: Int = 0,
    @SuppressLint("NewApi")
    val timestamp: LocalDateTime = LocalDateTime.now()
)

data class Reactions(val id: Int = 0, val reaction: Int = 0) {
    companion object {
        val reactionData = listOf(
            Reactions(1, R.drawable.like),
            Reactions(2, R.drawable.neutral),
            Reactions(3, R.drawable.laughter),
            Reactions(4, R.drawable.sad),
            Reactions(5, R.drawable.bad_reactions),
        )
    }
}

