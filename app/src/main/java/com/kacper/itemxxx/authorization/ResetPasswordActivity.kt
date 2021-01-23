package com.kacper.itemxxx.authorization

import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.kacper.itemxxx.R
import com.kacper.itemxxx.helpers.AuthenticationHelper.auth
import com.kacper.itemxxx.helpers.AuthenticationHelper.getUserAuth
import com.kacper.itemxxx.helpers.toastCustom
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        btnBack.setOnClickListener {
            finish()
        }
        getUserAuth()
        btnResetPassword.setOnClickListener {

            val email = edtResetEmail.text.toString().trim()

            if (TextUtils.isEmpty(email)) {
                toastCustom("Enter your email!")
            } else {
                auth?.sendPasswordResetEmail(email)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            toastCustom("Check your email!")
                        } else {
                            toastCustom("Email Failed")
                        }
                    }
            }
        }
    }
}