package com.kacper.itemxxx.mainPanel

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.kacper.itemxxx.R
import com.kacper.itemxxx.authorization.ChangePasswordActivity
import com.kacper.itemxxx.authorization.LoginActivity
import com.kacper.itemxxx.chat.chatsActivity.ChatActivity
import com.kacper.itemxxx.scanner.mainactivity.MainActivity
import kotlinx.android.synthetic.main.activity_panel.*

class PanelActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panel)


        btn_change.setOnClickListener {
            val intent = Intent(this@PanelActivity, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        btn_logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            startActivity(Intent(this@PanelActivity, LoginActivity::class.java))
            finish()
        }

        btn_scanner.setOnClickListener {

            val active = Intent(this@PanelActivity, MainActivity::class.java)
            startActivity(active)
        }

        btn_chat.setOnClickListener {

            val chats = Intent(this@PanelActivity, ChatActivity::class.java)
            startActivity(chats)
        }
    }

}
