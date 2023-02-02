package com.example.trelloapp.activities.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import com.example.trelloapp.activities.BaseActivity
import com.example.trelloapp.databinding.ActivitySplashBinding
import com.example.trelloapp.firebase.FirestoreClass

class SplashActivity : BaseActivity() {

    private var binding : ActivitySplashBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivitySplashBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        val typeFace : Typeface = Typeface.createFromAsset(assets, "Pacifico.ttf")
        binding?.tvAppName?.typeface = typeFace


        Handler(Looper.getMainLooper()).postDelayed({
            var currentUserId = getCurrentUserId()
            if (currentUserId.isNotEmpty())
            {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }
            else
            {
                startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
            }
            finish()
        }, 2500)




    }
}