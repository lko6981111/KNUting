package com.example.sogating_final.message

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bokchi.sogating_final.utils.FirebaseRef
import com.example.sogating_final.R
import com.example.sogating_final.auth.UserDataModel
import com.example.sogating_final.message.fcm.NotiModel
import com.example.sogating_final.message.fcm.PushNotification
import com.example.sogating_final.message.fcm.RetrofitInstance
import com.example.sogating_final.utils.FirebaseAuthUtils
import com.example.sogating_final.utils.MyInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//내가 좋아요한 사람들이 나를 좋아요 한 리스트
class MyLikeListActivity : AppCompatActivity() {

    private val TAG = "MyLikeListActivity"
    private val uid = FirebaseAuthUtils.getUid()

    private val likeUserListUid = mutableListOf<String>()
    private val likeUserList = mutableListOf<UserDataModel>()

    lateinit var listviewAdapter : ListViewAdapter
    lateinit var getterUid : String
    lateinit var getterToken : String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_like_list)


        val userListView = findViewById<ListView>(R.id.userListView)

        listviewAdapter = ListViewAdapter(this, likeUserList)
        userListView.adapter = listviewAdapter


        // 내가 좋아요한 사람들
        getMyLikeList()

        // 제가 하고 싶은것은
        // 전체 유저 중에서, 내가 좋아요한 사람들 가져와서
        // 이 사람이 나와 매칭이 되어있는지 확인하는 것!!

//        userListView.setOnItemClickListener { parent, view, position, id ->
//
////            Log.d(TAG, likeUserList[position].uid.toString())
//            checkMatching(likeUserList[position].uid.toString())
//
//            val notiModel = NotiModel("a", "b")
//
//            val pushModel = PushNotification(notiModel, likeUserList[position].token.toString())
//
//            testPush(pushModel)
//
//        }

        userListView.setOnItemLongClickListener { parent, view, position, id ->

            checkMatching(likeUserList[position].uid.toString())
            getterUid = likeUserList[position].uid.toString()
            getterToken = likeUserList[position].token.toString()

            return@setOnItemLongClickListener(true)
        }

        // 저기 내가 좋아요한 유저를 클릭하면은(Long Click)
        // 만약에 서로 좋아요한 사람이 아니면은, 메세지 못 보내도록 함
        // 메세지 보내기 창이 떠서 메세지를 보낼 수 있게 하고(당연히 받는 사람 부분 만들어줘야겠죠?)
        // 메세지 보내고 상대방에서 PUSH 알람 띄워주고



    }

    private fun checkMatching(otherUid : String){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                Log.d(TAG, otherUid)
                Log.e(TAG, dataSnapshot.toString())

                if(dataSnapshot.children.count() == 0) {

                    Toast.makeText(this@MyLikeListActivity, "상대방이 좋아요한 사람이 아무도 없어요", Toast.LENGTH_LONG).show()

                } else {

                    for (dataModel in dataSnapshot.children) {

                        val likeUserKey = dataModel.key.toString()
                        if(likeUserKey.equals(uid)) {

                            Toast.makeText(this@MyLikeListActivity, "매칭이 되었습니다.", Toast.LENGTH_LONG).show()
                            showDialog()

                            // Dialog

                        } else {
//                            Toast.makeText(this@MyLikeListActivity, "매칭이 되지 않았습니다.", Toast.LENGTH_LONG).show()
                        }

                    }

                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userLikeRef.child(otherUid).addValueEventListener(postListener)

    }

    private fun getMyLikeList(){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataModel in dataSnapshot.children) {
                    // 내가 좋아요 한 사람들의 uid가  likeUserList에 들어있음
                    likeUserListUid.add(dataModel.key.toString())
                }
                getUserDataList()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userLikeRef.child(uid).addValueEventListener(postListener)

    }

    private fun getUserDataList(){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataModel in dataSnapshot.children) {

                    val user = dataModel.getValue(UserDataModel::class.java)

                    // 전체 유저중에 내가 좋아요한 사람들의 정보만 add함
                    if(likeUserListUid.contains(user?.uid)) {

                        likeUserList.add(user!!)
                    }

                }
                listviewAdapter.notifyDataSetChanged()
                Log.d(TAG, likeUserList.toString())

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userInfoRef.addValueEventListener(postListener)

    }

    //PUSH
    private fun testPush(notification : PushNotification) = CoroutineScope(Dispatchers.IO).launch {

        RetrofitInstance.api.postNotification(notification)

    }


    // Dialog
    private fun showDialog(){

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("메세지 보내기")

        val mAlertDialog = mBuilder.show()

        val btn = mAlertDialog.findViewById<Button>(R.id.sendBtnArea)
        val textArea = mAlertDialog.findViewById<EditText>(R.id.sendTextArea)
        btn?.setOnClickListener {

            val msgText = textArea!!.text.toString()

            val mgsModel = MsgModel(MyInfo.myNickname, msgText)

            FirebaseRef.userMsgRef.child(getterUid).push().setValue(mgsModel)

            val notiModel = NotiModel(MyInfo.myNickname, msgText)

            val pushModel = PushNotification(notiModel, getterToken)

            testPush(pushModel)

            mAlertDialog.dismiss()
        }
        // message
        // 받는 사람 uid
        // Message
        // 누가 보냈는지
    }
}