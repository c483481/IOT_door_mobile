package com.example.myapplication.view_model

import android.view.View
import androidx.lifecycle.ViewModel
import com.example.myapplication.listener.ForgotPasswordListener

class ActivityForgotPasswordModel: ViewModel() {
    lateinit var forgotPasswordListener: ForgotPasswordListener

    fun back(view: View) {
        forgotPasswordListener.back()
    }
}