package com.example.randomchat.ui

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import android.widget.Toast

class App : Application() {
    override fun onCreate() {
        super.onCreate()

    }
    private fun showToast(message: String) {
        Toast.makeText(this, "" + message, Toast.LENGTH_SHORT).show()
    }
}