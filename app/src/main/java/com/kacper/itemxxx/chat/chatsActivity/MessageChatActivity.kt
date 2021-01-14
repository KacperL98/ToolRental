package com.kacper.itemxxx.chat.chatsActivity

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.kacper.itemxxx.R
import com.kacper.itemxxx.chat.adapters.ChatsAdapter
import com.kacper.itemxxx.chat.notifications.APIService
import com.kacper.itemxxx.chat.model.Chat
import com.kacper.itemxxx.chat.model.Users
import com.kacper.itemxxx.chat.notifications.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_message_chat.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessageChatActivity : AppCompatActivity() {

    var userIdVisit: String = ""
    var firebaseUser: FirebaseUser? = null
    var chatsAdapter: ChatsAdapter? = null
    var mChatList: List<Chat>? = null
    lateinit var recyclerViewChats: RecyclerView
    var reference: DatabaseReference? = null
    var notify = false
    var apiService: APIService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)
        val toolbar:androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_message_chat)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        apiService = Client.Client.getClient("https://fcm.googleapis.com/")!!.create(
            APIService::class.java)
        intent = intent
        userIdVisit = intent.getStringExtra("visit_id")!!
        firebaseUser = FirebaseAuth.getInstance().currentUser
        recyclerViewChats = findViewById(R.id.recycler_view_chats)
        recyclerViewChats.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recyclerViewChats.layoutManager = linearLayoutManager

        reference = FirebaseDatabase.getInstance().reference
            .child("Users").child(userIdVisit)
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(pO: DatabaseError) {
            }
            override fun onDataChange(pO: DataSnapshot) {
                val user: Users? = pO.getValue(Users::class.java)
                username_mchat.text = user!!.getUserName()
                Picasso.get().load(user.getProfile()).into(profile_image_mchat)
                retrieveMessages(firebaseUser!!.uid, userIdVisit, user.getProfile())
            }
        })
        send_message_btn.setOnClickListener {
            notify = true
            val message = text_message.text.toString()
            if (message == "") {
                Toast.makeText(
                    this@MessageChatActivity,
                    "Please write a message",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                sendMessageToUser(firebaseUser!!.uid, userIdVisit, message)
            }
            text_message.setText("")
        }
        attact_image_file_btn.setOnClickListener {
            notify = true
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Pick Image"), 438)
        }
        seenMessage(userIdVisit)
    }
    private fun sendMessageToUser(senderId: String, receiverId: String, message: String) {
        val reference = FirebaseDatabase.getInstance().reference
        val messageKey = reference.push().key
        val messageHasMap = HashMap<String, Any?>()

        messageHasMap["sender"] = senderId
        messageHasMap["message"] = message
        messageHasMap["receiver"] = receiverId
        messageHasMap["isseen"] = false
        messageHasMap["url"] = ""
        messageHasMap["messageId"] = messageKey
        reference.child("Chats").child(messageKey!!).setValue(messageHasMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val chatListReference = FirebaseDatabase.getInstance().reference
                        .child("ChatList")
                        .child(firebaseUser!!.uid)
                        .child(userIdVisit)

                    chatListReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(pO: DatabaseError) {

                        }

                        override fun onDataChange(pO: DataSnapshot) {
                            if (!pO.exists()){
                                chatListReference.child("id").setValue(userIdVisit)

                            }
                            val chatListReceiverRef = FirebaseDatabase.getInstance().reference
                                .child("ChatList")
                                .child(userIdVisit)
                                .child(firebaseUser!!.uid)
                            chatListReceiverRef.child("id").setValue(firebaseUser!!.uid)
                        }
                    })
                }
                val usersReference = FirebaseDatabase.getInstance().reference
                    .child("Users").child(firebaseUser!!.uid)
                usersReference.addValueEventListener(object: ValueEventListener {

                    override fun onDataChange(pO: DataSnapshot) {

                        val user = pO.getValue(Users::class.java)
                        if (notify){
                            sendNotification(receiverId, user!!.getUserName(), message){
                                notify = false
                            }
                        }
                    }
                    override fun onCancelled(pO: DatabaseError) {
                    }
                })
            }
    }
    private fun sendNotification(
        receiverId: String,
        userName: String?,
        message: String,
        function: () -> Unit
    ) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val query = ref.orderByKey().equalTo(receiverId)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(pO: DataSnapshot) {

                for (dataSnapshot in pO.children){
                    val token: Token? = dataSnapshot.getValue(Token::class.java)
                    val data = Data(firebaseUser!!.uid,
                        R.mipmap.ic_launcher,
                        "$userName: $message",
                        "New Message", userIdVisit)

                    val sender = Sender(data, token!!.getToken().toString())

                    apiService!!.sendNotification(sender)
                        .enqueue(object : Callback<MyResponse> {
                            override fun onResponse(
                                call: Call<MyResponse>,
                                response: Response<MyResponse>
                            ) {
                                if (response.code() == 200)
                                {
                                    if (response.body()!!.success != 1)
                                    {
                                        Toast.makeText(this@MessageChatActivity, "Send.Message!", Toast.LENGTH_LONG).show()

                                    }
                                }
                            }

                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {

                            }
                        })
                }
            }

            override fun onCancelled(pO: DatabaseError) {

            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 438 && resultCode == RESULT_OK && data!= null && data.data != null) {
            val progressBar = ProgressDialog(this)
            progressBar.setMessage("Image is uploading, please wait ...")
            progressBar.show()

            val fileUri = data.data
            val storageReference = FirebaseStorage.getInstance().reference.child("Chat Images")

            val ref = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filePath = storageReference.child("$messageId.jpg")


            val uploadTask: StorageTask<*>
            uploadTask = filePath.putFile(fileUri!!)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }

                return@Continuation filePath.downloadUrl

            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    val messageHasMap = HashMap<String, Any?>()
                    messageHasMap["sender"] = firebaseUser!!.uid
                    messageHasMap["message"] = "sent you an image."
                    messageHasMap["receiver"] = userIdVisit
                    messageHasMap["isseen"] = false
                    messageHasMap["url"] = url
                    messageHasMap["messageId"] = messageId

                    ref.child("Chats").child(messageId!!).setValue(messageHasMap)

                        .addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                progressBar.dismiss()
                                val reference = FirebaseDatabase.getInstance().reference
                                    .child("Users").child(firebaseUser!!.uid)
                                reference.addValueEventListener(object: ValueEventListener {

                                    override fun onDataChange(pO: DataSnapshot) {

                                        val user = pO.getValue(Users::class.java)
                                        if (notify){
                                            sendNotification(userIdVisit, user!!.getUserName(), "sent you an image."){
                                                notify = false
                                            }
                                        }
                                    }

                                    override fun onCancelled(pO: DatabaseError) {

                                    }
                                })
                            }
                        }
                }
            }
        }
    }
    private fun retrieveMessages(senderId: String, receiverId: String, receiverImageUrl: String?) {

        mChatList = ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(pO: DataSnapshot) {
                (mChatList as ArrayList<Chat>).clear()
                for (snapshot in pO.children){

                    val chat = snapshot.getValue(Chat::class.java)

                    if (chat!!.receiver.equals(senderId)&& chat.sender.equals(receiverId)
                        || chat.receiver.equals(receiverId)&& chat.sender.equals(senderId))
                    {
                        (mChatList as ArrayList<Chat>).add(chat)
                    }
                    chatsAdapter = ChatsAdapter(receiverImageUrl ?: "")
                    Log.d("TAG","KURWA ${mChatList?.map { it }}")
                    chatsAdapter?.submitList(mChatList)
                    recyclerViewChats.adapter = chatsAdapter

                }
            }
            override fun onCancelled(pO: DatabaseError) {
            }
        })
    }
    var seenListener: ValueEventListener? = null

    private fun seenMessage(userId: String){
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")
        seenListener = reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(pO: DataSnapshot) {
                for (dataSnapshot in pO.children){
                    val chat = dataSnapshot.getValue(Chat::class.java)
                    if (chat!!.receiver.equals(firebaseUser!!.uid) && chat.sender.equals(userId) ){
                        val hashMap = HashMap<String, Any>()
                        hashMap["isseen"] = true
                        dataSnapshot.ref.updateChildren(hashMap)
                    }
                }
            }
            override fun onCancelled(pO: DatabaseError) {
            }
        })
    }
    override fun onPause() {
        super.onPause()
        reference!!.removeEventListener(seenListener!!)
    }
}