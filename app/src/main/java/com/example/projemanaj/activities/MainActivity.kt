package com.example.projemanaj.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Layout
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.os.postDelayed
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.projemanaj.R
import com.example.projemanaj.firebase.FirestoreClass
import com.example.projemanaj.models.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var doubleBackToSignOutPressedOnce = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupActionBar()
        findViewById<NavigationView>(R.id.nav_view).setNavigationItemSelectedListener(this)
        FirestoreClass().signInRegisteredUser(this@MainActivity)
    }
    private fun setupActionBar(){
        val toolbar : Toolbar = findViewById(R.id.toolbar_main_activity)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.baseline_density_small_24)
        findViewById<Toolbar>(R.id.toolbar_main_activity).setNavigationOnClickListener{
            // Toggle drawer
            toggleDrawer()
        }
    }

    private fun toggleDrawer(){
        if(findViewById<DrawerLayout>(R.id.drawer_layout).isDrawerOpen(GravityCompat.START)){
            findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(GravityCompat.START)
        }
        else{
            findViewById<DrawerLayout>(R.id.drawer_layout).openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed(){
        if(findViewById<DrawerLayout>(R.id.drawer_layout).isDrawerOpen(GravityCompat.START)){
            findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(GravityCompat.START)
        }
        else{
            doubleBackToExit()
        }
    }

    // Called when an item in the navigation menu is selected.
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_sign_out ->{
                doubleBackToSignOut()
            }
            R.id.nav_my_profile ->{
                val intent = Intent(this,MyProfileActivity::class.java)
                findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(GravityCompat.START)
                Handler().postDelayed({startActivity(intent)},250)
            }
        }
        return true
    }

    private fun doubleBackToSignOut(){
        if(doubleBackToSignOutPressedOnce) {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this,IntroActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(GravityCompat.START)
            Handler().postDelayed({startActivity(intent)},250)
            return
        }
        doubleBackToSignOutPressedOnce = true
        showErrorSnackBar("Sure You want to Exit then Double Press")
        Handler().postDelayed({doubleBackToSignOutPressedOnce = false},4000)
    }

    fun updateNavigationUserDetails(user : User){
        Glide.with(this)
            .load(user.image)
            .centerCrop()
            .circleCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(findViewById(R.id.nav_user_image))

        findViewById<TextView>(R.id.nav_username).text = user.name
    }
}