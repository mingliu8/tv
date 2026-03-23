package com.tvappstore.app

import android.app.Application
import android.app.DownloadManager
import android.content.Context

/**
 * TV 应用市场 Application 类
 */
class TVAppStoreApp : Application() {
    
    companion object {
        lateinit var instance: TVAppStoreApp
            private set
        
        // 服务器地址（已配置为当前服务器）
        const val BASE_URL = "https://705e5dd5-a004-4a37-8e13-60d9d14f6310.dev.coze.site"
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
    
    /**
     * 获取下载管理器
     */
    fun getDownloadManager(): DownloadManager {
        return getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }
}
