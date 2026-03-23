# TV 应用市场 Android TV 客户端

这是一个完整的 Android TV 应用市场客户端，配合 Web 后台管理系统使用。

## 项目结构

```
android-tv-appstore/
├── app/
│   ├── src/main/
│   │   ├── java/com/tvappstore/app/
│   │   │   ├── TVAppStoreApp.kt              # Application 类
│   │   │   ├── MainActivity.kt                # 主界面
│   │   │   ├── data/
│   │   │   │   ├── api/ApiClient.kt           # API 客户端
│   │   │   │   ├── DeviceManager.kt           # 设备管理
│   │   │   │   ├── AppInstallManager.kt       # 安装管理
│   │   │   │   └── model/                     # 数据模型
│   │   │   ├── ui/
│   │   │   │   ├── detail/                    # 应用详情页
│   │   │   │   ├── settings/                  # 设置页
│   │   │   │   └── presenter/                 # Leanback Presenter
│   │   │   └── receiver/                      # 广播接收器
│   │   ├── res/                               # 资源文件
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts                       # 模块构建配置
│   └── proguard-rules.pro                     # 混淆规则
├── build.gradle.kts                           # 项目构建配置
├── settings.gradle.kts                        # 项目设置
└── gradle.properties                          # Gradle 配置
```

## 功能特性

- ✅ TV 遥控器操作优化
- ✅ 应用列表展示（按分类分组）
- ✅ 应用详情页
- ✅ 应用下载和安装
- ✅ 应用更新检测
- ✅ 设备自动注册
- ✅ 心跳保活机制
- ✅ 设置页面

## 编译环境要求

- **Android Studio**: Hedgehog (2023.1.1) 或更高版本
- **JDK**: 17 或更高版本
- **Android SDK**: API 34 (Android 14)
- **Gradle**: 8.2 或更高版本

## 编译步骤

### 1. 下载项目

将 `android-tv-appstore` 文件夹下载到本地。

### 2. 配置服务器地址

修改 `app/src/main/java/com/tvappstore/app/TVAppStoreApp.kt` 文件：

```kotlin
companion object {
    // ...
    
    // 修改为您的服务器地址
    const val BASE_URL = "http://YOUR_SERVER_IP:5000"
}
```

### 3. 使用 Android Studio 打开项目

1. 打开 Android Studio
2. 选择 "Open an Existing Project"
3. 选择 `android-tv-appstore` 文件夹
4. 等待 Gradle 同步完成

### 4. 编译 APK

**调试版本：**
```bash
./gradlew assembleDebug
```
生成的 APK 位于：`app/build/outputs/apk/debug/app-debug.apk`

**正式版本：**
```bash
./gradlew assembleRelease
```
生成的 APK 位于：`app/build/outputs/apk/release/app-release.apk`

或者在 Android Studio 中：
- 点击菜单 `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`

## 安装到 TV 设备

### 方法 1：ADB 安装

1. 确保 TV 设备已开启 USB 调试
2. 连接 TV 设备到电脑
3. 执行命令：
```bash
adb install app-debug.apk
```

### 方法 2：通过网络安装

1. 确保 TV 和电脑在同一网络
2. TV 上开启网络调试：
   - 设置 → 关于 → 连续点击"版本号"7次，开启开发者模式
   - 设置 → 开发者选项 → 开启"USB调试"
   - 设置 → 开发者选项 → 开启"网络ADB调试"
3. 连接并安装：
```bash
adb connect TV_IP:5555
adb install app-debug.apk
```

### 方法 3：U盘安装

1. 将 APK 复制到 U 盘
2. U 盘插入 TV
3. 使用 TV 的文件管理器打开 APK 安装

## 使用说明

### 首次启动

1. 安装完成后，在 TV 应用列表中找到"TV 应用市场"
2. 首次启动会自动注册设备到服务器
3. 等待应用列表加载完成

### 操作方式

- **方向键**：导航选择
- **确认键**：打开应用详情
- **返回键**：返回上一页

### 安装应用

1. 在应用列表中选择要安装的应用
2. 点击"安装"按钮
3. 等待下载完成
4. 系统会自动弹出安装界面
5. 按提示完成安装

### 设置

在主界面左侧菜单中可以访问设置页面，查看：
- 设备 ID
- 设备名称（可编辑）
- 设备型号
- 系统版本
- 服务器地址

## 后台管理系统

后台管理系统已部署在服务器上，访问地址：

```
http://YOUR_SERVER_IP:5000
```

后台功能包括：
- **仪表盘**：查看统计数据
- **应用管理**：添加、编辑、删除应用
- **设备管理**：查看已连接设备
- **推送管理**：向设备推送应用安装任务

## API 接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/apps` | GET | 获取应用列表 |
| `/api/apps` | POST | 添加应用 |
| `/api/devices` | POST | 注册/更新设备 |
| `/api/push` | GET | 获取推送任务 |
| `/api/push` | POST | 创建推送任务 |

## 常见问题

### 1. 无法连接到服务器

检查以下内容：
- 服务器地址配置是否正确
- TV 设备和服务器网络是否连通
- 服务器防火墙是否开放端口

### 2. 应用安装失败

确保：
- TV 设备已开启"允许安装未知来源应用"
- APK 文件签名正确
- APK 文件完整无损

### 3. 应用列表为空

检查：
- 后台管理系统是否已添加应用
- 应用是否已上架（is_active = true）

## 技术栈

- **语言**: Kotlin
- **UI 框架**: Android Leanback
- **网络库**: OkHttp
- **图片加载**: Glide
- **JSON 解析**: Gson
- **异步处理**: Kotlin Coroutines

## 许可证

Copyright © 2024 TV App Store. All rights reserved.
