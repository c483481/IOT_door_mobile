package com.example.myapplication.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityPinBinding
import com.example.myapplication.listener.PinListener
import com.example.myapplication.view_model.ActivityPinModel

class PinActivity : AppCompatActivity(), PinListener {
    lateinit var binding: ActivityPinBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pin)
        setContentView(binding.root)

        val model = ViewModelProvider(this)[ActivityPinModel::class.java]

        model.pinListener = this

        binding.model = model

    }

    override fun backToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun toForgotPassword() {
        startActivity(Intent(this, ForgotPasswordActivity::class.java))
        finish()
    }


}