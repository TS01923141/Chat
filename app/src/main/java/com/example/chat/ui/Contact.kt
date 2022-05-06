package com.example.chat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.chat.R
import com.example.chat.model.data.Message
import com.example.chat.model.data.User
import com.example.chat.model.data.initialUsers
import com.example.chat.ui.theme.ChatTheme

@Composable
fun ContactScreen(userList: List<User>) {
    Scaffold(
        topBar = { TitleBar(title = "Contact") {} },
        modifier = Modifier.systemBarsPadding())
    {
        ContactList(userList = userList, modifier = Modifier.fillMaxSize())
    }
}

@Preview
@Composable
fun PreviewContactScreen() {
    ChatTheme {
        androidx.compose.material.Surface {
            ContactScreen(userList = initialUsers)
        }
    }
}

@Composable
fun ContactList(userList: List<User>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(userList) {
            ContactItem(
                avatorPath = it.authorImage,
                chatName = it.userName,
                lastMessage = it.content,
                lastTime = it.timestamp,
                unreadCount = it.unreadCount
            )
        }
    }
}

@Preview
@Composable
fun PreviewContactList() {
    ChatTheme {
        androidx.compose.material.Surface {
            ContactList(userList = initialUsers)
        }
    }
}

@Composable
fun ContactItem(avatorPath: Any, chatName: String, lastMessage: String, lastTime: String, unreadCount: Int) {
    Row(modifier = Modifier.padding(8.dp)) {
        AsyncImage(model = avatorPath, contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .border(1.5.dp, MaterialTheme.colors.primary, CircleShape)
                .border(3.dp, MaterialTheme.colors.surface, CircleShape)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = chatName, maxLines = 1, style = MaterialTheme.typography.subtitle1)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = lastMessage, maxLines = 2, style = MaterialTheme.typography.body2)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = lastTime, style = MaterialTheme.typography.body2)
            Text(text = unreadCount.toString(), modifier = Modifier
                .align(Alignment.End)
                .clip(CircleShape)
                .background(MaterialTheme.colors.primary)
                .padding(4.dp),
                style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.surface)
            )
        }
    }
}

@Preview
@Composable
fun PreviewContactItem() {
    ChatTheme {
        androidx.compose.material.Surface {
            ContactItem(
                avatorPath = R.drawable.ic_launcher_foreground,
                chatName = "UserName",
                lastMessage = "test test test test test test test test test test",
                lastTime = "8:05 PM",
                unreadCount = 6
            )
        }
    }
}