package com.example.setapakhouse.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.setapakhouse.Model.Property
import com.example.setapakhouse.Model.Review
import com.example.setapakhouse.R
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class reviewAdapter(val review : MutableList<Review>): RecyclerView.Adapter<reviewAdapter.ViewHolder>() {
    lateinit var ref: DatabaseReference
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val user_Img: ImageView=itemView.findViewById(R.id.imgProfile)
        val user_Name: TextView =itemView.findViewById(R.id.txtUsername)
        val review_DateTime:TextView=itemView.findViewById(R.id.txtDateTime)
        val review_Content:TextView=itemView.findViewById(R.id.reviewContent)
        val review_Rating:RatingBar=itemView.findViewById(R.id.reviewRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v= LayoutInflater.from(parent.context).inflate(R.layout.review_layout_item,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return review.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.review_Content.text=review[position].reviewContent
        holder.review_Rating.setRating(review[position].numStar.toFloat())
        holder.review_DateTime.text=review[position].reviewDateTime
        ref=FirebaseDatabase.getInstance().getReference("Users")
        ref.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(h in snapshot.children){
                        if(h.child("userID").getValue().toString().equals(review[position].userID)){
                            holder.user_Name.text=h.child("username").getValue().toString()
                            Picasso.get().load(h.child("image").getValue().toString()).into(holder.user_Img)
                        }
                    }
                }

            }

        })

    }

}