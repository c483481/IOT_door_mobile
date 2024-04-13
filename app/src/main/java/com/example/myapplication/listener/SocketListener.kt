package com.example.myapplication.listener

interface SocketListener {
    fun onStatus(status: Boolean)

    fun onJoin(status: Boolean)
}