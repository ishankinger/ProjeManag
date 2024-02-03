package com.example.projemanaj.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.projemanaj.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

open class BaseActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false
    private lateinit var mProgressDialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun showProgressDialog(text : String){
        mProgressDialog = Dialog(this)
        // set the screen content from a layout resource
        // the resource will be inflated, adding all top-level views to the screen
        mProgressDialog.setContentView(R.layout.dialog_progress)
//        val progressText : TextView = findViewById(R.id.tv_progress_text)
//        progressText.text = text
        // start dialog and display it on the screen
        mProgressDialog.show()
    }

    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }

    fun getCurrentUserID(): String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun doubleBackToExit(){
        // when variable true then backPressed this true will occur before second click
        if(doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        // if user has clicked the button only once
        // so we give user a toast saying press again
        // variable is also set to true so after this click it can go back
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(
            this,
            resources.getString(R.string.please_click_back_again_to_exit),
            Toast.LENGTH_SHORT
        ).show()
        // if user doesn't press button for some time
        Handler().postDelayed({doubleBackToExitPressedOnce = false},2000)
    }

    fun showErrorSnackBar(message: String){
        val snackBar = Snackbar.make(findViewById(android.R.id.content)
            ,message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this,R.color.snackbar_error_color))
        snackBar.show()
    }
}