package com.example.setapakhouse.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.setapakhouse.Model.Review
import com.example.setapakhouse.Model.Topup
import com.example.setapakhouse.R
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class topupAdapter(val topup : MutableList<Topup>): RecyclerView.Adapter<topupAdapter.ViewHolder>() {
    lateinit var ref: DatabaseReference


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val topup_dateTime: TextView =itemView.findViewById(R.id.topupDateTime)
        val topup_amount: TextView =itemView.findViewById(R.id.topupAmount)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v= LayoutInflater.from(parent.context).inflate(R.layout.topup_layout_item,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return topup.size
    }

    override fun onBindViewHolder(holder: topupAdapter.ViewHolder, position: Int) {
        holder.topup_dateTime.text=topup[position].topupDateTime
        holder.topup_amount.text="+RM "+topup[position].topupAmount.toString()
    }

}