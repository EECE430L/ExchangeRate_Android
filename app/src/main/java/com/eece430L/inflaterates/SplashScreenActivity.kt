package com.eece430L.inflaterates

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

//https://youtu.be/WZWr0Abomfw
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        // render the splash screen
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // start the main activity after 1 s
        val rootLayout = findViewById<View>(android.R.id.content)
        rootLayout.postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 1000)
    }
}