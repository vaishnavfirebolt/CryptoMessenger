package com.example.cryptomessenger.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cryptomessenger.R
import com.example.cryptomessenger.utils.Utils.Companion.pwdText
import kotlinx.android.synthetic.main.activity_a_e_s.*
import kotlinx.android.synthetic.main.activity_rsa.input_text
import kotlinx.android.synthetic.main.activity_rsa.output_text
import kotlinx.android.synthetic.main.activity_rsa.send
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class AES : AppCompatActivity() {

    private var outputstring = ""
    var pwdtext = pwdText
    private var inptext: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a_e_s)

        speech.setOnClickListener { getSpeechInput() }

        enc.setOnClickListener {
            try {
                inptext = input_text.text.toString()
                outputstring = encrypt(inptext!!, pwdtext)!!
                output_text.text = outputstring
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }


        dec.setOnClickListener {
            try {
                inptext = input_text.text.toString()
                outputstring =
                    decrypt(inptext!!, pwdtext)!!
                output_text.text = outputstring
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        hash_button.setOnClickListener {
            try {
                input_text.setText(" ")
                output_text.text = ""
                input_text.hint = "enter message"
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        reset.setOnClickListener {
            try {
                val intent = Intent(this@AES, ResetPassword::class.java)
                startActivity(intent)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        send.setOnClickListener {
            if (outputstring.isNotEmpty()) {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, outputstring)
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
            } else {
                Toast.makeText(this@AES, "empty output", Toast.LENGTH_SHORT).show()
            }
        }


    }

    @Throws(Exception::class)
    private fun encrypt(data: String, password_text: String): String? {
        val key = generateKey(password_text)
        val c =
            Cipher.getInstance("AES/ECB/PKCS5Padding")
        c.init(Cipher.ENCRYPT_MODE, key)
        val encVal = c.doFinal(data.toByteArray(charset("UTF-8")))
        return Base64.encodeToString(encVal, Base64.DEFAULT)
    }

    @Throws(Exception::class)
    private fun decrypt(data: String, password_text: String): String? {
        val key = generateKey(password_text)
        val c = Cipher.getInstance("AES/ECB/PKCS5Padding")
        c.init(Cipher.DECRYPT_MODE, key)
        val decodedValue = Base64.decode(
            data,
            Base64.DEFAULT
        )
        val decValue = c.doFinal(decodedValue)
        return String(decValue, Charsets.UTF_8)
    }

    @Throws(Exception::class)
    private fun generateKey(password: String): SecretKeySpec {
        val digest =
            MessageDigest.getInstance("SHA-256")
        val bytes = password.toByteArray(charset("UTF-8"))
        digest.update(bytes, 0, bytes.size)
        val key =
            digest.digest()
        return SecretKeySpec(key, "AES")
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