package com.example.trelloapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trelloapp.R
import com.example.trelloapp.databinding.ItemMemberBinding
import com.example.trelloapp.models.User
import com.example.trelloapp.utils.Constants

class MemberListItemAdapter (private val context : Context, private val items: ArrayList<User>)
    : RecyclerView.Adapter<MemberListItemAdapter.ViewHolder>()
{
    private var onClickListener : OnClickListener? = null
        class ViewHolder(binding: ItemMemberBinding) : RecyclerView.ViewHolder(binding.root)
        {
            val iv_member_image = binding.ivMemberImage
            val tv_member_name = binding.tvMemberName
            val tv_member_email = binding.tvMemberEmail
            val iv_selected_member = binding.ivSelectedMember
            val item_member = binding.memberItem
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        Glide
            .with(context)
            .load(item.image)
            .centerCrop()
            .placeholder(R.drawable.ic_board_place_holder)
            .into(holder.iv_member_image)

        holder.tv_member_name.text = item.name
        holder.tv_member_email.text = item.email


        if (item.selected) {holder.iv_selected_member.visibility = View.VISIBLE}
        else {holder.iv_selected_member.visibility = View.GONE}

        holder.item_member.setOnClickListener {
            if (onClickListener != null) {
                if (item.selected) {onClickListener!!.onClick(position, item, Constants.UN_SELECT)}
                else {onClickListener!!.onClick(position, item, Constants.SELECT)}
            }
        }
    }


    fun setOnClickListener (onClickListener : OnClickListener)
    {
        this.onClickListener = onClickListener
    }


    interface OnClickListener
    {
        fun onClick(position: Int, user: User, action: String)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}