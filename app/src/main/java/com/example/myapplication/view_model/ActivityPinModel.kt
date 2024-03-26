package com.example.myapplication.view_model

import android.view.View
import androidx.lifecycle.ViewModel
import com.example.myapplication.listener.PinListener

class ActivityPinModel: ViewModel() {
    lateinit var pinListener: PinListener

    fun back(view: View) {
        pinListener.backToMain()
    }

    fun forgotPassword(view: View) {
        pinListener.toForgotPassword()
    }
}