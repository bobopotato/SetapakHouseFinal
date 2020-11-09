package com.example.setapakhouse.Fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.example.setapakhouse.*
import com.example.setapakhouse.Model.Property
import com.example.setapakhouse.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail_post.view.*
import kotlinx.android.synthetic.main.change_or_edit.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileFragment : Fragment() {

    lateinit var ref: DatabaseReference
    lateinit var ref1: DatabaseReference
    lateinit var propertyIDList:MutableList<Property>
    val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root:View = inflater.inflate(R.layout.fragment_profile, container, false)

        propertyIDList= mutableListOf()

        root.reviewSection.setOnClickListener {
            var intent=Intent(context,myReviewActivity::class.java)
            startActivity(intent)
        }
        root.editBtn.setOnClickListener{
            changeOrEdit(root)
        }
        root.topupSection.setOnClickListener {
            var intent=Intent(context,myPaymentActivity::class.java)
            startActivity(intent)

        }
        displayProfile(root)
        return root
    }

    private fun changeOrEdit(root:View){
        val mDialogView = LayoutInflater.from(activity!!).inflate(R.layout.change_or_edit,null)
        val mBuilder = AlertDialog.Builder(activity!!)
            .setView(mDialogView)
        val mAlertDialog=mBuilder.show()
        mDialogView.changePwBtn.setOnClickListener {
            startActivity(Intent(activity,changepwaActivity::class.java)) //changepass
            mAlertDialog.dismiss()
        }
        mDialogView.editInfoBtn.setOnClickListener {
            startActivity(Intent(activity,editProfileActivity::class.java)) //editprofile
            mAlertDialog.dismiss()
        }
        mDialogView.editBackBtn.setOnClickListener {
            mAlertDialog.dismiss()
        }


    }

    private fun displayProfile(root: View) {
        ref=FirebaseDatabase.getInstance().getReference("Users")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(h in snapshot.children){
                        if(h.child("userID").getValue().toString().equals(currentUserID)) {
                            Picasso.get().load(h.child("image").getValue().toString())
                                .placeholder(R.drawable.ic_profile).into(root.imgProfile)
                            root.usernameTxt.text = h.child("username").getValue().toString()
                            root.txtFullName.text = h.child("fullName").getValue().toString()
                            root.txtEmail.text = h.child("email").getValue().toString()
                            root.txtPhone.text = h.child("phoneNumber").getValue().toString()
                            root.txtWallet.text = "RM " + h.child("balance").getValue().toString()
                            root.txtGift.text =
                                h.child("rewardPoint").getValue().toString() + " Points"
                        }

                    }
                }
            }

        })


        var totalRating:Double
        var countRating:Int


        readData(object:FirebaseCallback{
            override fun onCallback(list: MutableList<Property>) {
                root.txtProperty.text=propertyIDList.size.toString()
                ref1=FirebaseDatabase.getInstance().getReference("Review")
                ref1.addValueEventListener(object :ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            countRating=0
                            totalRating=0.0
                            for(h in snapshot.children){
                                for(i in propertyIDList){
                                    if(h.child("propertyID").getValue().toString().equals(i.propertyID)&&h.child("status").getValue().toString().equals("completed")){
                                        countRating+=1
                                        totalRating+=h.child("numStar").getValue().toString().toDouble()
                                    }
                                }
                            }
                            if(totalRating.equals(0.0)){
                                root.txtReview.text = "0.0"
                            }else {
                                root.txtReview.text =
                                    String.format("%.1f", totalRating / countRating)
                                root.personRating.setRating((totalRating / countRating).toFloat())
                            }
                        }
                    }

                })
            }
        })
    }

    private fun readData(firebaseCallback:FirebaseCallback){

        ref=FirebaseDatabase.getInstance().getReference("Property")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){

                    for(h in snapshot.children){
                        if(h.child("userID").getValue().toString().equals(currentUserID)){
                            val property=h.getValue(Property::class.java)
                            propertyIDList.add(property!!)
                        }
                    }
                    firebaseCallback.onCallback(propertyIDList)
                }
            }

        })
    }


    private interface FirebaseCallback{
        fun onCallback(list : MutableList<Property>)
    }

}