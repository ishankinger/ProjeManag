package com.example.projemanaj.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.projemanaj.R
import com.example.projemanaj.utils.Constants
import java.io.IOException

class CreateBoardActivity : AppCompatActivity() {

    // variable storing the uri of the selected image
    private var mSelectedImageFileUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_create_board)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()


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

    }

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

}