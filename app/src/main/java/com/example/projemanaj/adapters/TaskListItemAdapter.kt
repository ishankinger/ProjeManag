package com.example.projemanaj.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
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

    @SuppressLint("CutPasteId")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // getting the task according to the position
        val model = list[position]

        if(holder is MyViewHolder){

            // setting the task list and add list text view
            if(position == list.size-1){
                // last position should show add list text view so to add the list so it' visibility is Visible
                holder.itemView.findViewById<MaterialTextView>(R.id.tv_add_task_list).visibility = View.VISIBLE
                holder.itemView.findViewById<CardView>(R.id.cv_add_task_list_name).visibility = View.GONE
                // other than that all other things will be Gone
                holder.itemView.findViewById<LinearLayout>(R.id.ll_task_item).visibility = View.GONE
            }
            else{
                // if we are not on last position then reverse will occur and if cv_add_task list name is opened by clicking
                // on the add list tv then that will also be gone
                holder.itemView.findViewById<MaterialTextView>(R.id.tv_add_task_list).visibility = View.GONE
                holder.itemView.findViewById<CardView>(R.id.cv_add_task_list_name).visibility = View.GONE
                holder.itemView.findViewById<LinearLayout>(R.id.ll_task_item).visibility = View.VISIBLE
            }

            // tasks title are allotted
            holder.itemView.findViewById<TextView>(R.id.tv_task_list_title).text = model.title

            // click listener for add list text view
            holder.itemView.findViewById<MaterialTextView>(R.id.tv_add_task_list).setOnClickListener{
                // text view of add list gone
                holder.itemView.findViewById<MaterialTextView>(R.id.tv_add_task_list).visibility = View.GONE
                // and edit text view card to be shown
                holder.itemView.findViewById<CardView>(R.id.cv_add_task_list_name).visibility = View.VISIBLE
            }

            // in add list for edit list name two buttons one is close
            holder.itemView.findViewById<ImageButton>(R.id.ib_close_list_name).setOnClickListener{
                // 'add list' text visible
                holder.itemView.findViewById<MaterialTextView>(R.id.tv_add_task_list).visibility = View.VISIBLE
                // edit card view gone
                holder.itemView.findViewById<CardView>(R.id.cv_add_task_list_name).visibility = View.GONE
            }

            // click listener for done button means adding this list name to the tasks
            holder.itemView.findViewById<ImageButton>(R.id.ib_done_list_name).setOnClickListener{
                // getting the list name
                val listName = holder.itemView.findViewById<EditText>(R.id.et_task_list_name).text.toString()
                // if list name is not empty then call task list activity function to further add this list using fire store class
                if(listName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.createTaskList(listName)
                    }
                }
                // else we will give a toast saying enter the name
                else{
                    Toast.makeText(context,"Please enter List Name", Toast.LENGTH_SHORT).show()
                }
            }

            // click listener for the image button of edit the title name
            holder.itemView.findViewById<ImageButton>(R.id.ib_edit_list_name).setOnClickListener{
                // initially setting the data of edit text card's text as title of task
                holder.itemView.findViewById<EditText>(R.id.et_edit_task_list_name).setText(model.title)
                // title view visibility gone
                holder.itemView.findViewById<LinearLayout>(R.id.ll_title_view).visibility = View.GONE
                // and edit text card visibility on
                holder.itemView.findViewById<CardView>(R.id.cv_edit_task_list_name).visibility = View.VISIBLE
            }

            // inside the card view the close button click listener
            holder.itemView.findViewById<ImageButton>(R.id.ib_close_editable_view).setOnClickListener{
                // title view visibility on
                holder.itemView.findViewById<LinearLayout>(R.id.ll_title_view).visibility = View.VISIBLE
                // card view of edit text gone
                holder.itemView.findViewById<CardView>(R.id.cv_edit_task_list_name).visibility = View.GONE
            }

            // for done button means updating the task title
            holder.itemView.findViewById<ImageButton>(R.id.ib_done_edit_list_name).setOnClickListener{
                // getting the new list name
                val listName = holder.itemView.findViewById<EditText>(R.id.et_edit_task_list_name).text.toString()
                // if not empty then call for update task list in the task list activity
                if(listName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.updateTaskList(position,listName,model)
                    }
                }
                // else make toast of please enter the name
                else{
                    Toast.makeText(context,"Please enter List Name", Toast.LENGTH_SHORT).show()
                }
            }

            // click listener for deleting the list
            holder.itemView.findViewById<ImageButton>(R.id.ib_delete_list).setOnClickListener{
                // using alert dialog box to be verified
                alertDialogForDeleteList(position,model.title)
            }

            // click listener for the add card text
            holder.itemView.findViewById<TextView>(R.id.tv_add_card).setOnClickListener{
                // add card text view visibility gone
                holder.itemView.findViewById<TextView>(R.id.tv_add_card).visibility = View.GONE
                // and edit text visible
                holder.itemView.findViewById<CardView>(R.id.cv_add_card).visibility = View.VISIBLE
            }

            // click listener for the close button in the edit text visible
            holder.itemView.findViewById<ImageButton>(R.id.ib_close_card_name).setOnClickListener{
                // add card visible
                holder.itemView.findViewById<TextView>(R.id.tv_add_card).visibility = View.VISIBLE
                // edit card visibility gone
                holder.itemView.findViewById<CardView>(R.id.cv_add_card).visibility = View.GONE
            }

            // click listener for done button in card view for adding the card
            holder.itemView.findViewById<ImageButton>(R.id.ib_done_card_name).setOnClickListener {
                // get name of the card
                val cardName = holder.itemView.findViewById<EditText>(R.id.et_card_name).text.toString()
                // if card name is not empty add to the task list using the addCardToTaskList function
                if(cardName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.addCardToTaskList(position,cardName)
                    }
                }
                // else show toast for filling the card name to add it
                else{
                    Toast.makeText(context,"Please enter Card Name", Toast.LENGTH_SHORT).show()
                }
            }

            // assigning the layout manager of the card list
            holder.itemView.findViewById<RecyclerView>(R.id.rv_card_list).layoutManager = LinearLayoutManager(context)
            holder.itemView.findViewById<RecyclerView>(R.id.rv_card_list).setHasFixedSize(true)

            // connecting the adapter of the card lists with our card list adapter
            val adapter = CardListItemsAdapter(context,model.cards)
            holder.itemView.findViewById<RecyclerView>(R.id.rv_card_list).adapter = adapter
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