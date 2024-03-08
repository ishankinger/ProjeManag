package com.example.projemanaj.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projemanaj.R
import com.example.projemanaj.adapters.MemberListItemAdapter
import com.example.projemanaj.firebase.FirestoreClass
import com.example.projemanaj.models.Board
import com.example.projemanaj.models.User
import com.example.projemanaj.utils.Constants

class MembersActivity : BaseActivity() {

    // storing the board details in this variable
    private lateinit var mBoardDetails : Board

    // list storing the members which are assigned to this board
    private lateinit var mAssignedMembersList : ArrayList<User>

    // boolean variable to check whether to reload the task list page or not after returning
    private var anyChangesMade : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member)

        // getting the board details from the task list activity and storing it in our local variable
        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }

        // function call to set up action bar
        setupActionBar()

        // calling firebase function so showing progress dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        // function of firebase called with input as list of 'assigned to' members of board list
        FirestoreClass().getAssignedMembersListDetails(this,mBoardDetails.assignedTo)
    }

    // function to set up action bar in the members list activity
    private fun setupActionBar(){
        val toolbar : Toolbar = findViewById(R.id.toolbar_members_activity)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_white__ios_24)
            actionBar.title = resources.getString(R.string.members)
        }
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    // function to show members of a particular board in the member list
    // this function is called from the firebase function getAssignedMembersListDetails
    @SuppressLint("CutPasteId")
    fun setUpMembersList(list: ArrayList<User>){

        // storing the user list in the local variable
        mAssignedMembersList = list

        // hiding the progress dialog which was started before calling the fire store function
        hideProgressDialog()

        // setting up the layout manager for our recycler view
        findViewById<RecyclerView>(R.id.rv_members_list).layoutManager = LinearLayoutManager(this)
        findViewById<RecyclerView>(R.id.rv_members_list).setHasFixedSize(true)

        // adding the adapter to our recycler view and giving it the user detail list to show on the screen
        val adapter = MemberListItemAdapter(this,list)
        findViewById<RecyclerView>(R.id.rv_members_list).adapter = adapter
    }

    // function to create a menu in the toolbar and inflating our layout menu to it
    override fun onCreateOptionsMenu(menu : Menu?) : Boolean{
        menuInflater.inflate(R.menu.menu_add_member,menu)
        return super.onCreateOptionsMenu(menu)
    }

    // function to perform functions after clicking on the items of the menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // when add member item is selected
        when(item.itemId){
            R.id.action_add_member->{
                // call for dialog search member
                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // function to show dialog to the user to add any member by putting his/her email id
    private fun dialogSearchMember(){
        // making a dialog variable and inflating to our dialog layout
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialogue_search_member)

        // click listener if in dialog box 'add' is clicked
        dialog.findViewById<TextView>(R.id.tv_add).setOnClickListener{
            // get the email from edit text view filled by user
            val email = dialog.findViewById<EditText>(R.id.et_email_search_member).text.toString()
            // if email is not empty
            if(email.isNotEmpty()){
                // remove dialog
                dialog.dismiss()
                // show progress dialog box
                showProgressDialog(resources.getString(R.string.please_wait))
                // call for fire store class function to get member having particular email
                FirestoreClass().getMemberDetails(this,email)
            }
            else{
                // if email is empty then make a toast
                Toast.makeText(this,"Please enter the email",Toast.LENGTH_SHORT).show()
            }
        }

        // click listener for 'cancel' option in dialog box
        dialog.findViewById<TextView>(R.id.tv_cancel).setOnClickListener{
            // just dismiss the dialog box
            dialog.dismiss()
        }

        // calling dialog to show
        dialog.show()
    }

    // function called from fire store class after getting the user of given email
    fun memberDetails(user: User){
        // add this user to the assigned to list of the board
        mBoardDetails.assignedTo.add(user.id)

        // now we will update this list in the fire store database by input of board details and user
        FirestoreClass().assignMemberToBoard(this,mBoardDetails,user)
    }

    // function called from the fire store class after updating the assigned to list of the board
    fun membersAssignedSuccess(user : User){
        // now hide progress dialog started from the add button of seach member dialog box
        hideProgressDialog()

        // add the user to the local variable of the assigned list
        mAssignedMembersList.add(user)

        // we have made some changes so make it true
        anyChangesMade = true

        // call for showing the member list again
        setUpMembersList(mAssignedMembersList)
    }

    // overriding the back pressed button
    override fun onBackPressed() {
        // if any changes made then give result ok to task list activity0
        if(anyChangesMade){
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

}