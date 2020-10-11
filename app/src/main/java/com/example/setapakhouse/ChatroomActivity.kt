package com.example.setapakhouse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.*

class ChatroomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatroom)

        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setTitle("Chatroom")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}