package com.example.projemanaj.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projemanaj.R
import com.example.projemanaj.adapters.CardMembersListItemAdapter
import com.example.projemanaj.dialog.LabelColorListDialog
import com.example.projemanaj.dialog.MembersSelectListDialog
import com.example.projemanaj.firebase.FirestoreClass
import com.example.projemanaj.models.*
import com.example.projemanaj.utils.Constants

class CardsDetailActivity : BaseActivity() {

    // variable storing the board details
    private lateinit var mBoardDetails : Board

    // variable storing the task list position of list in which the card is present
    private var mTaskListPosition = -1

    // variable storing the card position in the particular list
    private var mCardPosition = -1

    // storing the selected color of the card
    private var mSelectedColor = ""

    // storing the user's assigned to the board
    private lateinit var mMembersDetailList : ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cards_detail)

        // call function to get intent data from task list activity
        getIntentData()

        // setting the name of the edit text as initial card name
        findViewById<EditText>(R.id.et_name_card_details).
        setText(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)

        // when we will click on edit text the cursor will move to the end
        findViewById<EditText>(R.id.et_name_card_details).
        setSelection(findViewById<EditText>(R.id.et_name_card_details).
        text.toString().length)

        // calling function to set up action bar
        setupActionBar()

        // initialise the variable with initial value of the color
        mSelectedColor = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].labelColor
        if(mSelectedColor.isNotEmpty()){
            setColor()
        }

        // click listener for the update button
        findViewById<Button>(R.id.btn_update_card_details).setOnClickListener {
            // if edit text is not empty then calling update card details function
            if(findViewById<EditText>(R.id.et_name_card_details).text.toString().isNotEmpty()){
                updateCardDetails()
            }
            // else make a toast
            else{
                Toast.makeText(this@CardsDetailActivity,"Please enter a card name", Toast.LENGTH_SHORT).show()
            }
        }

        // setting on click listener for button to select the color
        findViewById<TextView>(R.id.tv_select_label_color).setOnClickListener{
            labelColorListDialog()
        }

        // setting up the click listener to select members
        findViewById<TextView>(R.id.tv_select_members).setOnClickListener{
            membersListDialog()
        }

        setupSelectedMembersList()
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

    // function to fetch the intent data
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
        if(intent.hasExtra(Constants.BOARD_MEMBERS_LIST)){
            mMembersDetailList = intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
        }
    }

    // connecting menu to delete card
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card,menu)
        return super.onCreateOptionsMenu(menu)
    }

    // writing the working of the menu click listener
    override fun onOptionsItemSelected(item: MenuItem) : Boolean{
        when(item.itemId){
            R.id.action_delete_card->{
                // calling alert dialog for delete list which contains delete card function also
                alertDialogForDeleteList(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // function called from fire store class after updating the task list
    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    // function to update the card details
    private fun updateCardDetails(){

        // here first we have to remove the last task list that is 'add list' as when we will again go back to
        // the taskList Activity we will load the details by calling getBoardDetails function which will again add
        // the task list activity
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        // making a new card as updated one
        val card = Card(
            findViewById<EditText>(R.id.et_name_card_details).text.toString(),
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].createdBy,
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo,
            mSelectedColor)

        // assigning new card details to our mBoardDetails variable
        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition] = card

        // updating the new details using update function used in task list activity
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@CardsDetailActivity,mBoardDetails)

    }

    // function to delete the card ( called from alert dialog function )
    private fun deleteCard(){
        // make updated list of cards
        val cardList : ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cards
        // remove the card
        cardList.removeAt(mCardPosition)

        // to get rid of add card element
        val taskList : ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)

        // fill the new card list
        taskList[mTaskListPosition].cards = cardList

        // calling add update card list function
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@CardsDetailActivity,mBoardDetails)
    }

    // function to show alert dialog while clicking on delete button
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

    // function to show the member list dialog
    private fun membersListDialog(){
        // list storing the assigned member to the board
        var cardAssignedMembersList =
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo

        // if list is not empty
        if(cardAssignedMembersList.size > 0){
            for(i in mMembersDetailList.indices){
                for(j in cardAssignedMembersList){
                    if(mMembersDetailList[i].id == j){
                        mMembersDetailList[i].selected = true
                    }
                }
            }
        }
        else{
            for(i in mMembersDetailList.indices){
                mMembersDetailList[i].selected = false
            }
        }
        val listDialog = object : MembersSelectListDialog(
            this@CardsDetailActivity,
            mMembersDetailList,
            resources.getString(R.string.str_select_members)
        ){
            override fun onItemSelected(user: User, action: String) {
                if(action == Constants.SELECT){
                    if(!mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.contains(user.id)){
                        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.add(user.id)
                    }
                }
                else{
                    mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.remove(user.id)

                    for(i in mMembersDetailList.indices){
                        if(mMembersDetailList[i].id == user.id){
                            mMembersDetailList[i].selected = false
                        }
                    }
                }
                setupSelectedMembersList()
            }
        }

        listDialog.show()
    }

    // function to set up the member list in that card in the select members space
    @SuppressLint("CutPasteId")
    private fun setupSelectedMembersList(){
        val cardAssignedMembersList =
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo

        val selectedMembersList : ArrayList<SelectedMembers> = ArrayList()

        for(i in mMembersDetailList.indices){
            for(j in cardAssignedMembersList){
                if(mMembersDetailList[i].id == j){
                    val selectedMember = SelectedMembers(
                        mMembersDetailList[i].id,
                        mMembersDetailList[i].image
                    )
                    selectedMembersList.add(selectedMember)
                }
            }
        }

        if(selectedMembersList.size > 0){
            selectedMembersList.add(SelectedMembers("",""))

            findViewById<TextView>(R.id.tv_select_members).visibility = View.GONE

            findViewById<RecyclerView>(R.id.rv_selected_members_list).visibility = View.VISIBLE
            findViewById<RecyclerView>(R.id.rv_selected_members_list).layoutManager = GridLayoutManager(this,5)

            val adapter = CardMembersListItemAdapter(this,selectedMembersList)
            findViewById<RecyclerView>(R.id.rv_selected_members_list).adapter = adapter

            adapter.setOnClickListener(
                object: CardMembersListItemAdapter.OnClickListener{
                    override fun onClick(){
                        membersListDialog()
                    }
                }
            )

        }
        else{
            findViewById<TextView>(R.id.tv_select_members).visibility = View.VISIBLE
            findViewById<RecyclerView>(R.id.rv_selected_members_list).visibility = View.GONE

        }
    }

}