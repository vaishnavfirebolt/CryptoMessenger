package com.example.cryptomessenger.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cryptomessenger.R
import kotlinx.android.synthetic.main.activity_des.*
import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import java.security.spec.KeySpec
import java.util.*
import javax.crypto.*
import javax.crypto.spec.DESKeySpec

class DES : AppCompatActivity() {

    private val UNICODE_FORMAT = "UTF8"
    private val DES_ENCRYPTION_SCHEME = "DES"
    private var myKeySpec: KeySpec? = null
    private var mySecretKeyFactory: SecretKeyFactory? = null
    private var cipher: Cipher? = null
    private lateinit var KeyAsBytes: ByteArray
    private var myEncryptionKey: String? = null
    private var getMyEncryptionScheme: String? = null
    private var key: SecretKey? = null
    private var myEncKey = "Vaishnav ki Key"

    private var ans = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_des)

        speech_Img.setOnClickListener { getSpeechInput() }

        myEncryptionKey = myEncKey
        getMyEncryptionScheme = DES_ENCRYPTION_SCHEME
        try {
            KeyAsBytes = myEncryptionKey!!.toByteArray(charset(UNICODE_FORMAT))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        try {
            myKeySpec = DESKeySpec(KeyAsBytes)
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        }
        try {
            mySecretKeyFactory = SecretKeyFactory.getInstance(getMyEncryptionScheme)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        try {
            cipher = Cipher.getInstance(getMyEncryptionScheme)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        }
        try {
            key = mySecretKeyFactory!!.generateSecret(myKeySpec)
        } catch (e: InvalidKeySpecException) {
            e.printStackTrace()
        }

        encryptDES.setOnClickListener {
            val temp = input_text.text.toString()
            ans = encrypt(temp)!!
            Log.d("NIKHIL", "encrypt key:$ans")
            output_text.text = ans
        }

        decryptDES.setOnClickListener {
            val temp = input_text.text.toString()
            ans = decrypt(temp)!!
            output_text.text = ans
        }

        hash_button.setOnClickListener {
            if (ans.isNotEmpty()) {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, ans)
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
            } else {
                Toast.makeText(this@DES, "empty output", Toast.LENGTH_SHORT).show()
            }
        }
        hash_button.setOnClickListener {
            try {
                input_text.setText(" ")
                output_text.text = ""
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }

    private fun encrypt(unencryptedString: String): String? {
        var encryptedString: String? = null
        try {
            cipher?.init(Cipher.ENCRYPT_MODE, key)
            val plainText =
                unencryptedString.toByteArray(charset(UNICODE_FORMAT))
            val encryptedText: ByteArray = cipher!!.doFinal(plainText)
            encryptedString =
                Base64.encodeToString(encryptedText, Base64.DEFAULT)
        } catch (e: InvalidKeyException) {
        } catch (e: UnsupportedEncodingException) {
        } catch (e: IllegalBlockSizeException) {
        } catch (e: BadPaddingException) {
        }
        return encryptedString
    }

    private fun decrypt(encryptedString: String?): String? {
        var decryptedText: String? = null
        try {
            cipher!!.init(Cipher.DECRYPT_MODE, key)
            val encryptedText =
                Base64.decode(encryptedString, Base64.DEFAULT)
            val plainText: ByteArray = cipher!!.doFinal(encryptedText)
            decryptedText = bytes2String(plainText)
        } catch (e: InvalidKeyException) {
        } catch (e: IllegalBlockSizeException) {
        } catch (e: BadPaddingException) {
        }
        return decryptedText
    }

    private fun bytes2String(bytes: ByteArray): String? {
        val stringBuffer = StringBuilder()
        for (i in bytes.indices) {
            stringBuffer.append(bytes[i].toChar())
        }
        return stringBuffer.toString()
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