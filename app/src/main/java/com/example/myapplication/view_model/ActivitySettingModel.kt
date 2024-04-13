package com.example.myapplication.view_model

import android.view.View
import androidx.lifecycle.ViewModel
import com.example.myapplication.listener.SettingListener

class ActivitySettingModel: ViewModel() {
    lateinit var settingListener: SettingListener

    fun backToHome(view: View){
        settingListener.goToHome()
    }

    fun onChangePin(view: View) {
        settingListener.onChangePin()
    }

    fun onChangeName(view: View) {
        settingListener.onChangeName()
    }
}