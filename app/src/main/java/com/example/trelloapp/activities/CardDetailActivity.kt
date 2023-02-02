package com.example.trelloapp.activities

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trelloapp.R
import com.example.trelloapp.adapter.CardListItemsAdapter
import com.example.trelloapp.adapter.CardMemberListItemsAdapter
import com.example.trelloapp.databinding.ActivityCardDetailBinding
import com.example.trelloapp.dialog.CardColorDialog
import com.example.trelloapp.dialog.MemberListDialog
import com.example.trelloapp.firebase.FirestoreClass
import com.example.trelloapp.models.*
import com.example.trelloapp.utils.Constants
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CardDetailActivity : BaseActivity() {

    private var binding : ActivityCardDetailBinding? = null

    private lateinit var mBoardDetails : Board
    private  var cardItemPosition : Int = -1
    private  var taskListItemPosition : Int = -1
    private lateinit var memberArr : ArrayList<User>
    private var themeColor : String = ""
    private var mSelectedDueDateMilliSecond : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCardDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        getIntentInfo()
        setContentView(binding?.root)
        binding?.etNameCardDetails!!.setText(mBoardDetails.taskList[taskListItemPosition].cards[cardItemPosition].name)
        setUpActionBar()
        setUpAssignedMemberInfo()
        binding?.tvSelectMembers!!.setOnClickListener {memberListDialog()}
        binding?.tvSelectLabelColor!!.setOnClickListener { showColorDialog() }
        binding?.btnUpdateCardDetails!!.setOnClickListener {
            showProgressDialog(resources.getString(R.string.please_wait))
            updateCardInfo()
        }


        if (mSelectedDueDateMilliSecond > 0)
        {
            mSelectedDueDateMilliSecond = mBoardDetails.taskList[taskListItemPosition].cards[cardItemPosition].dueDate
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val theDate = sdf.format(Date(mSelectedDueDateMilliSecond))
            binding?.tvSelectDueDate!!.text = theDate
        }

        binding?.tvSelectDueDate!!.setOnClickListener {
            showDatePicker()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId)
        {
            R.id.action_member ->
            {
                deleteCardDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_delete_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun setUpActionBar()
    {
        val toolBar = findViewById<Toolbar>(R.id.toolbar_card_details_activity)
        toolBar.setTitle(mBoardDetails.taskList[taskListItemPosition].cards[cardItemPosition].name)
        themeColor = mBoardDetails.taskList[taskListItemPosition].cards[cardItemPosition].labelColor
        if (themeColor.isNotEmpty())
        {
            toolBar.setBackgroundColor(Color.parseColor(themeColor))
        }
        else
        {
            themeColor = "#E9967A"
            toolBar.setBackgroundColor(Color.parseColor(themeColor))
        }
        setSupportActionBar(toolBar)
        toolBar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24dp)
        toolBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun showDatePicker()
    {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month , day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)

            val selectedDate = "$day/${month + 1}/$year"
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val theDate = sdf.parse(selectedDate)
            mSelectedDueDateMilliSecond = theDate!!.time
            binding?.tvSelectDueDate!!.text = sdf.format(Date(mSelectedDueDateMilliSecond))

                                                                            }, year, month, day)
        dpd.show()
    }


    private fun showColorDialog()
    {
        val colorArr : ArrayList<String> = ArrayList()
        colorArr.add("#E9967A")
        colorArr.add("#0C90F1")
        colorArr.add("#F72400")
        colorArr.add("#7A8089")
        colorArr.add("#D57C1D")
        colorArr.add("#770000")
        colorArr.add("#0022F8")
        val colorDialog = object : CardColorDialog(this, colorArr, "Select Color", themeColor)
        {
            override fun colorSelected(colorStr: String) {
                mBoardDetails.taskList[taskListItemPosition].cards[cardItemPosition].labelColor = colorStr
                setUpActionBar()
            }
        }
        colorDialog.show()

    }

    private fun updateCardInfo()
    {
        val cardName = binding?.etNameCardDetails!!.text.toString()
        if (cardName.isNotEmpty())
        {

            val card = Card(cardName, mBoardDetails.createdBy,
                mBoardDetails.taskList[taskListItemPosition].cards[cardItemPosition].assignedTo,
                themeColor, mSelectedDueDateMilliSecond)

            mBoardDetails.taskList[taskListItemPosition].cards[cardItemPosition] = card
            mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)
            FirestoreClass().addUpdateTaskList(this@CardDetailActivity, mBoardDetails)
        }
        else
        {
            Toast.makeText(this@CardDetailActivity, "Please enter card name", Toast.LENGTH_LONG).show()
        }

    }

    private fun setUpAssignedMemberInfo()
    {

        val assignedMemberList : ArrayList<SelectedMember> = ArrayList()

        for (i in memberArr)
        {
            for (j in mBoardDetails.taskList[taskListItemPosition].cards[cardItemPosition].assignedTo)
            {
                if (i.id == j) {
                    assignedMemberList.add(SelectedMember(i.id, i.image))
                }
            }
        }

        if (assignedMemberList.size > 0)
        {
            assignedMemberList.add(SelectedMember("", ""))
            binding?.tvSelectMembers!!.visibility = View.GONE
            binding?.rvSelectedMembersList!!.visibility = View.VISIBLE
            val assignedMemberAdapter = CardMemberListItemsAdapter(this, assignedMemberList, true)
            binding?.rvSelectedMembersList!!.adapter = assignedMemberAdapter
            binding?.rvSelectedMembersList!!.layoutManager = GridLayoutManager(this@CardDetailActivity, 6)
            assignedMemberAdapter.setOnClickListener(object :
                CardMemberListItemsAdapter.OnClickListener{
                override fun onClick() {
                    memberListDialog()
                }
                }
            )
        }
        else
        {
            binding?.tvSelectMembers!!.visibility = View.VISIBLE
            binding?.rvSelectedMembersList!!.visibility = View.GONE
        }

    }

    private fun getIntentInfo()
    {
        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
        }
        if (intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)) {
            taskListItemPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)) {
            cardItemPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.BOARD_MEMBER_LIST)) {
            memberArr = intent.getParcelableArrayListExtra<User>(Constants.BOARD_MEMBER_LIST)!!
        }
    }

    private fun memberListDialog()
    {
        val cardAssignedMemberList = mBoardDetails.taskList[taskListItemPosition].cards[cardItemPosition].assignedTo

        if (cardAssignedMemberList.size > 0)
        {

            for (member in memberArr)
            {
                for (assignedMemberId in cardAssignedMemberList)
                {
                    if (member.id == assignedMemberId) {member.selected = true}
                }
            }

        }
        else { for (member in memberArr) {member.selected = false} }

        val listDialog = object : MemberListDialog(
            this, memberArr, resources.getString(R.string.select_members)
        )
        {
            override fun onItemSelected(user: User, action: String) {
                if (action == Constants.SELECT){
                    if (!mBoardDetails.taskList[taskListItemPosition].cards[cardItemPosition].assignedTo.contains(user.id))
                    {mBoardDetails.taskList[taskListItemPosition].cards[cardItemPosition].assignedTo.add(user.id)}
                }
                else {mBoardDetails.taskList[taskListItemPosition].cards[cardItemPosition].assignedTo.remove(user.id)
                    for (member in memberArr)
                    {
                        if (member.id == user.id) {member.selected = false}
                    }

                }
                setUpAssignedMemberInfo()
            }
        }
        listDialog.show()
    }


    private fun deleteCardDialog()
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.delete_card))
        builder.setMessage("Are you sure you want to delete card")
        builder.setPositiveButton("Yes")
        {
            dialog, which ->
            showProgressDialog(resources.getString(R.string.please_wait))
            deleteCard()
        }
        builder.setNegativeButton("No")
        {
            dialog, which ->
            dialog.dismiss()
        }
        builder.show()

    }


    private fun deleteCard()
    {
        mBoardDetails.taskList[taskListItemPosition].cards.removeAt(cardItemPosition)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)
        FirestoreClass().addUpdateTaskList(this@CardDetailActivity, mBoardDetails)
    }


    fun cardUpdatedSuccess()
    {
        hideProgressDialog()
        setResult(RESULT_OK)
        Toast.makeText(this@CardDetailActivity, "Card has been updated successfully", Toast.LENGTH_LONG).show()
        finish()
    }









}