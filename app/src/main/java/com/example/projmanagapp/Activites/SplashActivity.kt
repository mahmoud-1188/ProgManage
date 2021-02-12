                                                                       package com.example.projmanagapp.Activites

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.projmanagapp.Firebase.FirestoreClass
import com.example.projmanagapp.R
import kotlinx.android.synthetic.main.activity_splash.*

//activity..3
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

                    window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                    WindowManager.LayoutParams.FLAG_FULLSCREEN)


        // prepare type face to splash title..
        val typeface :Typeface = Typeface.createFromAsset(assets,"carbon.black.ttf")
        splash_txt.typeface = typeface

        // handler to delay splash activity & chick if user logged in or not...
        Handler().postDelayed({

            val currentuserid = FirestoreClass().getcurrentuserid()

            if (currentuserid.isNotEmpty()){
                startActivity(Intent(this, MainActivity::class.java))
            }else{
                startActivity(Intent(this, IntroActivity::class.java))
            }

            finish()
        },2500)
    }
}