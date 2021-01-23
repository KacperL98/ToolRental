package com.kacper.itemxxx.authorization

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kacper.itemxxx.R
import com.kacper.itemxxx.helpers.AuthenticationHelper.auth
import com.kacper.itemxxx.helpers.AuthenticationHelper.getUserAuth
import com.kacper.itemxxx.mainPanel.PanelActivity
import com.kacper.itemxxx.helpers.toastCustom
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_forgotPass.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))

        }

        tv_register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        getUserAuth()
        btn_login.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val email: String = et_login_email.text.toString()
        val password: String = et_login_password.text.toString()

        when {
            email.isEmpty() || password.isEmpty() -> toastCustom("Please fill each form")
            else -> {
                auth?.signInWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(this@LoginActivity, PanelActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()

                        } else {
                            toastCustom("Error:" + task.exception!!.message.toString())
                        }
                    }
            }
        }
    }
    companion object {
        fun prepareIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }
}
