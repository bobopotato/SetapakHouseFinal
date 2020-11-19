package com.example.setapakhouse.Fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.setapakhouse.Adapter.ChatUserAdapter
import com.example.setapakhouse.Adapter.HomeAdapter
import com.example.setapakhouse.Model.Property
import com.example.setapakhouse.Model.User
import com.example.setapakhouse.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_chat_user.*
import kotlinx.android.synthetic.main.fragment_chat_user.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*


class chatUserFragment : Fragment() {

    val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
    //val currentUserID="k83T1GSoDjRPH6XIwJlRVP89CEk2"
    //val currentUserID="LfOPyQJQgqPaziIT823zv7DZJzY2"
    lateinit var userList : MutableList<User>
    lateinit var ref: DatabaseReference
    lateinit var query:Query
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root: View = inflater.inflate(R.layout.fragment_chat_user, container, false)


        userList= mutableListOf()

        root.searchUser.addTextChangedListener ( object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                searchUsers(s.toString(),root)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchUsers(s.toString(),root)
            }

        } )


        ref= FirebaseDatabase.getInstance().getReference("Users")
        ref.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (root.searchUser.text.toString().equals("")) {
                    userList.clear()
                    if (snapshot.exists()) {
                        for (h in snapshot.children) {
                            val user = h.getValue(User::class.java)
                            if (!(h.child("userID").getValue().toString().equals(currentUserID))) {
                                userList.add(user!!)
                            }
                        }

                        val mLayoutManager = LinearLayoutManager(context)

                        root.chatUserRecycler.layoutManager = mLayoutManager
                        root.chatUserRecycler.adapter = ChatUserAdapter(userList,false)
                    }
                }
            }

        })

        return root
    }

    private fun searchUsers(s: String,root:View) {
        query=FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
            .startAt(s)
            .endAt(s+"\uf8ff")
        query.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()
                if(snapshot.exists()) {

                    for (h in snapshot.children) {
                        val user = h.getValue(User::class.java)
                        if (!(user!!.userID.equals(currentUserID))) {
                            userList.add(user)
                        }
                    }
                    val mLayoutManager = LinearLayoutManager(context)

                    root.chatUserRecycler.layoutManager = mLayoutManager
                    root.chatUserRecycler.adapter = ChatUserAdapter(userList,false)
                }
            }

        })
    }

}