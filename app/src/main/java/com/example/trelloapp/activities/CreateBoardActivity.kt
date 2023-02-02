package com.example.trelloapp.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.trelloapp.R
import com.example.trelloapp.databinding.ActivityCreateBoardBinding
import com.example.trelloapp.firebase.FirestoreClass
import com.example.trelloapp.models.Board
import com.example.trelloapp.utils.Constants
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.IOException
import java.util.jar.Manifest

class CreateBoardActivity : BaseActivity() {

    private var binding : ActivityCreateBoardBinding? = null
    private var mSelectedImageFileUri : Uri? = null
    private var mBoardImageUri : String = ""
    private var mUserName : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        setUpActionBar()
        if (intent.hasExtra(Constants.NAME)) {mUserName = intent.getStringExtra(Constants.NAME)!!}
        binding?.btnCreate!!.setOnClickListener {
            if (mSelectedImageFileUri != null) {uploadBoardImage()}
            else {createBoard()}

        }
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {
        result ->
        if (result.resultCode == Activity.RESULT_OK && result!!.data != null)
        {
            val data = result.data
            if (data != null) {mSelectedImageFileUri = data.data}
            try{
                Glide
                    .with(this@CreateBoardActivity)
                    .load(Uri.parse(mSelectedImageFileUri.toString()))
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(binding?.ivBoardImage as ImageView)
            }
            catch (e : IOException)
            {
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
                Toast.makeText(this@CreateBoardActivity, "Permission granted", Toast.LENGTH_LONG).show()}
            else
            {
                Toast.makeText(this@CreateBoardActivity, "Permission not granted", Toast.LENGTH_LONG).show()}
        }
    }

    private fun setUpActionBar()
    {
        val toolBar = findViewById<Toolbar>(R.id.tool_bar_create_board_activity)
        setSupportActionBar(toolBar)
        toolBar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24dp)
        toolBar.title = resources.getString(R.string.create_board_title)
        toolBar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding?.ivBoardImage!!.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED ->
                {
                    val gallaryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    resultLauncher.launch(gallaryIntent)
                }

                else ->
                {
                    requestPermissionLauncher.launch(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE))
                }
            }
        }
    }

    private fun uploadBoardImage()
    {
        showProgressDialog(resources.getString(R.string.please_wait))
        val storageRef = Firebase.storage.reference
        val sd = getFileExtension(mSelectedImageFileUri)
        val uploadTask = storageRef.child("BOARD_IMAGE/$sd").putFile(mSelectedImageFileUri!!)
        uploadTask.addOnSuccessListener {
            taskSnapShot ->
            taskSnapShot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                uri ->
                mBoardImageUri = uri.toString()
                createBoard()
            }
        }.addOnFailureListener {
            exception ->
            Toast.makeText(this@CreateBoardActivity, exception.message, Toast.LENGTH_LONG).show()
            hideProgressDialog()
        }
    }

    fun createBoard()
    {
        showProgressDialog(resources.getString(R.string.please_wait))
        val assignedUserArrayList : ArrayList<String> = ArrayList()
        assignedUserArrayList.add(getCurrentUserId())
        val board = Board(
            binding?.etBoardName!!.text.toString(),
            mBoardImageUri,
            mUserName,
            assignedUserArrayList
        )
        FirestoreClass().createBoard(this@CreateBoardActivity, board)

    }

    private fun getFileExtension(uri : Uri?) : String?
    {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    fun boardCreatedSuccessfully()
    {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
}