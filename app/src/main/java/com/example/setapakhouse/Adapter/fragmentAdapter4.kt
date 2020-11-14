package com.example.setapakhouse.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.setapakhouse.Fragment.TopupHistoryFragment
import com.example.setapakhouse.Fragment.incomeFragment
import com.example.setapakhouse.Fragment.outgoingFragment
import com.example.setapakhouse.Fragment.topupFragment

class fragmentAdapter4 (fm: FragmentManager): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return incomeFragment()
            }
            1 -> {
                return outgoingFragment()
            }
            else -> {
                return incomeFragment()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> {
                return "INCOME"
            }
            1 -> {
                return "OUTGOING"
            }
        }
        return super.getPageTitle(position)
    }

    override fun getCount(): Int {
        return 2
    }
}