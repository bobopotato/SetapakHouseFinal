package com.example.setapakhouse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.setapakhouse.Adapter.fragmentAdapter4
import com.example.setapakhouse.Adapter.reviewFragmentAdapter
import kotlinx.android.synthetic.main.activity_my_review.*
import kotlinx.android.synthetic.main.activity_my_transaction.*

class myTransactionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_transaction)

        transactionBack.bringToFront()
        transactionBack.setOnClickListener {
            finish()
        }

        viewPagerTransaction.adapter= fragmentAdapter4(supportFragmentManager)
        tabLayoutTransaction.setupWithViewPager(viewPagerTransaction)
    }
}