package com.example.setapakhouse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.setapakhouse.Adapter.HomeAdapter
import com.example.setapakhouse.Model.Property
import com.google.firebase.database.*
import com.mindorks.editdrawabletext.DrawablePosition
import com.mindorks.editdrawabletext.onDrawableClickListener
import kotlinx.android.synthetic.main.activity_search_result.*
import kotlinx.android.synthetic.main.activity_search_without_filter.*
import kotlinx.android.synthetic.main.activity_search_without_filter.backBtn
import kotlinx.android.synthetic.main.activity_search_without_filter.noRecordFound
import kotlinx.android.synthetic.main.activity_search_without_filter.recycler_view
import kotlinx.android.synthetic.main.activity_search_without_filter.resultFound
import kotlinx.android.synthetic.main.activity_search_without_filter.searchBox
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*

class SearchWithoutFilter : AppCompatActivity() {

    lateinit var ref : DatabaseReference
    lateinit var ref2 : DatabaseReference
    lateinit var propertyList : MutableList<Property>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_without_filter)

        propertyList = mutableListOf()

        val propertyName = intent.getStringExtra("PropertyName")

        searchBox.setText(propertyName)

        searchBox.setDrawableClickListener(object : onDrawableClickListener {
            override fun onClick(target: DrawablePosition) {
                when (target) {
                    DrawablePosition.RIGHT -> {
                        val searchElement = searchBox.text.toString()
                        searchProperty(searchElement)
                    }
                }
            }
        })


        ref = FirebaseDatabase.getInstance().getReference("Property")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    propertyList.clear()
                    for (h in snapshot.children){
                        val propertyName1 = h.child("propertyName").getValue().toString().toLowerCase()
                        val propertyName2 = propertyName.toLowerCase()
                        val location1 = h.child("location").getValue().toString().toLowerCase()
                        if(h.child("status").getValue().toString().equals("available") && (propertyName1.contains(propertyName2)|| location1.contains(propertyName2))) {
                        //if(h.child("status").getValue().toString().equals("available")) {
                            val property = h.getValue(Property::class.java)
                            propertyList.add(property!!)
                            //Log.d(tag,propertyList.toString())
                        }

                    }

                    if(propertyList.size == 0){
                        noRecordFound.visibility = View.VISIBLE
                    }
                    else{
                        noRecordFound.visibility = View.GONE
                    }

                    val mLayoutManager = LinearLayoutManager(this@SearchWithoutFilter)
                    mLayoutManager.reverseLayout = true
                    resultFound.text = propertyList.size.toString() + " results founded"
                    recycler_view.layoutManager = mLayoutManager
                    recycler_view.scrollToPosition(propertyList.size-1)
                    recycler_view.adapter = HomeAdapter(propertyList, true)
                }
            }
        })

        backBtn.setOnClickListener {
            finish()
        }


    }

    private fun searchProperty(searchElement : String){
        val intent = Intent(this, SearchWithoutFilter::class.java)
        intent.putExtra("searchType", "propertyName")
        intent.putExtra("PropertyName", searchElement)
        startActivity(intent)
        finish()
    }
}