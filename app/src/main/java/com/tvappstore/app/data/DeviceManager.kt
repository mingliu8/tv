package com.tvappstore.app.data

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import com.tvappstore.app.TVAppStoreApp
import com.tvappstore.app.data.api.ApiClient
import com.tvappstore.app.data.model.DeviceRegisterRequest
import kotlinx.coroutines.*
import java.net.Inet4Address
import java.net.NetworkInterface

/**
 * 设备管理器
 * 负责设备注册、心跳维护等
 */
class DeviceManager private constructor(private val context: Context) {
    
    companion object {
        @Volatile
        private var instance: DeviceManager? = null
        
        fun getInstance(context: Context): DeviceManager {
            return instance ?: synchronized(this) {
                instance ?: DeviceManager(context.applicationContext).also { instance = it }
            }
        }
        
        private const val PREF_NAME = "device_prefs"
        private const val KEY_DEVICE_ID = "device_id"
        private const val KEY_DEVICE_NAME = "device_name"
    }
    
    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val apiClient = ApiClient.getInstance()
    
    private var heartbeatJob: Job? = null
    
    /**
     * 获取设备 ID
     */
    fun getDeviceId(): String {
        var deviceId = prefs.getString(KEY_DEVICE_ID, null)
        if (deviceId == null) {
            deviceId = "tv_${System.currentTimeMillis()}_${(Math.random() * 100000).toInt()}"
            prefs.edit().putString(KEY_DEVICE_ID, deviceId).apply()
        }
        return deviceId
    }
    
    /**
     * 获取设备名称
     */
    fun getDeviceName(): String {
        var name = prefs.getString(KEY_DEVICE_NAME, null)
        if (name == null) {
            name = "${Build.MODEL} TV"
            prefs.edit().putString(KEY_DEVICE_NAME, name).apply()
        }
        return name
    }
    
    /**
     * 设置设备名称
     */
    fun setDeviceName(name: String) {
        prefs.edit().putString(KEY_DEVICE_NAME, name).apply()
    }
    
    /**
     * 获取设备型号
     */
    fun getDeviceModel(): String {
        return "${Build.MANUFACTURER} ${Build.MODEL}"
    }
    
    /**
     * 获取系统版本
     */
    fun getOsVersion(): String {
        return "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
    }
    
    /**
     * 获取 IP 地址
     */
    fun getIpAddress(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    if (!address.isLoopbackAddress && address is Inet4Address) {
                        return address.hostAddress
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
    
    /**
     * 注册设备到服务器
     */
    suspend fun registerDevice(): Result<Boolean> {
        val request = DeviceRegisterRequest(
            deviceId = getDeviceId(),
            name = getDeviceName(),
            model = getDeviceModel(),
            osVersion = getOsVersion(),
            ip = getIpAddress()
        )
        
        return apiClient.registerDevice(request).map { true }
    }
    
    /**
     * 启动心跳
     */
    fun startHeartbeat() {
        stopHeartbeat()
        
        heartbeatJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                registerDevice()
                // 每 30 秒发送一次心跳
                delay(30000)
            }
        }
    }
    
    /**
     * 停止心跳
     */
    fun stopHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = null
    }
}
