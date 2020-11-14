package com.example.setapakhouse.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.setapakhouse.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class HousekeepingFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root:View= inflater.inflate(R.layout.fragment_housekeeping, container, false)
        return root
    }

}