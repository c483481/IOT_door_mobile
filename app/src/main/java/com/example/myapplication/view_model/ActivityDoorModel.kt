package com.example.myapplication.view_model

import android.view.View
import androidx.lifecycle.ViewModel
import com.example.myapplication.listener.DoorListener

class ActivityDoorModel: ViewModel() {
    lateinit var doorListener: DoorListener

    fun backToHome(view: View) {
        doorListener.backToHome()
    }

    fun onChangeName(view: View) {
        doorListener.onChangeName()
    }

    fun onChangePin(view: View) {
        doorListener.onChangePin()
    }

    fun onChangeTimer(view: View) {
        doorListener.onChangeTimer()
    }
}