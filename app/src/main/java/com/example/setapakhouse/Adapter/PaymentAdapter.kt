package com.example.setapakhouse.Adapter

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.setapakhouse.Fragment.NotificationFragment
import com.example.setapakhouse.Model.*
import com.example.setapakhouse.PaymentActivity2
import com.example.setapakhouse.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_notification.*

class PaymentAdapter(val paymentList:MutableList<Payment>, val rentList:MutableList<Rent>): RecyclerView.Adapter<PaymentAdapter.ViewHolder>() {

    lateinit var query: Query
    lateinit var ref : DatabaseReference
    lateinit var ref2 : DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.payment_layout_item,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {

        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid

        for(x in rentList){
            if(x.rentID.equals(paymentList[position].rentID)){
                ref = FirebaseDatabase.getInstance().getReference("Property").child(x.propertyID)

                ref.addValueEventListener(object: ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            holder.propertyName.text = snapshot.child("propertyName").getValue().toString()

                            ref2 = FirebaseDatabase.getInstance().getReference("PropertyImage")

                            ref2.addValueEventListener(object: ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                                override fun onDataChange(snapshot1: DataSnapshot) {
                                    if (snapshot1.exists()) {
                                        for(h1 in snapshot1.children){
                                            if(h1.child("imageName").getValue().toString().equals("image1") && h1.child("propertyID").getValue().toString().equals(x.propertyID)){
                                                Picasso.get().load(h1.child("imageSource").getValue().toString()).into(holder.propertyImage)
                                                holder.hiddenUserID.text = snapshot.child("userID").getValue().toString()
                                                //Log.d("Abb", "zz123 = " + holder.hiddenUserID.text)
                                            }
                                        }
                                    }
                                }
                            })
                        }
                    }
                })

            }
        }

        holder.paymentTitle.text = paymentList[position].paymentTitle
        holder.paymentTitle.underline()
        holder.priceText.text = "RM" + paymentList[position].paymentAmount.toString()
        //holder.propertyName.text = rentList[position].propertyID

        holder.payButton.setOnClickListener {
            val intent = Intent(holder.propertyName.context, PaymentActivity2::class.java)
            intent.putExtra("PaymentAmount", paymentList[position].paymentAmount)
            intent.putExtra("PaymentID", paymentList[position].paymentID)
            intent.putExtra("TargetUserID", holder.hiddenUserID.text)
            holder.propertyName.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return paymentList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //val wholeLayout : LinearLayout = itemView.findViewById(R.id.notificationCard)
        val propertyImage : ImageView = itemView.findViewById(R.id.propertyImage1)
        val propertyName : TextView = itemView.findViewById(R.id.propertyName)
        val paymentTitle : TextView = itemView.findViewById(R.id.paymentTitle)
        val priceText : TextView = itemView.findViewById(R.id.price)
        val payButton : Button = itemView.findViewById(R.id.payBtn)
        val hiddenUserID : TextView = itemView.findViewById(R.id.hiddenUserID123)


        //val uploadedImage : ImageView = itemView.findViewById(R.id.uploadedImage)
        //val imageName : TextView = itemView.findViewById(R.id.imageName)

    }

    fun TextView.underline() {
        paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }



}