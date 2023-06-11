package com.example.sogating_final.auth

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bokchi.sogating_final.utils.FirebaseRef
import com.example.sogating_final.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class JoinActivity : AppCompatActivity() {

    private val TAG = "JoinActivity"

    private lateinit var auth: FirebaseAuth


    private var gender = ""
    private var nickname = ""
    private var city = ""
    private var age = ""
    private var uid = ""


    lateinit var profileImage : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        // Initialize Firebase Auth
        auth = Firebase.auth

        profileImage=findViewById(R.id.imageArea)

        val getAction = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback { uri->
                profileImage.setImageURI(uri)
            }
        )
        profileImage.setOnClickListener {
            getAction.launch("image/*")
        }

        val joinBtn = findViewById<Button>(R.id.joinBtn)
        joinBtn.setOnClickListener {
            val email = findViewById<TextInputEditText>(R.id.emailArea)
            val pwd = findViewById<TextInputEditText>(R.id.pwdArea)
            val pwdchk = findViewById<TextInputEditText>(R.id.pwdchkArea)

            gender=findViewById<TextInputEditText>(R.id.genderArea).text.toString()
            city=findViewById<TextInputEditText>(R.id.cityArea).text.toString()
            age=findViewById<TextInputEditText>(R.id.ageArea).text.toString()
            nickname=findViewById<TextInputEditText>(R.id.nicknameArea).text.toString()


            val emailCheck = email.text.toString()
            if(emailCheck.isEmpty()) {
                Toast.makeText(this, "비어있음",Toast.LENGTH_LONG).show()
                val intent = Intent(this, IntroActivity::class.java)
                startActivity(intent)
            }else{

//                if(emailCheck.contains("@knu.ac.kr"))
//                {
//                    //Toast.makeText(this, "knu메일O",Toast.LENGTH_LONG).show()
//                }
//                else{
//                    Toast.makeText(this, "knu메일X",Toast.LENGTH_LONG).show()
//                    val intent = Intent(this, IntroActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                }
            }

            val pwdCheck = pwd.text.toString()
            val pwdchkCheck = pwdchk.text.toString()

            if(pwdCheck.isEmpty()||pwdchkCheck.isEmpty()){
                Toast.makeText(this, "비밀번호 비어있음",Toast.LENGTH_LONG).show()
                val intent = Intent(this, IntroActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                if (pwdCheck == pwdchkCheck){
                }
                else{
                    Toast.makeText(this, "비밀번호 다름",Toast.LENGTH_LONG).show()
                    val intent = Intent(this, IntroActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            if(gender.isEmpty()||city.isEmpty()||age.isEmpty()||nickname.isEmpty())
            {
                Toast.makeText(this, "다 채워주세요!",Toast.LENGTH_LONG).show()
                val intent = Intent(this, IntroActivity::class.java)
                startActivity(intent)
                finish()
            }

            auth.createUserWithEmailAndPassword(email.text.toString(), pwd.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")

                        val user = auth.currentUser
                        uid = user?.uid.toString()

                        //Token
                        FirebaseMessaging.getInstance().token.addOnCompleteListener(
                            OnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                                    return@OnCompleteListener
                                }

                                // Get new FCM registration token
                                val token = task.result

                                // Log and toast
                                Log.e(TAG,token)

                                val userModel = UserDataModel(
                                    uid,
                                    nickname,
                                    age,
                                    gender,
                                    city,
                                    token
                                )


                                FirebaseRef.userInfoRef.child(uid).setValue(userModel)

                                uploadImage(uid)
                            })

                        auth.currentUser?.sendEmailVerification()
                            ?.addOnCompleteListener{sendTask->
                                if(sendTask.isSuccessful){
                                    Toast.makeText(this, "인증 이메일 발송",Toast.LENGTH_LONG).show()

                                    val intent = Intent(this, IntroActivity::class.java)
                                    startActivity(intent)
                                }
                                else{
                                    Toast.makeText(this, "인증 이메일 발송 실패",Toast.LENGTH_LONG).show()
                                }
                            }

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this, "이메일 중복",Toast.LENGTH_LONG).show()
                        val intent = Intent(this, IntroActivity::class.java)
                        startActivity(intent)
                        finish()

                    }
                }

        }



    }
    private fun uploadImage(uid : String){

        val storage = Firebase.storage
        val storageRef = storage.reference.child(uid + ".png")


        // Get the data from an ImageView as bytes
        profileImage.isDrawingCacheEnabled = true
        profileImage.buildDrawingCache()
        val bitmap = (profileImage.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = storageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
        }

    }
}