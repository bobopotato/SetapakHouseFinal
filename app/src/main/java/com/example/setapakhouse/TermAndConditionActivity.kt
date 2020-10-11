package com.example.setapakhouse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.*

class TermAndConditionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_term_and_condition)

        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setTitle("Term & Condition")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}