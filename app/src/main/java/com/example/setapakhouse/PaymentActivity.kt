package com.example.setapakhouse

import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.setapakhouse.Adapter.NotificationAdapter
import com.example.setapakhouse.Adapter.PaymentAdapter
import com.example.setapakhouse.Model.Notification
import com.example.setapakhouse.Model.Payment
import com.example.setapakhouse.Model.Rent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.activity_post1.*
import kotlinx.android.synthetic.main.activity_post1.toolbar
import kotlinx.android.synthetic.main.fragment_notification2.view.*

class PaymentActivity : AppCompatActivity() {

    lateinit var ref: DatabaseReference
    lateinit var ref2: DatabaseReference
    lateinit var rentList : MutableList<Rent>
    lateinit var paymentList : MutableList<Payment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setTitle("My Payment")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        rentList = mutableListOf()
        paymentList = mutableListOf()

        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid

        ref= FirebaseDatabase.getInstance().getReference("Rent")
        ref2= FirebaseDatabase.getInstance().getReference("Payment")

        ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    rentList.clear()
                    for (h in snapshot.children){
                        if(h.child("userID").getValue().toString().equals(currentUserID)) {
                            val targetRent = h.getValue(Rent::class.java)
                            rentList.add(targetRent!!)
                        }
                    }

                    Log.d("abcc", "rentlist11 = " + rentList.size)
                    Log.d("abcc", "paymentList11 = " + paymentList.size)

                    ref2.addValueEventListener(object: ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                paymentList.clear()
                                for (h in snapshot.children) {
                                    for(x in rentList){
                                        if(h.child("rentID").getValue().toString().equals(x.rentID) && h.child("status").getValue().toString().equals("pending")){
                                            val targetPayment = h.getValue(Payment::class.java)
                                            paymentList.add(targetPayment!!)
                                            //Log.d("abcc", "rentlist123 = " + rentList.size)
                                            //Log.d("abcc", "paymentList123 = " + paymentList.size)
                                        }
                                    }

                                }
                                val adapter = PaymentAdapter(paymentList, rentList)
                                val mLayoutManager = LinearLayoutManager(this@PaymentActivity)
                                mLayoutManager.reverseLayout = true
                                myPaymentRecyclerView.layoutManager = mLayoutManager

                                myPaymentRecyclerView.scrollToPosition(paymentList.size-1)
                                myPaymentRecyclerView.adapter = adapter

                                //Log.d("abcc", "rentlist = " + rentList.size)
                                //Log.d("abcc", "paymentList = " + paymentList.size)
                            }

                        }
                    })

                }
            }

        })

    }

    fun TextView.underline() {
        paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}