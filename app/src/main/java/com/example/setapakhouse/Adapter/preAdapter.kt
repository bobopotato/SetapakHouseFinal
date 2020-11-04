package com.example.setapakhouse.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.setapakhouse.R

class preAdapter(private var pre:List<String>): RecyclerView.Adapter<preAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val preference: TextView =itemView.findViewById<TextView>(R.id.preTxtView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): preAdapter.ViewHolder {
        val v= LayoutInflater.from(parent.context).inflate(R.layout.preference_layout_item,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return pre.size
    }

    override fun onBindViewHolder(holder: preAdapter.ViewHolder, position: Int) {
        holder.preference.text=pre[position]
    }


}