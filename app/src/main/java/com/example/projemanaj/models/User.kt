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
    val fcmToken : String = "",
    var selected : Boolean = false
) : Parcelable

// This is the local variable to store the information of the board for a particular user
@Parcelize
data class Board(
    val name : String = "",
    val image : String = "",
    val createdBy : String = "",
    val assignedTo : ArrayList<String> = ArrayList(),
    var documentId : String = "",
    var taskList : ArrayList<Task> = ArrayList()
) : Parcelable

// This variable will store all the tasks contained by a particular board
@Parcelize
data class Task(
    var title : String = "",
    val createdBy : String = "",
    var cards : ArrayList<Card> = ArrayList()
) : Parcelable

//
@Parcelize
data class Card(
    val name : String = "",
    val createdBy : String = "",
    val assignedTo : ArrayList<String> = ArrayList(),
    val labelColor : String = "",
    val dueDate : Long = 0
) : Parcelable

@Parcelize
data class SelectedMembers(
    val id : String = "",
    val image : String = ""
): Parcelable