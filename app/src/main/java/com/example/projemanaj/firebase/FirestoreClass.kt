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
                    // also we are adding read Board List variable to check whether board list should be read or not
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

            // any random name will be given
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

    // function to get the board list for a particular user so that we can display boards on main screen
    fun getBoardsList(activity : MainActivity){
        // go to the collection of name board
        mFireStore.collection(Constants.BOARD)

            // now we will use query and this is where fire store comes into play
            // now we will take all boards whose assigned to is equal to uid of the user
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserID())

            // we get all the boards assigned to this user
            .get()

            // success listener of getting an array of boards assigned to the user uid
            .addOnSuccessListener {
                // we get this document
                document ->

                // an empty board list is defined
                val boardList : ArrayList<Board> = ArrayList()

                // traverse in the document and add every single board to the board list
                for(i in document.documents){
                    val board = i.toObject(Board::class.java)!!
                    // this local board variable will also contain the document id
                    // this is done to keep track of board id while in previous user object we know
                    // it's id that is uid which we set by our own but for board we have set random id
                    board.documentId = i.id
                    boardList.add(board)
                }

                // call for populate function to show these boards on the main screen
                activity.populateBoardsListToUI(boardList)
            }

            // also add failure listener
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