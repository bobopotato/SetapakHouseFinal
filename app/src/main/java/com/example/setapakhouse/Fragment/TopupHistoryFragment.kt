package com.example.setapakhouse.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.setapakhouse.Adapter.HomeAdapter
import com.example.setapakhouse.Adapter.topupAdapter
import com.example.setapakhouse.Model.Topup
import com.example.setapakhouse.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_topup_history.view.*


class TopupHistoryFragment : Fragment() {
    lateinit var ref:DatabaseReference
    val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
    lateinit var topupList:MutableList<Topup>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root:View= inflater.inflate(R.layout.fragment_topup_history, container, false)

        topupList= mutableListOf()
        ref=FirebaseDatabase.getInstance().getReference("Topup")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    topupList.clear()
                    for(h in snapshot.children){
                        if(h.child("userID").getValue().toString().equals(currentUserID)){
                            val topup=h.getValue(Topup::class.java)
                            topupList.add(topup!!)
                            val mLayoutManager = LinearLayoutManager(context)
                            mLayoutManager.reverseLayout = true
                            root.topupRecycle.layoutManager = mLayoutManager
                            root.topupRecycle.adapter = topupAdapter(topupList)
                        }
                    }
                }
            }

        })

        return root
    }


}