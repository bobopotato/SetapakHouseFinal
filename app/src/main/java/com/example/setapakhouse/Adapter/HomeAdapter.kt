package com.example.setapakhouse.Adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.setapakhouse.Fragment.ProfileFragment
import com.example.setapakhouse.MainActivity
import com.example.setapakhouse.Model.Property
import com.example.setapakhouse.OtherUserProfileActivity
import com.example.setapakhouse.R
import com.example.setapakhouse.detailPost
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail_post.*


class HomeAdapter(val property : MutableList<Property>,val clickableOrNot:Boolean): RecyclerView.Adapter<HomeAdapter.MyViewHolder>() {

    lateinit var ref:DatabaseReference


    inner class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var txt_username:TextView=itemView.findViewById<TextView>(R.id.txtUsername);
        var txt_price:TextView=itemView.findViewById<TextView>(R.id.txtPrice);
        var txt_location:TextView=itemView.findViewById<TextView>(R.id.txtLocation);
        var txt_propertyName:TextView=itemView.findViewById<TextView>(R.id.txtPropertyName);
        var img_user: ImageView = itemView.findViewById<ImageView>(R.id.imgProfile);
        var img_property: ImageView = itemView.findViewById<ImageView>(R.id.imgProperty);
        var txt_dateTime:TextView=itemView.findViewById<TextView>(R.id.txtDateTime);
        var txt_propertyType:TextView=itemView.findViewById<TextView>(R.id.txtPropertyType);
        var txt_rentalType:TextView=itemView.findViewById<TextView>(R.id.txtRentalType);

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.property_layout_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return property.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentUserID= FirebaseAuth.getInstance().currentUser!!.uid

        if(clickableOrNot) {
            holder.img_user.setOnClickListener {
                if (property[position].userID.equals(currentUserID)) {
                    val intent = Intent(holder.img_property.context, MainActivity::class.java)
                    intent.putExtra("goProfile", "yes")
                    holder.img_property.context.startActivity(intent)
                } else {
                    val intent =
                        Intent(holder.img_user.context, OtherUserProfileActivity::class.java)
                    intent.putExtra("otherUserID", property[position].userID)

                    holder.img_user.context.startActivity(intent)
                }
            }
        }

        holder.txt_dateTime.text=property[position].releaseDateTime
        holder.txt_location.text=property[position].location
        if(property[position].rentalType.toString().equals("Long-Term")) {
            holder.txt_price.text = "RM"+String.format("%.2f",property[position].price)+"/MONTH"
        }else{
            holder.txt_price.text = "RM"+String.format("%.2f",property[position].price)+"/DAY"
        }
        holder.txt_propertyType.text="Property Type: "+property[position].propertyType.toString()
        holder.txt_rentalType.text="Rental Type: "+property[position].rentalType.toString()
        holder.txt_propertyName.text="Property Name: "+property[position].propertyName.toString()
        ref= FirebaseDatabase.getInstance().getReference("Users")

        ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(h in snapshot.children){
                        if(h.child("userID").getValue().toString().equals(property[position].userID)){
                            holder.txt_username.text=h.child("username").getValue().toString()
                            Picasso.get().load(h.child("image").getValue().toString()).placeholder(R.drawable.ic_profile).into(holder.img_user)
                        }

                    }
                }
            }

        })

        ref=FirebaseDatabase.getInstance().getReference("PropertyImage")
        ref.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){

                    for(h in snapshot.children){
                        if(h.child("propertyID").getValue().toString().equals(property[position].propertyID) && h.child("imageName").getValue().toString().equals("image1")){
                            Picasso.get().load(h.child("imageSource").getValue().toString()).placeholder(R.drawable.ic_home).into(holder.img_property)
                        }
                    }
                }
            }

        })





//        holder.img_user.setOnClickListener {
//
//            if(property[position].userID.equals(currentUserID)){
//            }else {
//                val intent = Intent(holder.img_user.context, OtherUserProfileActivity::class.java)
//                intent.putExtra("otherUserID", property[position].userID)
//
//                holder.img_user.context.startActivity(intent)
//            }
//
//        }
        holder.img_property.setOnClickListener{
            val intent = Intent(holder.img_property.context, detailPost::class.java)
            intent.putExtra("selectedPosition", property[position].propertyID)
            intent.putExtra("selectedUserID",property[position].userID)
            holder.img_property.context.startActivity(intent)
        }

    }
}