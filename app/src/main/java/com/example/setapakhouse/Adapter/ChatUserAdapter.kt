package com.example.setapakhouse.Adapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.setapakhouse.MainActivity
import com.example.setapakhouse.MessageActivity
import com.example.setapakhouse.Model.Chat
import com.example.setapakhouse.Model.User
import com.example.setapakhouse.R
import com.example.setapakhouse.detailPost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail_post.*
import org.w3c.dom.Text

class ChatUserAdapter(private var user:MutableList<User>,private var isChat:Boolean): RecyclerView.Adapter<ChatUserAdapter.ViewHolder>() {

    var theLastMessage="default"


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val user_img: ImageView =itemView.findViewById<ImageView>(R.id.chatUserImage)
        val user_name: TextView =itemView.findViewById<TextView>(R.id.chatUserName)
        val last_message:TextView=itemView.findViewById<TextView>(R.id.lastMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.chat_user_layout_item,parent,false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return user.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.user_name.text=user[position].username
        Picasso.get().load(user[position].image).placeholder(R.drawable.ic_profile).into(holder.user_img)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, MessageActivity::class.java)
            intent.putExtra("selectedUserID",user[position].userID)

            holder.itemView.context.startActivity(intent)
        }

        if(isChat) {
            lastMessage(user[position].userID,holder.last_message)
        }else {
            holder.last_message.setVisibility(View.GONE)
        }


    }

    private fun lastMessage(userID:String,last_msg:TextView){
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        //val currentUserID="k83T1GSoDjRPH6XIwJlRVP89CEk2"
        //val currentUserID="LfOPyQJQgqPaziIT823zv7DZJzY2"
        val ref:DatabaseReference
        ref=FirebaseDatabase.getInstance().getReference("Chats")
        ref.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    for(h in snapshot.children) {
                        val chat=h.getValue(Chat::class.java)
                        if(chat!!.receiver.equals(currentUserID) && chat!!.sender.equals(userID)||
                            chat!!.receiver.equals(userID) && chat!!.sender.equals(currentUserID) ) {
                            theLastMessage=chat.message

                        }

                    }

                    when(theLastMessage){
                        "default" ->{last_msg.setText("No Message")}
                        else ->{last_msg.setText(theLastMessage)}
                    }

                }
                theLastMessage="default"
            }

        })

    }

}