package com.example.setapakhouse.Fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.setapakhouse.Adapter.NotificationAdapter
import com.example.setapakhouse.Adapter.accomAdapter
import com.example.setapakhouse.MessageActivity
import com.example.setapakhouse.Model.Notification
import com.example.setapakhouse.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_notification2.*
import kotlinx.android.synthetic.main.fragment_notification2.view.*

class NotificationFragment2 : Fragment() {

    lateinit var ref: DatabaseReference
    lateinit var ref2: DatabaseReference
    lateinit var notificationList : MutableList<Notification>
    lateinit var epicDialog : Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root: View = inflater.inflate(R.layout.fragment_notification2, container, false)

        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid

        notificationList = mutableListOf()

        epicDialog = Dialog(context!!)


        val ref1234 = FirebaseDatabase.getInstance().getReference("Notification").orderByChild("userID").equalTo(currentUserID)

        ref1234.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        var count = 0
                        for(h in snapshot.children){

                            if(h.child("status").getValue().toString().equals("delivered")){
                                count++

                            }
                            if(count ==0){
                                root.markRead.visibility = View.GONE
                            }
                            else{
                                root.markRead.visibility = View.VISIBLE
                            }
                            root.notificationCount.text = count.toString()
                        }

                    }
                }

        })


        root.markRead.setOnClickListener {
            if(root.notificationCount.text.toString() != ""){
                if(root.notificationCount.text.toString().toInt() == 0){
                    showDialog3()
                }
                else{
                    epicDialog.setContentView(R.layout.popup_confirmation)
                    //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
                    val yesButton : Button = epicDialog.findViewById(R.id.yesBtn)
                    val cancelButton : Button = epicDialog.findViewById(R.id.cancelBtn)
                    val title : TextView = epicDialog.findViewById(R.id.title)
                    val content : TextView = epicDialog.findViewById(R.id.content)

                    title.text = "Mark As Read"

                    content.text = "Are you sure to mark all notification as read?"


                    yesButton.text = "Yes"
                    yesButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

                    yesButton.setOnClickListener {
                        var store = 0
                        val ref123 = FirebaseDatabase.getInstance().getReference("Notification").orderByChild("userID").equalTo(currentUserID)
                        ref123.addValueEventListener(object: ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                if(store == 0){
                                    if(snapshot.exists()){
                                        for(h in snapshot.children){
                                            if(h.child("status").getValue().toString().equals("delivered")){
                                                var ref555 = FirebaseDatabase.getInstance().getReference("Notification")

                                                ref555.child(h.child("notificationID").getValue().toString()).child("status").setValue("seen")
                                            }
                                        }
                                    }
                                    store++
                                }

                            }

                        })
                        epicDialog.dismiss()

                    }
                    cancelButton.setOnClickListener {
                        epicDialog.dismiss()
                    }

                    epicDialog.setCancelable(true)
                    epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    epicDialog.show()
                }
            }
            else{
                showDialog3()
            }



        }

        ref= FirebaseDatabase.getInstance().getReference("Notification")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    notificationList.clear()
                    for (h in snapshot.children){
                        if(h.child("userID").getValue().toString().equals(currentUserID)) {
                            val notification = h.getValue(Notification::class.java)
                            notificationList.add(notification!!)
                            //Log.d("abcdc", "zzz fail")
                        }
                        else{
                            //Log.d("abcc", "zzz fail")
                        }
                    }

                    if(notificationList.size == 0){
                        root.noRecordFound.visibility = View.VISIBLE
                        root.markRead.visibility = View.GONE
                    }
                    else{
                        root.noRecordFound.visibility = View.GONE
                        root.markRead.visibility = View.VISIBLE
                    }
                    val adapter = NotificationAdapter(notificationList)
                    val mLayoutManager = LinearLayoutManager(activity)
                    mLayoutManager.reverseLayout = true
                    root.notificationRecyclerView.layoutManager = mLayoutManager

                    root.notificationRecyclerView.scrollToPosition(notificationList.size-1)
                    root.notificationRecyclerView.adapter = adapter

                }
            }

        })




        return root
    }

    private fun showDialog3(){
        epicDialog.setContentView(R.layout.popup_error)
        //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
        val okButton : Button = epicDialog.findViewById(R.id.okBtn)
        val title : TextView = epicDialog.findViewById(R.id.title)
        val content : TextView = epicDialog.findViewById(R.id.content)

        title.text = "Invalid Action"
        content.text = "Your notifications are all being marked as read already."

        okButton.setOnClickListener {
            epicDialog.dismiss()
        }
        epicDialog.setCancelable(true)
        epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        epicDialog.show()
    }

}