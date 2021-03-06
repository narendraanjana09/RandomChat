package com.nsa.randomchat.ui

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import com.nsa.randomchat.ui.home.extra.Analyitcs
import com.nsa.randomchat.ui.home.extra.FireBase
import com.nsa.randomchat.ui.home.extra.SavedText

class DestroyService: Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)

        val userUid=SavedText(baseContext).getText(SavedText.USER_UID)
        val randomUserUid=SavedText(baseContext).getText(SavedText.ROOM_USER_ID)
        val roomId=SavedText(baseContext).getText(SavedText.ROOM_ID)
        Analyitcs().log("App_Destroyed","User removed app from recents")

        FireBase().apply {
            userRef.child(userUid).child("room").removeValue()
            roomRef.child(userUid).removeValue()
            if(randomUserUid.isNotEmpty()){
                roomRef.child(randomUserUid).removeValue()
            userRef.child(randomUserUid).child("room").removeValue()
            }
            if(roomId.isNotEmpty()){
            chatRoomRef.child(roomId).removeValue()
            }
        }

        stopSelf()
    }
    private fun showToast(message: String) {
        Toast.makeText(applicationContext, "" + message, Toast.LENGTH_SHORT).show()
    }
}