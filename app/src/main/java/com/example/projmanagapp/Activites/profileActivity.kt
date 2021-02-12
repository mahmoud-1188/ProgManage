package com.example.projmanagapp.Activites

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.projmanagapp.Firebase.FirestoreClass
import com.example.projmanagapp.R
import com.example.projmanagapp.models.User
import com.example.projmanagapp.utils.Constant
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_profile.*

class profileActivity : BaseActivity() {

    private var mselectedimageUri :Uri? = null
    private var mprofileimageuri = ""
    lateinit var muserdetails :User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // set profile image..
        profile_image.setOnClickListener {
             //check if the user have permission or not..
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.
                    READ_EXTERNAL_STORAGE)
                    ==PackageManager.PERMISSION_GRANTED){

                choosingprofileimage()

            }else{    // ask the user for permission..
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        READE_STORAGE_REQEUST_CODE)

            }
        }

        profil_update_btn.setOnClickListener {
          if (mselectedimageUri != null){
              uploaduserimage()
          }else{

              showprogressdialog(resources.getString(R.string.dialog_progress))
              updateuserprofiledata()
          }

        }

        FirestoreClass().LouduserData(this)
        settoolbar()
    }

    // on create request permission result function. check for user request respond..
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == READE_STORAGE_REQEUST_CODE) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                choosingprofileimage()  // if we have the right permission we execute choose image function..
            }
        }else{  // this toast appear if the user denied the permission...
            Toast.makeText(this,"oops, you just denied the permission for storage," +
                    " you can also allow it from setting",Toast.LENGTH_SHORT).show()
        }
    }


    // this function to receive data from intent.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == PICK_FROM_Galary && resultCode == Activity.RESULT_OK && data!!.data != null){

                mselectedimageUri = data.data   //receive image uri.. and store in variable..

                // use glide library to set profile image..
                Glide
                        .with(this)
                        .load(mselectedimageUri)
                        .circleCrop()
                        .placeholder(R.drawable.ic_my_profile)
                        .into(profile_image)
            }
    }

    private fun settoolbar(){

        setSupportActionBar(profile_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_white_arrow_back)
        supportActionBar!!.title = resources.getString(R.string.profile_toolbar_title)

        profile_toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    // this function to fill profile details..
     fun fillprofileitems(user:User){

        //
        muserdetails = user
        // use glide library to set profile image from main activity to profile activity...
         Glide
             .with(this)
             .load(user.image)
             .circleCrop()
             .placeholder(R.drawable.ic_my_profile)
             .into(profile_image)

         profile_text_name.setText(user.name)
         profile_text_email.setText(user.email)

         if (user.mobile != 0L) {
             profile_text_mobile.setText(user.mobile.toString())
         }

     }

    // this function to pick an image from galary..
    private fun choosingprofileimage() {

        val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_FROM_Galary)

    }

    fun profilupdatesucces(){

        hideprogressdialog()

        // set result ok to update header items..
        setResult(Activity.RESULT_OK)
        finish()
    }

    // this function to update user profile data..firebase storage..
    private fun updateuserprofiledata(){


        val hashmap = HashMap<String,Any>()  // create hash map variable..

        if (mprofileimageuri.isNotEmpty() && mprofileimageuri != muserdetails.image){
            hashmap[Constant.IMAGE] = mprofileimageuri
        }
        if (profile_text_name.text.toString() != muserdetails.name){
            hashmap[Constant.NAME] = profile_text_name.text.toString()
            hashmap[Constant.CREATED_BY] = profile_text_name.text.toString()
        }
        if (profile_text_mobile.text.toString() != muserdetails.mobile.toString()){
            hashmap[Constant.MOBILE] = profile_text_mobile.text.toString().toLong()
        }

        // call firebase class  ..
        FirestoreClass().updateuserprofiledata(this,hashmap)

    }
    // this function to upload user image to fire store storage...
    private fun uploaduserimage(){

        // show dialog..
        showprogressdialog(resources.getString(R.string.dialog_progress))

        // check uri variable is not null...
        if (mselectedimageUri != null){

            // make reference in firebase storage..
         val sref :StorageReference =
               FirebaseStorage.getInstance().reference.child(
              "IMAGE_NAME"+System.currentTimeMillis()+
              "."+Constant.getimageExtention(mselectedimageUri,this))

            // put file in firebase storage ..
            sref.putFile(mselectedimageUri!!).addOnSuccessListener {

                TaskSnapshot ->
                Log.i("firebase image uri",
                TaskSnapshot.metadata!!.reference!!.downloadUrl.toString())

                // get download uri reference...
                TaskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {

                    Uri ->

                    Log.i("Download image uri",Uri.toString())

                    // put uri in variable..
                    mprofileimageuri = Uri.toString()

                    //update user profile data..
                    updateuserprofiledata()
                }
            }.addOnFailureListener {

                exeption ->

                Toast.makeText(this,exeption.message,Toast.LENGTH_SHORT).show()

                // hide dialog ..
                hideprogressdialog()

            }

        }
    }


  // constant variables ...
    companion object {
        private const val PICK_FROM_Galary = 1  // request variable for galary intent..
        private const val READE_STORAGE_REQEUST_CODE = 2 // variable for request permission for galary..
    }
}