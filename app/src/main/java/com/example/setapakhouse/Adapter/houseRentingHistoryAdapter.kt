package com.example.setapakhouse.Adapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.setapakhouse.Model.Property
import com.example.setapakhouse.R
import com.example.setapakhouse.detailPost
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class houseRentingHistoryAdapter(val propertyList : MutableList<Property>, val checkIn:MutableList<String>, val checkOut:MutableList<String>, val rentingStatus:MutableList<String>): RecyclerView.Adapter<houseRentingHistoryAdapter.ViewHolder>() {
    lateinit var ref: DatabaseReference
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val property_Image: ImageView =itemView.findViewById(R.id.imgProperty)
        val property_name: TextView =itemView.findViewById(R.id.txtPropertyName)
        val property_price: TextView =itemView.findViewById(R.id.txtPrice)
        val renting_status: TextView =itemView.findViewById(R.id.rentingStatus)
        val duration: TextView =itemView.findViewById(R.id.durationTxt)
        val whole: RelativeLayout =itemView.findViewById(R.id.wholeLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v= LayoutInflater.from(parent.context).inflate(R.layout.layout_houserentinghistory_item,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return propertyList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if(rentingStatus[position].equals("completed")) {
            holder.renting_status.setTextColor(Color.parseColor("#056608"))
            holder.renting_status.text = "Status : " + rentingStatus[position]
        }else if(rentingStatus[position].equals("withdraw")) {
            holder.renting_status.setTextColor(Color.parseColor("#BF0000"))
            holder.renting_status.text = "Status : " + rentingStatus[position]
        }
        holder.duration.text="Rental Duration: "+checkIn[position]+" to "+checkOut[position]
        holder.property_name.text=propertyList[position].propertyName
        if(propertyList[position].rentalType.toString().equals("Long-Term")) {
            holder.property_price.text = "RM"+String.format("%.2f",propertyList[position].price.toString().toDouble())+"/MONTH"
        }else{
            holder.property_price.text = "RM"+String.format("%.2f",propertyList[position].price.toString().toDouble())+"/DAY"
        }
        ref= FirebaseDatabase.getInstance().getReference("PropertyImage")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for(h in snapshot.children){
                    if(h.child("propertyID").getValue().toString().equals(propertyList[position].propertyID)&&
                        h.child("imageName").getValue().toString().equals("image1")){

                        Picasso.get().load(h.child("imageSource").getValue().toString()).placeholder(
                            R.drawable.ic_home).into(holder.property_Image)

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