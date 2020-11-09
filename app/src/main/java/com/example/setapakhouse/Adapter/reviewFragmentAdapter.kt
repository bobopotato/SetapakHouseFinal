package com.example.setapakhouse.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.setapakhouse.Fragment.TopupHistoryFragment
import com.example.setapakhouse.Fragment.completedReviewFragment
import com.example.setapakhouse.Fragment.pendingReviewFragment
import com.example.setapakhouse.Fragment.topupFragment

class reviewFragmentAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return completedReviewFragment()
            }
            1 -> {
                return pendingReviewFragment()
            }
            else -> {
                return completedReviewFragment()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> {
                return "My Review"
            }
            1 -> {
                return "Pending Review"
            }
        }
        return super.getPageTitle(position)
    }

    override fun getCount(): Int {
        return 2
    }
}