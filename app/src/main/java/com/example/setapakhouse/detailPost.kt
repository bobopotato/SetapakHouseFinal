package com.example.setapakhouse

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.example.setapakhouse.Adapter.HomeAdapter
import com.example.setapakhouse.Adapter.accomAdapter
import com.example.setapakhouse.Adapter.fragmentAdapter
import com.example.setapakhouse.Adapter.reviewAdapter
import com.example.setapakhouse.Fragment.AccomFragment
import com.example.setapakhouse.Model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.type.TimeOfDay
import com.ms.square.android.expandabletextview.ExpandableTextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail_post.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.fragment_accom.*
import kotlinx.android.synthetic.main.fragment_home.*
import ru.slybeaver.slycalendarview.SlyCalendarDialog
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.ArrayList

class detailPost : AppCompatActivity() {
    lateinit var ref: DatabaseReference
    lateinit var ref1: DatabaseReference
    lateinit var ref2: DatabaseReference
    lateinit var ref3: DatabaseReference
    lateinit var ref4: DatabaseReference
    lateinit var query: Query
    lateinit var reviewList : MutableList<Review>
    lateinit var epicDialog : Dialog
    lateinit var epicDialog2 : Dialog
    val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_post)
        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setTitle("Property Details")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //val slideModels:List<SlideModel>

        reviewList=mutableListOf()

        epicDialog = Dialog(this)
        epicDialog2 = Dialog(this)


        backButton.bringToFront()

        backButton.setOnClickListener{
            finish()
        }
        val slideModels=ArrayList<SlideModel>()

        val selectedPropertyID=intent.getStringExtra("selectedPosition")
        val selectedUserID=intent.getStringExtra("selectedUserID")

        requestBtn.setOnClickListener {
            //val intent = Intent(this, RequestRentActivity::class.java)
            //intent.putExtra("SelectedPropertyID", selectedPropertyID)
            //intent.putExtra("SelectedUserID", selectedUserID)
            //startActivity(intent)
            val callback: SlyCalendarDialog.Callback = object : SlyCalendarDialog.Callback {
                override fun onCancelled() {


                }
                override fun onDataSelected(firstDate: Calendar?, secondDate: Calendar?,hours: Int,minutes: Int) {

                    var completeDate = false
                    var validDate = false
                    var validDuration = false
                    //val abc =  today.format(DateTimeFormatter.ofPattern("d MMM uuuu HH:mm:ss "))

                    if(firstDate!= null && secondDate!=null) {
                        val day1 = SimpleDateFormat(("d"), Locale.getDefault()).format(firstDate!!.time)
                        val month1 = SimpleDateFormat(("MM"), Locale.getDefault()).format(firstDate!!.time)
                        val year1 = SimpleDateFormat(("yyyy"), Locale.getDefault()).format(firstDate!!.time)
                        val first = day1 + "/" + month1 + "/" + year1

                        val day2 = SimpleDateFormat(("d"), Locale.getDefault()).format(secondDate!!.time)
                        val month2 = SimpleDateFormat(("MM"), Locale.getDefault()).format(secondDate!!.time)
                        val year2 = SimpleDateFormat(("yyyy"),Locale.getDefault()).format(secondDate!!.time)
                        val second = day2 + "/" + month2 + "/" + year2
                        completeDate = true

                        val cal: Calendar = Calendar.getInstance()
                        val thisDay = SimpleDateFormat(("d"), Locale.getDefault()).format(cal.time)
                        val thisMonth = SimpleDateFormat(("MM"), Locale.getDefault()).format(cal.time)
                        val thisYear = SimpleDateFormat(("yyyy"), Locale.getDefault()).format(cal.time)
                        val today = LocalDate.of(thisYear.toInt(), thisMonth.toInt(), thisDay.toInt())
                        val firstt = LocalDate.of(year1.toInt(), month1.toInt(), day1.toInt())
                        val secondd = LocalDate.of(year2.toInt(), month2.toInt(), day2.toInt())

                        if (firstt.isBefore(today) || firstt.isEqual(today)) {
                            Toast.makeText(this@detailPost,"Please choose start date after today",Toast.LENGTH_SHORT).show()
                            calendarZ(this)
                        } else {
                            validDate = true
                        }

                        if (txtRentalType.text == "Rental Type: Long-Term" && completeDate) {
                            if (day1.toInt() == day2.toInt()) {
                                validDuration = true
                            }
                            else {
                                if(day1.toInt() == 31 || day1.toInt() == 30){
                                    if(month2.toInt() == 2 && (day2.toInt()==28 || day2.toInt() == 29)){
                                        validDuration = true
                                    }
                                    if(day2.toInt() == 31 || day2.toInt() == 30){
                                        validDuration = true
                                    }

                                }

                                if(!validDuration){
                                    Toast.makeText(this@detailPost,"Please choose the same date of month afterward",Toast.LENGTH_SHORT).show()
                                    calendarZ(this)
                                }


                            }

                        } else if (txtRentalType.text == "Rental Type: Short-Term" && completeDate) {
                            validDuration = true
                        }

                        if (completeDate && validDate && validDuration) {
                            showDialog(selectedPropertyID!!, selectedUserID!!, first, second)
                            //val monthCount = ChronoUnit.DAYS.between(firstt, secondd)
                            //Toast.makeText(this@detailPost, "abc = " + monthCount.toString() , Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this@detailPost, "Please choose both start date and end date" , Toast.LENGTH_SHORT).show()
                        calendarZ(this)
                    }
                    //Toast.makeText(this@detailPost, "abc = " + today.dayOfMonth.toString() , Toast.LENGTH_SHORT).show()

                    //Toast.makeText(this@detailPost, "abc = " + first + "|| zzz = " + second , Toast.LENGTH_SHORT).show()
                }
            }

            calendarZ(callback)

        }

        if(selectedUserID.equals(currentUserID)){
            chatBtn.isEnabled=false
            chatBtn.isClickable=false
            chatBtn.setBackgroundResource(R.drawable.round_button_disable)
            //chatBtn.visibility = View.INVISIBLE
            requestBtn.isEnabled=false
            requestBtn.isClickable=false
            requestBtn.setBackgroundResource(R.drawable.round_button_disable)
            //requestBtn.visibility = View.INVISIBLE
        }

        chatBtn.setOnClickListener {

            val intent = Intent(this, MessageActivity::class.java)
            intent.putExtra("selectedUserID",selectedUserID)

            this.startActivity(intent)
        }
        val pref= PreferenceManager.getDefaultSharedPreferences(this)
        val editor=pref.edit()
        editor
            .putString("PROPERTYID",selectedPropertyID)
            .apply()

        val imageSlider = findViewById<ImageSlider>(R.id.imgProperty)

        ref=FirebaseDatabase.getInstance().getReference("Property")
        ref.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(h in snapshot.children){
                        if(h.child("propertyID").getValue().toString().equals(selectedPropertyID)){
                            if(h.child("status").getValue().toString().equals("available")){
                                if(selectedUserID.equals(currentUserID)) {
                                    //requestBtn.visibility = View.INVISIBLE
                                    requestBtn.isEnabled=false
                                    requestBtn.isClickable=false
                                    requestBtn.setBackgroundResource(R.drawable.round_button_disable)
                                }else{
                                    requestBtn.isEnabled=true
                                    requestBtn.isClickable=true
                                    requestBtn.setBackgroundResource(R.drawable.round_button_black)
                                    //requestBtn.visibility = View.VISIBLE
                                }
                            }else{
                                requestBtn.isEnabled=false
                                requestBtn.isClickable=false
                                requestBtn.setBackgroundResource(R.drawable.round_button_disable)
                                //requestBtn.visibility = View.INVISIBLE
                            }
                            if(h.child("rentalType").getValue().toString().equals("long")){
                                txtPrice.text="RM"+String.format("%.2f",h.child("price").getValue().toString().toDouble())+"/MONTH"
                            }else{
                                txtPrice.text="RM"+String.format("%.2f",h.child("price").getValue().toString().toDouble())+"/DAY"
                            }

                            txtPropertyType.text="Property Type: "+h.child("propertyType").getValue().toString()
                            txtRentalType.text="Rental Type: "+h.child("rentalType").getValue().toString()
                            txtPropertyName.text="Property Name: "+h.child("propertyName").getValue().toString()
                            txtLocation.text=h.child("location").getValue().toString()

                            if(h.child("propertyType").getValue().toString().equals("Room")){
                                ref1=FirebaseDatabase.getInstance().getReference("Room")
                                ref1.addValueEventListener(object:ValueEventListener{
                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if(snapshot.exists()){
                                            for(h in snapshot.children){
                                                if(h.child("propertyID").getValue().toString().equals(selectedPropertyID)){
                                                    txtDetail.text="Room Type : "+h.child("roomType").getValue().toString()+"\nMaximum Occupancy : "+h.child("maxOccupancy").getValue().toString()
                                                }
                                            }
                                        }
                                    }

                                })
                            }else{
                                ref1=FirebaseDatabase.getInstance().getReference("House")
                                ref1.addValueEventListener(object:ValueEventListener{
                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if(snapshot.exists()){
                                            for(h in snapshot.children){
                                                if(h.child("propertyID").getValue().toString().equals(selectedPropertyID)){
                                                    txtDetail.text="House Type : "+h.child("houseType").getValue().toString()+"\nNumber of Rooms : "+h.child("numRoom").getValue().toString()+"\nNumber of Bathrooms : "+h.child("numBathroom").getValue().toString()
                                                }
                                            }
                                        }
                                    }

                                })
                            }

                            val txtView:ExpandableTextView=findViewById(R.id.expand_tex_view)
                            txtView.text=txtView.text.toString()+(h.child("description").getValue().toString())

                            val propertyUID=h.child("userID").getValue().toString()
                            ref2=FirebaseDatabase.getInstance().getReference("Users")
                            ref2.addValueEventListener(object:ValueEventListener{
                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if(snapshot.exists()){
                                        for(h in snapshot.children){
                                            if(h.child("userID").getValue().toString().equals(propertyUID)){
                                                contactPhone.text=h.child("phoneNumber").getValue().toString()
                                                contactUsername.text=h.child("username").getValue().toString()
                                                Picasso.get().load(h.child("image").getValue().toString()).into(contactProfile)
                                            }
                                        }
                                    }
                                }

                            })
                        }
                    }
                }
            }

        })


        ref=FirebaseDatabase.getInstance().getReference("Review")
        ref.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var total:Double=0.0
                    reviewList.clear()
                    for(h in snapshot.children){
                        if(h.child("propertyID").getValue().toString().equals(selectedPropertyID)&&h.child("status").getValue().toString().equals("completed")){
                            val review = h.getValue(Review::class.java)
                            reviewList.add(review!!)

                            total+=h.child("numStar").getValue().toString().toDouble()
                        }
                    }
                    if(total.equals(0.0)){
                        averageRating.setRating(0.0f)
                        averageRating1.setRating(0.0f)
                        txtAverage.text="0.0"
                        txtAverage1.text="0.0"
                    }else{
                        val average:Double=String.format("%.1f", total/reviewList.size).toDouble()
                        averageRating.setRating(average.toFloat())
                        averageRating1.setRating(average.toFloat())
                        txtAverage.text=average.toString()
                        txtAverage1.text=average.toString()
                    }

                    val mLayoutManager = LinearLayoutManager(this@detailPost)
                    txtTotalRating.text=reviewList.size.toString()+" Ratings |"
                    reviewRecycle.layoutManager = mLayoutManager
                    reviewRecycle.adapter = reviewAdapter(reviewList)
                }
            }

        })


        query= FirebaseDatabase.getInstance().getReference("PropertyImage").orderByChild("imageName")
        query.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    for (h in snapshot.children) {
                        if(h.child("propertyID").getValue().toString().equals(selectedPropertyID)){
                            slideModels.add(SlideModel(h.child("imageSource").getValue().toString()))
                        }
                        imageSlider.setImageList(slideModels,ScaleTypes.CENTER_CROP)

                    }
                }
            }

        })


        detailViewPager.adapter= fragmentAdapter(supportFragmentManager)
        detailsTabLayout.setupWithViewPager(detailViewPager)

    }

    private fun calendarZ(callback : SlyCalendarDialog.Callback){
        val cal: Calendar = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 1)
        //chooseCalendar.setMinDate(cal.getTimeInMillis())
        //val today = LocalDateTime.now(ZoneId.systemDefault())
        //val bb = calendar.setTime(getSimpleDateFormat().parse(stringDate))
        //.setStartDate(cal.time)
        SlyCalendarDialog().setSingle(false)
            .setCallback(callback)
            .show(supportFragmentManager, "TAG_SLYCALENDAR")
    }

    private fun showDialog(selectedPropertyID : String, selectedUserID : String, firstDate : String, secondDate : String){
        epicDialog.setContentView(R.layout.popup_confirmation)
        //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
        val yesButton : Button = epicDialog.findViewById(R.id.yesBtn)
        val cancelButton : Button = epicDialog.findViewById(R.id.cancelBtn)
        val title : TextView = epicDialog.findViewById(R.id.title)
        val content : TextView = epicDialog.findViewById(R.id.content)

        title.text = "Request Confirmation"
        content.text = "Are you sure to make this request?"

        yesButton.setOnClickListener {
            //val intent = Intent(this@detailPost, MainActivity::class.java)
            //startActivity(intent)
            ref1 = FirebaseDatabase.getInstance().getReference("Approval")
            ref2 = FirebaseDatabase.getInstance().getReference("Property").child(selectedPropertyID)
            ref3 = FirebaseDatabase.getInstance().getReference("Notification")
            ref4 = FirebaseDatabase.getInstance().getReference("Users").child(currentUserID)

            val approvalID = ref1.push().key.toString()
            val notificationID = ref3.push().key.toString()

            var store1 = 1
            var store2 = 1

            ref2.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (store2 == 1){
                        if (snapshot.exists()) {
                            val propertyName = snapshot.child("propertyName").getValue().toString()

                            ref4.addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    if (store1 == 1) {
                                        if (p0.exists()) {
                                            val targetUser = p0.getValue(User::class.java)
                                            val approvalContent =
                                                targetUser!!.username + " had requested to rent your " + propertyName + " property on " + firstDate.toString() + " to " + secondDate.toString()
                                            val notificationContent =
                                                targetUser!!.username + " had requested to rent one of your property"

                                            //Toast.makeText(this@RequestRentActivity, "abc = " + currentUserID + "wtf = " +  targetUser!!.username, Toast.LENGTH_SHORT).show()
                                            val storeApproval = Approval(
                                                approvalID,
                                                approvalContent,
                                                getTime(),
                                                firstDate,
                                                secondDate,
                                                "pending",
                                                currentUserID,
                                                selectedPropertyID,
                                                notificationID,
                                                selectedUserID
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
                                    store1++
                                }
                            })

                            epicDialog2.setContentView(R.layout.popup_positive)
                            //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
                            val okButton1: Button = epicDialog2.findViewById(R.id.okBtn)
                            val title1: TextView = epicDialog2.findViewById(R.id.title)
                            val content1: TextView = epicDialog2.findViewById(R.id.content)

                            title1.text = "Request Successful"
                            content1.text = "You will be redirected to the main page"

                            okButton1.setOnClickListener {
                                epicDialog.dismiss()
                                epicDialog2.dismiss()
                                //val intent = Intent(this@detailPost, MainActivity::class.java)
                                //startActivity(intent)
                            }
                            epicDialog2.setCancelable(true)
                            epicDialog2.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            epicDialog2.show()


                        }

                    }
                    store2++
                }
            })
        }
        cancelButton.setOnClickListener {
            epicDialog.dismiss()
        }

        epicDialog.setCancelable(true)
        epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        epicDialog.show()
    }

    private fun getTime(): String {

        val today = LocalDateTime.now(ZoneId.systemDefault())

        return today.format(DateTimeFormatter.ofPattern("d MMM uuuu HH:mm:ss "))
    }

}