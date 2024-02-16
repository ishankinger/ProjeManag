package com.example.projemanaj.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.projemanaj.R
import com.example.projemanaj.firebase.FirestoreClass
import com.example.projemanaj.models.User
import com.example.projemanaj.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    // companion object used in the permission request for choosing image from device
    companion object{
        private const val READ_STORAGE_PERMISSION_CODE = 1
        private const val PICK_IMAGE_REQUEST_CODE = 2
    }

    // variable storing the uri of the selected image
    private var mSelectedImageFileUri : Uri? = null

    // variable storing the url of the image which we will update after clicking the update button
    private var mProfileImageURL : String = ""

    // variable which will store all the details of the user
    // we will initialise it's value in updateMyProfileUserDetails function so that we can compare values with new values updated
    private lateinit var mUserDetails : User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        // action bar is set up similar to other activities
        setupActionBar()

        // calling the signInRegisteredUser function to update the data on the screen
        FirestoreClass().signInRegisteredUser(this@MyProfileActivity)

        // setting up the click listener on the image so to change if we want
        findViewById<ImageView>(R.id.update_user_image).setOnClickListener{

            // if we have permission to choose image then call show Image chooser function
            if(ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED){
                showImageChooser()

            // else we will ask for the request to the user, generic code for asking permission
            }else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        // setting the update button on click Listener for updating the data in the firebase cloud and storing image in storage
        findViewById<Button>(R.id.btn_update).setOnClickListener{

            // if selected file is not null means we have selected any file
            if(mSelectedImageFileUri != null){
                // function to upload to the firebase
                uploadUserImage()
            }
            // if selected file is null then we don't need to push it into the storage as already there
            // and we will directly call the function to update the data
            else{
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }
        }
    }

    // function to set the action bar showing name of activity with navigating arrow
    private fun setupActionBar(){
        val toolbar : Toolbar = findViewById(R.id.toolbar_my_profile_activity)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_white__ios_24)
            actionBar.title = resources.getString(R.string.my_profile)
        }
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }


    // This function is called just when this activity is created as this is called in firestore class
    // and this will update the user's details that are image, name, email and mobile
    fun updateMyProfileUserDetails(user : User){

        // storing the initial value of updated image
        mUserDetails = user

        Glide.with(this)
            .load(user.image)
            .centerCrop()
            .circleCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(findViewById(R.id.update_user_image))

        findViewById<TextView>(R.id.update_email).text = user.email
        findViewById<TextView>(R.id.update_name).text = user.name
        findViewById<TextView>(R.id.update_mobile).text = user.mobile.toString()
    }


    // function to get the permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ){
        // this is the generic code for getting permission result
        super.onRequestPermissionsResult(requestCode,permissions,grantResults)
        if(requestCode == READ_STORAGE_PERMISSION_CODE){
            // if we get the permission then we can call show image chooser function
            if(grantResults.isNotEmpty()){
                showImageChooser()

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

    // Function to choose image from our device after permission is granted
    private fun showImageChooser(){
        // generic code
        var galleryIntent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    // Generic code for updating the image on the image view after choosing image from device
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK
            && requestCode == PICK_IMAGE_REQUEST_CODE
            && data!!.data != null){
            mSelectedImageFileUri = data.data
            try{
                Glide.with(this@MyProfileActivity)
                    .load(mSelectedImageFileUri.toString())
                    .centerCrop()
                    .circleCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(findViewById(R.id.update_user_image))
            }catch(e: IOException){
                e.printStackTrace()
            }
        }
    }


    // function to upload image to the firebase storage
    private fun uploadUserImage(){
        // first show progress dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        // push file into the storage
        if(mSelectedImageFileUri != null){

            // generic code for storing file to the storage
            val sRef : StorageReference =
                FirebaseStorage.getInstance().reference
                        // getFileExtension used here
                    .child("UESER_IMAGE" + System.currentTimeMillis()
                            + "." + getFileExtension(mSelectedImageFileUri))

            // Put the file in storage and then work on it's success and failuire listener
            sRef.putFile(mSelectedImageFileUri!!)

                // if file loaded to the storage
                .addOnSuccessListener {

                    // log message
                    Log.i("Firebase Image Url",
                    it.metadata!!.reference!!.downloadUrl.toString())

                    // now we will call update Profile data
                    it.metadata!!.reference!!.downloadUrl.addOnSuccessListener {

                        // file url copied to local for further update in the firebase cloud
                        mProfileImageURL = it.toString()

                        // now we have all updated data with us so we can call this function
                        updateUserProfileData()
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

    // used in upload User image function to give it a name
    private fun getFileExtension(uri : Uri?) : String?{
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(contentResolver.getType(uri!!))
    }


    // last function called when all update process is overed
    fun profileUpdateSuccess(){
        // now we can hide progress dialog
        hideProgressDialog()

        //
        setResult(Activity.RESULT_OK)

        // we can go back to main activity by finishing the intent
        finish()
    }


    // function to update the user profile in firestore cloud after the update button is clicked
    private fun updateUserProfileData() {
        // storing the changed values in the hashmap
        val userHashMap: HashMap<String,Any> = HashMap()

        // bool function to check is there at least one change or not
        var anyChangesMade = false

        // compare image
        if (mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image) {
            anyChangesMade = true
            userHashMap[Constants.IMAGE] = mProfileImageURL
        }

        // compare name
        if (findViewById<TextView>(R.id.update_name).text.toString() != mUserDetails.name.toString()) {
            anyChangesMade = true
            userHashMap[Constants.NAME] = findViewById<TextView>(R.id.update_name).text.toString()
        }

        // compare mobile
        if (findViewById<TextView>(R.id.update_mobile).text.toString() != mUserDetails.mobile.toString()) {
            anyChangesMade = true
            userHashMap[Constants.MOBILE] = findViewById<TextView>(R.id.update_mobile).text.toString().toLong()
        }

        // if there are any changes then call updateUserProfile of Firestore Class
        if (anyChangesMade) {
            FirestoreClass().updateUserProfileData(this, userHashMap)
        }
        // else hide the progress dialog
        else{
            hideProgressDialog()
        }
    }
}