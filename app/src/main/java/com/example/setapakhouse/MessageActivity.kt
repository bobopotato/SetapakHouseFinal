package com.example.setapakhouse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.setapakhouse.Adapter.HomeAdapter
import com.example.setapakhouse.Adapter.MessageAdapter
import com.example.setapakhouse.Model.Chat
import com.example.setapakhouse.Model.Property
import com.example.setapakhouse.Model.Review
import com.example.setapakhouse.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail_post.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class MessageActivity : AppCompatActivity() {
    lateinit var ref : DatabaseReference
    lateinit var chatList : MutableList<Chat>
    //val currentUserID="k83T1GSoDjRPH6XIwJlRVP89CEk2"
    //val currentUserID="LfOPyQJQgqPaziIT823zv7DZJzY2"
    val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid

    lateinit var seenListener: ValueEventListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setTitle("")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        chatList= mutableListOf()
        val toolbar:Toolbar = findViewById(R.id.toolbar)
        val selectedUserID=intent.getStringExtra("selectedUserID")



        toolbar.setNavigationOnClickListener {
            finish()
        }

        btn_send.setOnClickListener{
            if(!(text_send.text.toString().equals(""))){
                sendMessage(currentUserID,selectedUserID,text_send.text.toString())
            }else{
                Toast.makeText(this,"You can't send empty message",Toast.LENGTH_SHORT).show()

            }
            text_send.setText("")
        }


//        ref= FirebaseDatabase.getInstance().getReference("Users")
//        ref.addValueEventListener(object:ValueEventListener{
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if(snapshot.exists()) {
//
//                    for(h in snapshot.children){
//                        if(h.child("userID").getValue().toString().equals(selectedUserID)){
//                            Picasso.get().load(h.child("image").getValue().toString()).placeholder(R.drawable.ic_profile).into(chatUserImage)
//                            chatUserName.text = h.child("username").getValue().toString()
//                            readMessage(currentUserID,h.child("userID").getValue().toString(),h.child("image").getValue().toString())
//                        }
//                    }
//
//
//                }
//            }
//
//        })

        ref=FirebaseDatabase.getInstance().getReference("Users").child(selectedUserID)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                Picasso.get().load(user!!.image)
                    .placeholder(R.drawable.ic_profile).into(chatUserImage)
                chatUserName.text = user!!.username
                readMessage(
                    currentUserID,
                    user.userID,
                    user.image
                )
            }

        })

        seenMessage(selectedUserID)
    }

    private fun seenMessage(userid:String){
        ref=FirebaseDatabase.getInstance().getReference("Chats")
        seenListener=ref.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for(h in snapshot.children){
                    val chat=h.getValue(Chat::class.java)
                    if(chat!!.receiver.equals(currentUserID) && chat!!.sender.equals(userid)){
                        val chat=h.getValue(Chat::class.java)
                        chat!!.isseen="true"
                        h.getRef().setValue(chat)
                    }
                }
            }

        })
    }

    private fun sendMessage(sender:String,receiver:String,message:String){
        ref=FirebaseDatabase.getInstance().getReference()
        var hashMap:HashMap<String,String> = HashMap<String,String>()
        hashMap.put("sender",sender)
        hashMap.put("receiver",receiver)
        hashMap.put("message",message)
        hashMap.put("isseen","false")
        ref.child("Chats").push().setValue(hashMap)
    }

    private fun readMessage(myID:String,userID:String,image:String){
        ref=FirebaseDatabase.getInstance().getReference("Chats")
        ref.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    chatList.clear()
                    for(h in snapshot.children){
                        if(h.child("receiver").getValue().toString().equals(myID) && h.child("sender").getValue().toString().equals(userID)||
                            h.child("receiver").getValue().toString().equals(userID) && h.child("sender").getValue().toString().equals(myID)) {
                            val chat = h.getValue(Chat::class.java)
                            chatList.add(chat!!)
                        }
                    }
                    val mLayoutManager = LinearLayoutManager(this@MessageActivity)
                    mLayoutManager.setStackFromEnd(true)
                    messageRecyclerView.layoutManager = mLayoutManager
                    messageRecyclerView.adapter = MessageAdapter(chatList,image)
                }
            }

        })
    }

    override fun onPause() {
        super.onPause()
        ref.removeEventListener(seenListener)
    }
}