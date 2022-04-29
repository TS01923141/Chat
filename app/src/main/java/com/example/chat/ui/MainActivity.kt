package com.example.chat.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.view.WindowCompat
import com.example.chat.ui.theme.ChatTheme
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"
class MainActivity : ComponentActivity() {
    private val viewModel : MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val coroutineScope = rememberCoroutineScope()
            ChatTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    ChatScreen(
                        onMessageSent = {text ->
                            coroutineScope.launch {
                                viewModel.addMessage(text)
                            }
                        },
                        messageList = viewModel.messageList,
                        messageListFlow = viewModel.messageListFlow,
                        coroutineScope = coroutineScope
                    )
                }
            }
        }
    }
}