package com.example.chat.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.chat.ui.theme.ChatTheme
import com.example.chat.R
import com.example.chat.model.data.Message
import com.example.chat.model.data.initialMessages
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val TAG = "Chat"

enum class InputSelector {
    NONE,
    MAP,
    DM,
    EMOJI,
    PHONE,
    PICTURE
}

private val ChatBubbleLeftShape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
private val ChatBubbleRightShape = RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp)

@Composable
fun ChatScreen(
    onMessageSent: (String) -> Unit,
    messageListFlow: StateFlow<List<Message>>,
    coroutineScope : CoroutineScope = rememberCoroutineScope()
) {
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()
    var textState by remember { mutableStateOf(TextFieldValue()) }
    var textFieldFocusState by remember { mutableStateOf(false) }
    var currentInputSelector by rememberSaveable { mutableStateOf(InputSelector.NONE) }
    val dismissKeyboard = {
        focusManager.clearFocus()
        currentInputSelector = InputSelector.NONE }
    val messageList by messageListFlow.collectAsState()
//    fun resetScroll() = coroutineScope.launch {
//        listState.animateScrollToItem(0)
//    }

    Scaffold (
        topBar = { TitleBar(title = "Chat", {}) },
        bottomBar = {
            InputBar(
                textFieldValue = textState,
                onTextChanged = { textState = it },
                onMessageSent = {
                    onMessageSent(textState.text)
                    // Reset text field and close keyboard
                    textState = TextFieldValue()
                    // Move scroll to bottom
//                    resetScroll()
                    dismissKeyboard()
                },
                keyboardShown = currentInputSelector == InputSelector.NONE && textFieldFocusState,
                onTextFieldFocused = { focused ->
                    if (focused) {
                        currentInputSelector = InputSelector.NONE
//                        resetScroll()
                    }
                    textFieldFocusState = focused
                },
                focusState = textFieldFocusState,
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding()
            )
        },
        modifier = Modifier.systemBarsPadding()
    ) {
        //自動fit bottom bar
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(bottom = it.calculateBottomPadding())) {
            ChatList(userName = "me", messageList = messageList, listState = listState)
        }
    }
}

@Preview
@Composable
private fun PreviewChatScreen() {
    ChatTheme {
        Surface {
            ChatScreen(
                onMessageSent = {},
                messageListFlow = MutableStateFlow(initialMessages)
            )
        }
    }
}

@Composable
fun TitleBar(title: String, onBackClick: () -> Unit) {
    Surface(elevation = 4.dp) {
        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { onBackClick.invoke() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_arrow_back_ios_new_24),
                    contentDescription = null
                )
            }
            Text(
                text = title,
                modifier = Modifier.align(Alignment.CenterVertically),
                style = MaterialTheme.typography.subtitle1
            )
        }
    }
}

@Preview
@Composable
private fun PreviewTitleBar() {
    ChatTheme {
        Surface {
            TitleBar(
                "Chat",
                {}
            )
        }
    }
}

val KeyboardShownKey = SemanticsPropertyKey<Boolean>("KeyboardShownKey")
var SemanticsPropertyReceiver.keyboardShownProperty by KeyboardShownKey

