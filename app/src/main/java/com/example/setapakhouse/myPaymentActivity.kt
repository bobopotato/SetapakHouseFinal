package com.example.setapakhouse

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.setapakhouse.Adapter.chatViewPagerAdapter
import com.example.setapakhouse.Adapter.fragmentAdapter3
import com.example.setapakhouse.Model.UserInfo
import com.paypal.android.sdk.payments.PayPalConfiguration
import com.paypal.android.sdk.payments.PayPalPayment
import com.paypal.android.sdk.payments.PayPalService
import com.paypal.android.sdk.payments.PaymentActivity
import kotlinx.android.synthetic.main.activity_chatroom.*
import kotlinx.android.synthetic.main.activity_my_payment.*
import kotlinx.android.synthetic.main.activity_my_payment.tabLayoutChat
import kotlinx.android.synthetic.main.activity_my_payment.viewPagerChat
import java.math.BigDecimal

class myPaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_payment)

        topupBackBtn.bringToFront()

        topupBackBtn.setOnClickListener {
            finish()
        }
        viewPagerChat.adapter= fragmentAdapter3(supportFragmentManager)
        tabLayoutChat.setupWithViewPager(viewPagerChat)
    }

}