# MagCode-plus (Android)

本目录是从现有 `magcode_app` 项目按 `MagCode-main` 的组织方式整理出的可发布版本，便于上传到 GitHub。

- 平台路径: `platforms/android/`
- 仅保留源码与必要配置；构建产物与大型模型文件通过根 `.gitignore` 忽略
- UI 使用 Jetpack Compose（不使用 XML）

## 目录结构

```
MagCode-plus/
├── platforms/
│   └── android/
│       ├── app/
│       │   ├── src/main/
│       │   │   ├── java/…               # Kotlin 源码（Compose）
│       │   │   ├── res/…                # 资源
│       │   │   └── AndroidManifest.xml
│       │   ├── build.gradle.kts
│       │   └── proguard-rules.pro
│       ├── gradle/…                      # Gradle wrapper（可选）
│       ├── settings.gradle.kts
│       ├── build.gradle.kts
│       ├── gradle.properties
│       ├── gradlew / gradlew.bat
│       └── （构建产物与大模型通过根 .gitignore 忽略）
├── docs/
│   ├── README.md
│   └── QUICK_START.md
├── shared/
│   └── algorithms/（可放算法规范或伪代码）
├── .gitignore
└── README.md（本文档）
```

## 构建与运行

1) 使用 Android Studio 打开 `MagCode-plus/platforms/android`
2) 同步 Gradle 后，选择 `app` 运行
3) 或命令行构建：

```bat
cd platforms\android
gradlew.bat assembleDebug
```

APK 输出：`platforms/android/app/build/outputs/apk/debug/app-debug.apk`

## 注意
- 未提交构建产物与大型模型文件（详见根 `.gitignore`）
- 本地测试如需模型，请将模型放入 `app/src/main/assets/`，但请勿提交
- 全部界面基于 Jetpack Compose，无 XML 布局

## 许可

MIT 许可证，见仓库根 `LICENSE`。
