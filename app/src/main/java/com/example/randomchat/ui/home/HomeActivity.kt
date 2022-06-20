package com.example.randomchat.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.example.randomchat.R
import com.example.randomchat.databinding.ActivityHomeBinding
import com.example.randomchat.ui.home.viewmodel.MainViewModel

class HomeActivity : AppCompatActivity() {
    private lateinit var binding:ActivityHomeBinding
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

    }

}