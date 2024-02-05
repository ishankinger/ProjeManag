package com.example.projemanaj.firebase

import android.app.Activity
import com.example.projemanaj.activities.BaseActivity
import com.example.projemanaj.activities.MainActivity
import com.example.projemanaj.activities.SignInActivity
import com.example.projemanaj.activities.SignUpActivity
import com.example.projemanaj.models.User
import com.example.projemanaj.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity : SignUpActivity, userInfo : User){
        // set name to the collection id
        mFireStore.collection(Constants.USERS)
            // set the documentID
            .document(getCurrentUserID())
            // set the user's Information to that document
            .set(userInfo, SetOptions.merge())
            // move to signUp Activity to it's final function
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
    }

    fun signInRegisteredUser(activity : Activity){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener {
                val loggedInUser = it.toObject(User::class.java)!!
                when(activity){
                    is SignInActivity ->{
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity ->{
                        activity.updateNavigationUserDetails(loggedInUser)
                    }
                }
            }
    }

    fun getCurrentUserID(): String{
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if(currentUser != null){
            currentUserId = currentUser.uid
        }
        return currentUserId
    }
}