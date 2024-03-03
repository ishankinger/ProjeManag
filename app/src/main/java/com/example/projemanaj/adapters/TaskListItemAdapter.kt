package com.example.projemanaj.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.projemanaj.R
import com.example.projemanaj.activities.TaskListActivity
import com.example.projemanaj.models.Task
import com.google.android.material.textview.MaterialTextView
import io.grpc.internal.SharedResourceHolder.Resource

open class TaskListItemAdapter(private val context : Context, private var list : ArrayList<Task>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_lists,parent,false)
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width*0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins( (15.toDp()).toPx() ,0, (40.toDp()).toPx(), 0)
        view.layoutParams = layoutParams
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            if(position == list.size-1){
                // this part is for adding the list
                holder.itemView.findViewById<MaterialTextView>(R.id.tv_add_task_list).visibility = View.VISIBLE
                // this part shows other lists
                holder.itemView.findViewById<LinearLayout>(R.id.ll_task_item).visibility = View.GONE
            }
            else{
                holder.itemView.findViewById<MaterialTextView>(R.id.tv_add_task_list).visibility = View.GONE
                holder.itemView.findViewById<LinearLayout>(R.id.ll_task_item).visibility = View.VISIBLE
            }

            holder.itemView.findViewById<TextView>(R.id.tv_task_list_title).text = model.title

            holder.itemView.findViewById<MaterialTextView>(R.id.tv_add_task_list).setOnClickListener{
                holder.itemView.findViewById<MaterialTextView>(R.id.tv_add_task_list).visibility = View.GONE
                holder.itemView.findViewById<CardView>(R.id.cv_add_task_list_name).visibility = View.VISIBLE
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_close_list_name).setOnClickListener{
                holder.itemView.findViewById<MaterialTextView>(R.id.tv_add_task_list).visibility = View.VISIBLE
                holder.itemView.findViewById<CardView>(R.id.cv_add_task_list_name).visibility = View.GONE
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_done_list_name).setOnClickListener{

                val listName = holder.itemView.findViewById<EditText>(R.id.et_task_list_name).text.toString()
                if(listName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.createTaskList(listName)
                    }
                }
                else{
                    Toast.makeText(context,"Please enter List Name", Toast.LENGTH_SHORT).show()
                }
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_edit_list_name).setOnClickListener{
                holder.itemView.findViewById<EditText>(R.id.et_edit_task_list_name).setText(model.title)
                holder.itemView.findViewById<LinearLayout>(R.id.ll_title_view).visibility = View.GONE
                holder.itemView.findViewById<CardView>(R.id.cv_add_task_list_name).visibility = View.VISIBLE
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_close_editable_view).setOnClickListener{
                holder.itemView.findViewById<LinearLayout>(R.id.ll_title_view).visibility = View.VISIBLE
                holder.itemView.findViewById<CardView>(R.id.cv_edit_task_list_name).visibility = View.GONE
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_done_edit_list_name).setOnClickListener{
                val listName = holder.itemView.findViewById<EditText>(R.id.et_edit_task_list_name).text.toString()
                if(listName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.updateTaskList(position,listName,model)
                    }
                }
                else{
                    Toast.makeText(context,"Please enter List Name", Toast.LENGTH_SHORT).show()
                }
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_delete_list).setOnClickListener{
                alertDialogForDeleteList(position,model.title)
            }
        }
    }

    private fun alertDialogForDeleteList(position: Int, title: String) {
        val builder = AlertDialog.Builder(context)

        //set title for alert dialog
        builder.setTitle("Alert")

        //set message for alert dialog
        builder.setMessage("Are you sure you want to delete $title.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed

            if (context is TaskListActivity) {
                context.deleteTaskList(position)
            }
        }

        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }

        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()

        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }

    private fun Int.toDp() :
            Int = (this / Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx() :
            Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    class MyViewHolder(view : View) : RecyclerView.ViewHolder(view)
}