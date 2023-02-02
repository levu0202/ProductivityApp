package com.example.trelloapp.activities.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import com.example.trelloapp.activities.BaseActivity
import com.example.trelloapp.databinding.ActivityIntroBinding
import com.example.trelloapp.firebase.FirestoreClass

class IntroActivity : BaseActivity() {

    private var binding : ActivityIntroBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityIntroBinding.inflate(layoutInflater)
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
        binding?.tvAppNameIntro?.typeface = typeFace


        binding?.btnSignUpIntro?.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java ))
        }

        binding?.btnSignInIntro?.setOnClickListener{
            startActivity(Intent(this, SignInActivity::class.java ))
        }
    }
}