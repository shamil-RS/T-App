package com.example.tapp

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@SuppressLint(
    "StateFlowValueCalledInComposition", "UnrememberedMutableState",
    "AutoboxingStateValueProperty", "NewApi"
)
@Composable
fun StoryViewer(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    storyId: Int = 0
) {
    val viewModel: StoryViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    // Launch effect to load the story when the screen is displayed
    LaunchedEffect(key1 = storyId) {
        viewModel.handleIntent(StoryViewerIntent.LoadStory(storyId))
    }

    // Display content based on state (loading, success, error)
    when {
        state.isLoading -> CircularProgressIndicator()
        state.error != null -> Text(state.error!!)
        else -> {
            state.story?.let { story ->
                val sheetState = rememberBottomSheetState(
                    initialValue = BottomSheetValue.Collapsed
                )
                val scaffoldState = rememberBottomSheetScaffoldState(
                    bottomSheetState = sheetState
                )

                var currentIndex by remember { mutableIntStateOf(0) }
                var progressStep by remember { mutableIntStateOf(0) }
                val totalSteps = 1
                val isPaused = remember { mutableStateOf(false) }
                val isReactionBlockVisible = mutableStateOf(false)
                val isSelectVisible = mutableStateOf(false)
                val scrollState = rememberLazyListState()
                val year = LocalDateTime.now().year
                val day = LocalDateTime.now().dayOfMonth
                val month = LocalDateTime.now().month
                val selectedCommentIndex = remember { mutableStateOf<Int?>(null) }
                val selectedReaction = remember { mutableStateOf<Int?>(null) }
                val coroutineScope = rememberCoroutineScope()

                BottomSheetScaffold(
                    modifier = modifier.wrapContentHeight(),
                    scaffoldState = scaffoldState,
                    sheetPeekHeight = 0.dp,
                    sheetBackgroundColor = Color(0xFF1c1c1e),
                    sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    sheetContent = {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.Gray)
                            )
                            androidx.compose.material.Text(
                                text = "Комментарии",
                                color = Color.White,
                                fontSize = 20.sp
                            )
                            androidx.compose.material.Text(
                                text = "$day $month $year",
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                modifier = Modifier,
                                text = viewModel.getCommentCountText(state.comments.size),
                                style = MaterialTheme.typography.subtitle1,
                                color = Color.Gray
                            )

                            selectedCommentIndex.value?.let { index ->
                                AnimatedVisibility(
                                    visible = isReactionBlockVisible.value,
                                    enter = fadeIn() + expandVertically(),
                                    exit = fadeOut() + shrinkVertically()
                                ) {
                                    ReactionSelectionBlock(
                                        reactions = state.reactions,
                                        selectedReaction = selectedReaction.value,
                                        onReactionSelected = { reactionId ->
                                            if (selectedReaction.value == reactionId) {
                                                viewModel.handleIntent(
                                                    StoryViewerIntent.UpdateReaction(
                                                        index,
                                                        0
                                                    )
                                                )
                                                selectedReaction.value = null
                                            } else {
                                                viewModel.handleIntent(
                                                    StoryViewerIntent.UpdateReaction(
                                                        index,
                                                        reactionId
                                                    )
                                                )
                                                selectedReaction.value = reactionId
                                            }
                                            isReactionBlockVisible.value = false
                                            selectedCommentIndex.value = null
                                        }
                                    )
                                }
                            }

                            LazyColumn(
                                state = scrollState,
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                itemsIndexed(state.comments) { index, comment ->
                                    MessageBlock(
                                        imgUser = R.drawable.da,
                                        userName = comment.userName,
                                        text = comment.text,
                                        icon = state.reactions.find { it.id == comment.reaction }?.reaction ?: R.drawable.like,
                                        count = comment.reaction,
                                        onClick = {
                                            selectedCommentIndex.value = index
                                            selectedReaction.value = comment.reaction
                                            isReactionBlockVisible.value =
                                                !isReactionBlockVisible.value
                                        },
                                        onClickCopyText = { commentText ->
                                            viewModel.handleIntent(
                                                StoryViewerIntent.CopyComment(
                                                    commentText
                                                )
                                            )
                                        },
                                        selectedReaction = selectedReaction.value,
                                        isSelected = selectedCommentIndex.value == index,
                                        timestamp = comment.timestamp,
                                        onReactionSelected = { reactionId ->
                                            if (selectedReaction.value == reactionId) {
                                                viewModel.handleIntent(
                                                    StoryViewerIntent.UpdateReaction(
                                                        index,
                                                        0
                                                    )
                                                )
                                                selectedReaction.value = null
                                            } else {
                                                viewModel.handleIntent(
                                                    StoryViewerIntent.UpdateReaction(
                                                        index,
                                                        reactionId
                                                    )
                                                )
                                                selectedReaction.value = reactionId
                                            }
                                        }
                                    )
                                }
                            }
                            SendMessage(
                                onSendMessage = { comment ->
                                    viewModel.handleIntent(StoryViewerIntent.AddComment(comment.text))
                                }
                            )
                        }
                    }
                ) {
                    // Handle progress and story changes
                    LaunchedEffect(currentIndex) {
                        progressStep = 0 // Reset progress step when currentIndex changes
                        delay(5_000) // Duration for viewing each story
                        if (!isPaused.value) {
                            if (currentIndex < state.comments.size - 1) {
                                currentIndex += 1 // Move to next story
                            } else {
                                navController.popBackStack() // Close the viewer if all stories are viewed
                            }
                        }
                    }

                    // Handle progress update
                    LaunchedEffect(progressStep) {
                        delay(5_000) // Adjust for actual progress update interval
                        if (progressStep < totalSteps && !isPaused.value) {
                            progressStep += 1
                        }
                    }

                    Column(
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                            val imageModifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = { offset ->
                                            val width = constraints.maxWidth
                                            if (offset.x < width / 2) {
                                                if (currentIndex > 0) {
                                                    currentIndex -= 1
                                                }
                                            } else {
                                                if (currentIndex < state.comments.size - 1) {
                                                    currentIndex += 1
                                                }
                                            }
                                        },
                                        onPress = {
                                            try {
                                                isPaused.value = true
                                                awaitRelease()
                                            } finally {
                                                isPaused.value = false
                                            }
                                        }
                                    )
                                }

                            this@Column.AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + slideInVertically(),
                                exit = fadeOut() + slideOutVertically()
                            ) {
                                StoryPage(
                                    story = story,
                                    modifier = Modifier.then(imageModifier),
                                    onClose = { navController.popBackStack() }
                                )
                            }

                            OptionsStory(
                                modifier = Modifier.align(Alignment.BottomCenter),
                                count = state.comments.size,
                                isSelected = isSelectVisible.value,
                                onSelectedChange = { isSelectVisible.value = it },
                                onClick = {
                                    coroutineScope.launch {
                                        if (sheetState.isCollapsed) {
                                            sheetState.expand()
                                            isPaused.value = true
                                        } else {
                                            sheetState
                                        }
                                    }
                                }
                            )

                            InstagramProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),
                                stepCount = totalSteps,
                                stepDuration = 5_000,
                                unSelectedColor = Color.LightGray,
                                selectedColor = Color.White,
                                currentStep = progressStep,
                                onStepChanged = { /* Handle step change if needed */ },
                                isPaused = isPaused.value, // Pass the pause state
                                onComplete = { /* Handle completion if needed */ }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OptionsStory(
    modifier: Modifier = Modifier,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    onSelectedChange: (Boolean) -> Unit
) {

    val colors = if (isSelected) Color.Red else Color(0xFFd4d4d4)

    Row(
        modifier = modifier.padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(modifier = Modifier.clickable { onSelectedChange(!isSelected) }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    tint = colors,
                    modifier = Modifier.size(28.dp),
                    contentDescription = null
                )
                Text(text = "4.6K", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors)
            }
        }
        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
        Box(modifier = Modifier.clickable { onClick() }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.comment),
                    tint = Color(0xFFd4d4d4),
                    modifier = Modifier.size(28.dp),
                    contentDescription = null
                )
                Text(text = "$count", fontSize = 16.sp, color = Color(0xFFd4d4d4))
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.Share,
            tint = Color(0xFFd4d4d4),
            modifier = Modifier.size(28.dp),
            contentDescription = null
        )
        Icon(
            imageVector = Icons.Rounded.Star,
            tint = Color(0xFFd4d4d4),
            modifier = Modifier.size(28.dp),
            contentDescription = null
        )
    }
}

