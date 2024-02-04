package com.example.projemanaj.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.projemanaj.R

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupActionBar()
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
        super.onBackPressed()
        if(findViewById<DrawerLayout>(R.id.drawer_layout).isDrawerOpen(GravityCompat.START)){
            findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(GravityCompat.START)
        }
        else{
            doubleBackToExit()
        }
    }
}