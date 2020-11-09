package com.example.setapakhouse.Adapter

import android.graphics.Color
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.notification_layout,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {

        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid

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
            holder.wholeLayout.setBackgroundColor(Color.rgb(135,206,250))
        }

        var username = holder.username.text

        holder.wholeLayout.setOnClickListener {
            if(notificationList[position].type.toString()=="approval"){
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



}