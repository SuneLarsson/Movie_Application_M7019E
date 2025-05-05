package com.example.moviedbapplication.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ConnectivityObserver(context: Context) {
    private val cm = context.getSystemService(ConnectivityManager::class.java)

    // StateFlow to emit connectivity changes
    private val _isOnline = MutableStateFlow(false)
    val isOnline: StateFlow<Boolean> get() = _isOnline

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _isOnline.value = true
        }

        override fun onLost(network: Network) {
            _isOnline.value = false
        }
    }

    init {
        cm.registerDefaultNetworkCallback(callback)
    }

    fun unregister() {
        cm.unregisterNetworkCallback(callback)
    }
}