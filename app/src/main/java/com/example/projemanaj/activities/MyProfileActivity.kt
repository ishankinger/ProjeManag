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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    companion object{
        private const val READ_STORAGE_PERMISSION_CODE = 1
        private const val PICK_IMAGE_REQUEST_CODE = 2
    }

    private var mSelectedImageFileUri : Uri? = null
    private var mProfileImageURL : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        setupActionBar()
        FirestoreClass().signInRegisteredUser(this@MyProfileActivity)

        findViewById<ImageView>(R.id.update_user_image).setOnClickListener{
            if(ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED){
                showImageChooser()
            }else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_CODE
                )
            }
        }
        findViewById<Button>(R.id.btn_update).setOnClickListener{
            if(mSelectedImageFileUri != null){
                uploadUserImage()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults)
        if(requestCode == READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty()){
                showImageChooser()
            }else{
                Toast.makeText(
                    this,
                    "Oops, you just denied the permission for storage.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showImageChooser(){
        var galleryIntent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

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

    fun updateMyProfileUserDetails(user : User){
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

    private fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        // push file into the storage
        if(mSelectedImageFileUri != null){
            val sRef : StorageReference =
                FirebaseStorage.getInstance().reference
                    .child("UESER_IMAGE" + System.currentTimeMillis()
                            + "." + getFileExtension(mSelectedImageFileUri))

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                Log.i(
                    "Firebase Image Url",
                it.metadata!!.reference!!.downloadUrl.toString()
                )
                Toast.makeText(
                    this,
                    "file upload to storage",
                    Toast.LENGTH_LONG
                ).show()
                hideProgressDialog()
                it.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    Log.i("Downloadable Image URL", it.toString())
                    mProfileImageURL = it.toString()
                }

                // TODO UPDATE PROFILE LIVE DATA

            }.addOnFailureListener{
                exception->
                Toast.makeText(
                    this,
                    exception.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun getFileExtension(uri : Uri?) : String?{
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(contentResolver.getType(uri!!))
    }
}