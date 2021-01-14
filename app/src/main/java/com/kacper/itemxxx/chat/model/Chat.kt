package com.kacper.itemxxx.chat.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Chat(
    val sender: String = "",
    val message: String = "",
    val receiver: String = "",
    val isseen: Boolean = false,
    val url: String = "",
    val messageId: String = ""
) : Parcelable