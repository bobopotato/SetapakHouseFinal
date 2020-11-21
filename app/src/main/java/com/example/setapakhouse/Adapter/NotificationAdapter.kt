package com.example.setapakhouse.Adapter

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.setapakhouse.*
import com.example.setapakhouse.Fragment.NotificationFragment
import com.example.setapakhouse.Model.Approval
import com.example.setapakhouse.Model.Notification
import com.example.setapakhouse.Model.User
import com.example.setapakhouse.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_notification.*

class NotificationAdapter(val notificationList:MutableList<Notification>): RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    lateinit var query: Query
    lateinit var ref1 : DatabaseReference
    lateinit var ref2 : DatabaseReference
    lateinit var epicDialog : Dialog
    lateinit var abcListener: ValueEventListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.notification_layout,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {

        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid

        epicDialog = Dialog(holder.wholeLayout.context)

        query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("userID").equalTo(notificationList[position].sender)

        query.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for(h in p0.children){
                        val targetUser = h.getValue(User::class.java)
                        val user = targetUser!!.username
                        val profilePhoto = targetUser!!.image

                        holder.username.text = user
                        Picasso.get().load(profilePhoto).placeholder(R.drawable.profile).into(holder.profilePhoto)
                    }
                }
            }
        })

        holder.date.text = notificationList[position].notificationDateTime
        holder.content.text = notificationList[position].content

        if(notificationList[position].status == "delivered"){
            holder.wholeLayout.setBackgroundColor(Color.rgb(204, 230, 255))
        }

        if(notificationList[position].type == "warning" && notificationList[position].status == "delivered"){
            holder.wholeLayout.setBackgroundColor(Color.rgb(255,204,203))
        }

        if(notificationList[position].sender == "system"){
            holder.profilePhoto.setImageResource(R.drawable.ic_page)
            //holder.profilePhoto.setBackgroundResource(R.drawable.ic_page)
            holder.username.text = "System Message"
        }

        var username = holder.username.text

        holder.wholeLayout.setOnClickListener {
            if(notificationList[position].type=="approval"){
                val notificationFragment = NotificationFragment()

                val manager: FragmentManager = (holder.wholeLayout.context as AppCompatActivity).supportFragmentManager

                manager.beginTransaction()
                    .replace(R.id.fl_wrapper, notificationFragment).addToBackStack(null)
                    .commit()

                val pref= PreferenceManager.getDefaultSharedPreferences(holder.wholeLayout.context)
                val editor=pref.edit()
                editor
                    .putString("changeTab","approval")
                    .apply()

                /*
                val pref1= PreferenceManager.getDefaultSharedPreferences(holder.wholeLayout.context)
                val editor1=pref1.edit()
                editor1
                    .putString("changeTab","approval")
                    .apply()
                */


            }

            if(notificationList[position].type=="approvalConfirmation"){
                if (notificationList[position].content.contains("approved")){
                    val intent = Intent(holder.wholeLayout.context, houseRentingActivity::class.java)
                    holder.wholeLayout.context.startActivity(intent)
                }
                if (notificationList[position].content.contains("rejected") || notificationList[position].content.contains("expired") || notificationList[position].content.contains("remove")){

                    epicDialog.setContentView(R.layout.popup_confirmation)
                    //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
                    val yesButton : Button = epicDialog.findViewById(R.id.yesBtn)
                    val cancelButton : Button = epicDialog.findViewById(R.id.cancelBtn)
                    val title : TextView = epicDialog.findViewById(R.id.title)
                    val content : TextView = epicDialog.findViewById(R.id.content)

                    title.text = "Chat With Owner"
                    if(notificationList[position].content.contains("rejected")){
                        content.text = "Do you wish to chat with the owner for the reason getting rejected?"
                    }
                    if(notificationList[position].content.contains("expired")){
                        content.text = "Do you wish to chat with the owner for the reason getting ignored?"
                    }
                    if(notificationList[position].content.contains("expired")){
                        content.text = "Do you wish to chat with the owner for the reason why the property is removed?"
                    }
                    yesButton.text = "Yes"
                    yesButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

                    yesButton.setOnClickListener {
                        epicDialog.dismiss()
                        val intent = Intent(holder.wholeLayout.context, MessageActivity::class.java)
                        intent.putExtra("selectedUserID",notificationList[position].sender)
                        holder.wholeLayout.context.startActivity(intent)
                    }
                    cancelButton.setOnClickListener {
                        epicDialog.dismiss()
                    }

                    epicDialog.setCancelable(true)
                    epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    epicDialog.show()
                }

            }

            if(notificationList[position].type == "warning"){
                val intent = Intent(holder.wholeLayout.context, PaymentActivity::class.java)
                holder.wholeLayout.context.startActivity(intent)
            }

            if(notificationList[position].type == "review"){
                //Log.d("aaaa", "a11a = " + notificationList[position].notificationID)
                ref1 = FirebaseDatabase.getInstance().getReference("Review")

                abcListener = ref1.addValueEventListener(object: ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if(p0.exists()){
                            for(h in p0.children){
                                if(h.child("notificationID").getValue().toString().equals(notificationList[position].notificationID)){
                                    val intent = Intent(holder.wholeLayout.context, detailPost::class.java)
                                    intent.putExtra("selectedPosition", h.child("propertyID").getValue().toString())
                                    intent.putExtra("selectedUserID",currentUserID)
                                    holder.wholeLayout.context.startActivity(intent)
                                    //Log.d("aaaa", "bbb")
                                }
                            }
                        }
                    }
                })
                //ref1.removeEventListener(abcListener)
            }

            if(notificationList[position].type == "withdraw"){

                val pref= PreferenceManager.getDefaultSharedPreferences(holder.wholeLayout.context)
                val editor=pref.edit()
                editor
                    .putString("changeTabRentingHistory","yes")
                    .apply()

                val intent = Intent(holder.wholeLayout.context, houseRentingActivity::class.java)
                holder.wholeLayout.context.startActivity(intent)
            }

            //Change notification status to "seen"
            ref2 = FirebaseDatabase.getInstance().getReference("Notification").child(notificationList[position].notificationID)
            ref2.child("status").setValue("seen")

        }


    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val wholeLayout : LinearLayout = itemView.findViewById(R.id.notificationCard)
        val profilePhoto : CircleImageView = itemView.findViewById(R.id.profilePhoto)
        val username : TextView = itemView.findViewById(R.id.tv_title)
        val date : TextView = itemView.findViewById(R.id.tv_date)
        val content : TextView = itemView.findViewById(R.id.tv_content)

        //val uploadedImage : ImageView = itemView.findViewById(R.id.uploadedImage)
        //val imageName : TextView = itemView.findViewById(R.id.imageName)

    }

    private fun showDialog(){
        epicDialog.setContentView(R.layout.popup_confirmation)
        //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
        val yesButton : Button = epicDialog.findViewById(R.id.yesBtn)
        val cancelButton : Button = epicDialog.findViewById(R.id.cancelBtn)
        val title : TextView = epicDialog.findViewById(R.id.title)
        val content : TextView = epicDialog.findViewById(R.id.content)

        title.text = "Chat With Owner"
        content.text = "Do you wish to chat with the owner for the reason getting rejected?"
        yesButton.text = "Yes"
        yesButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

        yesButton.setOnClickListener {
            epicDialog.dismiss()
        }
        cancelButton.setOnClickListener {
            epicDialog.dismiss()
        }

        epicDialog.setCancelable(true)
        epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        epicDialog.show()
    }



}