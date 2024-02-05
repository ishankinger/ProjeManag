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

class SignInActivity : BaseActivity() {

    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()
        findViewById<Button>(R.id.btn_sign_in).setOnClickListener{
            signInRegisteredUser()
        }
    }
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

    fun signInSuccess(user : User){
        hideProgressDialog()
        Toast.makeText(
            this,
            "Hi ${user.name}, Welcome to ProjeManag",
            Toast.LENGTH_LONG
        ).show()
        val intent = Intent(this,MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // remove all backstack activities
        startActivity(intent)
    }

    @SuppressLint("CutPasteId")
    private fun signInRegisteredUser(){
        val email : String = findViewById<TextView>(R.id.et_email_in).text.toString().trim{it <= ' '}
        val password : String = findViewById<TextView>(R.id.et_password_in).text.toString().trim{it <= ' '}
        if(validateForm(email,password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){task->
//                    hideProgressDialog()
                    if(task.isSuccessful){
//                        val user = auth.currentUser
//                        startActivity(Intent(this,MainActivity::class.java))
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