package com.example.projmanagapp.Activites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.example.projmanagapp.Firebase.FirestoreClass
import com.example.projmanagapp.R
import com.example.projmanagapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_signin.*

//activity..5
class SigninActivity : BaseActivity() {

    lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        // to make activity full screen..
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        auth = Firebase.auth

        signin_btn.setOnClickListener { signin() }

        settoolbar()
    }
   // function to setup the toolbar...
    private fun settoolbar(){

        setSupportActionBar(signin_toolbar)
        val toolbar = supportActionBar

        if (toolbar !=null){

            toolbar.title = "SIGN IN"
            toolbar.setDisplayHomeAsUpEnabled(true)
            toolbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

            signin_toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

   fun signinusersucces (user:User){
       hideprogressdialog()

       val intent = Intent(this,MainActivity::class.java)
           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
       startActivity(intent)

       finish()

   }

    // function to sign in the user...
    private fun signin(){

        val email = signin_text_email.text.toString().trim{ it <= ' '}
        val password = signin_text_password.text.toString().trim{ it <= ' '}

        if (valditeform(email,password)){

            showprogressdialog(resources.getString(R.string.dialog_progress))
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {

                task ->

                if (task.isSuccessful){

                    //get user info from firestore...
                    FirestoreClass().LouduserData(this)

                }else{
                    hideprogressdialog()
                    Toast.makeText(this,"authentication Failed",Toast.LENGTH_SHORT).show()


                }
            }
        }

    }
    // function to validate the form if empty...
    private fun valditeform (email:String, password:String):Boolean {
        return when {

            TextUtils.isEmpty(email) -> {
                showerrorsnackbar("please enter a email")
                false
            }
            TextUtils.isEmpty(password) -> {
                showerrorsnackbar("please enter a password")
                false
            }
            else -> {
                true
            }
        }
    }
}