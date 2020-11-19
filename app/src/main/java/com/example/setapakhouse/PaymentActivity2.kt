package com.example.setapakhouse

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.example.setapakhouse.Model.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_payment2.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

class PaymentActivity2 : AppCompatActivity() {

    lateinit var ref1 :DatabaseReference
    lateinit var ref2 :DatabaseReference
    lateinit var ref3 :DatabaseReference
    lateinit var epicDialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment2)

        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val paymentID = intent.getStringExtra("PaymentID")!!
        val targetUserID = intent.getStringExtra("TargetUserID")!!
        val paymentAmount = String.format("%.2f",intent.getDoubleExtra("PaymentAmount", 0.0)!!)

        Log.d("hellomamam", "abc =" + targetUserID)

        epicDialog = Dialog(this)

        paymentText.text = paymentAmount
        totalPaymentText.text = paymentAmount
        text123.text = "Total payment to pay to : RM " + paymentAmount
        discountText.text = "0.00"

        backBtn.setOnClickListener {
            finish()
        }

        ref1 = FirebaseDatabase.getInstance().getReference("Users").child(currentUserID)

        ref1.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val rewardPoint = snapshot.child("rewardPoint").getValue().toString().toInt()
                    val balance = snapshot.child("balance").getValue().toString()
                    balanceText.text = String.format("%.2f",balance.toDouble())
                    hiddenUsername.text = snapshot.child("username").getValue().toString()

                    if(rewardPoint == 0){
                        //rewardPointText.visibility = View.GONE
                        rewardPointText.text = "You have no reward point to redeem any discount..."
                        radioGroup1.visibility = View.GONE
                    }
                    else{
                        rewardPointText.text = "Do you want to spend your " + rewardPoint + " reward points to get discount?"
                        radioGroup1.visibility = View.VISIBLE
                        hiddenRewardPoint.text = rewardPoint.toString()
                    }

                    balanceAfterPayText.text = String.format("%.2f",balance.toDouble() - totalPaymentText.text.toString().toDouble())

                }
            }
        })


        radioGroup1.setOnCheckedChangeListener { group, checkedId ->
            if(yesBtn.isChecked){
                val rewardPoint = hiddenRewardPoint.text.toString().toDouble()
                discountText.text = String.format("%.2f",rewardPoint)
            }

            if(noBtn.isChecked){
                discountText.text = "0.00"
            }
        }


        discountText.addTextChangedListener {
            val discount = discountText.text.toString().toDouble()
            val totalPayment = String.format("%.2f",(paymentAmount.toDouble() - discount))
            totalPaymentText.text = totalPayment
            balanceAfterPayText.text = String.format("%.2f",balanceText.text.toString().toDouble() - totalPaymentText.text.toString().toDouble())
            text123.text = "Total payment to pay to : RM " + totalPayment
        }

        confirmPayBtn.setOnClickListener {

            val balance1 = balanceAfterPayText.text.toString().toDouble()

            if(balance1 <0){
                showDialog1()
            }
            else{
                epicDialog.setContentView(R.layout.popup_confirmation)

                val yesButton: Button = epicDialog.findViewById(R.id.yesBtn)
                val cancelButton: Button = epicDialog.findViewById(R.id.cancelBtn)
                val title: TextView = epicDialog.findViewById(R.id.title)
                val content: TextView = epicDialog.findViewById(R.id.content)

                title.text = "Payment Confirmation"
                content.text = "Are you sure to pay?"
                yesButton.text = "Confirm"
                yesButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

                yesButton.setOnClickListener {
                    val balance = balanceAfterPayText.text.toString().toDouble()
                    var currentRewardPoint = hiddenRewardPoint.text.toString()
                    val rewardPointGained =  (paymentAmount.toDouble() / 100).roundToInt().toString()

                    if(currentRewardPoint.isEmpty()){
                        currentRewardPoint = "0"
                    }

                    //change payment status to paid
                    ref2 = FirebaseDatabase.getInstance().getReference("Payment").child(paymentID)
                    ref2.child("paymentDate").setValue(getTime())
                    ref2.child("status").setValue("paid")



                    //update user - balance, reward point +/-
                    ref1.child("balance").setValue(balance)

                    if(yesBtn.isChecked){
                        ref1.child("rewardPoint").setValue(rewardPointGained.toInt())
                        //Log.d("antimage112", "aa = " + rewardPointGained)
                    }

                    if(noBtn.isChecked){
                        val newRewardPoint = currentRewardPoint.toInt() + rewardPointGained.toInt()
                        ref1.child("rewardPoint").setValue(newRewardPoint)
                    }

                    //send notification to payment receiver
                    ref2 = FirebaseDatabase.getInstance().getReference("Notification")
                    var notificationID = ref2.push().key.toString()

                    //set payment notificationID
                    ref3 = FirebaseDatabase.getInstance().getReference("Payment").child(paymentID)
                    ref3.child("notificationID").setValue(notificationID)

                    val notificationContent = hiddenUsername.text.toString() + " had paid for your rental"

                    val storeNotification = Notification(
                        notificationID,
                        currentUserID,
                        "delivered",
                        notificationContent,
                        getTime(),
                        "approvalConfirmation",
                        targetUserID
                    )

                    ref2.child(notificationID).setValue(storeNotification)

                    //add targetUser balance++
                    var store = 0
                    ref1 = FirebaseDatabase.getInstance().getReference("Users").child(targetUserID)
                    ref1.addValueEventListener(object: ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (store == 0){
                                if (snapshot.exists()) {
                                    val balance = snapshot.child("balance").getValue().toString()
                                    val newBalance = balance.toDouble() + paymentAmount.toDouble()

                                    ref1.child("balance").setValue(newBalance)
                                }
                                store++
                            }
                        }
                    })

                    showDialog(epicDialog, rewardPointGained)
                    //epicDialog.dismiss()

                }

                cancelButton.setOnClickListener {
                    epicDialog.dismiss()
                }
                epicDialog.setCancelable(true)
                epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                epicDialog.show()

            }

        }

    }

    private fun getTime(): String {

        val today = LocalDateTime.now(ZoneId.systemDefault())

        return today.format(DateTimeFormatter.ofPattern("d MMM uuuu HH:mm:ss "))
    }

    private fun showDialog(abc : Dialog, rewardPointGained : String){
        epicDialog.setContentView(R.layout.popup_positive)
        //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
        val okButton : Button = epicDialog.findViewById(R.id.okBtn)
        val title : TextView = epicDialog.findViewById(R.id.title)
        val content : TextView = epicDialog.findViewById(R.id.content)

        title.text = "Payment Successful"
        content.text = "You have gained " + rewardPointGained + " reward point(s)"

        okButton.setOnClickListener {
            epicDialog.dismiss()
            val intent = Intent(this@PaymentActivity2, PaymentActivity::class.java)
            startActivity(intent)
            finish()
        }
        epicDialog.setCancelable(true)
        epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        epicDialog.show()
    }

    private fun showDialog1(){
        epicDialog.setContentView(R.layout.popup_error)
        //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
        val okButton : Button = epicDialog.findViewById(R.id.okBtn)
        val title : TextView = epicDialog.findViewById(R.id.title)
        val content : TextView = epicDialog.findViewById(R.id.content)

        title.text = "Insufficient Balance"
        content.text = "Your balance is not enough to make this payment!"

        okButton.setOnClickListener {
            epicDialog.dismiss()
        }
        epicDialog.setCancelable(true)
        epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        epicDialog.show()
    }
}