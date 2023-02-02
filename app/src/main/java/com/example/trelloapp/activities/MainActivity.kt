package com.example.trelloapp.activities.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.trelloapp.R
import com.example.trelloapp.activities.BaseActivity
import com.example.trelloapp.activities.CreateBoardActivity
import com.example.trelloapp.activities.MyProfileActivity
import com.example.trelloapp.activities.TaskListActivity
import com.example.trelloapp.adapter.BoardItemsAdapter
import com.example.trelloapp.databinding.ActivityMainBinding
import com.example.trelloapp.databinding.AppBarMainBinding
import com.example.trelloapp.databinding.NavHeaderMainBinding
import com.example.trelloapp.firebase.FirestoreClass
import com.example.trelloapp.models.Board
import com.example.trelloapp.models.User
import com.example.trelloapp.utils.Constants
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity(), OnNavigationItemSelectedListener {

    private var binding : ActivityMainBinding? = null
    private var toolBarBinding : AppBarMainBinding? = null
    private var mUserName : String? = null
    private var callingActivity : String = ""

    private var startUpdateActivityAndGetResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {
            result ->
        if (result.resultCode == Activity.RESULT_OK && callingActivity == "profile") {FirestoreClass().signInUser(this)}
        else if (result.resultCode == Activity.RESULT_OK && callingActivity == "board")
        {
            FirestoreClass().signInUser(this, true)
        }
        else if (result.resultCode == Activity.RESULT_OK && callingActivity == "taskListActivity")
        {
            FirestoreClass().signInUser(this, true)
        }
        else {
            Log.e("onActivityResult()", "Profile update cancelled by user")}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        setupActionBar()
        binding?.navView!!.setNavigationItemSelectedListener(this)
        FirestoreClass().signInUser(this, true)

        binding?.iAppBarMain!!.fabCreateBoard.setOnClickListener{
            callingActivity = "board"
            val intent = Intent(this@MainActivity, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            startUpdateActivityAndGetResult.launch(intent)
        }
    }

    fun populateBoardListToUI(boardList : ArrayList<Board>)
    {
        if (boardList.size > 0)
        {
            binding?.iAppBarMain!!.mainContent.tvNoBoardAvailable.visibility = View.GONE
            binding?.iAppBarMain!!.mainContent.rvBoardList.visibility = View.VISIBLE
            binding?.iAppBarMain!!.mainContent.rvBoardList.setHasFixedSize(true)
            val boardAdapter = BoardItemsAdapter(boardList)
            binding?.iAppBarMain!!.mainContent.rvBoardList.layoutManager = LinearLayoutManager(this@MainActivity)
            binding?.iAppBarMain!!.mainContent.rvBoardList.adapter = boardAdapter
            boardAdapter.setOnClickListener( object :
                BoardItemsAdapter.OnClickListener {
                override fun onClick(position : Int, model : Board)
                {
                    callingActivity = "taskListActivity"
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    startUpdateActivityAndGetResult.launch(intent)
                }}
                )

        }
        else
        {
            binding?.iAppBarMain!!.mainContent.tvNoBoardAvailable.visibility = View.VISIBLE
            binding?.iAppBarMain!!.mainContent.rvBoardList.visibility = View.GONE
        }
    }

    private fun setupActionBar()
    {
        val toolBar = findViewById<Toolbar>(R.id.tool_bar_main_activity)
        setSupportActionBar(toolBar)
        toolBar.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        toolBar.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer()
    {
        if (binding?.drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            binding?.drawerLayout!!.closeDrawer(GravityCompat.START)
        }
        else
        {
            binding?.drawerLayout!!.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (binding?.drawerLayout!!.isDrawerOpen(GravityCompat.START))
        {
            binding?.drawerLayout!!.closeDrawer(GravityCompat.START)
        }
        else
        {
            doubleBackToExit()
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.nav_my_profile ->
            {
                callingActivity = "profile"
                startUpdateActivityAndGetResult.launch(Intent(this@MainActivity, MyProfileActivity::class.java))
            }
            R.id.nav_sign_out ->
            {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@MainActivity, IntroActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
                finish()
            }
        }
        binding?.drawerLayout!!.closeDrawer(GravityCompat.START)
        return true;
    }

    fun updateNavigationUserDetail(loggedInUser: User, readBoardList : Boolean = false) {
        mUserName = loggedInUser.name
        val headerView = binding?.navView!!.getHeaderView(0)
        val headerBinding = NavHeaderMainBinding.bind(headerView)

        Glide
            .with(this)
            .load(loggedInUser.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(headerBinding.navUserImage)

        headerBinding.tvUsername.text = loggedInUser.name

        if (readBoardList) {
            FirestoreClass().getBoardList(this)
        }

    }


}