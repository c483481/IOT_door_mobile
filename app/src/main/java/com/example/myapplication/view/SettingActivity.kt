package com.example.myapplication.view

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivitySettingBinding
import com.example.myapplication.listener.SettingListener
import com.example.myapplication.utils.LoginUtils
import com.example.myapplication.utils.show
import com.example.myapplication.utils.toast
import com.example.myapplication.view_model.ActivitySettingModel
import com.goodiebag.pinview.Pinview

class SettingActivity : AppCompatActivity(), SettingListener {
    lateinit var binding: ActivitySettingBinding
    lateinit var loginUtils: LoginUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)
        setContentView(binding.root)
        loginUtils = LoginUtils(this)

        val model = ViewModelProvider(this)[ActivitySettingModel::class.java]

        model.settingListener = this

        binding.model = model
        binding.usersName.text = loginUtils.getName()
    }

    override fun onChangeName() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.pop_up_change_name)

        dialog.findViewById<Button>(R.id.change).setOnClickListener {
            val name = dialog.findViewById<EditText>(R.id.name_field).text.toString()

            loginUtils.changeUsername(name)
            binding.usersName.text = name
            toast("success change name")
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun goToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    override fun onChangePin() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.pop_up_anter_pin)

        dialog.findViewById<Button>(R.id.confirm_pin).setOnClickListener {
            if(dialog.findViewById<Pinview>(R.id.pin).value == loginUtils.getPin()) {
                changePin()
                dialog.dismiss()
            } else {
                dialog.findViewById<TextView>(R.id.wrong_warning).show()
            }
        }

        dialog.show()
    }

    fun changePin() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.pop_up_anter_pin)

        dialog.findViewById<TextView>(R.id.sub_title).text = "Masukan Pin Baru"
        dialog.findViewById<TextView>(R.id.sub_title).visibility = View.VISIBLE

        dialog.findViewById<Button>(R.id.confirm_pin).setOnClickListener {
            val newPin = dialog.findViewById<Pinview>(R.id.pin).value

            if(newPin.length == 4) {
                loginUtils.changePin(newPin)
            }
            toast("success change pin")
            dialog.dismiss()
        }

        dialog.show()
    }
}