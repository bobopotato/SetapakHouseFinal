package com.example.setapakhouse.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.setapakhouse.Fragment.HouseRentingFragment
import com.example.setapakhouse.Fragment.HouseRentingHistory
import com.example.setapakhouse.Fragment.incomeFragment
import com.example.setapakhouse.Fragment.outgoingFragment

class rentingHouseFragmentAdapter (fm: FragmentManager): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return HouseRentingFragment()
            }
            1 -> {
                return HouseRentingHistory()
            }
            else -> {
                return HouseRentingFragment()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> {
                return "House Renting"
            }
            1 -> {
                return "Renting History"
            }
        }
        return super.getPageTitle(position)
    }

    override fun getCount(): Int {
        return 2
    }
}