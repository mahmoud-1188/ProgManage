package com.example.projmanagapp.Activites

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.projmanagapp.Firebase.FirestoreClass
import com.example.projmanagapp.R
import com.example.projmanagapp.models.Boardmodel
import com.example.projmanagapp.utils.Constant
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_board.*
import kotlinx.android.synthetic.main.activity_profile.*

class Board : BaseActivity() {

    private val PICK_IMAGE_FROM_Galary =5
    private var mBoardselectedimage:Uri? = null
    private val READ_STORAGE_REQUEST_CODE = 4
    private var mBoardimageuri = ""

    lateinit var musername:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        settoolbar()

            // set BOARD image..
             board_image.setOnClickListener {
            //check if the user have permission or not..
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.
                READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){

                choosingBoardimage()

            }else{    // ask the user for permission..
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_REQUEST_CODE
                )
            }
        }

        if (intent.hasExtra(Constant.NAME)){

            musername = intent.getStringExtra(Constant.NAME)!!
        }

        // create the board button...
        board_create_btn.setOnClickListener {

            if (mBoardselectedimage != null) {
                uploaduserimage()
            }else{

                showprogressdialog(resources.getString(R.string.dialog_progress))
                createBoard()
            }
        }
    }

    fun createboardsuccefully(){

        hideprogressdialog()

        setResult(Activity.RESULT_OK)
        finish()
    }

    // this function to upload user image to fire store storage...
    private fun uploaduserimage(){

        // show dialog..
       showprogressdialog(resources.getString(R.string.dialog_progress))

        // check uri variable is not null...
        if (mBoardselectedimage != null){

            // make reference in firebase storage..
            val sref : StorageReference =
                FirebaseStorage.getInstance().reference.child(
                    "IMAGE_NAME"+System.currentTimeMillis()+
                            "."+Constant.getimageExtention(mBoardselectedimage,this))

            // put file in firebase storage ..
            sref.putFile(mBoardselectedimage!!).addOnSuccessListener {

                    TaskSnapshot ->

                // get download uri reference...
                TaskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                      Uri ->

                    // put uri in variable..
                    mBoardimageuri = Uri.toString()

                    Log.i("Download image uri",mBoardimageuri)

                    //create the BOARD method ...
                    createBoard()
                }
            }.addOnFailureListener {
                    exeption ->

                Toast.makeText(this,exeption.message,Toast.LENGTH_SHORT).show()

                // hide dialog ..
                hideprogressdialog()
            }
        }
    }

    // function to create board...
    private fun createBoard(){

        val name = board_text_name.text.toString()

           if ( validatname(name)) {

               val image = mBoardimageuri
               val createdby = musername
               val list = getcurrentuserid()
               val assignto: ArrayList<String> = ArrayList()
               assignto.add(list)

               val board = Boardmodel(name, image, createdby, assignto)

               FirestoreClass().createBoard(this, board)
           }

    }


    // on request permission result function. check for user request respond..
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == READ_STORAGE_REQUEST_CODE) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                choosingBoardimage()  // if we have the right permission we execute choose image function..
            }
        }else{  // this toast appear if the user denied the permission...
            Toast.makeText(this,"oops, you just denied the permission for storage," +
                    " you can also allow it from setting", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_FROM_Galary){

            //receive image uri.. and store in variable..
            mBoardselectedimage = data!!.data

            // use glide library to set profile image..
            Glide
                .with(this)
                .load(mBoardselectedimage)
                .circleCrop()
                .placeholder(R.drawable.ic_my_profile)
                .into(board_image)

        }
    }
    // this function to pick an image from galary..
    private fun choosingBoardimage() {

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_FROM_Galary)

    }

    private fun settoolbar(){

        setSupportActionBar(board_toolbar)

        supportActionBar!!.title = "create Board"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_white_arrow_back)

        board_toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    // function to validate the name is not empty...
    private fun validatname (name:String):Boolean{

        return when{

            TextUtils.isEmpty(name) ->{

                showerrorsnackbar("please enter the name")
                false
            }else ->{

                true
            }
        }
    }
}