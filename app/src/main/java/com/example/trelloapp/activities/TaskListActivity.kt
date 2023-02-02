package com.example.trelloapp.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trelloapp.R
import com.example.trelloapp.adapter.TaskListItemAdapter
import com.example.trelloapp.databinding.ActivityTaskListBinding
import com.example.trelloapp.firebase.FirestoreClass
import com.example.trelloapp.models.Board
import com.example.trelloapp.models.Card
import com.example.trelloapp.models.Task
import com.example.trelloapp.models.User
import com.example.trelloapp.utils.Constants
import com.google.firebase.ktx.Firebase

class TaskListActivity : BaseActivity() {

    private var mBoardDetails : Board? = null
    private var callingActivity : String = ""
    private var documentId : String = ""
    lateinit var memberArr : ArrayList<User>

    private var binding : ActivityTaskListBinding? = null


    private var startupdateActivityAndGetResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {
        result ->
        if (result.resultCode == Activity.RESULT_OK && callingActivity == "MembersActivity")
        {
            FirestoreClass().getBoardDetail(this, documentId)
        }
        else if (result.resultCode == Activity.RESULT_OK && callingActivity == "CardDetailActivity")
        {
            FirestoreClass().getBoardDetail(this, documentId   )
        }
        else {
            Log.e("onActivityResult()", "Member task canceled by users")}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        if (intent.hasExtra(Constants.DOCUMENT_ID))
        {
            documentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }
        FirestoreClass().getBoardDetail(this, documentId)



    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.action_member -> {
                val intent = Intent(this@TaskListActivity, MembersActivity::class.java)
                callingActivity = "MembersActivity"
                intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
                startupdateActivityAndGetResult.launch(intent)
                return true
            }


            R.id.action_member2 -> {
                deleteBoardDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun deleteBoardDialog()
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Board")
        builder.setMessage("Are you sure you want to delete Board")
        builder.setPositiveButton("Yes")
        {
                dialog, which ->
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().deleteBoard(this, documentId)
        }
        builder.setNegativeButton("No")
        {
                dialog, which ->
            dialog.dismiss()
        }
        builder.show()

    }



    fun successfullyDeleteBoard()
    {
        hideProgressDialog()
        setResult(RESULT_OK)
        Toast.makeText(this@TaskListActivity, "Board has been successfully deleted", Toast.LENGTH_LONG).show()
        finish()
    }


    fun cardDetail(taskListPosition : Int, cardPosition : Int)
    {
        callingActivity = "CardDetailActivity"
        val intent = Intent(this@TaskListActivity, CardDetailActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION, taskListPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION, cardPosition)
        intent.putExtra(Constants.BOARD_MEMBER_LIST, memberArr)
        startupdateActivityAndGetResult.launch(intent)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_members, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun setUpActionBar (title : String)
    {
        val toolBar = findViewById<Toolbar>(R.id.toolbar_task_list_activity)
        setSupportActionBar(toolBar)
        toolBar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24dp)
        toolBar.setNavigationOnClickListener {
            onBackPressed()
        }
        toolBar.title = title
    }

    fun boardDetails(board: Board) {

        val addedTask = Task(resources.getString(R.string.add_list))
        board.taskList.add(addedTask)
        mBoardDetails = board
        FirestoreClass().getAssignedMemberList(this, mBoardDetails?.assignedTo!!)

    }

    fun updateTaskList(position : Int, listName : String, model : Task)
    {
        showProgressDialog(resources.getString(R.string.please_wait))

        val task = Task(listName, model.createdBy)
        mBoardDetails?.taskList!![position] = task
        FirestoreClass().addUpdateTaskList(this, mBoardDetails!!)

    }

    fun deleteTaskList(position : Int)
    {
        showProgressDialog(resources.getString(R.string.please_wait))
        mBoardDetails?.taskList!!.removeAt(position)
        mBoardDetails?.taskList!!.removeAt(mBoardDetails?.taskList!!.size - 1)
        FirestoreClass().addUpdateTaskList(this, mBoardDetails!!)
    }


    fun addCardToTaskList(position: Int, cardName : String)
    {
        showProgressDialog(resources.getString(R.string.please_wait))
        val assignedToList : ArrayList<String> = ArrayList()
        val card = Card(cardName, getCurrentUserId(), assignedToList)
        mBoardDetails?.taskList!![position].cards.add(card)
        mBoardDetails?.taskList!!.removeAt(mBoardDetails?.taskList!!.size - 1)
        FirestoreClass().addUpdateTaskList(this, mBoardDetails!!)
    }

    fun addUpdateTaskListSuccess ()
    {
        hideProgressDialog()
        Toast.makeText(this@TaskListActivity, "Tasklist addded success", Toast.LENGTH_LONG).show()
        FirestoreClass().getBoardDetail(this, mBoardDetails?.documentId!!)
    }


    fun createTaskList(taskListName : String)
    {
        showProgressDialog(resources.getString(R.string.please_wait))
        val task = Task(taskListName, getCurrentUserId())
        mBoardDetails?.taskList!!.add(0, task)
        mBoardDetails?.taskList!!.removeAt(mBoardDetails?.taskList!!.size - 1)
        FirestoreClass().addUpdateTaskList(this, mBoardDetails!!)
    }

    fun getMemberDetail(members : ArrayList<User>)
    {
        memberArr = members
        setUpActionBar(mBoardDetails!!.name)
        val taskAdapter = TaskListItemAdapter(this, mBoardDetails!!.taskList)
        binding?.rvTaskList!!.layoutManager = LinearLayoutManager(this@TaskListActivity, LinearLayoutManager.HORIZONTAL, false)
        binding?.rvTaskList!!.adapter = taskAdapter

        binding?.rvTaskList!!.setHasFixedSize(true)

    }
}