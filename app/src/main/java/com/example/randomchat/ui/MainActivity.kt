package com.example.randomchat.ui

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.randomchat.R
import com.example.randomchat.databinding.ActivityMainBinding
import com.example.randomchat.ui.home.HomeActivity


class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_main)
        var rotate=0f
        val anim= AnimationUtils.loadAnimation(this,R.anim.splash)
        object : CountDownTimer(2000,10){
            override fun onTick(p0: Long) {
                rotate+=10

                binding.progressLayout.rotation=rotate
                if(rotate>=720){
                    this.cancel()
                    this.onFinish()
                }
            }

            override fun onFinish() {
                startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                finish()
            }
        }.start()
        startService(Intent(baseContext, DestroyService::class.java))
        binding.progressLayout.startAnimation(anim)
    }
}