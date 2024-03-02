package com.example.projemanaj.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projemanaj.R
import com.example.projemanaj.models.Board

// Making an adapter for the boards which we have to show on the main screen
open class BoardItemAdapter(private val context : Context, private var list : ArrayList<Board>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

//        private var onClickListener: View.OnClickListener? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return MyViewHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.item_board, parent, false)
            )
        }

        override fun getItemCount(): Int {
            return list.size
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val model = list[position]
            if (holder is MyViewHolder) {
                Glide
                    .with(context)
                    .load(model.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_board_place_holder)
                    .into(holder.itemView.findViewById(R.id.iv_board_image))

                holder.itemView.findViewById<TextView>(R.id.tv_name).text = model.name
                holder.itemView.findViewById<TextView>(R.id.tv_created_by).text = "Created by : ${model.createdBy}"

//                holder.itemView.setOnClickListener {
//                    if (onClickListener != null) {
//                        onClickListener!!.onClick(position, model)
//                    }
//                }
            }
        }
//        fun setOnClickListener(onClickListener: OnClickListener) {
//            this.onClickListener = onClickListener
//        }
//
//        interface OnClickListener {
//            fun onClick(position: Int, model: Board)
//        }


        private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    }