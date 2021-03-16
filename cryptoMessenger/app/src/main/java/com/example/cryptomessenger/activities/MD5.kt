package com.example.cryptomessenger.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cryptomessenger.R
import kotlinx.android.synthetic.main.activity_m_d5.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import kotlin.experimental.and

class MD5 : AppCompatActivity() {

    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_m_d5)

        speech_Img.setOnClickListener { getSpeechInput() }
        hash_button.setOnClickListener {
            password = input_text.text.toString()
            if (password.isNotEmpty()) output_text.text = hashPassword(password)
            else Toast.makeText(
                this@MD5,
                "Empty output",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun hashPassword(password: String): String? {
        val passwordToHash = password
        return try {
            val md = MessageDigest.getInstance("MD5")
            md.update(passwordToHash.toByteArray())
            val bytes = md.digest()
            val sb = StringBuilder()
            for (i in bytes.indices) {
                sb.append(
                    ((bytes[i] and 0xff.toByte()) + 0x100).toString(32).substring(1)
                )
            }
            sb.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            null
        }
    }

    private fun getSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, 1001)
        } else {
            Toast.makeText(this, "your device does not support this feature", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1001 -> if (resultCode == Activity.RESULT_OK && data != null) {
                val res =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                input_text.setText(res!![0])
            }
        }
    }
}