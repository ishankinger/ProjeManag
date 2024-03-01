package com.example.projemanaj.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// This is local variable to store the information of the user details so that we can use it further easily
@Parcelize
data class User(
    val id : String = "",
    val name : String = "",
    val email : String = "",
    val image : String = "",
    val mobile : Long = 0,
    val fcmToken : String = ""
) : Parcelable

// This is the local variable to store the information of the board for a particular user
@Parcelize
data class Board(
    val name : String = "",
    val image : String = "",
    val createdBy : String = "",
    val assignedTo : ArrayList<String> = ArrayList(),
    var documentId : String = ""
) : Parcelable