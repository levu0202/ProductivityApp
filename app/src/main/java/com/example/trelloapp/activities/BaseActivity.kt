package com.example.trelloapp.activities

import android.app.Dialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.trelloapp.R
import com.example.trelloapp.databinding.ActivityBaseBinding
import com.example.trelloapp.databinding.DialogProgressBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

open class BaseActivity : AppCompatActivity() {

    private var binding : ActivityBaseBinding? = null
    private var doubleBackToExitPressedOnce = false
    private lateinit var mProgressDialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityBaseBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)


    }

    fun showProgressDialog(text : String)
    {
        mProgressDialog = Dialog(this)
        val dialogBinding = DialogProgressBinding.inflate(layoutInflater)
        mProgressDialog.setContentView(dialogBinding.root)

        dialogBinding.tvProgressText.text = text
        //Start the dialog and display it on screen.
        mProgressDialog.show()
    }

    fun hideProgressDialog()
    {
        mProgressDialog.dismiss()
    }

    fun getCurrentUserId() : String{
        val currentUser =  FirebaseAuth.getInstance().currentUser

        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    fun showSnackBar(message: String)
    {
        val snackbar = Snackbar.make(findViewById(androidx.appcompat.R.id.content), message, Snackbar.LENGTH_LONG)
        snackbar.setAction("Dismiss") {snackbar.dismiss()}
        snackbar.show()
    }


    fun doubleBackToExit()
    {
        if (doubleBackToExitPressedOnce)
        {
            super.onBackPressed()
            return
        }
        Toast.makeText(this, "Back button has been pressed", Toast.LENGTH_SHORT).show()
        doubleBackToExitPressedOnce = true
        Handler().postDelayed({doubleBackToExitPressedOnce = true}, 2000)
    }

    fun showErrorSnackBar(message : String)
    {
        val snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this@BaseActivity,
        R.color.snackbar_error_color))
        snackBar.show()
    }
}