package com.example.setapakhouse.Fragment

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.ImageSlider
import com.example.setapakhouse.Adapter.fragmentAdapter
import com.example.setapakhouse.Adapter.fragmentAdapter2
import com.example.setapakhouse.Adapter.preAdapter
import com.example.setapakhouse.Adapter.rentingHouseFragmentAdapter
import com.example.setapakhouse.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_notification.*


class NotificationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_notification, container, false)

        //val abc = findViewById<ViewPager>(R.id.detailViewPager)


        //root.detailViewPager123.adapter= fragmentAdapter(getFragmentManager()!!)
        //root.detailsTabLayout123.setupWithViewPager(detailViewPager123)


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //Toast.makeText(context, "abc zzz", Toast.LENGTH_SHORT).show()
        detailViewPager123.adapter= fragmentAdapter2(getChildFragmentManager())
        detailsTabLayout123.setupWithViewPager(detailViewPager123)

        val pref= PreferenceManager.getDefaultSharedPreferences(context)

        pref.apply{
            val changeTabMou=getString("changeTab","")
            if(changeTabMou=="approval"){
                detailsTabLayout123.getTabAt(1)!!.select()
                val pref= PreferenceManager.getDefaultSharedPreferences(context)
                val editor=pref.edit()
                editor
                    .putString("changeTab","")
                    .apply()
            }else{
                detailsTabLayout123.getTabAt(0)!!.select()
            }

            //Log.d("changeTapapappa", "abc = " + changeTabMou)

        }


    }


}