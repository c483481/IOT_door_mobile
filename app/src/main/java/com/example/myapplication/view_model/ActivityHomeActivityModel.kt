package com.example.myapplication.view_model

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import com.example.myapplication.listener.HomeListener
import com.example.myapplication.utils.SecurityUtils

class ActivityHomeActivityModel: ViewModel() {
    lateinit var homeListener: HomeListener
    lateinit var securityUtils: SecurityUtils

    fun clickTimer(view: View) {
        homeListener.onClickTimer()
    }

    fun clickSetting(view: View) {
        homeListener.goToSetting()
    }

    fun doorSetting(view: View) {
        homeListener.goToDoorSetting()
    }
}