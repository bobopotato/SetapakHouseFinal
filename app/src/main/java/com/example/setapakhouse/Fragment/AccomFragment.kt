package com.example.setapakhouse.Fragment

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.setapakhouse.Adapter.HomeAdapter
import com.example.setapakhouse.Adapter.accomAdapter
import com.example.setapakhouse.Model.Property
import com.example.setapakhouse.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_detail_post.*
import kotlinx.android.synthetic.main.activity_detail_post.view.*
import kotlinx.android.synthetic.main.fragment_accom.*
import kotlinx.android.synthetic.main.fragment_accom.view.*
import kotlinx.android.synthetic.main.fragment_home.*

class AccomFragment : Fragment() {

    lateinit var ref: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root: View = inflater.inflate(R.layout.fragment_accom, container, false)

        val pref= PreferenceManager.getDefaultSharedPreferences(context)


        pref.apply{
            val pID=getString("PROPERTYID","")
            ref= FirebaseDatabase.getInstance().getReference("Property")
            ref.addValueEventListener(object: ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        for (h in snapshot.children){
                            if(h.child("propertyID").getValue().toString().equals(pID)) {
                                val accomCombine=h.child("accommodation").getValue().toString()
                                val accomSplit=accomCombine.split(",")
                                val gridLayoutManager= GridLayoutManager(context,2,GridLayoutManager.VERTICAL,false)
                                root.accomRecycler.layoutManager = gridLayoutManager
                                root.accomRecycler.adapter= accomAdapter(accomSplit)

                            }
                        }

                    }
                }

            })
        }




        return root
    }


}