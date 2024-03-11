package com.example.projemanaj.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projemanaj.R
import com.example.projemanaj.adapters.TaskListItemAdapter
import com.example.projemanaj.firebase.FirestoreClass
import com.example.projemanaj.models.Board
import com.example.projemanaj.models.Card
import com.example.projemanaj.models.Task
import com.example.projemanaj.models.User
import com.example.projemanaj.utils.Constants

class TaskListActivity : BaseActivity() {

    // variable to store the board details
    private lateinit var mBoardDetails: Board

    // variable to store the board's document id
    private lateinit var mBoardDocumentId : String

    // variable to store the members assigned to the board
    private lateinit var mAssignedMemberDetailList : ArrayList<User>

    // for activity result, used in sharing intent
    companion object{
        const val MEMBERS_REQUEST_CODE : Int = 13
        const val CARD_DETAILS_REQUEST_CODE : Int = 14
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        // getting the document id for a particular board from the share intent coming from main activity
        mBoardDocumentId = ""
        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            mBoardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        // showing progress dialog as we are calling a fire store function further
        showProgressDialog(resources.getString(R.string.please_wait))

        // calling the fire store class function to get details of the board using it's document id
        FirestoreClass().getBoardDetails(this, mBoardDocumentId)
    }

    // function to start the card details activity ( called from the adapter of the taskListItemAdapter )
    fun cardDetails(taskListPosition : Int, cardListPosition : Int){
        val intent = Intent(this,CardsDetailActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION,taskListPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION,cardListPosition)
        intent.putExtra(Constants.BOARD_MEMBERS_LIST,mAssignedMemberDetailList)
        startActivityForResult(intent, CARD_DETAILS_REQUEST_CODE)
    }

    // function to set up the action bar
    private fun setupActionBar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar_task_list_activity)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_white__ios_24)
            actionBar.title = mBoardDetails.name
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    // function to create menu and inflate it to our given layout menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members,menu)
        return super.onCreateOptionsMenu(menu)
    }

    // function to do further operations after pressing the menu items
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // when our add member button is clicked
        when(item.itemId){
            R.id.action_members->{
                // navigating to members activity with board's details as sharing input extra
                val intent = Intent(this,MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL,mBoardDetails)
                // also adding the activity for result code
                startActivityForResult(intent,MEMBERS_REQUEST_CODE)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // this function is called from the fire store class
    @SuppressLint("CutPasteId")
    fun boardDetails(board: Board) {

        // the board details coming from fire store class is stored in this local variable
        mBoardDetails = board

        // after completion of fetching the details of a board hide progress dialog
        hideProgressDialog()

        // calling set up action bar and using mBoard details to set it's title name
        setupActionBar()

        // adding the first task as the add list button which will work to add another list
        val addTaskList = Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)

        // setting the layout manager for rv_task_list as linear layout horizontal
        findViewById<RecyclerView>(R.id.rv_task_list).layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        findViewById<RecyclerView>(R.id.rv_task_list).setHasFixedSize(true)

        // attaching the Task List adapter to the ui and passing task list to the adapter
        val adapter = TaskListItemAdapter(this, board.taskList)
        findViewById<RecyclerView>(R.id.rv_task_list).adapter = adapter

        // get the assigned member list from the fire store database function
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAssignedMembersListDetails(this@TaskListActivity,mBoardDetails.assignedTo)

    }

    // function called finally from fire store class after updating the task list
    fun addUpdateTaskListSuccess() {
        // hide progress dialog for updating the task list
        hideProgressDialog()

        // again show progress dialog as we will now show correct task list by calling board details function of fire store class
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this, mBoardDetails.documentId)
    }

    // function to add list in the tasks array of a particular board
    fun createTaskList(taskListName: String) {

        // getting the task list name from the ui add button from the adapter and current user id as the id of user who created it
        val task = Task(taskListName, FirestoreClass().getCurrentUserID())

        // adding the task list to the first of the array
        mBoardDetails.taskList.add(0, task)

        // we have to remove the last task List that is add list as when we again call board details
        // to show tasks then a new task of add list is pushed into the task list
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        // now show progress dialog to update the tasks in the fire store database
        showProgressDialog(resources.getString((R.string.please_wait)))

        // calling firestore database function to update the task list
        FirestoreClass().addUpdateTaskList(this@TaskListActivity, mBoardDetails)
    }

    // function called to update the tasks of a particular board
    fun updateTaskList(position: Int, listName: String, model: Task) {

        // updating the task list title at given position and removing 'add list' from the end
        mBoardDetails.taskList[position].title = listName
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        // now show progress dialog to update the tasks in the fire store database
        showProgressDialog(resources.getString((R.string.please_wait)))

        // calling firestore database function to update the task list
        FirestoreClass().addUpdateTaskList(this@TaskListActivity, mBoardDetails)

    }

    // function to delete the task from the task list
    fun deleteTaskList(position: Int) {

        // deleting the task of task list at given position and remove add list from the end
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        // now show progress dialog to update the tasks in the fire store database
        showProgressDialog(resources.getString((R.string.please_wait)))

        // calling fire store database function to update the task list
        FirestoreClass().addUpdateTaskList(this@TaskListActivity, mBoardDetails)

    }

    // function to add card to the particular task
    fun addCardToTaskList(position : Int, cardName : String){

        // remove add list
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        // a card can be assigned to multiple users so created array list for that
        var cardAssignedUserList : ArrayList<String> = ArrayList()
        // adding this user id to it
        cardAssignedUserList.add(FirestoreClass().getCurrentUserID())

        // making a new card
        val card = Card(cardName,FirestoreClass().getCurrentUserID(),cardAssignedUserList)
        // adding new card to cards List
        val cardsList = mBoardDetails.taskList[position].cards
        cardsList.add(card)

        // updated task
        val task = Task(
            mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createdBy,
            cardsList
        )

        // updating old task with the new one
        mBoardDetails.taskList[position] = task

        // now show progress dialog to update the tasks in the fire store database
        showProgressDialog(resources.getString((R.string.please_wait)))

        // calling fire store database function to update the task list
        FirestoreClass().addUpdateTaskList(this@TaskListActivity, mBoardDetails)

    }

    // function to get the activity result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // to load the details again when returning from any activity back to task list activity
        if (resultCode == Activity.RESULT_OK
            && (requestCode == MEMBERS_REQUEST_CODE || requestCode == CARD_DETAILS_REQUEST_CODE)
        ) {
            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardDetails(this@TaskListActivity, mBoardDocumentId)
        } else {
            Log.e("Cancelled", "Cancelled")
        }
    }

    // function called from fire store function after getting user list assigned to the board
    fun boardMembersDetailsList(list : ArrayList<User>){
        // filling the value of the variable which will further be navigated to the cards detail activity
        mAssignedMemberDetailList = list
        // hide the progress dialog started
        hideProgressDialog()
    }
}