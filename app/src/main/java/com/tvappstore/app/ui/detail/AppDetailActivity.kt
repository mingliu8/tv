package com.tvappstore.app.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.tvappstore.app.R
import com.tvappstore.app.data.AppInstallManager
import com.tvappstore.app.data.model.AppInfo
import kotlinx.android.synthetic.main.activity_app_detail.*
import kotlinx.coroutines.launch

/**
 * 应用详情页
 */
class AppDetailActivity : FragmentActivity() {
    
    companion object {
        private const val EXTRA_APP_INFO = "extra_app_info"
        
        fun newIntent(context: Context, app: AppInfo): Intent {
            val intent = Intent(context, AppDetailActivity::class.java)
            intent.putExtra(EXTRA_APP_INFO, app)
            return intent
        }
    }
    
    private lateinit var app: AppInfo
    private val installManager by lazy { AppInstallManager.getInstance(this) }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_detail)
        
        app = intent.getSerializableExtra(EXTRA_APP_INFO) as AppInfo
        
        setupUI()
        checkInstallStatus()
    }
    
    private fun setupUI() {
        // 应用名称
        tv_app_name.text = app.name
        
        // 开发者
        tv_developer.text = app.developer ?: "未知开发者"
        
        // 版本信息
        tv_version.text = "版本 ${app.version}"
        
        // 文件大小
        tv_size.text = "大小 ${app.getFormattedSize()}"
        
        // 下载量
        tv_downloads.text = "下载量 ${app.getFormattedDownloads()}"
        
        // 分类
        tv_category.text = "分类 ${app.category ?: "其他"}"
        
        // 描述
        tv_description.text = app.description ?: "暂无描述"
        
        // 图标
        if (!app.iconUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(app.iconUrl)
                .placeholder(R.drawable.ic_app_default)
                .error(R.drawable.ic_app_default)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(iv_app_icon)
        }
        
        // 安装按钮
        btn_install.setOnClickListener {
            onInstallClicked()
        }
        
        // 打开按钮
        btn_open.setOnClickListener {
            installManager.openApp(app.packageName)
        }
        
        // 卸载按钮
        btn_uninstall.setOnClickListener {
            installManager.uninstallApp(app.packageName)
        }
    }
    
    private fun checkInstallStatus() {
        val isInstalled = installManager.isAppInstalled(app.packageName)
        
        if (isInstalled) {
            btn_install.visibility = View.GONE
            btn_open.visibility = View.VISIBLE
            btn_uninstall.visibility = View.VISIBLE
            
            val installedVersion = installManager.getInstalledAppVersion(app.packageName)
            if (installedVersion != null && installedVersion != app.version) {
                // 有新版本可更新
                btn_install.text = "更新"
                btn_install.visibility = View.VISIBLE
            }
        } else {
            btn_install.visibility = View.VISIBLE
            btn_open.visibility = View.GONE
            btn_uninstall.visibility = View.GONE
        }
    }
    
    private fun onInstallClicked() {
        btn_install.isEnabled = false
        btn_install.text = "下载中..."
        
        lifecycleScope.launch {
            val result = installManager.downloadApp(app)
            
            result.fold(
                onSuccess = { downloadId ->
                    monitorDownload(downloadId)
                },
                onFailure = { error ->
                    btn_install.isEnabled = true
                    btn_install.text = "安装"
                    showError("下载失败: ${error.message}")
                }
            )
        }
    }
    
    private fun monitorDownload(downloadId: Long) {
        lifecycleScope.launch {
            var lastProgress = 0
            
            while (true) {
                val progress = installManager.getDownloadProgress(downloadId)
                
                if (progress != lastProgress) {
                    btn_install.text = "下载中 $progress%"
                    lastProgress = progress
                }
                
                if (progress >= 100) {
                    // 下载完成
                    val apkPath = installManager.getDownloadedFilePath(downloadId)
                    if (apkPath != null) {
                        btn_install.text = "安装中..."
                        val success = installManager.installApk(app, apkPath)
                        
                        if (success) {
                            btn_install.text = "安装"
                            btn_install.isEnabled = true
                        } else {
                            showError("安装失败")
                        }
                    }
                    break
                }
                
                kotlinx.coroutines.delay(500)
            }
        }
    }
    
    private fun showError(message: String) {
        // TODO: 显示错误提示
    }
    
    override fun onResume() {
        super.onResume()
        checkInstallStatus()
    }
}
