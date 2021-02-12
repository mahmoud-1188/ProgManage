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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_signup.*

//activity..4
class SignupActivity :BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        settoolbar()

        signup_btn.setOnClickListener {

            registeruser()
        }

    }

    private fun settoolbar(){

        setSupportActionBar(signup_toolbar)
        val toolbar = supportActionBar

        if (toolbar !=null){

            toolbar.title = "SIGN UP"
            toolbar.setDisplayHomeAsUpEnabled(true)
            toolbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

            signup_toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    // register user succes function...firestore class.
    fun Registerusersucces (){

        Toast.makeText(this,"you register successfully",Toast.LENGTH_SHORT).show()
        hideprogressdialog()

    }

    // function to register a user...
    private fun registeruser (){

        val name = text_name.text.toString().trim { it <= ' ' }
        val email = text_email.text.toString().trim { it <= ' ' }
        val password = text_password.text.toString().trim { it <= ' ' }

        if (valditeform(name,email,password)){

            showprogressdialog(resources.getString(R.string.dialog_progress))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener {

             taske ->


                 if (taske.isSuccessful){

                    val firebaseuser : FirebaseUser? = taske.result!!.user
                    val registeremail = firebaseuser!!.email

                    val user = User(firebaseuser.uid,name,registeremail!!)

                     // function to register user info in database...
                    FirestoreClass().registeruser(this,user)

                  startActivity(Intent(this,IntroActivity::class.java))

                }else{
                    hideprogressdialog()
                    Toast.makeText(this,"Sign up Failed",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // function to validate the form if empty...
    private fun valditeform (name :String, email:String, password:String):Boolean{
        return when{

            TextUtils.isEmpty(name) ->{
                showerrorsnackbar("please enter a name")
                false
            }
            TextUtils.isEmpty(email) ->{
                showerrorsnackbar("please enter a email")
                false
            }
            TextUtils.isEmpty(password) ->{
                showerrorsnackbar("please enter a password")
                false
            }else ->{
                true
            }
        }
    }
}