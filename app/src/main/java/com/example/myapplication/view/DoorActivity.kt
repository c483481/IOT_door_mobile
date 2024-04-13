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
import com.example.myapplication.databinding.ActivityDoorBinding
import com.example.myapplication.listener.DoorListener
import com.example.myapplication.utils.SecurityUtils
import com.example.myapplication.utils.show
import com.example.myapplication.utils.toast
import com.example.myapplication.view_model.ActivityDoorModel
import com.goodiebag.pinview.Pinview

class DoorActivity : AppCompatActivity(), DoorListener {
    lateinit var binding: ActivityDoorBinding
    lateinit var securityUtils: SecurityUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_door)
        setContentView(binding.root)

        val model = ViewModelProvider(this)[ActivityDoorModel::class.java]

        model.doorListener = this

        binding.model = model

        securityUtils = SecurityUtils(this)

        binding.doorName.text = securityUtils.getDoorName()

        binding.timerName.text = "${securityUtils.getSecondTimer()} detik"
    }

    override fun onChangeName() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.pop_up_change_name)

        dialog.findViewById<Button>(R.id.change).setOnClickListener {
            val name = dialog.findViewById<EditText>(R.id.name_field).text.toString()

            securityUtils.setDoorName(name)
            binding.doorName.text = name
            toast("success change name")
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onChangePin() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.pop_up_anter_pin)

        dialog.findViewById<Button>(R.id.confirm_pin).setOnClickListener {
            if(dialog.findViewById<Pinview>(R.id.pin).value == securityUtils.getPin()) {
                changePin()
                dialog.dismiss()
            } else {
                dialog.findViewById<TextView>(R.id.wrong_warning).show()
            }
        }

        dialog.show()
    }

    override fun onChangeTimer() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.pop_up_change_timer)

        dialog.findViewById<Button>(R.id.change).setOnClickListener {
            val timer = dialog.findViewById<EditText>(R.id.timer_field).text.toString().toInt()

            securityUtils.setTimer(timer)
            binding.timerName.text = "$timer detik"
            toast("success change timer")
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun backToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    fun changePin() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.pop_up_anter_pin)

        dialog.findViewById<TextView>(R.id.sub_title).text = "Masukan Pin Baru"
        dialog.findViewById<TextView>(R.id.sub_title).visibility = View.VISIBLE

        dialog.findViewById<Button>(R.id.confirm_pin).setOnClickListener {
            val newPin = dialog.findViewById<Pinview>(R.id.pin).value

            if(newPin.length == 4) {
                securityUtils.changePin(newPin)
            }
            toast("success change pin")
            dialog.dismiss()
        }

        dialog.show()
    }
}