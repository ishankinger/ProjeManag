package com.example.projemanaj.activities

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.projemanaj.R
import com.example.projemanaj.dialog.LabelColorListDialog
import com.example.projemanaj.firebase.FirestoreClass
import com.example.projemanaj.models.Board
import com.example.projemanaj.models.Card
import com.example.projemanaj.models.Task
import com.example.projemanaj.utils.Constants

class CardsDetailActivity : BaseActivity() {

    private lateinit var mBoardDetails : Board
    private var mTaskListPosition = -1
    private var mCardPosition = -1
    private var mSelectedColor = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cards_detail)

        getIntentData()

        findViewById<EditText>(R.id.et_name_card_details).
        setText(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)

        findViewById<EditText>(R.id.et_name_card_details).
        setSelection(findViewById<EditText>(R.id.et_name_card_details).
        text.toString().length)

        setupActionBar()

        // initialise the variable with initial value of the color
        mSelectedColor = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].labelColor
        if(mSelectedColor.isNotEmpty()){
            setColor()
        }

        findViewById<Button>(R.id.btn_update_card_details).setOnClickListener {
            if(findViewById<EditText>(R.id.et_name_card_details).text.toString().isNotEmpty()){
                updateCardDetails()
            }
            else{
                Toast.makeText(this@CardsDetailActivity,"Please enter a card name", Toast.LENGTH_SHORT).show()
            }
        }

        // setting on click listener for button to select the color
        findViewById<TextView>(R.id.tv_select_label_color).setOnClickListener{
            labelColorListDialog()
        }
    }

    // function to set up the action bar
    private fun setupActionBar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar_card_details_activity)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_white__ios_24)
            actionBar.title = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getIntentData(){
        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }
        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)){
            mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION,-1)
        }
        if(intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)){
            mCardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION,-1)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) : Boolean{
        when(item.itemId){
            R.id.action_delete_card->{
                alertDialogForDeleteList(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun updateCardDetails(){

        // here first we have to remove the last task list that is 'add list' as when we will again go back to
        // the taskList Activity we will load the details by calling getBoardDetails function which will again add
        // the task list activity
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        val card = Card(
            findViewById<EditText>(R.id.et_name_card_details).text.toString(),
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].createdBy,
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo,
            mSelectedColor)

        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition] = card

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@CardsDetailActivity,mBoardDetails)

    }

    private fun deleteCard(){
        val cardList : ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cards

        cardList.removeAt(mCardPosition)

        // to get rid of add card element
        val taskList : ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)

        taskList[mTaskListPosition].cards = cardList

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@CardsDetailActivity,mBoardDetails)
    }

    private fun alertDialogForDeleteList(cardName : String) {
        val builder = AlertDialog.Builder(this@CardsDetailActivity)

        //set title for alert dialog
        builder.setTitle("Alert")

        //set message for alert dialog
        builder.setMessage("Are you sure you want to delete the card : ${cardName}.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
            deleteCard()
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

    // color list for the label color to be shown
    private fun colorsList() : ArrayList<String>{
        val colorsList : ArrayList<String> = ArrayList()
        colorsList.add("#CAF0F8")
        colorsList.add("#B1F2FF")
        colorsList.add("#8AECFF")
        colorsList.add("#63E5FF")
        colorsList.add("#90E0EF")
        colorsList.add("#00B7EB")
        colorsList.add("#00B4D8")
        colorsList.add("#0077B6")

        return colorsList
    }

    // function to set up the color
    private fun setColor(){
        findViewById<TextView>(R.id.tv_select_label_color).text = ""
        findViewById<TextView>(R.id.tv_select_label_color)
            .setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    // function to show the dialog of different colors
    private fun labelColorListDialog(){
        // getting the list of color from the color list function
        val colorList : ArrayList<String> = colorsList()

        // making a listDialog ( from label color list item adapter )
        val listDialog = object : LabelColorListDialog(
            this,
            colorList,
            resources.getString(R.string.str_select_label_color),
            mSelectedColor){

            // change mSelected Color to the color selected and then setColor function to change the color
                override fun onItemSelected(color: String) {
                    mSelectedColor = color
                    setColor()
                }
        }

        // showing the list dialog
        listDialog.show()
    }

}