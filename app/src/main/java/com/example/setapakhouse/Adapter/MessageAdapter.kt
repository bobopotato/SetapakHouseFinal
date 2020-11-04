package com.example.setapakhouse.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.setapakhouse.MessageActivity
import com.example.setapakhouse.Model.Chat
import com.example.setapakhouse.Model.User
import com.example.setapakhouse.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.squareup.picasso.Picasso

class MessageAdapter(private var chat:MutableList<Chat>,private var image:String): RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
    //val currentUserID="k83T1GSoDjRPH6XIwJlRVP89CEk2"
    //val currentUserID="LfOPyQJQgqPaziIT823zv7DZJzY2"
    val MSG_TYPE_LEFT=0
    val MSG_TYPE_RIGHT=1

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val seen_message:TextView = itemView.findViewById<TextView>(R.id.txtSeen)
        val user_img: ImageView =itemView.findViewById<ImageView>(R.id.profile_image)
        val show_message: TextView =itemView.findViewById<TextView>(R.id.show_message)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if(viewType.equals(MSG_TYPE_RIGHT)){
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.chat_item_right,parent,false)
            return ViewHolder(itemView)
        }else{
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.chat_item_left,parent,false)
            return ViewHolder(itemView)
        }

    }

    override fun getItemCount(): Int {
        return chat.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.show_message.text=chat[position].message

        Picasso.get().load(image).placeholder(R.drawable.ic_profile).into(holder.user_img)

        if(position==chat.size-1){
            if(chat[position].isseen.equals("true")){
                holder.seen_message.text="Seen"
            }else{
                holder.seen_message.text="Delivered"
            }

        }else{
            holder.seen_message.setVisibility(View.GONE)
        }


    }

    override fun getItemViewType(position: Int): Int {
        if(chat[position].sender.equals(currentUserID)){
            return MSG_TYPE_RIGHT
        }else{
            return MSG_TYPE_LEFT
        }
    }

}