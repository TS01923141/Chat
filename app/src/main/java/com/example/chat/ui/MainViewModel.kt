package com.example.chat.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.chat.model.data.Message
import com.example.chat.model.data.initialMessages
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "MainViewModel"
class MainViewModel: ViewModel() {
    private val _messageListFlow = MutableStateFlow(initialMessages.toMutableList())
    var messageListFlow : StateFlow<List<Message>> = _messageListFlow

    suspend fun addMessage(text: String) {
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val list: MutableList<Message> = mutableListOf()
        list.addAll(_messageListFlow.value)
        list.add(0,
            Message(
                "me",
                text,
                dateFormat.format(Calendar.getInstance().timeInMillis)
            )
        )
        _messageListFlow.emit(list)
    }
    var messageList : MutableList<Message> = mutableStateListOf(*initialMessages.toTypedArray())

}