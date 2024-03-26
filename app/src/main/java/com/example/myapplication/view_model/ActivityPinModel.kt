package com.example.myapplication.view_model

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import com.example.myapplication.listener.PinListener
import com.example.myapplication.utils.LoginUtils

class ActivityPinModel: ViewModel() {
    lateinit var pinListener: PinListener
    lateinit var loginUtils: LoginUtils

    fun back(view: View) {
        pinListener.backToMain()
    }

    fun forgotPassword(view: View) {
        pinListener.toForgotPassword()
    }

    fun checkPin(pin: String) {
        if(pin == loginUtils.getPin()) {
            pinListener.goToHome()
        } else {
            pinListener.giveWarning()
        }
    }
}