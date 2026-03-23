package com.tvappstore.app.data

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import com.tvappstore.app.TVAppStoreApp
import com.tvappstore.app.data.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * 应用下载和安装管理器
 */
class AppInstallManager private constructor(private val context: Context) {
    
    companion object {
        @Volatile
        private var instance: AppInstallManager? = null
        
        fun getInstance(context: Context): AppInstallManager {
            return instance ?: synchronized(this) {
                instance ?: AppInstallManager(context.applicationContext).also { instance = it }
            }
        }
        
        private const val APK_DOWNLOAD_DIR = "tv_app_store"
    }
    
    private val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    
    // 下载状态监听
    private var downloadListener: ((Long, Int) -> Unit)? = null
    
    // 安装状态监听
    private var installListener: ((AppInfo, Boolean) -> Unit)? = null
    
    /**
     * 下载 APK
     * @param app 应用信息
     * @return 下载 ID
     */
    suspend fun downloadApp(app: AppInfo): Result<Long> = withContext(Dispatchers.IO) {
        try {
            // 创建下载目录
            val downloadDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), APK_DOWNLOAD_DIR)
            if (!downloadDir.exists()) {
                downloadDir.mkdirs()
            }
            
            // 删除旧文件
            val fileName = "${app.packageName}_${app.versionCode}.apk"
            val outputFile = File(downloadDir, fileName)
            if (outputFile.exists()) {
                outputFile.delete()
            }
            
            // 创建下载请求
            val request = DownloadManager.Request(Uri.parse(app.downloadUrl))
                .setTitle(app.name)
                .setDescription("正在下载 ${app.name} v${app.version}")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, "$APK_DOWNLOAD_DIR/$fileName")
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
            
            // 开始下载
            val downloadId = downloadManager.enqueue(request)
            
            Result.success(downloadId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取下载进度
     */
    fun getDownloadProgress(downloadId: Long): Int {
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager.query(query)
        
        if (cursor.moveToFirst()) {
            val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val bytesDownloadedIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
            val bytesTotalIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
            
            val status = cursor.getInt(statusIndex)
            val bytesDownloaded = cursor.getLong(bytesDownloadedIndex)
            val bytesTotal = cursor.getLong(bytesTotalIndex)
            
            cursor.close()
            
            if (bytesTotal > 0) {
                return ((bytesDownloaded * 100) / bytesTotal).toInt()
            }
        }
        
        cursor.close()
        return 0
    }
    
    /**
     * 获取下载文件路径
     */
    fun getDownloadedFilePath(downloadId: Long): String? {
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager.query(query)
        
        if (cursor.moveToFirst()) {
            val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val localUriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
            
            val status = cursor.getInt(statusIndex)
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                val uri = cursor.getString(localUriIndex)
                cursor.close()
                return Uri.parse(uri).path
            }
        }
        
        cursor.close()
        return null
    }
    
    /**
     * 安装 APK
     */
    fun installApk(app: AppInfo, apkPath: String): Boolean {
        return try {
            val apkFile = File(apkPath)
            if (!apkFile.exists()) {
                return false
            }
            
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    apkFile
                )
            } else {
                Uri.fromFile(apkFile)
            }
            
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * 检查应用是否已安装
     */
    fun isAppInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 获取已安装应用的版本
     */
    fun getInstalledAppVersion(packageName: String): String? {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(packageName, 0)
            packageInfo.versionName
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 卸载应用
     */
    fun uninstallApp(packageName: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_DELETE)
            intent.data = Uri.parse("package:$packageName")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * 打开应用
     */
    fun openApp(packageName: String): Boolean {
        return try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * 设置下载监听器
     */
    fun setDownloadListener(listener: (Long, Int) -> Unit) {
        downloadListener = listener
    }
    
    /**
     * 设置安装监听器
     */
    fun setInstallListener(listener: (AppInfo, Boolean) -> Unit) {
        installListener = listener
    }
}
