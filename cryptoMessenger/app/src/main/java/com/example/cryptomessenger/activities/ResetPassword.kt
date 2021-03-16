package com.example.cryptomessenger.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cryptomessenger.R
import com.example.cryptomessenger.utils.Utils.Companion.pwdText
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPassword : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        save.setOnClickListener(View.OnClickListener {
            val oldPassword = old.text.toString()
            val newPassword = newp.text.toString()
            val cnfPassword = cnfpwd.text.toString()
            if (pwdText == oldPassword) {
                if (newPassword == cnfPassword) {
                    pwdText = newPassword
                    println("updated successfully")
                    Toast.makeText(
                        this@ResetPassword,
                        "Updated Successfully!!",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@ResetPassword,
                        " password does not match",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@ResetPassword,
                    " password does not match",
                    Toast.LENGTH_SHORT
                ).show()

                println(oldPassword)
            }
        })
    }
}