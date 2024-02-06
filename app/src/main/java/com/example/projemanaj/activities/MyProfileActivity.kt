package com.example.projemanaj.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.projemanaj.R
import com.example.projemanaj.firebase.FirestoreClass
import com.example.projemanaj.models.User

class MyProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        setupActionBar()
        FirestoreClass().signInRegisteredUser(this@MyProfileActivity)
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
}