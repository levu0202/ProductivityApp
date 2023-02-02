package com.example.trelloapp.activities.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.example.trelloapp.R
import com.example.trelloapp.activities.BaseActivity
import com.example.trelloapp.databinding.ActivitySignInBinding
import com.example.trelloapp.firebase.FirestoreClass
import com.example.trelloapp.models.User
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : BaseActivity() {

    private var binding : ActivitySignInBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivitySignInBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarSignInActivity)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Sign In"
        }

        binding?.toolbarSignInActivity?.setNavigationOnClickListener {
            onBackPressed()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        binding?.btnSignIn?.setOnClickListener{signInRegisteredUser()}
    }

    private fun validateForm(email: String, password: String): Boolean {
        return if (TextUtils.isEmpty(email)) {
            showErrorSnackBar("Please enter email.")
            false
        } else if (TextUtils.isEmpty(password)) {
            showErrorSnackBar("Please enter password.")
            false
        } else {
            true
        }}

    private fun signInRegisteredUser()
    {
        val email : String = binding?.etEmail?.text.toString()
        val pass : String = binding?.etPassword?.text.toString()

        if (validateForm(email, pass))
        {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass).addOnCompleteListener(this)
            {
                task ->
                if (task.isSuccessful) {
                    FirestoreClass().signInUser(this)
                }
                else
                {
                    hideProgressDialog()
                    Log.d("Sign in", "signInWithEmail:failure", task.exception)
                    Toast.makeText(this@SignInActivity, "Sign in failed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun signInSuccess(loggedInUser: User?) {
        hideProgressDialog()
        Toast.makeText(this@SignInActivity, "Logged in success", Toast.LENGTH_LONG).show()
        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
        finish()

    }

}