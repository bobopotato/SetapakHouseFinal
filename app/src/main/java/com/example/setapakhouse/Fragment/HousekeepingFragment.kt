package com.example.setapakhouse.Fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.setapakhouse.MainActivity
import com.example.setapakhouse.R
import com.example.setapakhouse.test
import kotlinx.android.synthetic.main.fragment_housekeeping.view.*


class HousekeepingFragment : Fragment() {

    lateinit var epicDialog : Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_housekeeping, container, false)

        epicDialog = Dialog(activity!!)

        root.button.setOnClickListener {
            val intent = Intent(context, test::class.java)
            startActivity(intent)
            //showDialog()
        }

        return root
    }

    private fun showDialog(){
        epicDialog.setContentView(R.layout.popup_positive)
        //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
        val okButton : Button = epicDialog.findViewById(R.id.okBtn)
        val title : TextView = epicDialog.findViewById(R.id.title)
        val content : TextView = epicDialog.findViewById(R.id.content)

        title.text = "Post Successful"
        content.text = "You will be redirected to the main page"

        okButton.setOnClickListener {
            //val intent = Intent(this@HousekeepingFragment, MainActivity::class.java)
            //startActivity(intent)
            epicDialog.dismiss()
        }
        epicDialog.setCancelable(false)
        epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        epicDialog.show()
    }

}