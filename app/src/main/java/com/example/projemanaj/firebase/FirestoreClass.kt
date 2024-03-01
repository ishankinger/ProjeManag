package com.example.projemanaj.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.projemanaj.activities.*
import com.example.projemanaj.models.Board
import com.example.projemanaj.models.User
import com.example.projemanaj.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

// This class deals with set , get and update data in the Firestore cloud for any user


class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    // this function will be called when user will click the signUp button in sign up activity
    // This will store the users details in the cloud firestore
    fun registerUser(activity : SignUpActivity, userInfo : User){
        // set name to the collection id
        mFireStore.collection(Constants.USERS)

            // set the documentID (uid of the user)
            .document(getCurrentUserID())

            // set the user's Information to that document
            .set(userInfo, SetOptions.merge())

            // move to signUp Activity to it's final function
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
    }

    // this function will be called when user will click the signIn button in signIn activity
    // This function will help in getting user's information so is very useful for other activities also as we
    // want to update a lot of things in our app from user's information
    fun signInRegisteredUser(activity : Activity, readBoardList : Boolean = false){
        // collection id name
        mFireStore.collection(Constants.USERS)

            // uid of the user
            .document(getCurrentUserID())

            // now we will get the user's information stored in this particular id
            .get()

            // now in this success Listener we get the user's data stored in the cloud firestore
            .addOnSuccessListener {

                // get the data of user in some variable and further can be used in different activities
                val loggedInUser = it.toObject(User::class.java)!!
                when(activity){
                    // in signInActivity we load user data to show that data is loaded
                    is SignInActivity ->{
                        activity.signInSuccess(loggedInUser)
                    }
                    // in main activity we load user data to show in navDrawer
                    is MainActivity ->{
                        activity.updateNavigationUserDetails(loggedInUser, readBoardList)
                    }
                    // in myProfileActivity we load user data to show on the screen
                    is MyProfileActivity ->{
                        activity.updateMyProfileUserDetails(loggedInUser)
                    }
                }
            }
    }

    // This function is called when update button is clicked and there is any change in the user's data
    // function to update the user's data in the firestore cloud
    fun updateUserProfileData(activity: MyProfileActivity, userHashMap : HashMap<String,Any>){
        // collection id
        mFireStore.collection(Constants.USERS)

            // document id (uid)
            .document(getCurrentUserID())

            // update using userHashMap which stores only updated values
            .update(userHashMap)


            // if done successfully then toast and jump to profileUpdateSuccess function for the activity
            .addOnSuccessListener{
                Toast.makeText(activity,"Profile updated successfully!", Toast.LENGTH_LONG).show()
                activity.profileUpdateSuccess()
            }

            // else if failure then toast of profile not updated
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Toast.makeText(activity,"Profile not updated ", Toast.LENGTH_LONG).show()
            }
    }


    // function called when create button is clicked and all the information about the board is extracted
    fun createBoard(activity : CreateBoardActivity,board: Board){
        // set the name to the collection id
        mFireStore.collection(Constants.BOARD)

            .document()

            // set the board information to that document
            .set(board,SetOptions.merge())

            // move to final successful function
            .addOnSuccessListener {
                Toast.makeText(activity,
                    "Board created successfully",Toast.LENGTH_SHORT).show()
                activity.boardCreatedSuccessfully()
            }
    }

    fun getBoardsList(activity : MainActivity){
        mFireStore.collection(Constants.BOARD)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserID())
            .get()
            .addOnSuccessListener {
                document ->
                val boardList : ArrayList<Board> = ArrayList()
                for(i in document.documents){
                    val board = i.toObject(Board::class.java)!!
                    board.documentId = i.id
                    boardList.add(board)
                }
                activity.populateBoardsListToUI(boardList)
            }
            .addOnFailureListener{e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while creating")
            }
    }

    // function will return the uid of the current user
    fun getCurrentUserID(): String{
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if(currentUser != null){
            currentUserId = currentUser.uid
        }
        return currentUserId
    }
}