package com.example.myapplication.view_model

import android.view.View
import androidx.lifecycle.ViewModel
import com.example.myapplication.listener.ChangePasswordListener
import com.example.myapplication.utils.LoginUtils

class ActivityChangePasswordModel: ViewModel() {
    lateinit var changePasswordListener: ChangePasswordListener
    lateinit var loginUtils: LoginUtils
    fun back(view: View) {
        changePasswordListener.back()
    }

    fun changePin(newPin: String) {
        if(newPin.length == 4) {
            loginUtils.changePin(newPin)
            changePasswordListener.setValidPin()
        }
    }
}