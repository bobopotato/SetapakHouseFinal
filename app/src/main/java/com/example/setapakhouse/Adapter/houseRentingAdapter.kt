package com.example.setapakhouse.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.setapakhouse.Model.Property
import com.example.setapakhouse.Model.Review
import com.example.setapakhouse.R
import com.example.setapakhouse.detailPost
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class houseRentingAdapter(val propertyList : MutableList<Property>,val checkIn:MutableList<String>,val checkOut:MutableList<String>): RecyclerView.Adapter<houseRentingAdapter.ViewHolder>() {
    lateinit var ref: DatabaseReference
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val property_Image: ImageView =itemView.findViewById(R.id.imgProperty)
        val property_name: TextView =itemView.findViewById(R.id.txtPropertyName)
        val property_price: TextView =itemView.findViewById(R.id.txtPrice)
        val duration: TextView =itemView.findViewById(R.id.durationTxt)
        val whole: RelativeLayout =itemView.findViewById(R.id.wholeLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v= LayoutInflater.from(parent.context).inflate(R.layout.layout_houserenting_item,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return propertyList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.duration.text="Rental Duration: "+checkIn[position]+" to "+checkOut[position]
        holder.property_name.text=propertyList[position].propertyName
        if(propertyList[position].rentalType.toString().equals("Long-Term")) {
            holder.property_price.text = "RM"+propertyList[position].price.toString()+"/MONTH"
        }else{
            holder.property_price.text = "RM"+propertyList[position].price.toString()+"/DAY"
        }
        ref=FirebaseDatabase.getInstance().getReference("PropertyImage")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for(h in snapshot.children){
                    if(h.child("propertyID").getValue().toString().equals(propertyList[position].propertyID)&&
                        h.child("imageName").getValue().toString().equals("image1")){

                        Picasso.get().load(h.child("imageSource").getValue().toString()).placeholder(R.drawable.ic_home).into(holder.property_Image)

                    }
                }
            }

        })

        holder.whole.setOnClickListener{
            val intent = Intent(holder.whole.context, detailPost::class.java)
            intent.putExtra("selectedPosition", propertyList[position].propertyID)
            intent.putExtra("selectedUserID",propertyList[position].userID)
            holder.whole.context.startActivity(intent)
        }
    }

}