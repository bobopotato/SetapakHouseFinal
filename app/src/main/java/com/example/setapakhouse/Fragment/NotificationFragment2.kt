package com.example.setapakhouse.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.setapakhouse.Adapter.NotificationAdapter
import com.example.setapakhouse.Adapter.accomAdapter
import com.example.setapakhouse.Model.Notification
import com.example.setapakhouse.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_accom.view.*
import kotlinx.android.synthetic.main.fragment_notification2.*
import kotlinx.android.synthetic.main.fragment_notification2.view.*

class NotificationFragment2 : Fragment() {

    lateinit var ref: DatabaseReference
    lateinit var ref2: DatabaseReference
    lateinit var notificationList : MutableList<Notification>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root: View = inflater.inflate(R.layout.fragment_notification2, container, false)

        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid

        notificationList = mutableListOf()

        ref= FirebaseDatabase.getInstance().getReference("Notification")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    notificationList.clear()
                    for (h in snapshot.children){
                        if(h.child("userID").getValue().toString().equals(currentUserID)) {
                            val notification = h.getValue(Notification::class.java)
                            notificationList.add(notification!!)
                            Log.d("abcdc", "zzz fail")
                        }
                        else{
                            Log.d("abcc", "zzz fail")
                        }
                    }
                    val adapter = NotificationAdapter(notificationList)
                    val mLayoutManager = LinearLayoutManager(activity)
                    mLayoutManager.reverseLayout = true
                    root.notificationRecyclerView.layoutManager = mLayoutManager

                    root.notificationRecyclerView.scrollToPosition(notificationList.size-1)
                    root.notificationRecyclerView.adapter = adapter

                }
            }

        })




    return root
    }

}