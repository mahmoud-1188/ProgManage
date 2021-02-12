package com.example.projmanagapp.Activites

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projmanagapp.Firebase.FirestoreClass
import com.example.projmanagapp.R
import com.example.projmanagapp.adapters.member_adapter
import com.example.projmanagapp.models.Boardmodel
import com.example.projmanagapp.models.User
import com.example.projmanagapp.utils.Constant
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_member_.*
import kotlinx.android.synthetic.main.add_member_dialoge.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class member_Activity : BaseActivity() {

    private lateinit var mBoarddetails :Boardmodel
    private lateinit var massignedemberlist :ArrayList<User>
    private var anychangemade :Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_)

        if (intent.hasExtra(Constant.BOARD_DETAILS)){

         mBoarddetails = intent.getParcelableExtra<Boardmodel>(Constant.BOARD_DETAILS)!!
        }

        settoolbar()

        showprogressdialog(resources.getString(R.string.dialog_progress))
       FirestoreClass().getmembers(this,mBoarddetails.assignedto)

    }

    private fun settoolbar(){

        setSupportActionBar(member_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_white_arrow_back)
        supportActionBar!!.title = "members"

        member_toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        if (anychangemade){

            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }


    // this function to prepare recycle view of members activity..
    fun getmembers(user:ArrayList<User>){

        massignedemberlist = user

        hideprogressdialog()
        rv_members.layoutManager = LinearLayoutManager(this)
        rv_members.setHasFixedSize(true)

        val adapter = member_adapter(this,user)

        rv_members.adapter = adapter


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.add_members_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            R.id.add_menu ->{

                alertdialog()
               return true
            }

        }

        return super.onOptionsItemSelected(item)
    }


    private fun alertdialog(){

        val dialog = Dialog(this)
            dialog.setContentView(R.layout.add_member_dialoge)
            dialog.add_member_dialog.setOnClickListener {

                val email = dialog.add_member_ed.text.toString()
                if (email.isNotEmpty()){
                    dialog.dismiss()
                    showprogressdialog(resources.getString(R.string.dialog_progress))
                    FirestoreClass().getmemberDetials(this@member_Activity,email)

                }else{

                    Toast.makeText(this@member_Activity,
                            "please enter member email address!",
                            Toast.LENGTH_SHORT).show()
                }

            }
            dialog.cancel_member_dialog.setOnClickListener {

                dialog.dismiss()
            }

        dialog.show()
    }

    fun memberDetials(user:User){

        mBoarddetails.assignedto.add(user.id)

        FirestoreClass().assignmembertoBoard(this,mBoarddetails,user)
    }

    fun memberassignSuccess(user: User){

        hideprogressdialog()

        anychangemade = true
        massignedemberlist.add(user)

        getmembers(massignedemberlist)

        sendnotificationtouserasynctask(mBoarddetails.name,user.fcmtoken).execute()
    }

    // this class to send notification...
    private inner class sendnotificationtouserasynctask(val boardname:String,val token:String):
        AsyncTask<Any, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            showprogressdialog(resources.getString(R.string.dialog_progress))

        }


        override fun doInBackground(vararg p0: Any?): String {

            var result : String

            var connection : HttpURLConnection? = null // create connection//

            try {
                val uri = URL(Constant.FCM_BASE_URI) // create URI//
                connection = uri.openConnection() as HttpURLConnection // assign the connection//
                connection.doOutput = true
                connection.doInput = true
                connection.instanceFollowRedirects = false
                connection.requestMethod = "POST"   // type of connection.

                // set property of connection..
                connection.setRequestProperty("Content-Type","application/json")
                connection.setRequestProperty("charset","utf-8")
                connection.setRequestProperty("Accept","application/json")

                connection.setRequestProperty(
                    Constant.FCM_AUTHORIZATION,"${Constant.FCM_KEY}=${Constant.FCM_SERVER_KEY}")

                connection.useCaches = false

                val wr = DataOutputStream(connection.outputStream) // create writer..

                val jsonrequest = JSONObject()
                val dataobject = JSONObject()

                // add title and message to data object//
                dataobject.put(Constant.FCM_KEY_TITLE,"Assigned to the board ${boardname}")
                dataobject.put(Constant.FCM_KEY_MESSAGE,
                    "you have been assigned to the board by ${massignedemberlist[0].name}")

                // add the data object and token to json request//
                jsonrequest.put(Constant.FCM_KEY_DATA,dataobject)
                jsonrequest.put(Constant.FCM_KEY_TO,token)

                wr.writeBytes(jsonrequest.toString())
                wr.flush()
                wr.close() // close writer..

                val httpresult = connection.responseCode

                // check if the connection is ok..
                if (httpresult == HttpURLConnection.HTTP_OK){

                    val inputstream  = connection.inputStream // create input stream ..

                    val reader = BufferedReader(InputStreamReader(inputstream)) // create reader..

                    var sb = StringBuilder()
                    var line :String?

                    try {

                        while (reader.readLine().also { line = it } != null){ // read from reader by line..

                            sb.append(line+"\n")
                        }
                    }catch (e:IOException){
                        e.printStackTrace()
                    }finally {

                        try {
                            inputstream.close() // close the input stream..

                        }catch (e:IOException){

                            e.printStackTrace()
                        }
                    }

                    result = sb.toString() // return the result //
                }else{
                    result = connection.responseMessage //  return connection response message as result if connection is not ok..
                }
            }catch (e:SocketTimeoutException){

                result = "connection timeout"  // connection time out..
            }catch (e:IOException){

                result = "ERROR" + e.message
            }finally {

                connection?.disconnect() // disconnect the connection//
            }

            return result

        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            hideprogressdialog()
        }

    }
}