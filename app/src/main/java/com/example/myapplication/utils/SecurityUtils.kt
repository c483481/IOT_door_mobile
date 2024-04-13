package com.example.myapplication.utils

import android.content.Context
import android.content.SharedPreferences

class SecurityUtils {
    val pref: SharedPreferences
    val editor: SharedPreferences.Editor
    val con: Context
    val PRIVATE_MODE: Int = 0
    val defaultPin = "5432"
    val defaultDoorName = "Kunci Kamar"
    val defaultTimer = 30

    constructor(con: Context) {
        this.con = con
        this.pref = con.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        this.editor = this.pref.edit()
    }

    companion object {
        const val PREF_NAME = "Security_Session"
        const val PIN_NAME = "Pin_Security"
        const val DOOR_NAME = "door_name"
        const val DOOR_TIMER = "door_timer"
    }

    fun getPin(): String {
        return this.pref.getString(PIN_NAME, defaultPin).toString()
    }

    fun changePin(newPin: String) {
        this.editor.putString(PIN_NAME, newPin)
        this.editor.commit()
    }

    fun getDoorName(): String {
        return this.pref.getString(DOOR_NAME, defaultDoorName).toString()
    }

    fun setDoorName(name: String) {
        this.editor.putString(DOOR_NAME, name)
        this.editor.commit()
    }

    fun getSecondTimer(): Int {
        return this.pref.getInt(DOOR_TIMER, defaultTimer)
    }

    fun setTimer(second: Int) {
        this.editor.putInt(DOOR_TIMER, second)
        this.editor.commit()
    }
}