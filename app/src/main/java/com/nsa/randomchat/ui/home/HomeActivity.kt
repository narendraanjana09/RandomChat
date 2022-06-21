package com.nsa.randomchat.ui.home

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.nsa.randomchat.R
import com.nsa.randomchat.databinding.ActivityHomeBinding
import com.nsa.randomchat.extra.internet.NetworkStatus
import com.nsa.randomchat.ui.home.viewmodel.MainViewModel

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this, R.layout.activity_home)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        val window: Window =this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        findNavController(R.id.navHostFragment).addOnDestinationChangedListener { controller, destination, arguments ->
            window.statusBarColor =
                ContextCompat.getColor(this@HomeActivity, R.color.background)
            when (destination.id) {
                R.id.randomMatchFragment -> {
                    window.statusBarColor =
                        ContextCompat.getColor(this@HomeActivity, R.color.teal_700)
                }
                R.id.welcomeFragment -> {

                }
            }
        }
        NetworkStatus(this, supportFragmentManager).startInternetCheck()

    }

}