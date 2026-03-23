package com.tvappstore.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * API 响应数据模型
 */
data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("data")
    val data: T?,
    
    @SerializedName("error")
    val error: String?
)

/**
 * 设备注册请求
 */
data class DeviceRegisterRequest(
    @SerializedName("deviceId")
    val deviceId: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("model")
    val model: String,
    
    @SerializedName("osVersion")
    val osVersion: String,
    
    @SerializedName("ip")
    val ip: String?
)

/**
 * 设备信息响应
 */
data class DeviceInfo(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("device_id")
    val deviceId: String,
    
    @SerializedName("name")
    val name: String?,
    
    @SerializedName("model")
    val model: String?,
    
    @SerializedName("os_version")
    val osVersion: String?,
    
    @SerializedName("ip")
    val ip: String?,
    
    @SerializedName("last_online_at")
    val lastOnlineAt: String?,
    
    @SerializedName("is_active")
    val isActive: Boolean?,
    
    @SerializedName("created_at")
    val createdAt: String?,
    
    @SerializedName("updated_at")
    val updatedAt: String?
)

/**
 * 推送任务
 */
data class PushTask(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("app_id")
    val appId: Int,
    
    @SerializedName("device_id")
    val deviceId: Int?,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("message")
    val message: String?,
    
    @SerializedName("app")
    val app: AppInfo?,
    
    @SerializedName("created_at")
    val createdAt: String?,
    
    @SerializedName("completed_at")
    val completedAt: String?
)
