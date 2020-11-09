package com.example.setapakhouse.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.setapakhouse.Fragment.ApprovalFragment
import com.example.setapakhouse.Fragment.NotificationFragment2


class fragmentAdapter2(fm: FragmentManager):FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        when(position){
            0->{return NotificationFragment2()}
            1->{return ApprovalFragment()}
            else -> {return NotificationFragment2()}
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position){
            0->{return "Notification"}
            1->{return "Approval"}
        }
        return super.getPageTitle(position)
    }

    override fun getCount(): Int {
        return 2
    }
}