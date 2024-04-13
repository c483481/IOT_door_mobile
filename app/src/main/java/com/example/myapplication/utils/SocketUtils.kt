package com.example.myapplication.utils

import com.example.myapplication.listener.SocketListener
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

class SocketUtils {
    private val socket: Socket = IO.socket("http://10.0.2.2:3000")

    fun connect() {
        socket.connect()
        val json = JSONObject()
        json.put("key", "qwert12345")
        json.put("type", "mobile")
        socket.emit("join", json)
    }

    fun socketEventListener(listener: SocketListener) {
        socket.on("joined") {
            val status = it[0] as Boolean
            listener.onJoin(status)
        }

        socket.on("status") {
            val status = it[0] as Boolean
            listener.onStatus(status)
        }
    }

    fun sendTrigger() {
        socket.emit("send")
    }
}