@Composable
fun InputBar(
    keyboardType: KeyboardType = KeyboardType.Text,
    onTextChanged: (TextFieldValue) -> Unit,
    onMessageSent: () -> Unit,
    textFieldValue: TextFieldValue,
    keyboardShown: Boolean,
    onTextFieldFocused: (Boolean) -> Unit,
    focusState: Boolean,
    modifier : Modifier = Modifier
) {
    Surface(color = MaterialTheme.colors.secondary, modifier = modifier) {
        val a11ylabel = stringResource(id = R.string.textfield_desc)
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(start = 16.dp, end = 16.dp)
            .semantics {
                contentDescription = a11ylabel
                keyboardShownProperty = keyboardShown
            }
        ) {
            //input box
            Box(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)
            ) {
                var lastFocusState by remember { mutableStateOf(false) }
                BasicTextField(
                    value = textFieldValue,
                    onValueChange = { onTextChanged(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp, 4.dp, 4.dp, 4.dp))
                        .background(MaterialTheme.colors.background)
                        .padding(8.dp)
                        .align(Alignment.CenterStart)
                        .onFocusChanged { state ->
                            if (lastFocusState != state.isFocused) {
                                onTextFieldFocused(state.isFocused)
                            }
                            lastFocusState = state.isFocused
                        },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType,
                        imeAction = ImeAction.Send
                    ),
                    maxLines = 1,
                    cursorBrush = SolidColor(LocalContentColor.current),
                    textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current)
                )

                //hint
                if (textFieldValue.text.isEmpty() && !focusState) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp, 4.dp, 4.dp, 4.dp))
                            .background(MaterialTheme.colors.background)
                            .padding(8.dp)
                            .align(Alignment.CenterStart),
                        text = stringResource(id = R.string.textfield_hint),
                        style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.secondary)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            //send
            Button(
                enabled = textFieldValue.text.isNotEmpty(),
                onClick = { onMessageSent() }, modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.primary)
            ) {
                Text(
                    text = stringResource(id = R.string.send),
                    style = MaterialTheme.typography.body2
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewInputBar() {
    ChatTheme {
        var textState by remember { mutableStateOf(TextFieldValue()) }
        Surface {
            InputBar(
                textFieldValue = textState,
                onTextChanged = { textState = it },
                onMessageSent = {},
                keyboardShown = false,
                onTextFieldFocused = {},
                focusState = false
            )
        }
    }
}

@Composable
fun ChatList(userName: String, messageList: List<Message>, listState: LazyListState, modifier: Modifier = Modifier) {
    Log.d(TAG, "ChatList: messageList.size: ${messageList.size}")
    LazyColumn(modifier = modifier, reverseLayout = true, state = listState) {
        itemsIndexed(messageList) { index, message ->
            val prevAuthor = messageList.getOrNull(index - 1)?.author
            val nextAuthor = messageList.getOrNull(index + 1)?.author
            ChatItem(
                avatorPath = message.authorImage,
                author = message.author,
                publishTime = message.timestamp,
                isFirstMessageByAuthor = prevAuthor != message.author,
                isUserMe = userName == message.author,
                content = message.content
            )
        }
//        for (index in messageList.indices) {
//            val prevAuthor = messageList.getOrNull(index - 1)?.author
//            val nextAuthor = messageList.getOrNull(index + 1)?.author
//            val message = messageList[index]
//            item {
//                ChatItem(
//                    avatorPath = message.authorImage,
//                    author = message.author,
//                    publishTime = message.timestamp,
//                    isFirstMessageByAuthor = prevAuthor != message.author,
//                    isUserMe = userName == message.author,
//                    content = message.content
//                )
//            }
//        }
    }
}

@Composable
fun ChatItem(
    avatorPath: Any,
    author: String,
    publishTime: String,
    //要不要顯示姓名頭貼資料
    isFirstMessageByAuthor: Boolean,
    //方向、頭貼
    isUserMe: Boolean,
    content: String,
) {
    Surface {
        Row(modifier = Modifier.padding(start = 8.dp, end = 8.dp)) {
            if (isFirstMessageByAuthor) {
                if (!isUserMe) Avator(
                    avatorPath = avatorPath,
                    isFirstMessageByAuthor = isFirstMessageByAuthor
                )
                Column {
                    Row {
                        if (isUserMe) Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = author,
                            style = MaterialTheme.typography.subtitle2,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = publishTime,
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                    Row {
                        if (isUserMe) Spacer(modifier = Modifier.weight(1f))
                        ChatItemBubble(
                            isUserMe = isUserMe,
                            content = content,
                        )
                    }
                }
            } else {
                if (!isUserMe) Spacer(modifier = Modifier.width(58.dp))
                else Spacer(modifier = Modifier.weight(1f))
                ChatItemBubble(
                    isUserMe = isUserMe,
                    content = content,
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewChatItem() {
    ChatTheme {
        Surface {
            Column {
                ChatItem(
                    avatorPath = R.drawable.ic_launcher_foreground,
                    author = "UserName",
                    publishTime = "8:05 PM",
                    isFirstMessageByAuthor = true,
                    isUserMe = false,
                    content = "test test test test test test "
                )
                ChatItem(
                    avatorPath = R.drawable.ic_launcher_foreground,
                    author = "Me",
                    publishTime = "8:06 PM",
                    isFirstMessageByAuthor = true,
                    isUserMe = true,
                    content = "test test test test test test "
                )
            }
        }
    }
}

@Composable
private fun Avator(avatorPath: Any, isFirstMessageByAuthor: Boolean) {
    if (isFirstMessageByAuthor) {
        AsyncImage(
            model = avatorPath,
            contentDescription = null,
            modifier = Modifier
                .padding(end = 16.dp)
                .size(42.dp)
                .border(1.5.dp, MaterialTheme.colors.primary, CircleShape)
                .border(3.dp, MaterialTheme.colors.surface, CircleShape)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Spacer(modifier = Modifier.width(74.dp))
    }
}

@Composable
fun ChatItemBubble(
    isUserMe: Boolean,
    content: String,
    modifier: Modifier = Modifier
) {
    Row() {
        Surface(
            color = if (isUserMe) MaterialTheme.colors.secondary else MaterialTheme.colors.primary,
            shape = if (isUserMe) ChatBubbleRightShape else ChatBubbleLeftShape,
            modifier = if (isUserMe) modifier.padding(
                start = 64.dp,
                top = 4.dp,
                bottom = 8.dp
            ) else modifier.padding(end = 64.dp, top = 4.dp, bottom = 8.dp)
        ) {
            Text(
                text = content,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun PreviewChatItemBubble() {
    ChatTheme {
        Surface {
            Column {
                ChatItemBubble(
                    isUserMe = true,
                    content = "Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1Test1"
                )
                ChatItemBubble(
                    isUserMe = false,
                    content = "Test2Test2Test2Test2Test2Test2Test2Test2Test2Test2Test2Test2Test2Test2Test2Test2"
                )
            }
        }
    }
}