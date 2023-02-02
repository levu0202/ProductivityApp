package com.example.trelloapp.adapter

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trelloapp.R
import com.example.trelloapp.databinding.ItemBoardBinding
import com.example.trelloapp.models.Board

open class BoardItemsAdapter(private val items : ArrayList<Board>)
    : RecyclerView.Adapter<BoardItemsAdapter.ViewHolder>() {

    private var onClickListener : OnClickListener? = null


    class ViewHolder (binding : ItemBoardBinding) : RecyclerView.ViewHolder(binding.root)
    {
        val board_item = binding.boardItem
        val board_image = binding.ivBoardImage
        val board_name = binding.tvName
        val board_created_by = binding.tvCreatedBy
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemBoardBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val item = items[position]

        Glide
            .with(context)
            .load(item.image)
            .centerCrop()
            .placeholder(R.drawable.ic_board_place_holder)
            .into(holder.board_image)

        holder.board_name.text = item.name
        holder.board_created_by.text = "Created by : ${item.createdBy}"

        holder.board_item.setOnClickListener {
            if (onClickListener != null)
            {
                onClickListener!!.onClick(position, item)
            }
        }

    }

    fun setOnClickListener (onClickListener : OnClickListener)
    {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {fun onClick(position: Int, model: Board)}

    override fun getItemCount(): Int {
        return items.size
    }
}