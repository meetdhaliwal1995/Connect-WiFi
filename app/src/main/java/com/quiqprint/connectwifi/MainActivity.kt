package com.quiqprint.connectwifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.quiqprint.connectwifi.databinding.ActivityMainBinding
import com.thanosfisherman.wifiutils.WifiUtils
import com.thanosfisherman.wifiutils.wifiScan.ScanResultsListener
import com.thanosfisherman.wifiutils.wifiState.WifiStateListener

@RequiresApi(Build.VERSION_CODES.Q)
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding

    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var wifiManager: WifiManager
    private var wifiNetwork: Network? = null
    private var ssid: String? = null
    private var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


        binding?.btnConnect?.setOnClickListener {
            ssid = binding?.evSSD?.text.toString()
            password = binding?.evPassword?.text.toString()
            if (ssid.isNullOrEmpty()) {
                Toast.makeText(this, "Both fields must not be empty", Toast.LENGTH_SHORT).show()
            } else if (password.isNullOrEmpty()) {
                Toast.makeText(this, "Both fields must not be empty", Toast.LENGTH_SHORT).show()
            } else {
                connectToWifi()
            }
        }
    }


    private fun connectToWifi() {
        val specifier = WifiNetworkSpecifier.Builder()
            .setSsid(ssid!!)
            .setWpa2Passphrase(password!!)
            .build()

        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .setNetworkSpecifier(specifier)
            .build()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                wifiNetwork = network
            }
        }

        connectivityManager.requestNetwork(request, networkCallback)
    }

    private fun disconnectFromWifi() {
        if (wifiNetwork != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback)
            wifiNetwork = null
        }
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onLost(network: Network) {
            super.onLost(network)
            wifiNetwork = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disconnectFromWifi()
    }
}