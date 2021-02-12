package com.example.projmanagapp.Activites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.example.projmanagapp.R
import kotlinx.android.synthetic.main.activity_intro.*
import kotlinx.android.synthetic.main.activity_signup.*

//activity..2
class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)


        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        signup_btn_intro.setOnClickListener {

            startActivity(Intent(this, SignupActivity::class.java))
        }

        signin_btn.setOnClickListener {

            startActivity(Intent(this, SigninActivity::class.java))
        }
    }
}