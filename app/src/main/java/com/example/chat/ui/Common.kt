package com.example.chat.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chat.R
import com.example.chat.ui.theme.ChatTheme

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