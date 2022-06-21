package com.nsa.randomchat.ui.home.extra

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class Analyitcs {
    private var analytics: FirebaseAnalytics = Firebase.analytics
    fun log(tag:String,message:String){
        val bundle=Bundle()
        bundle.putString(tag,message)
        analytics.logEvent(tag,bundle)
    }
}