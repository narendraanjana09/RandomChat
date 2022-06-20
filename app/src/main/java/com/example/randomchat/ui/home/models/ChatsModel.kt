package com.example.randomchat.ui.home.models

import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class ChatsModel(val messageText: String? = null, val messageUser: String? = null, val messageTime: Long = Date().time) {

}

