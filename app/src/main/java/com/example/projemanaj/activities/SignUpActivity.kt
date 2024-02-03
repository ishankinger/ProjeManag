package com.example.projemanaj.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.projemanaj.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()
        findViewById<Button>(R.id.btn_sign_up).setOnClickListener{
            registerUser()
        }
    }
    private fun setupActionBar(){
        val toolbar : Toolbar = findViewById(R.id.toolbar_sign_up_activity)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24)
        }
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun registerUser(){
        val name : String = findViewById<TextView>(R.id.et_name).text.toString().trim{it <= ' '}
        val email : String = findViewById<TextView>(R.id.et_email).text.toString().trim{it <= ' '}
        val password : String = findViewById<TextView>(R.id.et_password).text.toString().trim{it <= ' '}

        if(validateForm(name,email,password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val registeredEmail = firebaseUser.email!!
                        Toast.makeText(
                            this@SignUpActivity,
                            "$name you have successfully registered the eamil address $registeredEmail",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@SignUpActivity,
                            task.exception!!.message,
                            Toast.LENGTH_LONG
                        )
                    }
                }
        }
    }

    private fun validateForm(name : String, email: String, password : String) : Boolean{
        return when{
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("Please enter a name")
                false
            }
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please enter a email")
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