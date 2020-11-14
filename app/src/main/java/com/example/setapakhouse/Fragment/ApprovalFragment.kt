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
import com.example.setapakhouse.Model.Property
import com.example.setapakhouse.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_approval.*
import kotlinx.android.synthetic.main.fragment_approval.view.*
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
        val root1: View = inflater.inflate(R.layout.fragment_notification2, container, false)
        val root2: View = inflater.inflate(R.layout.fragment_approval, container, false)

        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid

        var clearList = false

        approvalList = mutableListOf()
        propertyList = mutableListOf()

        ref= FirebaseDatabase.getInstance().getReference("Approval")
        //ref2 = FirebaseDatabase.getInstance().getReference("Property")


        ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    approvalList.clear()
                    for (h in snapshot.children) {
                        val targetApproval = h.getValue(Approval::class.java)
                        if (targetApproval!!.receiverID == currentUserID) {
                            approvalList.add(targetApproval)

                            //root2.hiddenCount1.text = store2.toString()
                            //Log.d("thisCount","abc = " + store2.toString())
                            //Log.d("abcdc", "wtf1 = " + root2.hiddenCount.text)
                            //Log.d("abcdc", "wtf2 = " + root2.hiddenCount1.text)
                        }
                    }
                }
                val adapter = ApprovalAdapter(approvalList)
                val mLayoutManager = LinearLayoutManager(activity)
                mLayoutManager.reverseLayout = true
                root1.notificationRecyclerView.layoutManager = mLayoutManager
                root1.notificationRecyclerView.scrollToPosition(approvalList.size - 1)
                root1.notificationRecyclerView.adapter = adapter

                //store++
            }
        })


            /*ref.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        approvalList.clear()
                        for (h in snapshot.children) {
                            ref2 = FirebaseDatabase.getInstance().getReference("Property")

                            ref2.addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                                override fun onDataChange(snapshot1: DataSnapshot) {
                                        if (snapshot1.exists()) {
                                            for (j in snapshot1.children) {
                                                val property = j.getValue(Property::class.java)
                                                if (property!!.propertyID.equals(
                                                        h.child("propertyID").getValue().toString()
                                                    ) && property.userID == currentUserID
                                                ) {
                                                    store++
                                                    root2.hiddenCount.text = store.toString()
                                                    Log.d("thisCount", "wtf = " + store1)
                                                }
                                            }

                                        }



                                }
                            })
                        }

                    }
                }
           })*/



        //var store2 = 0

        /*ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    approvalList.clear()
                    for (h in snapshot.children) {
                        ref2 = FirebaseDatabase.getInstance().getReference("Property")

                        ref2.addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                            override fun onDataChange(snapshot1: DataSnapshot) {
                                if (snapshot1.exists()) {
                                    for (j in snapshot1.children) {
                                        val property = j.getValue(Property::class.java)
                                        if (property!!.propertyID.equals(
                                                h.child("propertyID").getValue().toString()
                                            ) && property.userID == currentUserID
                                        ) {
                                            val approval = h.getValue(Approval::class.java)
                                            if (root2.hiddenCount1.text.toString()
                                                    .toInt() <= root2.hiddenCount.text.toString()
                                                    .toInt()
                                            ) {
                                                approvalList.add(approval!!)
                                                store2++
                                                root2.hiddenCount1.text = store2.toString()
                                                //Log.d("thisCount","abc = " + store2.toString())
                                                Log.d("abcdc", "wtf1 = " + root2.hiddenCount.text)
                                                Log.d("abcdc", "wtf2 = " + root2.hiddenCount1.text)
                                            }

                                        }

                                    }
                                }
                                val adapter = ApprovalAdapter(approvalList)
                                val mLayoutManager = LinearLayoutManager(activity)
                                mLayoutManager.reverseLayout = true
                                root1.notificationRecyclerView.layoutManager = mLayoutManager
                                root1.notificationRecyclerView.scrollToPosition(approvalList.size - 1)
                                root1.notificationRecyclerView.adapter = adapter

                                //store++
                            }
                        })
                    }

                }
            }
        })*/


        return root1
    }

}