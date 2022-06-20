package com.example.randomchat.ui.home.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserRoomModel(val roomId: String? = null, val userId: String? = null) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}

