package com.example.projemanaj.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projemanaj.R
import com.example.projemanaj.models.SelectedMembers

open class CardMembersListItemAdapter(private val context : Context,
                                      private val assignedMembers : Boolean,
                                      private val list : ArrayList<SelectedMembers>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var  onClickListener : OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder (LayoutInflater.from(context).inflate(
            R.layout.item_card_selected_member,
            parent,
            false
        ))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            if(position == list.size-1 && assignedMembers){
                holder.itemView.findViewById<ImageView>(R.id.iv_add_member).visibility = View.VISIBLE
                holder.itemView.findViewById<ImageView>(R.id.iv_selected_member_image).visibility = View.GONE
            }
            else{
                holder.itemView.findViewById<ImageView>(R.id.iv_add_member).visibility = View.GONE
                holder.itemView.findViewById<ImageView>(R.id.iv_selected_member_image).visibility = View.VISIBLE

                Glide
                    .with(context)
                    .load(model.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(holder.itemView.findViewById(R.id.iv_selected_member_image))

            }
            holder.itemView.setOnClickListener {
                if(onClickListener != null){
                    onClickListener!!.onClick()
                }
            }
        }
    }

    interface OnClickListener{
        fun onClick()
    }

    fun setOnClickListener(onClickListener: CardMembersListItemAdapter.OnClickListener) {
        this.onClickListener = onClickListener
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

}