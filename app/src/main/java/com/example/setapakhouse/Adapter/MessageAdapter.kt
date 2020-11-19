package com.example.setapakhouse.Adapter

import android.content.Context
import android.os.CountDownTimer
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.setapakhouse.Model.Chat
import com.example.setapakhouse.R
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class MessageAdapter(private var chat:MutableList<Chat>,private var image:String): RecyclerView.Adapter<MessageAdapter.ViewHolder>() {


    //val currentUserID="k83T1GSoDjRPH6XIwJlRVP89CEk2"
    //val currentUserID="LfOPyQJQgqPaziIT823zv7DZJzY2"
    val MSG_TYPE_LEFT=0
    val MSG_TYPE_RIGHT=1

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val seen_message:TextView = itemView.findViewById<TextView>(R.id.txtSeen)
        val date_message:TextView = itemView.findViewById<TextView>(R.id.messageTime)
        val user_img: ImageView =itemView.findViewById<ImageView>(R.id.profile_image)
        val show_message: TextView =itemView.findViewById<TextView>(R.id.show_message)
        val message_background:RelativeLayout=itemView.findViewById(R.id.backgroundID)
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

        holder.date_message.text=chat[position].messageDate
        holder.date_message.setVisibility(View.GONE)
        holder.seen_message.setVisibility(View.GONE)
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        holder.show_message.text=chat[position].message

        Picasso.get().load(image).placeholder(R.drawable.ic_profile).into(holder.user_img)

        if(position==chat.size-1){
            if(chat[position].receiver.equals(currentUserID)){
                holder.seen_message.setVisibility(View.GONE)
            }else{
                holder.seen_message.setVisibility(View.VISIBLE)
            }
            holder.date_message.setVisibility(View.VISIBLE)

            holder.date_message.text=chat[position].messageDate
            if(chat[position].isseen.equals("true")){
                holder.seen_message.text="Seen"
            }else{
                holder.seen_message.text="Delivered"
            }
        }else{
            holder.date_message.setVisibility(View.GONE)
            holder.seen_message.setVisibility(View.GONE)
        }

        val timer = object: CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                holder.date_message.setVisibility(View.GONE)
                holder.seen_message.setVisibility(View.GONE)
            }
        }
        holder.show_message.setOnClickListener {
            if(position!=chat.size-1) {
                if (holder.date_message.getVisibility() == View.VISIBLE && holder.date_message.getVisibility() == View.VISIBLE) {
                    holder.date_message.setVisibility(View.GONE)
                    holder.seen_message.setVisibility(View.GONE)
                    timer.cancel()
                    timer.start()
                } else {
                    holder.date_message.setVisibility(View.VISIBLE)
                    holder.seen_message.setVisibility(View.VISIBLE)

                    timer.cancel()
                    timer.start()

//                    Handler().postDelayed({
//                        holder.date_message.setVisibility(View.GONE)
//                        holder.seen_message.setVisibility(View.GONE)
//                    },2000)


                }

            }

        }
//        holder.message_background.setOnClickListener {
//            val imm = holder.date_message.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow((holder.date_message.context.getWindowToken().getDecorView().getApplicationWindowToken()),0)
//
//
//        }

    }

    override fun getItemViewType(position: Int): Int {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        if(chat[position].sender.equals(currentUserID)){
            return MSG_TYPE_RIGHT
        }else{
            return MSG_TYPE_LEFT
        }
    }

}