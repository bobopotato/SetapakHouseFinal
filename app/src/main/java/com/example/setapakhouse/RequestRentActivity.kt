package com.example.setapakhouse

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.setapakhouse.Model.Approval
import com.example.setapakhouse.Model.Notification
import com.example.setapakhouse.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_request_rent.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class RequestRentActivity : AppCompatActivity() {

    lateinit var ref1: DatabaseReference
    lateinit var ref2: DatabaseReference
    lateinit var ref3: DatabaseReference
    lateinit var ref4: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_rent)

        val cal: Calendar = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 1)
        chooseCalendar.setMinDate(cal.getTimeInMillis())
        chooseCalendar

        val selectedPropertyID=intent.getStringExtra("SelectedPropertyID")
        val selectedUserID=intent.getStringExtra("SelectedUserID")

        val currentUserID= FirebaseAuth.getInstance().currentUser!!.uid

        ref1 = FirebaseDatabase.getInstance().getReference("Approval")
        ref2 = FirebaseDatabase.getInstance().getReference("Property").child(selectedPropertyID)
        ref3 = FirebaseDatabase.getInstance().getReference("Notification")
        ref4 = FirebaseDatabase.getInstance().getReference("Users").child(currentUserID)


        val approvalID = ref1.push().key.toString()
        val notificationID = ref3.push().key.toString()


        sendRequestBtn.setOnClickListener {

            val day = chooseCalendar.dayOfMonth
            val month = chooseCalendar.month
            val year = chooseCalendar.year
            val date = day.toString() + "/" + (month+1).toString() + "/" + year.toString()

            //Toast.makeText(this, "abc = " + getPropertyDetail(),Toast.LENGTH_SHORT).show()
            ref2.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val propertyName = snapshot.child("propertyName").getValue().toString()

                        ref4.addValueEventListener(object: ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }

                            override fun onDataChange(p0: DataSnapshot) {

                                if(p0.exists()){
                                        val targetUser = p0.getValue(User::class.java)
                                        val approvalContent = targetUser!!.username + " had requested to rent your " + propertyName +" property on "
                                        val notificationContent = targetUser.username + " had requested to rent one of your property"

                                        //Toast.makeText(this@RequestRentActivity, "abc = " + currentUserID + "wtf = " +  targetUser!!.username, Toast.LENGTH_SHORT).show()
                                        val storeApproval = Approval(
                                            approvalID,
                                            approvalContent,
                                            getTime(),
                                            "first",
                                            "second",
                                            "pending",
                                            currentUserID,
                                            selectedPropertyID,
                                            notificationID
                                        )

                                    ref1.child(approvalID).setValue(storeApproval)

                                    val storeNotification = Notification(
                                        notificationID,
                                        currentUserID,
                                        "delivered",
                                        notificationContent,
                                        getTime(),
                                        "approval",
                                        selectedUserID
                                    )

                                    ref3.child(notificationID).setValue(storeNotification)


                                }
                            }
                        })


                        val builder = AlertDialog.Builder(this@RequestRentActivity)
                        builder.setTitle("Request have been sent to owner")
                        builder.setMessage("You will be redirected to the main page.")

                        builder.setNeutralButton(
                            "Okay",
                            { dialog: DialogInterface?, which: Int ->
                                val intent = Intent(
                                    this@RequestRentActivity,
                                    MainActivity::class.java
                                )
                                startActivity(intent)
                            })
                        builder.setCancelable(false)
                        builder.show()


                    }
                }

            })
        }


    }



    private fun getTime(): String {

        val today = LocalDateTime.now(ZoneId.systemDefault())

        return today.format(DateTimeFormatter.ofPattern("d MMM uuuu HH:mm:ss "))
    }

}