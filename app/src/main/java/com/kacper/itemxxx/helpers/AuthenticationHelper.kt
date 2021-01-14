package com.kacper.itemxxx.helpers

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

object AuthenticationHelper {
    var auth: FirebaseAuth? = null
    var refUsers: DatabaseReference? = null

    fun getUserAuth() {
        auth = FirebaseAuth.getInstance()
    }

}