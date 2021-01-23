package com.kacper.itemxxx.chat.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Users (
    val uid: String = "",
    val username: String = "",
    val profile: String = "",
    val cover: String = "",
    val status: String = "",
    val search: String = "",
    var userNameTxt: String = ""
) : Parcelable