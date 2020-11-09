package com.example.setapakhouse.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.setapakhouse.Model.Property
import com.example.setapakhouse.R
import com.example.setapakhouse.addReviewActivity
import com.example.setapakhouse.detailPost
import com.example.setapakhouse.editReviewActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import org.w3c.dom.Text

class pendingReviewAdapter(val property : MutableList<Property>): RecyclerView.Adapter<pendingReviewAdapter.MyViewHolder>() {

    lateinit var ref: DatabaseReference

    val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
    lateinit var query:Query

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var txt_price: TextView =itemView.findViewById<TextView>(R.id.txtPrice);
        var txt_location: TextView =itemView.findViewById<TextView>(R.id.txtLocation);
        var txt_propertyName: TextView =itemView.findViewById<TextView>(R.id.txtPropertyName);
        var img_property: ImageView = itemView.findViewById<ImageView>(R.id.imgProperty);
        var txt_propertyType: TextView =itemView.findViewById<TextView>(R.id.txtPropertyType);
        var txt_rentalType: TextView =itemView.findViewById<TextView>(R.id.txtRentalType);
        var to_reviewBtn:Button=itemView.findViewById<Button>(R.id.toReviewBtn)
        var whole_layout:RelativeLayout=itemView.findViewById(R.id.wholeLayout)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.pending_review_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return property.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.txt_location.text=property[position].location
        if(property[position].rentalType.toString().equals("Long-Term")) {
            holder.txt_price.text = "RM"+property[position].price.toString()+"/MONTH"
        }else{
            holder.txt_price.text = "RM"+property[position].price.toString()+"/DAY"
        }
        holder.txt_propertyType.text="Property Type: "+property[position].propertyType.toString()
        holder.txt_rentalType.text="Rental Type: "+property[position].rentalType.toString()
        holder.txt_propertyName.text="Property Name: "+property[position].propertyName.toString()

        ref= FirebaseDatabase.getInstance().getReference("PropertyImage")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){

                    for(h in snapshot.children){
                        if(h.child("propertyID").getValue().toString().equals(property[position].propertyID) && h.child("imageName").getValue().toString().equals("image1")){
                            Picasso.get().load(h.child("imageSource").getValue().toString()).placeholder(
                                R.drawable.ic_home).into(holder.img_property)
                        }
                    }
                }
            }

        })

        holder.whole_layout.setOnClickListener{
            val intent = Intent(holder.whole_layout.context, detailPost::class.java)
            intent.putExtra("selectedPosition", property[position].propertyID)
            intent.putExtra("selectedUserID", property[position].userID)
            holder.whole_layout.context.startActivity(intent)
        }

        holder.to_reviewBtn.setOnClickListener {
            val intent = Intent(holder.to_reviewBtn.context, addReviewActivity::class.java)
            intent.putExtra("selectedPropertyName", property[position].propertyName)
            intent.putExtra("selectedPropertyID", property[position].propertyID)
            holder.to_reviewBtn.context.startActivity(intent)
        }

    }
}