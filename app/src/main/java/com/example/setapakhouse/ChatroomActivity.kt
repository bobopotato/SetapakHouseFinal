package com.example.setapakhouse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.setapakhouse.Adapter.chatViewPagerAdapter
import kotlinx.android.synthetic.main.activity_chatroom.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.toolbar

class ChatroomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatroom)



        chatBackBtn.setOnClickListener {
            finish()
        }
        viewPagerChat.adapter= chatViewPagerAdapter(supportFragmentManager)
        tabLayoutChat.setupWithViewPager(viewPagerChat)
    }
}