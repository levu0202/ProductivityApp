package com.example.trelloapp.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trelloapp.R
import com.example.trelloapp.adapter.MemberListItemAdapter
import com.example.trelloapp.databinding.DialogListBinding
import com.example.trelloapp.models.User
import com.google.firebase.firestore.core.OnlineState
import com.google.firebase.firestore.core.View

abstract class MemberListDialog(
    context: Context,
    private var list : ArrayList<User>,
    private val title : String
) : Dialog(context) {

    private lateinit var dialogBinding : DialogListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialogBinding = DialogListBinding.inflate(layoutInflater)
        setContentView(dialogBinding.root)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView()


    }

    private fun setUpRecyclerView()
    {
        dialogBinding.tvTitle.text = title

        if (list.size > 0)
        {
            val adapter = MemberListItemAdapter(context, list)
            dialogBinding.rvList.layoutManager = LinearLayoutManager(context)
            dialogBinding.rvList.adapter = adapter
            adapter.setOnClickListener(object :
                MemberListItemAdapter.OnClickListener
            {
                override fun onClick(position: Int, user: User, action: String) {
                    dismiss()
                    onItemSelected(user, action)
                }
            }
            )

        }
    }

    protected abstract fun onItemSelected(user : User, action : String)


}