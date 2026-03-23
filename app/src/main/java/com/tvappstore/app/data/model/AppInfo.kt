package com.tvappstore.app.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 应用信息数据模型
 */
data class AppInfo(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("package_name")
    val packageName: String,
    
    @SerializedName("version")
    val version: String,
    
    @SerializedName("version_code")
    val versionCode: Int,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("icon_url")
    val iconUrl: String?,
    
    @SerializedName("download_url")
    val downloadUrl: String,
    
    @SerializedName("file_size")
    val fileSize: Long?,
    
    @SerializedName("category")
    val category: String?,
    
    @SerializedName("developer")
    val developer: String?,
    
    @SerializedName("rating")
    val rating: Int?,
    
    @SerializedName("downloads")
    val downloads: Int?,
    
    @SerializedName("is_active")
    val isActive: Boolean?,
    
    @SerializedName("created_at")
    val createdAt: String?,
    
    @SerializedName("updated_at")
    val updatedAt: String?
) : Serializable {
    /**
     * 获取格式化的文件大小
     */
    fun getFormattedSize(): String {
        val size = fileSize ?: return "未知"
        return when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> String.format("%.1f KB", size / 1024.0)
            size < 1024 * 1024 * 1024 -> String.format("%.1f MB", size / 1024.0 / 1024.0)
            else -> String.format("%.1f GB", size / 1024.0 / 1024.0 / 1024.0)
        }
    }
    
    /**
     * 获取格式化的下载量
     */
    fun getFormattedDownloads(): String {
        val count = downloads ?: return "0"
        return when {
            count < 1000 -> count.toString()
            count < 10000 -> String.format("%.1fK", count / 1000.0)
            count < 100000000 -> String.format("%.1fM", count / 1000000.0)
            else -> String.format("%.1fB", count / 1000000000.0)
        }
    }
}
