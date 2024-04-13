package com.example.myapplication.view

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioGroup.OnCheckedChangeListener
import android.widget.TextView
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityHomeBinding
import com.example.myapplication.listener.HomeListener
import com.example.myapplication.listener.SocketListener
import com.example.myapplication.utils.SecurityUtils
import com.example.myapplication.utils.SocketUtils
import com.example.myapplication.utils.show
import com.example.myapplication.utils.toast
import com.example.myapplication.view_model.ActivityHomeActivityModel
import com.goodiebag.pinview.Pinview
import java.util.concurrent.Executor

class HomeActivity : AppCompatActivity(), HomeListener {
    lateinit var binding: ActivityHomeBinding
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    lateinit var model: ActivityHomeActivityModel
    lateinit var securityUtils: SecurityUtils
    lateinit var socketUtils: SocketUtils
    var isCheck = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        setContentView(binding.root)


        model = ViewModelProvider(this)[ActivityHomeActivityModel::class.java]
        securityUtils = SecurityUtils(this)
        model.securityUtils = securityUtils
        model.homeListener = this
        binding.model = model

        binding.doorName.text = securityUtils.getDoorName()

        socketUtils = SocketUtils()
        val socketListener: SocketListener = object: SocketListener {
            override fun onStatus(status: Boolean) {
                runOnUiThread {
                    if(status) {
                        binding.statusDoor.setImageResource(R.drawable.terbuka)
                    } else {
                        binding.statusDoor.setImageResource(R.drawable.terkunci)
                    }
                }
            }

            override fun onJoin(status: Boolean) {
                runOnUiThread {
                    if(status) {
                        toast("connected to server")
                    } else {
                        toast("failed connect to server")
                    }
                }
            }
        }

        socketUtils.socketEventListener(socketListener)

        socketUtils.connect()

        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    toast("Authentication error: $errString")
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    if(isCheck) {
                        showTimer()
                    } else {
                        triggerEvent()
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    toast("Authentication failed")
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Fingerprint Authentication")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()
    }

    override fun onClickTimer() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.pop_up_select_method_auth)

        dialog.findViewById<Button>(R.id.pin).setOnClickListener {
            showPinDialog(dialog.findViewById<CheckBox>(R.id.use_timer).isChecked)
            dialog.dismiss()
        }
        dialog.findViewById<Button>(R.id.sidik_jari).setOnClickListener {
            isCheck = dialog.findViewById<CheckBox>(R.id.use_timer).isChecked
            biometricPrompt.authenticate(promptInfo)
            dialog.dismiss()
        }
        dialog.findViewById<Button>(R.id.kembail).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun goToSetting() {
        startActivity(Intent(this, SettingActivity::class.java))
        finish()
    }

    override fun goToDoorSetting() {
        startActivity(Intent(this, DoorActivity::class.java))
        finish()
    }

    fun showPinDialog(isCheck: Boolean) {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.pop_up_anter_pin)

        dialog.findViewById<Button>(R.id.confirm_pin).setOnClickListener {
            if(dialog.findViewById<Pinview>(R.id.pin).value == securityUtils.getPin()) {
                if(isCheck) {
                    showTimer()
                } else {
                    triggerEvent()
                }
                dialog.dismiss()
            } else {
                dialog.findViewById<TextView>(R.id.wrong_warning).show()
            }
        }

        dialog.show()
    }

    fun showTimer() {
        val timer = securityUtils.getSecondTimer() * 1000
        binding.timerSecond.visibility = View.VISIBLE
        val countDownTimer = object : CountDownTimer(timer.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000

                binding.timerSecond.text = "$secondsRemaining"
            }

            override fun onFinish() {
                triggerEvent()
                binding.timerSecond.visibility = View.INVISIBLE
            }
        }

        // Mulai timer
        countDownTimer.start()
    }

    fun triggerEvent() {
        socketUtils.sendTrigger()
    }

}