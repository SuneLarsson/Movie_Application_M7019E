package com.example.moviedbapplication.utils;

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
class ConnectivityObserver(context: Context) {
    private val cm = context.getSystemService(ConnectivityManager::class.java)
    private val _isOnline = MutableLiveData<Boolean>(false)
    val isOnline: LiveData<Boolean> = _isOnline

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) = _isOnline.postValue(true)
        override fun onLost(network:Network)     = _isOnline.postValue(false)
    }

    init {
        cm.registerDefaultNetworkCallback(callback)
    }

    fun unregister() {
        cm.unregisterNetworkCallback(callback)
    }
}

