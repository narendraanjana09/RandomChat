package com.nsa.randomchat.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.nsa.randomchat.R
import com.nsa.randomchat.databinding.ActivitySpashScreensBinding
import com.nsa.randomchat.ui.home.HomeActivity
import com.nsa.randomchat.ui.home.extra.Analyitcs
import com.nsa.randomchat.ui.home.extra.SavedText

class SplashScreensActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySpashScreensBinding
    var page=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= DataBindingUtil.setContentView(this, R.layout.activity_spash_screens)
        val window: Window =this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor =
            ContextCompat.getColor(this, R.color.purple_700)
        binding.nextBtn.setOnClickListener {
            if(page==1){
                val splashDone=SavedText(this).getBoolean(SavedText.SPLASH_DONE)
                if(!splashDone){
                SavedText(this).setBoolean(SavedText.SPLASH_DONE,true)
                startActivity(Intent(this, HomeActivity::class.java))
                }
                finish()
            }else{
            changeButton()
            }
        }
        Analyitcs().log("spalsh_opened","Splash Started")

    }
    var float=1f
    var progress1=0
    private fun changeButton() {
        if(page==0){
            window.statusBarColor =
                ContextCompat.getColor(this, R.color.teal_500)
            binding.rootLayout.setBackgroundResource(R.drawable.splash_2)
            binding.tvHeading.text="Connection Will Be Made\nWith Random User\nAvailable In Room"
            binding.tvDesc.text="Select Your Avtar"
        }

        object :CountDownTimer(100,10){
            override fun onTick(p0: Long) {
                progress1+=7
                float-=0.01f
                binding.shadowCard.apply {
                    scaleX=float
                    scaleY=float
                }
                binding.progress1.apply {
                    scaleX=float
                    scaleY=float
                    if(page==0){
                        progress=progress1
                    }
                }
                binding.progress2.apply {
                    scaleX=float
                    scaleY=float
                }
            }

            override fun onFinish() {
                progress1=0
                float=1f
                binding.shadowCard.apply {
                    scaleX=float
                    scaleY=float
                }
                binding.progress1.apply {
                    scaleX=float
                    scaleY=float
                    if(page==0){
                        progress=50
                    }
                }
                binding.progress2.apply {
                    scaleX=float
                    scaleY=float
                }
                page++

            }
        }.start()
    }
}