package com.example.myapplication.view_model

import android.view.View
import androidx.lifecycle.ViewModel
import com.example.myapplication.listener.ForgotPasswordListener
import com.example.myapplication.utils.LoginUtils

class ActivityForgotPasswordModel: ViewModel() {
    lateinit var forgotPasswordListener: ForgotPasswordListener
    lateinit var loginUtils: LoginUtils

    fun back(view: View) {
        forgotPasswordListener.back()
    }

    fun checkBirthDay(pin: String) {
        if(pin == loginUtils.getBirthDay()) {
            forgotPasswordListener.goToChangePassword()
        } else {
            forgotPasswordListener.giveWarning()
        }
    }
}