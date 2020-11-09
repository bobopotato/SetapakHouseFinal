package com.example.setapakhouse.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.setapakhouse.Adapter.myReviewAdapter
import com.example.setapakhouse.Adapter.pendingReviewAdapter
import com.example.setapakhouse.Model.Property
import com.example.setapakhouse.Model.Review
import com.example.setapakhouse.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_completed_review.view.*
import kotlinx.android.synthetic.main.fragment_pending_review.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class pendingReviewFragment : Fragment() {
    val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
    lateinit var ref: DatabaseReference
    lateinit var ref1: DatabaseReference
    lateinit var propertyList:MutableList<Property>
    lateinit var reviewList:MutableList<Review>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root:View= inflater.inflate(R.layout.fragment_pending_review, container, false)

        propertyList= mutableListOf()
        reviewList= mutableListOf()


        readData(object: pendingReviewFragment.FirebaseCallback {
            override fun onCallback(list: MutableList<Review>) {
                ref1=FirebaseDatabase.getInstance().getReference("Property")
                ref1.addValueEventListener(object :ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()) {
                            propertyList.clear()
                            for (h in snapshot.children) {
                                for (i in list) {
                                    if(h.child("propertyID").getValue().toString().equals(i.propertyID)){
                                        val property=h.getValue(Property::class.java)
                                        propertyList.add(property!!)
                                    }
                                }
                            }
                            val mLayoutManager = LinearLayoutManager(context)
                            root.pendingReviewRecycle.layoutManager = mLayoutManager
                            root.pendingReviewRecycle.adapter = pendingReviewAdapter(propertyList)
                        }
                    }

                })
            }
        })


        return root
    }

    private fun readData(firebaseCallback: pendingReviewFragment.FirebaseCallback){


        ref=FirebaseDatabase.getInstance().getReference("Review")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    reviewList.clear()
                    for(h in snapshot.children){
                        if(h.child("userID").getValue().toString().equals(currentUserID)&&
                            h.child("status").getValue().toString().equals("pending")){
                            val review=h.getValue(Review::class.java)
                            reviewList.add(review!!)

                        }
                    }
                    firebaseCallback.onCallback(reviewList)
                }
            }

        })

    }

    private interface FirebaseCallback{
        fun onCallback(list : MutableList<Review>)
    }
}