package com.tvappstore.app

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import com.tvappstore.app.data.DeviceManager
import com.tvappstore.app.data.api.ApiClient
import com.tvappstore.app.data.model.AppInfo
import com.tvappstore.app.ui.detail.AppDetailActivity
import com.tvappstore.app.ui.presenter.AppCardPresenter
import kotlinx.coroutines.launch

/**
 * TV 应用市场主界面
 */
class MainActivity : FragmentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment())
                .commitNow()
        }
        
        // 启动设备心跳
        DeviceManager.getInstance(this).startHeartbeat()
    }
    
    /**
     * 主 Fragment
     */
    class MainFragment : BrowseSupportFragment() {
        
        private val apiClient = ApiClient.getInstance()
        private val deviceManager by lazy { DeviceManager.getInstance(requireContext()) }
        
        private val apps = mutableListOf<AppInfo>()
        
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            
            setupUI()
            loadData()
        }
        
        private fun setupUI() {
            // 设置标题
            title = getString(R.string.app_name)
            
            // 设置图标
            badgeDrawable = requireContext().getDrawable(R.mipmap.ic_launcher)
            
            // 显示搜索按钮
            headersState = HEADERS_ENABLED
            isHeadersTransitionOnBackEnabled = true
            
            // 设置搜索颜色
            brandColor = requireContext().getColor(R.color.primary)
        }
        
        private fun loadData() {
            // 注册设备
            lifecycleScope.launch {
                deviceManager.registerDevice()
            }
            
            // 加载应用列表
            loadApps()
        }
        
        private fun loadApps() {
            lifecycleScope.launch {
                val result = apiClient.getApps()
                
                result.fold(
                    onSuccess = { appList ->
                        apps.clear()
                        apps.addAll(appList)
                        setupRows()
                    },
                    onFailure = { error ->
                        showError(error.message ?: "加载失败")
                    }
                )
            }
        }
        
        private fun setupRows() {
            val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
            
            // 按分类分组
            val categoryMap = apps.groupBy { it.category ?: "其他" }
            
            // 全部应用
            val allAppsRow = createAppRow("全部应用", apps)
            rowsAdapter.add(allAppsRow)
            
            // 各分类应用
            categoryMap.forEach { (category, appList) ->
                val row = createAppRow(category, appList)
                rowsAdapter.add(row)
            }
            
            adapter = rowsAdapter
            
            // 设置点击监听
            onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
                if (item is AppInfo) {
                    val intent = AppDetailActivity.newIntent(requireContext(), item)
                    startActivity(intent)
                }
            }
        }
        
        private fun createAppRow(category: String, appList: List<AppInfo>): ListRow {
            val listRowAdapter = ArrayObjectAdapter(AppCardPresenter())
            appList.forEach { app ->
                listRowAdapter.add(app)
            }
            
            val header = HeaderItem(category)
            return ListRow(header, listRowAdapter)
        }
        
        private fun showError(message: String) {
            // TODO: 显示错误提示
        }
    }
}
