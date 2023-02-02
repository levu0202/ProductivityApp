package com.example.trelloapp.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.trelloapp.databinding.DialogListBinding
import com.example.trelloapp.databinding.ItemLabelColorBinding

open class LabelColorAdapter (private val context : Context, private val items : ArrayList<String>,
                                private val mSelectedColor : String)
    : RecyclerView.Adapter<LabelColorAdapter.ViewHolder>()
{

    private var onClickListener :OnClickListener? = null

    class ViewHolder (binding: ItemLabelColorBinding) : RecyclerView.ViewHolder(binding.root)
    {
        val view_main = binding.viewMain
        val iv_selected_color = binding.ivSelectedColor
        val item_color = binding.itemColor
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemLabelColorBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        if (item == mSelectedColor) {holder.iv_selected_color.visibility = View.VISIBLE}
        else {holder.iv_selected_color.visibility = View.GONE}
        holder.view_main.setBackgroundColor(Color.parseColor(item))

        holder.item_color.setOnClickListener {
            if (onClickListener != null) {onClickListener!!.onClick(item)}
        }


    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setOnClickListener(onClickListener : OnClickListener)
    {
        this.onClickListener = onClickListener
    }

    interface OnClickListener
    {
        fun onClick(colorStr : String)
    }


}