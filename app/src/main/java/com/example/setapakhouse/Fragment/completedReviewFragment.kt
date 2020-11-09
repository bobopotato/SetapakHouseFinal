package com.example.setapakhouse.Fragment

import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.setapakhouse.Adapter.myReviewAdapter
import com.example.setapakhouse.Model.Review
import com.example.setapakhouse.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_completed_review.view.*


class completedReviewFragment : Fragment() {
    val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
    lateinit var ref:DatabaseReference
    lateinit var reviewList:MutableList<Review>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root:View= inflater.inflate(R.layout.fragment_completed_review, container, false)


        reviewList= mutableListOf()
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
                                h.child("status").getValue().toString().equals("completed")){
                            val review=h.getValue(Review::class.java)
                            reviewList.add(review!!)

                        }
                    }
                    val mLayoutManager = LinearLayoutManager(context)
                    mLayoutManager.reverseLayout = true
                    root.completedReviewRecycle.layoutManager = mLayoutManager
                    root.completedReviewRecycle.adapter = myReviewAdapter(reviewList)
                }
            }

        })

        return root
    }


}