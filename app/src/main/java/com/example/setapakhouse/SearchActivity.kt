package com.example.setapakhouse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.*

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setTitle("Search")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }
}