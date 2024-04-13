package com.example.myapplication.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityForgotPasswordBinding
import com.example.myapplication.listener.ForgotPasswordListener
import com.example.myapplication.utils.LoginUtils
import com.example.myapplication.utils.show
import com.example.myapplication.utils.toast
import com.example.myapplication.view_model.ActivityForgotPasswordModel

class ForgotPasswordActivity : AppCompatActivity(), ForgotPasswordListener {
    lateinit var binding: ActivityForgotPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)
        setContentView(binding.root)

        val model = ViewModelProvider(this)[ActivityForgotPasswordModel::class.java]
        model.forgotPasswordListener = this
        model.loginUtils = LoginUtils(this)

        binding.model = model

        binding.confirmButton.setOnClickListener {
            model.checkBirthDay(binding.birthday.value)
        }
    }

    override fun back() {
        startActivity(Intent(this, PinActivity::class.java))
        finish()
    }

    override fun giveWarning() {
        binding.wrongWarning.show()
    }

    override fun goToChangePassword() {
        startActivity(Intent(this, ChangePasswordActivity::class.java))
        finish()
    }
}