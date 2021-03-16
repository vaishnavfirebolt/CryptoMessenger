package com.example.cryptomessenger.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cryptomessenger.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        aes_card_view.setOnClickListener { openActivity(AES()) }
        des_card_view.setOnClickListener { openActivity(DES()) }
        rsa_card_view.setOnClickListener { openActivity(RSA()) }
        md5_card_view.setOnClickListener { openActivity(MD5()) }

    }

    private fun openActivity(activity: Activity) {
        val intent = Intent(this, activity::class.java)
        startActivity(intent)
    }
}