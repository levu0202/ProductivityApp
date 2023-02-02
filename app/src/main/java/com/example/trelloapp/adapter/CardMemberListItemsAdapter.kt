package com.example.trelloapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trelloapp.R
import com.example.trelloapp.databinding.ItemCardSelectedMemberBinding
import com.example.trelloapp.models.SelectedMember
import com.example.trelloapp.models.User

class CardMemberListItemsAdapter (private val context : Context, private val items : ArrayList<SelectedMember>, private val assignedMember : Boolean)
    : RecyclerView.Adapter<CardMemberListItemsAdapter.ViewHolder>()
{

    private var onClickListener : OnClickListener? = null

      class ViewHolder(binding : ItemCardSelectedMemberBinding) : RecyclerView.ViewHolder(binding.root)
      {
          val iv_selected_member_image = binding.ivSelectedMemberImage
          val iv_add_member = binding.ivAddMember
          val item_card_member = binding.itemCardMember
      }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemCardSelectedMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        if (position == items.size - 1 && assignedMember)
        {
            holder.iv_selected_member_image.visibility = View.GONE
            holder.iv_add_member.visibility = View.VISIBLE
        }
        else
        {
            holder.iv_selected_member_image.visibility = View.VISIBLE
            holder.iv_add_member.visibility = View.GONE
            Glide
                .with(context)
                .load(item.image)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(holder.iv_selected_member_image)
        }

        holder.item_card_member.setOnClickListener{
            if (onClickListener != null) {onClickListener!!.onClick()}
        }

    }

    fun setOnClickListener(onClickListener : OnClickListener)
    {
        this.onClickListener = onClickListener
    }

    interface OnClickListener
    {
        fun onClick()
    }



    override fun getItemCount(): Int {
        return items.size
    }
}