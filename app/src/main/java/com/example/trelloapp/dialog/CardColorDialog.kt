package com.example.trelloapp.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.engine.Resource
import com.example.trelloapp.adapter.LabelColorAdapter
import com.example.trelloapp.databinding.DialogListBinding

abstract class CardColorDialog(
    context : Context,
    private var items : ArrayList<String>,
    private var title : String,
    private var selectedColor : String
) : Dialog(context) {
    private lateinit var colorDialogBinding : DialogListBinding

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
        colorDialogBinding = DialogListBinding.inflate(layoutInflater)
        setContentView(colorDialogBinding.root)
        setUpRecyclerView()
        setCanceledOnTouchOutside(true)
        setCancelable(true)
    }

    private fun setUpRecyclerView()
    {
        colorDialogBinding.tvTitle.text = title
        val colorAdapter = LabelColorAdapter(context, items, selectedColor)
        colorDialogBinding.rvList.adapter = colorAdapter
        colorDialogBinding.rvList.layoutManager = LinearLayoutManager(context)
        colorDialogBinding.rvList.setHasFixedSize(true)

        colorAdapter.setOnClickListener(object :
            LabelColorAdapter.OnClickListener
        {
            override fun onClick(colorStr: String) {
                dismiss()
                colorSelected(colorStr)
            }
        }

        )

    }

    protected abstract fun colorSelected (colorStr : String)


}