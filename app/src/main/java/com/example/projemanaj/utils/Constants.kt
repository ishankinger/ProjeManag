package com.example.projemanaj.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.projemanaj.activities.MyProfileActivity

// some constant values used in the firebase cloud
object Constants{
    const val USERS : String = "users"
    const val IMAGE : String = "image"
    const val NAME : String = "name"
    const val MOBILE : String = "mobile"
    const val BOARD : String = "board"
    const val ASSIGNED_TO = "assignedTo"
    const val READ_STORAGE_PERMISSION_CODE = 1
    const val PICK_IMAGE_REQUEST_CODE = 2
    const val DOCUMENT_ID : String = "documentId"
    const val TASK_LIST : String = "taskList"
    const val BOARD_DETAIL: String = "board_detail"
    const val ID : String = "id"
    const val EMAIL : String = "email"
    const val TASK_LIST_ITEM_POSITION = "task_list_item_position"
    const val CARD_LIST_ITEM_POSITION = "card_list_item_position"
    const val BOARD_MEMBERS_LIST : String = "board_members_list"
    const val UN_SELECT : String = "UnSelect"
    const val SELECT : String = "Select"

    // These two functions are used without any change in different activities so we put it in constants

    // Function to choose image from our device after permission is granted
    fun showImageChooser(activity: Activity){
        // generic code
        var galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    // used in upload User image function to give it a name
    fun getFileExtension(uri : Uri?,activity:Activity) : String?{
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

}

