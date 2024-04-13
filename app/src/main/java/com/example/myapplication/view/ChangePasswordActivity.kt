package com.example.myapplication.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityChangePasswordBinding
import com.example.myapplication.listener.ChangePasswordListener
import com.example.myapplication.utils.LoginUtils
import com.example.myapplication.utils.toast
import com.example.myapplication.view_model.ActivityChangePasswordModel

class ChangePasswordActivity : AppCompatActivity(), ChangePasswordListener {
    lateinit var binding: ActivityChangePasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password)
        setContentView(binding.root)

        val model = ViewModelProvider(this)[ActivityChangePasswordModel::class.java]
        model.loginUtils = LoginUtils(this)
        model.changePasswordListener = this

        binding.model = model

        binding.confirmButton.setOnClickListener {
            model.changePin(binding.pin.value)
        }
    }

    override fun back() {
        startActivity(Intent(this, ForgotPasswordActivity::class.java))
        finish()
    }

    override fun setValidPin() {
        toast("success change password")
        startActivity(Intent(this, PinActivity::class.java))
        finish()
    }
}