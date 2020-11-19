package com.example.setapakhouse.Fragment

import android.content.Intent
import android.icu.lang.UCharacter.IndicPositionalCategory.LEFT
import android.icu.lang.UCharacter.IndicPositionalCategory.RIGHT
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.setapakhouse.*
import com.mindorks.editdrawabletext.DrawablePosition
import com.mindorks.editdrawabletext.onDrawableClickListener
import kotlinx.android.synthetic.main.activity_post1.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.houseButton
import kotlinx.android.synthetic.main.fragment_search.longTermButton
import kotlinx.android.synthetic.main.fragment_search.propertyType
import kotlinx.android.synthetic.main.fragment_search.renterType
import kotlinx.android.synthetic.main.fragment_search.roomButton
import kotlinx.android.synthetic.main.fragment_search.shortTermButton
import kotlinx.android.synthetic.main.fragment_search.view.*


class SearchFragment : Fragment() {

    lateinit var scaleUp: Animation
    lateinit var scaleDown: Animation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_search, container, false)

        root.searchBox.setDrawableClickListener(object : onDrawableClickListener {
            override fun onClick(target: DrawablePosition) {
                when (target) {
                    DrawablePosition.RIGHT -> {
                        val searchElement = searchBox.text.toString()
                        searchProperty(searchElement)
                    }
                }
            }
        })

        root.searchWithoutFilterBtn.setOnClickListener {
            val searchElement = searchBox.text.toString()
            searchProperty(searchElement)
        }

        scaleUp = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.scale_up)
        scaleDown = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.scale_down)

        var renterType = "Long-Term"
        var propertyType = "House"


        root.longTermButton.setOnTouchListener { v, event ->
            val action = event.action
            when(action){
                MotionEvent.ACTION_UP->{
                    longTermButton.startAnimation(scaleDown)
                }
                MotionEvent.ACTION_DOWN->{
                    longTermButton.startAnimation(scaleUp)
                    longTermButton.setBackgroundResource(R.drawable.round_button_blue)
                    shortTermButton.setBackgroundResource(R.drawable.round_button_white)
                    renterType="Long-Term"
                }
                else ->{

                }
            }

            true
        }

        root.shortTermButton.setOnTouchListener { v, event ->
            val action = event.action
            when(action){
                MotionEvent.ACTION_UP->{
                    shortTermButton.startAnimation(scaleDown)
                }
                MotionEvent.ACTION_DOWN->{
                    shortTermButton.startAnimation(scaleUp)
                    shortTermButton.setBackgroundResource(R.drawable.round_button_blue)
                    longTermButton.setBackgroundResource(R.drawable.round_button_white)
                    renterType="Short-Term"
                }
                else ->{

                }
            }

            true
        }

        root.houseButton.setOnTouchListener { v, event ->
            val action = event.action
            when(action){
                MotionEvent.ACTION_UP->{
                    houseButton.startAnimation(scaleDown)

                }
                MotionEvent.ACTION_DOWN->{
                    houseButton.startAnimation(scaleUp)
                    houseButton.setBackgroundResource(R.drawable.round_button_blue)
                    roomButton.setBackgroundResource(R.drawable.round_button_white)
                    propertyType = "House"
                }
                else ->{

                }
            }

            true
        }

        root.roomButton.setOnTouchListener { v, event ->
            val action = event.action
            when(action){
                MotionEvent.ACTION_UP->{
                    roomButton.startAnimation(scaleDown)
                }
                MotionEvent.ACTION_DOWN->{
                    roomButton.startAnimation(scaleUp)
                    roomButton.setBackgroundResource(R.drawable.round_button_blue)
                    houseButton.setBackgroundResource(R.drawable.round_button_white)
                    propertyType = "Room"
                }
                else ->{

                }
            }
            true
        }

        root.searchBtn.setOnClickListener {
            val intent = Intent(context, SearchActivity1::class.java)
            intent.putExtra("RenterType", renterType)
            intent.putExtra("PropertyType", propertyType)
            startActivity(intent)
        }

        return root
    }

    private fun searchProperty(searchElement : String){
        val intent = Intent(context, SearchWithoutFilter::class.java)
        intent.putExtra("searchType", "propertyName")
        intent.putExtra("PropertyName", searchElement)
        startActivity(intent)
    }


}