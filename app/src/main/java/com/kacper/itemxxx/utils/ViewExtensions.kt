package com.kacper.itemxxx.utils

import android.view.View
import java.text.SimpleDateFormat
import java.util.*


fun Calendar.toFormattedDisplay(): String {
    val simpleDateFormat = SimpleDateFormat("dd-M-yyyy hh:mm a", Locale.US)
    return simpleDateFormat.format(this.time)
}