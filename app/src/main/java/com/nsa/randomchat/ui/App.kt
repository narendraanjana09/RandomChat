package com.nsa.randomchat.ui

import android.app.Application
import android.widget.Toast

class App : Application() {
    override fun onCreate() {
        super.onCreate()

    }
    private fun showToast(message: String) {
        Toast.makeText(this, "" + message, Toast.LENGTH_SHORT).show()
    }
}