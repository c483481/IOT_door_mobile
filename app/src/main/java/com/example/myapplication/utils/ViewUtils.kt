package com.example.myapplication.utils

import android.content.Context
import android.view.View
import android.widget.TextView
import android.widget.Toast

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun TextView.show() {
    visibility = View.VISIBLE
}
