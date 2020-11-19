package com.example.setapakhouse.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.setapakhouse.R
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlin.math.roundToInt

class outgoingAdapter(private var receiverIdList:List<String>,private var paidDateTimeList:List<String>,private var paidAmountList:List<String>,private var receivedPropertyNameList:List<String>,private var rewardList:List<Int>,private var durationList:List<String>): RecyclerView.Adapter<outgoingAdapter.ViewHolder>() {

    lateinit var ref: DatabaseReference
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        var img_user: ImageView = itemView.findViewById<ImageView>(R.id.receiverIcon)
        val user_name: TextView =itemView.findViewById<TextView>(R.id.receiverName)
        val received_from: TextView =itemView.findViewById<TextView>(R.id.receivedFrom)
        val paid_Date: TextView =itemView.findViewById<TextView>(R.id.paidDate)
        val paid_amount: TextView =itemView.findViewById<TextView>(R.id.paidMoney)
        val reward_point: TextView =itemView.findViewById<TextView>(R.id.rpUsed)
        val gain_rp: TextView =itemView.findViewById<TextView>(R.id.rpEarn)
        val duration: TextView =itemView.findViewById<TextView>(R.id.duration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v= LayoutInflater.from(parent.context).inflate(R.layout.outgoing_layout_item,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return receiverIdList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.duration.text=durationList[position]
        holder.reward_point.text="Redeemed: "+rewardList[position].toString()+"RP"
        holder.paid_Date.text=paidDateTimeList[position]
        holder.paid_amount.text="-RM"+String.format("%.2f",(paidAmountList[position].toDouble()-rewardList[position].toString().toDouble()))
        holder.received_from.text=receivedPropertyNameList[position]
        if(((paidAmountList[position].toDouble())/100).roundToInt().equals(0)){
            holder.gain_rp.text =
                "Gain: 0RP"
        }else {
            holder.gain_rp.text =
                "Gain: " + ((paidAmountList[position].toDouble()) / 100).roundToInt().toString() + "RP"
        }
        ref= FirebaseDatabase.getInstance().getReference("Users")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for(h in snapshot.children){
                    if(h.child("userID").getValue().toString().equals(receiverIdList[position])){
                        holder.user_name.text="Paid To "+h.child("username").getValue().toString()
                        Picasso.get().load(h.child("image").getValue().toString()).placeholder(R.drawable.ic_profile).into(holder.img_user)
                    }
                }
            }

        })
    }

}