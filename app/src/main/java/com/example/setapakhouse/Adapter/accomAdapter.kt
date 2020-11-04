package com.example.setapakhouse.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.setapakhouse.R

class accomAdapter(private var accom:List<String>): RecyclerView.Adapter<accomAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        val accommodation: TextView =itemView.findViewById<TextView>(R.id.accommodationTxtView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v= LayoutInflater.from(parent.context).inflate(R.layout.accommodation_layout_item,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return accom.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.accommodation.text=accom[position]
    }

}