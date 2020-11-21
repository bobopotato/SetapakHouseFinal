package com.example.setapakhouse


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Color.rgb
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.setapakhouse.Fragment.*
import com.example.setapakhouse.Model.Notification
import com.example.setapakhouse.Model.Payment
import com.example.setapakhouse.Model.Rent
import com.example.setapakhouse.Model.Review
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_house_renting.*
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    lateinit var mToggle: ActionBarDrawerToggle
    val homeFragment = HomeFragment()
    val postFragment = PostFragment()
    val searchFragment = SearchFragment()
    val notificationFragment = NotificationFragment()
    val profileFragment = ProfileFragment()
    lateinit var handler:Handler
    lateinit var runnable:Runnable
    var count=0
    lateinit var notificationManager : NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder : android.app.Notification.Builder
    private val channelID = "com.example.setapakhouse"
    private val description = "Test notification"
    var abc123 = 0

    private fun content(){
        var settings:SharedPreferences= PreferenceManager.getDefaultSharedPreferences(this)
        var lastTimeStarted1 = settings.getInt("date",0)
        var calendar1: Calendar = Calendar.getInstance()
        var today=calendar1.get(Calendar.DAY_OF_YEAR);
        //Toast.makeText(this, count.toString(), Toast.LENGTH_SHORT).show()
        //Log.d("TODAYCHANGED",today.toString())
        //Log.d("TODAYNO",lastTimeStarted1.toString())
        if(today!=lastTimeStarted1){
            var editor:SharedPreferences.Editor=settings.edit()
            editor.putInt("date",today)
            editor.commit()
            //Log.d("TODAYACTION","DO automake")

            var step = 1
            if(step==1) {
                //Log.d("PLS","STARTRENT1")
                autoChangeRentStatus()
                step++

                if(step==2) {
                    //Log.d("PLS","STARTPAYMENT1")
                    autoChangePaymentStatus()
                    step++

                    if(step==3){
                        //Log.d("PLS","STARTFINRENT1")
                        autoChangeFinRentStatus()
                        step++
                        if(step==4){
                            autoChangeApproval()
                        }
                    }
                }
            }
        }
        refresh(1000)
    }
    private fun refresh(mill:Long){
        handler=Handler()
        runnable=Runnable{
            content()
        }
        handler.postDelayed(runnable,mill)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }


    //lateinit var receiver:DateChangedReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var settings:SharedPreferences= PreferenceManager.getDefaultSharedPreferences(this)
        var lastTimeStarted = settings.getInt("date",0)
        var calendar: Calendar = Calendar.getInstance()
        var today=calendar.get(Calendar.DAY_OF_YEAR);
        if(today!=lastTimeStarted) {

            //Toast.makeText(this, today.toString(), Toast.LENGTH_SHORT).show()
            var editor:SharedPreferences.Editor=settings.edit()
            editor.putInt("date",today)
            editor.commit()
        }

        redDot.bringToFront()

        var lastTimeStarted1 = settings.getInt("date",0)
        //Log.d("TODAY",lastTimeStarted1.toString())
        //Log.d("TODAYFIRST","FIRSTACTION".toString())
        //do automake over here and inside content

        var step = 1
        if(step==1) {
            //Log.d("PLS","STARTRENT")
            autoChangeRentStatus()
            step++

            if(step==2) {
                //Log.d("PLS","STARTPAYMENT")
                autoChangePaymentStatus()
                step++

                if(step==3){
                    //Log.d("PLS","STARTFINRENT")
                    autoChangeFinRentStatus()
                    step++

                    if(step==4){
                        autoChangeApproval()
                    }
                }
            }
        }


        content()
        //check User Login
        val currentUser= FirebaseAuth.getInstance().currentUser

        if(currentUser==null){
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()

        }

        //drawer.closeDrawers()
        //Set Action Bar and Navigation Drawer
        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setIcon(R.drawable.ic_logo)
        mToggle = ActionBarDrawerToggle(this, drawer, R.string.close, R.string.open)
        drawer.addDrawerListener(mToggle)
        mToggle.syncState()


        notificationManager =  getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //Navigation Drawer
        nav_drawer.setNavigationItemSelectedListener(this)

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        //Set bottom navigation
        makeCurrentFragment(homeFragment)
        bottom_nav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.ic_home -> makeCurrentFragment(homeFragment)
                R.id.ic_addpost -> makeCurrentFragment(postFragment)
                R.id.ic_search -> makeCurrentFragment(searchFragment)
                R.id.ic_notification -> makeCurrentFragment(notificationFragment)
                R.id.ic_profile -> makeCurrentFragment(profileFragment)
            }
            true
        }

        //log out
        logout.setOnClickListener {
            //Toast.makeText(this, "Log Out Successfully", Toast.LENGTH_SHORT).show()
            val pref= PreferenceManager.getDefaultSharedPreferences(this)
            val editor=pref.edit()
            editor
                .putBoolean("firstTime",true)
                .apply()
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            // val intent = Intent(this, LoginActivity::class.java)
            //startActivity(intent)
        }

        if(intent.getStringExtra("GoNotification")!=null){
            if (intent.getStringExtra("GoNotification")!!.equals("true")){
                makeCurrentFragment(notificationFragment)
            }
        }

        if(intent.getStringExtra("GoNotification1")!=null){
            if (intent.getStringExtra("GoNotification1")!!.equals("true")){
                makeCurrentFragment(notificationFragment)
            }
        }
        var first = true;
        var store = 0

        val pref= PreferenceManager.getDefaultSharedPreferences(this)

        pref.apply{
            val checkFirst =getBoolean("firstTime",true)
            if(checkFirst){
                first = true
                val editor=pref.edit()
                editor
                    .putBoolean("firstTime",false)
                    .apply()
            }else{
                first = false
            }
        }

        if(currentUser!=null){
            val notificationRef = FirebaseDatabase.getInstance().getReference("Notification").orderByChild("userID").equalTo(currentUser!!.uid)

            notificationRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                        var size = 0;
                        var stop = false;
                        if (p0.exists()) {
                            for (h in p0.children) {
                                val notification1 = h.getValue(Notification::class.java)
                                if(notification1!!.status=="delivered"){
                                    if(!stop) {
                                        size++

                                    }
                                }

                            }
                            if(size>0){
                                if(first){
                                    if(!stop) {
                                        if(intent.getStringExtra("GoNotification")==null && intent.getStringExtra("GoNotification1")==null){
                                            notification(size.toString())
                                            hiddenNotificationSize.text = size.toString()
                                            first = false
                                            stop = true
                                        }
                                    }
                                }
                                else{
                                    if(!stop && hiddenNotificationSize.text.toString().isNotEmpty()) {
                                        if(hiddenNotificationSize.text.toString().toInt()<size){
                                            notification1()
                                            stop = true
                                        }

                                    }
                                    hiddenNotificationSize.text = size.toString()
                                }
                            }else if (size == 0){
                                //Toast.makeText(this@MainActivity, "Boolean = " + getKey.first, Toast.LENGTH_SHORT).show()
                                hiddenNotificationSize.text = size.toString()
                                first = false
                            }else{
                                first =true
                            }
                        }

                }
            })
        }

        val chatRef = FirebaseDatabase.getInstance().getReference("Chats").orderByChild("receiver").equalTo(currentUser!!.uid)

        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                var gotMessage = false
                if (p0.exists()) {
                    for (h in p0.children) {
                        if (h.child("isseen").getValue().toString().equals("false")) {
                            gotMessage = true
                        }
                    }

                    if (gotMessage) {
                        redDot.visibility = View.VISIBLE
                    }
                    else{
                        redDot.visibility = View.GONE
                    }
                }
            }
        })

        if(intent.getStringExtra("goProfile")!=null){
            makeCurrentFragment(notificationFragment)
            bottom_nav.selectedItemId = R.id.ic_profile
        }

    }



    //    override fun onStop() {
