package com.tvappstore.app.data.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tvappstore.app.TVAppStoreApp
import com.tvappstore.app.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

/**
 * API 客户端
 */
class ApiClient private constructor() {
    
    companion object {
        private var instance: ApiClient? = null
        
        fun getInstance(): ApiClient {
            if (instance == null) {
                instance = ApiClient()
            }
            return instance!!
        }
    }
    
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()
    
    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()
    
    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    
    /**
     * 获取应用列表
     */
    suspend fun getApps(): Result<List<AppInfo>> = withContext(Dispatchers.IO) {
        try {
            val url = "${TVAppStoreApp.BASE_URL}/api/apps?active=true"
            val request = Request.Builder()
                .url(url)
                .get()
                .build()
            
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP ${response.code}"))
            }
            
            val body = response.body?.string() ?: return@withContext Result.failure(Exception("Empty response"))
            val apiResponse = gson.fromJson(body, ApiResponse::class.java) as ApiResponse<List<AppInfo>>
            
            if (apiResponse.success && apiResponse.data != null) {
                Result.success(apiResponse.data)
            } else {
                Result.failure(Exception(apiResponse.error ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取单个应用详情
     */
    suspend fun getAppById(id: Int): Result<AppInfo> = withContext(Dispatchers.IO) {
        try {
            val url = "${TVAppStoreApp.BASE_URL}/api/apps/$id"
            val request = Request.Builder()
                .url(url)
                .get()
                .build()
            
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP ${response.code}"))
            }
            
            val body = response.body?.string() ?: return@withContext Result.failure(Exception("Empty response"))
            val apiResponse = gson.fromJson(body, ApiResponse::class.java) as ApiResponse<AppInfo>
            
            if (apiResponse.success && apiResponse.data != null) {
                Result.success(apiResponse.data)
            } else {
                Result.failure(Exception(apiResponse.error ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 注册设备
     */
    suspend fun registerDevice(deviceRequest: DeviceRegisterRequest): Result<DeviceInfo> = withContext(Dispatchers.IO) {
        try {
            val url = "${TVAppStoreApp.BASE_URL}/api/devices"
            val json = gson.toJson(deviceRequest)
            val body = json.toRequestBody(JSON_MEDIA_TYPE)
            
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()
            
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP ${response.code}"))
            }
            
            val responseBody = response.body?.string() ?: return@withContext Result.failure(Exception("Empty response"))
            val apiResponse = gson.fromJson(responseBody, ApiResponse::class.java) as ApiResponse<DeviceInfo>
            
            if (apiResponse.success && apiResponse.data != null) {
                Result.success(apiResponse.data)
            } else {
                Result.failure(Exception(apiResponse.error ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取推送任务
     */
    suspend fun getPushTasks(deviceId: String? = null): Result<List<PushTask>> = withContext(Dispatchers.IO) {
        try {
            val urlBuilder = StringBuilder("${TVAppStoreApp.BASE_URL}/api/push")
            if (deviceId != null) {
                urlBuilder.append("?deviceId=$deviceId")
            }
            
            val request = Request.Builder()
                .url(urlBuilder.toString())
                .get()
                .build()
            
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP ${response.code}"))
            }
            
            val body = response.body?.string() ?: return@withContext Result.failure(Exception("Empty response"))
            val apiResponse = gson.fromJson(body, ApiResponse::class.java) as ApiResponse<List<PushTask>>
            
            if (apiResponse.success && apiResponse.data != null) {
                Result.success(apiResponse.data)
            } else {
                Result.failure(Exception(apiResponse.error ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
