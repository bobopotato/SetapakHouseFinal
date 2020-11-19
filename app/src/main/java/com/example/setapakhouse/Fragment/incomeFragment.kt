package com.example.setapakhouse.Fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.setapakhouse.Adapter.ChatUserAdapter
import com.example.setapakhouse.Adapter.accomAdapter
import com.example.setapakhouse.Adapter.incomeAdapter
import com.example.setapakhouse.Model.Payment
import com.example.setapakhouse.Model.Property
import com.example.setapakhouse.Model.Rent
import com.example.setapakhouse.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_income.*
import kotlinx.android.synthetic.main.fragment_income.view.*


class incomeFragment : Fragment() {

    val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid

    lateinit var ref:DatabaseReference
    lateinit var ref1:DatabaseReference
    lateinit var ref2:DatabaseReference
    lateinit var paymentList:MutableList<Payment>
    lateinit var propertyNameList:MutableList<String>
    lateinit var propertyList:MutableList<Property>
    lateinit var payerProfileList:MutableList<String>
    lateinit var payerNameList:MutableList<String>
    lateinit var paidDateTimeList:MutableList<String>
    lateinit var paidAmountList:MutableList<String>
    lateinit var payerIDList:MutableList<String>
    lateinit var durationList:MutableList<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root:View= inflater.inflate(R.layout.fragment_income, container, false)
        durationList= mutableListOf()
        propertyList= mutableListOf()
        propertyNameList= mutableListOf()
        paymentList= mutableListOf()
        payerProfileList= mutableListOf()
        payerNameList= mutableListOf()
        paidDateTimeList= mutableListOf()
        paidAmountList= mutableListOf()
        payerIDList= mutableListOf()
        //check which payment is paid
        ref=FirebaseDatabase.getInstance().getReference("Payment")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    paymentList.clear()
                    for(h in snapshot.children){
                        if(h.child("status").getValue().toString().equals("paid")){
                            val payment=h.getValue(Payment::class.java)
                            paymentList.add(payment!!)
                        }
                    }

                    //check property the currentuser got
                    ref2=FirebaseDatabase.getInstance().getReference("Property")
                    ref2.addValueEventListener(object :ValueEventListener{
                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.exists()){
                                propertyList.clear()
                                for(h in snapshot.children){
                                    if(h.child("userID").getValue().toString().equals(currentUserID)){
                                        val property=h.getValue(Property::class.java)
                                        propertyList.add(property!!)
                                    }
                                }
                                //check the userid inside rent!= currentuserid and propertyID=the currentuser de, and rentID = payment de rentID
                                ref1=FirebaseDatabase.getInstance().getReference("Rent")
                                ref1.addValueEventListener(object :ValueEventListener{
                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if(snapshot.exists()){
                                            paidDateTimeList.clear()
                                            paidAmountList.clear()
                                            payerIDList.clear()
                                            durationList.clear()
                                            propertyNameList.clear()
                                            for(r in snapshot.children){
                                                for(p in paymentList){
                                                    for(pr in propertyList){
                                                        if(!(r.child("userID").getValue().toString().equals(currentUserID))&&
                                                            r.child("rentID").getValue().toString().equals(p.rentID)&&
                                                            r.child("propertyID").getValue().toString().equals(pr.propertyID)){
                                                            payerIDList.add(r.child("userID").getValue().toString())
                                                            paidDateTimeList.add(p.paymentDate)
                                                            paidAmountList.add(p.paymentAmount.toString())
                                                            propertyNameList.add(pr.propertyName)
                                                            durationList.add(p.paymentTitle)
                                                        }
                                                    }
                                                }
                                            }

                                            if(payerIDList.size==0){
                                                root.noRecordFound.visibility = View.VISIBLE
                                            }
                                            else{
                                                root.noRecordFound.visibility = View.GONE
                                            }

                                            val mLayoutManager = LinearLayoutManager(context)
                                            mLayoutManager.reverseLayout = true

                                            root.incomeRecycle.layoutManager = mLayoutManager
                                            root.incomeRecycle.adapter = incomeAdapter(payerIDList,paidDateTimeList,paidAmountList,propertyNameList,durationList)

                                        }
                                    }

                                })
                            }
                        }

                    })

                }
            }

        })
        return root
    }

}