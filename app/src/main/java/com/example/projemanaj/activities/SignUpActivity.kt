package com.example.projemanaj.activities

import android.annotation.SuppressLint
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
import com.google.firebase.auth.FirebaseUser


// This activity will help user to signUp any id

class SignUpActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // making full screen and hiding status bar
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // using actionBar function to set navigation in activity
        setupActionBar()

        // setting up click listener on the button to  call registerUser function
        findViewById<Button>(R.id.btn_sign_up).setOnClickListener{
            registerUser()
        }
    }

    // This function will set up the upper navigating arrows
    private fun setupActionBar(){
        // for action bar first we have to make a toolbar in our activity xml file to get space for activity name and navigating arrows
        val toolbar : Toolbar = findViewById(R.id.toolbar_sign_up_activity)

        // this is generic code to generate an navigating arrow
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24)
        }
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    // this function is the last called function for the process of sign up of the user
    // this is called in the firestore class in userRegister function after storing the data in the cloud
    fun userRegisteredSuccess(){

        // first show the toast that the user is successfully registered
        Toast.makeText(
            this@SignUpActivity,
            "you have successfully registered the email address",
            Toast.LENGTH_LONG
        ).show()

        // then we will hide the progress dialog
        hideProgressDialog()

        // will move to intro by finishing this acitivity
        finish()
    }

    // function will be called when signUp button clicked and will initialise the task of signUp
    @SuppressLint("CutPasteId")
    private fun registerUser(){

        // getting the name, email and password from the text views filled by the users
        val name : String = findViewById<TextView>(R.id.et_name_up).text.toString().trim{it <= ' '}
        val email : String = findViewById<TextView>(R.id.et_email_up).text.toString().trim{it <= ' '}
        val password : String = findViewById<TextView>(R.id.et_password_up).text.toString().trim{it <= ' '}

        // checking the name, email and password filled are valid or not
        if(validateForm(name,email,password)){

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

            // For authentication in firebase the first step is to add google services.json in app
            // Then we will use generic code of firebase to create a user with email and password
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener { task ->

                    // now if the task is successful then
                    if (task.isSuccessful) {
                        // in firebase authentication the user is stored as uid (users id) with it's email to get unique address

                        // this variable store the user information (in authentication part of firebase)
                        val firebaseUser: FirebaseUser = task.result!!.user!!

                        // get it's registered email stored in the firebase authentication
                        val registeredEmail = firebaseUser.email!!

                        // then get it's uid and then make a local user variable which will store it's information in it
                        val user = User(firebaseUser.uid,name,registeredEmail)

                        // then we will call the register user method of Firestore class to add the user information in cloud
                        FirestoreClass().registerUser(this,user)

                    } else {
                        // if sign up fails then hide Progress dialog and make text empty
                        hideProgressDialog()
                        findViewById<TextView>(R.id.et_name_up).text = ""
                        findViewById<TextView>(R.id.et_email_up).text = ""
                        findViewById<TextView>(R.id.et_password_up).text = ""
                        showErrorSnackBar("Error occurs Email already exists")
                    }
                }
        }
    }

    // function to check whether the email, name and password are filled in the text views or not
    private fun validateForm(name : String, email: String, password : String) : Boolean{
        return when{
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("Please enter a name")
                false
            }
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