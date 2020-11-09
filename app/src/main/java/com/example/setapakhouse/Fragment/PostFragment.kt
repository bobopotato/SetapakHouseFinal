package com.example.setapakhouse.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.setapakhouse.PostActivity1
import com.example.setapakhouse.R
import com.example.setapakhouse.test
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout
import kotlinx.android.synthetic.main.fragment_post.view.*


class PostFragment : Fragment() {

    private var verticalStepperForm: VerticalStepperFormLayout? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root: View = inflater.inflate(R.layout.fragment_post, container, false)

        root.getStartButton.setOnClickListener {
            val intent = Intent(context, PostActivity1::class.java)
            startActivity(intent)
        }


        return root
    }




}