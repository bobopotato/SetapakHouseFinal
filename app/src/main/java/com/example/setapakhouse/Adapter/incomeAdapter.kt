package com.example.setapakhouse.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.setapakhouse.R
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlin.math.roundToInt

class incomeAdapter(private var idList:List<String>,private var dateTimeList:List<String>,private var amountList:List<String>,private var propertyNameList:List<String>,private var durationList: List<String>): RecyclerView.Adapter<incomeAdapter.ViewHolder>() {

    lateinit var ref:DatabaseReference
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        var img_user: ImageView = itemView.findViewById<ImageView>(R.id.payerIcon)
        val user_name: TextView =itemView.findViewById<TextView>(R.id.payerName)
        val pay_for: TextView =itemView.findViewById<TextView>(R.id.payForPropertyTxt)
        val received_Date: TextView =itemView.findViewById<TextView>(R.id.receivedDate)
        val received_amount: TextView =itemView.findViewById<TextView>(R.id.receiveMoney)
        val duration: TextView =itemView.findViewById<TextView>(R.id.duration)
        val rp: TextView =itemView.findViewById<TextView>(R.id.rewardEarn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v= LayoutInflater.from(parent.context).inflate(R.layout.income_layout_item,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return idList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(((amountList[position].toDouble())/100).roundToInt().equals(0)){
            holder.rp.text =
                "EARN: 0RP"
        }else {
            holder.rp.text =
                "EARN: " + ((amountList[position].toDouble()) / 100).roundToInt().toString() + "RP"
        }
        holder.received_Date.text=dateTimeList[position]
        holder.received_amount.text="+RM"+amountList[position]
        holder.pay_for.text=propertyNameList[position]
        holder.duration.text=durationList[position]
        ref=FirebaseDatabase.getInstance().getReference("Users")
        ref.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for(h in snapshot.children){
                    if(h.child("userID").getValue().toString().equals(idList[position])){
                        holder.user_name.text="Received from "+h.child("username").getValue().toString()
                        Picasso.get().load(h.child("image").getValue().toString()).placeholder(R.drawable.ic_profile).into(holder.img_user)
                    }
                }
            }

        })
    }

}