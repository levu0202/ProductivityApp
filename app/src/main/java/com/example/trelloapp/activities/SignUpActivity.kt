package com.example.trelloapp.activities.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.example.trelloapp.R
import com.example.trelloapp.activities.BaseActivity
import com.example.trelloapp.databinding.ActivitySignUpBinding
import com.example.trelloapp.firebase.FirestoreClass
import com.example.trelloapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : BaseActivity() {
    private var binding: ActivitySignUpBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolBarSignUpActivity)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        if (supportActionBar != null)
        {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Sign Up"
        }
        binding?.toolBarSignUpActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
        binding?.btnSignUp?.setOnClickListener{
            registerUser()
        }
    }

    private fun registerUser()
    {
        val name : String = binding?.etName?.text.toString()!!.trim {it <= ' '}
        val email : String = binding?.etEmail?.text.toString()!!.trim {it <= ' '}
        val password : String = binding?.etPassword?.text.toString()!!.trim {it <= ' '}

        if (validateForm(name, email, password))
        {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                {task ->
                    if (task.isSuccessful)
                    {
                        val firebaseUser : FirebaseUser = task.result!!.user!!
                        val registeredEmail  = firebaseUser.email!!
                        val user = User(firebaseUser.uid, name, registeredEmail)
                        FirestoreClass().registerUser(this, user)
                    }
                    else
                    {
                        Toast.makeText(this@SignUpActivity, task.exception!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }

    private fun validateForm(name : String, email : String, password : String) : Boolean
    {
        return when
        {
            TextUtils.isEmpty(name) ->
            {
                showErrorSnackBar("Please enter the name")
                return false
            }
            TextUtils.isEmpty(email) ->
            {
                showErrorSnackBar("Please enter the email")
                return false
            }
            TextUtils.isEmpty(password) ->
            {
                showErrorSnackBar("Please enter the password")
                return false
            }
            else -> {return true}
        }
    }

    fun userRegisteredSuccess() {
        Toast.makeText(this@SignUpActivity, "You have successfully registed a user", Toast.LENGTH_SHORT).show()
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()
    }
}