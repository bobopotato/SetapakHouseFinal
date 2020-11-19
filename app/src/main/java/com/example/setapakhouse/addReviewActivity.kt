package com.example.setapakhouse

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.setapakhouse.Model.Notification
import com.example.setapakhouse.Model.Review
import com.example.setapakhouse.Model.Topup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_review.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class addReviewActivity : AppCompatActivity() {
    val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
    lateinit var ref: DatabaseReference
    lateinit var ref1: DatabaseReference
    lateinit var ref2: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_review)

        val selectedReviewID=intent.getStringExtra("selectedReviewID")
        val selectedPropertyID=intent.getStringExtra("selectedPropertyID")
        val selectedPropertyName=intent.getStringExtra("selectedPropertyName")
        propertyName.text=selectedPropertyName
        //Toast.makeText(this@addReviewActivity,hiddenReviewID.text.toString(),Toast.LENGTH_SHORT).show()

        ref1=FirebaseDatabase.getInstance().getReference("Users")
        ref1.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for(h in snapshot.children){
                    if(h.child("userID").getValue().toString().equals(currentUserID)){
                        hiddenUsername.text=h.child("username").getValue().toString()
                        Log.d("QQ",hiddenUsername.text.toString())
                    }
                }
            }

        })
        ref1=FirebaseDatabase.getInstance().getReference("Property")
        ref1.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for(h in snapshot.children){
                    if(h.child("propertyID").getValue().toString().equals(selectedPropertyID)){
                        hiddenReceiverName.text=h.child("userID").getValue().toString()
                    }
                }
            }

        })
        ref=FirebaseDatabase.getInstance().getReference("PropertyImage")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for(h in snapshot.children){
                    if(h.child("propertyID").getValue().toString().equals(selectedPropertyID) &&
                        h.child("imageName").getValue().toString().equals("image1")){
                        Picasso.get().load(h.child("imageSource").getValue().toString()).placeholder(R.drawable.ic_home).into(propertyImage)
                    }
                }
            }

        })

        ref=FirebaseDatabase.getInstance().getReference("Review")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(h in snapshot.children){
                        if(h.child("userID").getValue().toString().equals(currentUserID)&&h.child("propertyID").getValue().toString().equals(selectedPropertyID)&&
                            h.child("status").getValue().toString().equals("pending")){
                            hiddenReviewID.text=h.child("reviewID").getValue().toString()
                        }
                    }
                }
            }

        })
        editSection.setOnClickListener {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }

        editBackBtn.setOnClickListener{
            finish()
        }

        contentText.addTextChangedListener {
            countText.setText(contentText.text.trim().toString().length.toString() + "/100")
            validateContent()
        }
        doneBtn.setOnClickListener {

            if(validateContent()) {

                ref2 = FirebaseDatabase.getInstance().getReference("Notification")
                var notificationID = ref2.push().key.toString()

                ref = FirebaseDatabase.getInstance().getReference("Review")
                val reviewID = ref.push().key.toString()
                var review = Review(
                    reviewID,
                    contentText.text.toString(),
                    getTime(),
                    newRating.rating.toDouble(),
                    selectedPropertyID,
                    currentUserID,
                    notificationID,
                    "completed"
                )
                ref.child(reviewID).setValue(review)

                ref= FirebaseDatabase.getInstance().getReference("Review").child(hiddenReviewID.text.toString())
                ref.removeValue()

                //add notification




                Log.d("QQ1",hiddenUsername.text.toString())
                //IMPORTANT - change the user ID to username
                val notificationContent = hiddenUsername.text.toString() + " had gave a rating to your property " + propertyName.text.toString()

                val storeNotification = Notification(
                    notificationID,
                    currentUserID,
                    "delivered",
                    notificationContent,
                    getTime(),
                    "review",
                    hiddenReceiverName.text.toString()
                )
                ref2.child(notificationID).setValue(storeNotification)
                Toast.makeText(this, "ADDED SUCCESSFUL", Toast.LENGTH_LONG).show()
                finish()
            }
        }


    }


    private fun getTime(): String {

        val today = LocalDateTime.now(ZoneId.systemDefault())

        return today.format(DateTimeFormatter.ofPattern("d MMM uuuu HH:mm:ss "))
    }

    private fun validateContent():Boolean{
        var content = contentText.text.toString().trim()

        if(content.length>100){
            contentText.setError("                     Content too long !")
            contentText.requestFocus()
            countText.setTextColor(getColor(R.color.red))
            return false
        }else{
            countText.setTextColor(getColor(R.color.black))
        }

        if(content.isEmpty()){
            contentText.setError("                     Field can't be empty !")
            contentText.requestFocus()
            return false
        }else{
            contentText.setError(null)
            return true
        }
    }
}