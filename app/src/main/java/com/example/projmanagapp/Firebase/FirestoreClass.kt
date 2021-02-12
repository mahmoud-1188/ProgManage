package com.example.projmanagapp.Firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.projmanagapp.Activites.*
import com.example.projmanagapp.models.User
import com.example.projmanagapp.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.example.projmanagapp.models.Boardmodel
import com.google.firebase.firestore.FieldPath.documentId


// this class to handle firestore database stuff...
class FirestoreClass {

// create firebase fire store instance variable...
    private val mFirestore = FirebaseFirestore.getInstance()

     // function to create collection and document in database...
    fun registeruser (activity:SignupActivity, userinfo : User){

        mFirestore.collection(Constant.USERS)
            .document(getcurrentuserid())
            .set(userinfo, SetOptions.merge())
            .addOnCompleteListener {

            activity.Registerusersucces()

        }.addOnFailureListener {

            Log.e(activity.javaClass.simpleName,"error writing document")
        }
    }

     // function to get board list from database..
     fun getboardlist (activity:MainActivity){

         mFirestore.collection(Constant.BOARDS)
             .whereArrayContains(Constant.Assignedto,getcurrentuserid()) // search in document..
             .get()
             .addOnSuccessListener {
                 document ->

                 val boardlist :ArrayList<Boardmodel> = ArrayList()

                 for (i in document.documents){

                     val board = i.toObject(Boardmodel::class.java)
                         board!!.documentid = i.id // add document id to board..
                     boardlist.add(board)

                 }


                 activity.populateBoardlisttoui(boardlist) // populate recycle view ui method..

             }.addOnFailureListener {

                 activity.hideprogressdialog()
             }
     }

    // this method to get bord details in task activity...
     fun getBoarddetails(activity: task_list_Activity,documentid:String){

         mFirestore.collection(Constant.BOARDS)
             .document(documentid)
             .get()
             .addOnSuccessListener {
                     documentSnap ->
                 Log.i("idbord",documentSnap.toString())

                 val board = documentSnap.toObject(Boardmodel::class.java)!!

                 board.documentid = documentSnap.id

                 activity.getborddetails(board)

                 Log.i(activity.javaClass.simpleName,documentSnap.toString() )

             }.addOnFailureListener {

                 Exception ->
                 Log.i(activity.javaClass.simpleName,Exception.message.toString())
                 activity.hideprogressdialog()
             }
     }

    // add update board list method..
    fun addupdateboardlist(activity:Activity,board:Boardmodel){

        val tasklisthashmap = HashMap<String,Any>()
            tasklisthashmap[Constant.TASK_LIST] = board.taskList

        mFirestore.collection(Constant.BOARDS)
            .document(board.documentid)
            .update(tasklisthashmap)
            .addOnSuccessListener {

                Log.i(activity.javaClass.simpleName,"task updated successfully")

                if (activity is task_list_Activity) {
                    activity.addupdatetasklistsuccess()
                }

                else if(activity is Card_Detials_Activity){

                    activity.addupdatetasklistsuccess()

                }

            }.addOnFailureListener {
                 Exception ->

              if(activity is task_list_Activity) {

                  activity.hideprogressdialog()
                  Log.i(activity.javaClass.simpleName, "error while creating a board", Exception)
              }
                else if(activity is Card_Detials_Activity){

                  activity.hideprogressdialog()
                  Log.i(activity.javaClass.simpleName, "error while creating a board", Exception)
              }
            }
    }


     // method to create board in database..
     fun createBoard(activity:Board, board:Boardmodel){
         mFirestore.collection(Constant.BOARDS)
             .document()
             .set(board, SetOptions.merge())
             .addOnCompleteListener{

                 Log.i(activity.javaClass.simpleName,"Board created successfully")
                 Toast.makeText(activity,"Board created successfully",Toast.LENGTH_SHORT).show()

                 activity.createboardsuccefully()
             }.addOnFailureListener {

                 Exception ->

                 activity.hideprogressdialog()
                 Log.i(activity.javaClass.simpleName,"Error while creating Board",Exception)
                 Toast.makeText(activity,"Error while creating Board",Toast.LENGTH_SHORT).show()

             }
     }

