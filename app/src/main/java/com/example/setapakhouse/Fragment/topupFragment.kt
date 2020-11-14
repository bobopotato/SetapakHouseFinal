package com.example.setapakhouse.Fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import com.example.setapakhouse.ConfirmAct
import com.example.setapakhouse.Model.Property
import com.example.setapakhouse.Model.Topup
import com.example.setapakhouse.Model.User
import com.example.setapakhouse.Model.UserInfo
import com.example.setapakhouse.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.paypal.android.sdk.payments.PayPalConfiguration
import com.paypal.android.sdk.payments.PayPalPayment
import com.paypal.android.sdk.payments.PayPalService
import com.paypal.android.sdk.payments.PaymentActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_topup.*
import kotlinx.android.synthetic.main.fragment_topup.view.*
import kotlinx.android.synthetic.main.fragment_topup.view.profileSection
import kotlinx.android.synthetic.main.fragment_topup.view.txtWallet
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class topupFragment : Fragment() {

    lateinit var ref:DatabaseReference
    val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
    var config: PayPalConfiguration?=null
    var amount:Double=0.0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root:View= inflater.inflate(R.layout.fragment_topup, container, false)
        root.balanceSection.setOnClickListener {
            val inputMethodManager = getActivity()!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
        root.amountSection.setOnClickListener {
            val inputMethodManager = getActivity()!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
        root.amount1.setOnClickListener {
            root.txtAmount.setText(root.amount1.text.toString())
        }
        root.amount2.setOnClickListener {
            root.txtAmount.setText(root.amount2.text.toString())
        }
        root.amount3.setOnClickListener {
            root.txtAmount.setText(root.amount3.text.toString())
        }
        root.amount4.setOnClickListener {
            root.txtAmount.setText(root.amount4.text.toString())
        }
        root.amount5.setOnClickListener {
            root.txtAmount.setText(root.amount5.text.toString())
        }
        root.amount6.setOnClickListener {
            root.txtAmount.setText(root.amount6.text.toString())
        }

        displayProfile(root)
        config=PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX).clientId(
            UserInfo.client_id)
        var i= Intent(getActivity(), PayPalService::class.java)
        i.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config)
        getActivity()!!.startService(i)

        root.paymentBtn.setOnClickListener {
            //amount=paymentAmount.text.toString().toDouble()
            amount=txtAmount.text.toString().toDouble()
            var payment= PayPalPayment(
                BigDecimal.valueOf(amount),"MYR","SETAPAK HOUSE",
                PayPalPayment.PAYMENT_INTENT_SALE)
            var intent= Intent(getActivity(), PaymentActivity::class.java)
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config)
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payment)
            startActivityForResult(intent,123)

        }
        return root
    }

    private fun displayProfile(root: View) {
        ref= FirebaseDatabase.getInstance().getReference("Users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(h in snapshot.children){
                        if(h.child("userID").getValue().toString().equals(currentUserID)) {
                            root.txtWallet.text = h.child("balance").getValue().toString()

                        }

                    }
                }
            }

        })

    }

    private fun getTime(): String {

        val today = LocalDateTime.now(ZoneId.systemDefault())

        return today.format(DateTimeFormatter.ofPattern("d MMM uuuu HH:mm:ss "))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==123){
            if(resultCode== Activity.RESULT_OK){
                ref=FirebaseDatabase.getInstance().getReference("Users").child(currentUserID)
                var totalBalance=txtAmount.text.toString().toDouble()+txtWallet.text.toString().toDouble()
                ref.child("balance").setValue(totalBalance)

                ref=FirebaseDatabase.getInstance().getReference("Topup")
                val topupID = ref.push().key.toString()
                var topup= Topup(topupID,getTime(),txtAmount.text.toString().toDouble(),currentUserID)
                ref.child(topupID).setValue(topup)
                Toast.makeText(context,"Top-up Successful",Toast.LENGTH_LONG).show()
                getView()!!.txtAmount.getText().clear()
                //var obj=Intent(getActivity(), ConfirmAct::class.java)
                //startActivity(obj)
            }
        }
    }

    override fun onDestroy() {
        getActivity()!!.stopService(Intent(getActivity(),PayPalService::class.java))
        super.onDestroy()
    }


}