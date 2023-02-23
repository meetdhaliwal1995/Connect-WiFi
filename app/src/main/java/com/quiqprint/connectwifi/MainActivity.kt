package com.quiqprint.connectwifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.quiqprint.connectwifi.databinding.ActivityMainBinding
import com.thanosfisherman.wifiutils.WifiUtils
import com.thanosfisherman.wifiutils.wifiScan.ScanResultsListener
import com.thanosfisherman.wifiutils.wifiState.WifiStateListener

@RequiresApi(Build.VERSION_CODES.Q)
class MainActivity : AppCompatActivity(), WifiStateListener, ScanResultsListener {

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

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        binding?.btnConnect?.setOnClickListener {
            ssid = binding?.evSSD?.text.toString()
            password = binding?.evPassword?.text.toString()
            if (ssid.isNullOrEmpty()) {
                Toast.makeText(this, "Both fields must not be empty", Toast.LENGTH_SHORT).show()
            } else if (password.isNullOrEmpty()) {
                Toast.makeText(this, "Both fields must not be empty", Toast.LENGTH_SHORT).show()
            } else {
                connectWIFI()
            }
        }
    }


    private fun connectWIFI() {
        val suggestion1 = password?.let {
            WifiNetworkSuggestion.Builder()
                .setSsid(ssid!!)
                .setWpa2Passphrase(it)
                .setIsAppInteractionRequired(true) // Optional (Needs location permission)
                .build()
        }

        val suggestionsList = listOf(suggestion1)

        wifiManager.removeNetworkSuggestions(suggestionsList)
        val status = wifiManager.addNetworkSuggestions(suggestionsList)

        if (status != WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
            // do error handling here
        }


        val panelIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
        startActivityForResult(panelIntent, 0)
//            WifiUtils.withContext(applicationContext).disableWifi()
//            WifiUtils.withContext(applicationContext).enableWifi(this)
    }

    override fun isSuccess(isSuccess: Boolean) {
        WifiUtils.withContext(applicationContext).scanWifi(this).start()
    }

    override fun onScanResults(scanResults: MutableList<ScanResult>) {}

    // Optional (Wait for post connection broadcast to one of your suggestions)
    private val intentFilter =
        IntentFilter(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION);

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (!intent.action.equals(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION)) {
                return
            }

            Toast.makeText(applicationContext, "Connected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }

}