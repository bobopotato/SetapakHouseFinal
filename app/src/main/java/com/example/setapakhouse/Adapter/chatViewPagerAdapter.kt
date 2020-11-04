package com.example.setapakhouse.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.setapakhouse.Fragment.AccomFragment
import com.example.setapakhouse.Fragment.PreFragment
import com.example.setapakhouse.Fragment.chatUserFragment
import com.example.setapakhouse.Fragment.chatroomFragment

class chatViewPagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        when(position){
            0->{return chatroomFragment()
            }
            1->{return chatUserFragment()
            }
            else -> {return chatroomFragment()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position){
            0->{return "Chat Room"}
            1->{return "User"}
        }
        return super.getPageTitle(position)
    }

    override fun getCount(): Int {
        return 2
    }
}