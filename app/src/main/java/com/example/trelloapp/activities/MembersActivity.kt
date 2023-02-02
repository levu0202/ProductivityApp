package com.example.trelloapp.activities

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.trelloapp.R
import com.example.trelloapp.adapter.MemberListItemAdapter
import com.example.trelloapp.databinding.ActivityMembersBinding
import com.example.trelloapp.databinding.ActivityMyProfileBinding
import com.example.trelloapp.databinding.DialogSearchMemberBinding
import com.example.trelloapp.firebase.FirestoreClass
import com.example.trelloapp.models.Board
import com.example.trelloapp.models.User
import com.example.trelloapp.utils.Constants
import com.google.android.material.snackbar.Snackbar
import com.google.api.Distribution.BucketOptions.Linear

class MembersActivity : BaseActivity() {

    private var binding : ActivityMembersBinding? = null
    private var mBoardDetails : Board? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMembersBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        if (intent.hasExtra(Constants.BOARD_DETAIL)) {mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!}
        FirestoreClass().getAssignedMemberList(this, mBoardDetails?.assignedTo!!)
        setUpActionBar()
    }


    fun memberDetail(member: User)
    {

        mBoardDetails?.assignedTo!!.add(member.id)

        FirestoreClass().assignUserBoard(this, mBoardDetails!!, member)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.action_member -> {
                dialogSearchMember()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun dialogSearchMember()
    {
        val dialogBinding : DialogSearchMemberBinding = DialogSearchMemberBinding.inflate(layoutInflater)
        dialogBinding.tvAdd.setOnClickListener {
            val memberEmail = dialogBinding.etEmailSearchMember.text.toString()
            if (memberEmail.isNotEmpty()) {
                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass().getMemberDetail(this, memberEmail)}
            else {Toast.makeText(this@MembersActivity, "Please enter the email", Toast.LENGTH_LONG).show()}
        }
        val dialog = AlertDialog.Builder(this).setView(dialogBinding.root).create()
        dialogBinding.tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun setUpActionBar()
    {
        val toolBar = findViewById<Toolbar>(R.id.toolbar_members_activity)
        setSupportActionBar(toolBar)
        toolBar?.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24dp)
        toolBar?.setNavigationOnClickListener {
            setResult(Activity.RESULT_OK)
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflator : MenuInflater = menuInflater
        inflator.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }



    fun userDetail (users: ArrayList<User>)
    {
        val memberAdapter = MemberListItemAdapter(this@MembersActivity, users)
        binding?.rvMembersList!!.adapter = memberAdapter
        binding?.rvMembersList!!.layoutManager = LinearLayoutManager(this@MembersActivity)
        binding?.rvMembersList!!.setHasFixedSize(true)
    }


    fun successfullyAssignUser()
    {
        hideProgressDialog()
        Toast.makeText(this@MembersActivity, "Successfully assigned user", Toast.LENGTH_LONG).show()
        FirestoreClass().getAssignedMemberList(this, mBoardDetails?.assignedTo!!)
    }


}