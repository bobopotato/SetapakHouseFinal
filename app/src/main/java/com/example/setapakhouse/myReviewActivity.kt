package com.example.setapakhouse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.setapakhouse.Adapter.fragmentAdapter3
import com.example.setapakhouse.Adapter.reviewFragmentAdapter
import kotlinx.android.synthetic.main.activity_my_payment.*
import kotlinx.android.synthetic.main.activity_my_review.*

class myReviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_review)

        reviewBackBtn.bringToFront()
        reviewBackBtn.setOnClickListener {
            finish()
        }
        viewPagerReview.adapter= reviewFragmentAdapter(supportFragmentManager)
        tabLayoutReview.setupWithViewPager(viewPagerReview)
    }
}