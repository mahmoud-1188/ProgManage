package com.example.projmanagapp.utils

import android.app.Activity
import android.net.Uri
import android.webkit.MimeTypeMap

object Constant {

       const val USERS = "users"

       const val IMAGE = "image"
       const val NAME = "name"
       const val MOBILE = "mobile"
       const val BOARDS = "Boards"
       const val Assignedto = "assignedto"
       const val CREATED_BY ="createdby"
       const val DOCUMENT_ID = "document id"
       const val TASK_LIST = "taskList"
       const val BOARD_DETAILS = "board details"
       const val EMAIL = "email"
       const val TASK_LIST_POSITION = "task list position"
       const val CARD_POSITION = "card position"
       const val BORD_MEMBERS_LIST = "bord member list"
       const val SELECT :String = "select"
       const val UN_SELECT :String = "un select"

       const val PROJMANAG_PREFERENCES = "projmanagprefs"
       const val FCM_TOKEN_UPDATED = "token updated"
       const val FCM_TOKEN = "fcmtoken"

       // notification constant....
       const val FCM_BASE_URI :String = "https://fcm.googleapis.com/fcm/send"
       const val FCM_AUTHORIZATION :String = "authorization"
       const val FCM_KEY :String = "key"
       const val FCM_SERVER_KEY :String = "AAAAno0ZS8A:APA91bH9ENHthzetZZ-kepLsgTCZh4gwt3gpeEncucSey7YP_DF-crTQOeE-Dy9Wofg6koo1NB7DyTno37V5p0E57Gzb-pwM2aqLJNR1lFhUYRsv3oj0vOXlONem-h4z20KFDvavr2kx"
       const val FCM_KEY_TITLE :String = "title"
       const val FCM_KEY_MESSAGE :String = "message"
       const val FCM_KEY_DATA :String = "data"
       const val FCM_KEY_TO :String = "to"


       // this function to return file Extention type...
        fun getimageExtention(uri: Uri?,activity:Activity): String? {

              return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
       }
}
