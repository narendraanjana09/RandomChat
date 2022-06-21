package com.nsa.randomchat.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nsa.randomchat.ui.home.HomeActivity
import com.nsa.randomchat.ui.home.extra.Analyitcs
import com.nsa.randomchat.ui.home.extra.SavedText
import com.nsa.randomchat.ui.splash.SplashScreensActivity


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
        Analyitcs().log("app_open","App Started")
    }
}