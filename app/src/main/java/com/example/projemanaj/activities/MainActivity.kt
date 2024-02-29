package com.example.projemanaj.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Layout
import android.view.MenuItem
import android.widget.Button
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
import com.example.projemanaj.utils.Constants
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

// Main activity is divided into some ui parts. First is it's action bar , other is it's main content
// and other is navigation drawer which further includes nav header and menu drawer
class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var doubleBackToSignOutPressedOnce = false

    private lateinit var mUserName : String

    // this object used in the startActivityForResult
    companion object{
        const val MY_PROFILE_REQUEST_CODE : Int = 11
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // function to set action bar
        setupActionBar()

        // nav_view is nav Drawer's id of the main activity which is set to item selected listener
        findViewById<NavigationView>(R.id.nav_view).setNavigationItemSelectedListener(this)

        // when main activity is created then we will call this function of firestroe class
        // this function will help in updating the data in nav drawer that is our name and image
        FirestoreClass().signInRegisteredUser(this@MainActivity)

        findViewById<FloatingActionButton>(R.id.fab_create_board).setOnClickListener{
            val intent = Intent(this@MainActivity,CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            startActivity(intent)
        }
    }

    // This function will set up action bar and also contain the button to toggle the navDrawer
    private fun setupActionBar(){
        val toolbar : Toolbar = findViewById(R.id.toolbar_main_activity)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.baseline_density_small_24)
        // when the toolbar icon is pressed drawer will come out
        findViewById<Toolbar>(R.id.toolbar_main_activity).setNavigationOnClickListener{
            toggleDrawer()
        }
    }

    // function to get drawer out and display on the main activity
    private fun toggleDrawer(){
        // if drawer open then close else open it
        if(findViewById<DrawerLayout>(R.id.drawer_layout).isDrawerOpen(GravityCompat.START)){
            findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(GravityCompat.START)
        }
        else{
            findViewById<DrawerLayout>(R.id.drawer_layout).openDrawer(GravityCompat.START)
        }
    }


    // This function is written so that when back button is pressed and nav drawer is opened then first the drawer should close
    // then call for doubleBackToExit function if again back button pressed
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
            // if signOut is pressed then this function will call
            R.id.nav_sign_out ->{
                doubleBackToSignOut()
            }
            // if myProfile is clicked then we will move to MyProfile Activity with small delay so that navDrawer can close
            R.id.nav_my_profile ->{
                val intent = Intent(this,MyProfileActivity::class.java)
                findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(GravityCompat.START)
                // we will start the intent with startActivity for result so that we can update
                // the nav drawer after coming back from updating the profile
                Handler().postDelayed({startActivityForResult(intent,MY_PROFILE_REQUEST_CODE)},250)
            }
        }
        return true
    }


    // similar function ot double Back to exit
    private fun doubleBackToSignOut(){
        if(doubleBackToSignOutPressedOnce) {

            // call firebase instance to sign out
            FirebaseAuth.getInstance().signOut()

            // navigate to intro activity
            val intent = Intent(this,IntroActivity::class.java)

            // all behind stacks activity to be closed
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            // NavDrawer to be closed
            findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(GravityCompat.START)

            // with small delay we will navigate to intro activity
            Handler().postDelayed({startActivityForResult(intent, MY_PROFILE_REQUEST_CODE)},250)
            return
        }

        // make variable true so that next time if pressed then navigate
        doubleBackToSignOutPressedOnce = true

        // give snackBar event giving warning
        showErrorSnackBar("Sure You want to Exit then Double Press")

        // also adding post delay of 4s if not clicked again then make variable value to false again
        Handler().postDelayed({doubleBackToSignOutPressedOnce = false},4000)
    }

    // This function is called in the Firestore Class in signInRegisterUser method to display user's data on NavDrawer
    fun updateNavigationUserDetails(user : User){
        mUserName = user.name
        // We will be using Glide library to uploading the image
        Glide.with(this)
            .load(user.image)
            .centerCrop()
            .circleCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(findViewById(R.id.nav_user_image))

        // also the name is also updated
        findViewById<TextView>(R.id.nav_username).text = user.name
    }

    // when it returns to main activity from updating the data this function will be called and we will update the data of navDrawer
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE){
            FirestoreClass().signInRegisteredUser(this)
        }
    }

}