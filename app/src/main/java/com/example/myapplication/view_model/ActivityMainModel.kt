package com.example.myapplication.view_model

import android.view.View
import androidx.lifecycle.ViewModel
import com.example.myapplication.listener.MainListener

class ActivityMainModel: ViewModel() {
    lateinit var mainListener: MainListener

    fun onClickPin(view: View) {
        mainListener.changeToPinSelect()
    }

    fun onClickFingerprint(view: View) {
        mainListener.fingerPrintAuth()
    }

    fun pengenalanWajah(view: View) {
        mainListener.imageAuth()
    }
}