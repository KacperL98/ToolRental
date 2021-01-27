package com.kacper.itemxxx.chat.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Data (

    val user: String = "",
    val icon: Int = 0,
    val body: String = "",
    val title: String = "",
    val sented: String = "",
    var success: Int = 0

): Parcelable