package ru.dimon.ydav2024

import android.net.ConnectivityManager.NetworkCallback
import android.net.LinkProperties
import android.net.Network

class NetworkInfo: NetworkCallback() {
    override fun onAvailable(network: Network) {
        super.onAvailable(network)
    }

    override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
        super.onLinkPropertiesChanged(network, linkProperties)
    }
}