package com.example.setapakhouse

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    lateinit var ref : DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.fragment_home, container, false)

        ref = FirebaseDatabase.getInstance().getReference("Property")

        ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    //Toast.makeText(context,"wtf = " + snapshot.child("Area").value.toString(),Toast.LENGTH_SHORT).show()
                    //Log.d("wtf", "hoho = existo0o")

                }
            }
        })

        return root
    }



}