package com.example.setapakhouse.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.setapakhouse.Adapter.ApprovalAdapter
import com.example.setapakhouse.Adapter.NotificationAdapter
import com.example.setapakhouse.Model.Approval
import com.example.setapakhouse.Model.Notification
import com.example.setapakhouse.Model.Property
import com.example.setapakhouse.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_notification2.*
import kotlinx.android.synthetic.main.fragment_notification2.view.*


class ApprovalFragment : Fragment() {

    lateinit var ref: DatabaseReference
    lateinit var ref2: DatabaseReference
    lateinit var ref3: DatabaseReference
    lateinit var approvalList : MutableList<Approval>
    lateinit var propertyList : MutableList<Property>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root: View = inflater.inflate(R.layout.fragment_notification2, container, false)

        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid

        var clearList = false

        approvalList = mutableListOf()
        propertyList = mutableListOf()

        ref= FirebaseDatabase.getInstance().getReference("Approval")
        ref2 = FirebaseDatabase.getInstance().getReference("Property")


        /*ref2.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    approvalList.clear()
                    for (h in snapshot.children) {
                        val property = h.getValue(Property::class.java)

                        if(property!!.userID==currentUserID){

                            ref.addValueEventListener(object: ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                                override fun onDataChange(snapshot1: DataSnapshot) {
                                    if (snapshot1.exists()) {
                                        for (j in snapshot1.children) {
                                            val approval = j.getValue(Approval::class.java)

                                            if(approval!!.propertyID==property.propertyID){

                                                approvalList.add(approval)
                                            }
                                        }
                                    }
                                    val adapter = ApprovalAdapter(approvalList)
                                    val mLayoutManager = LinearLayoutManager(activity)
                                    mLayoutManager.reverseLayout = true
                                    root.notificationRecyclerView.layoutManager = mLayoutManager

                                    root.notificationRecyclerView.scrollToPosition(approvalList.size-1)
                                    root.notificationRecyclerView.adapter = adapter

                                }

                                })


                        }


                    }

                }
            }
        })*/

        ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    approvalList.clear()
                    for (h in snapshot.children){
                        ref2 = FirebaseDatabase.getInstance().getReference("Property")

                        ref2.addValueEventListener(object: ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                            override fun onDataChange(snapshot1: DataSnapshot) {
                                if (snapshot1.exists()) {
                                    for (j in snapshot1.children) {
                                        val property = j.getValue(Property::class.java)
                                        if(property!!.propertyID.equals(h.child("propertyID").getValue().toString()) && property.userID == currentUserID){
                                            val approval = h.getValue(Approval::class.java)

                                            approvalList.add(approval!!)

                                            //Log.d("abcdc", "zzz fail")
                                        }
                                    }

                                }
                                val adapter = ApprovalAdapter(approvalList)
                                val mLayoutManager = LinearLayoutManager(activity)
                                mLayoutManager.reverseLayout = true
                                root.notificationRecyclerView.layoutManager = mLayoutManager

                                root.notificationRecyclerView.scrollToPosition(approvalList.size-1)
                                root.notificationRecyclerView.adapter = adapter

                            }

                        })
                    }

                }
            }

        })


        return root
    }

}