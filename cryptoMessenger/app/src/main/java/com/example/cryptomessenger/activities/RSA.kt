package com.example.cryptomessenger.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cryptomessenger.R
import kotlinx.android.synthetic.main.activity_rsa.*
import java.security.Key
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.spec.KeySpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher

class RSA : AppCompatActivity() {
    private lateinit var temp: String
    private lateinit var toSend: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rsa)

        speech_img.setOnClickListener { getSpeechInput() }

        val kp: KeyPair? = getKeyPair()
        val publicKey = kp!!.public
        val publicKeyBytes = publicKey.encoded
        val publicKeyBytesBase64 =
            String(Base64.encode(publicKeyBytes, Base64.DEFAULT))
        val privateKey = kp.private
        val privateKeyBytes = privateKey.encoded
        val privateKeyBytesBase64 =
            String(Base64.encode(privateKeyBytes, Base64.DEFAULT))

        encrypt.setOnClickListener {
            temp = input_text.text.toString()
            val encrypted: String? =
                encryptRSAToString(temp, publicKeyBytesBase64)

            output_text.text = encrypted
            if (encrypted != null) {
                toSend = encrypted
            }
        }

        decrypt.setOnClickListener {
            temp = input_text.text.toString()
            val decrypted: String? =
                decryptRSAToString(temp, privateKeyBytesBase64)
            output_text.text = decrypted
            if (decrypted != null) {
                toSend = decrypted
            }
        }

        clear_button.setOnClickListener {
            try {
                input_text.setText(" ")
                output_text.text = ""
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        send.setOnClickListener {
            if (toSend.isNotEmpty()) {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, toSend)
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
            } else {
                Toast.makeText(this@RSA, "empty output", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun encryptRSAToString(
        clearText: String,
        publicKey: String
    ): String? {
        var encryptedBase64 = ""
        try {
            val keyFac = KeyFactory.getInstance("RSA")
            val keySpec: KeySpec =
                X509EncodedKeySpec(
                    Base64.decode(publicKey.trim { it <= ' ' }
                        .toByteArray(), Base64.DEFAULT
                    )
                )
            val key: Key = keyFac.generatePublic(keySpec)

            // get an RSA cipher object and print the provider
            val cipher =
                Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING")
            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val encryptedBytes =
                cipher.doFinal(clearText.toByteArray(charset("UTF-8")))
            encryptedBase64 =
                String(Base64.encode(encryptedBytes, Base64.DEFAULT))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return encryptedBase64.replace(
            "([\\r\\n])".toRegex(),
            ""
        )
    }

    private fun decryptRSAToString(
        encryptedBase64: String?,
        privateKey: String
    ): String? {
        var decryptedString: String? = ""
        try {
            val keyFac = KeyFactory.getInstance("RSA")
            val keySpec: KeySpec =
                PKCS8EncodedKeySpec(
                    Base64.decode(privateKey.trim { it <= ' ' }
                        .toByteArray(), Base64.DEFAULT
                    )
                )
            val key: Key = keyFac.generatePrivate(keySpec)

            val cipher =
                Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING")

            cipher.init(Cipher.DECRYPT_MODE, key)
            val encryptedBytes =
                Base64.decode(encryptedBase64, Base64.DEFAULT)
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            decryptedString = String(decryptedBytes)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return decryptedString
    }

    private fun getKeyPair(): KeyPair? {
        var keyPair: KeyPair? = null
        try {
            val kpg =
                KeyPairGenerator.getInstance("RSA")
            kpg.initialize(2048)
            keyPair = kpg.generateKeyPair()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return keyPair
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