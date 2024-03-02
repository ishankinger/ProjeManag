package com.example.projemanaj.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.projemanaj.R
import com.example.projemanaj.firebase.FirestoreClass
import com.example.projemanaj.models.Board
import com.example.projemanaj.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

// This activity is used to create boards for particular user and store them in fire store database

class CreateBoardActivity : BaseActivity() {

    // variable storing the uri of the selected image
    private var mSelectedImageFileUri : Uri? = null

    // variable storing the url of the selected image
    private var mBoardImageURL : String = ""

    // variable for storing the user name of the user who has created the board which should be filled
    private lateinit var mUserName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_create_board)

        // to hide the status bar
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // call setup action bar function
        setupActionBar()

        // getting the userName from the intent of navigation from main activity to createBoard activity
        if (intent.hasExtra(Constants.NAME)) {
            mUserName = intent.getStringExtra(Constants.NAME)!!
        }

        // setting up the click listener on the image so to change if we want
        findViewById<ImageView>(R.id.iv_board_image).setOnClickListener{

            // if we have permission to choose image then call show Image chooser function
            if(ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this@CreateBoardActivity)

                // else we will ask for the request to the user, generic code for asking permission
            }else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        // click listener for create button
        findViewById<Button>(R.id.btn_create).setOnClickListener(){

            // if chosen image is null means nothing is chosen then call upload image function
            // inside uploadBoardImage function, create board function will be called
            if(mSelectedImageFileUri != null){
                uploadBoardImage()
            }
            // else directly call create Board function
            else{
                showProgressDialog(resources.getString(R.string.please_wait))
                createBoard()
            }
        }

    }

    // function to setUp Action Bar ( navigating arrows and title of the screen )
    private fun setupActionBar(){
        val toolbar : Toolbar = findViewById(R.id.toolbar_create_board_activity)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_white__ios_24)
            actionBar.title = resources.getString(R.string.create_board_title)
        }
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    // function to get the permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ){
        // this is the generic code for getting permission result
        super.onRequestPermissionsResult(requestCode,permissions,grantResults)
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            // if we get the permission then we can call show image chooser function
            if(grantResults.isNotEmpty()){
                // using the imageChooser function present in the Constants
                Constants.showImageChooser(this@CreateBoardActivity)

                // else we will give a toast saying permission denied
            }else{
                Toast.makeText(
                    this,
                    "Oops, you just denied the permission for storage.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // Generic code for updating the image on the image view after choosing image from device
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK
            && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
            && data!!.data != null){
            mSelectedImageFileUri = data.data
            try{
                Glide.with(this@CreateBoardActivity)
                    .load(mSelectedImageFileUri.toString())
                    .centerCrop()
                    .circleCrop()
                    .placeholder(R.drawable.ic_board_place_holder)
                    .into(findViewById(R.id.iv_board_image))
            }catch(e: IOException){
                e.printStackTrace()
            }
        }
    }

    // function to upload image to the firebase storage
    private fun uploadBoardImage(){
        // first show progress dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        // push file into the storage
        if(mSelectedImageFileUri != null){

            // generic code for storing file to the storage
            val sRef : StorageReference =
                FirebaseStorage.getInstance().reference
                    // getFileExtension used here( from Constants )
                    .child("BOARD_IMAGE" + System.currentTimeMillis()
                            + "." + Constants.getFileExtension(mSelectedImageFileUri,this@CreateBoardActivity))

            // Put the file in storage and then work on it's success and failure listener
            sRef.putFile(mSelectedImageFileUri!!)

                // if file loaded to the storage
                .addOnSuccessListener {

                    // log message
                    Log.i("Firebase Board Image Url",
                        it.metadata!!.reference!!.downloadUrl.toString())

                    // now we will call update Profile data
                    it.metadata!!.reference!!.downloadUrl.addOnSuccessListener {

                        // file url copied to local for further update in the firebase cloud
                        mBoardImageURL = it.toString()

                        // now we have all updated data with us so we can call this function
                        createBoard()
                    }
                }

                // if the file not loaded then show exception
                .addOnFailureListener{
                    Toast.makeText(
                        this,
                        "file not loaded to the firebase storage",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

    // function to create board, all the information of board will be filled and then call to firebase
    private fun createBoard(){
        val assignedUsersArrayList : ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserID())

        // board variable ( combining all the information extracted )
        var board = Board(
            findViewById<AppCompatEditText>(R.id.et_board_name).text.toString(), // board name
            mBoardImageURL,                                                      // image url
            mUserName,                                                           // user name
            assignedUsersArrayList                                               // assignedTo array list
        )

        // calling firestore class create Board function to create board in the firebase
        FirestoreClass().createBoard(this,board)
    }

    // at last board is stored in the firebase document and hide progress dialog and finish
    fun boardCreatedSuccessfully(){
        hideProgressDialog()
        // activity result set to RESULT_OK this will help in updating the board list just after adding the board on the screen
        setResult(Activity.RESULT_OK)
        finish()
    }

}