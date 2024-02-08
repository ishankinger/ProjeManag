package com.example.projemanaj.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.projemanaj.activities.*
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
                    is MyProfileActivity ->{
                        activity.updateMyProfileUserDetails(loggedInUser)
                    }
                }
            }
    }

    fun updateUserProfileData(activity: MyProfileActivity, userHashMap : HashMap<String,Any>){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener{
                Toast.makeText(activity,"Profile updated successfully!", Toast.LENGTH_LONG).show()
                activity.profileUpdateSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Toast.makeText(activity,"Profile not updated ", Toast.LENGTH_LONG).show()
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