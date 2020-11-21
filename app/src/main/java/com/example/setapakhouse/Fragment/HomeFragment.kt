package com.example.setapakhouse.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.setapakhouse.Adapter.HomeAdapter
import com.example.setapakhouse.Model.Property
import com.example.setapakhouse.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment() {

    lateinit var ref : DatabaseReference
    lateinit var propertyList : MutableList<Property>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.fragment_home, container, false)


        propertyList = mutableListOf()

        addToList(root)




        return root
    }


    private fun addToList(root:View){
        ref = FirebaseDatabase.getInstance().getReference("Property")
        ref.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    propertyList.clear()
                    for (h in snapshot.children){
                        if(h.child("status").getValue().toString().equals("available")) {
                            val property = h.getValue(Property::class.java)
                            propertyList.add(property!!)
                            //Log.d(tag,propertyList.toString())
                        }

                    }

                    val mLayoutManager = LinearLayoutManager(context)
                    mLayoutManager.reverseLayout = true

                    root.recycler_view.layoutManager = mLayoutManager
                    root.recycler_view.scrollToPosition(propertyList.size-1)
                    root.recycler_view.adapter = HomeAdapter(propertyList, true)
                }
            }

        })

    }

}