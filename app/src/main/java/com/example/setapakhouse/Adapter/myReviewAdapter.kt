package com.example.setapakhouse.Adapter

import android.content.Intent
import android.media.Rating
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.setapakhouse.Model.Review
import com.example.setapakhouse.R
import com.example.setapakhouse.detailPost
import com.example.setapakhouse.editReviewActivity
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import org.w3c.dom.Text


class myReviewAdapter(val review : MutableList<Review>): RecyclerView.Adapter<myReviewAdapter.ViewHolder>() {
    lateinit var ref: DatabaseReference
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val property_Image: ImageView =itemView.findViewById(R.id.propertyImage)
        val property_Name: TextView =itemView.findViewById(R.id.txtPropertyName)
        val rating_bar: RatingBar =itemView.findViewById(R.id.ratingBar)
        val review_Content: TextView =itemView.findViewById(R.id.reviewContent)
        val whole:RelativeLayout=itemView.findViewById(R.id.wholeReview)
        val hidden_userid:TextView=itemView.findViewById(R.id.hiddenUserID)
        val edit_btn:Button=itemView.findViewById(R.id.editBtn)
        val review_date: TextView =itemView.findViewById(R.id.reviewDate)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v= LayoutInflater.from(parent.context).inflate(R.layout.completed_review_item,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return review.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.review_Content.text=review[position].reviewContent
        holder.rating_bar.setRating(review[position].numStar.toFloat())
        holder.review_date.text=review[position].reviewDateTime

        ref=FirebaseDatabase.getInstance().getReference("Property")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(h in snapshot.children){
                        if(h.child("propertyID").getValue().toString().equals(review[position].propertyID)){
                            holder.property_Name.text=h.child("propertyName").getValue().toString()
                            holder.hidden_userid.text=h.child("userID").getValue().toString()
                        }
                    }
                }
            }

        })
        ref=FirebaseDatabase.getInstance().getReference("PropertyImage")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for(h in snapshot.children){
                    if(h.child("propertyID").getValue().toString().equals(review[position].propertyID) &&
                        h.child("imageName").getValue().toString().equals("image1")){
                        Picasso.get().load(h.child("imageSource").getValue().toString()).placeholder(R.drawable.ic_home).into(holder.property_Image)
                    }
                }
            }

        })



        holder.edit_btn.setOnClickListener {
            val intent = Intent(holder.edit_btn.context, editReviewActivity::class.java)
            intent.putExtra("selectedReviewID", review[position].reviewID)
            intent.putExtra("selectedReviewContent", review[position].reviewContent)
            intent.putExtra("selectedNumStar", review[position].numStar.toString())
            intent.putExtra("selectedPropertyID", review[position].propertyID)
            holder.edit_btn.context.startActivity(intent)
        }

        holder.whole.setOnClickListener{
            val intent = Intent(holder.whole.context, detailPost::class.java)
            intent.putExtra("selectedPosition", review[position].propertyID)
            intent.putExtra("selectedUserID",holder.hidden_userid.text.toString())
            holder.whole.context.startActivity(intent)
        }

    }

}