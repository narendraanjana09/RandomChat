package com.example.randomchat.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.randomchat.ui.home.HomeActivity
import com.example.randomchat.ui.home.extra.SavedText
import com.example.randomchat.ui.splash.SplashScreensActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

            val splashDone=SavedText(this).getBoolean(SavedText.SPLASH_DONE)
        if(!splashDone){
            startActivity(Intent(this@MainActivity, SplashScreensActivity::class.java))
            finish()
        }else{
            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
            finish()
        }
    }
}