package com.example.projmanagapp.Activites

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.projmanagapp.Firebase.FirestoreClass
import com.example.projmanagapp.R
import com.example.projmanagapp.adapters.Board_items_adapter
import com.example.projmanagapp.models.Boardmodel
import com.example.projmanagapp.models.User
import com.example.projmanagapp.utils.Constant
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.main_content.*
import kotlinx.android.synthetic.main.nav_header_main.*


//activity..1

class MainActivity : BaseActivity(),NavigationView.OnNavigationItemSelectedListener {

    companion object{

        const val MY_PROFILE_REQUESTE_CODE = 10
        const val CREATE_BOARD_REQUSTE_CODE = 13
    }

    lateinit var musername :String
    lateinit var msharedpreferences :SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        msharedpreferences = this.getSharedPreferences(Constant.PROJMANAG_PREFERENCES, MODE_PRIVATE)

        val tokenupdated  = msharedpreferences.getBoolean(Constant.FCM_TOKEN_UPDATED,false)

        if (tokenupdated){ // check if token updated or not...

            showprogressdialog(resources.getString(R.string.dialog_progress))
            FirestoreClass().LouduserData(this,true)
        }else{  // get token from firebase add token to database...

            FirebaseInstanceId.getInstance().instanceId
                .addOnSuccessListener (this@MainActivity) { instanceresult->

                updatefcmtoken(instanceresult.token)
            }

        }

        showprogressdialog(resources.getString(R.string.dialog_progress))
        FirestoreClass().LouduserData(this,true)
        settoolbar()
        Nav_view.setNavigationItemSelectedListener(this)

        fab_main.setOnClickListener {

            val intent = Intent(this,Board::class.java)
              intent.putExtra(Constant.NAME,musername)
            startActivityForResult(intent, CREATE_BOARD_REQUSTE_CODE)
        }

    }

    // method to populate ui with board list...
    fun populateBoardlisttoui (boardarraylist:ArrayList<Boardmodel>){

        hideprogressdialog()

        if (boardarraylist.size > 0){

            rv_board_items.visibility = View.VISIBLE
            no_bord_items.visibility = View.GONE

            rv_board_items.layoutManager = LinearLayoutManager(this)
            rv_board_items.setHasFixedSize(true)

            val adapter = Board_items_adapter(this,boardarraylist)

            rv_board_items.adapter = adapter

            // set on click listener to recycle view items...
            adapter.setonclicklistener(object :Board_items_adapter.OnClickListener{
                override fun onclick(position: Int, model: Boardmodel) {

                    val intent = Intent(this@MainActivity,task_list_Activity::class.java)
                        intent.putExtra(Constant.DOCUMENT_ID,model.documentid)
                        startActivity(intent)

                }
            })


        }else{
            rv_board_items.visibility = View.GONE
            no_bord_items.visibility = View.VISIBLE
        }
    }

    // token updated successfully...
    fun tockenupdatesuccess(){

        hideprogressdialog()
        val editor :SharedPreferences.Editor = msharedpreferences.edit()
        editor.putBoolean(Constant.FCM_TOKEN_UPDATED,true)
        editor.apply()

        showprogressdialog(resources.getString(R.string.dialog_progress))
        FirestoreClass().LouduserData(this,true)
    }

    // add token to database...
    private fun updatefcmtoken (token:String){

        val userhashmap = HashMap<String,Any>()
        userhashmap[Constant.FCM_TOKEN] = token

        showprogressdialog(resources.getString(R.string.dialog_progress))
        FirestoreClass().updateuserprofiledata(this,userhashmap)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUESTE_CODE){

            showprogressdialog(resources.getString(R.string.dialog_progress))
            FirestoreClass().LouduserData(this)

        }else if (resultCode == Activity.RESULT_OK && requestCode == CREATE_BOARD_REQUSTE_CODE){

            FirestoreClass().getboardlist(this)

        }else{

            Log.e("Canceled","Canceled")
        }
    }

    // function to set user info to header image and name using Glide library...
    fun updateNavigationuserdetils(user:User,readBoardlist:Boolean){

        hideprogressdialog()

        musername = user.name
        Glide
            .with(this)
            .load(user.image)
            .fitCenter()
            .placeholder(R.drawable.ic_my_profile)
            .into(header_image)

        header_text.text = user.name

        if (readBoardlist){
            showprogressdialog(resources.getString(R.string.dialog_progress))

            FirestoreClass().getboardlist(this)
        }

    }

    private fun settoolbar (){

        setSupportActionBar(appbar_toolbar)
        appbar_toolbar.setNavigationIcon(R.drawable.ic_nav_menu)

        appbar_toolbar.setNavigationOnClickListener {
             toggleDrawer()
        }
    }

    // check drawer if it open, it will be closed. and if the drawer was close it will opened..
    private fun toggleDrawer (){

        if (drawer_layout.isDrawerOpen(GravityCompat.START)){

            drawer_layout.closeDrawer(GravityCompat.START)
        }else{

            drawer_layout.openDrawer(GravityCompat.START)
        }
    }

    // manage back button to close drawer if was open, and close program if the drawer was close..
    override fun onBackPressed() {

        if (drawer_layout.isDrawerOpen(GravityCompat.START)){

            drawer_layout.closeDrawer(GravityCompat.START)
        }else{

           doubleBackToExit()  // base activity..
        }

    }

    // override navigation listener to excute the selected item from navigation menu and close the drawer...
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            R.id.nav_my_profile ->{

               startActivityForResult(Intent(this,profileActivity::class.java),
                   MY_PROFILE_REQUESTE_CODE)
            }

            R.id.nav_sign_out ->{

                FirebaseAuth.getInstance().signOut()

                msharedpreferences.edit().clear().apply()

                val intent = Intent(this,IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}