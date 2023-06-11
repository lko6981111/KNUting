package com.example.sogating_final.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sogating_final.MainActivity
import com.example.sogating_final.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        val loginBtn = findViewById<Button>(R.id.loginBtn)
        loginBtn.setOnClickListener {

            val email = findViewById<TextInputEditText>(R.id.emailArea)
            val pwd = findViewById<TextInputEditText>(R.id.pwdArea)


            auth.signInWithEmailAndPassword(email.text.toString(), pwd.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)

                        if(checkAuth()){
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }else{
                            Toast.makeText(baseContext,"인증X",Toast.LENGTH_SHORT).show()
                        }

                    } else {

                        Toast.makeText(this,"실패",Toast.LENGTH_LONG).show()

                    }
                }
        }

    }

    private fun checkAuth(): Boolean {
        var email: String? = null
        val currentUser = auth.currentUser

        return currentUser?.let {
            email = currentUser.email
            if (currentUser.isEmailVerified) {
                true
            } else {
                false
            }
        }?:let{
            false
        }
    }
}
