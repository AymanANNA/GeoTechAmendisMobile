package com.ar.backgroundlocation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import org.chromium.base.task.AsyncTask
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import android.text.format.Formatter
import android.widget.Toast
import androidx.core.app.ActivityCompat.finishAffinity
import org.chromium.base.ThreadUtils.runOnUiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.log



/**
 * @Author: Abdul Rehman
 * @Date: 06/05/2024.
 */
class LocationService : Service(), LocationUpdatesCallBack {
    private val TAG = LocationService::class.java.simpleName

    private lateinit var gpsLocationClient: GPSLocationClient
    private var notification: NotificationCompat.Builder? = null
    private var notificationManager: NotificationManager? = null

    override fun onCreate() {
        super.onCreate()
        gpsLocationClient = GPSLocationClient()
        gpsLocationClient.setLocationUpdatesCallBack(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_SERVICE_START -> startService()
            ACTION_SERVICE_STOP -> stopService()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    companion object {
        const val ACTION_SERVICE_START = "ACTION_START"
        const val ACTION_SERVICE_STOP = "ACTION_STOP"
    }


    private fun startService() {
        gpsLocationClient.getLocationUpdates(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location",
                "Location",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Tracking location...")
            .setContentText("Searching...")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)

        notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        startForeground(1, notification?.build())
    }

    private fun stopService() {
        gpsLocationClient.setLocationUpdatesCallBack(null)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun locationException(message: String) {
        Log.d(TAG, message)
    }

    private fun streamToString(inputStream: InputStream): String {
        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            return reader.readText()
        }
    }

    override fun onLocationUpdate(location: Location) {
        val token = getToken()
        if (token != null) {
            if (token <= 0) {
                performLogin()
            }
        }
        Thread {
            sendLocationToApi(location)
            sendTechnicienDetail()
        }.start()
    }

    private fun getToken(): Int? {
        val sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("technicienId", 0)
    }

    private fun performLogin() {
        val deviceName = Build.MODEL
        println("Device Name: $deviceName")
        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val ipAddress = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)

        val loginRequest = LoginRequest(deviceName, ipAddress)
        fun shouldExitAppOnError(): Boolean {
            // Implement your logic to decide if the app should exit
            return true // Example: return true to exit the app on this error
        }

        RetrofitClient.apiService.performLogin(loginRequest).enqueue(object : retrofit2.Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: retrofit2.Response<Map<String, Any>>) {
                if (response.isSuccessful) {
                    println("Response: ${response.body()}")

                    response.body()?.let {
                        val accessToken = it["access"] as String?
                        val refreshToken = it["refresh"] as String?
                        val userId = it["userId"] as Double?
                        val technicienId = it["technicienId"] as Double?

                        saveTokensAndUsername(accessToken, refreshToken, userId, technicienId)
                    }
                } else {
                    println("Response error: ${response.code()} ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                println("Error during login operation: ${t.message}")
            }
        })
    }

    fun saveTokensAndUsername(accessToken: String?, refreshToken: String?, userId: Double?, technicienId: Double?) {
        val sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            if (accessToken != null) putString("AccessToken", accessToken)
            if (refreshToken != null) putString("RefreshToken", refreshToken)
            if (userId != null) putInt("userId", userId.toInt())
            if (technicienId != null) putInt("technicienId", technicienId.toInt())
            apply()
        }
    }

    private fun sendLocationToApi(location: Location) {
        val sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("userId", 0)
        val token = sharedPreferences.getString("AccessToken", null)
        val editor = sharedPreferences.edit()
        editor.clear()
        // Format the current time as an ISO 8601 string
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val currentTimestamp = dateFormat.format(Date())
        val locationRequest = userId.let {
            LocationRequest(
                technicien = it,
                latitude = location.latitude,
                longitude = location.longitude,
                timestamp = currentTimestamp // Include the timestamp here
            )
        }

        if (token == null) {
            println("No access token available.")
            return
        }

        if (locationRequest != null) {
            val authToken = "Bearer $token"
            RetrofitClient.apiService.sendLocation(authToken, locationRequest).enqueue(object : retrofit2.Callback<Void> {
                override fun onResponse(call: Call<Void>, response: retrofit2.Response<Void>) {
                    if (response.isSuccessful) {
                        println("Location sent successfully")
                    } else {
                        println("HTTP error code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }
    }

    fun sendTechnicienDetail() {
        val sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val technicienId = sharedPreferences.getInt("technicienId", 0)
        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val ipAddress = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
        val bm = applicationContext.getSystemService(BATTERY_SERVICE) as BatteryManager
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("GMT+1") // Set the timezone to GMT+1
        val currentTime = dateFormat.format(Date())

        val technicienDetail = TechnicienDetail(
            technicien_id = technicienId,
            device_name = Build.DEVICE,
            manufacturer = Build.MANUFACTURER,
            os_version = Build.VERSION.RELEASE,
            model = Build.MODEL,
            ip_address = ipAddress,
            battery_level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY).toString(),
            last_sent_location_time = currentTime
        )

        RetrofitClient.apiService.postTechnicienDetail(technicienDetail).enqueue(object :
            Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    // Handle success
                    println("Successfully posted technicien detail")
                } else {
                    // Handle possible failures
                    println("Failed to post technicien detail: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                // Handle complete failure to communicate with the API
                println("Error posting technicien detail: ${t.message}")
            }
        })
    }



}