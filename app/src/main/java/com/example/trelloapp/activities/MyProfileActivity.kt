package com.example.trelloapp.activities


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.trelloapp.R
import com.example.trelloapp.databinding.ActivityMyProfileBinding
import com.example.trelloapp.firebase.FirestoreClass
import com.example.trelloapp.models.User
import com.example.trelloapp.utils.Constants
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    companion object
    {
        private const val READ_STORAGE_PERMISSION_CODE = 1
        private const val PICK_IMAGE_REQUEST_CODE = 2
    }

    private var binding : ActivityMyProfileBinding? = null
    private var mSelectedImageFileUrl : Uri? = null
    private var mProfileImageUrl : String = ""
    private lateinit var mUserDetail : User

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {
        result ->
        if (result.resultCode == Activity.RESULT_OK && result!!.data != null)
        {
            val data = result.data
            if (data != null) {mSelectedImageFileUrl = data.data}
            try {
                // Load the user image in the ImageView.
                Glide
                    .with(this@MyProfileActivity)
                    .load(Uri.parse(mSelectedImageFileUrl.toString())) // URI of the image
                    .centerCrop() // Scale type of the image.
                    .placeholder(R.drawable.ic_user_place_holder) // A default place holder
                    .into(binding?.ivProfileUserImage as ImageView) // the view in which the image will be loaded.
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }




    val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
    {
        permissions ->
        permissions.entries.forEach {
            val permissionName = it.key
            val isGranted = it.value

            if (isGranted) {
                Toast.makeText(this@MyProfileActivity, "Permission granted", Toast.LENGTH_LONG).show()}
            else
            {
                Toast.makeText(this@MyProfileActivity, "Permission not granted", Toast.LENGTH_LONG).show()}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        setUpActionBar()
        FirestoreClass().signInUser(this)


        binding?.ivProfileUserImage!!.setOnClickListener{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
            {
                val gallaryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                resultLauncher.launch(gallaryIntent)

            }
            else
            {
                requestPermissionLauncher.launch(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE))
            }
        }

        binding?.btnUpdate!!.setOnClickListener {
            if (mSelectedImageFileUrl != null) {uploadUserImage()}
            else {updateUserProfileData()}
        }

    }

    private fun setUpActionBar()
    {
        val toolBar = findViewById<Toolbar>(R.id.toolbar_my_profile_activity)
        setSupportActionBar(toolBar)
        if (toolBar != null)
        {

            toolBar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24dp)
            toolBar.title = resources.getString(R.string.my_profile)
        }
        toolBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }


    fun setUserDataInUI(loggedInUser: User) {
        mUserDetail = loggedInUser
        val userImage = findViewById<ImageView>(R.id.iv_profile_user_image)
        Glide.
                with(this).load(loggedInUser.image).centerCrop()
            .placeholder(R.drawable.ic_user_place_holder).into(userImage)


        binding?.etName!!.setText(loggedInUser.name)
        binding?.etEmail!!.setText(loggedInUser.email)
        if (loggedInUser.mobile != 0L) {
            binding?.etMobile!!.setText(loggedInUser.mobile.toString())
        }
    }

    private fun uploadUserImage()
    {
        showProgressDialog(resources.getString(R.string.please_wait))
        val sRef = Firebase.storage.reference
        val sd = getFileExtension(mSelectedImageFileUrl)
        val uploadTask = sRef.child("USER_IMAGE/$sd").putFile(mSelectedImageFileUrl!!)
        uploadTask.addOnSuccessListener {
            taskSnapShot ->
            Log.i("Firebase image URL", taskSnapShot.metadata!!.reference!!.downloadUrl.toString())
            taskSnapShot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                uri ->
                Log.i("Downloadable image URL", uri.toString())
                mProfileImageUrl = uri.toString()
                updateUserProfileData()
            }

        }
            .addOnFailureListener{
                exception ->
                Toast.makeText(this@MyProfileActivity, exception.message, Toast.LENGTH_LONG).show()
                hideProgressDialog()
            }

    }

    fun updateUserProfileData()
    {
        val userHashMap = HashMap<String, Any>()
        if (mProfileImageUrl.isNotEmpty() && mProfileImageUrl != mUserDetail.image)
        {
            userHashMap[Constants.IMAGE] = mProfileImageUrl
        }
        if (binding?.etName!!.toString() != mUserDetail.name)
        {
            userHashMap[Constants.NAME] = binding?.etName!!.text.toString()
        }
        if (binding?.etEmail!!.toString() != mUserDetail.email)
        {
            userHashMap[Constants.EMAIL] = binding?.etEmail!!.text.toString()
        }
        if (binding?.etMobile!!.toString() != mUserDetail.mobile.toString())
        {
            userHashMap[Constants.MOBILE] = binding?.etMobile!!.text.toString().toLong()
        }
        FirestoreClass().updateUserProfileData(this, userHashMap)
    }

    private fun getFileExtension(uri : Uri?) : String?
    {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    fun updateProfileSuccess()
    {
        Toast.makeText(this@MyProfileActivity, "Profile Updated", Toast.LENGTH_LONG).show()
        setResult(Activity.RESULT_OK)
        finish()
    }


}