package com.example.setapakhouse.Adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Color.rgb
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.setapakhouse.*
import com.example.setapakhouse.Model.Notification
import com.example.setapakhouse.Model.Property
import com.example.setapakhouse.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class MyRentalHouseAdapter(val property : MutableList<Property>): RecyclerView.Adapter<MyRentalHouseAdapter.MyViewHolder>() {

    lateinit var ref:DatabaseReference
    lateinit var ref1: DatabaseReference
    lateinit var ref2: DatabaseReference
    lateinit var ref3: DatabaseReference
    lateinit var epicDialog : Dialog


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.my_rental_house_list,parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return property.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        epicDialog = Dialog(holder.editButton.context)

        val currentUserID11 = FirebaseAuth.getInstance().currentUser!!.uid

        holder.txt_location.text=property[position].location
        if(property[position].rentalType.toString().equals("Long-Term")) {
            holder.txt_price.text = "RM"+String.format("%.2f",property[position].price.toString().toDouble())+"/MONTH"
        }else{
            holder.txt_price.text = "RM"+String.format("%.2f",property[position].price.toString().toDouble())+"/DAY"
        }
        holder.txt_propertyType.text="Property Type: "+property[position].propertyType.toString()
        holder.txt_rentalType.text="Rental Type: "+property[position].rentalType.toString()
        holder.txt_propertyName.text="Property Name: "+property[position].propertyName.toString()

        val userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUserID11)

        userRef.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    holder.hiddenCurrentUsername.text = snapshot.child("username").getValue().toString()
                }
            }

        })


        ref=FirebaseDatabase.getInstance().getReference("PropertyImage")
        ref.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){

                    for(h in snapshot.children){
                        if(h.child("propertyID").getValue().toString().equals(property[position].propertyID) && h.child("imageName").getValue().toString().equals("image1")){
                            Picasso.get().load(h.child("imageSource").getValue().toString()).placeholder(R.drawable.ic_home).into(holder.img_property)
                        }
                    }
                }
            }

        })

        if(property[position].status!="available"){
            //get the username who rent this property
            ref=FirebaseDatabase.getInstance().getReference("Rent")

            ref.addValueEventListener(object:ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        var duration = ""
                        for(h in snapshot.children){
                            if(h.child("propertyID").getValue().toString().equals(property[position].propertyID) && h.child("status").getValue().toString()!="completed"){
                                holder.hiddenUserID.text = h.child("userID").getValue().toString()
                                duration = " from " + h.child("checkInDate").getValue().toString() + " to " + h.child("checkOutDate").getValue().toString()
                            }
                        }

                        ref1 = FirebaseDatabase.getInstance().getReference("Users").child(holder.hiddenUserID.text.toString())

                        ref1.addValueEventListener(object:ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    //change the status
                                    holder.statusText.text = "Status : Currently rented by " + snapshot.child("username").getValue().toString() + duration
                                    holder.statusText.setTextColor(rgb(255,0,0))
                                    //put profile pic
                                    Picasso.get().load(snapshot.child("image").getValue().toString()).placeholder(R.drawable.ic_profile).into(holder.profilePhoto)
                                    holder.profilePhoto.visibility = View.VISIBLE
                                    holder.chatBtn.visibility = View.VISIBLE

                                    //holder.profilePhoto.setBackgroundColor(rgb(0,0,0))
                                }
                            }
                        })
                    }
                }

            })
        }

        holder.chatBtn.setOnClickListener {
            epicDialog.setContentView(R.layout.popup_confirmation)
            //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
            val yesButton : Button = epicDialog.findViewById(R.id.yesBtn)
            val cancelButton : Button = epicDialog.findViewById(R.id.cancelBtn)
            val title : TextView = epicDialog.findViewById(R.id.title)
            val content : TextView = epicDialog.findViewById(R.id.content)

            title.text = "Chat With Owner"

            content.text = "Do you wish to chat with the tenant of your property?"


            yesButton.text = "Yes"
            yesButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

            yesButton.setOnClickListener {
                epicDialog.dismiss()
                val intent = Intent(holder.chatBtn.context, MessageActivity::class.java)
                intent.putExtra("selectedUserID",holder.hiddenUserID.text.toString())
                holder.chatBtn.context.startActivity(intent)
            }
            cancelButton.setOnClickListener {
                epicDialog.dismiss()
            }

            epicDialog.setCancelable(true)
            epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            epicDialog.show()
        }


        holder.img_property.setOnClickListener{
            val intent = Intent(holder.img_property.context, detailPost::class.java)
            intent.putExtra("selectedPosition", property[position].propertyID)
            intent.putExtra("selectedUserID",property[position].userID)
            holder.img_property.context.startActivity(intent)
        }

        holder.removeButton.setOnClickListener {
            if(property[position].status=="available"){
                epicDialog.setContentView(R.layout.popup_negative)
                //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
                val yesButton: Button = epicDialog.findViewById(R.id.yesBtn)
                val cancelButton: Button = epicDialog.findViewById(R.id.cancelBtn)
                val title: TextView = epicDialog.findViewById(R.id.title)
                val content: TextView = epicDialog.findViewById(R.id.content)
                val image : ImageView = epicDialog.findViewById(R.id.warningImage)

                title.text = "Remove Confirmation"
                content.text = "Are you sure to remove this property?"
                yesButton.text = "Yes"
                //image.setBackgroundResource(R.drawable.ic_remove)

                yesButton.setOnClickListener {
                    //Change property status to "unavailable"
                    ref1 = FirebaseDatabase.getInstance().getReference("Property").child(property[position].propertyID)

                    showDialog(epicDialog, property[position].propertyName, property[position].propertyID, holder.hiddenCurrentUsername.text.toString())
                }

                cancelButton.setOnClickListener {
                    epicDialog.dismiss()
                }

                epicDialog.setCancelable(true)
                epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                epicDialog.show()
            }
            else{
                showDialog2()
            }

        }

        holder.editButton.setOnClickListener {
            if(property[position].status=="available"){
                val intent = Intent(holder.editButton.context, EditRentalHouse::class.java)
                intent.putExtra("RenterType", property[position].rentalType)
                intent.putExtra("PropertyID" , property[position].propertyID)
                intent.putExtra("PropertyName", property[position].propertyName)
                intent.putExtra("RentalPrice", property[position].price.toString())
                intent.putExtra("Description", property[position].description)
                intent.putExtra("Accomodation", property[position].accommodation)
                intent.putExtra("Preference", property[position].preference)
                holder.editButton.context.startActivity(intent)
            }
            else{
                showDialog2()
            }

        }
    }

    private fun showDialog(abc: Dialog, propertyName : String, position: String, username :String){
        epicDialog.setContentView(R.layout.popup_user_confirmation)
        //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
        val okButton : Button = epicDialog.findViewById(R.id.yesBtn)
        val cancelButton : Button = epicDialog.findViewById(R.id.cancelBtn)
        val title : TextView = epicDialog.findViewById(R.id.title)
        val content : TextView = epicDialog.findViewById(R.id.content)
        val propertyText : TextView = epicDialog.findViewById(R.id.propertyName)
        val propertyEdit : EditText = epicDialog.findViewById(R.id.propertyEditText)

        //title.text = "Approve Successful"
        //content.text = "A notification will be sent to the requester to inform them"
        propertyText.text = propertyName

        okButton.setOnClickListener {
            if(propertyEdit.text.isEmpty()){
                propertyEdit.setError("This field cannot be empty!")
                propertyEdit.requestFocus()
            }
            else{
                val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
                propertyEdit.error = null
                if(propertyEdit.text.toString() == propertyText.text.toString()){
                    ref1.child("status").setValue("unavailable")
                    showDialog1(abc, epicDialog)

                    val approvalRef = FirebaseDatabase.getInstance().getReference("Approval")
                    var store1 = 0
                    approvalRef.addValueEventListener(object:ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(store1 == 0){
                                if (snapshot.exists()) {
                                    for(h in snapshot.children){
                                        var store2 = 0
                                        Log.d("position", "position = " + h.child("propertyID").getValue().toString())
                                        Log.d("position", "position = " + position)

                                        if(h.child("propertyID").getValue().toString().equals(position) && h.child("status").getValue().toString()=="pending"){
                                            val ref7 =FirebaseDatabase.getInstance().getReference("Approval").child(h.child("approvalID").getValue().toString())
                                            ref7.child("status").setValue("unavailable")

                                            val ref8 = FirebaseDatabase.getInstance().getReference("Notification")

                                            var notificationID = ref8.push().key.toString()
                                            //IMPORTANT - change the user ID to username
                                            val notificationContent = username + " had remove his own property " + propertyName

                                            val storeNotification = Notification(
                                                notificationID,
                                                currentUserID,
                                                "delivered",
                                                notificationContent,
                                                getTime(),
                                                "approvalConfirmation",
                                                h.child("userID").getValue().toString()
                                            )

                                            if (store2 == 0) {
                                                ref8.child(notificationID).setValue(storeNotification)
                                                store2++
                                            }

                                        }
                                    }
                                }
                                store1++
                            }

                        }
                    })

                    //epicDialog.dismiss()
                    //abc.dismiss()
                }
                else{
                    propertyEdit.setError("Wrong property name")
                    propertyEdit.requestFocus()
                }

            }
        }

        cancelButton.setOnClickListener {
            epicDialog.dismiss()
        }
        epicDialog.setCancelable(true)
        epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        epicDialog.show()
    }

    private fun showDialog1(abc1: Dialog, abc2 : Dialog){
        epicDialog.setContentView(R.layout.popup_positive)
        //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
        val okButton : Button = epicDialog.findViewById(R.id.okBtn)
        val title : TextView = epicDialog.findViewById(R.id.title)
        val content : TextView = epicDialog.findViewById(R.id.content)

        title.text = "Remove Successful"
        content.text = "Your property had been removed successfully"

        okButton.setOnClickListener {
            epicDialog.dismiss()
            abc1.dismiss()
            abc2.dismiss()
        }
        epicDialog.setCancelable(true)
        epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        epicDialog.show()
    }

    private fun showDialog2(){
        epicDialog.setContentView(R.layout.popup_error)
        //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
        val okButton : Button = epicDialog.findViewById(R.id.okBtn)
        val title : TextView = epicDialog.findViewById(R.id.title)
        val content : TextView = epicDialog.findViewById(R.id.content)

        title.text = "Invalid Action "
        content.text = "Your property is currently rented by the others"

        okButton.setOnClickListener {
            epicDialog.dismiss()
        }
        epicDialog.setCancelable(true)
        epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        epicDialog.show()
    }

    inner class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var txt_price:TextView=itemView.findViewById<TextView>(R.id.txtPrice)
        var txt_location:TextView=itemView.findViewById<TextView>(R.id.txtLocation)
        var txt_propertyName:TextView=itemView.findViewById<TextView>(R.id.txtPropertyName)
        var img_property: ImageView = itemView.findViewById<ImageView>(R.id.imgProperty)
        var txt_propertyType:TextView=itemView.findViewById<TextView>(R.id.txtPropertyType)
        var txt_rentalType:TextView=itemView.findViewById<TextView>(R.id.txtRentalType)
        var editButton:TextView=itemView.findViewById<TextView>(R.id.editBtn)
        var removeButton:TextView=itemView.findViewById<TextView>(R.id.removeBtn)
        var statusText:TextView=itemView.findViewById<TextView>(R.id.statusText)
        var hiddenUserID:TextView=itemView.findViewById<TextView>(R.id.hiddenUserID)
        var profilePhoto: CircleImageView =itemView.findViewById<CircleImageView>(R.id.profilePhoto)
        var chatBtn:Button=itemView.findViewById<Button>(R.id.chatBtn)
        var hiddenCurrentUsername:TextView=itemView.findViewById<TextView>(R.id.currentUsername)


    }

    private fun getTime(): String {

        val today = LocalDateTime.now(ZoneId.systemDefault())

        return today.format(DateTimeFormatter.ofPattern("d MMM uuuu HH:mm:ss "))
    }
}