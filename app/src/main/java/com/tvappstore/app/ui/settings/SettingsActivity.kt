package com.tvappstore.app.ui.settings

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import com.tvappstore.app.R
import com.tvappstore.app.TVAppStoreApp
import com.tvappstore.app.data.DeviceManager

/**
 * 设置页面
 */
class SettingsActivity : FragmentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            GuidedStepSupportFragment.addAsRoot(this, SettingsFragment(), android.R.id.content)
        }
    }
    
    class SettingsFragment : GuidedStepSupportFragment() {
        
        private val deviceManager by lazy { DeviceManager.getInstance(requireContext()) }
        
        override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
            return GuidanceStylist.Guidance(
                getString(R.string.settings_title),
                "查看设备信息和设置",
                "",
                null
            )
        }
        
        override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
            // 设备 ID
            actions.add(
                GuidedAction.Builder(requireContext())
                    .id(ACTION_DEVICE_ID)
                    .title(getString(R.string.device_id))
                    .description(deviceManager.getDeviceId())
                    .infoOnly(true)
                    .build()
            )
            
            // 设备名称
            actions.add(
                GuidedAction.Builder(requireContext())
                    .id(ACTION_DEVICE_NAME)
                    .title(getString(R.string.device_name))
                    .description(deviceManager.getDeviceName())
                    .editable(true)
                    .build()
            )
            
            // 设备型号
            actions.add(
                GuidedAction.Builder(requireContext())
                    .id(ACTION_DEVICE_MODEL)
                    .title("设备型号")
                    .description(deviceManager.getDeviceModel())
                    .infoOnly(true)
                    .build()
            )
            
            // 系统版本
            actions.add(
                GuidedAction.Builder(requireContext())
                    .id(ACTION_OS_VERSION)
                    .title("系统版本")
                    .description(deviceManager.getOsVersion())
                    .infoOnly(true)
                    .build()
            )
            
            // 服务器地址
            actions.add(
                GuidedAction.Builder(requireContext())
                    .id(ACTION_SERVER_URL)
                    .title(getString(R.string.server_url))
                    .description(TVAppStoreApp.BASE_URL)
                    .infoOnly(true)
                    .build()
            )
            
            // 关于
            actions.add(
                GuidedAction.Builder(requireContext())
                    .id(ACTION_ABOUT)
                    .title(getString(R.string.about))
                    .description("TV 应用市场 v1.0.0")
                    .infoOnly(true)
                    .build()
            )
        }
        
        override fun onGuidedActionEditedAndProceed(action: GuidedAction): Long {
            when (action.id) {
                ACTION_DEVICE_NAME -> {
                    deviceManager.setDeviceName(action.editDescription.toString())
                }
            }
            return super.onGuidedActionEditedAndProceed(action)
        }
        
        companion object {
            private const val ACTION_DEVICE_ID = 1L
            private const val ACTION_DEVICE_NAME = 2L
            private const val ACTION_DEVICE_MODEL = 3L
            private const val ACTION_OS_VERSION = 4L
            private const val ACTION_SERVER_URL = 5L
            private const val ACTION_ABOUT = 6L
        }
    }
}
