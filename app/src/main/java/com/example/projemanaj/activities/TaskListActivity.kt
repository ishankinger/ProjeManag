package com.example.projemanaj.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projemanaj.R
import com.example.projemanaj.adapters.TaskListItemAdapter
import com.example.projemanaj.firebase.FirestoreClass
import com.example.projemanaj.models.Board
import com.example.projemanaj.models.Task
import com.example.projemanaj.utils.Constants

class TaskListActivity : BaseActivity() {

    // variable to store the board details
    private lateinit var mBoardDetails: Board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        // getting the document id for a particular board from the share intent coming from main activity
        var boardDocumentId = ""
        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        // showing progress dialog as we are calling a fire store function further
        showProgressDialog(resources.getString(R.string.please_wait))

        // calling the fire store class function to get details of the board using it's document id
        FirestoreClass().getBoardDetails(this, boardDocumentId)
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

        // getting the task name and uid of the user who has created it
        val task = Task(listName, model.createdBy)

        // updating the task list at given position and removing add list from the end
        mBoardDetails.taskList[position] = task
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

        // calling firestore database function to update the task list
        FirestoreClass().addUpdateTaskList(this@TaskListActivity, mBoardDetails)

    }
}