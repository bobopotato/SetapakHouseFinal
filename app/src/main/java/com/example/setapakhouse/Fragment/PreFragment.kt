package com.example.setapakhouse.Fragment

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.setapakhouse.Adapter.accomAdapter
import com.example.setapakhouse.Adapter.preAdapter
import com.example.setapakhouse.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_accom.view.*
import kotlinx.android.synthetic.main.fragment_pre.view.*


class PreFragment : Fragment() {

    lateinit var ref: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root: View = inflater.inflate(R.layout.fragment_pre, container, false)

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
                                val preCombine=h.child("preference").getValue().toString()
                                val preSplit=preCombine.split(",")
                                val gridLayoutManager= GridLayoutManager(context,2,GridLayoutManager.VERTICAL,false)
                                root.preRecycler.layoutManager = gridLayoutManager
                                root.preRecycler.adapter= preAdapter(preSplit)

                            }
                        }

                    }
                }

            })
        }

        return root
    }
}