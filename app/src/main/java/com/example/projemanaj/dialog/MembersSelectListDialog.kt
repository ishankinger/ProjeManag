package com.example.projemanaj.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projemanaj.R
import com.example.projemanaj.adapters.LabelColorListItemAdapter
import com.example.projemanaj.adapters.MemberListItemAdapter
import com.example.projemanaj.models.User

abstract class MembersSelectListDialog
    (context : Context,
     private var list : ArrayList<User>,
     private val title : String = "")
    : Dialog(context){

    private var adapter : MemberListItemAdapter? = null

    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_list,null)

        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView(view)
    }

    @SuppressLint("CutPasteId")
    private fun setUpRecyclerView(view : View){

        view.findViewById<TextView>(R.id.tvTitle).text = title

        if(list.size > 0){
            view.findViewById<RecyclerView>(R.id.rvList).layoutManager = LinearLayoutManager(context)
            adapter = MemberListItemAdapter(context,list)
            view.findViewById<RecyclerView>(R.id.rvList).adapter = adapter

            adapter!!.setOnClickListener(object :
                MemberListItemAdapter.OnClickListener{
                override fun onClick(position: Int, user : User, action : String){
                    dismiss()
                    onItemSelected(user,action)
                }
            })
        }
    }

    protected abstract fun onItemSelected( user : User, action : String)

}