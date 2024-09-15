@file:OptIn(ExperimentalMaterialApi::class)

package com.example.tapp

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Icon
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.MaterialTheme
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@SuppressLint("NewApi")
@Composable
fun SendMessage(
    modifier: Modifier = Modifier,
    onSendMessage: (Comment) -> Unit
) {

    val focusManager = LocalFocusManager.current
    var text by remember { mutableStateOf("") }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Row {
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .width(332.dp)
                    .height(32.dp)
                    .clip(RoundedCornerShape(16.dp)),
                textStyle = TextStyle.Default.copy(color = Color.White),
                cursorBrush = SolidColor(Color.White),
                visualTransformation = VisualTransformation.None,
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                maxLines = 5,
            ) { innerTextField ->

                TextFieldDefaults.TextFieldDecorationBox(
                    value = text,
                    innerTextField = innerTextField,
                    singleLine = true,
                    enabled = true,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color(0xFF3e3e40)
                    ),
                    placeholder = {
                        Text(
                            text = "Комментарий",
                            style = MaterialTheme.typography.subtitle2,
                            color = Color.White.copy(0.3f),
                            maxLines = 1
                        )
                    },
                    interactionSource = remember { MutableInteractionSource() },
                    contentPadding = PaddingValues(horizontal = 10.dp),
                    visualTransformation = VisualTransformation.None
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xBA03CAFC))
                    .clickable(
                        onClick = {
                            if (text.isNotEmpty()) {
                                focusManager.clearFocus()
                                onSendMessage(Comment(text = text, timestamp = LocalDateTime.now()))
                                text = ""
                            }
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Done,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

@SuppressLint("NewApi", "UnrememberedMutableState")
@Composable
fun MessageBlock(
    modifier: Modifier = Modifier,
    userName: String,
    text: String,
    imgUser: Int,
    icon: Int,
    count: Int,
    timestamp: LocalDateTime,
    onClick: () -> Unit,
    onClickCopyText: (String) -> Unit,
    selectedReaction: Int?,
    isSelected: Boolean,
    onReactionSelected: (Int) -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val formattedTime = timestamp.format(formatter)

    val showReaction = remember { mutableStateOf(false) }
    val reaction = Reactions()

    // Update visibility of the reaction animation based on the selected reaction
    LaunchedEffect(selectedReaction) {
        showReaction.value = selectedReaction != null
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(16.dp))
                .align(Alignment.Bottom)
        ) {
            Image(
                painter = painterResource(id = imgUser),
                contentScale = ContentScale.FillBounds,
                contentDescription = null
            )
        }
        Box(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF3e3e40))
                .clickable { onClick() }
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = userName, color = Color(0xFF3b78d7))
                Text(
                    text = text,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    AnimatedVisibility(
                        visible = showReaction.value,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Reaction(
                            icon = icon,
                            count = count,
                            selectedReaction = selectedReaction,
                            onReactionSelected = { onReactionSelected(reaction.id) }
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clickable { onClickCopyText(text) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.copy),
                            tint = Color.Gray,
                            contentDescription = null
                        )
                    }
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text(
                        text = formattedTime,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun Reaction(
    modifier: Modifier = Modifier,
    icon: Int,
    count: Int,
    selectedReaction: Int?,
    onReactionSelected: (Int) -> Unit
) {
    val reaction = Reactions()
    Box(
        modifier = modifier
            .width(72.dp)
            .height(28.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF21C4AC))
            .clickable {
                if (selectedReaction == reaction.id) {
                    // Remove reaction if already selected
                    onReactionSelected(0)
                } else {
                    // Add or change reaction
                    onReactionSelected(reaction.id)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = icon),
                modifier = Modifier.size(20.dp),
                contentDescription = null
            )
            Text(text = "$count")
        }
    }
}

@Composable
fun ReactionSelectionBlock(
    modifier: Modifier = Modifier,
    reactions: List<Reactions>,
    selectedReaction: Int?,
    onReactionSelected: (Int) -> Unit
) {
    Column(
        modifier = modifier
            .padding(vertical = 8.dp)
            .background(Color(0xFF1c1c1e))
            .clip(RoundedCornerShape(8.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Select a response", color = Color.White, fontSize = 16.sp)
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            reactions.forEach { reaction ->
                Image(
                    painter = painterResource(id = reaction.reaction),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(4.dp)
                        .size(24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            if (selectedReaction == reaction.id) {
                                // Remove reaction if already selected
                                onReactionSelected(0)
                            } else {
                                // Add or change reaction
                                onReactionSelected(reaction.id)
                            }
                        }
                )
            }
        }
    }
}