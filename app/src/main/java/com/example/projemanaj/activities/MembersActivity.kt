package com.example.projemanaj.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.projemanaj.R
import com.example.projemanaj.models.Board
import com.example.projemanaj.utils.Constants

class MembersActivity : AppCompatActivity() {

    private lateinit var mBoardDetails : Board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member)

        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }

        setupActionBar()
    }

    private fun setupActionBar(){
        val toolbar : Toolbar = findViewById(R.id.toolbar_members_activity)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_white__ios_24)
            actionBar.title = resources.getString(R.string.members)
        }
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }
}