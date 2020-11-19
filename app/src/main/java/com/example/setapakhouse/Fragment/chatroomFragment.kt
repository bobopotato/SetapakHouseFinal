package com.example.setapakhouse.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.setapakhouse.Adapter.ChatUserAdapter
import com.example.setapakhouse.Adapter.HomeAdapter
import com.example.setapakhouse.Model.Chat
import com.example.setapakhouse.Model.User
import com.example.setapakhouse.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_chatroom2.view.*



class chatroomFragment : Fragment() {
    val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
    //val currentUserID="k83T1GSoDjRPH6XIwJlRVP89CEk2"
    //val currentUserID="LfOPyQJQgqPaziIT823zv7DZJzY2"
    lateinit var ref:DatabaseReference
    lateinit var userList : MutableList<User>
    lateinit var mUser:MutableList<String>
    lateinit var mUser1:MutableList<String>
    lateinit var mUser2:MutableList<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root: View =  inflater.inflate(R.layout.fragment_chatroom2, container, false)



        mUser= mutableListOf()
        mUser1= mutableListOf()
        mUser2= mutableListOf()
        userList= mutableListOf()
        ref=FirebaseDatabase.getInstance().getReference("Chats")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                mUser.clear()
                mUser1.clear()
                mUser2.clear()

                for(h in snapshot.children){
                    val chat=h.getValue(Chat::class.java)
                    //take all ppl who chat with me
                    if(chat!!.sender.equals(currentUserID)){
                        mUser.add(chat.receiver)
                    }
                    if(chat!!.receiver.equals(currentUserID)){
                        mUser.add(chat.sender)
                    }

                }

                mUser1=mUser.reversed().toMutableList()
                mUser2=mUser1.distinct().toMutableList()
                Log.d("name",mUser.toString().toString())
                Log.d("name1",mUser1.toString().toString())
                Log.d("name2",mUser2.toString().toString())
                readChats(root)
            }

        })
        return root
    }

    private fun readChats(root:View){
        ref=FirebaseDatabase.getInstance().getReference("Users")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for(id in mUser2){
                    for(h in snapshot.children){
                        val user=h.getValue(User::class.java)
                        //validate for those who chat with me and only add in 1 time into the userlist
                        if(user!!.userID.equals(id)){
                            userList.add(user)
                        }
                    }
                }

                if(userList.size == 0){
                    root.noRecordFound.visibility = View.VISIBLE
                }
                else{
                    root.noRecordFound.visibility = View.GONE
                }

                val mLayoutManager = LinearLayoutManager(context)
                root.chatRoomRecycler.layoutManager = mLayoutManager
                root.chatRoomRecycler.setHasFixedSize(true)

                root.chatRoomRecycler.adapter=ChatUserAdapter(userList,true)
            }

        })
    }

}