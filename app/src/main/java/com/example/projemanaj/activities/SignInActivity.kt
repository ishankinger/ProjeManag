package com.example.projemanaj.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.projemanaj.R
import com.example.projemanaj.firebase.FirestoreClass
import com.example.projemanaj.models.User
import com.google.firebase.auth.FirebaseAuth


// This activity will help user to signIn any id

class SignInActivity : BaseActivity() {

    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()

        // full screen and hide status bar
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // similar to signUp activity for this also action bar is made
        setupActionBar()

        // setting an on click Listener on the signIn button to start the process of signing in of the user
        findViewById<Button>(R.id.btn_sign_in).setOnClickListener{
            signInRegisteredUser()
        }
    }

    // This function will set up the upper navigating arrows
    private fun setupActionBar(){
        val toolbar : Toolbar = findViewById<Toolbar>(R.id.toolbar_sign_in_activity)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24)
        }
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    // This function is called after the end of sign in process
    // We will get the user information from the signInRegisteredUser function of Firestore class
    fun signInSuccess(user : User){

        // first hide the Progress dialog
        hideProgressDialog()

        // then show Toast using the information of the user
        Toast.makeText(
            this,
            "Hi ${user.name}, Welcome to ProjeManag",
            Toast.LENGTH_LONG
        ).show()

        // after this we will navigate to the mainActivity
        val intent = Intent(this,MainActivity::class.java)
        // remove all backstack activities
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    // function will be called when signIn button clicked and will initialise the task of signIn
    @SuppressLint("CutPasteId")
    private fun signInRegisteredUser(){
        // get the email and password filled by the user in the text views
        val email : String = findViewById<TextView>(R.id.et_email_in).text.toString().trim{it <= ' '}
        val password : String = findViewById<TextView>(R.id.et_password_in).text.toString().trim{it <= ' '}

        // check whether the email and password are filled or empty
        if(validateForm(email,password)){

            // checking the password length error
            if(password.length <= 6){
                showErrorSnackBar("Password must be of atleast 6 characters")
                return
            }

            // checking the email length error
            var len = email.length
            if(len <= 4){
                showErrorSnackBar("Email is not formatted properly")
                return
            }
            if(email[len-1] != 'm' && email[len-2] != 'o' && email[len-3] != 'c' && email[len-4] != '.'){
                showErrorSnackBar("Email is not formatted properly")
                return
            }

            // if everything is correct in email, name and password then we are ready to start process for sign up
            // so start the progress dialog box
            showProgressDialog(resources.getString(R.string.please_wait))

            // the generic code for sign in the user with email and password
            auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){task->

                    // if the task is successful then
                    if(task.isSuccessful){
                        // call the signInRegisteredUser of Firestore class to get the data of the user
                        FirestoreClass().signInRegisteredUser(this)
                    }
                    else{
                        // if sign in fails then hide Progress dialog and make text empty
                        hideProgressDialog()
                        findViewById<TextView>(R.id.et_email_in).text = ""
                        findViewById<TextView>(R.id.et_password_in).text = ""
                        showErrorSnackBar("Authentication failed no such id found")
                    }
                }
        }
    }

    // function to check whether the email and password are filled in the text views or not
    private fun validateForm(email: String, password : String) : Boolean{
        return when{
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please enter an email")
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("Please enter a password")
                false
            }
            else ->{
                true
            }
        }
    }
}