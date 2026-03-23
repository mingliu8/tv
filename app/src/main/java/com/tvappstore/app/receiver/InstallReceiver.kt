package com.tvappstore.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tvappstore.app.data.AppInstallManager

/**
 * 应用安装/卸载广播接收器
 */
class InstallReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        
        val packageName = intent.data?.schemeSpecificPart ?: return
        
        when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED -> {
                // 应用已安装
                notifyInstallStatus(context, packageName, true)
            }
            Intent.ACTION_PACKAGE_REMOVED -> {
                // 应用已卸载
                notifyInstallStatus(context, packageName, false)
            }
        }
    }
    
    private fun notifyInstallStatus(context: Context, packageName: String, installed: Boolean) {
        // 发送本地广播通知 UI 更新
        val intent = Intent(ACTION_INSTALL_STATUS_CHANGED).apply {
            putExtra(EXTRA_PACKAGE_NAME, packageName)
            putExtra(EXTRA_INSTALLED, installed)
        }
        context.sendBroadcast(intent)
    }
    
    companion object {
        const val ACTION_INSTALL_STATUS_CHANGED = "com.tvappstore.app.INSTALL_STATUS_CHANGED"
        const val EXTRA_PACKAGE_NAME = "package_name"
        const val EXTRA_INSTALLED = "installed"
    }
}
