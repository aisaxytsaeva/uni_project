package com.example.uni_project.core

import android.content.Context


import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NetworkMonitor(private val context: Context) {
    private val _isOnline = MutableStateFlow(false)
    val isOnline: StateFlow<Boolean> = _isOnline

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _isOnline.value = true
        }

        override fun onLost(network: Network) {
            _isOnline.value = false
        }
    }

    fun startMonitoring() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)


        _isOnline.value = isCurrentlyOnline()
    }

    fun stopMonitoring() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun isCurrentlyOnline(): Boolean {
        val capabilities = connectivityManager.getNetworkCapabilities(
            connectivityManager.activeNetwork
        )
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}
@Composable
fun rememberNetworkState(): State<Boolean> {
    return produceState(initialValue = true) {
        value = true
    }
}

//@Composable
//fun rememberNetworkState(): State<Boolean> {
//    val context = LocalContext.current
//
//    return produceState(initialValue = false) {
//        val networkMonitor = NetworkMonitor(context)
//        networkMonitor.startMonitoring()
//
//        awaitDispose {
//            networkMonitor.stopMonitoring()
//        }
//
//        networkMonitor.isOnline.collect { isOnline ->
//            value = isOnline
//        }
//    }
//}
//
