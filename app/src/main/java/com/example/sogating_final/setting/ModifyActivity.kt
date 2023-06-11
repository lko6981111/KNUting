package com.example.sogating_final.setting

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bokchi.sogating_final.utils.FirebaseRef
import com.example.sogating_final.R
import com.example.sogating_final.utils.FirebaseAuthUtils
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class ModifyActivity : AppCompatActivity() {


    private var nickname = ""
    private var city = ""
    private var age = ""

    lateinit var profileImage : ImageView

    private val uid = FirebaseAuthUtils.getUid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify)

        profileImage=findViewById(R.id.modifyImage)

        val getAction = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback { uri->
                profileImage.setImageURI(uri)
            }
        )
        profileImage.setOnClickListener {
            getAction.launch("image/*")
        }

        val modifyBtn = findViewById<Button>(R.id.modifyBtn)

        modifyBtn.setOnClickListener {

            city=findViewById<TextInputEditText>(R.id.modifyCity).text.toString()
            age=findViewById<TextInputEditText>(R.id.modifyArea).text.toString()
            nickname=findViewById<TextInputEditText>(R.id.modifyName).text.toString()

            if(city.isEmpty()||age.isEmpty()||nickname.isEmpty())
            {
                Toast.makeText(this, "다 채워주세요!", Toast.LENGTH_LONG).show()
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
                finish()
            }

            var hashmap = HashMap<String, Any>()
            hashmap.put("city",city)
            hashmap.put("age",age)
            hashmap.put("nickname",nickname)
            FirebaseRef.userInfoRef.child(uid).updateChildren(hashmap)
            uploadImage(uid)
            Toast.makeText(this, "정보수정 완료", Toast.LENGTH_LONG).show()
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)

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

