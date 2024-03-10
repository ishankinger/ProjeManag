package com.example.projemanaj.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projemanaj.R
import com.example.projemanaj.models.Card

open class CardListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Card>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null


    // Inflates the item views which is designed in xml layout file create a new
    // {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_cards,
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

            // if the labour color of the card is not empty then we will show the view with that color filled
            if(model.labelColor.isNotEmpty()){
                holder.itemView.findViewById<View>(R.id.view_label_color).visibility = View.VISIBLE
                holder.itemView.findViewById<View>(R.id.view_label_color)
                    .setBackgroundColor(Color.parseColor(model.labelColor))
            }

            // else it's visibility will be gone
            else{
                holder.itemView.findViewById<View>(R.id.view_label_color).visibility = View.GONE
            }

            holder.itemView.setOnClickListener{
                if(onClickListener != null){
                    onClickListener!!.onClick(position)
                }
            }
            holder.itemView.findViewById<TextView>(R.id.tv_card_name).text = model.name
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
        fun onClick(position: Int)
    }


    // A ViewHolder describes an item view and metadata about its place within the RecyclerView.
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
