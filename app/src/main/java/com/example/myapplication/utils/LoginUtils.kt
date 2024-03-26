package com.example.myapplication.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor

class LoginUtils {
    lateinit var pref: SharedPreferences
    lateinit var editor: Editor
    lateinit var con: Context
    val PRIVATE_MODE: Int = 0
    val defaultPin = "5432"
    constructor(con: Context) {
        this.con = con
        this.pref = con.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        this.editor = this.pref.edit()
    }

    companion object {
        const val PREF_NAME = "LOGIN_SESSION"
        const val PIN_NAME = "Pin"
    }

    fun getPin(): String {
        return this.pref.getString(PIN_NAME, defaultPin).toString()
    }

}