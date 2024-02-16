package com.example.projemanaj.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import com.example.projemanaj.R

// This activity is the page which will be shown to the user after splash activity if the user is not logged in.
// This will give choice to user to either sign in or sign up.

class IntroActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        // similar to splash activity we hide it's status bar and make it full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // Here we are adding click Listener to the button for navigating to sign up activity
        val btn_sign_up : Button = findViewById(R.id.btn_sign_up_intro)
        btn_sign_up.setOnClickListener {
            // Launch the sign up screen.
            startActivity(Intent(this@IntroActivity, SignUpActivity::class.java))
        }

        // Here we are adding click Listener to the button for navigating to sign in activity
        val btn_sign_in : Button = findViewById(R.id.btn_sign_in_intro)
        btn_sign_in.setOnClickListener {
            // Launch the sign in screen.
            startActivity(Intent(this@IntroActivity, SignInActivity::class.java))
        }

    }
}