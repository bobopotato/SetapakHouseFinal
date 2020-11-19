package com.example.setapakhouse

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import com.example.setapakhouse.Adapter.chatViewPagerAdapter
import com.google.android.material.tabs.TabLayout
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


        toolbar.setOnClickListener{
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)

        }
        tabLayoutChat.addTab(tabLayoutChat.newTab().setText("Chat Room"))
        tabLayoutChat.addTab(tabLayoutChat.newTab().setText("User"))
        viewPagerChat.adapter= chatViewPagerAdapter(supportFragmentManager)
        viewPagerChat.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayoutChat))
        tabLayoutChat.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
                val focus = getCurrentFocus()
                if(focus!=null) {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(focus!!.windowToken, 0)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPagerChat.currentItem=tab!!.position
                val focus = getCurrentFocus()
                if(focus!=null) {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(focus!!.windowToken, 0)
                }
            }

        })
        //tabLayoutChat.setupWithViewPager(viewPagerChat)

    }
}