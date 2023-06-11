package com.example.sogating_final.setting

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sogating_final.R
import com.example.sogating_final.auth.IntroActivity
import com.example.sogating_final.message.MyLikeListActivity
import com.example.sogating_final.message.MyMsgActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val mybtn = findViewById<Button>(R.id.myPageBtn)
        mybtn.setOnClickListener {

            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)

        }

        val myLikeBtn = findViewById<Button>(R.id.myLikeList)
        myLikeBtn.setOnClickListener {

            val intent = Intent(this, MyLikeListActivity::class.java)
            startActivity(intent)

        }

        val myMsg = findViewById<Button>(R.id.myMsg)
        myMsg.setOnClickListener {

            val intent = Intent(this, MyMsgActivity::class.java)
            startActivity(intent)

        }

        val goodbye = findViewById<Button>(R.id.goodbye)
        goodbye.setOnClickListener{

            val user = Firebase.auth.currentUser!!

            user.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "회원탈퇴", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, IntroActivity::class.java)
                        startActivity(intent)
                        
                    }
                }

        }


        val changepwd = findViewById<Button>(R.id.changepwd)

        changepwd.setOnClickListener{
            val user = Firebase.auth.currentUser!!

            val email = user.email.toString()

            Firebase.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "이메일을 보냈습니다!ㅣ", Toast.LENGTH_LONG).show()
                    }
                }


        }

        val logoutBtn = findViewById<Button>(R.id.logoutBtn)
        logoutBtn.setOnClickListener {

            val auth = Firebase.auth
            auth.signOut()

            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)

        }

        val modifyBtn = findViewById<Button>(R.id.modify)
        modifyBtn.setOnClickListener {

            val intent = Intent(this, ModifyActivity::class.java)
            startActivity(intent)

        }


    }
}