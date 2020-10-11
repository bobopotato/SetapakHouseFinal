package com.example.setapakhouse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.*

class AboutUsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setTitle("About Us")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}