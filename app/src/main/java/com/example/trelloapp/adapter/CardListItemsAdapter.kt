package com.example.trelloapp.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trelloapp.activities.TaskListActivity
import com.example.trelloapp.databinding.ItemCardBinding
import com.example.trelloapp.models.Card
import com.example.trelloapp.models.SelectedMember

class CardListItemsAdapter (private val context : Context, private val items : ArrayList<Card>)
    :RecyclerView.Adapter<CardListItemsAdapter.ViewHolder> ()

{

    private var onClickListener : OnClickListener? = null

    class ViewHolder(binding : ItemCardBinding) : RecyclerView.ViewHolder(binding.root)
    {
        val tv_card_name = binding.tvCardName
        val rv_card_selected_members_list = binding.rvCardSelectedMembersList
        val card_item = binding.cardItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent , false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tv_card_name.text = item.name
        if (item.labelColor.isNotEmpty()) {
            holder.card_item.setBackgroundColor(Color.parseColor(item.labelColor))

        }

        holder.card_item.setOnClickListener {
            onClickListener!!.onClick(position, item)
        }

        if ((context as TaskListActivity).memberArr.size > 0)
        {
            val selectedMemberList : ArrayList<SelectedMember> = ArrayList()
            for (i in context.memberArr)
            {
                for (j in item.assignedTo)
                {
                    if (i.id == j) {selectedMemberList.add(SelectedMember(i.name, i.image))}
                }
            }
            if (selectedMemberList.size > 0)
            {
                holder.rv_card_selected_members_list.visibility = View.VISIBLE
                holder.rv_card_selected_members_list.layoutManager = GridLayoutManager(context, 4)
                val memberAdapter = CardMemberListItemsAdapter(context, selectedMemberList, false)
                holder.rv_card_selected_members_list.adapter = memberAdapter
            }
            else
            {
                holder.rv_card_selected_members_list.visibility = View.GONE
            }
        }




    }




    override fun getItemCount(): Int {
        return items.size
    }

    fun setOnClickLIstener (onClickListener: OnClickListener)
    {
        this.onClickListener = onClickListener
    }

    interface OnClickListener
    {
        fun onClick(position : Int, model : Card)
    }




}