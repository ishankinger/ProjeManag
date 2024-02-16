package com.example.projemanaj.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.projemanaj.R
import com.example.projemanaj.firebase.FirestoreClass


// This activity is for displaying the very first screen of the app for small interval giving name for the app
// It doesn't contain any action bar and window title so replace it's theme with no action bar in manifest.xml

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        // taking all other methods from onCreate inbuilt function in AppCompatActivity
        super.onCreate(savedInstanceState)

        // it's content is connected with xml activity_splash2
        setContentView(R.layout.activity_splash2)

        // so this hides the status bar and makes our splash activity full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // Navigation from splash Activity to intro Activity after a certain period of time
        // Handler function will post delay any process for some interval
        Handler().postDelayed({

            // This code is for auto login

            // This variable is for checking that whether the user is already logged in the app or not
            var currentUserId = FirestoreClass().getCurrentUserID()

            // if already logged in then jump to MainActivity
            if(currentUserId.isNotEmpty()){
                startActivity(Intent(this,MainActivity::class.java))
            }

            // else jump to intro Activity
            else{
                startActivity(Intent(this,IntroActivity::class.java))
            }

            // this finish function help user not come back to splash Activity after pressing back button
            finish()

            // here we have define 2500 ms of post delay means after 2.5 seconds the methods in handler will be executed
        },2500)
    }
}