@SuppressLint("AutoboxingStateValueProperty")
@Composable
fun InstagramProgressIndicator(
    modifier: Modifier = Modifier,
    stepCount: Int,
    stepDuration: Int,
    unSelectedColor: Color,
    selectedColor: Color,
    currentStep: Int,
    onStepChanged: (Int) -> Unit,
    isPaused: Boolean = false,
    onComplete: () -> Unit
) {
    val progress = remember { Animatable(0f) }
    val currentStepState = remember { mutableIntStateOf(currentStep) }

    // Update the current step state when currentStep changes
    LaunchedEffect(currentStep) {
        currentStepState.value = currentStep
    }

    // Handle progress animation
    LaunchedEffect(currentStepState.value, isPaused) {
        if (isPaused) {
            progress.stop()
        } else {
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = stepDuration,
                    easing = LinearEasing
                )
            )
            if (currentStepState.value < stepCount - 1) {
                currentStepState.value += 1
                onStepChanged(currentStepState.value)
            } else {
                onComplete()
            }
        }
    }

    Row(
        modifier = modifier
    ) {
        for (i in 0 until stepCount) {
            val stepProgress = when {
                i == currentStepState.value -> progress.value
                i > currentStepState.value -> 0f
                else -> 1f
            }

            LinearProgressIndicator(
                color = selectedColor,
                backgroundColor = unSelectedColor,
                progress = stepProgress,
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
                    .height(4.dp)
            )
        }
    }
}

@Composable
fun StoryPage(
    story: Story,
    modifier: Modifier = Modifier,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier.padding(top = 5.dp, bottom = 65.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(15.dp)
                    .clickable { onClose() }) {
                Icon(
                    modifier = Modifier
                        .padding(5.dp)
                        .size(32.dp),
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Image(
                painter = painterResource(id = story.imageResId),
                contentDescription = story.description,
                modifier = modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds,
            )
            Spacer(modifier = Modifier.padding(vertical = 12.dp))
            Text(
                text = story.description,
                color = Color.White,
                modifier = Modifier
                    .padding(16.dp),
                fontSize = 20.sp
            )
        }
    }
}