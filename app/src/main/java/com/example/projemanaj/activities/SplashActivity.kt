package com.example.projemanaj.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.projemanaj.R

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash2)

        // so this hides the status bar and makes our splash activity full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // Navigation from splash Activity to intro Activity after a certain period of time
        Handler().postDelayed({
            startActivity(Intent(this,IntroActivity::class.java))
            // this finish function help user not come back to splash Activity after pressing back button
            finish()
        },2500)
    }
}