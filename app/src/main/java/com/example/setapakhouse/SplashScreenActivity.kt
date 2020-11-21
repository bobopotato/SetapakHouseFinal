package com.example.setapakhouse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val pref= PreferenceManager.getDefaultSharedPreferences(this)
        val editor=pref.edit()
        editor
            .putBoolean("firstTime",true)
            .apply()

        splashIcon.alpha=0f
        splashIcon.animate().setDuration(4000).alpha(1f).withEndAction({
            val i= Intent(this,MainActivity::class.java)
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
        })
    }
}