     // this function to load assigned members..
    fun getmembers(activity:Activity,assignedto:List<String>){

        mFirestore.collection(Constant.USERS)
            .whereIn(documentId(),assignedto) // get assigned members by id...
            .get()
            .addOnSuccessListener {
                    documentSnap ->

                val user : ArrayList<User> = ArrayList()

                for (i in documentSnap){

                     user.add(i.toObject(User::class.java))
                }


                if (activity is member_Activity)
                activity.getmembers(user)

                else if (activity is task_list_Activity)
                    activity.boardmembersDetialslist(user)


                Log.i(activity.javaClass.simpleName,documentSnap.toString() )

            }.addOnFailureListener {

                    Exception ->
                Log.i(activity.javaClass.simpleName,Exception.message.toString())
                    if (activity is member_Activity)
                    activity.hideprogressdialog()

                    else if (activity is task_list_Activity)
                        activity.hideprogressdialog()
            }

    }

    fun getmemberDetials(activity: member_Activity,email:String){

        mFirestore.collection(Constant.USERS)
                .whereEqualTo(Constant.EMAIL,email)
                .get()
                .addOnSuccessListener {
                    document ->

                    if (document.documents.size > 0){

                        val user = document.documents[0].toObject(User::class.java)

                        activity.memberDetials(user!!)

                    }else{

                     activity.hideprogressdialog()
                     activity.showerrorsnackbar("No such member found")
                    }
                }.addOnFailureListener {
                    Exception ->

                    activity.hideprogressdialog()
                    Log.i(activity.javaClass.simpleName,"error while getting member Details",Exception)

                }
    }

    fun assignmembertoBoard(activity: member_Activity,board: Boardmodel,user: User){

        val assignHashmap = HashMap<String,Any>()
        assignHashmap[Constant.Assignedto] = board.assignedto

        mFirestore.collection(Constant.BOARDS)
                .document(board.documentid)
                .update(assignHashmap)
                .addOnSuccessListener {

                 activity.memberassignSuccess(user)
                }.addOnFailureListener {
                    e ->
                    activity.hideprogressdialog()
                    Log.i(activity.javaClass.simpleName,"error while updating bord",e)
                }
    }

     // function to read user info from collection..
     fun LouduserData(activity:Activity,readBoardlist:Boolean = false) {

         //get user document from fire store collection...
         mFirestore.collection(Constant.USERS).document(getcurrentuserid()).get()
             .addOnSuccessListener { document ->

                 val loggedinuser = document.toObject(User::class.java)

                 when (activity) {

                     is SigninActivity -> {
                         activity.signinusersucces(loggedinuser!!)
                     }
                     is MainActivity -> {

                         // add read board list variable to read board list one time..
                         activity.updateNavigationuserdetils(loggedinuser!! , readBoardlist)
                     }
                     is profileActivity ->{

                         activity.fillprofileitems(loggedinuser!!)
                     }

                 }
             }.addOnFailureListener {       // on failure function...

             when (activity) {

                 is SigninActivity -> {
                     activity.hideprogressdialog()
                 }
                 is MainActivity -> {

                     activity.hideprogressdialog()
                 }
             }
             Log.e(activity.javaClass.simpleName, "error reading document")

         }
     }

     // update user profile data...
     fun updateuserprofiledata(activity:Activity,userhashmap:HashMap<String,Any>){

         mFirestore.collection(Constant.USERS).document(getcurrentuserid()).update(userhashmap)
             .addOnSuccessListener {

                 Log.i(activity.javaClass.simpleName,"profile updated successfully")
                 Toast.makeText(activity,"profile updated successfully",Toast.LENGTH_SHORT).show()

                 when(activity){

                     is MainActivity ->{

                         activity.tockenupdatesuccess()
                     }

                     is profileActivity ->{

                         activity.profilupdatesucces()
                     }
                 }


             }.addOnFailureListener {
                 e ->

                 when(activity){

                     is MainActivity ->{

                         activity.hideprogressdialog()
                     }

                     is profileActivity ->{

                         activity.hideprogressdialog()
                     }
                 }

                 Log.i(activity.javaClass.simpleName,"error while creating bord",e)
                 Toast.makeText(activity,"error when updating profile",Toast.LENGTH_SHORT).show()
             }


             }

    // get current user id to use it as document name...
     fun getcurrentuserid():String{

        val currentuser = FirebaseAuth.getInstance().currentUser
        var currentuserid = ""

        if (currentuser != null){

            currentuserid = currentuser.uid
        }
         return currentuserid

     }
}