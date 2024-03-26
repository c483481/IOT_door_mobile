package com.example.myapplication.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.listener.MainListener
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.view_model.ActivityMainModel

class MainActivity : AppCompatActivity(), MainListener {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(binding.root)
        val model = ViewModelProvider(this)[ActivityMainModel::class.java]
        model.mainListener = this

        binding.model = model
    }

    override fun changeToPinSelect() {
        startActivity(Intent(this, PinActivity::class.java))
        finish()
    }
}