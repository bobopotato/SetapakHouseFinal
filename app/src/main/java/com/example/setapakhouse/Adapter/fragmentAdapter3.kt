package com.example.setapakhouse.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.setapakhouse.Fragment.AccomFragment
import com.example.setapakhouse.Fragment.PreFragment
import com.example.setapakhouse.Fragment.TopupHistoryFragment
import com.example.setapakhouse.Fragment.topupFragment

class fragmentAdapter3(fm: FragmentManager): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return topupFragment()
            }
            1 -> {
                return TopupHistoryFragment()
            }
            else -> {
                return topupFragment()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> {
                return "Top-Up"
            }
            1 -> {
                return "Top-Up History"
            }
        }
        return super.getPageTitle(position)
    }

    override fun getCount(): Int {
        return 2
    }
}