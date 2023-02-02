package com.example.trelloapp.adapter

import android.content.ClipData.Item
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trelloapp.R
import com.example.trelloapp.activities.CardDetailActivity
import com.example.trelloapp.activities.TaskListActivity
import com.example.trelloapp.databinding.ItemBoardBinding
import com.example.trelloapp.databinding.ItemTaskBinding
import com.example.trelloapp.models.Card
import com.example.trelloapp.models.Task
import com.example.trelloapp.utils.Constants
import kotlin.contracts.contract

open class TaskListItemAdapter (private val context: Context, private val items : ArrayList<Task>)
    :RecyclerView.Adapter<TaskListItemAdapter.ViewHolder> ()

{
    class ViewHolder (binding : ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)
    {
        val tv_add_task_list = binding.tvAddTaskList
        val cv_add_task_list_name = binding.cvAddTaskListName
        val cv_add_card = binding.cvAddCard
        val ib_close_list_name = binding.ibCloseListName
        val et_task_list_name = binding.etTaskListName
        val ib_done_list_name = binding.ibDoneListName
        val ll_task_item = binding.llTaskItem
        val ll_title_view = binding.llTitleView
        val tv_task_list_title = binding.tvTaskListTitle
        val ib_edit_list_name = binding.ibEditListName
        val ib_delete_list = binding.ibDeleteList
        val cv_edit_task_list_name = binding.cvEditTaskListName
        val ib_close_editable_view = binding.ibCloseEditableView
        val et_edit_task_list_name = binding.etEditTaskListName
        val ib_done_edit_list_name = binding.ibDoneEditListName
        val rv_card_list = binding.rvCardList
        val ib_close_card_name = binding.ibCloseCardName
        val et_card_name = binding.etCardName
        val ib_done_card_name = binding.ibDoneCardName
        val tv_add_card = binding.tvAddCard
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        val view = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val layoutParam = LinearLayout.LayoutParams((0.7 * parent.width).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)

        layoutParam.setMargins((15.toDp()).toPx(), 0, (40.toDp()).toPx(), 0)

        view.root.layoutParams = layoutParam

        return ViewHolder(view)
    }




    /**
     * A function to get the density pixel from pixels
     */
    private fun Int.toDp() : Int =
        (this / Resources.getSystem().displayMetrics.density).toInt()


    /**
     * A function to get the pixel from density pixel
     */
    private fun Int.toPx() : Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        if (position == items.size - 1)
        {
            holder.tv_add_task_list.visibility = View.VISIBLE
            holder.ll_task_item.visibility = View.GONE
        }
        else
        {
            holder.tv_add_task_list.visibility = View.GONE
            holder.ll_task_item.visibility = View.VISIBLE
        }
        holder.tv_task_list_title.text = item.title

        holder.tv_add_task_list.setOnClickListener {
            holder.cv_add_task_list_name.visibility = View.VISIBLE
            holder.tv_add_task_list.visibility = View.GONE
        }

        holder.ib_close_list_name.setOnClickListener {
            holder.ll_title_view.visibility = View.VISIBLE
            holder.cv_add_task_list_name.visibility = View.GONE
        }

        holder.ib_done_list_name.setOnClickListener {
            val listName = holder.et_task_list_name.text.toString()

            if (listName.isNotEmpty())
            {
                if (context is TaskListActivity)
                {
                    context.createTaskList(listName)
                }
                else{
                    Toast.makeText(context, "Please enter list name", Toast.LENGTH_LONG).show()
                }
            }
        }

        holder.ib_done_edit_list_name.setOnClickListener {
            val listName = holder.et_edit_task_list_name.toString()

            if (listName.isNotEmpty()) {
                if (context is TaskListActivity) {
                    context.updateTaskList(position, listName, item)
                }
            }
            else
            {
                Toast.makeText(context, "Please enter the list name", Toast.LENGTH_LONG).show()
            }
        }

        holder.ib_edit_list_name.setOnClickListener {
            holder.cv_add_task_list_name.visibility = View.VISIBLE
            val edit_title = item.title
            holder.et_edit_task_list_name.setText(edit_title)
            holder.ll_title_view.visibility = View.GONE
        }

        holder.ib_close_editable_view.setOnClickListener {
            holder.cv_edit_task_list_name.visibility = View.GONE
            holder.ll_title_view.visibility = View.VISIBLE
        }

        holder.ib_delete_list.setOnClickListener {
            alertDialogForDeleteList(position, item.title)
        }

        holder.tv_add_card.setOnClickListener {
            holder.tv_add_card.visibility = View.GONE
            holder.cv_add_card.visibility = View.VISIBLE
        }
        holder.ib_close_card_name.setOnClickListener {
            holder.cv_add_card.visibility = View.GONE
            holder.tv_add_card.visibility = View.VISIBLE
        }

        holder.ib_done_card_name.setOnClickListener {
            val cardName = holder.et_card_name.text.toString()
            if (cardName.isNotEmpty())
            {
                if (context is TaskListActivity) {
                    context.addCardToTaskList(position, cardName)}
            }
        }
        val cardAdapter = CardListItemsAdapter(context, item.cards)
        holder.rv_card_list.adapter = cardAdapter
        holder.rv_card_list.layoutManager = LinearLayoutManager(context)
        holder.rv_card_list.setHasFixedSize(true)
        cardAdapter.setOnClickLIstener(object :
            CardListItemsAdapter.OnClickListener{
            override fun onClick(cardPosition: Int, model: Card) {
                if (context is TaskListActivity)
                {
                    context.cardDetail(position, cardPosition)
                }
            }

            }
        )

    }

    override fun getItemCount(): Int {
        return items.size
    }


    private fun alertDialogForDeleteList (position: Int, title: String)
    {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to delete the task")
        builder.setIcon(R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes"){
                dialogInterface, which ->
                    dialogInterface.dismiss()
                    if (context is TaskListActivity)
                    {
                        Toast.makeText(context, "delete task", Toast.LENGTH_LONG).show()
                        (context as TaskListActivity).deleteTaskList(position)
                    }
                }
            builder.setNegativeButton("No")
            { dialogInterface, id ->
                dialogInterface.dismiss()
            }
        builder.setCancelable(false)
        builder.show()
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
    }



