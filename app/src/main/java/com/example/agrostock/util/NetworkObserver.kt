package com.example.agrostock.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

/**
 * Real-time Android Network Observer for AgroStock App.
 * Automatically detects Wi-Fi, Mobile Data (Cellular), and Ethernet internet connectivity.
 */
object NetworkObserver {

    /**
     * Checks if the device is currently connected to Wi-Fi, Mobile Data, or Ethernet.
     */
    fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        val isWifi = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        val isCellular = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        val isEthernet = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val isValidated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

        return (isWifi || isCellular || isEthernet) && hasInternet && isValidated
    }

    /**
     * Registers a real-time network callback to continuously observe internet changes.
     */
    fun registerNetworkCallback(context: Context, onStatusChanged: (Boolean) -> Unit): ConnectivityManager.NetworkCallback? {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return null

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                val isConnected = isNetworkConnected(context)
                onStatusChanged(isConnected)
            }

            override fun onLost(network: Network) {
                val isConnected = isNetworkConnected(context)
                onStatusChanged(isConnected)
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                val isConnected = isNetworkConnected(context)
                onStatusChanged(isConnected)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .build()

        try {
            connectivityManager.registerNetworkCallback(request, callback)
            // Emit initial status immediately
            onStatusChanged(isNetworkConnected(context))
        } catch (e: Exception) {
            onStatusChanged(isNetworkConnected(context))
        }

        return callback
    }
}
