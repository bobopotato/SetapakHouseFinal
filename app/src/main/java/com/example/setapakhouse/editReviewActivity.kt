package com.example.setapakhouse

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import com.example.setapakhouse.Model.Topup
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_review.*
import kotlinx.android.synthetic.main.activity_edit_review.propertyName
import kotlinx.android.synthetic.main.fragment_topup.*

class editReviewActivity : AppCompatActivity() {
    lateinit var ref:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_review)

        val selectedReviewID=intent.getStringExtra("selectedReviewID")
        val selectedReviewContent=intent.getStringExtra("selectedReviewContent")
        val numStar=intent.getStringExtra("selectedNumStar")
        val selectedPropertyID=intent.getStringExtra("selectedPropertyID")

        newRating.setRating(numStar.toFloat())
        contentText.setText(selectedReviewContent.toString())
        countText.setText(contentText.text.trim().toString().length.toString() + "/100")

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
                ref = FirebaseDatabase.getInstance().getReference("Review").child(selectedReviewID)
                ref.child("numStar").setValue(newRating.rating.toDouble())
                ref.child("reviewContent").setValue(contentText.text.toString())
                Toast.makeText(this, "UPDATED SUCCESSFUL", Toast.LENGTH_LONG).show()
                finish()
            }
        }
        ref=FirebaseDatabase.getInstance().getReference("Property")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(h in snapshot.children){
                        if(h.child("propertyID").getValue().toString().equals(selectedPropertyID)){
                            propertyName.text=h.child("propertyName").getValue().toString()

                        }
                    }
                }
            }

        })
        ref=FirebaseDatabase.getInstance().getReference("PropertyImage")
        ref.addValueEventListener(object : ValueEventListener {
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