package com.kacper.itemxxx.chat.chatsActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kacper.itemxxx.R
import com.kacper.itemxxx.chat.model.Users
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_visit_user_profile.*

class VisitUserProfileActivity : AppCompatActivity() {
    private var userVisitId: String = ""
    var user: Users? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_user_profile)
        
        userVisitId = intent.getStringExtra("visit_id")!!
        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(userVisitId)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(pO: DataSnapshot) {
                if (pO.exists()) {
                    user = pO.getValue(Users::class.java)
                    username_display.text = user!!.username
                    Picasso.get().load(user!!.profile).placeholder(R.drawable.profile).into(profile_display1)
                    Picasso.get().load(user!!.cover).placeholder(R.drawable.cover).into(cover_display1)
                }
            }
            override fun onCancelled(pO: DatabaseError) {
            }
        })
        send_msg_btn.setOnClickListener {
            val intent = Intent(this@VisitUserProfileActivity, MessageChatActivity::class.java)
            intent.putExtra("visit_id", user!!.uid)
            startActivity(intent)
        }
    }
}