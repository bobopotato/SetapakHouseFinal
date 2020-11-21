package com.example.setapakhouse.Adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.preference.PreferenceManager
import android.provider.Settings.Global.getString
import android.text.TextUtils.replace
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.setapakhouse.Fragment.NotificationFragment
import com.example.setapakhouse.Model.*
import com.example.setapakhouse.R
import com.example.setapakhouse.detailPost
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class ApprovalAdapter(val approvalList: MutableList<Approval>): RecyclerView.Adapter<ApprovalAdapter.ViewHolder>() {

    lateinit var query: Query
    lateinit var query2: Query
    lateinit var ref1: DatabaseReference
    lateinit var ref2: DatabaseReference
    lateinit var ref3: DatabaseReference
    lateinit var ref4: DatabaseReference
    lateinit var ref5: DatabaseReference
    lateinit var ref6: DatabaseReference
    lateinit var ref7: DatabaseReference
    lateinit var epicDialog : Dialog
    var store1 = 1
    var store2 = 1
    var store3 = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view : View = LayoutInflater.from(parent.context).inflate(
            R.layout.approval_layout,
            parent,
            false
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {

        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid

        epicDialog = Dialog(holder.wholeLayout.context)

        query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("userID").equalTo(currentUserID)

        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()) {
                    for (h in p0.children) {
                        val targetUser = h.getValue(User::class.java)
                        val user = targetUser!!.username

                        holder.hiddenValue.text = user

                    }
                }
            }
        })

        query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("userID").equalTo(
            approvalList[position].userID
        )

        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()) {
                    for (h in p0.children) {
                        val targetUser = h.getValue(User::class.java)
                        val user = targetUser!!.username
                        val profilePhoto = targetUser!!.image

                        holder.username.text = user
                        Picasso.get().load(profilePhoto).placeholder(R.drawable.profile)
                            .into(holder.profilePhoto)

                    }
                }
            }
        })

        holder.date.text = approvalList[position].approvalDateTime
        holder.content.text = approvalList[position].approvalContent

        if(approvalList[position].status == "pending"){
            holder.wholeLayout.setBackgroundColor(Color.rgb(204, 230, 255))
            holder.approveBtn.visibility = View.VISIBLE
            holder.rejectBtn.visibility = View.VISIBLE
            holder.statusBtn.visibility = View.GONE
            Log.d("colourTest", position.toString())
        }
        if(approvalList[position].status == "rejected"){
            holder.approveBtn.visibility = View.GONE
            holder.rejectBtn.visibility = View.GONE
            holder.statusBtn.visibility = View.VISIBLE
            holder.statusBtn.setText("Status : Rejected")
            holder.statusBtn.setBackgroundResource(R.drawable.round_button_red)
            holder.wholeLayout.setBackgroundColor(Color.rgb(255, 255, 255))
            //holder.wholeLayout.setBackgroundColor(Color.rgb(128,128,128))
        }

        if(approvalList[position].status == "unavailable"){
            holder.approveBtn.visibility = View.GONE
            holder.rejectBtn.visibility = View.GONE
            holder.statusBtn.visibility = View.VISIBLE
            holder.statusBtn.setText("Status : Unavailable")
            holder.statusBtn.setBackgroundResource(R.drawable.round_button_disable2)

            holder.wholeLayout.setBackgroundColor(Color.rgb(255, 255, 255))
            //holder.wholeLayout.setBackgroundColor(Color.rgb(128,128,128))
        }

        if(approvalList[position].status == "approved"){
            holder.approveBtn.visibility = View.GONE
            holder.rejectBtn.visibility = View.GONE
            holder.statusBtn.visibility = View.VISIBLE
            holder.statusBtn.setText("Status : Approved")
            holder.wholeLayout.setBackgroundColor(Color.rgb(255, 255, 255))
            holder.statusBtn.setBackgroundResource(R.drawable.round_button_green)
            //holder.wholeLayout.setBackgroundColor(Color.rgb(128,128,128))
        }

        if(approvalList[position].status == "expired"){
            holder.approveBtn.visibility = View.GONE
            holder.rejectBtn.visibility = View.GONE
            holder.statusBtn.visibility = View.VISIBLE
            holder.statusBtn.setText("Status : Expired")
            holder.wholeLayout.setBackgroundColor(Color.rgb(255, 255, 255))
            holder.statusBtn.setBackgroundResource(R.drawable.round_button_disable)
            //holder.wholeLayout.setBackgroundColor(Color.rgb(128,128,128))
        }

        //onclick go in detailPost
        holder.wholeLayout.setOnClickListener{


            if(approvalList[position].status == "unavailable1"){

                showDialog3()
                val intent = Intent(holder.wholeLayout.context, detailPost::class.java)
                intent.putExtra("selectedPosition", approvalList[position].propertyID)
                intent.putExtra("selectedUserID",currentUserID)
                holder.wholeLayout.context.startActivity(intent)
            }else{
                val intent = Intent(holder.wholeLayout.context, detailPost::class.java)
                intent.putExtra("selectedPosition", approvalList[position].propertyID)
                intent.putExtra("selectedUserID",currentUserID)
                holder.wholeLayout.context.startActivity(intent)
            }
        }


        holder.approveBtn.setOnClickListener {
            //var username123 = holder.username.text.toString()
            epicDialog.setContentView(R.layout.popup_confirmation)
            //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
            val yesButton: Button = epicDialog.findViewById(R.id.yesBtn)
            val cancelButton: Button = epicDialog.findViewById(R.id.cancelBtn)
            val title: TextView = epicDialog.findViewById(R.id.title)
            val content: TextView = epicDialog.findViewById(R.id.content)

            title.text = "Approval Confirmation"
            content.text = "Are you sure to approve this request?"
            yesButton.text = "Approve Request"

            yesButton.setOnClickListener {
                ref1 = FirebaseDatabase.getInstance().getReference("Approval")
                    .child(approvalList[position].approvalID)
                ref2 = FirebaseDatabase.getInstance().getReference("Notification")

                ref3 = FirebaseDatabase.getInstance().getReference("Property")
                    .child(approvalList[position].propertyID)


                //Send notification to the requester
                ref3.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        if (p0.exists()) {
                            val targetProperty = p0.getValue(Property::class.java)
                            val propertyName = targetProperty!!.propertyName

                            var notificationID = ref2.push().key.toString()
                            //IMPORTANT - change the user ID to username
                            val notificationContent =
                                holder.hiddenValue.text.toString() + " had approved your request to rent " + propertyName

                            val storeNotification = Notification(
                                notificationID,
                                currentUserID,
                                "delivered",
                                notificationContent,
                                getTime(),
                                "approvalConfirmation",
                                approvalList[position].userID
                            )

                            if (store1 == 1) {
                                ref2.child(notificationID).setValue(storeNotification)
                                store1++
                            }

                        }
                    }
                })

                //Create Rent in firebase
                ref4 = FirebaseDatabase.getInstance().getReference("Rent")

                val rentID = ref4.push().key.toString()

                val storeRent = Rent(
                    rentID,
                    approvalList[position].requestStartDate,
                    approvalList[position].requestEndDate,
                    "new",
                    approvalList[position].propertyID,
                    approvalList[position].userID
                )
                ref4.child(rentID).setValue(storeRent)

                var firstDate = approvalList[position].requestStartDate.split("/")
                var secondDate = approvalList[position].requestEndDate.split("/")

                var day1 = firstDate[0].toInt()
                var month1 = firstDate[1].toInt()
                var year1 = firstDate[2].toInt()
                var firstShort = day1.toString() + "/" + month1.toString() + "/" + year1.toString()

                var day2 = secondDate[0].toInt()
                var month2 = secondDate[1].toInt()
                var year2 = secondDate[2].toInt()
                var secondShort = day2.toString() + "/" + month2.toString() + "/" + year2.toString()

                var firstt = LocalDate.of(year1, month1, day1)   // 1/1/2020 - 1/2/2020
                var secondd = LocalDate.of(year2, month2, day2)  // 6/1/2020

                var monthCount = ChronoUnit.MONTHS.between(firstt, secondd)
                var dayCount = ChronoUnit.DAYS.between(firstt, secondd)

                if(dayCount%30>20){
                    monthCount++
                }
                //Create payment

                //Long-Term Payment + Short-Term Payment
                ref3.addValueEventListener(object : ValueEventListener {

                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (store2 == 1) {
                            if (p0.exists()) {
                                if (p0.child("rentalType").getValue().toString() == "Long-Term") {
                                    var day = day1
                                    var dayy = day1
                                    var month = month1
                                    var year = year1          //12-2020
                                    var monthh = month + 1
                                    var yearr = year          //1-2021
                                    if (monthh == 13) {
                                        monthh = 1
                                        yearr++
                                    }

                                    var i = 0
                                    var count = 0
                                    while(i < monthCount){

                                        Log.d("abc", "count = " + count)
                                        if(i == (monthCount-1).toInt()){
                                            if(monthh != month2){
                                                i--
                                                Log.d("abc", "last1 = " + i)

                                            }

                                            //Log.d("abc", "last2 = " + monthCount)
                                        }
                                        if((day1 == 30 && (month1 == 4 || month1 == 6 || month1 == 9 || month1 == 11))|| (day1==31 && (month1 == 1 || month1 == 3 || month1 == 5 || month1 == 7|| month1 == 8|| month1 == 10|| month1 == 12))){
                                            if(month == 4 || month == 6 || month == 9 || month == 11){
                                                if(day == 31){
                                                    day = 30
                                                }
                                            }
                                            if(monthh == 4 || monthh == 6 || monthh == 9 || monthh == 11){
                                                if(dayy == 31){
                                                    dayy = 30
                                                }
                                            }
                                            if(month == 1 || month == 3 || month == 5 || month == 7|| month == 8|| month == 10|| month == 12){
                                                if(day == 30  && (month1 == 4 || month1 == 6 || month1 == 9 || month1 == 11)){
                                                    day = 30
                                                }
                                                else{
                                                    day = 31
                                                }
                                            }
                                            if(monthh == 1 || monthh == 3 || monthh == 5 || monthh == 7|| monthh == 8|| monthh == 10|| monthh == 12){
                                                if(dayy == 30  && (month1 == 4 || month1 == 6 || month1 == 9 || month1 == 11)){
                                                    dayy = 30
                                                }
                                                else{
                                                    dayy = 31
                                                }
                                            }
                                            if(month == 2){
                                                if(day == 31 || day == 30){
                                                    if(year%4 == 0){
                                                        day = 29
                                                    }else{
                                                        day = 28
                                                    }
                                                }
                                            }
                                            if(monthh == 2){
                                                if(dayy == 31 || dayy == 30){
                                                    if(yearr%4 == 0){
                                                        dayy = 29
                                                    }else{
                                                        dayy = 28
                                                    }
                                                }
                                            }
                                            if(month ==3){
                                                if(day == 28 || day == 29){
                                                    day = 31
                                                }
                                            }
                                            if(monthh ==3){
                                                if(dayy == 28 || dayy == 29){
                                                    dayy = 31
                                                }
                                            }
                                        }

                                        var first = day.toString() + "/" + month.toString() + "/" + year.toString()
                                        var second = dayy.toString() + "/" + monthh.toString() + "/" + yearr.toString()
                                        var paymentTitle = first + " to " + second + " Rental"

                                        ref5 =
                                            FirebaseDatabase.getInstance().getReference("Payment")

                                        val paymentID = ref5.push().key.toString()

                                        val storePayment = Payment(
                                            paymentID,
                                            paymentTitle,
                                            getTime(),
                                            p0.child("price").getValue().toString().toDouble(),
                                            0,
                                            "new",
                                            "",
                                            "",
                                            rentID,
                                            "installment"
                                        )


                                        ref5.child(paymentID).setValue(storePayment)

                                        month++
                                        monthh++
                                        if (month == 13) {
                                            month = 1
                                            year++
                                        }
                                        if (monthh == 13) {
                                            monthh = 1
                                            yearr++
                                        }

                                        i++
                                    }

                                    /*for (x in 0 until monthCount) { //monthCount = 0
                                        Log.d("abc", "abbb = " + x)
                                        if(x == monthCount-1){
                                            monthCount++
                                            continue
                                            Log.d("abc", "last1 = " + x)
                                            //Log.d("abc", "last2 = " + monthCount)
                                        }
                                        if((day1 == 30 && (month1 == 4 || month1 == 6 || month1 == 9 || month1 == 11))|| (day1==31 && (month1 == 1 || month1 == 3 || month1 == 5 || month1 == 7|| month1 == 8|| month1 == 10|| month1 == 12))){
                                            if(month == 4 || month == 6 || month == 9 || month == 11){
                                                if(day == 31){
                                                    day = 30
                                                }
                                            }
                                            if(monthh == 4 || monthh == 6 || monthh == 9 || monthh == 11){
                                                if(dayy == 31){
                                                    dayy = 30
                                                }
                                            }
                                            if(month == 1 || month == 3 || month == 5 || month == 7|| month == 8|| month == 10|| month == 12){
                                                if(day == 30  && (month1 == 4 || month1 == 6 || month1 == 9 || month1 == 11)){
                                                    day = 30
                                                }
                                                else{
                                                    day = 31
                                                }
                                            }
                                            if(monthh == 1 || monthh == 3 || monthh == 5 || monthh == 7|| monthh == 8|| monthh == 10|| monthh == 12){
                                                if(dayy == 30  && (month1 == 4 || month1 == 6 || month1 == 9 || month1 == 11)){
                                                    dayy = 30
                                                }
                                                else{
                                                    dayy = 31
                                                }
                                            }
                                            if(month == 2){
                                                if(day == 31 || day == 30){
                                                    if(year%4 == 0){
                                                        day = 29
                                                    }else{
                                                        day = 28
                                                    }
                                                }
                                            }
                                            if(monthh == 2){
                                                if(dayy == 31 || dayy == 30){
                                                    if(yearr%4 == 0){
                                                        dayy = 29
                                                    }else{
                                                        dayy = 28
                                                    }
                                                }
                                            }
                                            if(month ==3){
                                                if(day == 28 || day == 29){
                                                    day = 31
                                                }
                                            }
                                            if(monthh ==3){
                                                if(dayy == 28 || dayy == 29){
                                                    dayy = 31
                                                }
                                            }
                                        }

                                        var first = day.toString() + "/" + month.toString() + "/" + year.toString()
                                        var second = dayy.toString() + "/" + monthh.toString() + "/" + yearr.toString()
                                        var paymentTitle = first + " to " + second + " Rental"

                                        ref5 =
                                            FirebaseDatabase.getInstance().getReference("Payment")

                                        val paymentID = ref5.push().key.toString()

                                        val storePayment = Payment(
                                            paymentID,
                                            paymentTitle,
                                            getTime(),
                                            p0.child("price").getValue().toString().toDouble(),
                                            0,
                                            "new",
                                            "",
                                            "",
                                            rentID
                                        )


                                        ref5.child(paymentID).setValue(storePayment)

                                        month++
                                        monthh++
                                        if (month == 13) {
                                            month = 1
                                            year++
                                        }
                                        if (monthh == 13) {
                                            monthh = 1
                                            yearr++
                                        }
                                    }*/

                                } else if (p0.child("rentalType").getValue().toString() == "Short-Term") {
                                    var paymentTitle = firstShort + " to " + secondShort + " Rental"
                                    val dailyPrice =
                                        p0.child("price").getValue().toString().toDouble()
                                    val totalPrice = dailyPrice * (dayCount + 1)

                                    ref5 = FirebaseDatabase.getInstance().getReference("Payment")

                                    val paymentID = ref5.push().key.toString()

                                    val storePayment = Payment(
                                        paymentID,
                                        paymentTitle,
                                        "",
                                        totalPrice,
                                        0,
                                        "new",
                                        "",
                                        "",
                                        rentID,
                                        "fullpayment"
                                    )
                                    if (store2 == 1) {
                                        ref5.child(paymentID).setValue(storePayment)
                                        store2++
                                    }

                                }


                            }
                        }
                        store2++
                    }
                })


                //Short-Term Payment


                //Approve the requester rental request
                ref6 = FirebaseDatabase.getInstance().getReference("Approval")

                var storeOne = 1
                ref6.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (storeOne == 1) {
                            if (p0.exists()) {
                                for (h in p0.children) {
                                    var storeTwo = 1
                                    val targetApproval = h.getValue(Approval::class.java)
                                    var propertyID = targetApproval!!.propertyID
                                    var approvalID = targetApproval!!.approvalID
                                    if (propertyID == approvalList[position].propertyID && approvalID != approvalList[position].approvalID && targetApproval.status=="pending") {

                                        ref7 =FirebaseDatabase.getInstance().getReference("Approval").child(approvalID)
                                        ref7.child("status").setValue("rejected")

                                        //send reject notification
                                        ref3.addValueEventListener(object : ValueEventListener {
                                            override fun onCancelled(p0: DatabaseError) {
                                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                            }

                                            override fun onDataChange(p0: DataSnapshot) {

                                                if (p0.exists()) {
                                                    val targetProperty = p0.getValue(Property::class.java)
                                                    val propertyName = targetProperty!!.propertyName

                                                    var notificationID = ref2.push().key.toString()
                                                    //IMPORTANT - change the user ID to username
                                                    val notificationContent = holder.hiddenValue.text.toString() + " had rejected your request to rent " + propertyName

                                                    val storeNotification = Notification(
                                                        notificationID,
                                                        currentUserID,
                                                        "delivered",
                                                        notificationContent,
                                                        getTime(),
                                                        "approvalConfirmation",
                                                        targetApproval.userID
                                                    )

                                                    if (storeTwo == 1 && targetApproval.userID!= approvalList[position].userID && targetApproval.status=="pending") {
                                                        ref2.child(notificationID).setValue(storeNotification)
                                                        storeTwo++
                                                    }


                                                }
                                            }
                                        })

                                    }

                                }
                            }
                            storeOne++
                        }
                    }

                })

                //Change property status
                ref3.child("status").setValue("renting")


                ref1.child("status").setValue("approved")


                //close everything
                showDialog1(epicDialog)

                /*val notificationFragment = NotificationFragment()

                val manager: FragmentManager = (holder.wholeLayout.context as AppCompatActivity).supportFragmentManager

                manager.beginTransaction()
                    .replace(R.id.fl_wrapper, notificationFragment).addToBackStack(null)
                    .commit()

                val pref= PreferenceManager.getDefaultSharedPreferences(holder.wholeLayout.context)
                val editor=pref.edit()
                editor
                    .putString("changeTab","approval")
                    .apply()
                */
                //val sharedPref = getSharedPreferences("wtfisThis", Context.MODE_PRIVATE)


                //makeCurrentFragment(notificationFragment, holder.wholeLayout.context)


            }

            cancelButton.setOnClickListener {
                epicDialog.dismiss()
            }
            epicDialog.setCancelable(true)
            epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            epicDialog.show()

        }

        holder.rejectBtn.setOnClickListener {
            //var username123 = holder.username.text.toString()
            epicDialog.setContentView(R.layout.popup_negative)
            //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
            val yesButton : Button = epicDialog.findViewById(R.id.yesBtn)
            val cancelButton : Button = epicDialog.findViewById(R.id.cancelBtn)
            val title : TextView = epicDialog.findViewById(R.id.title)
            val content : TextView = epicDialog.findViewById(R.id.content)

            title.text = "Approval Confirmation"
            content.text = "Are you sure to reject this request?"


            yesButton.setOnClickListener {
                ref1 = FirebaseDatabase.getInstance().getReference("Approval").child(approvalList[position].approvalID)
                ref2 = FirebaseDatabase.getInstance().getReference("Notification")

                ref3 = FirebaseDatabase.getInstance().getReference("Property").child(approvalList[position].propertyID)



                //Send notification to the requester
                ref3.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        if (p0.exists()) {
                            val targetProperty = p0.getValue(Property::class.java)
                            val propertyName = targetProperty!!.propertyName

                            var notificationID = ref2.push().key.toString()
                            //IMPORTANT - change the user ID to username
                            val notificationContent =
                                holder.hiddenValue.text.toString() + " had rejected your request to rent " + propertyName

                            val storeNotification = Notification(
                                notificationID,
                                currentUserID,
                                "delivered",
                                notificationContent,
                                getTime(),
                                "approvalConfirmation",
                                approvalList[position].userID
                            )

                            if(store1==1){
                                ref2.child(notificationID).setValue(storeNotification)
                                store1++
                            }

                        }
                    }
                })


                ref1.child("status").setValue("rejected")


                //close everything
                showDialog2(epicDialog)


            }

            cancelButton.setOnClickListener {
                epicDialog.dismiss()
            }

            epicDialog.setCancelable(true)
            epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            epicDialog.show()

        }




    }

    override fun getItemCount(): Int {
        return approvalList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val wholeLayout : LinearLayout = itemView.findViewById(R.id.notificationCard)
        val profilePhoto : CircleImageView = itemView.findViewById(R.id.profilePhoto)
        val username : TextView = itemView.findViewById(R.id.tv_title)
        val hiddenValue : TextView = itemView.findViewById(R.id.hiddenValue)
        val date : TextView = itemView.findViewById(R.id.tv_date)
        val content : TextView = itemView.findViewById(R.id.tv_content)
        val approveBtn : Button = itemView.findViewById(R.id.approveButton)
        val rejectBtn : Button = itemView.findViewById(R.id.rejectButton)
        val statusBtn : Button = itemView.findViewById(R.id.statusButton)

    }

    private fun getTime(): String {

        val today = LocalDateTime.now(ZoneId.systemDefault())

        return today.format(DateTimeFormatter.ofPattern("d MMM uuuu HH:mm:ss "))
    }

    private fun showDialog1(abc: Dialog){
        epicDialog.setContentView(R.layout.popup_positive)
        //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
        val okButton : Button = epicDialog.findViewById(R.id.okBtn)
        val title : TextView = epicDialog.findViewById(R.id.title)
        val content : TextView = epicDialog.findViewById(R.id.content)

        title.text = "Approve Successful"
        content.text = "A notification will be sent to the requester to inform them"

        okButton.setOnClickListener {
            epicDialog.dismiss()
            abc.dismiss()
        }
        epicDialog.setCancelable(true)
        epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        epicDialog.show()
    }

    private fun showDialog2(abc: Dialog){
        epicDialog.setContentView(R.layout.popup_positive)
        //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
        val okButton : Button = epicDialog.findViewById(R.id.okBtn)
        val title : TextView = epicDialog.findViewById(R.id.title)
        val content : TextView = epicDialog.findViewById(R.id.content)

        title.text = "Reject Successful"
        content.text = "A notification will be sent to the requester to inform them"

        okButton.setOnClickListener {
            epicDialog.dismiss()
            abc.dismiss()
        }
        epicDialog.setCancelable(true)
        epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        epicDialog.show()
    }

    private fun showDialog3(){
        epicDialog.setContentView(R.layout.popup_error)
        //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
        val okButton : Button = epicDialog.findViewById(R.id.okBtn)
        val title : TextView = epicDialog.findViewById(R.id.title)
        val content : TextView = epicDialog.findViewById(R.id.content)

        title.text = "Property Non-exist"
        content.text = "This property is being removed !"

        okButton.setOnClickListener {
            epicDialog.dismiss()
        }
        epicDialog.setCancelable(true)
        epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        epicDialog.show()
    }



}