package com.example.setapakhouse.Fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.setapakhouse.Adapter.outgoingAdapter
import com.example.setapakhouse.Model.Payment
import com.example.setapakhouse.Model.Property
import com.example.setapakhouse.Model.Rent
import com.example.setapakhouse.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_outgoing.*
import kotlinx.android.synthetic.main.fragment_outgoing.view.*


class outgoingFragment : Fragment() {

    lateinit var ref: DatabaseReference
    lateinit var ref1: DatabaseReference
    lateinit var ref2: DatabaseReference
    lateinit var paymentList:MutableList<Payment>
    lateinit var propertyNameList:MutableList<String>
    lateinit var propertyList:MutableList<Property>
    lateinit var receiverProfileList:MutableList<String>
    lateinit var receiverNameList:MutableList<String>
    lateinit var receivedDateTimeList:MutableList<String>
    lateinit var receivedAmountList:MutableList<String>
    lateinit var receiverIDList:MutableList<String>
    lateinit var rentList:MutableList<Rent>
    lateinit var rewardList:MutableList<Int>
    lateinit var durationList:MutableList<String>
    val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root:View= inflater.inflate(R.layout.fragment_outgoing, container, false)
        durationList= mutableListOf()
        rewardList= mutableListOf()
        propertyList= mutableListOf()
        propertyNameList= mutableListOf()
        rentList= mutableListOf()
        paymentList= mutableListOf()
        receiverProfileList= mutableListOf()
        receiverNameList= mutableListOf()
        receivedDateTimeList= mutableListOf()
        receivedAmountList= mutableListOf()
        receiverIDList= mutableListOf()


        //check which payment is paid
        ref= FirebaseDatabase.getInstance().getReference("Payment")
        ref.addValueEventListener(object : ValueEventListener {
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
                    //find the userid in rent is equals to current user
                    ref1=FirebaseDatabase.getInstance().getReference("Rent")
                    ref1.addValueEventListener(object :ValueEventListener{
                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.exists()){
                                rentList.clear()
                                for(h in snapshot.children){
                                    if(h.child("userID").getValue().toString().equals(currentUserID)) {
                                        val rent = h.getValue(Rent::class.java)
                                        rentList.add(rent!!)
                                        //Toast.makeText(context,rentList.size.toString(), Toast.LENGTH_SHORT).show()
                                    }
                                }

                                //check which property is not belong to current user
                                ref2=FirebaseDatabase.getInstance().getReference("Property")
                                ref2.addValueEventListener(object:ValueEventListener{
                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if(snapshot.exists()){
                                            receivedDateTimeList.clear()
                                            receivedAmountList.clear()
                                            propertyNameList.clear()
                                            receiverIDList.clear()
                                            rewardList.clear()
                                            durationList.clear()
                                            for(p in paymentList){
                                                for(pr in snapshot.children){
                                                    if(!(pr.child("userID").getValue().toString().equals(currentUserID))){
                                                        for(r in rentList){

                                                            if(pr.child("propertyID").getValue().toString().equals(r.propertyID) &&
                                                                r.rentID.equals(p.rentID)){
                                                                //Toast.makeText(context,h.child("propertyName").getValue().toString(), Toast.LENGTH_SHORT).show()
                                                                receiverIDList.add(
                                                                    pr.child("userID").getValue()
                                                                        .toString()
                                                                )
                                                                durationList.add(p.paymentTitle)
                                                                rewardList.add(p.rewardPointUsed)
                                                                receivedDateTimeList.add(p.paymentDate)
                                                                receivedAmountList.add(p.paymentAmount.toString())
                                                                propertyNameList.add(pr.child("propertyName").getValue().toString())

                                                            }

                                                        }
                                                    }
                                                }
                                            }

                                            if(receiverIDList.size==0){
                                                root.noRecordFound.visibility = View.VISIBLE
                                            }
                                            else{
                                                root.noRecordFound.visibility = View.GONE
                                            }

                                            val mLayoutManager = LinearLayoutManager(context)
                                            mLayoutManager.reverseLayout = true
                                            root.outgoingRecycle.layoutManager = mLayoutManager
                                            root.outgoingRecycle.adapter = outgoingAdapter(receiverIDList,receivedDateTimeList,receivedAmountList,propertyNameList,rewardList,durationList)

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