//        super.onStop()
//        unregisterReceiver(receiver)
//    }
    //Add toolbar icon(search & chat)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.topbar, menu)
        return true
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.ic_search) {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
            //Toast.makeText(this, "user = " + currentUser.uid, Toast.LENGTH_SHORT).show()
        }
        if (menuItem.itemId == R.id.ic_chat) {
            val intent = Intent(this, ChatroomActivity::class.java)
            startActivity(intent)
        }

        if(mToggle.onOptionsItemSelected(menuItem)){
            return true
        }

        return super.onOptionsItemSelected(menuItem)
    }


    override fun onNavigationItemSelected(@NonNull menuItem: MenuItem): Boolean {

        if(menuItem.itemId == R.id.aboutUs){
            val intent = Intent(this, AboutUsActivity::class.java)
            startActivity(intent)
        }
        if(menuItem.itemId == R.id.help){
            val intent = Intent(this, ContactUsActivity::class.java)
            startActivity(intent)
        }
        if(menuItem.itemId == R.id.termCondition){
            val intent = Intent(this, TermAndConditionActivity::class.java)
            startActivity(intent)
        }

        return false
    }


    private fun makeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply{
            replace(R.id.fl_wrapper, fragment)
            commit()
        }

    override fun onStart() {
        super.onStart()
        abc123++
        Log.d("zzzzzzz123", abc123.toString())
        val currentUser= FirebaseAuth.getInstance().currentUser
        if(currentUser==null || currentUser!!.email!!.isEmpty()){
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()

        }
    }


    override fun onResume() {
        super.onResume()
        //content()
        //autoChangePaymentStatus()

        //down is only run 1 time a day


    }

    private fun getTime(): String {

        val today = LocalDateTime.now(ZoneId.systemDefault())

        return today.format(DateTimeFormatter.ofPattern("d MMM uuuu HH:mm:ss "))
    }

    private fun autoChangeApproval(){
        var countApproval=0
        var ref13:DatabaseReference
        ref13=FirebaseDatabase.getInstance().getReference("Approval")
        ref13.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    if(countApproval==0){
                        for(ap in snapshot.children){
                            if(ap.child("status").getValue().toString().equals("pending")){
                                var ApprovalFirstDate=ap.child("requestStartDate").getValue().toString().split("/")
                                var approvalDay=ApprovalFirstDate[0].toInt()
                                var approvalMonth=ApprovalFirstDate[1].toInt()
                                var approvalYear=ApprovalFirstDate[2].toInt()

                                //today
                                val cal: Calendar = Calendar.getInstance()
                                val thisDay =SimpleDateFormat(("d"), Locale.getDefault()).format(cal.time)
                                val thisMonth = SimpleDateFormat(("MM"), Locale.getDefault()).format(cal.time)
                                val thisYear = SimpleDateFormat(("yyyy"), Locale.getDefault()).format(cal.time)
                                //val today = LocalDate.of(thisYear.toInt(), thisMonth.toInt(), thisDay.toInt())
                                //var firstt = LocalDate.of(2020, 11, 6)
                                var firstt = LocalDate.of(approvalYear, approvalMonth, approvalDay)
                                var secondd = LocalDate.of(thisYear.toInt(), thisMonth.toInt(), thisDay.toInt())
                                var dayCount = ChronoUnit.DAYS.between(secondd, firstt)
                                if(dayCount<=0){
                                    ref13.child(ap.child("approvalID").getValue().toString()).child("status").setValue("expired")
                                    var ref14 =FirebaseDatabase.getInstance().getReference("Notification")
                                    var notificationID = ref14.push().key.toString()
                                    //IMPORTANT - change the user ID to username
                                    var approvalContent=ap.child("approvalContent").getValue().toString().split(" ")
                                    var notificationContent="Request Rent For ("
                                    for (x in 6..(approvalContent.size-1)){
                                        notificationContent+=" "+approvalContent[x]
                                    }
                                    notificationContent +=" ) had expired"
                                    //Log.d("MESSAGE",notificationContent)
                                    val storeNotification = Notification(
                                        notificationID,
                                        "system",
                                        "delivered",
                                        notificationContent,
                                        getTime(),
                                        "approvalConfirmation",
                                        ap.child("userID").getValue().toString()
                                    )
                                    ref14.child(notificationID).setValue(storeNotification)
                                }
                            }
                        }
                        countApproval++
                    }
                }
            }

        })
    }

    private fun autoChangeFinRentStatus(){
        var ref10:DatabaseReference
        var countRent2=0
        ref10=FirebaseDatabase.getInstance().getReference("Rent")
        ref10.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){

                    if(countRent2.toString().equals("0")){
                        for(rr in snapshot.children){
                            if(rr.child("status").getValue().toString().equals("continuing")){
                                var rentFirstDate=rr.child("checkOutDate").getValue().toString().split("/")
                                var rentDay=rentFirstDate[0].toInt()
                                var rentMonth=rentFirstDate[1].toInt()
                                var rentYear=rentFirstDate[2].toInt()

                                //today
                                val cal: Calendar = Calendar.getInstance()
                                val thisDay =SimpleDateFormat(("d"), Locale.getDefault()).format(cal.time)
                                val thisMonth = SimpleDateFormat(("MM"), Locale.getDefault()).format(cal.time)
                                val thisYear = SimpleDateFormat(("yyyy"), Locale.getDefault()).format(cal.time)
                                //val today = LocalDate.of(thisYear.toInt(), thisMonth.toInt(), thisDay.toInt())
                                //var firstt = LocalDate.of(2020, 11, 6)
                                var firstt = LocalDate.of(rentYear, rentMonth, rentDay)
                                var secondd = LocalDate.of(thisYear.toInt(), thisMonth.toInt(), thisDay.toInt())

                                var dayCount = ChronoUnit.DAYS.between(secondd, firstt)
                                if(dayCount<=-1){
                                    ref10.child(rr.child("rentID").getValue().toString()).child("status").setValue("completed")

                                    var countProperty1=0
                                    var ref11=FirebaseDatabase.getInstance().getReference("Property")
                                    ref11.addValueEventListener(object:ValueEventListener{
                                        override fun onCancelled(error: DatabaseError) {
                                            TODO("Not yet implemented")
                                        }

                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if(snapshot.exists()){
                                                if(countProperty1.toString().equals("0")){
                                                    for(pr in snapshot.children){
                                                        if(rr.child("propertyID").getValue().toString().equals(pr.child("propertyID").getValue().toString())&&
                                                            pr.child("status").getValue().toString().equals("renting")){
                                                            ref11.child(pr.child("propertyID").getValue().toString()).child("status").setValue("available")
                                                        }
                                                    }
                                                    countProperty1++
                                                }
                                            }
                                        }

                                    })

                                    var ref12 =FirebaseDatabase.getInstance().getReference("Review")
                                    var reviewID =ref12.push().key.toString()
                                    val storeReview = Review(
                                        reviewID,
                                        "",
                                        "",
                                        0.0,
                                        rr.child("propertyID").getValue().toString(),
                                        rr.child("userID").getValue().toString(),
                                        "",
                                        "pending"
                                    )
                                    ref12.child(reviewID).setValue(storeReview)

                                }
                            }
                        }
                        countRent2++
                    }
                }
            }

        })
    }
    private fun autoChangeRentStatus(){

        var ref1:DatabaseReference

        var countRent1=0

        ref1=FirebaseDatabase.getInstance().getReference("Rent")
        ref1.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){

                    if(countRent1.toString().equals("0")){
                        for(rr in snapshot.children){
                            if(rr.child("status").getValue().toString().equals("new")){

                                var rentFirstDate=rr.child("checkInDate").getValue().toString().split("/")
                                var rentDay=rentFirstDate[0].toInt()
                                var rentMonth=rentFirstDate[1].toInt()
                                var rentYear=rentFirstDate[2].toInt()

                                //today
                                val cal: Calendar = Calendar.getInstance()
                                val thisDay =SimpleDateFormat(("d"), Locale.getDefault()).format(cal.time)
                                val thisMonth = SimpleDateFormat(("MM"), Locale.getDefault()).format(cal.time)
                                val thisYear = SimpleDateFormat(("yyyy"), Locale.getDefault()).format(cal.time)
                                //val today = LocalDate.of(thisYear.toInt(), thisMonth.toInt(), thisDay.toInt())
                                //var firstt = LocalDate.of(2020, 11, 6)
                                var firstt = LocalDate.of(rentYear, rentMonth, rentDay)
                                var secondd = LocalDate.of(thisYear.toInt(), thisMonth.toInt(), thisDay.toInt())

                                var dayCount = ChronoUnit.DAYS.between(secondd, firstt)
                                if(dayCount.toString().equals("0")){
                                    ref1.child(rr.child("rentID").getValue().toString()).child("status").setValue("continuing")
                                }
                            }
                        }
                        Log.d("PLS","FINAUTORENT")
                        countRent1++
                    }
                }
            }

        })

    }

    private fun autoChangePaymentStatus() {
        var countNotification=0
        var countRent=0
        var ref1:DatabaseReference
        var ref2:DatabaseReference
        var ref3:DatabaseReference
        var ref4:DatabaseReference
        var rentList:MutableList<Rent>
        var paymentList:MutableList<Payment>
        rentList= mutableListOf()
        paymentList= mutableListOf()
        ref1= FirebaseDatabase.getInstance().getReference("Rent")
        ref1.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {

                    if(countRent.toString().equals("0")){
                        rentList.clear()
                        for (h in snapshot.children) {
                            if (!(h.child("status").getValue().toString().equals("withdraw"))&&
                                !(h.child("status").getValue().toString().equals("completed"))) {  //ori no this completed
                                //Log.d("PLS",h.child("rentID").getValue().toString())
                                val rent = h.getValue(Rent::class.java)
                                rentList.add(rent!!)

                            }
                        }
                        Log.d("PLS","Outside:"+rentList.size.toString())
                        ref2 = FirebaseDatabase.getInstance().getReference("Payment")
                        ref2.addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {

                                    if (countNotification.toString().equals("0")) {
                                        paymentList.clear()
                                        for (p in snapshot.children) {
                                            Log.d("PLS","inside:"+rentList.size.toString())
                                            for (r in rentList) {

                                                if (p.child("rentID").getValue().toString().equals(r.rentID) &&
                                                    !(p.child("status").getValue().toString().equals("paid")) &&
                                                    !(p.child("status").getValue().toString().equals("expired"))) {

                                                    //Log.d("PLS",p.child("paymentID").getValue().toString())
                                                    var splitTitle =
                                                        p.child("paymentTitle").getValue().toString()
                                                            .split("/")
                                                    var paymentDate = splitTitle[0]
                                                    var paymentMonth = splitTitle[1]
                                                    var paymentYearSection = splitTitle[2].split(" ")
                                                    var paymentYear = paymentYearSection[0]
                                                    //today
                                                    val cal: Calendar = Calendar.getInstance()
                                                    val thisDay =
                                                        SimpleDateFormat(("d"), Locale.getDefault()).format(
                                                            cal.time
                                                        )
                                                    val thisMonth = SimpleDateFormat(
                                                        ("MM"),
                                                        Locale.getDefault()
                                                    ).format(cal.time)
                                                    val thisYear = SimpleDateFormat(
                                                        ("yyyy"),
                                                        Locale.getDefault()
                                                    ).format(cal.time)
                                                    //val today = LocalDate.of(thisYear.toInt(), thisMonth.toInt(), thisDay.toInt())
                                                    //var firstt = LocalDate.of(2020, 11, 6)
                                                    var firstt = LocalDate.of(
                                                        paymentYear.toInt(),
                                                        paymentMonth.toInt(),
                                                        paymentDate.toInt()
                                                    )
                                                    var secondd = LocalDate.of(
                                                        thisYear.toInt(),
                                                        thisMonth.toInt(),
                                                        thisDay.toInt()
                                                    )


                                                    //dayCount >= -4&&<=0 then correct elseif <-4 change semua to cancel

                                                    var dayCount = ChronoUnit.DAYS.between(secondd, firstt)
                                                    Log.d("Check",dayCount.toString())

                                                    if(p.child("paymentType").getValue().toString().equals("installment")){
                                                        //Toast.makeText(context,secondd.toString(),Toast.LENGTH_SHORT).show()
                                                        if (dayCount >= -4 && dayCount <= 0) { //5 days limit, 6 to 15 is left 1 day. this is within 10 days (long)
                                                            //change new to pending and add notification
                                                            var payment = p.getValue(Payment::class.java)
                                                            ref2.child(
                                                                p.child("paymentID").getValue().toString()
                                                            ).child("status").setValue("pending")
                                                            //Toast.makeText(context,countNotification.toString().equals("0").toString(),Toast.LENGTH_SHORT).show()



                                                            //if no warning
                                                            if (p.child("warningNotificationID").getValue().toString().equals("")) {
                                                                //change payment status from pending to pending, new to pending
                                                                val leftday = 5 + dayCount
                                                                val notificationContent =
                                                                    "You have " + leftday.toString() + "days left for this payment(" + p.child(
                                                                        "paymentTitle"
                                                                    ).getValue().toString() + ")"

                                                                //add notification
                                                                ref3 = FirebaseDatabase.getInstance()
                                                                    .getReference("Notification")
                                                                var notificationID =
                                                                    ref3.push().key.toString()
                                                                val storeNotification = Notification(
                                                                    notificationID,
                                                                    "system",
                                                                    "delivered",
                                                                    notificationContent,
                                                                    getTime(),
                                                                    "warning",
                                                                    r.userID
                                                                )
                                                                ref3.child(notificationID)
                                                                    .setValue(storeNotification)
                                                                ref2.child(p.child("paymentID").getValue().toString()).child("warningNotificationID").setValue(notificationID)
                                                                //Toast.makeText(context,"Here is no notification",Toast.LENGTH_SHORT).show()
                                                            } else {   //if alr got 1 or more warning

                                                                var countInside = 0
                                                                var combineWarning =p.child("warningNotificationID").getValue().toString()
                                                                val splitWarning = combineWarning.split(",")
                                                                val splitSize = splitWarning.size
                                                                val lastWarning =splitWarning[splitSize - 1]
                                                                //Toast.makeText(context,lastWarning,Toast.LENGTH_SHORT).show()

                                                                ref4 = FirebaseDatabase.getInstance().getReference("Notification")
                                                                ref4.addValueEventListener(object :ValueEventListener {
                                                                    override fun onCancelled(error: DatabaseError) {
                                                                        TODO("Not yet implemented")
                                                                    }

                                                                    override fun onDataChange(snapshot: DataSnapshot) {
                                                                        if (snapshot.exists()) {
                                                                            if (countInside.toString().equals("0")) {
                                                                                for (n in snapshot.children) {

                                                                                    if (n.child("notificationID").getValue().toString().equals(lastWarning)) {
                                                                                        var notificationDate =n.child("notificationDateTime").getValue().toString()
                                                                                        var splitNotificationDate =notificationDate.split(" ")
                                                                                        var notificationDays =splitNotificationDate[0]
                                                                                        var engNotificationMonth =splitNotificationDate[1]
                                                                                        var notificationYear =splitNotificationDate[2]
                                                                                        var notificationMonth ="1"
                                                                                        //convert eng month to number
                                                                                        if (engNotificationMonth.equals(
                                                                                                "Jan"
                                                                                            )
                                                                                        ) {
                                                                                            notificationMonth =
                                                                                                "1"
                                                                                        } else if (engNotificationMonth.equals(
                                                                                                "Feb"
                                                                                            )
                                                                                        ) {
                                                                                            notificationMonth =
                                                                                                "2"
                                                                                        } else if (engNotificationMonth.equals(
                                                                                                "Mar"
                                                                                            )
                                                                                        ) {
                                                                                            notificationMonth =
                                                                                                "3"
                                                                                        } else if (engNotificationMonth.equals(
                                                                                                "Apr"
                                                                                            )
                                                                                        ) {
                                                                                            notificationMonth =
                                                                                                "4"
                                                                                        } else if (engNotificationMonth.equals(
                                                                                                "May"
                                                                                            )
                                                                                        ) {
                                                                                            notificationMonth =
                                                                                                "5"
                                                                                        } else if (engNotificationMonth.equals(
                                                                                                "Jun"
                                                                                            )
                                                                                        ) {
                                                                                            notificationMonth =
                                                                                                "6"
                                                                                        } else if (engNotificationMonth.equals(
                                                                                                "Jul"
                                                                                            )
                                                                                        ) {
                                                                                            notificationMonth =
                                                                                                "7"
                                                                                        } else if (engNotificationMonth.equals(
                                                                                                "Aug"
                                                                                            )
                                                                                        ) {
                                                                                            notificationMonth =
                                                                                                "8"
                                                                                        } else if (engNotificationMonth.equals(
                                                                                                "Sep"
                                                                                            )
                                                                                        ) {
                                                                                            notificationMonth =
                                                                                                "9"
                                                                                        } else if (engNotificationMonth.equals(
                                                                                                "Oct"
                                                                                            )
                                                                                        ) {
                                                                                            notificationMonth =
                                                                                                "10"
                                                                                        } else if (engNotificationMonth.equals(
                                                                                                "Nov"
                                                                                            )
                                                                                        ) {
                                                                                            notificationMonth =
                                                                                                "11"
                                                                                        } else if (engNotificationMonth.equals(
                                                                                                "Dec"
                                                                                            )
                                                                                        ) {
                                                                                            notificationMonth =
                                                                                                "12"
                                                                                        }

                                                                                        //today
                                                                                        val cal: Calendar =Calendar.getInstance()
                                                                                        val thisDay =SimpleDateFormat(("d"),Locale.getDefault()).format(cal.time)
                                                                                        val thisMonth =SimpleDateFormat(("MM"),Locale.getDefault()).format(cal.time)
                                                                                        val thisYear =SimpleDateFormat(("yyyy"),Locale.getDefault()).format(cal.time)
                                                                                        //val today = LocalDate.of(thisYear.toInt(), thisMonth.toInt(), thisDay.toInt())
                                                                                        var firstt =LocalDate.of(notificationYear.toInt(),notificationMonth.toInt(),notificationDays.toInt())
                                                                                        //var firstt = LocalDate.of(paymentYear.toInt(), paymentMonth.toInt(), paymentDate.toInt())
                                                                                        var secondd =LocalDate.of(thisYear.toInt(),thisMonth.toInt(),thisDay.toInt())
                                                                                        var dayCount =ChronoUnit.DAYS.between(secondd,firstt)
                                                                                        //Toast.makeText(context,"Current:"+secondd.toString(),Toast.LENGTH_SHORT).show()


                                                                                        var splitTitle1=
                                                                                            p.child("paymentTitle").getValue().toString()
                                                                                                .split("/")
                                                                                        var paymentDate1 = splitTitle1[0]
                                                                                        var paymentMonth1 = splitTitle1[1]
                                                                                        var paymentYearSection1 = splitTitle1[2].split(" ")
                                                                                        var paymentYear1 = paymentYearSection1[0]
                                                                                        var third = LocalDate.of(
                                                                                            paymentYear.toInt(),
                                                                                            paymentMonth.toInt(),
                                                                                            paymentDate.toInt()
                                                                                        )
                                                                                        //Toast.makeText(context,third.toString(),Toast.LENGTH_SHORT).show()
                                                                                        if (!(dayCount.toString().equals("0"))) {

                                                                                            var dayCount1 =ChronoUnit.DAYS.between(secondd,third)
                                                                                            val leftday = 5 + dayCount1
                                                                                            val notificationContent ="You have " + leftday.toString() + "days left for this payment(" + p.child("paymentTitle").getValue().toString() + ")"

                                                                                            //add notification
                                                                                            ref3 =FirebaseDatabase.getInstance().getReference("Notification")
                                                                                            var notificationID =ref3.push().key.toString()
                                                                                            val storeNotification = Notification(
                                                                                                notificationID,
                                                                                                "system",
                                                                                                "delivered",
                                                                                                notificationContent,
                                                                                                getTime(),
                                                                                                "warning",
                                                                                                r.userID
                                                                                            )
                                                                                            ref3.child(notificationID).setValue(storeNotification)
                                                                                            combineWarning += "," + notificationID.toString()
                                                                                            ref2.child(p.child("paymentID").getValue().toString()).child("warningNotificationID").setValue(combineWarning)
                                                                                        }
                                                                                    }

                                                                                }
                                                                                countInside++
                                                                            }
                                                                        }
                                                                    }

                                                                })

                                                                //Toast.makeText(context,"here is got",Toast.LENGTH_SHORT).show()
                                                            }

                                                        } else if (dayCount < -4) { //exceed 5days
                                                            //change status = cancel(many thing and property status to available)
                                                            //send a notification tell user his rent get cancelled
                                                            var countProperty=0
                                                            var countPayment1=0
                                                            //Toast.makeText(context,"here is expired!",Toast.LENGTH_SHORT).show()
                                                            //ref2.child(p.child("paymentID").getValue().toString()).child("status").setValue("expired")
                                                            ref1.child(r.rentID).child("status").setValue("withdraw")
                                                            var ref6=FirebaseDatabase.getInstance().getReference("Payment")
                                                            ref6.addValueEventListener(object:ValueEventListener{
                                                                override fun onCancelled(error: DatabaseError) {
                                                                    TODO("Not yet implemented")
                                                                }

                                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                                    if(snapshot.exists()){
                                                                        if(countPayment1.toString().equals("0")) {
                                                                            for (p1 in snapshot.children) {
                                                                                if(p1.child("rentID").getValue().toString().equals(r.rentID)&&
                                                                                    !(p1.child("status").getValue().toString().equals("paid")) &&
                                                                                    !(p1.child("status").getValue().toString().equals("expired"))){
                                                                                    ref6.child(p1.child("paymentID").getValue().toString()).child("status").setValue("expired")

                                                                                }
                                                                            }
                                                                            countPayment1++
                                                                        }
                                                                    }
                                                                }

                                                            })
                                                            var ref5=FirebaseDatabase.getInstance().getReference("Property")
                                                            ref5.addValueEventListener(object:ValueEventListener{
                                                                override fun onCancelled(error: DatabaseError) {
                                                                    TODO("Not yet implemented")
                                                                }

                                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                                    if(snapshot.exists()){
                                                                        if(countProperty.toString().equals("0")){
                                                                            for(pr in snapshot.children){
                                                                                if(r.propertyID.equals(pr.child("propertyID").getValue().toString())&&
                                                                                    pr.child("status").getValue().toString().equals("renting")){
                                                                                    ref5.child(pr.child("propertyID").getValue().toString()).child("status").setValue("available")

                                                                                    //add withdraw notification
                                                                                    val notificationContent ="Your rent for (" + pr.child("propertyName").getValue().toString() + ") had been withdrawned"

                                                                                    //add notification
                                                                                    val ref55 =FirebaseDatabase.getInstance().getReference("Notification")
                                                                                    var notificationID =ref55.push().key.toString()
                                                                                    val storeNotification = Notification(
                                                                                        notificationID,
                                                                                        "system",
                                                                                        "delivered",
                                                                                        notificationContent,
                                                                                        getTime(),
                                                                                        "withdraw",
                                                                                        r.userID
                                                                                    )
                                                                                    ref55.child(notificationID).setValue(storeNotification)

                                                                                }
                                                                            }
                                                                            countProperty++
                                                                        }
                                                                    }
                                                                }

                                                            })



                                                        } //long term process
                                                    }else{
                                                        if (dayCount.toString().equals("0")) { //1 days limit, 6 to 15 is left 1 day. this is within 10 days (short)
                                                            //change new to pending and add notification
                                                            var payment = p.getValue(Payment::class.java)
                                                            ref2.child(p.child("paymentID").getValue().toString()).child("status").setValue("pending")
                                                            //Toast.makeText(context,countNotification.toString().equals("0").toString(),Toast.LENGTH_SHORT).show()



                                                            //if no warning
                                                            if (p.child("warningNotificationID").getValue().toString().equals("")) {
                                                                //change payment status from pending to pending, new to pending
                                                                val leftday = 1 + dayCount
                                                                val notificationContent =
                                                                    "You have " + leftday.toString() + "days left for this payment(" + p.child(
                                                                        "paymentTitle"
                                                                    ).getValue().toString() + ")"

                                                                //add notification
                                                                ref3 = FirebaseDatabase.getInstance()
                                                                    .getReference("Notification")
                                                                var notificationID =
                                                                    ref3.push().key.toString()
                                                                val storeNotification = Notification(
                                                                    notificationID,
                                                                    "system",
                                                                    "delivered",
                                                                    notificationContent,
                                                                    getTime(),
                                                                    "warning",
                                                                    r.userID
                                                                )
                                                                ref3.child(notificationID)
                                                                    .setValue(storeNotification)
                                                                ref2.child(p.child("paymentID").getValue().toString()).child("warningNotificationID").setValue(notificationID)
                                                                //Toast.makeText(context,"Here is no notification",Toast.LENGTH_SHORT).show()
                                                            } else {   //if alr got 1 or more warning

                                                                var countInside = 0
                                                                var combineWarning =p.child("warningNotificationID").getValue().toString()
                                                                val splitWarning = combineWarning.split(",")
                                                                val splitSize = splitWarning.size
                                                                val lastWarning =splitWarning[splitSize - 1]
                                                                //Toast.makeText(context,lastWarning,Toast.LENGTH_SHORT).show()

                                                                ref4 = FirebaseDatabase.getInstance().getReference("Notification")
                                                                ref4.addValueEventListener(object :ValueEventListener {
                                                                    override fun onCancelled(error: DatabaseError) {
                                                                        TODO("Not yet implemented")
                                                                    }

                                                                    override fun onDataChange(snapshot: DataSnapshot) {
                                                                        if (snapshot.exists()) {
                                                                            if (countInside.toString().equals("0")) {
                                                                                for (n in snapshot.children) {

                                                                                    if (n.child("notificationID").getValue().toString().equals(lastWarning)) {
                                                                                        var notificationDate =n.child("notificationDateTime").getValue().toString()
                                                                                        var splitNotificationDate =notificationDate.split(" ")
                                                                                        var notificationDays =splitNotificationDate[0]
                                                                                        var engNotificationMonth =splitNotificationDate[1]
                                                                                        var notificationYear =splitNotificationDate[2]
                                                                                        var notificationMonth ="1"
                                                                                        //convert eng month to number
                                                                                        if (engNotificationMonth.equals(
                                                                                                "Jan"
                                                                                            )
                                                                                        ) {
                                                                                            notificationMonth =
                                                                                                "1"
                                                                                        } else if (engNotificationMonth.equals(
                                                                                                "Feb"
                                                                                            )
                                                                                        ) {
                                                                                            notificationMonth =
                                                                                                "2"
                                                                                        } else if (engNotificationMonth.equals(
                                                                                                "Mar"
                                                                                            )
                                                                                        ) {
                                                                                            notificationMonth =
                                                                                                "3"
                                                                                        } else if (engNotificationMonth.equals(
                                                                                                "Apr"
                                                                                            )
                                                                                        ) {
                                                                                            notificationMonth =
                                                                                                "4"
                                                                                        } else if (engNotificationMonth.equals(
                                                                                                "May"
                                                                                            )
                                                                                        ) {
                                                                                            notificationMonth =
                                                                                                "5"
                                                                                        } else if (engNotificationMonth.equals(
                                                                                                "Jun"
                                                                                            )
                                                                                        ) {
                                                                                            notificationMonth =
                                                                                                "6"
                                                                                        } else if (engNotificationMonth.equals(
                                                                                                "Jul"
                                                                                            )
                                                                                        ) {
                                                                                            notificationMonth =
                                                                                                "7"
                                                                                        } else if (engNotificationMonth.equals(
                                                                                                "Aug"
                                                                                            )
                                                                                        ) {
                                                                                            notificationMonth =
                                                                                                "8"
                                                                                        } else if (engNotificationMonth.equals(
                                                                                                "Sep"
                                                                                            )
                                                                                        ) {
                                                                                            notificationMonth =
                                                                                                "9"
                                                                                        } else if (engNotificationMonth.equals(
                                                                                                "Oct"
                                                                                            )
                                                                                        ) {
                                                                                            notificationMonth =
                                                                                                "10"
                                                                                        } else if (engNotificationMonth.equals(
                                                                                                "Nov"
                                                                                            )
                                                                                        ) {
                                                                                            notificationMonth =
                                                                                                "11"
                                                                                        } else if (engNotificationMonth.equals(
                                                                                                "Dec"
                                                                                            )
                                                                                        ) {
                                                                                            notificationMonth =
                                                                                                "12"
                                                                                        }

                                                                                        //today
                                                                                        val cal: Calendar =Calendar.getInstance()
                                                                                        val thisDay =SimpleDateFormat(("d"),Locale.getDefault()).format(cal.time)
                                                                                        val thisMonth =SimpleDateFormat(("MM"),Locale.getDefault()).format(cal.time)
                                                                                        val thisYear =SimpleDateFormat(("yyyy"),Locale.getDefault()).format(cal.time)
                                                                                        //val today = LocalDate.of(thisYear.toInt(), thisMonth.toInt(), thisDay.toInt())
                                                                                        var firstt =LocalDate.of(notificationYear.toInt(),notificationMonth.toInt(),notificationDays.toInt())
                                                                                        //var firstt = LocalDate.of(paymentYear.toInt(), paymentMonth.toInt(), paymentDate.toInt())
                                                                                        var secondd =LocalDate.of(thisYear.toInt(),thisMonth.toInt(),thisDay.toInt())
                                                                                        var dayCount =ChronoUnit.DAYS.between(secondd,firstt)
                                                                                        //Toast.makeText(context,"Current:"+secondd.toString(),Toast.LENGTH_SHORT).show()


                                                                                        var splitTitle1=
                                                                                            p.child("paymentTitle").getValue().toString()
                                                                                                .split("/")
                                                                                        var paymentDate1 = splitTitle1[0]
                                                                                        var paymentMonth1 = splitTitle1[1]
                                                                                        var paymentYearSection1 = splitTitle1[2].split(" ")
                                                                                        var paymentYear1 = paymentYearSection1[0]
                                                                                        var third = LocalDate.of(
                                                                                            paymentYear.toInt(),
                                                                                            paymentMonth.toInt(),
                                                                                            paymentDate.toInt()
                                                                                        )
                                                                                        //Toast.makeText(context,third.toString(),Toast.LENGTH_SHORT).show()
                                                                                        if (!(dayCount.toString().equals("0"))) {

                                                                                            var dayCount1 =ChronoUnit.DAYS.between(secondd,third)
                                                                                            val leftday = 1 + dayCount1
                                                                                            val notificationContent ="You have " + leftday.toString() + "days left for this payment(" + p.child("paymentTitle").getValue().toString() + ")"

                                                                                            //add notification
                                                                                            ref3 =FirebaseDatabase.getInstance().getReference("Notification")
                                                                                            var notificationID =ref3.push().key.toString()
                                                                                            val storeNotification = Notification(
                                                                                                notificationID,
                                                                                                "system",
                                                                                                "delivered",
                                                                                                notificationContent,
                                                                                                getTime(),
                                                                                                "warning",
                                                                                                r.userID
                                                                                            )
                                                                                            ref3.child(notificationID).setValue(storeNotification)
                                                                                            combineWarning += "," + notificationID.toString()
                                                                                            ref2.child(p.child("paymentID").getValue().toString()).child("warningNotificationID").setValue(combineWarning)
                                                                                        }
                                                                                    }

                                                                                }
                                                                                countInside++
                                                                            }
                                                                        }
                                                                    }

                                                                })

                                                                //Toast.makeText(context,"here is got",Toast.LENGTH_SHORT).show()
                                                            }

                                                        } else if (dayCount < 0) { //exceed 1days
                                                            //change status = cancel(many thing and property status to available)
                                                            //send a notification tell user his rent get cancelled
                                                            var countProperty=0
                                                            var countPayment1=0
                                                            //Toast.makeText(context,"here is expired!",Toast.LENGTH_SHORT).show()
                                                            //ref2.child(p.child("paymentID").getValue().toString()).child("status").setValue("expired")
                                                            ref1.child(r.rentID).child("status").setValue("withdraw")
                                                            var ref6=FirebaseDatabase.getInstance().getReference("Payment")
                                                            ref6.addValueEventListener(object:ValueEventListener{
                                                                override fun onCancelled(error: DatabaseError) {
                                                                    TODO("Not yet implemented")
                                                                }

                                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                                    if(snapshot.exists()){
                                                                        if(countPayment1.toString().equals("0")) {
                                                                            for (p1 in snapshot.children) {
                                                                                if(p1.child("rentID").getValue().toString().equals(r.rentID)&&
                                                                                    !(p1.child("status").getValue().toString().equals("paid")) &&
                                                                                    !(p1.child("status").getValue().toString().equals("expired"))){
                                                                                    ref6.child(p1.child("paymentID").getValue().toString()).child("status").setValue("expired")

                                                                                }
                                                                            }
                                                                            countPayment1++
                                                                        }
                                                                    }
                                                                }

                                                            })
                                                            var ref5=FirebaseDatabase.getInstance().getReference("Property")
                                                            ref5.addValueEventListener(object:ValueEventListener{
                                                                override fun onCancelled(error: DatabaseError) {
                                                                    TODO("Not yet implemented")
                                                                }

                                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                                    if(snapshot.exists()){
                                                                        if(countProperty.toString().equals("0")){
                                                                            for(pr in snapshot.children){
                                                                                if(r.propertyID.equals(pr.child("propertyID").getValue().toString())&&
                                                                                    pr.child("status").getValue().toString().equals("renting")){
                                                                                    ref5.child(pr.child("propertyID").getValue().toString()).child("status").setValue("available")


                                                                                    //add withdraw notification
                                                                                    val notificationContent ="Your rent for (" + pr.child("propertyName").getValue().toString() + ") had been withdrawned"

                                                                                    //add notification
                                                                                    val ref55 =FirebaseDatabase.getInstance().getReference("Notification")
                                                                                    var notificationID =ref55.push().key.toString()
                                                                                    val storeNotification = Notification(
                                                                                        notificationID,
                                                                                        "system",
                                                                                        "delivered",
                                                                                        notificationContent,
                                                                                        getTime(),
                                                                                        "withdraw",
                                                                                        r.userID
                                                                                    )
                                                                                    ref55.child(notificationID).setValue(storeNotification)
                                                                                }
                                                                            }
                                                                            countProperty++
                                                                        }
                                                                    }
                                                                }

                                                            })


                                                        }  //short process

                                                    } //end of choose term
                                                }//hehe
                                            }
                                        }
                                        countNotification++
                                    }
                                } //up here
                            }

                        })
                        countRent++
                    }
                }
            }

        })

    }

    private fun notification(notificationCount : String){

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("GoNotification", "true")
        val pendingIntent =  PendingIntent.getActivity(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT)

        val contentView = RemoteViews(packageName, R.layout.notification_popup)
        contentView.setImageViewResource(R.id.tv_icon, R.drawable.ic_home2)
        contentView.setTextViewText(R.id.tv_title, "Setapak House")
        contentView.setTextViewText(R.id.tv_content, "You have " + notificationCount + " unread notifications")

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelID, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = android.app.Notification.Builder(this,channelID)
                .setContentTitle("Setapak House")
                .setContentText("You have " + notificationCount + " unread notification")
                .setSmallIcon(R.drawable.ic_home2)
                .setColor(rgb(38, 153, 251))
                .setLargeIcon((BitmapFactory.decodeResource(this.resources, R.drawable.ic_home2)))
                .setBadgeIconType(R.drawable.ic_home2)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        }
        else{
            builder = android.app.Notification.Builder(this)
                .setContentTitle("Setapak House")
                .setContentText("You have " + notificationCount + " unread notification")
                .setColor(rgb(38, 153, 251))
                .setSmallIcon(R.drawable.ic_home2)
                .setLargeIcon((BitmapFactory.decodeResource(this.resources, R.drawable.ic_home2)))
                .setBadgeIconType(R.drawable.ic_home2)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
        }

        notificationManager.notify(1234,builder.build())

    }

    private fun notification1(){

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("GoNotification1", "true")
        val pendingIntent =  PendingIntent.getActivity(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT)

        val contentView = RemoteViews(packageName, R.layout.notification_popup)
        contentView.setImageViewResource(R.id.tv_icon, R.drawable.ic_home2)
        contentView.setTextViewText(R.id.tv_title, "Setapak House")
        contentView.setTextViewText(R.id.tv_content, "You have new notification! Please check it out.")

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(channelID, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = android.app.Notification.Builder(this,channelID)
                .setContentTitle("Setapak House")
                .setColor(rgb(38, 153, 251))
                .setContentText("You have new notification! Please check it out.")
                .setSmallIcon(R.drawable.ic_home2)
                .setLargeIcon((BitmapFactory.decodeResource(this.resources, R.drawable.ic_home2)))
                .setBadgeIconType(R.drawable.ic_home2)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
        }
        else{
            builder = android.app.Notification.Builder(this)
                .setContentTitle("Setapak House")
                .setContentText("You have new notification! Please check it out.")
                .setSmallIcon(R.drawable.ic_home2)
                .setColor(rgb(38, 153, 251))
                .setLargeIcon((BitmapFactory.decodeResource(this.resources, R.drawable.ic_home2)))
                .setBadgeIconType(R.drawable.ic_home2)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
        }

        notificationManager.notify(1234,builder.build())

    }
}