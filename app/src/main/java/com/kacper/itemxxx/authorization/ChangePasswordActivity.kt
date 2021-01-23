package com.kacper.itemxxx.authorization

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.kacper.itemxxx.R
import com.kacper.itemxxx.helpers.AuthenticationHelper.auth
import com.kacper.itemxxx.helpers.AuthenticationHelper.getUserAuth
import com.kacper.itemxxx.mainPanel.PanelActivity
import com.kacper.itemxxx.helpers.toastCustom
import kotlinx.android.synthetic.main.activity_change_password.*

class ChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        getUserAuth()
        btn_change_password.setOnClickListener {
            changePassword()
        }
    }

    private fun changePassword() {
            if (newPassword.text.toString() == confirmPassword.text.toString()) {
                val user = auth?.currentUser
                if (user != null && user.email != null) {

                    val credential = EmailAuthProvider
                        .getCredential(user.email!!, currentPassword.text.toString())

                    user.reauthenticate(credential)
                    user.updatePassword(newPassword.text.toString())
                        .addOnCompleteListener { task ->

                            if (task.isSuccessful) {
                                toastCustom("Password changed successfully")
                                auth?.signOut()
                                startActivity(Intent(this, PanelActivity::class.java))
                                finish()
                            }
                        }

                } else {
                    toastCustom("Authentication Password failed")
                }
            }

        }
    }



