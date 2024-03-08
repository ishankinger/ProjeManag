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
import com.example.projemanaj.models.Card
import com.example.projemanaj.models.User

open class MemberListItemAdapter(
    private val context: Context,
    private var list: ArrayList<User>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null


    // Inflates the item views which is designed in xml layout file create a new
    // {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_members,
                parent,
                false
            )
        )
    }

    // Binds each item in the ArrayList to a view
    // Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent an item.
    // This new ViewHolder should be constructed with a new View that can represent the items
    // of the given type. You can either create a new View manually or inflate it from an XML layout file.
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder) {

            holder.itemView.findViewById<TextView>(R.id.tv_member_name).text = model.name

            holder.itemView.findViewById<TextView>(R.id.tv_member_email).text = model.email

            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.itemView.findViewById(R.id.iv_member_image))
        }
    }


    // Gets the number of items in the list
    override fun getItemCount(): Int {
        return list.size
    }


    // A function for OnClickListener where the Interface is the expected parameter..
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }


    // An interface for onclick items.
    interface OnClickListener {
        fun onClick(position: Int, card: Card)
    }


    // A ViewHolder describes an item view and metadata about its place within the RecyclerView.
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}